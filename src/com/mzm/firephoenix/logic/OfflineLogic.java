package com.mzm.firephoenix.logic;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.oppa.utils.cardutils.CardResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mzm.firephoenix.cache.GameCache;
import com.mzm.firephoenix.cache.PlayerInfo;
import com.mzm.firephoenix.constant.GameConstant;
import com.mzm.firephoenix.dao.JdbcDaoSupport;
import com.mzm.firephoenix.dao.entity.FivepkPlayerInfo;
import com.mzm.firephoenix.protobuf.CoreProtocol.Cmd;
import com.mzm.firephoenix.protobuf.CoreProtocol.MessageContent;
import com.mzm.firephoenix.protobuf.CoreProtocol.MessagePack;
import com.mzm.firephoenix.protobuf.CoreProtocol.SCMachineInfo;

@Component
public class OfflineLogic {
	public final static Log logger = LogFactory.getLog(OfflineLogic.class);
	@Autowired
	JdbcDaoSupport jdbcDaoSupport;

	public void sessionClosed(IoSession session) {
		long accountId = (long) session.getAttribute("accountId");
		PlayerInfo playerInfo = GameCache.getPlayerInfo(accountId);
		if (playerInfo == null){
			return;
		}
		GameCache.removeIoSession(playerInfo.getSeoId(), session);
		GameCache.removePlayerInfo(accountId);
		CardResult cr = (CardResult) session.getAttribute("cardResult");
		if (cr != null && cr.getWin() > 0) {
			FivepkPlayerInfo fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
			if (fivepkPlayerInfo == null) {
				return;
			}
			fivepkPlayerInfo.setScore(cr.getWin() + fivepkPlayerInfo.getScore() - cr.getBet() + cr.getGiftWin());
			jdbcDaoSupport.update(fivepkPlayerInfo);
		}
		String machineId = (String) session.getAttribute("machineId");
		if (machineId != null) {
			GameCache.updateMachineInfo(playerInfo.getSeoId(),machineId, GameConstant.MACHINE_TYPE_FREE, 0, null);
			List<IoSession> sessionList = GameCache.getSeoIdIoSessionList(playerInfo.getSeoId());
			for (IoSession ioSession : sessionList) {
				if (!ioSession.isClosing() && ioSession.isConnected() && (Long) ioSession.getAttribute("accountId") != accountId) {
					MessagePack.Builder returnMessagePack = MessagePack.newBuilder();
					returnMessagePack.setCmd(Cmd.CMD_MACHINE_INFO);
					returnMessagePack.setContent(MessageContent.newBuilder().setResult(0).setScMachineInfo(SCMachineInfo.newBuilder().setMachineId(machineId).setMachineType(GameConstant.MACHINE_TYPE_FREE)));
					logger.info("sent message pack : " + returnMessagePack.toString());
					ioSession.write(returnMessagePack);
				}
			}
		}
	}
}

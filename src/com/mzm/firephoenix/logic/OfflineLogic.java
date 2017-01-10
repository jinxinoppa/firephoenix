package com.mzm.firephoenix.logic;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mzm.firephoenix.cache.GameCache;
import com.mzm.firephoenix.cache.PlayerInfo;
import com.mzm.firephoenix.constant.ErrorCode;
import com.mzm.firephoenix.constant.GameConstant;
import com.mzm.firephoenix.dao.JdbcDaoSupport;
import com.mzm.firephoenix.dao.entity.FivepkAccount;
import com.mzm.firephoenix.dao.entity.FivepkDefault;
import com.mzm.firephoenix.dao.entity.FivepkPath;
import com.mzm.firephoenix.dao.entity.FivepkPlayerInfo;
import com.mzm.firephoenix.dao.entity.FivepkSeoId;
import com.mzm.firephoenix.dao.entity.MachineMatch;
import com.mzm.firephoenix.protobuf.CoreProtocol.Cmd;
import com.mzm.firephoenix.protobuf.CoreProtocol.MessageContent;
import com.mzm.firephoenix.protobuf.CoreProtocol.MessagePack;
import com.mzm.firephoenix.protobuf.CoreProtocol.SCMachineInfo;
import com.mzm.firephoenix.utils.CardResult;

@Component
public class OfflineLogic {
	public final static Log logger = LogFactory.getLog(OfflineLogic.class);
	@Autowired
	JdbcDaoSupport jdbcDaoSupport;

	public void sessionClosed(IoSession session) {
		long accountId = 0;
		CardResult cr = null;
		String machineId = null;
		if (session.containsAttribute("accountId")) {
			accountId = (long) session.removeAttribute("accountId");
		}
		if (session.containsAttribute("cardResult")) {
			cr = (CardResult) session.removeAttribute("cardResult");
		}
		if (session.containsAttribute("machineId")) {
			machineId = (String) session.removeAttribute("machineId");
		}
		if (!session.isClosing() && session.isConnected()) {
			MessagePack.Builder returnMessagePack = MessagePack.newBuilder();
			returnMessagePack.setCmd(Cmd.CMD_NET_WORK_ERROR);
			returnMessagePack.setContent(MessageContent.newBuilder().setResult(ErrorCode.ERROR_NET_WORK_ERROR.getErrorCode()));
			logger.info("sent message pack : " + returnMessagePack.toString());
			session.write(returnMessagePack);
		}
		PlayerInfo playerInfo = GameCache.getPlayerInfo(accountId);
		if (playerInfo == null) {
			return;
		}
		FivepkAccount fivepkAccount = jdbcDaoSupport.queryOne(FivepkAccount.class, new Object[]{accountId});
		fivepkAccount.setAccountInfo(0);
		jdbcDaoSupport.update(fivepkAccount);
		GameCache.removeIoSession(playerInfo.getSeoId(), session);
		GameCache.removePlayerInfo(accountId);
		if (cr != null && (cr.getWin() > 0 || cr.getBet() > 0)) {
			FivepkPlayerInfo fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
			if (fivepkPlayerInfo == null) {
				return;
			}
			fivepkPlayerInfo.setScore(cr.getWin() + fivepkPlayerInfo.getScore() - cr.getBet() + cr.getGiftWin() + cr.getWinSevenFive() * cr.getWinCount() * 4000);
			jdbcDaoSupport.update(fivepkPlayerInfo);
		}
		if (machineId != null) {
//			GameCache.updateMachineInfo(playerInfo.getSeoId(), machineId, GameConstant.MACHINE_TYPE_FREE, 0, null);
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
			FivepkSeoId fivepkSeoId=jdbcDaoSupport.queryOne(FivepkSeoId.class, new Object[]{machineId}, null, new String[]{"seoMachineId"});
			fivepkSeoId.setSeoMachineType(GameConstant.MACHINE_TYPE_FREE);
			fivepkSeoId.setAccountId(0);
			fivepkSeoId.setMachineAuto(0);
			jdbcDaoSupport.update(fivepkSeoId);
			
			String defsultStr = (String)session.getAttribute("defsult");
			if(defsultStr != null && !defsultStr.isEmpty()){
				String defsult[]= defsultStr.split("\\|");
				FivepkDefault fivepkDefault=new FivepkDefault(defsult[0],defsult[1],Integer.parseInt(defsult[2]),Integer.parseInt(defsult[3]),cr.getWin(),defsult[4],defsult[5],defsult[6],Integer.parseInt(defsult[7]),cr.getBetScore(),cr.getBetType());
				jdbcDaoSupport.save(fivepkDefault);
				session.removeAttribute("defsult");
				cr.setBetType("");
			}
			
			FivepkPath fivepkPath=(FivepkPath) session.getAttribute("fivepkPath");
			jdbcDaoSupport.save(fivepkPath);
			session.removeAttribute("fivepkPath");
			
			MachineMatch machineMatch=(MachineMatch)session.getAttribute("machineMatch");
			if(machineMatch!=null){
				if (cr.getBetType() != null && cr.getBetType()!="" && cr.getWin() > 0) {
					machineMatch.setWinPoint(machineMatch.getWinPoint() + cr.getWin());
				}
				if (!cr.getHalfWin().isEmpty()) {
					int totalHalfWin = 0;
					for (int i = 0; i < cr.getHalfWin().size(); i++) {
						totalHalfWin += cr.getHalfWin().get(i);
					}
					machineMatch.setWinPoint(machineMatch.getWinPoint() + totalHalfWin);
				}
				if (cr.getWin() >= 75000) {
					machineMatch.setWinPoint(machineMatch.getWinPoint()+cr.getPassScore());
				}
				jdbcDaoSupport.update(machineMatch);
				session.removeAttribute("machineMatch");
			}
		}
	}
}

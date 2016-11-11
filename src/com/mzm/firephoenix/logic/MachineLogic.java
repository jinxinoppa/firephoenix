package com.mzm.firephoenix.logic;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mzm.firephoenix.cache.GameCache;
import com.mzm.firephoenix.cache.MachineInfo;
import com.mzm.firephoenix.cache.PlayerInfo;
import com.mzm.firephoenix.constant.GameConstant;
import com.mzm.firephoenix.dao.JdbcDaoSupport;
import com.mzm.firephoenix.dao.entity.FivepkSeoId;
import com.mzm.firephoenix.protobuf.CoreProtocol.Cmd;
import com.mzm.firephoenix.protobuf.CoreProtocol.ErrorCode;
import com.mzm.firephoenix.protobuf.CoreProtocol.MessageContent;
import com.mzm.firephoenix.protobuf.CoreProtocol.MessageContent.Builder;
import com.mzm.firephoenix.protobuf.CoreProtocol.MessagePack;
import com.mzm.firephoenix.protobuf.CoreProtocol.SCMachineInfo;

/**
 * 
 * @author oppa
 *
 */
@Component
public class MachineLogic {
	public final static Log logger = LogFactory.getLog(MachineLogic.class);
	@Autowired
	JdbcDaoSupport jdbcDaoSupport;
	public Builder ccEnterMachine(IoSession session, MessageContent content) {
		Long accountId = (Long) session.getAttribute("accountId");
		if (accountId == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT_VALUE);
		}
		int machineId = content.getCcEnterMachine().getMachineId();
		PlayerInfo playerInfo = GameCache.getPlayerInfo(accountId);
		FivepkSeoId fivepkSeoId = jdbcDaoSupport.queryOne(FivepkSeoId.class, new Object[]{playerInfo.getSeoId(), machineId}, null, new String[]{"seoid", "seoMachineId"});
		if (fivepkSeoId == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_SMS_INVALID_PARAMETER_VALUE);
		}
		MachineInfo machineInfo = GameCache.getMachineInfo(fivepkSeoId.getSeoId(), fivepkSeoId.getSeoMachineId());
		if (machineInfo.getMachineType() != GameConstant.MACHINE_TYPE_FREE) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_SMS_INVALID_PARAMETER_VALUE);
		}
		machineInfo = GameCache.updateMachineInfo(fivepkSeoId.getSeoId(), fivepkSeoId.getSeoMachineId(), GameConstant.MACHINE_TYPE_ONLINE, accountId);
		List<IoSession> sessionList = GameCache.getSeoIdIoSessionList(fivepkSeoId.getSeoId());
		for (IoSession ioSession : sessionList) {
			if (!ioSession.isClosing() && ioSession.isConnected()) {
				MessagePack.Builder returnMessagePack = MessagePack.newBuilder();
				returnMessagePack.setCmd(Cmd.CMD_MACHINE_INFO);
				returnMessagePack.setContent(MessageContent.newBuilder().setScMachineInfo(SCMachineInfo.newBuilder().setMachineId(fivepkSeoId.getSeoMachineId()).setMachineType(GameConstant.MACHINE_TYPE_ONLINE).setPic(playerInfo.getPic()).setNickName(playerInfo.getNickName())));
				logger.info("sent message pack : " + returnMessagePack.toString());
				session.write(returnMessagePack);
			}
		}
		return MessageContent.newBuilder().setResult(0);
	}

	public Builder ccLeaveMachine(IoSession session, MessageContent content) {
		return MessageContent.newBuilder().setResult(0);
	}

	public Builder ccMachineStay(IoSession session, MessageContent content) {
		return MessageContent.newBuilder().setResult(0);
	}
}

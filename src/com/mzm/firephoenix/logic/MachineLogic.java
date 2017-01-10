package com.mzm.firephoenix.logic;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mzm.firephoenix.cache.GameCache;
import com.mzm.firephoenix.cache.MachineInfo;
import com.mzm.firephoenix.cache.PlayerInfo;
import com.mzm.firephoenix.constant.ErrorCode;
import com.mzm.firephoenix.constant.GameConstant;
import com.mzm.firephoenix.dao.JdbcDaoSupport;
import com.mzm.firephoenix.dao.entity.AccessPoints;
import com.mzm.firephoenix.dao.entity.FivepkPath;
import com.mzm.firephoenix.dao.entity.FivepkPlayerInfo;
import com.mzm.firephoenix.dao.entity.FivepkSeoId;
import com.mzm.firephoenix.protobuf.CoreProtocol.Cmd;
import com.mzm.firephoenix.protobuf.CoreProtocol.MessageContent;
import com.mzm.firephoenix.protobuf.CoreProtocol.MessageContent.Builder;
import com.mzm.firephoenix.protobuf.CoreProtocol.MessagePack;
import com.mzm.firephoenix.protobuf.CoreProtocol.SCMachineInfo;
import com.mzm.firephoenix.protobuf.CoreProtocol.SCMachineList;
import com.mzm.firephoenix.utils.CardResult;

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
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT.getErrorCode());
		}
		String machineId = content.getCcEnterMachine().getMachineId();
		PlayerInfo playerInfo = GameCache.getPlayerInfo(accountId);
		FivepkSeoId fivepkSeoId = jdbcDaoSupport.queryOne(FivepkSeoId.class, new Object[]{playerInfo.getSeoId(), machineId}, null, new String[]{"seoId", "seoMachineId"});
		if (fivepkSeoId == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_NOT_MACHINE.getErrorCode());
		}
//		MachineInfo machineInfo = GameCache.getMachineInfo(fivepkSeoId.getSeoId(), fivepkSeoId.getSeoMachineId());
		if (fivepkSeoId.getSeoMachineType() == GameConstant.MACHINE_TYPE_ONLINE) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_MACHINE_TYPE_ONLINE.getErrorCode());
		} 
		FivepkPlayerInfo fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
		if (fivepkPlayerInfo == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT.getErrorCode());
		}
//		else if (fivepkSeoId.getSeoMachineType() == GameConstant.MACHINE_TYPE_STAY && fivepkSeoId.getSeoMachineStayTime() != null && fivepkSeoId.getSeoMachineStayTime().after(new Date())) {
//			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_MACHINE_STAY.getErrorCode());
//		}
//		machineInfo = GameCache.updateMachineInfo(fivepkSeoId.getSeoId(), fivepkSeoId.getSeoMachineId(), GameConstant.MACHINE_TYPE_ONLINE, accountId);
		List<IoSession> sessionList = GameCache.getSeoIdIoSessionList(fivepkSeoId.getSeoId());
		for (IoSession ioSession : sessionList) {
			if (!ioSession.isClosing() && ioSession.isConnected() && (Long) ioSession.getAttribute("accountId") != accountId) {
				MessagePack.Builder returnMessagePack = MessagePack.newBuilder();
				returnMessagePack.setCmd(Cmd.CMD_MACHINE_INFO);
				returnMessagePack.setContent(MessageContent.newBuilder().setResult(0).setScMachineInfo(SCMachineInfo.newBuilder().setMachineId(fivepkSeoId.getSeoMachineId()).setMachineType(GameConstant.MACHINE_TYPE_ONLINE).setPic(playerInfo.getPic()).setNickName(playerInfo.getNickName())));
				logger.info("sent message pack : " + returnMessagePack.toString());
				ioSession.write(returnMessagePack);
			}
		}
		fivepkSeoId.setSeoMachineType(GameConstant.MACHINE_TYPE_ONLINE);
		fivepkSeoId.setAccountId(accountId);
		jdbcDaoSupport.update(fivepkSeoId);
		session.setAttribute("machineId", machineId);
		FivepkPath fivepkPath=new FivepkPath();
		fivepkPath.setMachineId(machineId);
		fivepkPath.setName(playerInfo.getNickName());
		fivepkPath.setBeginPoint(fivepkPlayerInfo.getScore());
		fivepkPath.setLoginIp((String)session.getAttribute("ipAddress"));
		fivepkPath.setBeginTime(new java.sql.Timestamp(System.currentTimeMillis()));
		session.setAttribute("fivepkPath", fivepkPath);
		return MessageContent.newBuilder().setResult(0);
	}

	public Builder ccLeaveMachine(IoSession session, MessageContent content) {
		String machineId = content.getCcLeaveMachine().getMachineId();
		Long accountId = (Long) session.getAttribute("accountId");
		if (accountId == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT.getErrorCode());
		}
		PlayerInfo playerInfo = GameCache.getPlayerInfo(accountId);
		FivepkSeoId fivepkSeoId = jdbcDaoSupport.queryOne(FivepkSeoId.class, new Object[]{playerInfo.getSeoId(), machineId}, null, new String[]{"seoId", "seoMachineId"});
		if (fivepkSeoId == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_NOT_MACHINE.getErrorCode());
		}
		FivepkPlayerInfo fivepkPlayerInfo = jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new Object[]{accountId});
		if (fivepkPlayerInfo == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_PLAYER_NOT_EXIT.getErrorCode());
		}
		CardResult cr = (CardResult) session.getAttribute("cardResult");
		if (cr != null){
			fivepkPlayerInfo.setScore(fivepkPlayerInfo.getScore() - cr.getBet());
			jdbcDaoSupport.update(fivepkPlayerInfo);
			cr.setBet(0);
		}
		
//		GameCache.updateMachineInfo(playerInfo.getSeoId(), machineId, GameConstant.MACHINE_TYPE_FREE, 0, null);
		fivepkSeoId.setSeoMachineType(GameConstant.MACHINE_TYPE_FREE);
		fivepkSeoId.setAccountId(0);
		jdbcDaoSupport.update(fivepkSeoId);
		List<IoSession> sessionList = GameCache.getSeoIdIoSessionList(fivepkSeoId.getSeoId());
		for (IoSession ioSession : sessionList) {
			if (!ioSession.isClosing() && ioSession.isConnected() && (Long) ioSession.getAttribute("accountId") != accountId) {
				MessagePack.Builder returnMessagePack = MessagePack.newBuilder();
				returnMessagePack.setCmd(Cmd.CMD_MACHINE_INFO);
				returnMessagePack.setContent(MessageContent.newBuilder().setResult(0).setScMachineInfo(SCMachineInfo.newBuilder().setMachineId(fivepkSeoId.getSeoMachineId()).setMachineType(GameConstant.MACHINE_TYPE_FREE)));
				logger.info("sent message pack : " + returnMessagePack.toString());
				ioSession.write(returnMessagePack);
			}
		}
		session.removeAttribute("machineId");
		FivepkPath fivepkPath=(FivepkPath) session.getAttribute("fivepkPath");
		jdbcDaoSupport.save(fivepkPath);
		session.removeAttribute("fivepkPath");
		return MessageContent.newBuilder().setResult(0);
	}

	public Builder ccMachineStay(IoSession session, MessageContent content) {
		Long accountId = (Long) session.getAttribute("accountId");
		if (accountId == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT.getErrorCode());
		}
		String machineId = content.getCcMachineStay().getMachineId();
		int machineType = content.getCcMachineStay().getMachineType();
		if (machineType != 1 && machineType != 0){
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_MACHINE_STAY.getErrorCode());
		}
		PlayerInfo playerInfo = GameCache.getPlayerInfo(accountId);
		FivepkSeoId fivepkSeoId = jdbcDaoSupport.queryOne(FivepkSeoId.class, new Object[]{playerInfo.getSeoId(), machineId}, null, new String[]{"seoId", "seoMachineId"});
		if (fivepkSeoId == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_NOT_MACHINE.getErrorCode());
		}
		if (machineType == 1) {
//			GameCache.updateMachineInfo(playerInfo.getSeoId(), machineId, GameConstant.MACHINE_TYPE_STAY, accountId, new Date(System.currentTimeMillis() + 1000 * 3600));
			fivepkSeoId.setSeoMachineType(GameConstant.MACHINE_TYPE_STAY);
			fivepkSeoId.setAccountId(accountId);
		} else if (machineType == 0) {
//			GameCache.updateMachineInfo(playerInfo.getSeoId(), machineId, GameConstant.MACHINE_TYPE_ONLINE, accountId, null);
			fivepkSeoId.setSeoMachineType(GameConstant.MACHINE_TYPE_ONLINE);
			fivepkSeoId.setAccountId(accountId);
		}
		jdbcDaoSupport.update(fivepkSeoId);
		return MessageContent.newBuilder().setResult(0);
	}

	public Builder csMachineList(IoSession session, MessageContent content) {
		Long accountId = (Long) session.getAttribute("accountId");
		if (accountId == null) {
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT.getErrorCode());
		}
		PlayerInfo playerInfo = GameCache.getPlayerInfo(accountId);
		if (playerInfo == null){
			return MessageContent.newBuilder().setResult(ErrorCode.ERROR_ACCOUNT_RECONNECT.getErrorCode());
		}
		List<FivepkSeoId> fivepkSeoIdList = jdbcDaoSupport.query(FivepkSeoId.class, new Object[]{playerInfo.getSeoId()}, null, new String[]{"seoId"});
		MessagePack.Builder returnMessagePack = MessagePack.newBuilder();
		returnMessagePack.setCmd(Cmd.CMD_MACHINE_LIST);
		SCMachineList.Builder listBuilder = SCMachineList.newBuilder();
		SCMachineInfo.Builder infoBuilder = null;
		long machineInfoAccountId = 0;
		for (FivepkSeoId fivepkSeoId : fivepkSeoIdList) {
			machineInfoAccountId = fivepkSeoId.getAccountId();
			PlayerInfo playerInfo1 = GameCache.getPlayerInfo(machineInfoAccountId);
			if (machineInfoAccountId != 0 && playerInfo1 == null){
				machineInfoAccountId = 0;
				fivepkSeoId.setSeoMachineType(GameConstant.MACHINE_TYPE_FREE);
				fivepkSeoId.setAccountId(0);
				jdbcDaoSupport.update(fivepkSeoId);
				
				List<IoSession> sessionList = GameCache.getSeoIdIoSessionList(fivepkSeoId.getSeoId());
				for (IoSession ioSession : sessionList) {
					if (!ioSession.isClosing() && ioSession.isConnected() && (Long) ioSession.getAttribute("accountId") != accountId) {
						MessagePack.Builder returnMessagePack1 = MessagePack.newBuilder();
						returnMessagePack1.setCmd(Cmd.CMD_MACHINE_INFO);
						returnMessagePack1.setContent(MessageContent.newBuilder().setResult(0).setScMachineInfo(SCMachineInfo.newBuilder().setMachineId(fivepkSeoId.getSeoMachineId()).setMachineType(GameConstant.MACHINE_TYPE_FREE)));
						logger.info("sent message pack : " + returnMessagePack1.toString());
						ioSession.write(returnMessagePack1);
					}
				}
			}
			String seoMachineId = fivepkSeoId.getSeoMachineId();
//			MachineInfo machineInfo = GameCache.getMachineInfo(fivepkSeoId.getSeoId(), seoMachineId);
			infoBuilder = SCMachineInfo.newBuilder();
			infoBuilder.setMachineId(seoMachineId).setMachineType(fivepkSeoId.getSeoMachineType());
			
			if (machineInfoAccountId != 0 && playerInfo != null) {
				infoBuilder.setPic(playerInfo.getPic()).setNickName(playerInfo.getNickName());
			}
			listBuilder.addScMachineInfo(infoBuilder);
		}
		return MessageContent.newBuilder().setResult(0).setScMachineList(listBuilder);
	}
}

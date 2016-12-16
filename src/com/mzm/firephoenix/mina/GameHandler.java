package com.mzm.firephoenix.mina;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mzm.firephoenix.logic.OfflineLogic;
import com.mzm.firephoenix.protobuf.CoreProtocol.Cmd;
import com.mzm.firephoenix.protobuf.CoreProtocol.MessageContent;
import com.mzm.firephoenix.protobuf.CoreProtocol.MessageContent.Builder;
import com.mzm.firephoenix.protobuf.CoreProtocol.MessagePack;
import com.mzm.firephoenix.utils.SocketUtil;

/**
 * 
 * @author oppa
 * 
 */
public class GameHandler extends IoHandlerAdapter implements ApplicationContextAware {
	private ApplicationContext applicationContext;

	public final static Log logger = LogFactory.getLog(GameHandler.class);

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		byte[] msg = (byte[]) message;
		if (msg.length <= 0) {
			// FIXME return errorcode
			return;
		}
		MessagePack messagePack = null;
		try {
			messagePack = MessagePack.parseFrom(msg);
		} catch (InvalidProtocolBufferException e) {
			return;
		} catch (Exception e) {
			return;
		}
		logger.info("receive message pack : " + messagePack.toString());
		// System.out.println("number : " + messagePack.getCmd().getNumber());
		// System.out.println("name : " + messagePack.getCmd().name());
		// System.out.println("toString : " + messagePack.getCmd().toString());
		// System.out.println("getVersion : " + messagePack.getVersion());
		// System.out.println("getSessionId : " + messagePack.getSessionId());
		// System.out.println("toString : " +
		// messagePack.getContent().toString());
		int cmdNumber = messagePack.getCmd().getNumber();
		if (cmdNumber == 65541) {
			logger.info("session : [" + session.getId() + " ] ip address : [" + session.getAttribute("ipAddress") + "] heart beat");
			return;
		}
		String logicName = SocketUtil.getLogicName(cmdNumber);
		if (logicName == null) {
			// FIXME return errorcode
			return;
		}
		String methodName = null;
		for (FieldDescriptor string : messagePack.getContent().getAllFields().keySet()) {
			if (string.getName().startsWith("cs") || string.getName().startsWith("cc")) {
				methodName = string.getName();
			}
		}
		if (methodName == null) {
			methodName = SocketUtil.getLogicMethodName(cmdNumber);
		}
		if (methodName == null) {
			logger.error("methodName doesn't exist. cmdNumber: " + cmdNumber);
			return;
		}
		Object executor = applicationContext.getBean(logicName + "Logic");

		if (executor == null) {
			logger.error("logic doesn't exist. logicName: " + logicName);
			return;
		}

		Method m = executor.getClass().getMethod(methodName, IoSession.class, MessageContent.class);
		if (m == null) {
			logger.error("method is null. methodName: " + methodName + ". logicName: " + logicName);
			return;
		}

		long start = System.currentTimeMillis();
		Builder returnBuilder = null;
		try {
			returnBuilder = (Builder) m.invoke(executor, session, messagePack.getContent());
		} catch (Exception e) {
			logger.error(e, e);
		}
		long diff = System.currentTimeMillis() - start;
		if (diff > 1000) {
			logger.info("spending too much time on logicName : [" + logicName + "] methodName : [" + methodName + "]");
		}
		if (cmdNumber != Cmd.CMD_FOUR_KIND_TIME_VALUE) {
			MessagePack.Builder returnMessagePack = MessagePack.newBuilder();
			returnMessagePack.setCmd(Cmd.valueOf(cmdNumber));
			returnMessagePack.setContent(returnBuilder);
			logger.info("sent message pack : " + returnBuilder.toString());
			session.write(returnMessagePack);
		}
	}
	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		logger.info("sessionSent. session id=" + session.getId());
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		logger.info("sessionClosed. session id=" + session.getId());
		OfflineLogic executor = (OfflineLogic) applicationContext.getBean("offlineLogic");
		executor.sessionClosed(session);
		session.closeOnFlush();
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		String address = ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress();
		session.setAttribute("ipAddress", address);
		logger.info("sessionCreated, client ip address: " + address);
	}
	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		logger.info("sessionIdle. session id=" + session.getId());
		OfflineLogic executor = (OfflineLogic) applicationContext.getBean("offlineLogic");
		executor.sessionClosed(session);
		session.closeOnFlush();
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		logger.info("sessionOpened. session id=" + session.getId());
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		try {
			String message = cause.getMessage();
			String className = cause.getClass().getName();
			// if (!"远程主机强迫关闭了一个现有的连接。".equals(message) &&
			// !"Connection reset by peer".equals(message) &&
			// !"Broken pipe".equals(message) &&
			// !"java.lang.String cannot be cast to org.json.JSONObject".equals(message)
			// &&
			// !className.equals("org.apache.mina.core.write.WriteToClosedSessionException"))
			// {
			// long accountId = (long) session.removeAttribute("accountId");
			// CardResult cr = (CardResult) session.getAttribute("cardResult");
			// String machineId = (String) session.getAttribute("machineId");
			// PlayerInfo playerInfo = GameCache.getPlayerInfo(accountId);
			// if (playerInfo == null){
			// return;
			// }
			// GameCache.removeIoSession(playerInfo.getSeoId(), session);
			// GameCache.removePlayerInfo(accountId);
			//
			// if (cr != null && cr.getWin() > 0) {
			// JdbcDaoSupport jdbcDaoSupport =
			// applicationContext.getBean(JdbcDaoSupport.class);
			// if (jdbcDaoSupport != null){
			// FivepkPlayerInfo fivepkPlayerInfo =
			// jdbcDaoSupport.queryOne(FivepkPlayerInfo.class, new
			// Object[]{accountId});
			// if (fivepkPlayerInfo == null) {
			// return;
			// }
			// fivepkPlayerInfo.setScore(cr.getWin() +
			// fivepkPlayerInfo.getScore() - cr.getBet() + cr.getGiftWin());
			// jdbcDaoSupport.update(fivepkPlayerInfo);
			// }
			// }
			// if (machineId != null) {
			// GameCache.updateMachineInfo(playerInfo.getSeoId(),machineId,
			// GameConstant.MACHINE_TYPE_FREE, 0, null);
			// List<IoSession> sessionList =
			// GameCache.getSeoIdIoSessionList(playerInfo.getSeoId());
			// for (IoSession ioSession : sessionList) {
			// if (!ioSession.isClosing() && ioSession.isConnected() && (Long)
			// ioSession.getAttribute("accountId") != accountId) {
			// MessagePack.Builder returnMessagePack = MessagePack.newBuilder();
			// returnMessagePack.setCmd(Cmd.CMD_MACHINE_INFO);
			// returnMessagePack.setContent(MessageContent.newBuilder().setResult(0).setScMachineInfo(SCMachineInfo.newBuilder().setMachineId(machineId).setMachineType(GameConstant.MACHINE_TYPE_FREE)));
			// logger.info("sent message pack : " +
			// returnMessagePack.toString());
			// ioSession.write(returnMessagePack);
			// }
			// }
			// }
			// }
		} catch (Exception e) {
			logger.error(cause, cause);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}

package com.mzm.firephoenix.mina;

import java.lang.reflect.Method;

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
 * 接受socket请求，转发至executor。端口 19360
 * 
 * @author oppa
 * 
 */
public class GameHandler extends IoHandlerAdapter implements ApplicationContextAware {
	private ApplicationContext applicationContext;

	public final static Log logger = LogFactory.getLog(GameHandler.class);

	public static ByteMessage staticBm = new ByteMessage();

	public void sendError(IoSession session) {
		ByteMessage bm = new ByteMessage((short) 0);
		bm.writeString("error");
		session.write(bm);
	}

	public void sendUpdate(IoSession session) {
		ByteMessage bm = new ByteMessage((short) 1);
		bm.writeString("");
		session.write(bm);
	}

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
			logger.error(e.getMessage(), e);
		}
		long diff = System.currentTimeMillis() - start;
		if (diff > 1000) {
			logger.info("spending too much time on logicName : [" + logicName + "] methodName : [" + methodName + "]");
		}
		MessagePack.Builder returnMessagePack = MessagePack.newBuilder();
		returnMessagePack.setCmd(Cmd.valueOf(cmdNumber));
		returnMessagePack.setContent(returnBuilder);
		logger.info("sent message pack : " + returnBuilder.toString());
		session.write(returnMessagePack);
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
		session.close(true);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		logger.info("sessionIdle. session id=" + session.getId());
		// ByteMessage bm = new ByteMessage(ActConstant.GAME_ALERT);
		// bm.writeString(GameAlert.GAME_8);
		// session.write(bm);
		// session.close(true);
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		logger.info("sessionOpened. session id=" + session.getId());
		// 5分钟没有通讯则断开连接
		// Thread.sleep(5000);
		// session.getConfig().setIdleTime(IdleStatus.READER_IDLE, 900000);
		// MsgOuterClass.Msg.Builder msgBuilder =
		// MsgOuterClass.Msg.newBuilder();
		// msgBuilder.setA(true);
		// msgBuilder.setB(Integer.MAX_VALUE);
		// msgBuilder.setC(Long.MAX_VALUE);
		// msgBuilder.setD(Float.MIN_NORMAL);
		// msgBuilder.setE(Double.MAX_VALUE);
		// msgBuilder.setF("abc");
		// byte[] msgBody = msgBuilder.build().toByteArray();
		// short length = (short) (msgBody.length + 2);
		// IoBuffer buffer = IoBuffer.allocate(length);
		// buffer.putShort(length);
		// buffer.put(msgBody);
		// System.out.println(buffer.array().length);
		// session.write(buffer.array());
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		try {
			// CommonCache.removeCurTime(String.valueOf(session.getAttribute(
			// SessionConstantPK.ROLE_ID, "0")));
			// String message = cause.getMessage();
			// String className = cause.getClass().getName();
			// if (!"远程主机强迫关闭了一个现有的连接。".equals(message)
			// && !"Connection reset by peer".equals(message)
			// && !"Broken pipe".equals(message)
			// && !"java.lang.String cannot be cast to org.json.JSONObject"
			// .equals(message)
			// && !className
			// .equals("org.apache.mina.core.write.WriteToClosedSessionException"))
			// {
			//
			// String roleId =
			// String.valueOf(session.getAttribute(SessionConstantPK.ROLE_ID,
			// "0"));
			// Request request = SessionCache.removeRoleRequest(roleId);
			// logger.error(request + " message:" + message
			// + " uncaught exception:", cause);
			// ByteMessage bm = new ByteMessage(ActConstant.GAME_ALERT);
			// bm.writeString("");
			// bm.writeString("");
			// session.write(bm);
			//
			// }
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}

package com.mzm.poker.mina.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.mzm.poker.protobuf.test.MsgOuterClass;
import com.mzm.poker.protobuf.test.MsgOuterClass.Msg;
import com.mzm.poker.utils.SocketUtil;

public class ClientHandler extends IoHandlerAdapter{

	public final static Log logger = LogFactory.getLog(ClientHandler.class);
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		logger.debug("exceptCaught : " + cause.getMessage());
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		logger.debug("messageReceived:" + message);
		byte[] msg = (byte[])message;
//		byte[] shortArray = new byte[2];
//		System.arraycopy(msg, 0, shortArray, 0, 2);
//		IoBuffer lengthBuffer = IoBuffer.EMPTY_BUFFER;
//		lengthBuffer.setAutoExpand(true);
//		lengthBuffer.put(shortArray);
//		short length = lengthBuffer.getShort();//SocketUtil.bytes2short(shortArray);
//		
//		
//		IoBuffer buffer = IoBuffer.allocate(length);
//		buffer.put(msg, 0, length);
//		short length2 = buffer.getShort();
//		
//		System.out.println("length : " + length);
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		logger.debug("messageSent:");
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		logger.debug("messageClosed:");
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		logger.debug("messageCreated:");
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		logger.debug("messageReceived:");
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		logger.debug("messageOpened:");
	}

}

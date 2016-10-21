package com.mzm.poker.mina.test;

import java.net.InetSocketAddress;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.mzm.poker.protobuf.CoreProtocol;
import com.mzm.poker.protobuf.CoreProtocol.Cmd;

public class Client {
//	private static final String HOSTNAME = "localhost";
	private static final String HOSTNAME = "115.159.193.195";
	private static final int PORT = 16111;
	private static final long CONNECT_TIMEOUT = 3000 * 1000L; // 30 seconds
	private static final boolean USE_CUSTOM_CODEC = true;

	public static void main(String[] args) throws Throwable {

		NioSocketConnector connector = new NioSocketConnector();

		// Configure the service.
		connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);
		if (USE_CUSTOM_CODEC) {
			ClientByteCodecFactory factory = new ClientByteCodecFactory();
			factory.setDecoder(new ClientByteDecoder());
			factory.setEncoder(new ClientByteEncoder());

			connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(factory));
		} else {
			connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
		}
		connector.getFilterChain().addLast("logger", new LoggingFilter());

		connector.setHandler(new ClientHandler());

		IoSession session;
		for (;;) {
			try {
				ConnectFuture future = connector.connect(new InetSocketAddress(HOSTNAME, PORT));
				future.awaitUninterruptibly();
				session = future.getSession();
				System.out.println("session id : " + session.getId());
				CoreProtocol.MessagePack.Builder messagePackBuilder = CoreProtocol.MessagePack.newBuilder();
				messagePackBuilder.setCmd(Cmd.CMD_REGISTER);
				CoreProtocol.MessageContent.Builder messageContentBuilder = CoreProtocol.MessageContent.newBuilder();
				messageContentBuilder.setResult(0);
				CoreProtocol.CSRegsiter.Builder csRegsiterBuilder = CoreProtocol.CSRegsiter.newBuilder();
				csRegsiterBuilder.setAccount("account2");
				csRegsiterBuilder.setPassword("password2");
				csRegsiterBuilder.setSeoid("seoid");
				messageContentBuilder.setCsRegister(csRegsiterBuilder);
				messagePackBuilder.setContent(messageContentBuilder);
//				MessageContent.Builder messageContentBuilder = MessageContent.newBuilder();
//				messageContentBuilder.setResult(0);
//				messageContentBuilder.setLcRetServer(LCRetServer)
//				messagePackBuilder.setContent();
//				byte[] msgBody = messagePackBuilder.build().toByteArray();
//				IoBuffer buffer = IoBuffer.allocate(msgBody.length + 2);
//				buffer.putShort((short)(msgBody.length + 2));
//				buffer.put(msgBody);
				
				session.write(messagePackBuilder);
//				MsgOuterClass.Msg.Builder msgBuilder = MsgOuterClass.Msg.newBuilder();
//				msgBuilder.setA(true);
//				msgBuilder.setB(Integer.MAX_VALUE);
//				msgBuilder.setC(Long.MAX_VALUE);
//				msgBuilder.setD(Float.MIN_NORMAL);
//				msgBuilder.setE(Double.MAX_VALUE);
//				msgBuilder.setF("abc");
//				byte[] msgBody = msgBuilder.build().toByteArray();
//				IoBuffer buffer = IoBuffer.allocate(msgBody.length + 2);
//				buffer.putShort((short)(msgBody.length + 2));
//				buffer.put(msgBody);
//				session.write(msgBuilder);
				break;
			} catch (RuntimeIoException e) {
				System.err.println("Failed to connect.");
				e.printStackTrace();
				Thread.sleep(5000);
			}
		}

		// wait until the summation is done
//		session.getCloseFuture().awaitUninterruptibly();
//
//		connector.dispose();
	}
}

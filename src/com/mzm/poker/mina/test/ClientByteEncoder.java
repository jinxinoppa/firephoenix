package com.mzm.poker.mina.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.mzm.poker.protobuf.CoreProtocol;

/**
 * 加密数据返回给客户端。先压缩，再AES加密。
 * 
 * @author oppa
 * 
 */
public class ClientByteEncoder implements ProtocolEncoder {
	public final static Log logger = LogFactory.getLog(ClientByteEncoder.class);
	private String HEAD = "cswf";

	private String IOS_HEAD = "iosp";
	public static long sendBytes=0;

	@Override
	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		IoBuffer buffer;
		// String s;
		// if (message instanceof String) {
		// s = (String) message;
		//
		// } else {
		// Map<String, Object> m = (Map<String, Object>) message;
		// JSONObject jo = new JSONObject(m);
		// s = jo.toString();
		// }

		// byte[] bytes =
		// Compresser.compressBytes(s.getBytes(Constant.CHARSET));

		byte[] bytes;
//		if (message instanceof ByteMessage) {
//			ByteMessage bm = (ByteMessage) message;
//			bytes = bm.getData();
//		} else {
//			bytes = ArrayUtils.EMPTY_BYTE_ARRAY;
//		}
//		sendBytes+=bytes.length;
//		logger.info("############send all bytes:" + sendBytes);

//		bytes = Compresser.compressBytes(bytes);
//		if (session.getAttribute("ios") == null) {
			//bytes = AES.encryptAES(bytes);
//			buffer = IoBuffer.allocate(bytes.length + 4);
//			buffer.put(HEAD.getBytes());
//		} else {
//			bytes = AES.encryptAES4IOS(bytes);
//			buffer = IoBuffer.allocate(bytes.length + 4);
//			buffer.put(IOS_HEAD.getBytes());
//		}
//
//		buffer.put(SocketUtil.int2bytes(bytes.length+4));
//		buffer.put(bytes);
//		buffer.flip();
//		buffer = IoBuffer.allocate(((byte[])message).length);
//		buffer.put((byte[])message);
//		buffer.flip();
//		out.write(buffer.array());
//		buffer.free();
//		if (message instanceof MsgOuterClass.Msg.Builder){
//			byte[] msgBody = ((MsgOuterClass.Msg.Builder) message).build().toByteArray();
//			buffer = IoBuffer.allocate(((byte[])msgBody).length);
//			buffer.put((byte[])msgBody);
//			buffer.flip();
//			out.write(buffer);
//			buffer.free();
//		}
		byte[] msgBody = ((CoreProtocol.MessagePack.Builder)message).build().toByteArray();
		int length = msgBody.length;
		buffer = IoBuffer.allocate(length + 1);
		buffer.put((byte)length);
		buffer.put(msgBody);
		buffer.flip();
		out.write(buffer);
		buffer.free();
	}

	@Override
	public void dispose(IoSession session) throws Exception {

	}

}

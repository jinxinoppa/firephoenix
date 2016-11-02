package com.mzm.firephoenix.mina;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.mzm.firephoenix.protobuf.CoreProtocol;

/**
 * 
 * @author oppa
 * 
 */
public class ByteEncoder implements ProtocolEncoder {
	public final static Log logger = LogFactory.getLog(ByteEncoder.class);

	@Override
	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		IoBuffer buffer;
		byte[] msgBody = ((CoreProtocol.MessagePack.Builder)message).build().toByteArray();
		int length = msgBody.length + 4;
		buffer = IoBuffer.allocate(length);
		buffer.putInt(length);
		buffer.put(msgBody);
		buffer.flip();
		out.write(buffer);
		buffer.free();
	}

	@Override
	public void dispose(IoSession session) throws Exception {

	}

}

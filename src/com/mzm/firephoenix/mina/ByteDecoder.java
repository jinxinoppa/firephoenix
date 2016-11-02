package com.mzm.firephoenix.mina;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * 
 * @author oppa
 * 
 */
public class ByteDecoder extends CumulativeProtocolDecoder {
	public final static Log logger = LogFactory.getLog(ByteDecoder.class);

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		if (in.remaining() < 1) {
			System.out.println("remaining less than 1");
			return false;
		}
		in.mark();
		byte[] lengthArr = new byte[1];
		in.get(lengthArr);
		int length = lengthArr[0];
		if (in.limit() < length) {
			System.out.println("limit : " + in.limit() + " length : " + length);
			in.reset();
			return false;
		}
		byte[] msgArr = new byte[length];
		in.get(msgArr);
		out.write(msgArr);
		in.free();
		return true;
	}

}

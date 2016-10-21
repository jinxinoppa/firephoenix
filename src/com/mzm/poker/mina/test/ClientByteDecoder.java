package com.mzm.poker.mina.test;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.mzm.poker.utils.SocketUtil;

/**
 * 解密客户端请求的数据。先AES解密，再解压缩。
 * 
 * @author oppa
 * 
 */
public class ClientByteDecoder extends CumulativeProtocolDecoder {
	public final static Log logger = LogFactory.getLog(ClientByteDecoder.class);
	private String HEAD = "";

	private String IOS_HEAD = "iosp";
	public static long receiveBytes = 0;

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		// receiveBytes+=in.array().length;
		// logger.info("############Receive all bytes:" + receiveBytes);
		// requestData, start, allData, head
		// Object[] data = getJSONReq(in);
		//
		// byte[] request = (byte[]) data[0];
		// byte[] allData = (byte[]) data[2];
		// if (null == request) {
		// in.position((Integer) data[1]);
		// return false;
		// }
		// String head = (String) data[3];
		//
		// byte[] decryptByte = new byte[0];
		// if (HEAD.equals(head)) {
		// byte[] decompressBytes = new byte[0];
		// try {
		// decryptByte = AES.decryptAES(request);
		// decompressBytes = Compresser.decompressBytes(request);
		// decompressBytes = request;
		// } catch (Exception e) {
		// decryptByte = AES.decryptAES(request);
		// decompressBytes = Compresser.decompressBytes(request);
		// logger.error("decryptByte::" + new String(decryptByte));
		// logger.error("decompressBytes::" + new String(decompressBytes));
		// }

		// ByteMessage bm = new ByteMessage(decompressBytes);
		// ByteMessage bm = new ByteMessage(request);
		if (in.remaining() < 1) {
			System.out.println("remaining less than 1");
			return false;
		}
		in.mark();
		byte[] lengthArr = new byte[4];
		in.get(lengthArr);
		int length = SocketUtil.byte2Int(lengthArr);
		if (in.limit() < length) {
			System.out.println("limit : " + in.limit() + " length : " + length);
			in.reset();
			return false;
		}
		byte[] msgArr = new byte[length - 4];
		in.get(msgArr);
		out.write(msgArr);
		in.free();
		return true;
		// } else {
		// if (session.getAttribute("ios") == null) {
		// session.setAttribute("ios", "1");
		// }
		//
		// decryptByte = AES.decryptAES4IOS(request);
		// byte[] decompressBytes = Compresser.decompressBytes(decryptByte);
		//
		// String jsonString = new String(decompressBytes, Constant.CHARSET);
		//
		// try {
		// JSONObject jo = new JSONObject(jsonString);
		//
		// out.write(jo);
		// in.free();
		// return true;
		// } catch (Exception e) {
		// byte[] nb = new byte[0];
		// int i = 0;
		// for (byte b : allData) {
		// if (i >= 8) {
		// nb = ArrayUtils.add(nb, b);
		// }
		// i++;
		// }
		// decryptByte = AES.decryptAES(nb);
		// decompressBytes = Compresser.decompressBytes(decryptByte);
		// jsonString = new String(decompressBytes, Constant.CHARSET);
		// logger.error("new jsonString:" + jsonString);
		//
		// JSONObject jo = new JSONObject(jsonString);
		// out.write(jo);
		// in.free();
		// return true;
		//
		// }
		// }

	}

	private Object[] getJSONReq(IoBuffer buf) {
		byte[] requestHead = new byte[0];
		byte[] requestLength = new byte[0];
		byte[] requestData = new byte[0];
		byte[] allData = new byte[0];
		int start = buf.position();
		int lengthEnd = start + 4;

		// for (int i = buf.position(); i < buf.limit() && i < headEnd; i++) {
		// byte data = (byte) (buf.get());
		// requestHead = ArrayUtils.add(requestHead, data);
		// allData = ArrayUtils.add(allData, data);
		// }
		// String head = new String(requestHead);
		String head = "";
		// if (!HEAD.equals(head) && !IOS_HEAD.equals(head)) {
		// return new Object[] { null, start + 1, allData, head };
		// }

		for (int i = buf.position(); i < buf.limit() && i < lengthEnd; i++) {
			byte data = (byte) (buf.get());
			requestLength = ArrayUtils.add(requestLength, data);
			allData = ArrayUtils.add(allData, data);
		}
		int length;
		if (requestLength.length == 4) {
			length = SocketUtil.byte2Int(requestLength) + start;
		} else {
			return new Object[] { null, start, allData, head };
		}
		if (buf.limit() < length) {
			return new Object[] { null, start, allData, head };
		}
		for (int i = buf.position(); i < buf.limit()
				&& i - buf.position() < length; i++) {
			byte data = (byte) (buf.get());
			requestData = ArrayUtils.add(requestData, data);
			allData = ArrayUtils.add(allData, data);
		}

		return new Object[] { requestData, start, allData, head };

	}

}

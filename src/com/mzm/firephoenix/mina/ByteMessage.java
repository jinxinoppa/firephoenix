package com.mzm.firephoenix.mina;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mzm.firephoenix.utils.SocketUtil;


/**
 * 
 */
public class ByteMessage {
	private final static Log logger = LogFactory.getLog(ByteMessage.class);

	private byte[] data;
	private int offset;

	public ByteMessage() {
		data = ArrayUtils.EMPTY_BYTE_ARRAY;
	}
	
	public ByteMessage(byte[] b) {
		data = b;
		offset = 0;
	}

	/**
	 * 鍒濆鍖栵紝浼犲叆鍗忚缂栧彿
	 * 
	 * @param actId
	 */
	public ByteMessage(short actId) {
		data = ArrayUtils.EMPTY_BYTE_ARRAY;
		offset = 0;
		writeShort(actId);
	}
	
	public int getLength(){
		return data.length;
	}

	/**
	 * 鍐欏叆byte
	 * 
	 * @param b
	 * @return
	 */
	public int writeByte(byte b) {
		data = ArrayUtils.add(data, b);
		return data.length;
	}

	/**
	 * 鍐欏叆byte
	 * 
	 * @param b
	 * @return
	 */
	public int writeByte(int b) {
		data = ArrayUtils.add(data, (byte) b);
		return data.length;
	}

	/**
	 * 鍐欏叆short
	 * 
	 * @param num
	 * @return
	 */
	public int writeShort(short num) {
		data = ArrayUtils.addAll(data, SocketUtil.short2bytes(num));
		return data.length;
	}

	/**
	 * 鍐欏叆鏁扮粍闀垮害
	 * 
	 * @param length
	 * @return
	 */
	public int writeArrLength(int length) {
		return writeShort((short) length);
	}

	/**
	 * 鍐欏叆int
	 * 
	 * @param num
	 * @return
	 */
	public int writeInt(int num) {
		data = ArrayUtils.addAll(data, SocketUtil.int2bytes(num));
		return data.length;
	}

	/**
	 * 鍐欏叆string
	 * 
	 * @param s
	 * @return
	 */
	public int writeString(String s) {
		byte[] sByte = ArrayUtils.EMPTY_BYTE_ARRAY;
		try {
			sByte = s.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(e, e);
		}
		writeShort((short) sByte.length);
		data = ArrayUtils.addAll(data, sByte);
		return data.length;
	}

	/**
	 * 鍐欏叆timestamp
	 * 
	 * @param num
	 * @return
	 */
	public int writeTimestamp(Timestamp time) {
		int num = (int) (time.getTime() / DateUtils.MILLIS_PER_SECOND);
		data = ArrayUtils.addAll(data, SocketUtil.int2bytes(num));
		return data.length;
	}

	/**
	 * 鍐欏叆鏁翠釜byte鏁扮粍
	 * 
	 * @param b
	 * @return
	 */
	public int writeData(byte[] b) {
		data = ArrayUtils.addAll(data, b);
		return data.length;
	}

	/**
	 * 鎸塨yte璇诲彇
	 * 
	 * @return
	 */
	public byte readByte() {
		byte b = data[offset++];
		reset();
		return b;
	}

	/**
	 * 璇诲彇short
	 * 
	 * @return
	 */
	public short readShort() {
		byte[] b = new byte[2];
		for (int i = 0; i < 2; i++) {
			b[i] = data[offset++];
		}
		reset();
		return SocketUtil.bytes2short(b);
	}

	/**
	 * 璇诲彇鏁扮粍闀垮害
	 * 
	 * @return
	 */
	public short readArrLength() {
		return readShort();
	}

	/**
	 * 璇诲彇int
	 * 
	 * @return
	 */
	public int readInt() {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = data[offset++];
		}

		reset();
		return SocketUtil.byte2Int(b);
	}

	/**
	 * 璇诲彇string
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String readString() {
		short strLength = readShort();
		byte[] b = new byte[strLength];
		try {
			for (int i = 0; i < strLength; i++) {
				b[i] = data[offset++];
			}	
		} catch (Exception e) {
			logger.error("offset : [ " + offset + " ] strLength : [ " + strLength + " ] ");
		}
		reset();
		try {
			return new String(b, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(e, e);
			return "";
		}
	}

	/**
	 * 鑾峰緱鏁翠釜鏁扮粍
	 * 
	 * @return
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * 鑾峰緱鍓╀綑鐨勬暣涓暟缁�
	 * 
	 * @return
	 */
	public byte[] getLeftData() {
		byte[] b = new byte[data.length - offset];
		for (int i = 0; i < b.length; i++) {
			b[i] = data[offset++];
		}
		return b;
	}

	/**
	 * 閲嶇疆offset
	 */
	private void reset() {
		if (offset >= data.length) {
			offset = 0;
		}
	}
	
	public void clear(){
		data = ArrayUtils.EMPTY_BYTE_ARRAY;
		offset = 0;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0, n = data.length; i < n; i++) {
			sb.append("[" + i + "]=" + data[i] + ",");
		}
		return sb.toString();
	}

}

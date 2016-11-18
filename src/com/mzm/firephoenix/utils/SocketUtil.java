package com.mzm.firephoenix.utils;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SocketUtil {

	public final static Log logger = LogFactory.getLog(SocketUtil.class);

	public static void main(String args[]) throws UnsupportedEncodingException {
		// byte[] b = short2bytes((short)-6);
		// short s = bytes2short(b);
		// System.out.println(s);
		// 142704644
		//
		// byte[] b = {8,81,80,4};
		byte[] b = int2bytes(25);
		int a = byte2Int(b);
		System.out.println(a);
		// for (int i = 0, n = b.length; i < n; i++) {
		// System.out.println(b[i]);
		// }
		//
		// System.out.println(b[3] & 0xff | (b[2] & 0xff) << 8 | (b[1] & 0xff)
		// << 8 | (b[0] & 0xff) << 8);

	}

	public static byte[] short2bytes(short num) {
		byte[] b = new byte[2];
		b[0] = (byte) ((num >> 8) & 0xff);
		b[1] = (byte) (num & 0xff);
		return b;
	}

	public static short bytes2short(byte[] b) {
		short s = (short) ((b[0] << 8) + (b[1] & 0xff));
		return s;
	}

	public static byte[] int2bytes(int num) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (num >>> (24 - i * 8));
		}
		return b;
	}
	// public static byte[] int2bytes(int num) {
	// byte[] b = new byte[4];
	// for (int i = 0, j = 3; i < 4; i++, j--) {
	// b[i] = (byte) (num >>> (24 - j * 8));
	// }
	// return b;
	// }

	public static int byte2Int(byte[] b) {
		int mask = 0xff;
		int temp = 0;
		int n = 0;
		for (int i = 0; i < 4; i++) {
			n <<= 8;
			temp = b[i] & mask;
			n |= temp;
		}
		return n;
	}

	public static int bytesToInt(byte[] bytes) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (bytes[i + 4] & 0x000000FF) << shift;
		}
		return value;
	}

	public static byte[] createReturnMessage(short act, Object... args) {
		byte[] actByte = short2bytes(act);
		byte[] data = convert2Byte(true, args);
		return ArrayUtils.addAll(actByte, data);
	}

	private static byte[] convert2Byte(boolean flag, Object... args) {
		byte[] data = ArrayUtils.EMPTY_BYTE_ARRAY;
		for (int i = 0, n = args.length; i < n; i++) {
			Object o = args[i];
			byte[] temp = ArrayUtils.EMPTY_BYTE_ARRAY;
			if (o.getClass().equals(Byte.class)) {
				temp = new byte[]{(Byte) o};
			} else if (o.getClass().equals(Short.class)) {
				temp = short2bytes((Short) o);
			} else if (o.getClass().equals(Integer.class)) {
				temp = int2bytes((Integer) o);
			} else if (o.getClass().equals(String.class)) {
				try {
					String s = (String) o;
					byte[] sByte = s.getBytes("UTF-8");
					byte[] lengthByte = short2bytes((short) sByte.length);

					temp = ArrayUtils.addAll(lengthByte, sByte);
				} catch (UnsupportedEncodingException e) {
					logger.error(e, e);
				}
			} else if (o.getClass().isArray()) {// 是数组,递归处理
				Object[] arr = (Object[]) o;
				byte[] lengthByte = ArrayUtils.EMPTY_BYTE_ARRAY;
				if (flag) {
					lengthByte = short2bytes((short) arr.length);
				}
				byte[] arrByte = ArrayUtils.EMPTY_BYTE_ARRAY;
				for (int j = 0, m = arr.length; j < m; j++) {
					arrByte = ArrayUtils.addAll(arrByte, convert2Byte(false, arr[j]));
				}
				temp = ArrayUtils.addAll(lengthByte, arrByte);
			}
			data = ArrayUtils.addAll(data, temp);
		}
		return data;
	}

	public static String getLogicName(int cmdNumber) {
		switch (cmdNumber) {
			case 65538 :
			case 65539 :
			case 65541 :
			case 131073 :
			case 131074 :
			case 131080 :
			case 196645 :
				return "account";
			case 65543 :
			case 65544 :
			case 65545 :
			case 65546 :
			case 65547 :
				return "card";
			case 131075 :
			case 131076 :
			case 131077 :
			case 131078 :
			case 131079 :
				return "machine";
			default :
				return null;
		}
	}
}

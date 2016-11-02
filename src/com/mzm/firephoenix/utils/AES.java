package com.mzm.firephoenix.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;


/**
 * AES加密解密类
 * 
 * TODO sun.misc.BASE64Decoder 是比较慢的方式，换成apache的codec
 * 
 * 
 */
public class AES {
	// private static final String AES_KEY = "bluepandabluepan";
	private static final String AES_KEY = "gj@34ddf$2fj!@4d";
	private static final String AES_KEY_PASSPORT = "@kiu7^%4h*93he8!";
	private static final String AES_KEY_BALANCE = "8^J#d76@iw&OU0G*";
	
	/** 
	 * 加密 
	 *  
	 * @param content 需要加密的内容 
	 * @param password  加密密码 
	 * @return 
	 */  
	public static byte[] encrypt(String content, String password) {  
		if(password==null) password=AES_KEY;
	        try {             
	                KeyGenerator kgen = KeyGenerator.getInstance("AES");  
	                kgen.init(128, new SecureRandom(password.getBytes()));
	                SecretKey secretKey = kgen.generateKey();  
	                byte[] enCodeFormat = secretKey.getEncoded();  
	                SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");  
	                Cipher cipher = Cipher.getInstance("AES");// 创建密码器  
	                byte[] byteContent = content.getBytes("utf-8");  
	                cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化  
	                byte[] result = cipher.doFinal(byteContent);  
	                return result; // 加密  
	        } catch (NoSuchAlgorithmException e) {  
	                e.printStackTrace();  
	        } catch (NoSuchPaddingException e) {  
	                e.printStackTrace();  
	        } catch (InvalidKeyException e) {  
	                e.printStackTrace();  
	        } catch (UnsupportedEncodingException e) {  
	                e.printStackTrace();  
	        } catch (IllegalBlockSizeException e) {  
	                e.printStackTrace();  
	        } catch (BadPaddingException e) {  
	                e.printStackTrace();  
	        }  
	        return null;  
	}  
	/**解密 
	 * @param content  待解密内容 
	 * @param password 解密密钥 
	 * @return 
	 */  
	public static byte[] decrypt(byte[] content, String password) { 
		if(password==null) password=AES_KEY;
	        try {  
	                 KeyGenerator kgen = KeyGenerator.getInstance("AES");  
	                 kgen.init(128, new SecureRandom(password.getBytes()));  
	                 SecretKey secretKey = kgen.generateKey();  
	                 byte[] enCodeFormat = secretKey.getEncoded();  
	                 SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");              
	                 Cipher cipher = Cipher.getInstance("AES");// 创建密码器  
	                cipher.init(Cipher.DECRYPT_MODE, key);// 初始化  
	                byte[] result = cipher.doFinal(content);  
	                return result; // 加密  
	        } catch (NoSuchAlgorithmException e) {  
	                e.printStackTrace();  
	        } catch (NoSuchPaddingException e) {  
	                e.printStackTrace();  
	        } catch (InvalidKeyException e) {  
	                e.printStackTrace();  
	        } catch (IllegalBlockSizeException e) {  
	                e.printStackTrace();  
	        } catch (BadPaddingException e) {  
	                e.printStackTrace();  
	        }  
	        return null;  
	}  
	public static byte[] encryptAES(byte[] b) {
		if (AES_KEY == null) {
			return null;
		}
		if (AES_KEY.length() != 16) {
			return null;
		}
		try {
			byte[] raw = AES_KEY.getBytes();
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(b);
			return encrypted;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] decryptAES(byte[] b) {

		if (AES_KEY == null) {
			return null;
		}
		if (AES_KEY.length() != 16) {
			return null;
		}
		try {
			byte[] raw = AES_KEY.getBytes();
			SecretKeySpec skp = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, skp);
			byte[] original = cipher.doFinal(b);
			return original;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String decryptAES4Passport(String encryptText) {
		try {
			if (AES_KEY_PASSPORT == null) {
				return null;
			}

			if (AES_KEY_PASSPORT.length() != 16) {
				return null;
			}
			byte[] raw = AES_KEY_PASSPORT.getBytes("ASCII");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec iv = new IvParameterSpec(
					"0210925111010145".getBytes());
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] encryptedByte = new BASE64Decoder()
					.decodeBuffer(encryptText);
			byte[] plainByte = cipher.doFinal(encryptedByte);
			String plainText = new String(plainByte);
			return plainText;
		} catch (Exception ex) {
			return null;
		}
	}

	public static String decryptAES4Balance(String encryptText) {
		try {
			if (AES_KEY_BALANCE == null) {
				return null;
			}

			if (AES_KEY_BALANCE.length() != 16) {
				return null;
			}
			byte[] raw = AES_KEY_BALANCE.getBytes("ASCII");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec iv = new IvParameterSpec(
					"0210925111010145".getBytes());
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] encryptedByte = new BASE64Decoder()
					.decodeBuffer(encryptText);
			byte[] plainByte = cipher.doFinal(encryptedByte);
			String plainText = new String(plainByte);
			return plainText;
		} catch (Exception ex) {
			return null;
		}
	}

	public static byte[] encryptAES4IOS(byte[] b) {
		try {
			if (AES_KEY == null) {
				return null;
			}

			if (AES_KEY.length() != 16) {
				return null;
			}
			byte[] raw = AES_KEY.getBytes();
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec iv = new IvParameterSpec(
					"0210925111010145".getBytes());
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

			byte[] encrypted = cipher.doFinal(b);
			return encrypted;
		} catch (Exception ex) {
			return null;
		}
	}

	public static byte[] decryptAES4IOS(byte[] b) {
		try {
			if (AES_KEY == null) {
				return null;
			}

			if (AES_KEY.length() != 16) {
				return null;
			}
			byte[] raw = AES_KEY.getBytes();
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec iv = new IvParameterSpec(
					"0210925111010145".getBytes());
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] original = cipher.doFinal(b);
			return original;
		} catch (Exception ex) {
			return null;
		}
	}

	public static byte[] decryptAES4Base64(String encryptText) {
		try {
			if (AES_KEY == null) {
				return null;
			}

			if (AES_KEY.length() != 16) {
				return null;
			}
			byte[] raw = AES_KEY.getBytes();
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec iv = new IvParameterSpec(
					"0210925111010145".getBytes());
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] encryptedByte = new BASE64Decoder()
					.decodeBuffer(encryptText);
			byte[] original = cipher.doFinal(encryptedByte);
			return original;
		} catch (Exception ex) {
			return null;
		}
	}

	public static void main(String args[]) throws UnsupportedEncodingException {

		// String s = "[1111111,23477,2387,3984534,3853948,5838593,73823]";
		// byte[] aa=s.getBytes(Constant.CHARSET);

//		byte[] aa = SocketUtil.int2bytes(1111111);
//		aa = ArrayUtils.addAll(aa, SocketUtil.int2bytes(23477));
//		aa = ArrayUtils.addAll(aa, SocketUtil.int2bytes(2387));
//		aa = ArrayUtils.addAll(aa, SocketUtil.int2bytes(3984534));
//		aa = ArrayUtils.addAll(aa, SocketUtil.int2bytes(3853948));
//		aa = ArrayUtils.addAll(aa, SocketUtil.int2bytes(5838593));
//		aa = ArrayUtils.addAll(aa, SocketUtil.int2bytes(73823));
//
//		System.out.println(aa.length);
//		byte[] bytes = Compresser.compressBytes(aa);
//		System.out.println(bytes.length);
//		bytes = AES.encryptAES(bytes);
//		System.out.println(bytes.length);

		// byte[] aaa = SocketUtil.short2bytes((short) 99);
		// System.out.print(SocketUtil.bytes2short(aaa));
		// System.out.println("\n-------");
		// byte[] b = AES.encryptAES("abc".getBytes(Constant.CHARSET));
		// for (int i = 0; i < b.length; i++) {
		// System.out.print("[" + i + "]=" + b[i] + " ");
		// }
		// System.out.println(new String(b));
		// System.out.println("\n-------");

		// String s = "{\"data\":[],\"act\":\"gm_onlineCount\"}";
		// String s = "{\"data\":[],\"act\":\"gm_onlineCount\"}";
		// String s="a";
		// // byte[] b = Compresser.compressBytes(s.getBytes(Constant.CHARSET));
		// // b = AES.encryptAES(b);
		// byte[] b = AES.encryptAES4IOS(s.getBytes(Constant.CHARSET));
		// System.out.println(new String(b));
		//
		// for (int i = 0; i < b.length; i++) {
		// System.out.print("["+i+"]="+b[i] + " ");
		// }
		// System.out.println("\n-------");

		// byte[] decryptByte = AES.decryptAES(b);
		// byte[] decompressBytes = Compresser.decompressBytes(decryptByte);
		//
		// String jsonString = new String(decompressBytes, Constant.CHARSET);
		// System.out.println(jsonString);
		String content = "test";  
		String password = "12345678";  
		//加密  
		System.out.println("加密前：" + content);  
		byte[] encryptResult = encrypt(content, password);  
		//解密  
		byte[] decryptResult = decrypt(encryptResult,password);  
		System.out.println("解密后：" + new String(decryptResult)); 
	}
}
package com.mzm.poker.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Compresser {
	private static int cachesize = 1024;
	
//	private static Deflater compresser = new Deflater();

	public static byte[] compressBytes(byte input[]) {
		Deflater compresser = new Deflater();
		compresser.reset();
		compresser.setInput(input);
		compresser.finish();
		byte output[] = new byte[0];
		ByteArrayOutputStream o = new ByteArrayOutputStream(input.length);
		try {
			byte[] buf = new byte[cachesize];
			int got;
			while (!compresser.finished()) {
				got = compresser.deflate(buf);
				o.write(buf, 0, got);
			}
			output = o.toByteArray();
		} finally {
			try {
				o.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return output;
	}

	public static byte[] decompressBytes(byte input[]) throws Exception {
		byte output[] = new byte[0];
		Inflater decompresser = new Inflater();
		decompresser.reset();
		decompresser.setInput(input);
		ByteArrayOutputStream o = new ByteArrayOutputStream(input.length);
		try {
			byte[] buf = new byte[cachesize];
			int got;
			while (!decompresser.finished()) {
				got = decompresser.inflate(buf);
				o.write(buf, 0, got);
			}
			output = o.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {
				o.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return output;
	}

	public static void main(String args[]) {
		String s = "pack";
		byte[] b = compressBytes(s.getBytes());
//		byte[] b= SocketUtil.int2bytes();
		for (int i = 0; i < b.length; i++) {
			System.out.print(b[i] + " ");
		}
		
		byte[] a = null;
		try {
			a = decompressBytes(b);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(new String(a));

	}
}

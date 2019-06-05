package com.shanjin.manager.utils;


import java.security.MessageDigest;

/**
 * MD5加密工具类
 * @author Huang yulai
 *
 */
public class Md5Util {
	public static String md5Code(String str) {
		try {
			byte[] res = str.getBytes();
			MessageDigest md = MessageDigest.getInstance("MD5".toUpperCase());
			byte[] result = md.digest(res);
			for (int i = 0; i < result.length; i++) {
				md.update(result[i]);
			}
			byte[] hash = md.digest();
			StringBuffer d = new StringBuffer("");
			for (int i = 0; i < hash.length; i++) {
				int v = hash[i] & 0xFF;
				if (v < 16)
					d.append("0");
				d.append(Integer.toString(v, 16).toUpperCase() + "");
			}
			return d.toString();
		} catch (Exception e) {
			return "";
		}

	}

	// 通用加密
	public static String md5CodeCommon(String plainStr) {
		byte[] source = plainStr.getBytes();
		String s = null;
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(source);
			byte tmp[] = md.digest();

			char str[] = new char[16 * 2];
			int k = 0;
			for (int i = 0; i < 16; i++) {
				byte byte0 = tmp[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			s = new String(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
}

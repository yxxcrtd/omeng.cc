package com.shanjin.financial.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

public class MD5Util {

	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(MD5Util.class);

	public final static String MD5_32(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("", e);
			return null;
		}
	}

	public final static String MD5_16(String plainText) {
		String result = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			result = buf.toString().substring(8, 24);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			logger.error("", e);
		}
		return result;
	}

	/**
	 * 生成处理过的base64 md5编码
	 * @param secret
	 * @param url
	 * @param expired
	 * @return
	 * @throws NoSuchAlgorithmException 
	 */
	private static String downloadKey(String secret,String url,int expired) throws NoSuchAlgorithmException{
		
	    String result=null;
	    
	    String path = url.substring(url.lastIndexOf("/"));
		String content = secret+path+expired;
		
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(content.getBytes());
			byte bytes[] = md.digest();
			
			result= new String(Base64.encodeBase64(bytes));
			result = result.replaceAll("\\+", "-");
			result = result.replaceAll("/", "_");
			result = result.replaceAll("=", "");
			
		} catch (NoSuchAlgorithmException e) {
			 logger.error("生成图片下载toke错误："+e.getMessage(), e);
			 throw e;
		}
		return result;
	}
	
	
	/**
	 * 返回图片访问的后缀
	 * @param secret		秘钥
	 * @param url			访问资源的路径
	 * @param expired	             失效 
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String getPostFix(String secret,String url, int expired) throws NoSuchAlgorithmException{
		StringBuffer  result= new StringBuffer("?st=");
		int timeout =  (int) (System.currentTimeMillis()/1000 + expired) ;
	//	int timeout = 1439296495;
		String token = downloadKey(secret,url,timeout);
		result.append(token).append("&e=").append(timeout);
		return result.toString();
	}
	
	/*
	public static void main(String[] args) {
		String time = System.currentTimeMillis() + "";
		// MD5_32(time+clientId+phone+userKey)
		String token = MD5Util.MD5_32(time + "" + "00DC29EA10FAD898F93015507D80BD1A" + "" + "15821865990" + "" + "BA10067D621487D8469C56BB58F7F502");
		System.out.println("time: " + time);
		System.out.println("token: " + token);
	} */
	
	public static void main(String[] args) throws NoSuchAlgorithmException{
			String secret="www.omeng.com.cn";
			int expired =  5*60*60;
		//	String  url ="http://192.168.11.131/building.jpg";
			String url ="http://192.168.1.31/building.jpg";
			System.out.println("toke is"+getPostFix(secret,url,expired));
	}
}

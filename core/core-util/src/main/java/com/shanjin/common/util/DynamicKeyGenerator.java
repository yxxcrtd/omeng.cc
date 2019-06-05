package com.shanjin.common.util;

import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.log4j.Logger;

public class DynamicKeyGenerator {
	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(DynamicKeyGenerator.class);
	public static Random random = new Random();

	public static String generateDynamicKey() {
		KeyGenerator kGen = null;
		try {
			kGen = KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			logger.error("", e);
		}
		kGen.init(128);
		SecretKey key = kGen.generateKey();
		return AESUtil.parseByte2HexStr(key.getEncoded());
	}

	/**
	 * 生成15位流水号 当前系统时间(后10位)+5位随机水
	 * @return
	 */
	public static String  generateDynaminKey15()
	{
		int radom = random.nextInt(90000) +10000;
		String key = String.valueOf(System.currentTimeMillis());
		key = key.substring(key.length()-10, key.length());
		key = key +String.valueOf(radom);
		return key;
	}
	
	
	public static void main(String[] args) {
		System.out.println(DynamicKeyGenerator.generateDynamicKey());
	}

}

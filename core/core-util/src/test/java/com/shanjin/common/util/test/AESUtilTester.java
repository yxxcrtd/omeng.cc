package com.shanjin.common.util.test;

import org.junit.Assert;
import org.junit.Test;

import com.shanjin.common.util.AESUtil;

public class AESUtilTester {

	private String plainText = "我是明文";
	private String encrypedText = "7D7E69DAA3EA3E86AB6878070E30949F";
	private static String key = "F87A5F1229185C9AADAAA48C7F5249D5";
	
	@Test
	public void testEncrypt() {
		try {
			String result = AESUtil.parseByte2HexStr(AESUtil.encrypt(plainText, key));
			Assert.assertEquals(result, encrypedText);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
		
	}
	
	@Test
	public void testDecrypt() {
		try {
			String result = AESUtil.decrypt(encrypedText, key);
			Assert.assertEquals(result, plainText);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

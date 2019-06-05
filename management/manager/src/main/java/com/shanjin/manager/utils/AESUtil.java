package com.shanjin.manager.utils;


import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;

public class AESUtil {
	private static final String bm = "UTF-8";
	private static final String KEY_GENERATION_ALG = "PBKDF2WithHmacSHA1";

	private static final int HASH_ITERATIONS = 10000;
	private static final int KEY_LENGTH = 128;

	private static final byte[] salt = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0xA, 0xB, 0xC, 0xD,
			0xE, 0xF }; // must save this for next time we want the key

	private static final String CIPHERMODEPADDING = "AES/CBC/PKCS7Padding";

	private static byte[] iv = { 0xA, 1, 0xB, 5, 4, 0xF, 7, 9, 0x17, 3, 1, 6, 8, 0xC,
			0xD, 91 };
	private static IvParameterSpec IV = new IvParameterSpec(iv);
	static {//FIXME
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}
	public static byte[] encrypt(String plainText, String password) throws UnsupportedEncodingException {
		SecretKeySpec skforAES = generateIvParameterSpecAndIvParameterSpec(password);
		byte[] ciphertext = encrypt(CIPHERMODEPADDING, skforAES, IV, plainText.getBytes("UTF8"));
		return ciphertext;
	}

	public static String decrypt(String encryptedText, String password) {
		SecretKeySpec skforAES = generateIvParameterSpecAndIvParameterSpec(password);
		String decrypted;
		try {
			decrypted = new String(decrypt(CIPHERMODEPADDING, skforAES, IV,
					parseHexStr2Byte(encryptedText)), bm);
			return decrypted;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static byte[] encrypt(String cmp, SecretKey sk, IvParameterSpec IV,
			byte[] msg) {
		try {
			Cipher c = Cipher.getInstance(cmp);
			c.init(Cipher.ENCRYPT_MODE, sk, IV);
			return c.doFinal(msg);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static byte[] decrypt(String cmp, SecretKey sk, IvParameterSpec IV,
			byte[] ciphertext) {
		try {
			Cipher c = Cipher.getInstance(cmp);
			c.init(Cipher.DECRYPT_MODE, sk, IV);
			return c.doFinal(ciphertext);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
                String hex = Integer.toHexString(buf[i] & 0xFF);
                if (hex.length() == 1) {
                        hex = '0' + hex;
                }
                sb.append(hex.toUpperCase());
        }
        return sb.toString();
}
    
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
                return null;
        byte[] result = new byte[hexStr.length()/2];
        for (int i = 0;i< hexStr.length()/2; i++) {
                int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);
                int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);
                result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
    
    public static SecretKeySpec generateIvParameterSpecAndIvParameterSpec(String password) {
		try {
			PBEKeySpec myKeyspec = new PBEKeySpec(password.toCharArray(), salt,
					HASH_ITERATIONS, KEY_LENGTH);
			SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(KEY_GENERATION_ALG);
			SecretKey sk = keyfactory.generateSecret(myKeyspec);
			byte[] skAsByteArray = sk.getEncoded();
			SecretKeySpec skforAES = new SecretKeySpec(skAsByteArray, "AES");
			return skforAES;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return null;
    }
    
    public static String reverse(String text){
        return new StringBuffer(text).reverse().toString();
    }
    
    public static void main(String[] args) {
//    	String plainText = "{\"key\":\"23456\"}";
//    	String dynamicKey = "367937E1967092280C56077755E4C65B";
//    	String encryptedText = null;
//        try {
//        	encryptedText = parseByte2HexStr(AESUtil.encrypt(plainText, dynamicKey));
//        	System.out.println("加密后的数据--->"+encryptedText);
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
//        
    	try {
    		String decrypt = AESUtil.decrypt("89D4DE1D5C2114C3422BDFBC84765622716ECCD7DC44ADACC77BB9D6EBB345201614ABE5C1E2138664E5C98C14C59D774909737DB531A9E336019C015E0F6246E8C59E4E7461CA467E9F2ABD6AF3659395D95C078203C93E9B5EDBE719D38388D8699E4020A6FFC9D975FB9D1DF1EC565E736D4A4D5C0A69A50DBE21C10397F71A7BF3F4EF3A0F26E56EA3C9FC351643FDF3B6477E431B6F1BFA68E9E724BED6199AA4CC1920DDADA853964D9527A8C7E2D2B5095F2061BBFA9FDBEF74D7DD6BD4D307AA1EE4589BD5F34020C476FD7DC65AF069C61980444BFBAB6F2A1F4A466734EFBE2B6A203ECB3A167D3BB1B250AED04B0B9DD6D1F94EC352290AF77004C7E9020B1A75A262C9EEEE6248B98ADB86AD308B14B57B351E8BE5CEF772AB1900487BF187AFDB71329E6220707A1038", "367937E1967092280C56077755E4C65B");
    		System.out.println("解密数据--->"+decrypt);
    	} catch (Exception e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    }
}
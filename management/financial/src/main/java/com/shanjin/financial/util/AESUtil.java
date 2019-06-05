package com.shanjin.financial.util;

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
    	String plainText = "D09413D6EFF4C00CA7DC994A906906BA";
    	String dynamicKey = "80DBC293132DF7F75BB3BA2563236559";
    	String encryptedText = "FAE4C592B5C1754285889AE73D2658AF97709715DEF46481E56370442D921DCD8D3AF909499DA3E01601526EC5753B4398B9EEDE87639BEAC90B9EA8A2076F5CAD37C6834A933F79295DDC18B7724BCE396D65EDE13490D10186D2DDB1B44A7461A26CDB1CF1AFB03C13023D06046E84266392173AB413157D8F854B2AAC7E0420EF07FC4AC067AF184CFC58DD4707B51363AC44A572458B5ABF63A2AFDFA2CB831F154E80FFE0877786A22EF0770CDE7E542266580014AC998F3175C72E5F06B3F5793332F1E07F1EB73C4AC47FF1EACD12FCD3C820ECBD7A14AEE6EB86DA41";
    	
//        try {
//        	System.out.println(parseByte2HexStr(AESUtil.encrypt(plainText, dynamicKey)));
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
        
    	try {
    		System.out.println(AESUtil.decrypt(encryptedText, dynamicKey));
    	} catch (Exception e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    }
}
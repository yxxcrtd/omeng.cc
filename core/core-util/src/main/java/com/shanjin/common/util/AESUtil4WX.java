package com.shanjin.common.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

public class AESUtil4WX {
    private static final int keySize = 128;
    private static final int iterationCount = 100;
    private static final String CIPHERMODEPADDING = "AES/CBC/PKCS5Padding";
    private static final String salt = "0001020304050607080A0B0C0D0E0F09";
    private static final String iv = "06080C0D5B0A010B05040F0709170301";

    public static String encrypt( String passphrase, String plaintext){
        return hex(base64(encrypt(salt,iv,passphrase,plaintext))).toUpperCase();
    }

    public static String decrypt( String passphrase, String ciphertext){
        return decrypt(salt,iv,passphrase,base64(hex(ciphertext)));
    }

    private static String encrypt(String salt, String iv, String passphrase, String plaintext) {
        try {
            SecretKey key = generateKey(salt, passphrase);
            byte[] encrypted = doFinal(Cipher.ENCRYPT_MODE, key, iv, plaintext.getBytes("UTF-8"));
            return base64(encrypted);
        }
        catch (UnsupportedEncodingException e) {
            throw fail(e);
        }
    }

    private static String decrypt(String salt, String iv, String passphrase, String ciphertext) {
        try {
            SecretKey key = generateKey(salt, passphrase);
            byte[] decrypted = doFinal(Cipher.DECRYPT_MODE, key, iv, base64(ciphertext));
            return new String(decrypted, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw fail(e);
        }
    }
    
    private static byte[] doFinal(int encryptMode, SecretKey key, String iv, byte[] bytes) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHERMODEPADDING);
            cipher.init(encryptMode, key, new IvParameterSpec(hex(iv)));
            return cipher.doFinal(bytes);
        }
        catch (Exception e) {
            throw fail(e);
        }
    }
    
    private static SecretKey generateKey(String salt, String passphrase) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), hex(salt), iterationCount, keySize);
            SecretKey key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
            return key;
        }
        catch (Exception e) {
            throw fail(e);
        }
    }
    
    public static String random(int length) {
        byte[] salt = new byte[length];
        new SecureRandom().nextBytes(salt);
        return hex(salt);
    }
    
    public static String base64(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }
    
    public static byte[] base64(String str) {
        return Base64.decodeBase64(str);
    }
    
    public static String hex(byte[] bytes) {
        return Hex.encodeHexString(bytes);
    }
    
    public static byte[] hex(String str) {
        try {
            return Hex.decodeHex(str.toCharArray());
        }
        catch (DecoderException e) {
            throw new IllegalStateException(e);
        }
    }
    
    private static IllegalStateException fail(Exception e) {
        return new IllegalStateException(e);
    }

    public static void main(String args[]){


        String passphrase = "B102000007186E4A554F756103D4590F";
        String plaintext = "3F550FE089A3BB9A701E0CE46E08C800";

        String ciphertext = AESUtil4WX.encrypt(passphrase,plaintext);

        System.out.println(ciphertext);
        System.out.println(hex(base64(ciphertext)));
        System.out.println(AESUtil4WX.decrypt(passphrase, ciphertext));

        ciphertext = "81F90A6F811340B2AF6BFED478F3B3AA87F3103C05871AA189A14AD961C308C65849EA0D6CC37C30B46D3FA16CA31A476FE55E0A011F2A6343A27BF3858333DE0FA2E55709A22851512BD3C2A9756F594717EBACEB96136F817F8A85B857780C9BC57C11669467450F83AFDAF9580B32C1F15DBDA2F345CD1A37BF558D3DC6EAFCE1F7520A8127CAAC7FF556DD656257CFE62585E8C1026B06A6D5B621DDA6912C2FC0B5F4FE3D1DB0341434CEB4D4AAFEF605EB3FDB5DFD1746631AFECD1754AAD7A9168471CDAA6B573BBB7731CFA58A982ECBF06BA2A814BDAD84AD441FD56A0AB7C74BE7890D72359BEC810D1693";
        System.out.println(AESUtil4WX.decrypt(plaintext, ciphertext));

        String plaintext2 = "{\"message\":\"获取服务端打包的支付参数异常\",\"resultCode\":\"003\"}";
        String ciphertext2 = AESUtil4WX.encrypt(plaintext, plaintext2);
        System.out.println(ciphertext2);

        System.out.println(AESUtil4WX.decrypt(plaintext, ciphertext2));
    }
}

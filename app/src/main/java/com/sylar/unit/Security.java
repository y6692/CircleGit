/*
 * Encryption and DEcryption 
 * 
 * Created by sealy on 2013-07-01. 
 * Copyright 2013 Sealy, Inc. All rights reserved.
 */

package com.sylar.unit;

import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Encryption and DEcryption
 *
 * @author Wikison
 */

public class Security {
    private static final String strTag = "Security";
    /*
     * 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。
     */
    public static String sKey = "1375d7ac2b2a8e23";//key
    public static String ivParameter = "1234567890123456";//偏移量

    /**
     * Encryption with MD5
     *
     * @param sData The string of Special encrypted.
     * @return the string with MD5, or null
     */
    public static String encryptByMD5(String sData) {
        try {
            byte[] bTemp = encryptByMD5(sData.getBytes("UTF-8"));
            StringBuffer sBuffer = new StringBuffer();
            for (int intI = 0; intI < bTemp.length; intI++) {
                if (Integer.toHexString(0xFF & bTemp[intI]).length() == 1)
                    sBuffer.append("0").append(Integer.toHexString(0xFF & bTemp[intI]));
                else
                    sBuffer.append(Integer.toHexString(0xFF & bTemp[intI]));
            }
            return sBuffer.toString();
        } catch (Exception e) {
            Log.e(strTag, "Security.encryptByMD5:" + e.toString());
        }
        return null;
    }

    /**
     * Encryption with MD5
     *
     * @param data the byte data
     * @return the byte data
     */
    public static byte[] encryptByMD5(byte[] data) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(data);
            return md5.digest();
        } catch (Exception e) {
            Log.e(strTag, "Security.encryptByMD5:" + e.toString());
        }
        return null;
    }

    /**
     * Encryption by Base64
     *
     * @param data The string of Special encrypted.
     * @return the string, or null
     */
    public static String encryptByBase64(String data) {
        try {
            return Base64.encodeToString(data.getBytes(), Base64.DEFAULT);
        } catch (Exception e) {
            Log.e(strTag, "Security.encryptByBase64:" + e.toString());
        }
        return null;
    }

    /**
     * Encryption by Base64
     *
     * @param data the byte data.
     * @return the string, or null
     */
    public static String encryptByBase64(byte[] data) {
        try {
            return Base64.encodeToString(data, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e(strTag, "Security.encryptByBase64:" + e.toString());
        }
        return null;
    }

    /**
     * DEcryption by Base64
     *
     * @param data The string of Special DEcrypted.
     * @return the string, or null
     */
    public static String decryptByBase64(String data) {
        try {
            return new String(Base64.decode(data, Base64.DEFAULT));
        } catch (Exception e) {
            Log.e(strTag, "Security.decryptByBase64:" + e.toString());
        }
        return null;
    }

    /**
     * DEcryption by Base64
     *
     * @param data The string of Special DEcrypted.
     * @return the byte data, or null
     */
    public static byte[] decryptByBase64_byte(String data) {
        try {
            return Base64.decode(data, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e(strTag, "Security.decryptByBase64_byte:" + e.toString());
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        String content = "{\"account\":\"15380479973\",\"password\":\"E10ADC3949BA59ABBE56E057F20F883E\"}";

        System.out.println("加密前：" + content);
        System.out.println("加密密钥和解密密钥：" + sKey);
        String encrypt = encrypt(content);
        System.out.println("加密后：" + encrypt);


        String decrypt = decrypt(encrypt);
        System.out.println("解密后：" + decrypt);


//		InterEntity objEntity = getMsgEntity(encrypt);
//		System.out.println(objEntity.getAccount());
//		System.out.println(objEntity.getIndustryId());
//		System.out.println(objEntity.getDiscount());

    }

    public static String encrypt(String encData, String secretKey, String vector) throws Exception {
        if (secretKey == null) {
            return null;
        }
        if (secretKey.length() != 16) {
            return null;
        }
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] raw = secretKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec iv = new IvParameterSpec(vector.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(encData.getBytes("utf-8"));
        return new String(encryptByBase64(encrypted));
    }

    // 加密
    public static String encrypt(String sSrc) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] raw = sKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
        return new String(encryptByBase64(encrypted));
    }

    public static String decrypt(String sSrc, String key, String ivs) throws Exception {
        try {
            byte[] raw = key.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ivs.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = decryptByBase64_byte(sSrc);// 先用base64解密
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original, "utf-8");
        } catch (Exception ex) {
            return null;
        }
    }

    // 解密
    public static String decrypt(String sSrc) throws Exception {
        try {
            byte[] raw = sKey.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = decryptByBase64_byte(sSrc);
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original, "utf-8");
        } catch (Exception ex) {
            return null;
        }
    }


    public static String encodeBytes(byte[] bytes) {
        StringBuffer strBuf = new StringBuffer();

        for (int i = 0; i < bytes.length; i++) {
            strBuf.append((char) (((bytes[i] >> 4) & 0xF) + ((int) 'a')));
            strBuf.append((char) (((bytes[i]) & 0xF) + ((int) 'a')));
        }

        return strBuf.toString();
    }


}

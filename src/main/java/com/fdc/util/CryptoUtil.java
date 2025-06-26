package com.fdc.util;

import com.fdc.exception.BusinessException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class CryptoUtil {

    public static String encrypt(String plaintext, byte[] sk) throws Exception {
        if (sk.length != 16) {
            throw new BusinessException("500", "AES-128 密钥长度必须为16个字符");
        }
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");  // 严格来说，ECB 模式不是最安全的
        SecretKeySpec secretKey = new SecretKeySpec(sk, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String encrypt(byte[] data, byte[] sk) throws Exception {
        if (sk.length != 16) {
            throw new BusinessException("500", "AES-128 密钥长度必须为16个字符");
        }
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec secretKey = new SecretKeySpec(sk, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(data);
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static byte[] md5(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String md5Base64(String input) throws Exception {
        byte[] md5Bytes = md5(input);
        return Base64.getEncoder().encodeToString(md5Bytes);
    }

    public static String base64Encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

}

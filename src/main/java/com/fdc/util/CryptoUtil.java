package com.fdc.util;

import com.fdc.exception.BusinessException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class CryptoUtil {

    public static String encrypt(String plaintext, byte[] sk) {
        if (sk.length != 16) {
            throw new BusinessException("500", "AES-128 密钥长度必须为16个字符");
        }
        byte[] encryptedBytes;
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // 严格来说，ECB 模式不是最安全的
            SecretKeySpec secretKey = new SecretKeySpec(sk, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new BusinessException("500", "加密失败: " + e.getMessage());
        }

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String encrypt(byte[] data, byte[] sk)  {
        if (sk.length != 16) {
            throw new BusinessException("500", "AES-128 密钥长度必须为16个字符");
        }
        byte[] encryptedBytes;
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec secretKey = new SecretKeySpec(sk, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            encryptedBytes = cipher.doFinal(data);
        } catch (Exception e) {
            throw new BusinessException("500", "加密失败: " + e.getMessage());
        }

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static byte[] md5(String input)  {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessException("500", "MD5 算法不可用: " + e.getMessage());
        }
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String md5Base64(String input) {
        byte[] md5Bytes = md5(input);
        return Base64.getEncoder().encodeToString(md5Bytes);
    }

    public static String base64Encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public static byte[] base64Decode(String base64String) {
        return Base64.getDecoder().decode(base64String);
    }

    public static String hexEncode(byte[] data) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : data) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static byte[] sha256(byte[] input)  {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessException("500", "SHA-256 算法不可用: " + e.getMessage());
        }
        return md.digest(input);
    }
}

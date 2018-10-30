package com.kvo.utils.digest;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

/**
 * MD5摘要算法
 */
@Slf4j
public abstract class MD5Utils extends AbstractDigestUtils {

    /**
     * 对输入流进行md5散列.
     */
    public static byte[] md5(InputStream input) throws IOException, GeneralSecurityException {
        return digest(input, MD5);
    }

    /**
     * 对输入字符串进行sha1散列.
     */
    public static byte[] md5(byte[] input) throws GeneralSecurityException {
        return digest(input, MD5, null, 1);
    }

    public static byte[] md5(byte[] input, byte[] salt) throws GeneralSecurityException {
        return digest(input, MD5, salt, 1);
    }

    public static byte[] md5(byte[] input, byte[] salt, int iterations) throws GeneralSecurityException {
        return digest(input, MD5, salt, iterations);
    }

    /**
     * 生成MD5
     *
     * @param data
     * @return
     */
    public static String md5(String data) {
        try {
            byte[] messageByte = data.getBytes("UTF-8");
            // 获得MD5字节数组,16*8=128位
            byte[] md5Byte = md5(messageByte);
            // 转换为16进制字符串
            return bytesToHex(md5Byte);
        } catch (Exception e) {
            log.error(String.format("md5 error %s", e.getMessage()));
        }
        return null;
    }


}

package com.kvo.utils.digest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;

public abstract class AbstractDigestUtils {

    protected static final String SHA1 = "SHA-1";

    protected static final String MD5 = "MD5";

    private static SecureRandom random = new SecureRandom();

    /**
     * 对字符串进行散列, 支持md5与sha1算法.
     */
    protected static byte[] digest(byte[] input, String algorithm, byte[] salt, int iterations) throws GeneralSecurityException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        if (salt != null) {
            digest.update(salt);
        }
        byte[] result = digest.digest(input);

        for (int i = 1; i < iterations; i++) {
            digest.reset();
            result = digest.digest(result);
        }
        return result;
    }

    protected static byte[] digest(InputStream input, String algorithm) throws IOException, GeneralSecurityException {
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        ByteArrayOutputStream baso = new ByteArrayOutputStream();
        IOUtils.copy(input, baso);
        return messageDigest.digest(baso.toByteArray());
    }


    // 二进制转十六进制
    protected static String bytesToHex(byte[] bytes) {
        StringBuffer hexStr = new StringBuffer();
        int num;
        for (int i = 0; i < bytes.length; i++) {
            num = bytes[i];
            if (num < 0) {
                num += 256;
            }
            if (num < 16) {
                hexStr.append("0");
            }
            hexStr.append(Integer.toHexString(num));
        }
        return hexStr.toString().toUpperCase();
    }

    /**
     * 生成随机的Byte[]作为salt.
     *
     * @param numBytes byte数组的大小
     */
    public static byte[] generateSalt(int numBytes) {
        Validate.isTrue(numBytes > 0, "numBytes argument must be a positive integer (1 or larger)", numBytes);
        byte[] bytes = new byte[numBytes];
        random.nextBytes(bytes);
        return bytes;
    }


}

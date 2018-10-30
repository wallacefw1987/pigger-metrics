package com.kvo.utils.digest;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

public abstract  class SHAUtils extends AbstractDigestUtils {

    /**
     * 对输入流进行md5散列.
     */
    public static byte[] sha1(InputStream input) throws IOException, GeneralSecurityException {
        return digest(input, SHA1);
    }

    /**
     * 对输入字符串进行sha1散列.
     */
    public static byte[] sha1(byte[] input) throws GeneralSecurityException {
        return digest(input, SHA1, null, 1);
    }

    public static byte[] sha1(byte[] input, byte[] salt) throws GeneralSecurityException {
        return digest(input, SHA1, salt, 1);
    }

    public static byte[] sha1(byte[] input, byte[] salt, int iterations) throws GeneralSecurityException {
        return digest(input, SHA1, salt, iterations);
    }

}

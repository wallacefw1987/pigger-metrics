package com.kvo.utils.encrypt;

import com.kvo.utils.encode.Base64Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.Arrays;

/**
 * AES 加密类
 */
@Slf4j
public abstract class AESUtils {

    /**
     * 默认加密Key
     */
    public final static String DEFAULT_KEY = "!@#$%^&*(";

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    /**
     * 获得密钥
     *
     * @param secretKey
     * @return
     * @throws Exception
     */
    private static SecretKey generateKey(String secretKey) throws NoSuchAlgorithmException {
        //防止linux下 随机生成key
        Provider p = Security.getProvider("SUN");
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG", p);
        secureRandom.setSeed(secretKey.getBytes());
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(secureRandom);
        // 生成密钥
        return kg.generateKey();
    }


    /**
     * AES 加密
     *
     * @param content
     * @param key
     * @return
     */
    public static byte[] aesEncrypt(String content, String key) throws GeneralSecurityException, UnsupportedEncodingException {
        if (StringUtils.isBlank(key)) {
            key = DEFAULT_KEY;
        }
        SecretKey secretKey = generateKey(key);
        byte[] enCodeFormat = secretKey.getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        byte[] byteContent = content.getBytes("utf-8");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        return cipher.doFinal(byteContent);
    }

    /**
     * AES 加密 Base64 编码
     *
     * @param content
     * @param key
     * @return
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    public static String aesEncryptToBase64(String content, String key) throws GeneralSecurityException, UnsupportedEncodingException {
        byte[] aesEncrypt = aesEncrypt(content, key);
        if(log.isDebugEnabled()) {
            log.debug("aesEncrypt String:" + Arrays.toString(aesEncrypt));
        }
        String base64EncodeStr = Base64Utils.encodeFromByteArray(aesEncrypt);
        if(log.isDebugEnabled()) {
            log.debug("aesEncryptToBase64 base64EncodeStr:" + base64EncodeStr);
        }
        return base64EncodeStr;
    }

    /**
     * AES 解密
     *
     * @param content
     * @param key
     * @return
     * @throws GeneralSecurityException
     */
    public static byte[] aesDecrypt(byte[] content, String key) throws GeneralSecurityException {
        if (StringUtils.isBlank(key)) {
            key = DEFAULT_KEY;
        }
        SecretKey secretKey = generateKey(key);
        byte[] enCodeFormat = secretKey.getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        return cipher.doFinal(content);
    }

    /**
     * AES 解密 Base64位编码
     *
     * @param content
     * @param key
     * @return
     * @throws UnsupportedEncodingException
     * @throws GeneralSecurityException
     */
    public static String aesDecryptFromBase64(String content, String key) throws UnsupportedEncodingException, GeneralSecurityException {
        byte[] base64DecodeStr = Base64Utils.decodeFromString(content);
        if(log.isDebugEnabled()) {
            log.debug("aesDecryptFromBase64 String:" + Arrays.toString(base64DecodeStr));
        }
        byte[] aesDecode = aesDecrypt(base64DecodeStr, key);
        if (aesDecode == null) {
            return null;
        }
        String result = new String(aesDecode, "UTF-8");
        if(log.isDebugEnabled()) {
            log.debug("aesDecryptFromBase64 result:" + result);
        }
        return result;
    }
}

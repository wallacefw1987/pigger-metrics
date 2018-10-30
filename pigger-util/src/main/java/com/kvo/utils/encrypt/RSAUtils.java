package com.kvo.utils.encrypt;


import com.kvo.utils.encode.Base64Utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * <p>
 * RSA公钥/私钥/签名工具包
 * </p>
 * <p>
 * 罗纳德·李维斯特（Ron [R]ivest）、阿迪·萨莫尔（Adi [S]hamir）和伦纳德·阿德曼（Leonard [A]dleman）
 * </p>
 * <p>
 * 字符串格式的密钥在未在特殊说明情况下都为BASE64编码格式<br/>
 * 由于非对称加密速度极其缓慢，一般文件不使用它来加密而是使用对称加密，<br/>
 * 非对称加密算法可以用来对对称加密的密钥加密，这样保证密钥的安全也就保证了数据的安全
 * </p>
 *
 * @author IceWee
 * @version 1.0
 * @date 2012-4-26
 */
public abstract class RSAUtils {

    /**
     * 加密算法RSA
     */
    public static final String KEY_ALGORITHM = "RSA";

    /**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * <p>
     * 生成密钥对(公钥和私钥)
     * </p>
     *
     * @return
     * @throws Exception
     */
    public static KeyPair getRSAKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new KeyPair(publicKey, privateKey);
    }

    /**
     * <p>
     * 用私钥对信息生成数字签名
     * </p>
     *
     * @param data       已加密数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = Base64Utils.decodeFromString(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data);
        return Base64Utils.encodeFromByteArray(signature.sign());
    }

    /**
     * <p>
     * 校验数字签名
     * </p>
     *
     * @param data      已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign      数字签名
     * @return
     * @throws Exception
     */
    public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
        byte[] keyBytes = Base64Utils.decodeFromString(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(Base64Utils.decodeFromString(sign));
    }

    public static byte[] decryptByPrivateKey(String base64, String privateKey) throws Exception {
        return RSAUtils.decryptByPrivateKey(Base64Utils.decodeFromString(base64), privateKey);
    }

    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey) throws Exception {
        byte[] keyBytes = Base64Utils.decodeFromString(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        return RSAUtils.decryptByPrivateKey(encryptedData, privateK);
    }

    /**
     * <P>
     * 私钥解密
     * </p>
     *
     * @param encryptedData 已加密数据
     * @param privateKey    私钥(BASE64编码)
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws IOException
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, Key privateKey) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * <p>
     * 公钥加密
     * </p>
     *
     * @param data      源数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {
        byte[] keyBytes = Base64Utils.decodeFromString(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }


    public static void main(String[] args) {

        String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKM2m1DwZB7pNZ/jCjtNjTIf/uBBz5RTAIXce4C0NWZI3NCZMwjhGHU3TWORTWiR4K0xpaQMeVPierMu9DdAStQhFGOHwDwVEwZoZEdRtcFCAU44z559uii6uj5h5f9n7CpAj6XuCpuIw5OaCnbezYigW4oZlkwbb23YCsbCTYKXAgMBAAECgYAvIXy2YcS3vGZ01k5FE777Q4wJXFfCIDndbe7oGqH0+INzZVn8bWvvpVmer/3nfYCg4t5PQh7jH1qJVbzGG7W/l+zVhxPD8JZUSSCDpkG5tSN/WjudEs4jFOrBwHEmmQhOVxNVbhHr04mYc2EjFc6cRszE+cr+jL//EJ2m2KHXuQJBANSJIDyI2HLzOAZYIZLpMiadmw21+fc0TnhoGJax8zeSEjJHCKTVfNghnJMB4sz4LEp4r8Vxq/9qC/ftOVA8e5MCQQDEl08y53wICJvkzYwRNujOkP7UN1gQk9Blp+MCBDAyC0su2QXN489caEkfqIsaGkQ5hOyAYITJyFLWrWgiUKdtAkADljTXELAmcHhXkxEt+4V/JVXCi3mmdFmGqqsPj/08yNhJxO0eawPqWdbcnEGJl/6XWyYMPPjcHJwLGRPIiNzFAkB0uut57F2HX6VBbJNYvWPrE8lcNSiDX2GEmvV+AwKjFHWl8ZMyt6wu0HVq6Ob/rs0H4leTurveHKajdf+5MG7ZAkBW22tEY46hCLY8A//wTzj/NcDSqM/sF2LB3b5w9IWFBubwjYBA3jKR74zmqGq6NXk4YPaOQkdTgkjqkl2cSBSd";

        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCjNptQ8GQe6TWf4wo7TY0yH/7gQc+UUwCF3HuAtDVmSNzQmTMI4Rh1N01jkU1okeCtMaWkDHlT4nqzLvQ3QErUIRRjh8A8FRMGaGRHUbXBQgFOOM+efboouro+YeX/Z+wqQI+l7gqbiMOTmgp23s2IoFuKGZZMG29t2ArGwk2ClwIDAQAB";

        try {
            byte[] bytes = RSAUtils.encryptByPublicKey(
                    "{\"data\": {\"password\": \"a123456\",\"phone\": \"13800138000\"}}".getBytes(), publicKey);
            System.out.println(Base64Utils.encodeFromByteArray(bytes));

            System.out.println(new String(RSAUtils.decryptByPrivateKey(
                    "hyLdzlofqmjl5cfTUb26h107gC3MznWEJjdaKinLGk1XSevVI4RWduasgAQ+Sgk+UYL3JSlMh1Is/sQoOEVCsg8U+LWvMZ73gyijewyW0VymJ/qjoLZWBpWsLZcAwchEuc6IGoKOnAT66xWiuwI4cnhtTBY+/nPvgLKBvf/pf/U=",
                    privateKey)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
package com.kvo.utils.encode;


import org.apache.commons.io.FileUtils;
import java.io.File;
import java.util.Base64;

/**
 * BASE64编码解码工具包
 */
public abstract class Base64Utils {

    /**
     * 文件读取缓冲区大小
     */
    private static final int CACHE_SIZE = 1024;

    /**
     * <p>
     * BASE64字符串解码为二进制数据
     * </p>
     *
     * @param source
     * @return
     * @throws Exception
     */
    public static byte[] decodeFromString(String source) {
        return Base64.getDecoder().decode(source.getBytes());
    }

    /**
     * <p>
     * 二进制数据编码为BASE64字符串
     * </p>
     *
     * @param bytes
     * @return
     * @throws Exception
     */
    public static String encodeFromByteArray(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * <p>
     * 将文件编码为BASE64字符串
     * </p>
     * <p>
     * 大文件慎用，可能会导致内存溢出
     * </p>
     *
     * @param filePath 文件绝对路径
     * @return
     * @throws Exception
     */
    public static String encodeFromFilePath(String filePath) throws Exception {
        byte[] bytes = FileUtils.readFileToByteArray(new File(filePath));
        return encodeFromByteArray(bytes);
    }

    /**
     * <p>
     * BASE64字符串转回文件
     * </p>
     *
     * @param filePath 文件绝对路径
     * @param base64   编码字符串
     * @throws Exception
     */
    public static void decodeToFile(String filePath, String base64) throws Exception {
        byte[] bytes = decodeFromString(base64);
        FileUtils.writeByteArrayToFile(new File(filePath), bytes);
    }


}

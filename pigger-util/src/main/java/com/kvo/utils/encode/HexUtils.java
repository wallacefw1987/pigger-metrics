package com.kvo.utils.encode;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

@Slf4j
public abstract class HexUtils {

    /**
     * Hex编码.
     */
    public static String encode(byte[] input) {
        return Hex.encodeHexString(input);
    }

    /**
     * Hex解码.
     */
    public static byte[] decode(String input) {
        try {
            return Hex.decodeHex(input.toCharArray());
        } catch (DecoderException e) {
            log.error(String.format("decode error %s", e.getMessage()), e);
        }
        return null;
    }

}

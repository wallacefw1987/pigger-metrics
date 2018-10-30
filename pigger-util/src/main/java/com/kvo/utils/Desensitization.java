package com.kvo.utils;

/**
 * 脱敏工具类
 */
public abstract class Desensitization {


    /**
     * 身份证号脱敏
     *
     * @param idCard
     * @return
     */
    public static String idCard(String idCard) {
        return desensitize(idCard, "(\\w{4})\\w{10}(\\w{4})", "$1**********$2");
    }

    /**
     * 手机号脱敏
     *
     * @param phone
     * @return
     */
    public static String phone(String phone) {
        return desensitize(phone, "(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }


    /**
     * 银行卡脱敏
     *
     * @param bankCardNo
     * @return
     */
    public static String bankCardNo(String bankCardNo) {
        return desensitize(bankCardNo, "(\\d{4})\\d{8}(\\d{4})", "$1********$2");
    }

    /**
     * 脱敏方法
     *
     * @param source
     * @param regExp
     * @param presentExp
     * @return
     */
    private static String desensitize(String source, String regExp, String presentExp) {
        if (StringUtils.isNotBlank(source)) {
            return source.replaceAll(regExp, presentExp);
        }
        return null;
    }

}



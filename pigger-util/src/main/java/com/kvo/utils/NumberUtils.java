package com.kvo.utils;

import java.text.DecimalFormat;

public abstract class NumberUtils {

    /**
     * 保留两位小数
     */
    public final static DecimalFormat decimal2Format = new DecimalFormat("0.00");

    /**
     * 保留整数
     */
    public final static DecimalFormat integerFormat = new DecimalFormat("0");
}

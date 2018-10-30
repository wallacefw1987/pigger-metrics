package com.kvo.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.util.Date;

/**
 * twitter的snowflake算法 -- java实现
 *
 * @author beyond
 * @date 2016/11/26
 * @url https://raw.githubusercontent.com/beyondfengyu/SnowFlake/master/SnowFlake.java
 */
@Slf4j
public class SnowFlakeIdGenerator {

    /**
     * 起始的时间戳
     */
    private final static long START_STMP = 1514736000000L;

    /**
     * 每一部分占用的位数
     */
    private final static long SEQUENCE_BIT = 12; //序列号占用的位数
    private final static long MACHINE_BIT = 5;   //机器标识占用的位数
    private final static long DATACENTER_BIT = 5;//数据中心占用的位数

    /**
     * 每一部分的最大值
     */
    private final static long MAX_DATACENTER_NUM = -1L ^ (-1L << DATACENTER_BIT);
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

    /**
     * 每一部分向左的位移
     */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;
    /**
     * 内部默认生成器
     */
    private static final SnowFlakeIdGenerator snowFlakeIdGenerator = new SnowFlakeIdGenerator(0, 0);

    private long datacenterId;  //数据中心
    private long machineId;     //机器标识
    private long sequence = 0L; //序列号
    private long lastStmp = -1L;//上一次时间戳

    private SnowFlakeIdGenerator(long datacenterId, long machineId) {
        if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
            throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }
        this.datacenterId = datacenterId;
        this.machineId = machineId;
    }

    /**
     * 默认生辰器
     *
     * @return
     */
    public synchronized static long getDefaultNextId() {
        long id;
        // 必须大于5位数
        do {
            id = snowFlakeIdGenerator.nextId();
        } while (Long.toString(id).length() < 10);
        return id;
    }

    /**
     * 产生下一个ID
     *
     * @return
     */
    public synchronized long nextId() {
        long currStmp = getNewstmp();
        if (currStmp < lastStmp) {
            log.error("Clock moved backwards.  Refusing to generate id, currentTimestamp={}, lastTimestamp={}",
                    currStmp, lastStmp);
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }

        if (currStmp == lastStmp) {
            //相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            //同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                currStmp = getNextMill();
            }
        } else {
            //不同毫秒内，序列号置为0
            sequence = 0L;
        }

        lastStmp = currStmp;

        return (currStmp - START_STMP) << TIMESTMP_LEFT //时间戳部分
                | datacenterId << DATACENTER_LEFT       //数据中心部分
                | machineId << MACHINE_LEFT             //机器标识部分
                | sequence;                             //序列号部分
    }

    private long getNextMill() {
        long mill = getNewstmp();
        while (mill <= lastStmp) {
            mill = getNewstmp();
        }
        return mill;
    }

    private long getNewstmp() {
        return System.currentTimeMillis();
    }

    /**
     * 默认时间格式
     */
    private final static FastDateFormat fdf = FastDateFormat.getInstance("ddHHmmss");

    /**
     * 获取流水号带前缀
     *
     * @return
     */
    public static String getSerialNo(String prefix) {
        try {
            long nextid = snowFlakeIdGenerator.nextId();
            if (nextid > 0) {
                return prefix + nextid;
            }
        } catch (Exception e) {
            log.error(String.format("getSerialNo prefix<%s>", prefix), e);
        }
        String time = "";
        try {
            time = fdf.format(new Date());
        } catch (Exception e) {
            log.error(String.format("getSerialNo prefix<%s>", prefix), e);
            time = "00000000";
        }
        return prefix + time + RandomStringUtils.randomNumeric(5);
    }

}

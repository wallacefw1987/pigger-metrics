package com.kvo.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
public abstract class StringUtils extends org.apache.commons.lang3.StringUtils {

    private static Pattern linePattern = Pattern.compile("_(\\w)");
    private static Pattern humpPattern = Pattern.compile("[A-Z]");

    /**
     * 下划线转驼峰
     */
    public static String lineToHump(String str) {
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 驼峰转下划线,效率比上面高
     */
    public static String humpToLine(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 清理字符串类型内容的前后引号
     */
    public static String cleanToJson(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        if (str.indexOf("\"") == 0) {
            str = str.substring(1, str.length()); // 去掉第一个 "
        }
        if (str.lastIndexOf("\"") == (str.length() - 1)) {
            str = str.substring(0, str.length() - 1); // 去掉最后一个 "
        }
        str = str.replace("\\\\", "");
        str = str.replace("\\\"", "\"");// \"转化问题
        return str;
    }

    /**
     * 清理json中的多斜杠数据
     * @param jsonStr
     * @return
     */
    public static String formatJson(String jsonStr){
        String params = "";
        JSONObject object = JSONObject.parseObject(jsonStr);
        params += StringEscapeUtils.unescapeJava(JSONObject.toJSONString(object))
                .replaceAll("\\\\","")
                .replaceAll("\\\\\\\\","").replaceAll("\\\"","\"")
                .replaceAll(":\"\\{",":{")
        .replaceAll("}\"","}");
        return params;
    }
}

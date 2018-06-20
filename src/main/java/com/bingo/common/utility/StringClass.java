package com.bingo.common.utility;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Created by Administrator on 2017/5/26.
 */
public class StringClass {
    public static boolean isNullOrEmpty(String str) {
        if (str == null) {
            return true;
        }
        if (Objects.equals(str, "")) {
            return true;
        }
        return false;
    }

    public static List<Integer> toIntList(String str) {
        if (isNullOrEmpty(str)) {
            return null;
        }
        String[] arrStr = str.split(",");
        List<Integer> list = new LinkedList<>();
        for (String st : arrStr) {
            list.add(Integer.parseInt(st));
        }
        return list;
    }

    public static List<Long> toLongList(String str) {
        if (isNullOrEmpty(str)) {
            return null;
        }
        String[] arrStr = str.split(",");
        List<Long> list = new LinkedList<>();
        for (String st : arrStr) {
            list.add(Long.parseLong(st));
        }
        return list;
    }

    public static List<String> toStringList(String str) {
        if (isNullOrEmpty(str)) {
            return null;
        }
        String[] arrStr = str.split(",");
        List<String> list = new LinkedList<>();
        for (String st : arrStr) {
            list.add(String.valueOf(st));
        }
        return list;
    }

    public static String join(Iterable<?> iterable) {
        return StringUtils.join(iterable, ",");
    }

    public static String join(Iterable<?> iterable, String separator) {
        return StringUtils.join(iterable, separator);
    }

    // 返回长度为【strLength】的随机数，在前面补0
    public static String randomStr(int strLength) {

        Random rm = new Random();

        // 获得随机数
        double pross = (1 + rm.nextDouble()) * Math.pow(10, strLength);

        // 将获得的获得随机数转化为字符串
        String fixLenthString = String.valueOf(pross);

        // 返回固定的长度的随机数
        return fixLenthString.substring(1, strLength + 1);
    }
}

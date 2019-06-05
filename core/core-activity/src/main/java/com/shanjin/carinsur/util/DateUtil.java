package com.shanjin.carinsur.util;

import java.util.Calendar;
import java.util.Date;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/9/3
 * @desc 日期工具类
 */
public class DateUtil {

    /**
     * 讲指定日期转换为 去除 只包含 年月日的日期值
     *
     * @param date
     */
    public static Date date2YmdDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}

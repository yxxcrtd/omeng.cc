package com.shanjin.manager.time;


import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;

public class TimerUtil {
    static int count = 0;
    private  static final Logger log = Logger.getLogger(TimerUtil.class);
    public static void executeTimer() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
            	log.info("merchentInfo商户统计excel开始执行:" + new Date()); // 1次
                StaticsThread staticsThread=new StaticsThread();
                staticsThread.setPriority(5);
                staticsThread.start();
                log.info("merchentInfo商户统计excel执行结束:" + new Date()); // 1次
            }
        };

        //设置执行时间
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);//每天
        //定制每天的21:09:00执行，
        calendar.set(year, month, day, 15, 22, 00);
        Date date = calendar.getTime();
        Timer timer = new Timer();
        System.out.println(date);
        
        // int period = 2 * 1000;
        //每天的date时刻执行task，每隔2秒重复执行
        //  timer.schedule(task, date, period);
        //每天的date时刻执行task, 仅执行一次
       timer.schedule(task, date);
    }

    public static void main(String[] args) {
    	//executeTimer();
    }
}

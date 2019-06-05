package com.shanjin.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/9/7
 * @desc 日志记录工具
 */
public class LogUtil {

    private static final Logger callBackLog = LoggerFactory.getLogger("CALL_BACK");

    private static  String hhf;
    static {
        String os = System.getProperty("os.name");
        if(os.startsWith("Windows")){
            hhf="\r\n";
        }else{
            hhf="\n";
        }
    }

    /**
     * 记录回调日志
     * @param callBackBody
     */
    public static final void logCallBackLog(final String callBackBody){
        new Thread(new Runnable() {
            @Override
            public void run() {
                callBackLog.info(callBackBody.replaceAll(hhf,""));
            }
        }).start();
    }
}

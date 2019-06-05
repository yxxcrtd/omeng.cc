package com.shanjin.financial.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;




import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayDataDataserviceBillDownloadurlQueryRequest;
import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.shanjin.financial.bean.IDownloadEvent;
import com.shanjin.financial.constant.Constants;

public class DownloadUtil {
    /** 
     * 从网络Url中下载文件 
     * @param urlStr 
     * @param fileName 
     * @param savePath 
     * @throws IOException 
     */  
    public static void  downLoadFromUrl(final String urlStr, final String fileName, final String savePath, final IDownloadEvent downloadEvent) throws IOException{
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //设置超时间为3秒
                    conn.setConnectTimeout(3 * 1000);
                    //防止屏蔽程序抓取而返回403错误
                    conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

                    //得到输入流
                    InputStream inputStream = conn.getInputStream();
                    //获取自己数组
                    byte[] getData = readInputStream(inputStream);

                    //文件保存位置
                    File saveDir = new File(savePath);
                    if (!saveDir.exists()) {
                        saveDir.mkdir();
                    }
                    File file = new File(saveDir + File.separator + fileName);
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(getData);
                    if (fos != null) {
                        fos.close();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    downloadEvent.complete(file.getAbsolutePath());
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();

  
//        System.out.println("info:"+url+" download success");
  
    }  
  
  
  
    /** 
     * 从输入流中获取字节数组 
     * @param inputStream 
     * @return 
     * @throws IOException 
     */  
    public static  byte[] readInputStream(InputStream inputStream) throws IOException {    
        byte[] buffer = new byte[1024];    
        int len = 0;    
        ByteArrayOutputStream bos = new ByteArrayOutputStream();    
        while((len = inputStream.read(buffer)) != -1) {    
            bos.write(buffer, 0, len);    
        }    
        bos.close();    
        return bos.toByteArray();    
    }
}

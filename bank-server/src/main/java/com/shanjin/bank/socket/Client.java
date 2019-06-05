package com.shanjin.bank.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.shanjin.bank.service.OpayService;
import com.shanjin.bank.util.ClientLogUtil;
import com.shanjin.bank.util.PropUtil;
  
public class Client {  
  
	private final Logger log = Logger.getLogger(Client.class);
	static Properties properties = PropUtil.getPropUtil("ipport.properties");
	static String bankServerIp = "";
	static Integer bankServerPort = 0;
	static {
    	if("true".equals(properties.getProperty("bank.server.online.inuse"))){
    		bankServerIp = properties.getProperty("bank.server.online.ip");
    		bankServerPort = Integer.parseInt(properties.getProperty("bank.server.online.port"));
    	}
    	else{
    		bankServerIp = properties.getProperty("bank.server.test.ip");
    		bankServerPort = Integer.parseInt(properties.getProperty("bank.server.test.port"));
    	}
    }
	
	
    public String send(String info){  
    	String advice="";
    	try{
    		//向服务器端发送请求，服务器IP地址和服务器监听的端口号  
            Socket client = new Socket(bankServerIp, bankServerPort);  
              
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(client.getOutputStream(),"GBK"),true);
            System.out.println("连接已建立...");  
                        
            //发送消息  
            printWriter.println(info);  
              
            printWriter.flush();  
              
            //InputStreamReader是低层和高层串流之间的桥梁  
            //client.getInputStream()从Socket取得输入串流  
            InputStreamReader streamReader = new InputStreamReader(client.getInputStream(),"GBK");  
              
            //链接数据串流，建立BufferedReader来读取，将BufferReader链接到InputStreamReder  
            BufferedReader reader = new BufferedReader(streamReader);  
            advice =reader.readLine();  
            System.out.println("接收到服务器的消息 ："+advice);  
            reader.close();
    	}
        catch(Exception e){
        	log.error(e.getMessage(),e);
        }
    	finally{
    		ClientLogUtil.insertLog("前置机","银行服务器","http://"+bankServerIp+":"+bankServerPort,info,advice);
    	}
    	
        return advice;
    }  
      
    public static void main(String[] args) throws UnknownHostException, IOException {  
        Client c = new Client();  
        c.send("hello");  
    }  
}  

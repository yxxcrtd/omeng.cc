package com.shanjin.outServices.aliOss;

import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * 压力测试
 * @author 2015.11.10 Revoke Yu
 *
 */
public class OssProfile {
	 private static String fileName;
	 private static String path;
	 private static Random  tools = new Random();
	 private static int TOTAL_CONSUMER = 20;
	 static CountDownLatch  latch = new CountDownLatch(TOTAL_CONSUMER);
	 static int TOTAL_FILES = 50;
	 
	public static void  main(String[] args){
		boolean devMod=true;
		path = "/upload/merchantInfo/image/portrait/201513/";
		fileName="test.jpg";
		AliOssUtil.setModel(false);
		
		
		Thread[] consumer= new MyThread[TOTAL_CONSUMER];
		for (int i=0;i<consumer.length;i++){
			consumer[i] = new MyThread();
		}

		for (int i=0;i<consumer.length;i++){
			consumer[i].start();
		}
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("finished");
	}
	
	
	
	
	static void upload(InputStream fileStream){
		
		AliOssUtil.upload(path, fileName+fileNo(), fileStream);
		
	}
	
	
	private static int fileNo(){
		  return  tools.nextInt(99999999);
	}

}

class MyThread extends Thread {
	 MyThread() {
	
	}
	
	 public void run(){
		  for (int i=0;i<OssProfile.TOTAL_FILES;i++){
			  OssProfile.upload(OssProfile.class.getResourceAsStream("/test.jpg"));
		  }
		  
		  OssProfile.latch.countDown();
	 }
}

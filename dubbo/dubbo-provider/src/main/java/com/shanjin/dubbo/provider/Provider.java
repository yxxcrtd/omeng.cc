package com.shanjin.dubbo.provider;

import com.shanjin.service.ICommonService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Provider {

	public static void main(String[] args) throws Exception {
	//	String[]  xmlContext = new String[] { "classpath*:spring/applicationContext.xml", "classpath*:spring/spring-mybatis.xml", "classpath*:spring/mq-beans.xml","classpath:spring/spring-rest.xml" }; 
		String[]  xmlContext = new String[] {"classpath*:spring/*.xml"};
		
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(xmlContext);
		context.start();
		
		testServer(context);
		BlockingQueue queue = new ArrayBlockingQueue(10);
		queue.take();
//		System.in.read(); // 为保证服务一直开着，利用输入流的阻塞来模拟
	}

	private static void testServer(ClassPathXmlApplicationContext context) {
		ICommonService  commonService= (ICommonService) context.getBean("commonService");
		System.out.println(commonService.helloWorld("hello"));
	
		
	}
	
	
		
}
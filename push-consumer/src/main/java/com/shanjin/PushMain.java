package com.shanjin;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PushMain {
	public static ApplicationContext context =null;
	public static void main(String[] args) {
		try{
			context = new ClassPathXmlApplicationContext("classpath*:spring/spring-beans.xml");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
		}			
	}
}

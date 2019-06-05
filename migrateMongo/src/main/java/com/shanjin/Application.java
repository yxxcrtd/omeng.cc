package com.shanjin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class Application {

	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "classpath*:spring/applicationContext.xml", "classpath*:spring/spring-mybatis.xml","classpath*:spring/spring-mongo.xml"});
		context.start();
	}

}

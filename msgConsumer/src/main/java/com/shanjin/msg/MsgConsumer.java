package com.shanjin.msg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.shanjin.common.constant.TopicConstants;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.kafka.ConsumerFactory;
import com.shanjin.mail.MailTools;
import com.shanjin.service.IUserInfoService;

/**
 * 消息处理器主程序
 * @author revoke  2015.12.9
 * --------------------------------------修订历史-----------------------------------------------------------
 *  2016.1.6 ：   加入serviceError主题的处理， serviceError主题与其它主题，才用不同的group 
 */
public class MsgConsumer {
	private static ClassPathXmlApplicationContext context=null;
	
	KafkaConsumer<String,String> consumer=null;
	
	
	MsgConsumer(boolean flag){
		List<String> topicList = new ArrayList<String>();
		
		if(!flag){
				topicList.add(TopicConstants.CLIENT_UPDATE_PUSH_LOGIN);
				topicList.add(TopicConstants.IP_CITY_RESOLVE);
		}else{
				topicList.add(TopicConstants.ERROR_TOPIC);
		}
		
		String[] topics = topicList.toArray(new String[0]);
		
		/**
		 * 如果采用同一个组，可能会丢失消息
		 */
		String groupId="vitalConsumer";
		if (flag){
			   groupId = "emailConsumer";
		}
		
		consumer = ConsumerFactory.getConsumer(groupId,
				topics, true,
				BusinessUtil.getIpAddress()).getOrginalConsumer();
	}
	
	
	public void start(){
		System.out.println("进入消息处理循环....");
		while (true) {
	         ConsumerRecords<String, String> records = consumer.poll(1000);
	         for (ConsumerRecord<String, String> record : records){
	        	   String topic = record.topic();
	        	   String key = record.key();
	        	   String content = record.value();
	        	   MessageProcessor.dispatch(topic,key,content);
	         }
	         if (records!=null && records.count()>0){
	        	 try{
	        		 	consumer.commitSync();
	        	 }catch(CommitFailedException ex){
	        		    //ignore commit
	        	 }catch(Exception ex){
	        		   ex.printStackTrace();
	        	 }
	         }	      
	    }
	}
	
	
	 public static void main(String[] args){
			context = new ClassPathXmlApplicationContext(
					new String[] { "classpath*:spring/spring-beans.xml"});
		
			if (args.length<1){
				prompt();
				return;
			}
			
			
			
			context.start();
	
		   // callTest();
			
			System.out.println("Spring 上下文加载成功");
			
			MessageProcessor.initProcessor(context);
			
			if(!MailTools.init()){
				System.exit(1);
			}
			
			System.out.println("消息处理器初始化成功");
			
		 	new MsgConsumer(Boolean.parseBoolean(args[0])).start();
	 }

	 
	 public static void callTest(){
		 IUserInfoService  userInfoService=(IUserInfoService) context.getBean("userService");
		 try {
			Map<String,String> userInfo = userInfoService.getUserInfoByPhoneWithStr("13865954757");
			System.out.println(userInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }
	 
	 
	 private static void prompt(){
		 System.out.println("格式为 java -jar msgConsumer.jar flag");
		 System.out.println("flag 为true 表示有公网， false 表示内网");
	 }
}

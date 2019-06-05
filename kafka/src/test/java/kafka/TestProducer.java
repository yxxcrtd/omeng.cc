package kafka;

import java.util.Random;

import org.junit.Test;

import com.shanjin.kafka.ProducerFactory;
import com.shanjin.kafka.ProducerInterface;
import com.shanjin.kafka.impl.ModulePartition;

public class TestProducer {
	
	
	@Test
	public void TestSendMsg(){
		 int totalMsgNum = 1000*1000;
		 String topic = "normalMsg1";
		 String content="abcdefghijklmnopqrstuvwxyxABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		 long beginTime = System.currentTimeMillis();
		 for (int i=0;i<totalMsgNum;i++){
			  ProducerFactory.getProducer(false,"192.168.1.54").sendMsg(topic, i+"---"+content);
		 }

		 long endTime = System.currentTimeMillis();
		 System.out.println(totalMsgNum+" cost :" + (endTime-beginTime)/1000.00f);
		 
		  ProducerFactory.getProducer(false,"192.168.1.54").flush();

		 
	}

	@Test
	public void TestSendVitalMsg(){
		 int totalMsgNum = 1000*1000;
		 String topic = "vitalMsg2";
		 String content="abcdefghijklmnopqrstuvwxyxABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		 long beginTime = System.currentTimeMillis();
		 for (int i=0;i<totalMsgNum;i++){
			  ProducerFactory.getProducer(true,"192.168.1.54").sendMsg(topic, i+"---"+content);
		 }
		 long endTime = System.currentTimeMillis();
		  ProducerFactory.getProducer(true,"192.168.1.54").flush();
		 System.out.println(totalMsgNum+" cost :" + (endTime-beginTime)/1000.00f);
		 
	}
	
	@Test
	public void TestSendPartitionMsg(){
		 int totalMsgNum = 1000*1000;
		 String topic = "vitalPhoneMsg2";
		 String content="abcdefghijklmnopqrstuvwxyxABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		 long beginTime = System.currentTimeMillis();
		 ProducerInterface producer=ProducerFactory.getProducer(true,"192.168.1.54");
		 producer.setPartition(topic, new ModulePartition(3));
		 for (int i=0;i<totalMsgNum;i++){
			 producer.sendMsg(topic,getPhoneNum(), i+"---"+content);
		 }
		 long endTime = System.currentTimeMillis();
		 ProducerFactory.getProducer(true,"192.168.1.54").flush();
		 System.out.println(totalMsgNum+" cost :" + (endTime-beginTime)/1000.00f);
	}
	
	
	
	
	private String getPhoneNum(){
		 StringBuffer phone=new StringBuffer();
		 for (int i=0;i<11;i++){
			  phone.append(getRandomInt());
		 }
		 return phone.toString();
	}
	
	private int getRandomInt(){
		 return new Random().nextInt(10);
	}
}

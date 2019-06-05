package kafka;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.junit.Test;

import com.shanjin.kafka.ConsumerFactory;
import com.shanjin.kafka.impl.NormalConsumer;
import com.shanjin.kafka.impl.VitalConsumer;

public class TestConsumer {
	
	@Test
	public void TestNormalConsumer(){
		    String[] topics ={ "normalMsg1"};
		    String  groupId="normalGroup";
		    String clientId="消费者1";
		    long total=0;
			//获取某个topic的消费者客户端
		    //对每个消息，做特殊处理
		    KafkaConsumer<String, String> consumer = ConsumerFactory.getConsumer(groupId, topics, false,"test").getOrginalConsumer();
		 
		    boolean hasMore =false;
		    while (true) {
		         ConsumerRecords<String, String> records = consumer.poll(100);
		         for (ConsumerRecord<String, String> record : records){
		        	 total++;
		             System.out.printf("offset = %d, key = %s, value = %s \n", record.offset(), record.key(), record.value());
		         }
		         if (records!=null && records.count()>0)
		        	 	System.out.println("total records now:"+total);
		    }
		
	}
	
	@Test
	public void TestVitalConsumer(){
		    String[] topics ={ "vitalMsg2"};
		    String  groupId="vitalGroup";
		    String clientId="消费者1";
		    long total=0;
			//获取某个topic的消费者客户端
		    //对每个消息，做特殊处理
		    KafkaConsumer<String, String> consumer = ConsumerFactory.getConsumer(groupId, topics, true,"test").getOrginalConsumer();
		    
		    boolean hasMore =false;
		    boolean runFlag =true;
		    while (runFlag) {
		         ConsumerRecords<String, String> records = consumer.poll(1);
		         for (ConsumerRecord<String, String> record : records){
		        	 total++;
		             execute(record);    
		         }
		         if (records!=null && records.count()>0){
		        	// 	commitOffset(consumer,record);
		        	 	System.out.println("total records now:"+total);
		        	    consumer.commitSync();
//		        	    if (isBreak()){
//			            	  runFlag =false;
//			            	  break;
//			             }
		         }
		      
	          
		    }
		
	}
	
	private void execute(ConsumerRecord<String, String> record){
		System.out.printf("offset = %d, key = %s, value = %s \n", record.offset(), record.key(), record.value());
	}
	
	private void commitOffset(KafkaConsumer<String, String> consumer,ConsumerRecord<String, String> record){
		java.util.Map<TopicPartition,OffsetAndMetadata> offsets = new HashMap<TopicPartition,OffsetAndMetadata>();
		TopicPartition topicPartition = new TopicPartition(record.topic(),record.partition());
		OffsetAndMetadata offsetAndMetadata = new OffsetAndMetadata(record.offset());
		offsets.put(topicPartition, offsetAndMetadata);
		consumer.commitSync(offsets);
	}

	
	private boolean isBreak(){
		 return (new Random().nextInt(10)%5)==0? true:false;
	}
}

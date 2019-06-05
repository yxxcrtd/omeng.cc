package com.shanjin.kafka;

import com.shanjin.kafka.impl.NormalProducer;
import com.shanjin.kafka.impl.VitalProducer;


/**
 * 消息生产者工厂类
 * @author Revoke   2015.12.4
 *
 */
public class ProducerFactory {

	/**
	 * 
	 * @param flag    true 重要消息  false  常规消息
	 * @param client  生产者标识
	 * @return
	 */
	public static ProducerInterface getProducer(boolean flag,String client){
			if (flag){
				return VitalProducer.getInstance(client);
			}else{
				return NormalProducer.getInstance(client);
			}
	}
}

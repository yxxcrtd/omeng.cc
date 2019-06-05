package com.shanjin.kafka.impl;

import com.shanjin.kafka.Partition;

/**
 * 取模分区，key 必须为数字型字符串
 * @author Revoke 2015.12.4
 *
 */
public class ModulePartition implements Partition {
	private long module;
	
	public ModulePartition(int module){
		this.module = module;
	}
	
	@Override
	public int getPartition(String topic, String key, String content) {
		Long keyNumer = Long.parseLong(key);
		return (int) (keyNumer%module);
	}

}

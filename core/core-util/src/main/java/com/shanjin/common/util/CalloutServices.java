package com.shanjin.common.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 外部调用服务类， 用于
 * @author Revoke
 *
 */
public class CalloutServices {
	 
	private static  int  POOL_SIZE = 50;
	 
	private static  ExecutorService  pool =  Executors.newFixedThreadPool(POOL_SIZE);
	 
	 
	public static void executor(Runnable task) {
		  pool.execute(task);
	}
	
}

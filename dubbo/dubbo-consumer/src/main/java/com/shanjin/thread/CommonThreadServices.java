package com.shanjin.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 公共线程服务类
 * 
 * @author Revoke
 *
 */
public class CommonThreadServices {

	private static int POOL_SIZE = 50;

	private static ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE);

	public static void executor(Runnable task) {
		pool.execute(task);
	}

}

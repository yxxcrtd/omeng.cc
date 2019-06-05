package com.shanjin;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.shanjin.service.PurifyMigratedService;

/**
 * 清理已迁移的历史订单及缓存
 * 
 * @author Revoke Yu
 *
 */
public class PurifyMigrated {

	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(PurifyMigrated.class);

	private final static Integer CONCURRENT_NUM = 1; // 并发线程数据

	public static void main(String[] args) throws Exception {
		long beginTime = System.currentTimeMillis();

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "classpath*:spring/*.xml" });

		context.start();

		PurifyMigratedService purifyService = (PurifyMigratedService) context
				.getBean("purifyService");

		PurifyMigrated _inst = new PurifyMigrated();

		CountDownLatch doneSignal = new CountDownLatch(CONCURRENT_NUM);

		new Thread(_inst.new PurifyCancelOrder(doneSignal, purifyService))
				.start();

		doneSignal.await();

		long endTime = System.currentTimeMillis();

		System.out.println("total cost:" + (endTime - beginTime) / 1000);
		System.out.println("本批清理任务结束");
	}

	class PurifyCancelOrder implements Runnable {
		private final PurifyMigratedService service;
		private final CountDownLatch doneSignal;

		PurifyCancelOrder(CountDownLatch doneSignal, PurifyMigratedService svr) {
			this.doneSignal = doneSignal;
			this.service = svr;
		}

		public void run() {
			try {
					Map<String,Object> lastMigrateRec=service.getLastMigrateRecord();
					while(lastMigrateRec!=null && lastMigrateRec.size()>0) {
						List<Map<String,Object>> orders = service.purifyCache(lastMigrateRec);
						if (orders==null || orders.size()<1){
							 System.out.println("发现异常情况，迁移记录未找到订单:"+lastMigrateRec.get("id"));
							 break;
						}
						service.removeMigratedRec(orders, (Long) lastMigrateRec.get("id"));
						
						mySleep(2);
						lastMigrateRec=service.getLastMigrateRecord();
					}
					
			} catch (Exception ex) {
				ex.printStackTrace(
				);
			} finally {
				doneSignal.countDown();
			}
		}
	}
	
	
	private void mySleep(long sec){
   	 try {
			Thread.sleep(sec*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
}

}

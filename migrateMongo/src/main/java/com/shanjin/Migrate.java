package com.shanjin;

import java.util.concurrent.CountDownLatch;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.shanjin.service.CustomOrderMigrateService;

public class Migrate {
	private final static Integer CONCURRENT_NUM=4; //并发线程数据
	
	public static void main(String[] args) throws Exception{
		long beginTime = System.currentTimeMillis();
		
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "classpath*:spring/*.xml" });

		context.start();
		
		CustomOrderMigrateService  cOrderMigrateSvr = (CustomOrderMigrateService) context.getBean("coMigrateSvr");
		
	
		Migrate _inst = new Migrate();
		
		CountDownLatch doneSignal = new CountDownLatch(CONCURRENT_NUM);

		new Thread(_inst.new ProcessCancelOrder(doneSignal,cOrderMigrateSvr)).start();
		new Thread(_inst.new ProcessNoBidOrder(doneSignal,cOrderMigrateSvr)).start();
		new Thread(_inst.new ProcessNoChoosedOrder(doneSignal,cOrderMigrateSvr)).start();
		new Thread(_inst.new ProcessNormalOrder(doneSignal,cOrderMigrateSvr)).start();

		doneSignal.await();
		
		long endTime = System.currentTimeMillis();
		
		System.out.println("total cost:"+(endTime-beginTime)/1000);
		System.out.println("本批迁移结束");
	}

	
	
	class ProcessCancelOrder implements Runnable {
		   private final CustomOrderMigrateService service;
		   private final CountDownLatch doneSignal;
		   ProcessCancelOrder(CountDownLatch doneSignal,CustomOrderMigrateService svr) {
		      this.doneSignal = doneSignal;
		      this.service = svr;
		   }
		   public void run() {
		      try {
		        doWork();
		        doneSignal.countDown();
		      } catch (Exception ex) {ex.printStackTrace(
		    		  
		    		  );} 
		   }

		   void doWork() { this.service.migrateCancelOrder(); }
	}
	
	
	class ProcessNoBidOrder implements Runnable {
		   private final CountDownLatch doneSignal;
		   private final CustomOrderMigrateService service;
		   ProcessNoBidOrder(CountDownLatch doneSignal,CustomOrderMigrateService svr) {
		      this.doneSignal = doneSignal;
		      this.service = svr;
		   }
		   public void run() {
		      try {
		        doWork();
		      } catch(Exception ex) { ex.printStackTrace();}
		      finally{
		    	doneSignal.countDown(); 
		      }
		   }

		   void doWork() { this.service.migrateNoBidOrder(); }
	}
	
	
	
	class ProcessNoChoosedOrder implements Runnable {
		   private final CountDownLatch doneSignal;
		   private final CustomOrderMigrateService service;
		   ProcessNoChoosedOrder(CountDownLatch doneSignal,CustomOrderMigrateService svr) {
		      this.doneSignal = doneSignal;
		      this.service = svr;
		   }
		   public void run() {
		      try {
		        doWork();
		      } catch (Exception ex) { ex.printStackTrace();}finally{
		    	  doneSignal.countDown();  
		      }
		      
		   }

		   void doWork() { this.service.migrateNoChoosedOrder(); }
	}
	
	
	class ProcessNormalOrder implements Runnable {
		   private final CountDownLatch doneSignal;
		   private final CustomOrderMigrateService service;
		   ProcessNormalOrder(CountDownLatch doneSignal,CustomOrderMigrateService svr) {
		      this.doneSignal = doneSignal;
		      this.service = svr;
		   }
		   public void run() {
		      try {
		        doWork();
		      } catch (Exception ex) {
		    	  ex.printStackTrace();
		      }
		      finally{
		    	 doneSignal.countDown();
		      }
		   }

		   void doWork() { this.service.migrateNormalOrder(); }
	}

	
}

package com.shanjin.log.analysis.job;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;




import com.shanjin.common.util.DateUtil;
import com.shanjin.log.analysis.service.VisitLogHandleService;
import com.shanjin.log.analysis.service.VisitLogImportService;
import com.shanjin.log.analysis.service.impl.VisitLogHandleServiceImpl;
import com.shanjin.log.analysis.service.impl.VisitLogImportServiceImpl;

/**
 * 访问日志导入DB
 * @author Huang yulai
 *
 */
public class VisitLogImportJob {
	private VisitLogImportService visitLogImportService=new VisitLogImportServiceImpl();	
	private VisitLogHandleService visitLogHandleService=new VisitLogHandleServiceImpl();
	
	private final Log log = LogFactory.getLog(this.getClass());
	
	protected void work(){
//		test();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, -1);
		Date yesterday = calendar.getTime();
		
		SimpleDateFormat time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	    log.info("访问日志导入job start ! 开始处理时间："+time.format(new Date())+"  LOG DATE: " + DateUtil.formatDate(DateUtil.DATE_YYYY_MM_DD_PATTERN, yesterday));
		try{
			visitLogImportService.importVisitLog(yesterday);
			
			visitLogHandleService.insertTerminalStatistics(yesterday);
			
			visitLogHandleService.insertFirstVisitTerminal(yesterday);
			
		 }catch(Exception e){
			 e.printStackTrace();
		 }

	     log.info("访问日志导入job end ! 导入结束时间："+time.format(new Date())+" LOG DATE: " + DateUtil.formatDate(DateUtil.DATE_YYYY_MM_DD_PATTERN, yesterday));
	}
	
	public void test(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, -3);
		for(int i=2;i>=1;i--){
			calendar.add(Calendar.DATE, 1);
			Date yesterday = calendar.getTime();
			SimpleDateFormat time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		    log.info("访问日志导入job start ! 开始处理时间："+time.format(new Date())+"  LOG DATE: " + DateUtil.formatDate(DateUtil.DATE_YYYY_MM_DD_PATTERN, yesterday));
			try{
				visitLogImportService.importVisitLog(yesterday);
				
				visitLogHandleService.insertTerminalStatistics(yesterday);
				
				visitLogHandleService.insertFirstVisitTerminal(yesterday);
				
			 }catch(Exception e){
				 e.printStackTrace();
			 }

		     log.info("访问日志导入job end ! 导入结束时间："+time.format(new Date())+" LOG DATE: " + DateUtil.formatDate(DateUtil.DATE_YYYY_MM_DD_PATTERN, yesterday));
		}
	}
	

}

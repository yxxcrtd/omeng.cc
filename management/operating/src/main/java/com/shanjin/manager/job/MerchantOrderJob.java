package com.shanjin.manager.job;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shanjin.manager.Bean.MerchantsInfo;
import com.shanjin.manager.constant.Constant;
import com.shanjin.manager.service.ExportService;
import com.shanjin.manager.service.IMerchantsInfoService;
import com.shanjin.manager.service.IServiceStatisticService;
import com.shanjin.manager.service.ExcelExportUtil.Pair;
import com.shanjin.manager.service.impl.MerchantsInfoServiceImpl;
import com.shanjin.manager.service.impl.ServiceStatisticServiceImpl;
import com.shanjin.manager.time.StatisticalUtil;
import com.shanjin.manager.utils.Util;



/**
 * 后台业务数据处理job
 * 
 * @author 
 * 
 */
public class MerchantOrderJob {
	
	private static IServiceStatisticService serviceStatistic=new ServiceStatisticServiceImpl();
	private static ExportService service = ExportService.service;
	protected void work(){
		System.out.println("-----------success-----------");
		exportMerchantOrderJob();
	}

	private void exportMerchantOrderJob() {
		Map<String, Object> paramMap=new HashMap<String, Object>();
		paramMap.put("time", Util.getLastDayByDay());
		List<MerchantsInfo> list = serviceStatistic.exportMerchantOrder(paramMap); // 查询数据
		List<Pair> titles = serviceStatistic.getMerchantOrderTitles();
		String time=getCurrentTime();
		String fileName="";
		fileName="订单_"+time;
		
		// 导出
		if(list.size()>60000){
			List<MerchantsInfo> list1=list.subList(0, 60000);
			List<MerchantsInfo> list2=list.subList(60001, list.size()-1);
			service.exportMonth(null, null, titles,fileName,Constant.EXPOTR_TYPE,list1,list2);
			list.clear(); 
			list1.clear();
			list2.clear();
			System.gc() ;
		}else{
			service.exportMonth(null, null, titles,fileName,Constant.EXPOTR_TYPE,list);
			list.clear(); 
			System.gc();
	}
		
	}
	public static String getCurrentTime(){
		Date date = new Date();
	    Calendar calendar = new GregorianCalendar();
	    calendar.setTime(date);
	    calendar.add(5, -1);
	    date = calendar.getTime();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    String dateString = formatter.format(date);
	    return dateString.replace("-", "_");
}
}


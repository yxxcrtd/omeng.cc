package com.shanjin.manager.time;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.MerchantsInfo;
import com.shanjin.manager.constant.Constant;
import com.shanjin.manager.service.ExportService;
import com.shanjin.manager.service.IMerchantsInfoService;
import com.shanjin.manager.service.ExcelExportUtil.Pair;
import com.shanjin.manager.service.impl.MerchantsInfoServiceImpl;

public class StatisticalUtil {

	private static IMerchantsInfoService merchantsInfoService = new MerchantsInfoServiceImpl();
	private static ExportService service = ExportService.service;
	public static void merchantStatis(){
		List<Record> proAndCity=getAllProvince();
		Map<String, Object> paramMap = null;
		if(proAndCity!=null&&proAndCity.size()>0){
			for(Record re:proAndCity){
				paramMap=new HashMap<String, Object>();
				paramMap.put("province", re.getStr("area"));
				paramMap.put("city", null);
				exportTimeStoreList(paramMap);
				System.out.println(re.getStr("area"));
			}	
		}
		
		List<Record> city=getAllCity();
		if(city!=null&&city.size()>0){
			for(Record re:city){
				paramMap=new HashMap<String, Object>();
				paramMap.put("province", re.getStr("province"));
				paramMap.put("city", re.getStr("city"));
				exportTimeStoreList(paramMap);
				System.out.println(re.getStr("province")+"_"+re.getStr("city"));
			}	
		}
	}
	
	public static void exportTimeStoreList(Map<String, Object> paramMap){
		List<MerchantsInfo> list = merchantsInfoService.exportTimeStoreList(paramMap); // 查询数据
		List<Pair> titles = merchantsInfoService.getTimeExportTitles();
		String time=getCurrentTime();
		String fileName="";
		if(paramMap.get("city")!=null){
			fileName="商户_"+paramMap.get("province")+"_"+paramMap.get("city")+"_"+time;
		}else{
			fileName="商户_"+paramMap.get("province")+"_"+time;
		}
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
			System.gc() ;
	}
	}
	
	public static String getCurrentTime(){
		    Calendar calendar = Calendar.getInstance();
	        int year = calendar.get(Calendar.YEAR);
	        int month = calendar.get(Calendar.MONTH);
	        String time=year+"_"+month;
	        return time;
	}
	
	private static List<Record> getAllProvince() {
		String sql="select area from area where parent_id is null";
		//List<Record> proAndCityRe=Db.use("ana_main").find(sql);
		List<Record> proAndCityRe=Db.find(sql);
		return proAndCityRe;
	}
	
	private static List<Record> getAllCity() {
		String sql="select a.area as city,(select ae.area from area ae where ae.id=a.parent_id) as province from area a where parent_id is not null";
		//List<Record> proAndCityRe=Db.use("ana_main").find(sql);
		List<Record> proAndCityRe=Db.find(sql);
		return proAndCityRe;
	}
	
}

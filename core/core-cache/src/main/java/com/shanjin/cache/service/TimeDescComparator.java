package com.shanjin.cache.service;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

import com.shanjin.common.util.DateUtil;
import com.shanjin.common.util.StringUtil;


public class TimeDescComparator implements Comparator<Map<String,Object>> {
//	private String joinTimeStr;
	private String paramStr;
	
	public TimeDescComparator(String paramStr){
		 this.paramStr = paramStr;
//		 this.isPastDateStr = isPastDate;
	}

	@Override
	public int compare(Map<String, Object> o1, Map<String, Object> o2) {
//		int isPastDate1 = Integer.parseInt(o1.get(isPastDateStr)==null?"0":o1.get(isPastDateStr).toString());
//		int isPastDate2 = Integer.parseInt(o2.get(isPastDateStr)==null?"0":o2.get(isPastDateStr).toString());
//		if (isPastDate1!=isPastDate2){		
//			return isPastDate1-isPastDate2;
//		}else {
//			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			long obj1=0;
//			long obj2=0;
//			try{
//				Date date=sdf.parse(StringUtil.null2Str(o1.get(joinTimeStr)));
//				obj1=date.getTime();
//
//				date=sdf.parse(StringUtil.null2Str(o2.get(joinTimeStr)));
//				obj2=date.getTime();
//				
//				System.out.println(StringUtil.null2Str(o1.get(joinTimeStr))+","+obj1);
//			}catch(Exception e){
//				System.out.println(e.getMessage());
//			}
			long v1=StringUtil.nullToLong(o1.get(paramStr));
			long v2=StringUtil.nullToLong(o2.get(paramStr));
			return  (int)(v2-v1);
//		}
	}
}

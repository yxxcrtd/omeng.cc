package com.shanjin.service.impl;

import java.util.Comparator;
import java.util.Map;


public class DistansComparator implements Comparator<Map<String,Object>> {
	private String distance;
	
	public DistansComparator(String distance){
		 this.distance = distance;
	}

	@Override
	public int compare(Map<String, Object> o1, Map<String, Object> o2) {
//		int isPastDate1 = Integer.parseInt(o1.get(isPastDateStr)==null?"0":o1.get(isPastDateStr).toString());
//		int isPastDate2 = Integer.parseInt(o2.get(isPastDateStr)==null?"0":o2.get(isPastDateStr).toString());
//		if (isPastDate1!=isPastDate2){		
//			return isPastDate1-isPastDate2;
//		}else {
			long obj1=Long.parseLong(o1.get(distance)==null?"0":o1.get(distance).toString());
			long obj2=Long.parseLong(o2.get(distance)==null?"0":o2.get(distance).toString());
			return  (int)(obj1-obj2);
//		}
	}
}

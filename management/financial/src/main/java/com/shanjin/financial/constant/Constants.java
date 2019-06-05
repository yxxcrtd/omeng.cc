package com.shanjin.financial.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Constants {
	
	public static final String ADMIN = "omengadmin";

	public static String ali_bill_file_path = "C:\\Users\\Administrator\\Documents";
	
	public static String ali_day_bill_name = "_业务明细(汇总).csv";
	
	public static String ali_detail_bill_name = "_业务明细.csv";
	
	public static final int EXPOTR_TYPE=1;
	
	
	public static Map<String,Object> sysResource = new HashMap<String,Object>(); //所有资源
	
	public static List<String> commonResourcePathList = new ArrayList<String>(); //公共资源列表，无权限约束
}

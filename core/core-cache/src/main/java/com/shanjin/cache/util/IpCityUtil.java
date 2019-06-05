package com.shanjin.cache.util;


import java.util.Map.Entry;
import java.util.TreeMap;

import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.impl.CommonCacheServiceImpl;


public class IpCityUtil {
	private static ICommonCacheService commonCacheService = new CommonCacheServiceImpl();
	private static TreeMap<Long, String[]> ipTreeMap = new TreeMap<Long, String[]>();
	
	/**
	 * 根据ip解析省市，以“|”隔开
	 * @param strIp
	 * @return
	 */
//	public static String getCity(String strIp) {
//		String s = "";
//		if(strIp.contains("192.168")){
//			return s;
//		}
//		long start = System.currentTimeMillis();
//		Long ip = convertIpToLong(strIp);
//		Entry<Long, String[]> entry = ((TreeMap<Long, String[]>) commonCacheService.getObject(CacheConstants.IP_CITY_TREE_MAP)).floorEntry(ip);
//		if(entry!=null){
//			String[] value = entry.getValue();
//			if (Long.valueOf(value[0]).compareTo(ip) >= 0) {
//				s = value[1]+"|"+value[2];
//			}
//		}
//		long end = System.currentTimeMillis();
//		System.out.println("城市名s="+s+"  "+"start="+start+"  end="+end+"  耗时="+(end-start));
//		return s;
//	}
	
	/**
	 * 根据ip解析省市，以“|”隔开
	 * @param strIp
	 * @return
	 */
	public static String getCity(String strIp) {
		String s = "|";
		if (strIp.contains("192.168")) {
			return s;
		}
		if(strIp.contains(",")){
			strIp = strIp.split(",")[0];
		}
		Long ip = convertIpToLong(strIp);
		if (ip.intValue() == 0)
			return s;
		try {
			if (ipTreeMap == null || ipTreeMap.isEmpty()) {
				System.out.println("ipTreeMap为空！");
				ipTreeMap = (TreeMap<Long, String[]>) commonCacheService.getObject(CacheConstants.IP_CITY_TREE_MAP);
			}
			if(ipTreeMap == null || ipTreeMap.isEmpty()){
				return s;
			}
			Entry<Long, String[]> entry = ipTreeMap.floorEntry(ip);
			if (entry != null) {
				String[] value = entry.getValue();
				if (Long.valueOf(value[0]).compareTo(ip) >= 0) {
					s = value[1] + "|" + value[2];
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return s;
		}
		return s;
	}
	
//	/**
//	 * 根据ip解析省市，以“|”隔开
//	 * @param strIp
//	 * @return
//	 */
//	public static String getCityByHash(String strIp) {
//		String s = "";
//		if(strIp.contains("192.168")){
//			return s;
//		}
//		Long ip = convertIpToLong(strIp);
//		Entry<Long, String[]> entry = ((Map<Long, String[]>) commonCacheService.getObject("ip_city_tree_map")).floorEntry(ip);
//		if(entry!=null){
//			String[] value = entry.getValue();
//			if (Long.valueOf(value[0]).compareTo(ip) >= 0) {
//				s = value[1]+"|"+value[2];
//			}
//		}
//		return s;
//	}

	public static long convertIpToLong(String ip) {
		long intIp = 0;
		try{
			if(ip.contains("192.168")){
				return intIp;
			}
			String[] checkIp = ip.split("\\.", 4);
			for (int i = 3, j = 0; i >= 0 && j <= 3; i--, j++) {
				intIp += Integer.parseInt(checkIp[j]) * Math.pow(256, i);
			}
		}catch(Exception e){
			e.printStackTrace();
			return intIp;
		}

		return intIp;
	}

	public static void main(String[] args) {
		IpCityUtil iputil = new IpCityUtil();
	//	iputil.load();
		long ip = 3;
		String[] arrs = new String[]{"1,3657861866,四川,内江市","3,3683247265,广东,广州市","5,3702754313,安徽,滁州市"};
//		for(String s:arrs){
//			String str[] = s.split(",", 3);
//			if (str.length == 3) {
//				ipTreeMap.put(Long.valueOf(str[0].trim()), new String[] { str[1].trim(), str[2].trim() });
//			}
//		}
	    long ipInt = IpCityUtil.convertIpToLong("221.181.100.37");
		System.out.println(ipInt);
//		System.out.println(getCity("221.181.100.37"));
//		System.out.println(getCity("124.35.23.21"));
//		System.out.println(getCity("114.112.86.188"));

	}
}

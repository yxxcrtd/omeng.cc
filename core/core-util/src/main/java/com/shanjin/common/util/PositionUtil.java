package com.shanjin.common.util;

import java.util.HashMap;
import java.util.Map;

public class PositionUtil {
	
	public static final String TOP_LATITUDE = "topLatitude";
	public static final String BUTTOM_LATITUDE = "buttomLatitude";
	public static final String LEFT_LONGITUDE = "leftLongitude";
	public static final String RIGHT_LONGITUDE = "rightLongitude";

	public static Map<String, Double> calcTopLfRgBtCoordinate(double range, double longitude, double latitude) {
		Map<String, Double> tlrbCoordinate = new HashMap<String, Double>();
		//step1:求同纬度地球半径
		double R = 6371.393 * Math.cos(latitude); // 公式   地球半径  * cos(latitude);
		
		//step2:转换纬度为弧度制
		latitude = latitude * Math.PI / 180.0;
		
		//step3: 计算上下纬度值
		double topLatitude = latitude + (range / 6371.393); //公式  (latitude - x) * 6371.393 = range, 求 x值
		double buttomLatitude = latitude - (range / 6371.393); //公式  (x - latitude) * 6371.393 = range, 求 x值
		
		//step4:转换经度为弧度制
		longitude = longitude * Math.PI / 180.0;
		
		//step5: 计算左右经度值
		double leftLongitude = longitude - Math.abs(range / R); //公式  (latitude - x) * 6371.393 = range, 求 x值
		double rightLongitude = longitude + (range / 6371.393); //公式  (x - latitude) * 6371.393 = range, 求 x值
		
		//step6: 转换为角度制
		topLatitude = (topLatitude * 180.0) / Math.PI;
		buttomLatitude = (buttomLatitude * 180.0) / Math.PI;
		leftLongitude = (leftLongitude * 180.0) / Math.PI;
		rightLongitude = (rightLongitude * 180.0) / Math.PI;
		
		if (topLatitude>buttomLatitude){
			tlrbCoordinate.put(TOP_LATITUDE, topLatitude);
			tlrbCoordinate.put(BUTTOM_LATITUDE, buttomLatitude);
		}else {
			tlrbCoordinate.put(TOP_LATITUDE, buttomLatitude);
			tlrbCoordinate.put(BUTTOM_LATITUDE, topLatitude);
			
		}
		
		if (rightLongitude>leftLongitude){
				tlrbCoordinate.put(LEFT_LONGITUDE, leftLongitude);
				tlrbCoordinate.put(RIGHT_LONGITUDE, rightLongitude);
		}else{
			
			tlrbCoordinate.put(LEFT_LONGITUDE, rightLongitude);
			tlrbCoordinate.put(RIGHT_LONGITUDE, leftLongitude);
		}
		
		return tlrbCoordinate;
	}
	
	public static void main(String[] str){
		
		Map<String, Double> locationInfo = calcTopLfRgBtCoordinate(5, 117.6669726295, 39.0492784275);
		System.out.println("leftLongitude:"+locationInfo.get("leftLongitude"));
		System.out.println("rightLongitude:"+locationInfo.get("rightLongitude"));
		System.out.println("buttomLatitude:"+locationInfo.get("buttomLatitude"));
		System.out.println("topLatitude:"+locationInfo.get("topLatitude"));
	}
}

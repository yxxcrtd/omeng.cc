package com.shanjin.push;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.shanjin.common.util.StringUtil;
import com.shanjin.push.android.GetuiPushSend;
import com.shanjin.push.android.HuaweiPushSend;
import com.shanjin.push.android.XiaomiPushSend;

public class AndroidPushSend {
	
	public static void send(Map<String, Object> configMap,Map<String, Object> msg,String concatKey,List<Map<String, Object>> androidPushInfoList){
		List<Map<String, Object>> getuiList=new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> huaweiList=new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> xiaomiList=new ArrayList<Map<String,Object>>();
		for(Map<String, Object> map : androidPushInfoList){
			String phoneModel=StringUtil.null2Str(map.get("phoneModel"));
			if(phoneModel.toLowerCase().startsWith("huawei_")){
				huaweiList.add(map);
			}else if(phoneModel.toLowerCase().startsWith("xiaomi_")){
				xiaomiList.add(map);			
			}else{
				getuiList.add(map);	
			}
		}
		if(getuiList.size()>0){
			GetuiPushSend.send(configMap,msg,concatKey,getuiList);
		}
		if(huaweiList.size()>0){
			System.out.println("华为的手机有："+huaweiList.toString());
			HuaweiPushSend.send(configMap,msg,concatKey,huaweiList);
		}
		if(xiaomiList.size()>0){
			System.out.println("小米的手机有："+xiaomiList.toString());	
			XiaomiPushSend.send(configMap,msg,concatKey,xiaomiList);
		}
	}
	
}

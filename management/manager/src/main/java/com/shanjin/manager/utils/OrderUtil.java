package com.shanjin.manager.utils;

import com.shanjin.manager.constant.Constant;
import com.shanjin.manager.constant.Constant.CBT;
import com.shanjin.manager.constant.Constant.FYB;
import com.shanjin.manager.constant.Constant.LXZ;
import com.shanjin.manager.constant.Constant.MST;
import com.shanjin.manager.constant.Constant.ORDER;
import com.shanjin.manager.constant.Constant.SXD;
import com.shanjin.manager.constant.Constant.XLB;
import com.shanjin.manager.constant.Constant.YD;
import com.shanjin.manager.constant.Constant.YXT;
import com.shanjin.manager.constant.Constant.ZSY;
import com.shanjin.manager.constant.Constant.ZYB;

public class OrderUtil {
public static String getTableByOrder(String appType,
		String serviceType){
	String subAppType="";
	if(appType.contains("_")){
	subAppType=appType.substring(0,appType.indexOf('_'));
	}else{
	subAppType=	appType;
	}
	if(subAppType.equals(Constant.APPTYPE_LXZ)){
		return LXZ.lxz_table;
	}
	else if(subAppType.equals(Constant.APPTYPE_CBT)){
		int service=Integer.parseInt(serviceType);
		switch(service){
		case 1: 
		return CBT.cbt_table_wash;
		case 2: 
			return CBT.cbt_table_repair;
		case 3: 
			return CBT.cbt_table_upkeep;
		case 4: 
			return CBT.cbt_table_beautify;
		case 5:
			return CBT.cbt_table_park;
		case 6:
			return CBT.cbt_rental;
		case 7:
			return CBT.cbt_agency_service;
		case 8:
			return CBT.cbt_drivingSchool;
		}		
	}
	else if(subAppType.equals(Constant.APPTYPE_SXD)){
		return SXD.sxd_table;
	}
	else if(subAppType.equals(Constant.APPTYPE_YXT)){
		return YXT.yxt_table;
	}
	else if(subAppType.equals(Constant.APPTYPE_ZYB)){
		int service=Integer.parseInt(serviceType);
		if(appType.equals(ZYB.zyb_acw)){
			 return ZYB.zyb_table_pet;
		}else if(appType.equals(ZYB.zyb_smx)){
			 return ZYB.zyb_table_repair;
		}else if(appType.equals(ZYB.zyb_ysh)){
			 return ZYB.zyb_table_liveService;
		}
		switch(service){
		case 1: 
		    return ZYB.zyb_table_remove;
		case 2: 
			return ZYB.zyb_table_clean;
		case 3: 
			return ZYB.zyb_table_nanny;
		case 5: 
			return ZYB.zyb_table_wash;
		case 18: 
			return ZYB.zyb_table_recycle;
		}
		
	}else if(subAppType.equals(Constant.APPTYPE_YD)){
		int service=Integer.parseInt(serviceType);
		switch(service){
		case 1: 
		    return YD.YD_BAR_ORDER_INFO;
		case 2: 
			return YD.YD_CLUB_ORDER_INFO;
		case 3: 
			return YD.YD_KTV_ORDER_INFO;
		case 4: 
			return YD.YD_SPA_ORDER_INFO;
		case 5: 
			return YD.YD_BATH_SAUNA_ORDER_INFO;
		case 6: 
			return YD.YD_FOOT_MASSAGE_ORDER_INFO;
		case 7: 
			return YD.YD_EVEING_SHOW_ORDER_INFO;
		case 8: 
			return YD.YD_KHAN_ORDER_INFO;
		case 9: 
			return YD.YD_CHEIR_ORDER_INFO;
		}
		
	}else if(subAppType.equals(Constant.APPTYPE_XLB)){
		int service=Integer.parseInt(serviceType);
		switch(service){
		case 1: 
		    return XLB.XLB_CELE_MODEL;
		case 2: 
			return XLB.XLB_WEDDING_CAR;
		case 3: 
			return XLB.XLB_CELE_MODEL;
		case 4: 
			return XLB.XLB_WEDDING_ORDER;
		case 5: 
			return XLB.XLB_DRESS_ORDER;
		case 6: 
			return XLB.XLB_FESST_ORDER;	
		
		}
		
	}
	else if(subAppType.equals(Constant.APPTYPE_FYB)){
		int service=Integer.parseInt(serviceType);
		switch(service){
		case 1: 
		    return FYB.fyb_table_buyHouse;
		case 2: 
			return FYB.fyb_table_rentHouse;
		}
		
	}else if(subAppType.equals(Constant.APPTYPE_ZSY)){
		int service=Integer.parseInt(serviceType);
		switch(service){
		case 1: 
		    return ZSY.ZSY_table_LITIGATION;
		case 2: 
			return ZSY.ZSY_table_NOLITIGATIO;
		}
		
	}else if(subAppType.equals(Constant.APPTYPE_MST)){
		int service=Integer.parseInt(serviceType);
		switch(service){
		case 2: 
		    return MST.mst_table_massage;
		case 3: 
			return MST.mst_table_physio;
		case 4: 
			return MST.mst_table_medicated;
		}
		
	}else{
		
		 return subAppType+ORDER.ORDER_table;
	}

	return null;
}
}

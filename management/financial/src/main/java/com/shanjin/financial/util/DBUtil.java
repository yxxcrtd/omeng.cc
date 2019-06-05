package com.shanjin.financial.util;
import java.util.Properties;



import com.jfinal.config.Plugins;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.shanjin.financial.bean.*;
import com.shanjin.sso.bean.SystemUserInfo;


public class DBUtil{

	public static void configPlugins(Plugins me) {
		Properties prop=PropUtil.getPropUtil("database.properties");
		C3p0Plugin c3p0Plugin = new C3p0Plugin(prop);
		me.add(c3p0Plugin);
		ActiveRecordPlugin arp = new ActiveRecordPlugin("main",c3p0Plugin);
		arp.setShowSql(false);
		me.add(arp);

		arp.addMapping("merchant_apply_withdraw_record", MerchantApplyWithdrawRecord.class);
		arp.addMapping("order_info", OrderInfo.class);
		arp.addMapping("activity_fensi_payment_detail", ActivityFensiPaymentDetail.class);
		arp.addMapping("activity_cutting", ActivityCutting.class);
		arp.addMapping("activity_cutting_detail", ActivityCuttingDetail.class);
		arp.addMapping("activity_order_reward_detail", ActivityOrderRewardDetail.class);
		arp.addMapping("merchant_employees_num_apply", MerchantEmployeesNumApply.class);
		arp.addMapping("authority_user_info", SystemUserInfo.class);
	}

	public static void configPlugins_Opay(Plugins me) {
		Properties prop=PropUtil.getPropUtil("database_opay.properties");
		C3p0Plugin c3p0Plugin = new C3p0Plugin(prop);
		me.add(c3p0Plugin);
		ActiveRecordPlugin arp = new ActiveRecordPlugin("opay",c3p0Plugin);
		arp.setShowSql(false);
		me.add(arp);
	}
}

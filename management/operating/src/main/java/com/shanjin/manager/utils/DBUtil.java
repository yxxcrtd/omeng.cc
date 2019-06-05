package com.shanjin.manager.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;


import com.jfinal.config.Plugins;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
//import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.shanjin.manager.Bean.Agent;
import com.shanjin.manager.Bean.AgentCharge;
import com.shanjin.manager.Bean.AgentEmployee;
import com.shanjin.manager.Bean.AppKey;
import com.shanjin.manager.Bean.AppUpdate;
import com.shanjin.manager.Bean.Area;
import com.shanjin.manager.Bean.BlackUser;
import com.shanjin.manager.Bean.Catalog;
import com.shanjin.manager.Bean.CustomKeyWords;
import com.shanjin.manager.Bean.FensiAddRanking;
import com.shanjin.manager.Bean.FensiAddTotal;
import com.shanjin.manager.Bean.GroupRole;
import com.shanjin.manager.Bean.MerchantAdviserApply;
import com.shanjin.manager.Bean.MerchantPushApply;
import com.shanjin.manager.Bean.MerchantServiceTag;
import com.shanjin.manager.Bean.MerchantVipApply;
import com.shanjin.manager.Bean.MerchantsInfo;
import com.shanjin.manager.Bean.OrderInfo;
import com.shanjin.manager.Bean.OrderRewardAccount;
import com.shanjin.manager.Bean.Recommend;
import com.shanjin.manager.Bean.RoleResource;
import com.shanjin.manager.Bean.SearchStatistic;
import com.shanjin.manager.Bean.SearchStatisticAttch;
import com.shanjin.manager.Bean.SearchWord;
import com.shanjin.manager.Bean.ServiceWord;
import com.shanjin.manager.Bean.Slider;
import com.shanjin.manager.Bean.StopKeyWords;
import com.shanjin.manager.Bean.SystemGroup;
import com.shanjin.manager.Bean.SystemResource;
import com.shanjin.manager.Bean.SystemOperateLog;
import com.shanjin.manager.Bean.SystemUserInfo;
import com.shanjin.manager.Bean.SystemRole;
import com.shanjin.manager.Bean.UserChannel;
import com.shanjin.manager.Bean.UserFeedback;
import com.shanjin.manager.Bean.UserGroup;
import com.shanjin.manager.Bean.AppInfo;
import com.shanjin.manager.Bean.UserApp;
import com.shanjin.manager.Bean.UserInfo;
import com.shanjin.manager.Bean.UserRole;
import com.shanjin.manager.Bean.UserStartUp;
import com.shanjin.manager.Bean.UserVisit;
import com.shanjin.manager.Bean.UserWord;
import com.shanjin.manager.Bean.Voucher;
import com.shanjin.manager.Bean.WithDraw;
import com.shanjin.manager.Bean.MerchantsEmployees;
import com.shanjin.manager.Bean.Loading;
import com.shanjin.manager.Bean.MerPhotoStatic;
import com.shanjin.manager.Bean.Message;
import com.shanjin.manager.constant.Constant;



public class DBUtil{

	public static void configPlugins(Plugins me) {
		Properties prop=PropUtil.getPropUtil("database.properties");
		C3p0Plugin c3p0Plugin = new C3p0Plugin(prop);
		me.add(c3p0Plugin);
		ActiveRecordPlugin arp = new ActiveRecordPlugin("main",c3p0Plugin);
		arp.setShowSql(false);
		me.add(arp);
		
		arp.addMapping("merchant_info", MerchantsInfo.class);
		arp.addMapping("order_info", OrderInfo.class);
		arp.addMapping("manager_slider_info", Slider.class);
		arp.addMapping("manager_hot_recommend", Recommend.class);
		arp.addMapping("vouchers_info", Voucher.class);
		arp.addMapping("merchant_employees", MerchantsEmployees.class);
		arp.addMapping("authority_user_info", SystemUserInfo.class);
	    arp.addMapping("manager_agent_charge", Agent.class);
		arp.addMapping("merchant_apply_withdraw_record", WithDraw.class);
		arp.addMapping("authority_resource_info", SystemResource.class);
		arp.addMapping("manager_operate_log", SystemOperateLog.class);
		arp.addMapping("authority_role_info", SystemRole.class);
		arp.addMapping("area", Area.class);
		arp.addMapping("app_update", AppUpdate.class);
		arp.addMapping("authority_role_resource", RoleResource.class);
		arp.addMapping("authority_user_group", UserGroup.class);
		arp.addMapping("authority_user_role", UserRole.class);
		arp.addMapping("authority_user_app", UserApp.class);
		arp.addMapping("authority_group_info", SystemGroup.class);
		arp.addMapping("merchant_app_info", AppInfo.class);
		arp.addMapping("authority_group_role", GroupRole.class);
		arp.addMapping("manager_agent_charge", AgentCharge.class);
		arp.addMapping("manager_loading", Loading.class);
		arp.addMapping("message_center", Message.class);
		arp.addMapping("agent_employee", AgentEmployee.class);
		arp.addMapping("app_key_words", SearchWord.class);
		arp.addMapping("app_name_key_words", AppKey.class);
		arp.addMapping("feedback", UserFeedback.class);
		arp.addMapping("user_supply_app_key_word", UserWord.class);
		arp.addMapping("user_supply_app_name_key_word", ServiceWord.class);
		arp.addMapping("merchant_vip_apply", MerchantVipApply.class);
		arp.addMapping("merchant_employees_num_apply", MerchantAdviserApply.class);
		arp.addMapping("merchant_topup_apply", MerchantPushApply.class);
		arp.addMapping("app_key_words_stop_dic", StopKeyWords.class);
		arp.addMapping("app_key_words_custom_dic", CustomKeyWords.class);
		arp.addMapping("catalog", Catalog.class);
		arp.addMapping("service_type_apply", MerchantServiceTag.class);
		arp.addMapping("search_statistic", SearchStatistic.class);
		arp.addMapping("search_statistic_attachment", SearchStatisticAttch.class);
		arp.addMapping("activity_order_reward_detail", OrderRewardAccount.class);
		arp.addMapping("user_info", UserInfo.class);
		arp.addMapping("black_user", BlackUser.class);
	}
	
	public static void configStatisticPlugins(Plugins me) {
		Properties prop=PropUtil.getPropUtil("record_database.properties");
		C3p0Plugin c3p0Plugin = new C3p0Plugin(prop);
		me.add(c3p0Plugin);
		ActiveRecordPlugin arp = new ActiveRecordPlugin("ana_main",c3p0Plugin);
		arp.setShowSql(false);
		me.add(arp);
		arp.addMapping("record_first_visit_terminal", UserVisit.class);
		arp.addMapping("record_start_up_statistic", UserStartUp.class);
		arp.addMapping("record_day_visit_terminal", UserChannel.class);
		arp.addMapping("report_day_merchant_fans", FensiAddTotal.class);
		arp.addMapping("report_fans_top", FensiAddRanking.class);
		arp.addMapping("merchat_photo_statistical", MerPhotoStatic.class);
		
	}
	
//	public static void configSqlInXmlPlugins(Plugins me) {
//		SqlInXmlPlugin sqlXmlPlu=new SqlInXmlPlugin("com/shanjin/manager/sql");
//		me.add(sqlXmlPlu);//可以添加多个路径
//		
//	}
	/**
	 * 获取批量插入的数据库链接
	 * @return
	 */
	public static Connection getConnection(){
		String dbUrl = Constant.databaseConfig.getProperty("jdbcUrl")+"&rewriteBatchedStatements=true";
		String userName = Constant.databaseConfig.getProperty("user");
		String password = Constant.databaseConfig.getProperty("password");
		String driverClass = Constant.databaseConfig.getProperty("driverClass");
		Connection conn = null;
		try {
			Class.forName(driverClass);
			conn = DriverManager.getConnection(dbUrl, userName, password);
			conn.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		    return conn;
		}
        return conn;
	}
	
}

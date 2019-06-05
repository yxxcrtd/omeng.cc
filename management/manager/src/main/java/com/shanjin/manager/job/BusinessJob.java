package com.shanjin.manager.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.common.util.MD5Util;
import com.shanjin.manager.time.StatisticalUtil;



/**
 * 后台业务数据处理job
 * 
 * @author 
 * 
 */
public class BusinessJob {

	protected void work(){
//		 Date d = new Date();
//		 SimpleDateFormat time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
//		 System.out.println("---------------------job start,now time is:"+time.format(d)+"----------------------");
//		 System.out.println("=============================================");
//		 System.out.println("**************** 春有百花秋望月 **************");
//		 System.out.println("**************** 夏有凉风冬听雪 **************");
//		 System.out.println("**************** 心中若无烦恼事 **************");
//		 System.out.println("**************** 便是人生好时节 **************");
//		 System.out.println("=============================================");
		 OrderRewardJob.work();
//		 
//		 // *******************************以下是定时数据处理*******************************
//		 // ===================增值服务过期（失效）数据处理===================
//		 // 处理过期的商户vip数据
//		 String sql = "UPDATE merchant_vip_apply SET apply_status=4 WHERE failure_time<NOW();UPDATE merchant_info SET vip_level=0 WHERE id in(SELECT t.merchant_id FROM merchant_vip_apply t WHERE t.apply_status=4)";
//		 // 处理过期的商户顾问号数据
//		 List<Record> res=Db.find("select * from authority_user_info");
//			String pwd="";
//			for(Record r:res){
//				pwd=MD5Util.MD5_32(r.getStr("psw"));
//				r.set("password", pwd);
//				Db.update("authority_user_info",r);
//			}
		
	}

}


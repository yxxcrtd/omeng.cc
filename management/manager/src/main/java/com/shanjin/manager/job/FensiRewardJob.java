package com.shanjin.manager.job;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.time.StatisticalUtil;
import com.shanjin.manager.utils.AESUtil;
import com.shanjin.manager.utils.CommonUtil;
import com.shanjin.manager.utils.DateUtil;
import com.shanjin.manager.utils.MqUtil;



/**
 * 后台业务数据处理job
 * 
 * @author 
 * 
 */
public class FensiRewardJob {

	protected void work(){
		System.out.println("start"+new Date());
        handleFensiReward();
        System.out.println("end"+new Date());
	}

	private void handleFensiReward() {
		List<Record> list=Db.find(" /*master*/ select * from activity_fensi_payment_detail where status=0 and merchant_id=147089834972615675 limit 0,1");
		String sql="update merchant_statistics set total_income_price=total_income_price+?,surplus_price=surplus_price+? where merchant_id=?";
		 
		if(list!=null&&list.size()>0){
		Record reDetail;
		for(Record re:list){
			String date=DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN, new Date()); 
			long id=re.getLong("id");
			long merchant_id=re.getLong("merchant_id");	
			BigDecimal fans_price=re.getBigDecimal("fans_price");
			
			Db.update(sql, re.getBigDecimal("fans_price"),re.getBigDecimal("fans_price"),merchant_id);
			//插入到帐明细
			reDetail=new Record();
			reDetail.set("merchant_id",merchant_id).set("payment_type", 3).set("business_id", id)
			.set("payment_price", fans_price).set("payment_time", date);
			Db.save("merchant_payment_details", reDetail);
			
			
			BigDecimal rank_price=re.getBigDecimal("rank_price");
			int compare=rank_price.compareTo(BigDecimal.ZERO);
			if(compare==1){
				Db.update(sql, re.getBigDecimal("rank_price"),re.getBigDecimal("rank_price"),merchant_id);
				//插入到帐明细
				reDetail=new Record();
				reDetail.set("merchant_id",merchant_id).set("payment_type", 4).set("business_id", id)
				.set("payment_price", rank_price).set("payment_time", date);
				Db.save("merchant_payment_details", reDetail);
				
				writeMq(re,1,date);
			}
			
			re.set("pay_time", date).set("status", 1);
			Db.update("activity_fensi_payment_detail", re);
			
			writeMq(re,2,date);
		}
		}
	}
	private void writeMq(Record re,int flag, String date) {

		Map<String, Object> configMap = new HashMap<String, Object>();
		String sql = "SELECT me.user_id,me.phone,(select mi.name from merchant_info mi where mi.id=me.merchant_id) as name "
				+ " from merchant_employees me where me.employees_type=1 and me.merchant_id=? limit 1";
		String msg="";
		String encryptedText="";
		try {
		long merchant_id=re.getLong("merchant_id");	
		List<Record> merInfo = Db.find(sql, merchant_id);
		if (merInfo != null && merInfo.size() > 0) {
			configMap.put("merchantId", merchant_id);
			configMap.put("merchantUserId", merInfo.get(0).getLong("user_id"));
			configMap.put("merchantUserPhone", merInfo.get(0).getStr("phone"));
			configMap.put("merchantName", merInfo.get(0).getStr("name"));
			
			configMap.put("userName", "");
			configMap.put("activityName", "千万粉丝活动");
			configMap.put("activityId", "2");
			configMap.put("transSeq", re.getLong("id"));
			configMap.put("rewardTime", date);
			if(flag==1){
			    configMap.put("transAmount", re.getBigDecimal("rank_price"));
			    configMap.put("remark", "排名奖");
			}else{
				configMap.put("transAmount", re.getBigDecimal("fans_price"));
				configMap.put("remark", "粉丝奖");
			}
			
		}
		    msg = JSONObject.toJSONString(configMap);
		    encryptedText= AESUtil.parseByte2HexStr(AESUtil.encrypt(msg, "367937E1967092280C56077755E4C65B"));
			MqUtil.writeToMQ("orderTemplate", "opay.activityRewardExchange", encryptedText);
		} catch (Exception e) {
			CommonUtil.writeMqFailure(re.getLong("id"), encryptedText,"opay.activityRewardExchange", 3);
			e.printStackTrace();
		}
		
	}
}


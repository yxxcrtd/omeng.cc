package com.shanjin.manager.utils;

import java.util.Date;
import java.util.List;

import com.shanjin.manager.Bean.Agent;
import com.shanjin.manager.Bean.MerchantsInfo;
import com.shanjin.manager.Bean.SystemUserInfo;
import com.shanjin.manager.constant.Constant;

/**
 * 增值服务收益计算工具类
 * @author Huang yulai
 *
 */
public class ValueAddedIncomeUtil {

	private static final double omengRate=0.3;
	
	private static final double provinceRate=0.1;
	
	private static final double cityRate=0.1;
	
	private static final double appRate=0.5;
	
	
	
	
	/**
	 * 增值服务收益计算
	 * 收益分配：公司30%，省代10%，市代10%，项目代50%；若市代直营，则市代60%
	 * @param merchantId 商户用户ID
	 * @param income 本单金额
	 * @param operUserName 操作用户名
	 * @param incomeType 增值服务类型
	 * @return
	 */
	public static boolean calculateIncome(Long merchantId,double income,String operUserName,int incomeType){
		boolean flag = false;
		MerchantsInfo merchant = MerchantsInfo.dao.findById(merchantId);
		if(merchant==null) 
			return flag;
		String province = merchant.getStr("province");
		String city = merchant.getStr("city");
		String appType = merchant.getStr("app_type");
		boolean haveAppUser = false; //  是否有项目代理 true : 有  false : 没有
		boolean haveCityUser = false; //  是否有市代理 true : 有  false : 没有
		boolean haveProvinceUser = false; //  是否有省代理 true : 有  false : 没有
        // 查询项目代理
		if(!StringUtil.isNullStr(province)&&!StringUtil.isNullStr(city)&&!StringUtil.isNullStr(appType)){
			StringBuffer appSql = new  StringBuffer();
			appSql.append(" SELECT t.* FROM authority_user_info t  ");
			appSql.append(" LEFT JOIN authority_user_app ua ON t.id=ua.userId   ");
			appSql.append(" LEFT JOIN merchant_app_info ai ON ua.appId=ai.id AND ai.is_del=0 ");
			appSql.append(" WHERE t.isDel=0 AND t.userType=4 AND t.disabled=0  AND t.isAccount=1 ");
			appSql.append(" AND t.provinceDesc='").append(province).append("'");
			appSql.append(" AND t.cityDesc='").append(city).append("'");
			appSql.append(" AND ai.app_type='").append(appType).append("'");
			List<SystemUserInfo> list = SystemUserInfo.dao.find(appSql.toString());
			if(list!=null&&list.size()>0){
				haveAppUser = true;  //有项目代理
				SystemUserInfo appUser = list.get(0);
				// 记录项目代理收益
				int balance = appUser.getInt("balance");
				int appIncome = (int) (income*appRate);
				balance = balance + appIncome;
				appUser.set("balance", balance).update();
				
				// 保存代理收益详细
				Agent agent=new Agent();
				agent.set("agent_id", appUser.getLong("id")).set("charge_money", appIncome)
				.set("charge_time", new Date()).set("charge_type", 1).set("head_name", operUserName)
				.set("remark", incomeRemark(merchantId,incomeType)).set("charge_reason", incomeType)
				.set("order_status", "0").save();
			}
		}
		
		// 查询市代用户
		if(!StringUtil.isNullStr(province)&&!StringUtil.isNullStr(city)){
			String citySql = " SELECT * FROM authority_user_info t WHERE t.isDel=0 AND t.userType=3 AND t.disabled=0  AND t.isAccount=1 AND t.provinceDesc=?  AND t.cityDesc=?";
			List<SystemUserInfo> list = SystemUserInfo.dao.find(citySql, province,city);
			if(list!=null&&list.size()>0){
				haveCityUser = true; 
				SystemUserInfo cityUser = list.get(0);
				int balance = cityUser.getInt("balance");
				int cityIncome = 0;
				if(haveAppUser){
					// 有项目代理
					cityIncome = (int) (income*cityRate);
				}else{
					// 无项目代理（市代直营）
					cityIncome = (int) (income*(cityRate+appRate));
				}
				balance = balance + cityIncome;
				cityUser.set("balance", balance).update();
				
				// 保存代理收益详细
				Agent agent=new Agent();
				agent.set("agent_id", cityUser.getLong("id")).set("charge_money", cityIncome)
				.set("charge_time", new Date()).set("charge_type", 1).set("head_name", operUserName)
				.set("remark", incomeRemark(merchantId,incomeType)).set("charge_reason", incomeType)
				.set("order_status", "0").save();
			}
		}
		
		// 查询省代用户
		if(!StringUtil.isNullStr(province)){
			String provinceSql = " SELECT * FROM authority_user_info t WHERE t.isDel=0 AND t.userType=2 AND t.disabled=0  AND t.isAccount=1 AND t.provinceDesc=?";
			List<SystemUserInfo> list = SystemUserInfo.dao.find(provinceSql, province);
			if(list!=null&&list.size()>0){
				haveProvinceUser = true;
				SystemUserInfo provinceUser = list.get(0);
				int balance = provinceUser.getInt("balance");
				int provinceIncome = (int) (income*provinceRate);
				balance = balance + provinceIncome;
				provinceUser.set("balance", balance).update();
				
				// 保存代理收益详细
				Agent agent=new Agent();
				agent.set("agent_id", provinceUser.getLong("id")).set("charge_money", provinceIncome)
				.set("charge_time", new Date()).set("charge_type", 1).set("head_name", operUserName)
				.set("remark", incomeRemark(merchantId,incomeType)).set("charge_reason", incomeType)
				.set("order_status", "0").save();
			}
		}
		
		//公司指定账号收益计算 
		String omengSql = " SELECT * FROM authority_user_info t WHERE t.isDel=0 AND t.userType=1 AND t.userName=?";
		List<SystemUserInfo> list = SystemUserInfo.dao.find(omengSql, Constant.OMENG_INCOME_USERNAME);
		if(list!=null&&list.size()>0){
			SystemUserInfo omengUser = list.get(0);
			int balance = omengUser.getInt("balance");
			double rate = omengRate;
			if(haveAppUser){
				// 有项目代理
				if(haveCityUser){
					// 有市代
				}else{
					// 没有市代
					rate = rate + cityRate ;  // 市代收益归总部（有项目代无市代）
				}
			}else{
				// 没有项目代理
				if(haveCityUser){
					// 有市代
				}else{
					// 没有市代
					rate = rate + cityRate + appRate; // 项目代和市代收益均归总部（无市代和项目代）
				}
			}
			
			if(!haveProvinceUser){
				// 没有省代
				rate = rate + provinceRate; // 省代收益归总部（无省代）
			}
			
			int omengIncome = (int) (income*rate);
			balance = balance + omengIncome;
			omengUser.set("balance", balance).update();
			
			// 保存代理收益详细
			Agent agent=new Agent();
			agent.set("agent_id", omengUser.getLong("id")).set("charge_money", omengIncome)
			.set("charge_time", new Date()).set("charge_type", 1).set("head_name", operUserName)
			.set("remark", incomeRemark(merchantId,incomeType)).set("charge_reason", incomeType)
			.set("order_status", "0").save();
		}
		return flag;
	}
	
	private static String incomeRemark(Long merchantId,int incomeType){
		StringBuffer remark = new StringBuffer();
		if(incomeType==Constant.VALUEADDED_VIP){
			remark = remark.append("开通服务商vip(").append(merchantId).append(")");
		}else if(incomeType==Constant.VALUEADDED_PUSH){
			remark = remark.append("开通服务商订单推送(").append(merchantId).append(")");
		}else if(incomeType==Constant.VALUEADDED_ADVISER){
			remark = remark.append("开通服务商顾问号(").append(merchantId).append(")");
		}
		return remark.toString();
	}
	
}

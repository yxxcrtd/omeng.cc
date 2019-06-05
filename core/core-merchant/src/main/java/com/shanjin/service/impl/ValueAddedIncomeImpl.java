package com.shanjin.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.shanjin.common.constant.Constant;
import com.shanjin.common.util.StringUtil;
import com.shanjin.dao.IValueAddedIncomeDao;
import com.shanjin.service.IValueAddedIncomeService;

@Service("valueAddedIncomeService")
public class ValueAddedIncomeImpl implements IValueAddedIncomeService {
	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(ValueAddedIncomeImpl.class);
	@Resource
	private IValueAddedIncomeDao valueAddedIncomeDao;

	private static final double omengRate = 0.3;

	private static final double provinceRate = 0.1;

	private static final double cityRate = 0.1;

	private static final double appRate = 0.5;

	/**
	 * 增值服务收益计算 收益分配：公司30%，省代10%，市代10%，项目代50%；若市代直营，则市代60%
	 * 
	 * @param merchantId
	 *            商户用户ID
	 * @param income
	 *            本单金额
	 * @param operUserName
	 *            操作用户名
	 * @param incomeType
	 *            增值服务类型
	 * @return
	 */
	@Override
	public boolean calculateIncome(Long merchantId, double income, String operUserName, int incomeType) {
		boolean flag = false;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		paramMap.put("income", income);
		paramMap.put("operUserName", operUserName);
		paramMap.put("incomeType", incomeType);
		Map<String, Object> merchantInfo = valueAddedIncomeDao.selectMerchantInfo(paramMap);
		if (merchantInfo == null)
			return flag;
		String province = StringUtil.null2Str(merchantInfo.get("province"));
		String city = StringUtil.null2Str(merchantInfo.get("city"));
		String appType = StringUtil.null2Str(merchantInfo.get("app_type"));

		boolean haveAppUser = false; // 是否有项目代理 true : 有 false : 没有
		// 查询项目代理
		if (!StringUtil.isNullStr(province) && !StringUtil.isNullStr(city) && !StringUtil.isNullStr(appType)) {
			paramMap = new HashMap<String, Object>();
			paramMap.put("province", province);
			paramMap.put("city", city);
			paramMap.put("appType", appType);
			List<Map<String, Object>> list = valueAddedIncomeDao.getSystemUserInfo(paramMap);
			if (list != null && list.size() > 0) {

				haveAppUser = true; // 有项目代理
				Map<String, Object> appUser = list.get(0);
				// 记录项目代理收益
				int balance = StringUtil.nullToInteger(appUser.get("balance"));
				int appIncome = (int) (income * appRate);
				balance = balance + appIncome;
				appUser.put("balance", balance);
				valueAddedIncomeDao.updateSystemUserInfo(appUser);

				// 保存代理收益详细
				paramMap = new HashMap<String, Object>();
				paramMap.put("agent_id", StringUtil.nullToLong(appUser.get("id")));
				paramMap.put("charge_money", appIncome);
				paramMap.put("charge_time", new Date());
				paramMap.put("charge_type", 1);
				paramMap.put("head_name", operUserName);
				paramMap.put("remark", incomeRemark(merchantId, incomeType));
				paramMap.put("charge_reason", incomeType);
				paramMap.put("order_status", "0");
				valueAddedIncomeDao.saveAgent(paramMap);
			}
		}

		// 查询市代用户
		if (!StringUtil.isNullStr(province) && !StringUtil.isNullStr(city)) {
			String citySql = " SELECT * FROM authority_user_info t WHERE t.isDel=0 AND t.userType=3 AND t.provinceDesc=?  AND t.cityDesc=?";
			paramMap = new HashMap<String, Object>();
			paramMap.put("province", province);
			paramMap.put("city", city);
			List<Map<String, Object>> list = valueAddedIncomeDao.getSystemUserInfo(paramMap);
			if (list != null && list.size() > 0) {

				Map<String, Object> cityUser = list.get(0);
				int balance = StringUtil.nullToInteger(cityUser.get("balance"));
				int cityIncome = 0;
				if (haveAppUser) {
					// 有项目代理
					cityIncome = (int) (income * cityRate);
				} else {
					// 无项目代理（市代直营）
					cityIncome = (int) (income * (cityRate + appRate));
				}
				balance = balance + cityIncome;
				cityUser.put("balance", balance);
				valueAddedIncomeDao.updateSystemUserInfo(cityUser);

				// 保存代理收益详细
				paramMap = new HashMap<String, Object>();
				paramMap.put("agent_id", StringUtil.nullToLong(cityUser.get("id")));
				paramMap.put("charge_money", cityIncome);
				paramMap.put("charge_time", new Date());
				paramMap.put("charge_type", 1);
				paramMap.put("head_name", operUserName);
				paramMap.put("remark", incomeRemark(merchantId, incomeType));
				paramMap.put("charge_reason", incomeType);
				paramMap.put("order_status", "0");
				valueAddedIncomeDao.saveAgent(paramMap);
			}
		}

		// 查询省代用户
		if (!StringUtil.isNullStr(province)) {
			String provinceSql = " SELECT * FROM authority_user_info t WHERE t.isDel=0 AND t.userType=2 AND t.provinceDesc=?";
			paramMap = new HashMap<String, Object>();
			paramMap.put("province", province);
			List<Map<String, Object>> list = valueAddedIncomeDao.getProvinceUser(paramMap);
			if (list != null && list.size() > 0) {

				Map<String, Object> provinceUser = list.get(0);
				int balance = StringUtil.nullToInteger(provinceUser.get("balance"));
				int provinceIncome = (int) (income * provinceRate);
				balance = balance + provinceIncome;
				provinceUser.put("balance", balance);
				valueAddedIncomeDao.updateSystemUserInfo(provinceUser);

				// 保存代理收益详细
				paramMap = new HashMap<String, Object>();
				paramMap.put("agent_id", StringUtil.nullToLong(provinceUser.get("id")));
				paramMap.put("charge_money", provinceIncome);
				paramMap.put("charge_time", new Date());
				paramMap.put("charge_type", 1);
				paramMap.put("head_name", operUserName);
				paramMap.put("remark", incomeRemark(merchantId, incomeType));
				paramMap.put("charge_reason", incomeType);
				paramMap.put("order_status", "0");
				valueAddedIncomeDao.saveAgent(paramMap);
			}
		}

		// 公司指定账号收益计算
		String omengSql = " SELECT * FROM authority_user_info t WHERE t.isDel=0 AND t.userType=1 AND t.userName=?";
		paramMap = new HashMap<String, Object>();
		paramMap.put("userName", Constant.OMENG_INCOME_USERNAME);
		List<Map<String, Object>> list = valueAddedIncomeDao.getOmengUser(paramMap);
		if (list != null && list.size() > 0) {

			Map<String, Object> omengUser = list.get(0);
			int balance = StringUtil.nullToInteger(omengUser.get("balance"));
			int omengIncome = (int) (income * omengRate);
			balance = balance + omengIncome;
			omengUser.put("balance", balance);
			valueAddedIncomeDao.updateSystemUserInfo(omengUser);

			// 保存代理收益详细
			paramMap = new HashMap<String, Object>();
			paramMap.put("agent_id", StringUtil.nullToLong(omengUser.get("id")));
			paramMap.put("charge_money", omengIncome);
			paramMap.put("charge_time", new Date());
			paramMap.put("charge_type", 1);
			paramMap.put("head_name", operUserName);
			paramMap.put("remark", incomeRemark(merchantId, incomeType));
			paramMap.put("charge_reason", incomeType);
			paramMap.put("order_status", "0");
			valueAddedIncomeDao.saveAgent(paramMap);
		}
		return flag;
	}

	private static String incomeRemark(Long merchantId, int incomeType) {
		StringBuffer remark = new StringBuffer();
		if (incomeType == Constant.VALUEADDED_VIP) {
			remark = remark.append("开通服务商vip(").append(merchantId).append(")");
		} else if (incomeType == Constant.VALUEADDED_PUSH) {
			remark = remark.append("开通服务商订单推送(").append(merchantId).append(")");
		} else if (incomeType == Constant.VALUEADDED_ADVISER) {
			remark = remark.append("开通服务商顾问号(").append(merchantId).append(")");
		}
		return remark.toString();
	}
}

package com.shanjin.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.dao.IMerchantUsersDao;
import com.shanjin.service.IUserManageService;

@Service("userManageService")
public class UserManageServiceImpl implements IUserManageService {
	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(UserManageServiceImpl.class);
	@Resource
	private IMerchantUsersDao iMerchantUsersDao;

	/** 商户的用户信息 */
	@Override
	public JSONObject selectMerchantUsers(String appType, Long merchantId, int pageNo)throws Exception{
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 商户ID
		paramMap.put("merchantId", merchantId);
		// 应用程序类型
		paramMap.put("appType", appType);
		
		int count = this.iMerchantUsersDao.selectMerchantUsersCount(paramMap);
		jsonObject = new ResultJSONObject("000", "客户信息查询成功");
		if (count == 0) {
			jsonObject.put("totalPage", 0);
			jsonObject.put("merchantUsersInfoList", new ArrayList<HashMap<String, String>>());
		} else {
			// 查询起始记录行号
			paramMap.put("rows", pageNo * Constant.PAGESIZE);
			// 每页显示的记录数
			paramMap.put("pageSize", Constant.PAGESIZE);
			List<Map<String, Object>> merchantUsersMapList = this.iMerchantUsersDao.selectMerchantUsers(paramMap);
			for (Map<String, Object> merchantUsersMap : merchantUsersMapList) {
				BusinessUtil.disposePath(merchantUsersMap, "path");
			}
			jsonObject.put("totalPage", BusinessUtil.totalPageCalc(count));
			jsonObject.put("merchantUsersInfoList", merchantUsersMapList);
		}
		
		return jsonObject;
	}
}

package com.shanjin.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.common.util.DynamicKeyGenerator;
import com.shanjin.dao.IAppConfigDao;
import com.shanjin.service.IValidateService;

/**
 * 验证token接口实现类
 * 
 * @author 李焕民
 * @version 2015-4-5
 *
 */
@Service("validateService")
public class ValidateServiceImpl implements IValidateService {
	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(ValidateServiceImpl.class);

	@Resource
	private IAppConfigDao appConfigDao;

	@Resource
	private ICommonCacheService commonCacheService;

	/**
	 * 计算两个日期之间相差的天数
	 * 
	 * @param smdate
	 *            较小的时间
	 * @param bdate
	 *            较大的时间
	 * @return 相差天数
	 * @throws ParseException
	 */
	public static int daysBetween(Date smdate, Date bdate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		smdate = sdf.parse(sdf.format(smdate));
		bdate = sdf.parse(sdf.format(bdate));
		Calendar cal = Calendar.getInstance();
		cal.setTime(smdate);
		long time1 = cal.getTimeInMillis();
		cal.setTime(bdate);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);
		return Integer.parseInt(String.valueOf(between_days));
	}

	@Override
	public String lastValidatedTime(String clientId) {
		return (String) commonCacheService.getObject("validateTime", clientId);
	}

	@Override
	public void updateLastValidatedTime(String clientId, String time) {
		commonCacheService.setObject(time,60*10, "validateTime", clientId);
	}

	@Override
	public String getDynamicKey(String clientId) throws Exception {
		String dynamicKey = (String) commonCacheService.getObject("dynamicKey", clientId);
		if (dynamicKey == null) {
			dynamicKey = DynamicKeyGenerator.generateDynamicKey();
			commonCacheService.setObject(dynamicKey, "dynamicKey", clientId);
		}
		return dynamicKey;
	}

	@Override
	public void removeDynamicKey(String clientId) {
		commonCacheService.deleteObject("dynamicKey", clientId);
	}
}

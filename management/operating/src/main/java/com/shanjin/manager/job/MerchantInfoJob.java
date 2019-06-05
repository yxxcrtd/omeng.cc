package com.shanjin.manager.job;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.shanjin.manager.time.StatisticalUtil;



/**
 * 后台业务数据处理job
 * 
 * @author 
 * 
 */
public class MerchantInfoJob {

	protected void work(){
		StatisticalUtil.merchantStatis();
	}

}


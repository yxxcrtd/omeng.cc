package com.shanjin.Jest;

import javax.annotation.Resource;

import io.searchbox.client.http.JestHttpClient;
import io.searchbox.core.Delete;
import io.searchbox.core.Index;

import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.StringUtil;
import com.shanjin.dao.IMerchantForSearchDao;
import com.shanjin.service.IElasticSearchService;

/**
 * 更新商户位置信息索引
 * 
 * @author Revoke 2016.4.25
 *
 */
public class MerchantIndexUpdateRunner implements Runnable {
	
	private IElasticSearchService elasticSearchService;
	private String id;
	private String name;
	private Double latitude;
	private Double longitude;

	/**
	 * @param elasticSearchService
	 * @param merchant
	 * @param isNew
	 */
	MerchantIndexUpdateRunner(IElasticSearchService elasticSearchService,
			String id,String name,Double latitude, Double longitude) {
		this.elasticSearchService = elasticSearchService;
		this.id = id;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	public void run() {
		try {
			elasticSearchService.updateDocument(id,name,latitude,longitude);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

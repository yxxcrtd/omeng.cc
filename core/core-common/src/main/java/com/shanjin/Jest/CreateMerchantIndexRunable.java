package com.shanjin.Jest;

import io.searchbox.client.http.JestHttpClient;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.shanjin.common.util.StringUtil;
import com.shanjin.dao.IMerchantForSearchDao;

/**
 * 商户基本信息索引      2016.4.21  
 * @author Revoke
 *
 */
public class CreateMerchantIndexRunable implements Runnable {
	private IMerchantForSearchDao mertchantDao;
	
	
	public CreateMerchantIndexRunable(IMerchantForSearchDao dao){
			this.mertchantDao = dao;
		
	}

	@Override
	public void run() {
		// 获得客户端
		JestHttpClient client = ESFactory.getClient();
		try {
			int totalRecords = mertchantDao.getTotalMerchant();
			int PAGESIZE = 1000;
			int totalPages = (totalRecords+PAGESIZE-1)/PAGESIZE;
			System.out.println("搜索引擎总页数："+totalPages);
			Map<String,Object> param = new HashMap<String,Object>();
			for(int startPage=0;startPage<totalPages;startPage++){
					 param.put("rows", startPage*PAGESIZE);
					 param.put("pageSize", PAGESIZE);
					 List<Map<String,Object>> mertchantInfos = mertchantDao.getMerchantInPage(param);
					 
					Bulk.Builder bulkBuilder = new Bulk.Builder();
					
					for (Map<String, Object> mertchant : mertchantInfos) {
						MertChantOutline outline = new MertChantOutline();
						outline.setId(StringUtil.null2Str(mertchant.get("id")));
						outline.setName(StringUtil.null2Str(mertchant.get("name")));
						if (mertchant.get("latitude")==null){
							continue;
						}
						
						if (mertchant.get("longitude")==null){
							continue;
						}
						
						String indexId = StringUtil.null2Str(mertchant.get("id"));
						outline.setLocation(mertchant.get("latitude").toString()+","+mertchant.get("longitude").toString());
						
						String  serviceTypeIds=mertchantDao.getMerchantServiceTypeIds(indexId);
						if (serviceTypeIds!=null){
							  String[] ids=serviceTypeIds.split(",");
							  outline.setServiceTypeIds(JSON.toJSONString(ids));
						}
						Index index = new Index.Builder(outline).index("merchantindex").id(indexId).type("merchantindex").build();
						bulkBuilder.addAction(index);
					}
					client.execute(bulkBuilder.build());
					System.out.println("搜索引擎已处理："+startPage+"页");
					 
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			client.shutdownClient();
		}
	}
}

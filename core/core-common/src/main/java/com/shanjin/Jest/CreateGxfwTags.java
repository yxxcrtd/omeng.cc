package com.shanjin.Jest;

import io.searchbox.client.http.JestHttpClient;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.shanjin.common.util.StringUtil;

public class CreateGxfwTags implements Runnable {
	List<Map<String, Object>> tagsList = new ArrayList<Map<String, Object>>();

	public CreateGxfwTags(List<Map<String, Object>> tagsList) {
		this.tagsList = tagsList;
	}

	@Override
	public void run() {
		// 获得客户端
		JestHttpClient client = ESFactory.getClient();
		try {
			if (tagsList.size() > 0) {
				Integer count = 10000;
				Integer tagsListCount = tagsList.size() / count;
				System.out.println("tagsListCount:" + tagsListCount);
				Integer total = 0;
				for (int i = 0; i <= tagsListCount; i++) {
					List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>();
					if (i == tagsListCount) {
						tempList = tagsList.subList(i * count, tagsList.size());
					} else {
						tempList = tagsList.subList(i * count, count * (i + 1));
					}
					total = total + tempList.size();
					Bulk.Builder bulkBuilder = new Bulk.Builder();
					for (Map<String, Object> tagTemp : tempList) {
						GxfwTags tags = new GxfwTags();
						tags.setId(StringUtil.null2Str(tagTemp.get("id")));
						tags.setMerchantId(StringUtil.null2Str(tagTemp.get("merchantId")));
						tags.setTag(StringUtil.null2Str(tagTemp.get("tag")));
						tags.setPrice(StringUtil.null2Str(tagTemp.get("price")));
						String indexId = StringUtil.null2Str(tagTemp.get("id"));
						Index index = new Index.Builder(tags).index("gxfwindex").id(indexId).type("gxfwindex").build();
						bulkBuilder.addAction(index);
					}
					client.execute(bulkBuilder.build());
				}
				System.out.println("total:" + total);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			client.shutdownClient();
		}
	}
}

package com.shanjin.Jest;

import io.searchbox.client.http.JestHttpClient;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;
import io.searchbox.indices.mapping.PutMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.shanjin.common.util.DateUtil;
import com.shanjin.common.util.StringUtil;

public class CreateAppKeyWords implements Runnable {
	List<Map<String, Object>> wordList = new ArrayList<Map<String, Object>>();

	public CreateAppKeyWords(List<Map<String, Object>> wordList) {
		this.wordList = wordList;
	}

	@Override
	public void run() {
		// 获得客户端
		JestHttpClient client = ESFactory.getClient();
		System.out.println("size:" + this.wordList.size());
		System.out.println(DateUtil.getNowYYYYMMDDHHMMSS());
		try {
			if (wordList.size() > 0) {
				Integer count = 10000;
				Integer wordListCount = wordList.size() / count;
				System.out.println("wordListCount:" + wordListCount);
				Integer total = 0;
				for (int i = 0; i <= wordListCount; i++) {
					List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>();
					if (i == wordListCount) {
						tempList = wordList.subList(i * count, wordList.size());
					} else {
						tempList = wordList.subList(i * count, count * (i + 1));
					}
					total = total + tempList.size();
					Bulk.Builder bulkBuilder = new Bulk.Builder();
					for (Map<String, Object> word : tempList) {
						AppKeyWords keyWord = new AppKeyWords();
						keyWord.setId(StringUtil.null2Str(word.get("id")));
						keyWord.setKeyword(StringUtil.null2Str(word.get("keyword")));
						keyWord.setServiceTypeName(StringUtil.null2Str(word.get("serviceTypeName")));
						keyWord.setServiceType(StringUtil.null2Str(word.get("serviceType")));
						keyWord.setAppType(StringUtil.null2Str(word.get("appType")));
						String indexId = StringUtil.null2Str(word.get("id"));
						Index index = new Index.Builder(keyWord).index("appkeyword").id(indexId).type("appkeyword").build();
						bulkBuilder.addAction(index);
					}
					client.execute(bulkBuilder.build());
				}
				System.out.println("total:" + total);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client.shutdownClient();
		}
	}

	public static void createAppKeyWordMapping(String indices, String mappingType) {
		JestHttpClient client = ESFactory.getClient();
		try {
			System.out.println("建立map");
			PutMapping putMapping = new PutMapping.Builder(
					indices,
					mappingType,
					"{ \"properties\" : { \"keyword\" : {\"type\" : \"string\", \"store\" : \"yes\", \"indexAnalyzer\" : \"ik_smart\", \"searchAnalyzer\" : \"ik_smart\"},\"serviceTypeName\" : {\"type\" : \"string\", \"store\" : \"yes\"},\"serviceType\" : {\"type\" : \"string\", \"store\" : \"yes\"},\"serviceTypeName\" : {\"appType\" : \"string\", \"store\" : \"yes\"} } }")
					.build();
			client.execute(putMapping);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// /**
	// * 创建索引名称
	// *
	// * @param indices
	// * 索引名称
	// */
	// public static void createCluterName(String indices) {
	// Client client = null;
	// try {
	// client = ESClient.getClient();
	// } catch (Exception e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// }
	// client.admin().indices().prepareCreate(indices).execute().actionGet();
	// client.close();
	// }
	//
	// /**
	// * 创建mapping(feid("indexAnalyzer","ik")该字段分词IK索引
	// * ；feid("searchAnalyzer","ik")该字段分词ik查询；具体分词插件请看IK分词插件说明)
	// *
	// * @param indices
	// * 索引名称；
	// * @param mappingType
	// * 索引类型
	// * @throws Exception
	// */
	// public static void createMapping(String indices, String mappingType) {
	// System.out.println("创建mapping");
	// System.out.println("创建mapping");
	// System.out.println("创建mapping");
	// Client client = null;
	// try {
	// client = ESClient.getClient();
	// new XContentFactory();
	// XContentBuilder builder =
	// XContentFactory.jsonBuilder().startObject().startObject(indices).startObject("properties").startObject("keyword").field("type",
	// "string").field("store", "yes").field("indexAnalyzer",
	// "ik").field("searchAnalyzer",
	// "ik").endObject().startObject("serviceTypeName").field("type",
	// "string").field("store",
	// "yes").endObject().startObject("serviceType").field("type",
	// "string").field("store",
	// "yes").endObject().startObject("appType").field("type",
	// "string").field("store",
	// "yes").endObject().endObject().endObject().endObject();
	// PutMappingRequest mapping =
	// Requests.putMappingRequest(indices).type(mappingType).source(builder);
	// client.admin().indices().putMapping(mapping).actionGet();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// client.close();
	// }

	// @Override
	// public void run() {
	// Client client = null;
	// try {
	// client = ESClient.getClient();
	// } catch (Exception e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// }
	// System.out.println("size:" + this.wordList.size());
	// System.out.println(DateUtil.getNowYYYYMMDDHHMMSS());
	// // createMapping("appkeyword", "appkeyword");
	// // createCluterName("appkeyword");
	// BulkRequestBuilder bulkRequest = client.prepareBulk();
	//
	// // either use client#prepare, or use Requests# to directly build
	// // index/delete requests
	// try {
	// for (java.util.Map<String, Object> word : wordList) {
	// String indexId = StringUtil.null2Str(word.get("keyword")) + "|" +
	// StringUtil.null2Str(word.get("service_type")) + "|" +
	// StringUtil.null2Str(word.get("app_type"));
	// bulkRequest.add(client.prepareIndex("appKeyWords", "appKeyWords",
	// "indexId").setSource(jsonBuilder().startObject().field("keyword",
	// StringUtil.null2Str(word.get("keyword"))).field("serviceTypeName",
	// StringUtil.null2Str(word.get("service_type_name"))).field("serviceType",
	// StringUtil.null2Str(word.get("service_type"))).field("appType",
	// StringUtil.null2Str(word.get("app_type"))).endObject()));
	// }
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// BulkResponse bulkResponse = bulkRequest.get();
	// if (bulkResponse.hasFailures()) {
	// }
	// client.close();
	// //========================================
	// // // Bulk.Builder bulkBuilder = new Bulk.Builder();
	// // // for (Map<String, Object> word : wordList) {
	// // // AppKeyWords keyWord = new AppKeyWords();
	// // // keyWord.setKeyword(StringUtil.null2Str(word.get("keyword")));
	// // //
	// //
	// keyWord.setServiceTypeName(StringUtil.null2Str(word.get("service_type_name")));
	// // //
	// // keyWord.setServiceType(StringUtil.null2Str(word.get("service_type")));
	// // // keyWord.setAppType(StringUtil.null2Str(word.get("app_type")));
	// // // String indexId = StringUtil.null2Str(keyWord.getKeyword()) + "|" +
	// // // StringUtil.null2Str(keyWord.getServiceType()) + "|" +
	// // // StringUtil.null2Str(keyWord.getAppType());
	// // // Index index = new
	// // //
	// //
	// Index.Builder(keyWord).index("appkeyword").id(indexId).type("appkeyword").build();
	// // // bulkBuilder.addAction(index);
	// // // }
	// // // try {
	// // // client.execute(bulkBuilder.build());
	// // // } catch (IOException e) {
	// // // e.printStackTrace();
	// // // } finally {
	// // // client.shutdownClient();
	// // // }
	// }
}

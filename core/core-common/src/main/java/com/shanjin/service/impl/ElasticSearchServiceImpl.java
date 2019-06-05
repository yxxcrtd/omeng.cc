package com.shanjin.service.impl;

import io.searchbox.client.JestResult;
import io.searchbox.client.http.JestHttpClient;
import io.searchbox.core.Delete;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.CardinalityAggregation;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation.Entry;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.IndicesExists;
import io.searchbox.indices.mapping.PutMapping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder.Operator;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.shanjin.Jest.AppKeyWords;
import com.shanjin.Jest.CreateAppKeyWords;
import com.shanjin.Jest.CreateGxfwTags;
import com.shanjin.Jest.CreateMerchantIndexRunable;
import com.shanjin.Jest.ESFactory;
import com.shanjin.Jest.GxfwTags;
import com.shanjin.Jest.MertChantOutline;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.impl.GenericCacheServiceImpl;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.CalloutServices;
import com.shanjin.common.util.LocationUtil;
import com.shanjin.common.util.MD5Util;
import com.shanjin.common.util.StringUtil;
import com.shanjin.dao.IAppKeyWordDao;
import com.shanjin.dao.IGxfwIndexDao;
import com.shanjin.dao.ILoadingDao;
import com.shanjin.dao.IMerchantForSearchDao;
import com.shanjin.service.IElasticSearchService;

@Service("elasticSearchService")
@Transactional(rollbackFor = Exception.class)
public class ElasticSearchServiceImpl implements IElasticSearchService {
	// 本地异常日志记录对象
	private static final Logger logger = Logger
			.getLogger(ElasticSearchServiceImpl.class);

	private static final String PWDKEY = "X-SCE-ES-PASSWORD";
	@Resource
	private IAppKeyWordDao appKeyWordDao;
	@Resource
	private ICommonCacheService commonCacheService;
	@Resource
	private IGxfwIndexDao gxfwIndexDao;
	@Resource
	private ILoadingDao loadingDao;

	@Resource
	private IMerchantForSearchDao mertchantDao;

	/**
	 * 创建mapping(feid("indexAnalyzer","ik")该字段分词IK索引
	 * ；feid("searchAnalyzer","ik")该字段分词ik查询；具体分词插件请看IK分词插件说明)
	 * 
	 * @param indices
	 *            索引名称；
	 * @param mappingType
	 *            索引类型
	 * @throws Exception
	 */
	public static void createAppKeyWordMapping(String indices,
			String mappingType) {
		JestHttpClient client = ESFactory.getClient();
		try {
			// System.out.println("建立map");
			// XContentBuilder content =
			// XContentFactory.jsonBuilder().startObject().startObject(indices).startObject("properties").startObject("keyword").field("type",
			// "string").field("store", "yes").field("indexAnalyzer",
			// "ik_smart").field("searchAnalyzer",
			// "ik_smart").endObject().startObject("serviceTypeName").field("type",
			// "string").field("store",
			// "yes").endObject().startObject("serviceType").field("type",
			// "string").field("store",
			// "yes").endObject().startObject("appType").field("type",
			// "string").field("store",
			// "yes").endObject().endObject().endObject().endObject();
			// System.out.println(content.toString());
			// PutMapping.Builder builder = new PutMapping.Builder(indices,
			// mappingType, content);
			// JestResult result = client.execute(builder.build());
			// System.out.println(result.getJsonString());
			// if (result == null || !result.isSucceeded()) {
			// throw new RuntimeException(result.getErrorMessage() +
			// "创建索引类型失败!");
			// }
			// PutMapping putMapping = new PutMapping.Builder(indices,
			// mappingType,
			// "{ \"properties\" : { \"keyword\" : {\"type\" : \"string\", \"store\" : \"yes\", \"indexAnalyzer\" : \"ik_smart\", \"searchAnalyzer\" : \"ik_smart\"},\"serviceTypeName\" : {\"type\" : \"string\", \"store\" : \"yes\"},\"serviceType\" : {\"type\" : \"string\", \"store\" : \"yes\"},\"serviceTypeName\" : {\"appType\" : \"string\", \"store\" : \"yes\"} } }").build();
			// client.execute(putMapping);

			// client.execute(putMapping);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client.shutdownClient();
		}
	}

	protected static String getSecret() {
		long time = System.currentTimeMillis() / 1000;
		return time + "," + MD5Util.MD5_32(time + PWDKEY).toUpperCase();
	}

	/** 服务关键词建立索引 */
	@Override
	public JSONObject createAppWordsIndex() throws Exception {
		JSONObject jsonObject = new ResultJSONObject("000", "创建关键词搜索索引成功");
		JestHttpClient client = ESFactory.getClient();
		long start = System.currentTimeMillis();
		try {

			// DeleteIndex deleteIndex = new
			// DeleteIndex.Builder("appkeyword").build();
			// client.execute(deleteIndex);

			// CreateIndex createIndex = new
			// CreateIndex.Builder("appkeyword").build();
			// client.execute(createIndex);

			client.execute(new CreateIndex.Builder("appkeyword").build());

			/*
			 * PutMapping putMapping = new PutMapping.Builder("appkeyword",
			 * "appkeyword",
			 * "{ \"document\" : { \"properties\" : { \"keyword\" : {\"type\" : \"string\", \"store\" : \"yes\"} } } }"
			 * ) .build(); client.execute(putMapping);
			 */

			// PutMapping putMapping = new PutMapping.Builder(
			// "appkeyword",
			// "appkeyword",
			// "{ \"appkeyword\" : { \"properties\" : { \"appType\" : {\"type\" : \"string\", \"store\" : \"yes\"} }, { \"id\" : {\"type\" : \"string\", \"store\" : \"yes\"} }, { \"keyword\" : {\"type\" : \"string\", \"store\" : \"yes\"} }, { \"serviceType\" : {\"type\" : \"string\", \"store\" : \"yes\"} }, { \"serviceTypeName\" : {\"type\" : \"string\", \"store\" : \"yes\"} } } }")
			// .build();
			// String
			// indexBuf="{ \"appkeyword\" : {  \"_all\": {\"analyzer\" : {\"ik_syno\" : {\"tokenizer\" : \"ik\", \"filter\" : [\"my_synonym_filter\"]}}, \"search_analyzer\" : {\"ik_syno\" : {\"tokenizer\" : \"ik\", \"filter\" : [\"my_synonym_filter\"]}},\"term_vector\" : \"no\",\"store\" : \"false\"},\"properties\" : { \"keyword\" : {\"type\" : \"string\",  \"analyzer\" : {\"ik_syno\" : {\"tokenizer\" : \"ik\", \"filter\" : [\"my_synonym_filter\"]}}, \"search_analyzer\" : {\"ik_syno\": {\"tokenizer\" : \"ik\", \"filter\" : [\"my_synonym_filter\"]}} } } } }";
			String indexBuf = "{ \"appkeyword\" : {  \"_all\": {\"analyzer\": \"ik_syno\", \"search_analyzer\": \"ik_smart\",\"term_vector\": \"no\",\"store\": \"false\"},\"properties\" : { \"keyword\" : {\"type\" : \"string\",  \"analyzer\" : \"ik_smart\", \"search_analyzer\" : \"ik_smart\"} } } }";

			PutMapping putMapping = new PutMapping.Builder("appkeyword",
					"appkeyword",
					// "{ \"appkeyword\" : {  \"_all\": {\"analyzer\": \"ik_smart\", \"search_analyzer\": \"ik_smart\",\"term_vector\": \"no\",\"store\": \"false\"},\"properties\" : { \"keyword\" : {\"type\" : \"string\",  \"analyzer\" : \"ik_smart\", \"search_analyzer\" : \"ik_smart\"} } } }")
					indexBuf).build();
			client.execute(putMapping);

			// createAppKeyWordMapping("appkeyword", "appkeyword");

			// IndicesExists indicesExists = new
			// IndicesExists.Builder("appkeyword").build();
			// JestResult result = client.execute(indicesExists);
			// if (!result.isSucceeded()) {
			// createAppKeyWordMapping("appkeyword", "appkeyword");
			// // CreateIndex createIndex = new
			// // CreateIndex.Builder("appkeyword").build();
			// // client.execute(createIndex);
			// }
			List<Map<String, Object>> wordList = appKeyWordDao
					.getAllAppKeyWord();
			// Client client =
			// TransportClient.builder().build().addTransportAddress(new
			// InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"),
			// 9300));
			// System.out.println(client);
			// createCluterName("appkeyword");
			// createMapping("appkeyword", "appkeyword");
			// BulkRequestBuilder bulkRequest = client.prepareBulk();
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
			// BulkResponse bulkResponse = bulkRequest.get();
			// if (bulkResponse.hasFailures()) {
			// }
			// client.close();
			CreateAppKeyWords createAppKeyWords = new CreateAppKeyWords(
					wordList);
			CalloutServices.executor(createAppKeyWords);
			long end = System.currentTimeMillis();
			System.out.println("创建索引时间:共用时间 -->> " + (end - start) + " 毫秒");
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject = new ResultJSONObject("fail", "创建关键词搜索索引失败");
		} finally {
			client.shutdownClient();
			return jsonObject;
		}
	}

	/** 服务关键词搜索 */
	@Override
	public JSONObject searchAppWords(String queryString, Boolean isTest,
			String prefix) {
		// 初始化Client
		JestHttpClient client = ESFactory.getClient();
		// 初始化返回的值
		ArrayList<Map> searchresult = new ArrayList<Map>();
		JSONObject jsonObject = new ResultJSONObject("000", "搜索成功");
		try {
			List<String> analyzeKeywords = new ArrayList<String>();
			// 2016年1月5日 获得拆分的词
			CloseableHttpClient httpClient = HttpClients.createDefault();
			try {
				// 用get方法发送http请求
				HttpGet get = new HttpGet(
						Constant.SEARCH_URL
								+ "/appkeyword/_analyze?analyzer=ik_smart&pretty=true&text="
								+ queryString);
				CloseableHttpResponse httpResponse = null;
				// 发送get请求
				httpResponse = httpClient.execute(get);
				try {
					// response实体
					HttpEntity entity = httpResponse.getEntity();
					if (null != entity) {
						if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
							String response = EntityUtils.toString(entity,
									"utf8");
							JSONObject resObject = JSON.parseObject(response);
							JSONArray tokens = resObject.getJSONArray("tokens");
							if (tokens.size() > 0) {
								for (int i = 0; i < tokens.size(); i++) {
									JSONObject token = (JSONObject) tokens
											.get(i);
									if (StringUtil.isNotEmpty(token
											.getString("token"))) {
										analyzeKeywords.add(token
												.getString("token"));
									}
								}
							}
						}
					}
				} finally {
					httpResponse.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			//
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			// StringBuffer modifiedQueryString = new StringBuffer("");
			// if (analyzeKeywords.size()>0){
			// for(String keywords:analyzeKeywords){
			// modifiedQueryString.append(" or ").append(keywords);
			// }
			// modifiedQueryString.delete(0, 4);
			// }else{
			// modifiedQueryString = new StringBuffer(modifiedQueryString);
			// }

			searchSourceBuilder.query(QueryBuildersUtils
					.multiMatchQueryM1(analyzeKeywords));
			searchSourceBuilder.from(0);
			searchSourceBuilder.size(1000);
			// searchSourceBuilder.query(QueryBuilders.queryStringQuery(queryString).analyzer("ik_smart").field("keyword"));
			Search search = new Search.Builder(searchSourceBuilder.toString())
					.addIndex("appkeyword").addType("appkeyword").build();
			JestResult result = client.execute(search);
			List<Map> relist = result.getSourceAsObjectList(Map.class);

			if (!isTest) {
				// 去多余字段
				List<Map> returnList = new ArrayList<Map>();
				List<Map> sameList = new ArrayList<Map>();
				for (Map record : relist) {
					String keyword = StringUtil.null2Str(record.get("keyword"));
					record.remove("es_metadata_id");
					record.remove("keyword");
					record.remove("id");
					if (record.get("serviceTypeName") == null) {
						record.put("serviceTypeName", "");
					}
					if (record.get("serviceType") == null) {
						record.put("serviceType", "");
					} else {
						record.put("serviceType",
								StringUtil.null2Str(record.get("serviceType")));
					}
					// 如果出现精准的关键词，则添加到另一个列表做处理

					record.put("url", prefix + record.get("serviceType"));

					returnList.add(record);
					for (String token : analyzeKeywords) {
						if (token.equals(keyword)) {
							sameList.add(record);
						}
					}
				}

				// 去重复
				if (sameList.size() > 0) {
					// 先根据搜索出来的关键词多的排序
					sortByMoreKeyword(sameList);
					for (Map<String, Object> s : sameList) {
						// 去除多余的统计字段
						s.remove(StringUtil.null2Str(s.get("appType")) + "_"
								+ StringUtil.null2Str(s.get("serviceType")));
						if (!searchresult.contains(s)) {
							searchresult.add(s);
						}
					}
				} else {
					// 先根据搜索出来的关键词多的排序
					sortByMoreKeyword(returnList);
					for (Map<String, Object> s : returnList) {
						// 去除多余的统计字段
						s.remove(StringUtil.null2Str(s.get("appType")) + "_"
								+ StringUtil.null2Str(s.get("serviceType")));
						if (!searchresult.contains(s)) {
							searchresult.add(s);
						}
					}
				}
				// 如果有个性服务的话，个性服务排前面
				sortGxfwAppType(searchresult);
			} else {
				searchresult = (ArrayList<Map>) relist;
				// 先根据搜索出来的关键词多的排序
				sortByMoreKeyword(searchresult);
				// 如果有个性服务的话，个性服务排前面
				sortGxfwAppType(searchresult);
			}

			// 如果没搜到结果，将用户输入的词插入到待审核的词库
			if (searchresult == null || searchresult.size() < 1) {
//				if (this.appKeyWordDao.checkUserAppKeyWord(queryString) == 0) {
//					this.appKeyWordDao.insertUserAppKeyWord(queryString);
//				}
				
				Map<String, Object> paraMap=new HashMap<String, Object>();
				paraMap.put("queryString", queryString);
				paraMap.put("is_del", 0);//
				paraMap.put("is_audit", 0);
				paraMap.put("remark", "");
				paraMap.put("feedbacknum", 1);
				paraMap.put("app_key_word_num", 0);
				paraMap.put("is_feedbackaudit", 0);
				paraMap.put("keywordtype", 1);
				
//				int update = appKeyWordDao.updateUserFeedBackAppKeyWord(paraMap);
				
				// 查询用户搜索关键词是否反馈记录
				if (this.appKeyWordDao.checkUserFeedBackAppKeyWord(paraMap) == 0) {
					// 新增反馈记录
					this.appKeyWordDao.insertUserFeedBackAppKeyWord(paraMap);
				} else {
					// 更新反馈记录
					this.appKeyWordDao.updateUserFeedBackAppKeyWord(paraMap);
				}
				
				
				
			} else {
				this.updateSearchStatistic(searchresult);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new ResultJSONObject("fail", "搜索失败");
		} finally {
			client.shutdownClient();
			jsonObject.put("totalCount", searchresult.size());
			jsonObject.put("resultList", searchresult);
			return jsonObject;
		}
	}

	private void updateSearchStatistic(ArrayList<Map> searchresult) {
		if (searchresult == null || searchresult.size() < 1) {

		} else {
			List<Map<String, Object>> results = (List<Map<String, Object>>) commonCacheService
					.getObject(CacheConstants.SEARCH_STATICTIS_KEY);
			Map m = null;
			if (results == null || results.size() == 0) {
				results = new ArrayList<Map<String, Object>>();
				for (Map ma : searchresult) {
					m = ma;
					m.put("search_count", 1);
					results.add(m);
				}
				commonCacheService.setObject(results,
						CacheConstants.SEARCH_STATICTIS_KEY);

			} else {
				for (Map ma : searchresult) {
					boolean flag = false;
					for (Map re : results) {
						if (ma.get("appType").equals(re.get("appType"))
								&& ma.get("serviceType").equals(
										re.get("serviceType"))) {
							int count = (int) re.get("search_count") + 1;
							re.put("search_count", count);
							flag = true;
						}
					}
					if (!flag) {
						m = ma;
						m.put("search_count", 1);
						results.add(m);
					}
				}
				commonCacheService.setObject(results,
						CacheConstants.SEARCH_STATICTIS_KEY);
			}

		}
	}

	/** 个性服务搜索 */
	@Override
	public JSONObject iGxfwSearch(String queryString, Boolean isTest)
			throws Exception {
		JestHttpClient client = ESFactory.getClient();
		JSONObject jsonObject = new ResultJSONObject("000", "搜索成功");
		ArrayList<Map> searchresult = new ArrayList<Map>();
		try {
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			searchSourceBuilder.query(QueryBuilders
					.queryStringQuery(queryString).field("tag")
					.analyzer("ik_smart"));
			searchSourceBuilder.from(0);
			searchSourceBuilder.size(20);
			// searchSourceBuilder.query(QueryBuilders.simpleQueryStringQuery(queryString).field("tag"));
			Search search = new Search.Builder(searchSourceBuilder.toString())
					.addIndex("gxfwindex").addType("gxfwindex").build();
			JestResult result = client.execute(search);
			List<Map> relist = result.getSourceAsObjectList(Map.class);
			List<Map> returnList = new ArrayList<Map>();
			if (!isTest) {
				for (Map record : relist) {
					record.remove("es_metadata_id");
					record.remove("id");
					returnList.add(record);
				}
				// 去重复

				for (Map<String, Object> s : returnList) {
					if (!searchresult.contains(s)) {
						searchresult.add(s);
					}
				}
			} else {
				jsonObject = new ResultJSONObject("000", "测试GXFW搜索成功");
				searchresult = (ArrayList<Map>) relist;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultJSONObject("fail", "搜索失败");
		} finally {
			client.shutdownClient();
			jsonObject.put("totalCount", searchresult.size());
			jsonObject.put("resultList", searchresult);
			return jsonObject;
		}
	}

	/**
	 * 个性服务的搜索
	 */
	@Override
	public ArrayList<Map> iGxfwSearchMerchantId(String queryString) {
		JestHttpClient client = ESFactory.getClient();
		try {
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			// searchSourceBuilder.query(QueryBuilders.simpleQueryStringQuery(queryString).field("tag"));
			searchSourceBuilder.query(QueryBuilders
					.queryStringQuery(queryString).field("tag")
					.analyzer("ik_smart"));
			searchSourceBuilder.from(0);
			searchSourceBuilder.size(20);
			Search search = new Search.Builder(searchSourceBuilder.toString())
					.addIndex("gxfwindex").addType("gxfwindex").build();
			JestResult result = client.execute(search);
			List<Map> relist = result.getSourceAsObjectList(Map.class);
			List<Map> returnList = new ArrayList<Map>();
			for (Map record : relist) {
				record.remove("es_metadata_id");
				record.remove("id");
				returnList.add(record);
			}
			// 去重复
			ArrayList<Map> searchresult = new ArrayList<Map>();
			for (Map<String, Object> s : returnList) {
				if (!searchresult.contains(s)) {
					searchresult.add(s);
				}
			}
			return searchresult;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client.shutdownClient();
		}
		return null;
	}

	/** 个性服务建立索引 */
	@Override
	public JSONObject iCreateGxfwIndex() throws Exception {
		JSONObject jsonObject = new ResultJSONObject("000", "创建个性服务索引成功");
		JestHttpClient client = ESFactory.getClient();
		long start = System.currentTimeMillis();
		try {
			IndicesExists indicesExists = new IndicesExists.Builder("gxfwindex")
					.build();
			JestResult result = client.execute(indicesExists);
			if (!result.isSucceeded()) {
				CreateIndex createIndex = new CreateIndex.Builder("gxfwindex")
						.build();
				client.execute(createIndex);
			}

			PutMapping putMapping = new PutMapping.Builder(
					"gxfwindex",
					"gxfwindex",
					"{ \"gxfwindex\" : { \"properties\" : { \"name\" : {\"type\" : \"string\",  \"analyzer\" : \"ik_smart\", \"search_analyzer\" : \"ik_smart\"} } } }")
					.build();
			client.execute(putMapping);

			List<Map<String, Object>> tagsList = gxfwIndexDao
					.getAllMerchantTags();
			CreateGxfwTags createGxfwTags = new CreateGxfwTags(tagsList);
			CalloutServices.executor(createGxfwTags);
			long end = System.currentTimeMillis();
			System.out.println("创建索引时间:共用时间 -->> " + (end - start) + " 毫秒");
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject = new ResultJSONObject("fail", "创建个性服务索引失败");
		} finally {
			client.shutdownClient();
			return jsonObject;
		}
	}

	/** 服务商建立索引 ---------Revoke 2016.4.21 */
	@Override
	public JSONObject iCreateMerchantIndex() throws Exception {
		JSONObject jsonObject = new ResultJSONObject("000", "创建服务商索引成功");
		JestHttpClient client = ESFactory.getClient();
		long start = System.currentTimeMillis();
		try {
			IndicesExists indicesExists = new IndicesExists.Builder(
					"merchantindex").build();
			JestResult result = client.execute(indicesExists);
			if (!result.isSucceeded()) {
				CreateIndex createIndex = new CreateIndex.Builder(
						"merchantindex").build();
				client.execute(createIndex);
			}

			String indexBuf = "{ \"merchantindex\" : { \"properties\" : { \"location\" : {\"type\" : \"geo_point\"} } } }";

			PutMapping putMapping = new PutMapping.Builder("merchantindex",
					"merchantindex", indexBuf).build();
			client.execute(putMapping);

			CreateMerchantIndexRunable merchantIndex = new CreateMerchantIndexRunable(
					mertchantDao);
			CalloutServices.executor(merchantIndex);
			long end = System.currentTimeMillis();
			System.out.println("创建索引时间:共用时间 -->> " + (end - start) + " 毫秒");
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject = new ResultJSONObject("fail", "服务索引失败");
		} finally {
			client.shutdownClient();
			return jsonObject;
		}
	}

	@Override
	public JSONObject delIndex(String indexName) throws Exception {
		JSONObject jsonObject = new ResultJSONObject("000", "删除索引成功");
		JestHttpClient client = ESFactory.getClient();
		try {
			DeleteIndex deleteIndex = new DeleteIndex.Builder(indexName)
					.build();
			client.execute(deleteIndex);
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject = new ResultJSONObject("fail", "删除索引失败");
		} finally {
			client.shutdownClient();
			return jsonObject;
		}
	}

	// 如果有个性服务，则放到末尾
	private void sortGxfwAppType(ArrayList<Map> sortList) {
		for (int i = 0; i < sortList.size(); i++) {
			if (sortList.get(i) != null) {
				if (StringUtil.null2Str(sortList.get(i).get("appType")).equals(
						"gxfw")) {
					Map value = sortList.get(i);
					sortList.remove(sortList.get(i));
					sortList.add(value);
				}
			}
		}
	}

	// 根据搜索的服务类型多的关键词排序
	private void sortByMoreKeyword(List<Map> sortList) {
		// 统计不同服务类型的出现次数
		Map<String, Integer> countMap = new HashMap<String, Integer>();
		for (int i = 0; i < sortList.size(); i++) {
			if (sortList.get(i) != null) {
				Map value = sortList.get(i);
				String mapKey = StringUtil.null2Str(value.get("appType")) + "_"
						+ StringUtil.null2Str(value.get("serviceType"));
				if (countMap.containsKey(mapKey)) {
					value.put(mapKey, (Integer) countMap.get(mapKey) + 1);
					countMap.put(mapKey, (Integer) countMap.get(mapKey) + 1);
				} else {
					// 如果不存在则初始化为1
					value.put(mapKey, 1);
					countMap.put(mapKey, 1);
				}
			}
		}
		Collections.sort(sortList, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				Map map1 = (Map) o1;
				Map map2 = (Map) o2;
				String key1 = StringUtil.null2Str(map1.get("appType")) + "_"
						+ StringUtil.null2Str(map1.get("serviceType"));
				String key2 = StringUtil.null2Str(map2.get("appType")) + "_"
						+ StringUtil.null2Str(map2.get("serviceType"));
				return Integer.compare((Integer) map2.get(key2),
						(Integer) map1.get(key1));
			}
		});
	}

	@Override
	public void testSearchMerchantId(String queryString) {
		List<Map<String, Object>> resultList = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<Map> merchantIds = iGxfwSearchMerchantId(queryString);
		if (merchantIds != null && merchantIds.size() > 0) {
			paramMap.put("merchantIds", merchantIds);
			System.out.println("merchantIds:" + merchantIds);
			resultList = loadingDao.getClientIdsByServiceTag(paramMap);
			System.out.println(resultList);
		} else {
		}

	}

	/** 添加索引文档 */
	@Override
	public JSONObject addDocument(String ids, String indexName)
			throws Exception {
		String[] idArray = ids.split(",");
		JSONObject jsonObject = new ResultJSONObject("000", "添加文档到索引成功");
		JestHttpClient client = ESFactory.getClient();
		if (StringUtil.isNotEmpty(indexName)) {
			try {
				if (indexName.equals("appkeyword")) {
					for (String id : idArray) {
						Map<String, Object> paramMap = new HashMap<String, Object>();
						paramMap.put("id", id);
						Map<String, Object> word = appKeyWordDao
								.getAllAppKeyWordById(paramMap);
						if (word != null) {
							AppKeyWords keyWord = new AppKeyWords();
							keyWord.setId(StringUtil.null2Str(word.get("id")));
							keyWord.setKeyword(StringUtil.null2Str(word
									.get("keyword")));
							keyWord.setServiceTypeName(StringUtil.null2Str(word
									.get("serviceTypeName")));
							keyWord.setServiceType(StringUtil.null2Str(word
									.get("serviceType")));
							keyWord.setAppType(StringUtil.null2Str(word
									.get("appType")));
							client.execute(new Index.Builder(keyWord)
									.index("appkeyword").id(id)
									.type("appkeyword").build());
						}
					}
				}
				if (indexName.equals("merchantindex")) {
					for (String id : idArray) {
						Map<String, Object> paramMap = new HashMap<String, Object>();
						paramMap.put("id", id);
						Map<String, Object> merchant = mertchantDao
								.getMerchantOutLineById(paramMap);
						if (merchant != null) {
							MertChantOutline outline = new MertChantOutline();

							outline.setId(merchant.get("id").toString());
							outline.setName(merchant.get("name").toString());

							if (merchant.get("latitude") == null) {
								continue;
							}

							if (merchant.get("longitude") == null) {
								continue;
							}

							String indexId = outline.getId();
							outline.setLocation(merchant.get("latitude")
									.toString()
									+ ","
									+ merchant.get("longitude").toString());
							client.execute(new Index.Builder(outline)
									.index("merchantindex").id(id)
									.type("merchantindex").build());
						}
					}
				}

				if (indexName.equals("gxfwindex")) {
					for (String id : idArray) {
						Map<String, Object> paramMap = new HashMap<String, Object>();
						paramMap.put("id", id);
						Map<String, Object> tag = gxfwIndexDao
								.getAllMerchantTagsById(paramMap);
						if (tag != null) {
							GxfwTags tags = new GxfwTags();
							tags.setId(StringUtil.null2Str(tag.get("id")));
							tags.setMerchantId(StringUtil.null2Str(tag
									.get("merchantId")));
							tags.setTag(StringUtil.null2Str(tag.get("tag")));
							tags.setPrice(StringUtil.null2Str(tag.get("price")));
							client.execute(new Index.Builder(tags)
									.index("gxfwindex").id(id)
									.type("gxfwindex").build());
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				jsonObject = new ResultJSONObject("fail", "添加文档到索引失败");
			} finally {
				client.shutdownClient();
				return jsonObject;
			}
		}
		return jsonObject;
	}

	/** 删除索引文档 */
	@Override
	public JSONObject delDocument(String ids, String indexName)
			throws Exception {
		String[] idArray = ids.split(",");
		JSONObject jsonObject = new ResultJSONObject("000", "删除索引文档成功");
		JestHttpClient client = ESFactory.getClient();
		if (StringUtil.isNotEmpty(indexName)) {
			try {
				if (indexName.equals("appkeyword")) {
					for (String id : idArray) {
						client.execute(new Delete.Builder(id)
								.index("appkeyword").type("appkeyword").build());
					}
				}
				if (indexName.equals("gxfwindex")) {
					for (String id : idArray) {
						client.execute(new Delete.Builder(id)
								.index("gxfwindex").type("gxfwindex").build());
					}
				}
				// 增加删除商户位置索引---Revoke 2016.4.25
				if (indexName.equals("merchantindex")) {
					for (String id : idArray) {
						client.execute(new Delete.Builder(id)
								.index("merchantindex").type("merchantindex")
								.build());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				jsonObject = new ResultJSONObject("fail", "删除索引文档失败");
			} finally {
				client.shutdownClient();
				return jsonObject;
			}
		}
		return jsonObject;
	}

	// 获取自定义词库
	@Override
	public List<String> getCustomDict() {
		return appKeyWordDao.getCustomDict();
	}

	// 获取自定义停词库
	@Override
	public List<String> getCustomStopDict() {
		return appKeyWordDao.getCustomStopDict();
	}

	@Override
	public List<String> getAppKeyWordDict() {
		// TODO Auto-generated method stub
		return appKeyWordDao.getAppKeyWordDict();
	}

	// 获取自定义词库的个数
	@Override
	public Integer getCustomDictCount() {
		return appKeyWordDao.getCustomDictCount();
	}

	// 获取自定义停词库的个数
	@Override
	public Integer getCustomStopDictCount() {
		return appKeyWordDao.getCustomStopDictCount();
	}

	@Override
	public Integer getAppKeyWordDictCount() {
		// TODO Auto-generated method stub
		return appKeyWordDao.getAppKeyWordCount();
	}

	@Override
	public JSONObject searchAppWordsByGrpScore(String queryString) {
		// 初始化Client
		JestHttpClient client = ESFactory.getClient();
		// 初始化返回的值
		ArrayList<Map> searchresult = new ArrayList<Map>();
		JSONObject jsonObject = new ResultJSONObject("000", "搜索成功");
		try {
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			StringBuilder stringBuilder = new StringBuilder("{")
					.append("\"min_score\":1.0,").append("\"query\" : {")
					.append("\"query_string\" : {")
					.append("\"query\" : \"keyword:").append(queryString)
					.append("\"").append("}").append("}").append(",\"size\":0")
					.append(",\"aggs\" : {").append("\"byAppType\" : {")
					.append("\"terms\" : {").append("\"field\" : \"appType\"")
					.append(",\"order\": {")
					.append("\"grpMaxScore\": \"desc\"").append("}")
					.append("}").append(",\"aggs\": {")
					.append("\"grpMaxScore\": {").append(" \"max\": {")
					.append("\"script\": \"_score\"").append("}").append("}")
					.append("}").append("}").append("}").append("}");
			Search search = new Search.Builder(stringBuilder.toString())
					.addIndex("appkeyword").addType("appkeyword").build();
			SearchResult result = client.execute(search);
			List<String> appTypeList = new ArrayList<String>();
			if (result.getTotal() > 0) {
				TermsAggregation agg = result.getAggregations()
						.getTermsAggregation("byAppType");
				List<Entry> bucketList = agg.getBuckets();
				for (Entry entry : bucketList) {
					appTypeList.add(entry.getKey());
				}
				jsonObject.put("totalCount", appTypeList.size());
				jsonObject.put("resultList", appTypeList);
			}
		} catch (Exception e) {
			logger.error("搜索失败" + e.getMessage(), e);
			return new ResultJSONObject("fail", "搜索失败");
		} finally {
			client.shutdownClient();
			// jsonObject.put("totalCount", searchresult.size());
			// jsonObject.put("resultList", searchresult);
			return jsonObject;
		}
	}

	@Override
	public String getMerchantIds(String distance, String latitude,
			String longitude, Integer size) {
		StringBuilder ids = new StringBuilder();

		// 初始化Client
		JestHttpClient client = ESFactory.getClient();
		// 初始化返回的值

		JSONObject jsonObject = new ResultJSONObject("000", "搜索成功");
		try {
			StringBuilder stringBuilder = new StringBuilder("{")
					.append("\"size\":").append(size.toString()).append(",")
					.append("\"query\" : {").append("\"match_all\" : {}")
					.append("},").append("\"filter\" : {\"geo_distance\": {")
					.append("\"location\": \"").append(latitude).append(",")
					.append(longitude).append("\",").append("\"distance\": \"")
					.append(distance).append("\"}").append("},")
					.append("\"sort\" : {\"_geo_distance\": {")
					.append("\"location\": \"").append(latitude).append(",")
					.append(longitude).append("\",")
					.append("\"order\": \"asc\",").append("\"unit\": \"m\"}")
					.append("}").append("}");

			CloseableHttpClient httpClient = HttpClients.createDefault();
			try {
				// 用get方法发送http请求
				HttpPost post = new HttpPost(Constant.SEARCH_URL
						+ "/merchantindex/merchantindex/_search");

				StringEntity postEntity = new StringEntity(
						stringBuilder.toString());
				// postEntity.setContentEncoding("UTF-8");
				// postEntity.setContentType("application/x-www-form-urlencoded");
				post.setEntity(postEntity);

				CloseableHttpResponse httpResponse = null;
				// 发送get请求
				httpResponse = httpClient.execute(post);
				try {
					// response实体
					HttpEntity entity = httpResponse.getEntity();
					if (null != entity) {
						if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
							String response = EntityUtils.toString(entity,
									"utf8");
							JSONObject resObject = JSON.parseObject(response);
							Long total = resObject.getJSONObject("hits")
									.getLong("total");
							JSONArray merchants = resObject.getJSONObject(
									"hits").getJSONArray("hits");
							if (total > 0) {
								for (int i = 0; i < merchants.size(); i++) {
									JSONObject token = (JSONObject) merchants
											.get(i);
									ids.append(",").append(
											token.getString("_id"));
								}
							}
						}
					}
				} finally {
					httpResponse.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			logger.error("搜索失败" + e.getMessage(), e);
		} finally {
			client.shutdownClient();
			if (ids.length() > 1) {
				// 删除前置,
				ids.deleteCharAt(0);
			}
			return ids.toString();
		}
	}

	@Override
	public JSONObject updateDocument(String id, String name, Double latitude,
			Double longitude) throws Exception {
		JSONObject result = new ResultJSONObject("000", "更新商户位置索引成功");

		JestHttpClient client = ESFactory.getClient();
		MertChantOutline merchant = new MertChantOutline();
		merchant.setId(id);
		merchant.setName(name);
		merchant.setLocation(latitude.toString() + "," + longitude.toString());

		String serviceTypeIds = mertchantDao.getMerchantServiceTypeIds(id);
		if (serviceTypeIds != null) {
			String[] ids = serviceTypeIds.split(",");
			String serviceTypesJsonString = JSON.toJSONString(ids);
			merchant.setServiceTypeIds(serviceTypesJsonString);
		}

		client.execute(new Delete.Builder(merchant.getId())
				.index("merchantindex").type("merchantindex").build());

		Thread.sleep(2 * 1000);

		String indexId = merchant.getId();
		client.execute(new Index.Builder(merchant).index("merchantindex")
				.id(indexId).type("merchantindex").build());
		return result;
	}

	/**
	 * 按服务项目及距离查找附近商家
	 * 
	 * @param longitude
	 *            发单用户的经度
	 * @param latitude
	 *            发单用户的纬度
	 * @param searchRange
	 *            搜素范围
	 * @param serviceTypeId
	 *            服务项目id
	 * @param limit
	 *            结果集尺寸
	 * @return
	 */
	public JSONObject getJsonUserServiceTypeIdNearBy(double longitude,
			double latitude, double searchRange, String serviceTypeId,
			Integer limit) {
		List<Map<String, Object>> userMerchantInfos = new ArrayList<Map<String, Object>>();

		String ids = getMerchantIds(longitude, latitude,
				(Math.round(searchRange) * 1000) + "m", serviceTypeId, limit);
		if (StringUtil.isNotEmpty(ids)) {
			userMerchantInfos = mertchantDao.getUserIdsByMerchantIds(ids);
		}
		JSONObject jsonObject = new ResultJSONObject("000", "按服务id获得用户附近商家成功");
		jsonObject.put("merchantInfo", userMerchantInfos);

		if (userMerchantInfos != null)
			jsonObject.put("totalPage", userMerchantInfos.size());
		else
			jsonObject.put("totalPage", 0);
		return jsonObject;
	}

	@Override
	public List<Map<String, Object>> getUserServiceTypeIdNearBy(
			double longitude, double latitude, double searchRange,
			String serviceTypeId, Integer limit,
			List<Map<String, Object>> allVIPMerchantList) {
		List<Map<String, Object>> userMerchantInfos = null;

		String ids = getMerchantIds(longitude, latitude,
				(Math.round(searchRange) * 1000) + "m", serviceTypeId, limit);
		System.out.println("过滤前ids:" + ids.split(",").length);
		// 过滤vip商户
		for (Map<String, Object> map : allVIPMerchantList) {
			String merchantId = StringUtil.null2Str(map.get("merchantId"));
			if (ids.contains(merchantId + ",")) {
				ids = ids.replace(merchantId + ",", "");
			} else if (ids.contains("," + merchantId)) {
				ids = ids.replace(merchantId, "");
			}
		}

		if (0 < ids.length()) {
			ids = ids.substring(0, ids.length() - 1);
		}
		logger.info("过滤后ids:" + ids.split(",").length + " - " + ids);
		
		if (StringUtil.isNotEmpty(ids)) {
			userMerchantInfos = mertchantDao.getUserIdsByMerchantIds(ids);
		}

		return userMerchantInfos;
	}

	private String getMerchantIds(double longitude, double latitude,
			String distance, String serviceTypeId, Integer limit) {
		StringBuilder ids = new StringBuilder();

		// 初始化Client
		JestHttpClient client = ESFactory.getClient();
		// 初始化返回的值

		JSONObject jsonObject = new ResultJSONObject("000", "搜索成功");
		try {
			StringBuilder stringBuilder = new StringBuilder("{")
					.append("\"size\":").append(limit.toString()).append(",")
					.append("\"query\" : {").append("\"match_all\" : {}")
					.append("},").append("\"filter\" : {\"and\" : [")
					.append("{\"term\" : {\"serviceTypeIds\" : ")
					.append(serviceTypeId).append("}},")
					.append("{\"geo_distance\": {").append("\"location\": \"")
					.append(latitude).append(",").append(longitude)
					.append("\",").append("\"distance\": \"").append(distance)
					.append("\"}").append("}]").append("},")
					.append("\"sort\" : {\"_geo_distance\": {")
					.append("\"location\": \"").append(latitude).append(",")
					.append(longitude).append("\",")
					.append("\"order\": \"asc\",").append("\"unit\": \"m\"}")
					.append("}").append("}");

			CloseableHttpClient httpClient = HttpClients.createDefault();
			try {
				// 用get方法发送http请求
				HttpPost post = new HttpPost(Constant.SEARCH_URL
						+ "/merchantindex/merchantindex/_search");

				StringEntity postEntity = new StringEntity(
						stringBuilder.toString());

				post.setEntity(postEntity);

				CloseableHttpResponse httpResponse = null;
				// 发送get请求
				httpResponse = httpClient.execute(post);
				try {
					// response实体
					HttpEntity entity = httpResponse.getEntity();
					if (null != entity) {
						if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
							String response = EntityUtils.toString(entity,
									"utf8");
							JSONObject resObject = JSON.parseObject(response);
							Long total = resObject.getJSONObject("hits")
									.getLong("total");
							JSONArray merchants = resObject.getJSONObject(
									"hits").getJSONArray("hits");
							if (total > 0) {
								for (int i = 0; i < merchants.size(); i++) {
									JSONObject token = (JSONObject) merchants
											.get(i);
									ids.append(",").append(
											token.getString("_id"));
								}
							}
						}
					}
				} finally {
					httpResponse.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			logger.error("搜索失败" + e.getMessage(), e);
		} finally {
			client.shutdownClient();
			if (ids.length() > 1) {
				// 删除前置,
				ids.deleteCharAt(0);
			}
			return ids.toString();
		}
	}

	@Override
	public JSONObject test_search(String queryString, Boolean isTest,
			String prefix) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param queryString
	 * @return
	 * @author Liuxingwen
	 * @created 2016年10月12日 上午11:46:37
	 * @lastModified
	 * @history
	 */
	@Override
	public int updateUserFeedBackAppKeyWord(Map<String, Object> objMap) {
		try {
			// 查询用户搜索关键词是否反馈记录
			if (this.appKeyWordDao.checkUserFeedBackAppKeyWord(objMap) == 0) {
				// 新增反馈记录
				this.appKeyWordDao.insertUserFeedBackAppKeyWord(objMap);
			} else {
				// 更新反馈记录
				this.appKeyWordDao.updateUserFeedBackAppKeyWord(objMap);
			}
			return 1;
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 *  @param objMap
	 *  @return
	 *  @throws Exception
	 *  @author Liuxingwen
	 *  @created 2016年10月18日 下午4:21:21
	 *  @lastModified      
	 *  @history            
	 */
	@Override
	public JSONObject getrecommendLabel(Map<String, Object> objMap)
			throws Exception {
		// TODO Auto-generated method stub

		JSONObject jsonObject = null;
		List<Map<String, Object>> resultMap = null;
		resultMap=this.appKeyWordDao.selectrecommendLabel(objMap);
		jsonObject = new ResultJSONObject("000", "首页文案推荐获取成功");
		jsonObject.put("labeList", resultMap);
		return jsonObject;
	}
	
	
	
	
	
	
}

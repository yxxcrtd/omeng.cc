package com.shanjin.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.MsgTools;
import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.service.ICommonService;
import com.shanjin.service.IElasticSearchService;
import com.shanjin.service.IMyMerchantService;

@Controller
@RequestMapping("/search")
public class SearchController {

	@Reference
	private IElasticSearchService elasticSearchService;
	private static final Logger logger = Logger
			.getLogger(SearchController.class);

	@Reference
	private IMyMerchantService myMerchantService;

	@Reference
	private ICommonService commonService;

	@Autowired
	private ServletContext servletContext;

	/** 搜索 */
	@RequestMapping("/isearch")
	@SystemControllerLog(description = "搜索")
	public @ResponseBody Object isearch(String queryString,
			HttpServletRequest request) {
		JSONObject jsonObject = null;
		try {
			queryString = StringUtil.null2Str(queryString);
			// 转小写
			queryString = queryString.toLowerCase();
			// 去标点符号
			queryString = queryString.replaceAll("[\\pP‘’“”]", "");

			jsonObject = elasticSearchService.searchAppWords(queryString,
					false, this.getPrefix(request));
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "isearch");

			e.printStackTrace();
			jsonObject = new ResultJSONObject("isearch_exception", "搜索失败");
			logger.error("搜索失败", e);
		}
		return jsonObject;
	}

	/** 搜索反馈 */
	//   /search/feedbackkeyword?queryString=那个什么时候是吧啊&searchnum=10
	@RequestMapping("/feedbackkeyword")
	@SystemControllerLog(description = "搜索反馈")
	public @ResponseBody Object feedbackKeyword(String queryString,String searchnum,
			HttpServletRequest request) {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject();
			queryString = StringUtil.null2Str(queryString);
			// 转小写
			queryString = queryString.toLowerCase();
			// 去标点符号
			queryString = queryString.replaceAll("[\\pP‘’“”]", "");

//			 #{queryString},
//             now(),
//             #{is_del},
//             #{is_audit},
//             #{remark},
//             #{feedbacknum},
//             #{app_key_word_num},
//             #{is_feedbackaudit},
//             #{keywordtype}     
			if((null==searchnum || "".equals(searchnum))||(null==queryString || "".equals(queryString)))
			{
				jsonObject.put("code", "001");
				jsonObject.put("text", "反馈失败,参数为空");
				return jsonObject;
			}
			Map<String, Object> paraMap=new HashMap<String, Object>();
			paraMap.put("queryString", queryString);
			paraMap.put("is_del", 0);//
			paraMap.put("is_audit", 0);
			paraMap.put("remark", "");
			paraMap.put("feedbacknum", 1);
			paraMap.put("app_key_word_num", searchnum);
			paraMap.put("is_feedbackaudit", 0);
			paraMap.put("keywordtype", 2);
			
			int update = elasticSearchService
					.updateUserFeedBackAppKeyWord(paraMap);
//			jsonObject.put("resultCode", update);
			if (update == 1) {
				jsonObject.put("resultCode", "000");
				jsonObject.put("message", "已反馈成功");
			} else {
				jsonObject.put("resultCode", "001");
				jsonObject.put("message", "反馈失败");
			}

		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "feedbackkeyword");
			jsonObject = new JSONObject();
			jsonObject.put("resultCode", "001");
			jsonObject.put("message", "反馈失败");
			e.printStackTrace();
			// jsonObject = new ResultJSONObject("feedbackkeyword_exception",
			// "反馈失败");
			logger.error("反馈失败", e);
		}
		return jsonObject;
	}


	/** 按标签搜索-----企业入驻 */
	@RequestMapping("/isearchForMerchant")
	@SystemControllerLog(description = "商家入驻标签搜索行业")
	public @ResponseBody Object isearchForMerchant(String queryString,
			HttpServletRequest request) {
		JSONObject resultJsonObject = null;

		if (queryString == null || queryString.trim().length() < 1) {
			resultJsonObject = new ResultJSONObject("isearchForMerchant",
					"搜索标签不能为空");
			return resultJsonObject;
		}

		try {
			queryString = StringUtil.null2Str(queryString);
			// 转小写
			queryString = queryString.toLowerCase();
			// 去标点符号
			queryString = queryString.replaceAll("[\\pP‘’“”]", "");

			// JSONObject jsonObject =
			// elasticSearchService.searchAppWords(queryString, false);
			// 改为取分类中最大得分排名的前几个 ---2016.4.22
			// JSONObject jsonObject =
			// elasticSearchService.searchAppWordsByGrpScore(queryString);

			// ---2016.5.3 根据公司特殊的匹配要求，改回先用焕明版本的搜服务测略，再根据appType找分类。
			JSONObject jsonObject = elasticSearchService.searchAppWords(
					queryString, false, this.getPrefix(request));

			if (jsonObject != null && jsonObject.getIntValue("totalCount") > 0) {
				// resultJsonObject=getTopCatalogs(jsonObject);
				// 直接根据别名到分类表中查询 2016.4.22
				resultJsonObject = getCatalogInfo(jsonObject);
			} else {
				resultJsonObject = new ResultJSONObject("000", "商家入驻标签搜索行业");
				resultJsonObject.put("catalogs", new ArrayList());
			}

		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "isearch");
			e.printStackTrace();
			resultJsonObject = new ResultJSONObject("isearchForMerchant",
					"商家入驻标签搜索行业");
			logger.error("搜索失败", e);
		}
		return resultJsonObject;
	}

	/** 按标签搜索-----个人入驻 */
	@RequestMapping("/isearchForPerson")
	@SystemControllerLog(description = "个人入驻标签搜索行业")
	public @ResponseBody Object isearchForPerson(String queryString) {
		JSONObject resultJsonObject = null;

		if (queryString == null || queryString.trim().length() < 1) {
			resultJsonObject = new ResultJSONObject("isearchForMerchant",
					"搜索标签不能为空");
			return resultJsonObject;
		}

		try {
			queryString = StringUtil.null2Str(queryString);
			// 转小写
			queryString = queryString.toLowerCase();
			// 去标点符号
			queryString = queryString.replaceAll("[\\pP‘’“”]", "");
			JSONObject jsonObject = elasticSearchService.searchAppWords(
					queryString, false, "");

			if (jsonObject != null && jsonObject.getIntValue("totalCount") > 0) {
				resultJsonObject = getCustomService(jsonObject);
			} else {
				resultJsonObject = new ResultJSONObject("000", "个人入驻标签搜索行业");
				resultJsonObject.put("service", new ArrayList());
			}

		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "isearch");
			e.printStackTrace();
			resultJsonObject = new ResultJSONObject("isearchForPerson",
					"个人入驻标签搜索行业");
			logger.error("搜索失败", e);
		}
		return resultJsonObject;
	}

	/** 按服务获取附近商家的信息 */
	@RequestMapping("/getMerchantNearBy")
	@SystemControllerLog(description = "按服务获得附近商家")
	public @ResponseBody Object getUserHomePageNearBy(double longitude,
			double latitude, double searchRange, String serviceTypeId,
			Integer limit) {
		JSONObject jsonObject = null;
		try {
			jsonObject = elasticSearchService.getJsonUserServiceTypeIdNearBy(
					longitude, latitude, searchRange, serviceTypeId, limit);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getUserHomePageNearBy");
			logger.error("获取附近商户错误", e);
			jsonObject = new ResultJSONObject("fail", "获取附近商户错误");
		}
		return jsonObject;
	}

	/** 搜索 */
	@RequestMapping("/isearchTest")
	@SystemControllerLog(description = "搜索关键词测试")
	public @ResponseBody Object searchtest(String queryString) {
		JSONObject jsonObject = null;
		try {
			// 转小写
			queryString = queryString.toLowerCase();
			// 去标点符号
			queryString = queryString.replaceAll("[\\pP‘’“”]", "");
			jsonObject = elasticSearchService.searchAppWords(queryString, true,
					"");
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "isearchTest");

			e.printStackTrace();
			jsonObject = new ResultJSONObject("search_exception", "搜索关键词测试失败");
			logger.error("搜索关键词测试失败", e);
		}
		return jsonObject;
	}

	/** 创建关键词搜索的索引 */
	@RequestMapping("/icreateIndex")
	@SystemControllerLog(description = "创建关键词搜索的索引")
	public @ResponseBody Object icreateIndex() {
		JSONObject jsonObject = null;
		try {
			jsonObject = elasticSearchService.createAppWordsIndex();
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "icreateIndex");

			e.printStackTrace();
			jsonObject = new ResultJSONObject("icreateIndex_exception",
					"创建关键词搜索的索引失败");
			logger.error("创建关键词搜索的索引失败", e);
		}
		return jsonObject;
	}

	/** 重建关键词搜索的索引 */
	@RequestMapping("/rebuildAppWordsIndex")
	@SystemControllerLog(description = "重建关键词搜索的索引")
	public @ResponseBody Object rebuildAppWordsIndex() {
		JSONObject jsonObject = null;
		try {
			elasticSearchService.delIndex("appkeyword");
			Thread.sleep(2000);
			elasticSearchService.createAppWordsIndex();
			jsonObject = new ResultJSONObject("000", "重建关键词搜索的索引成功");
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "rebuildAppWordsIndex");

			e.printStackTrace();
			jsonObject = new ResultJSONObject(
					"icreate_appkeyword_index_exception", "重建关键词搜索的索引失败");
			logger.error("重建关键词搜索的索引失败", e);
		}
		return jsonObject;
	}

	/** 重建商户信息搜索的索引 */
	@RequestMapping("/rebuildMerchantIndex")
	@SystemControllerLog(description = "重建关键词搜索的索引")
	public @ResponseBody Object rebuildMerchantIndex() {
		JSONObject jsonObject = null;
		try {
			elasticSearchService.delIndex("merchantindex");
			Thread.sleep(2000);
			elasticSearchService.iCreateMerchantIndex();
			jsonObject = new ResultJSONObject("000", "重建商户位置搜索的索引成功");
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "rebuildMerchantIndex");

			e.printStackTrace();
			jsonObject = new ResultJSONObject("rebuildMerchantIndex_exception",
					"重建商户位置搜索的索引失败");
			logger.error("重建商户位置搜索的索引失败", e);
		}
		return jsonObject;
	}

	/** 创建商户位置搜索的索引 */
	@RequestMapping("/icreateMerchantIndex")
	@SystemControllerLog(description = "创建商户位置搜索的索引")
	public @ResponseBody Object icreateMerchantIndex() {
		JSONObject jsonObject = null;
		try {
			jsonObject = elasticSearchService.iCreateMerchantIndex();
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "icreateMerchantIndex");

			e.printStackTrace();
			jsonObject = new ResultJSONObject("icreateIndex_exception",
					"创建商户位置搜索的索引失败");
			logger.error("创建商户位置搜索的索引失败", e);
		}
		return jsonObject;
	}

	/** 删除索引 */
	@RequestMapping("/delIndex")
	@SystemControllerLog(description = "删除索引")
	public @ResponseBody Object delIndex(String indexName) {
		JSONObject jsonObject = null;
		try {
			jsonObject = elasticSearchService.delIndex(indexName);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "delIndex");

			jsonObject = new ResultJSONObject("icreateIndex_exception",
					"删除索引失败");
			logger.error("删除索引失败", e);
		}
		return jsonObject;
	}

	/** 搜索 */
	@RequestMapping("/isearchGxfw")
	@SystemControllerLog(description = "搜索GXFW")
	public @ResponseBody Object isearchGxfw(String queryString) {
		JSONObject jsonObject = null;
		try {
			queryString = StringUtil.null2Str(queryString);
			// 转小写
			queryString = queryString.toLowerCase();
			// 去标点符号
			queryString = queryString.replaceAll("[\\pP‘’“”]", "");
			jsonObject = elasticSearchService.iGxfwSearch(queryString, false);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "isearchGxfw");

			jsonObject = new ResultJSONObject("isearchGxfw_exception",
					"搜索GXFW失败");
			logger.error("搜索GXFW失败", e);
		}
		return jsonObject;
	}

	/** 搜索 */
	@RequestMapping("/isearchMerchantId")
	@SystemControllerLog(description = "搜索GXFW")
	public @ResponseBody Object isearchMerchantId(String queryString) {
		JSONObject jsonObject = null;
		List<Map> merchantIds = elasticSearchService
				.iGxfwSearchMerchantId(queryString);
		if (merchantIds != null && merchantIds.size() > 0) {
			System.out.println("merchantIds:" + merchantIds);
			jsonObject = new ResultJSONObject("000", "搜索商户ID成功");
			jsonObject.put("merchantIds", merchantIds);
		} else {
			jsonObject = new ResultJSONObject("isearchGxfw_exception",
					"搜索GXFW失败");
		}
		return jsonObject;
	}

	/** 搜索 */
	@RequestMapping("/testSearchMerchantId")
	@SystemControllerLog(description = "测试搜索GXFW")
	public @ResponseBody Object testSearchMerchantId(String queryString) {
		elasticSearchService.testSearchMerchantId(queryString);
		return new ResultJSONObject("000", "测试搜索GXFW");
	}

	/** 创建个性服务的索引 */
	@RequestMapping("/icreateGxfwIndex")
	@SystemControllerLog(description = "创建个性服务的索引")
	public @ResponseBody Object icreateGxfwIndex(String queryString) {
		JSONObject jsonObject = null;
		try {
			jsonObject = elasticSearchService.iCreateGxfwIndex();
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "icreateGxfwIndex");

			jsonObject = new ResultJSONObject("icreateGxfwIndex_exception",
					"创建个性服务的索引失败");
			logger.error("创建个性服务的索引失败", e);
		}
		return jsonObject;
	}

	/** 重建个性服务搜索的索引 */
	@RequestMapping("/rebuildGxfwIndex")
	@SystemControllerLog(description = "重建个性服务搜索的索引")
	public @ResponseBody Object rebuildGxfwIndex() {
		JSONObject jsonObject = null;
		try {
			elasticSearchService.delIndex("gxfwindex");
			Thread.sleep(2000);
			elasticSearchService.iCreateGxfwIndex();
			jsonObject = new ResultJSONObject("000", "重建个性服务搜索的索引成功");
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "rebuildGxfwIndex");

			jsonObject = new ResultJSONObject("icreate_gxfw_index_exception",
					"重建个性服务搜索的索引失败");
			logger.error("重建个性服务搜索的索引失败", e);
		}
		return jsonObject;
	}

	/** 添加索引文档 */
	@RequestMapping("/addDocument")
	@SystemControllerLog(description = "添加索引文档")
	public @ResponseBody Object addDocument(String ids, String indexName) {
		JSONObject jsonObject = null;
		try {
			jsonObject = elasticSearchService.addDocument(ids, indexName);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "addDocument");

			jsonObject = new ResultJSONObject("000", "添加索引文档失败");
			logger.error("添加索引文档失败", e);
		}
		return jsonObject;
	}

	/** 删除索引文档 */
	@RequestMapping("/delDocument")
	@SystemControllerLog(description = "删除索引文档")
	public @ResponseBody Object delDocument(String ids, String indexName) {
		JSONObject jsonObject = null;
		try {
			jsonObject = elasticSearchService.delDocument(ids, indexName);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "delDocument");

			jsonObject = new ResultJSONObject("icreateIndex_exception",
					"删除索引文档失败");
			logger.error("删除索引文档失败", e);
		}
		return jsonObject;
	}

	/** 更新索引文档 */
	@RequestMapping("/updateDocument")
	@SystemControllerLog(description = "更新索引文档")
	public @ResponseBody Object updateDocument(String ids, String indexName) {
		JSONObject jsonObject = null;
		try {
			elasticSearchService.delDocument(ids, indexName);
			Thread.sleep(2000);
			elasticSearchService.addDocument(ids, indexName);
			jsonObject = new ResultJSONObject("000", "更新索引文档成功");
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "updateDocument");

			jsonObject = new ResultJSONObject("update_document_exception",
					"更新索引文档失败");
			logger.error("更新索引文档失败", e);
		}
		return jsonObject;
	}

	/**
	 * 获取自定义词库
	 * 
	 * @throws IOException
	 */
	@RequestMapping(value = "/getAppKeyWordDict")
	@SystemControllerLog(description = "获取自定义词库 ")
	public @ResponseBody String getAppKeyWordDict(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String result = "";

		StringBuilder sb = new StringBuilder();
		Integer wordListCount = elasticSearchService.getAppKeyWordDictCount();

		String eTag = request.getHeader("If-None-Match");
		String modified = request.getHeader("If-Modified-Since");

		// 设置头
		if (!StringUtil.isNotEmpty(modified)) {
			// 如果没有，则使用当前时间
			modified = System.currentTimeMillis() + "";
		}

		// 设置头信息。
		String newTag = wordListCount + "";
		response.setHeader("Last-Modified", modified);
		response.setHeader("ETag", wordListCount + "");
		response.setHeader("Connection", "gzip,deflate");
		response.setCharacterEncoding("UTF-8");
		if (!newTag.equals(eTag)) {
			List<String> wordList = elasticSearchService.getAppKeyWordDict();// 获取所有分词，这里可以改进使用缓存等。
			// 拼装结果
			for (String tempWord : wordList) {
				sb.append(tempWord);
				sb.append("\r\n");
			}
			result = sb.toString();
			// 更新时间
			response.setHeader("Last-Modified", System.currentTimeMillis() + "");
		}
		return result;
	}

	/**
	 * 获取自定义词库
	 * 
	 * @throws IOException
	 */
	@RequestMapping(value = "/getCustomDict")
	@SystemControllerLog(description = "获取自定义词库 ")
	public @ResponseBody String getCustomDict(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String result = "";

		StringBuilder sb = new StringBuilder();
		Integer wordListCount = elasticSearchService.getCustomDictCount();

		String eTag = request.getHeader("If-None-Match");
		String modified = request.getHeader("If-Modified-Since");

		// 设置头
		if (!StringUtil.isNotEmpty(modified)) {
			// 如果没有，则使用当前时间
			modified = System.currentTimeMillis() + "";
		}

		// 设置头信息。
		String newTag = wordListCount + "";
		response.setHeader("Last-Modified", modified);
		response.setHeader("ETag", wordListCount + "");
		response.setHeader("Connection", "gzip,deflate");
		response.setCharacterEncoding("UTF-8");
		if (!newTag.equals(eTag)) {
			List<String> wordList = elasticSearchService.getCustomDict();// 获取所有分词，这里可以改进使用缓存等。
			// 拼装结果
			for (String tempWord : wordList) {
				sb.append(tempWord);
				sb.append("\r\n");
			}
			result = sb.toString();
			// 更新时间
			response.setHeader("Last-Modified", System.currentTimeMillis() + "");
		}
		return result;
	}

	/** 获取自定义停词库 */
	@RequestMapping("/getCustomStopDict")
	@SystemControllerLog(description = "获取自定义停词库 ")
	public @ResponseBody String getCustomStopDict(HttpServletRequest request,
			HttpServletResponse response) {
		String result = "";

		StringBuilder sb = new StringBuilder();
		Integer wordListCount = elasticSearchService.getCustomStopDictCount();

		String eTag = request.getHeader("If-None-Match");
		String modified = request.getHeader("If-Modified-Since");

		// 设置头
		if (!StringUtil.isNotEmpty(modified)) {
			// 如果没有，则使用当前时间
			modified = System.currentTimeMillis() + "";
		}

		// 设置头信息。
		String newTag = wordListCount + "";

		response.setHeader("Last-Modified", modified);
		response.setHeader("ETag", wordListCount + "");
		response.setHeader("Connection", "gzip,deflate");
		response.setCharacterEncoding("UTF-8");
		if (!newTag.equals(eTag)) {
			List<String> wordList = elasticSearchService.getCustomStopDict();// 获取所有分词，这里可以改进使用缓存等。
			// 拼装结果
			for (String tempWord : wordList) {
				sb.append(tempWord);
				sb.append("\r\n");
			}
			result = sb.toString();
			// 更新时间
			response.setHeader("Last-Modified", System.currentTimeMillis() + "");
		}
		return result;
	}

	/** 搜索 */
	@RequestMapping("/isearchByGrp")
	@SystemControllerLog(description = "搜索---按组最高得分返回结果")
	public @ResponseBody Object isearchByGrp(String queryString) {
		JSONObject jsonObject = null;
		try {
			queryString = StringUtil.null2Str(queryString);
			// 转小写
			queryString = queryString.toLowerCase();
			// 去标点符号
			queryString = queryString.replaceAll("[\\pP‘’“”]", "");
			jsonObject = elasticSearchService
					.searchAppWordsByGrpScore(queryString);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "isearch");

			e.printStackTrace();
			jsonObject = new ResultJSONObject("isearch_exception", "搜索失败");
			logger.error("搜索失败", e);
		}
		return jsonObject;
	}

	/** 搜索 */
	@RequestMapping("/testUpdateMerchantIndex")
	@SystemControllerLog(description = "测试---跟新商店位置信息索引")
	public @ResponseBody Object updateMerchantIndex(String id, String name,
			Double latitude, Double longitude) {
		JSONObject jsonObject = null;
		try {

			jsonObject = elasticSearchService.updateDocument(id, name,
					latitude, longitude);

		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "isearch");

			e.printStackTrace();
			jsonObject = new ResultJSONObject("isearch_exception", "搜索失败");
			logger.error("搜索失败", e);
		}
		return jsonObject;
	}

	// 过滤掉同行业，返回行业列表---------基于李焕明搜索引擎的版本00
	private JSONObject getTopCatalogs(JSONObject searchResult) {
		JSONObject resultJson = new ResultJSONObject("000", "根据关键字搜索行业成功");

		JSONArray searchList = searchResult.getJSONArray("resultList");

		List<Map<String, Object>> catalogs = new ArrayList<Map<String, Object>>();

		// 搜索引擎返回空结果
		if (searchList != null && searchList.size() > 0) {
			String serviceIds = findServiceIds(searchList);
			catalogs = commonService.getCatalogInfo(serviceIds);

			List<Map<String, Object>> catalogByApps = findCatalogs(searchList);
			if (catalogByApps != null && catalogByApps.size() > 0) {
				if (catalogs == null) {
					catalogs = new ArrayList();
				}
				for (Map<String, Object> item : catalogByApps) {
					catalogs.add(item);
				}
			}
		}
		BusinessUtil.disposeManyPath(catalogs, "iconPath");
		BusinessUtil.disposeManyPath(catalogs, "bigIconPath");
		resultJson.put("catalogs", catalogs);
		return resultJson;

	}

	// 根据搜索引擎返回的分类别名列表，返回分类信息。
	private JSONObject getCatalogInfo(JSONObject searchResult) {
		JSONObject resultJson = new ResultJSONObject("000", "根据关键字搜索行业成功");
		JSONArray searchList = searchResult.getJSONArray("resultList");

		List<Map<String, Object>> catalogs = new ArrayList<Map<String, Object>>();

		// 搜索引擎返回空结果
		if (searchList != null && searchList.size() > 0) {
			int length = searchList.size();
			StringBuilder appTypes = new StringBuilder("");
			for (int i = 0; i < length; i++) {
				String appType = searchList.getJSONObject(i).getString(
						"appType");
				if (appType.equals("gxfw")) {
					continue;
				}
				appTypes.append(",\'").append(appType).append("\'");
			}
			if (appTypes.length() > 1) {
				appTypes.deleteCharAt(0);
				catalogs = commonService.getCatalogsByAppType(appTypes
						.toString());
			}
		}
		BusinessUtil.disposeManyPath(catalogs, "iconPath");
		BusinessUtil.disposeManyPath(catalogs, "bigIconPath");
		resultJson.put("catalogs", catalogs);
		return resultJson;
	}

	// 返回个性化的服务列表
	private JSONObject getCustomService(JSONObject searchResult) {
		JSONObject resultJson = new ResultJSONObject("000", "根据关键字搜索个性化服务列表成功");

		JSONArray searchList = searchResult.getJSONArray("resultList");

		List<Map<String, Object>> services = new ArrayList<Map<String, Object>>();

		// 搜索引擎返回空结果
		if (searchList != null && searchList.size() > 0) {
			String serviceIds = findServiceIds(searchList);

			services = commonService.getSearchedCustomServiceInfo(serviceIds);
		}
		resultJson.put("services", services);
		return resultJson;

	}

	// 从搜索引擎返回的结果中生成appType 字符串，以，分隔
	private String extractAppTypes(JSONArray searchList) {
		String appTypesx;
		Map<String, String> appMap = new HashMap<String, String>();
		int length = searchList.size();
		for (int i = 0; i < length; i++) {
			JSONObject jsonObject = searchList.getJSONObject(i);
			String orginalAppType = jsonObject.getString("appType");
			if (!appMap.containsKey(orginalAppType)) {
				appMap.put(orginalAppType, orginalAppType);
			}
		}
		StringBuffer appTypes = new StringBuffer();
		Set<Entry<String, String>> entries = appMap.entrySet();
		for (Entry<String, String> entry : entries) {
			appTypes.append(",'").append(entry.getKey()).append("'");
		}
		appTypes.deleteCharAt(0);

		return appTypes.toString();
	}

	private String findServiceIds(JSONArray searchList) {
		int length = searchList.size();
		List<Map<String, Object>> searchConditions = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < length; i++) {
			JSONObject each = searchList.getJSONObject(i);
			Map<String, Object> eachCondtion = new HashMap<String, Object>();
			eachCondtion.put("serviceTypeId", each.getInteger("serviceType"));
			eachCondtion.put("appType", each.getString("appType"));
			searchConditions.add(eachCondtion);
		}
		return commonService.getServiceIds(searchConditions);
	}

	private List<Map<String, Object>> findCatalogs(JSONArray searchList) {
		int length = searchList.size();
		StringBuilder appTypes = new StringBuilder("");
		for (int i = 0; i < length; i++) {
			JSONObject each = searchList.getJSONObject(i);
			String appType = each.getString("appType");
			if (appType.equals("ams_szx") || appType.indexOf("yxt_") > -1) {
				appTypes.append(",\'").append(appType).append("\'");
			}
		}
		if (appTypes.length() > 1) {
			appTypes.deleteCharAt(0);
			return commonService.getCatalogsByAppType(appTypes.toString());
		} else {
			return new ArrayList<Map<String, Object>>();
		}
	}

	/** 搜索 */
	@RequestMapping("/test_search")
	@SystemControllerLog(description = "搜索")
	public @ResponseBody Object test_search(String queryString,
			HttpServletRequest request) {
		JSONObject jsonObject = null;
		try {
			queryString = StringUtil.null2Str(queryString);
			// 转小写
			queryString = queryString.toLowerCase();
			// 去标点符号
			queryString = queryString.replaceAll("[\\pP‘’“”]", "");
			jsonObject = elasticSearchService.test_search(queryString, false,
					this.getPrefix(request));
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "isearch");

			e.printStackTrace();
			jsonObject = new ResultJSONObject("isearch_exception", "搜索失败");
			logger.error("搜索失败", e);
		}
		return jsonObject;
	}

	private String getPrefix(HttpServletRequest request) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("http://").append(request.getServerName()).append(":")
				.append(request.getServerPort())
				.append(request.getContextPath());
		buffer.append("/customOrder/getOrderForm?serviceId=");
		return buffer.toString();

	}
	
	
	
	/** 首页推荐文案 */
	//   /search/recommendlabel?lines=3
	@RequestMapping("/recommendlabel")
	@SystemControllerLog(description = "首页推荐文案")
	public @ResponseBody Object recommendLabel(int lines) {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject();
			if(lines==0)
				lines=1;
			Map<String, Object> paraMap=new HashMap<String, Object>();
			paraMap.put("lines", lines);
			
			
			jsonObject = elasticSearchService
					.getrecommendLabel(paraMap);
			

		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "recommendlabel");
			jsonObject = new JSONObject();
			jsonObject.put("resultCode", "001");
			jsonObject.put("message", "失败");
			e.printStackTrace();
			// jsonObject = new ResultJSONObject("feedbackkeyword_exception",
			// "反馈失败");
			logger.error("首页推荐文案失败", e);
		}
		return jsonObject;
	}

}

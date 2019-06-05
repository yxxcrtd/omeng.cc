package com.shanjin.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.MsgTools;
import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.service.IElasticSearchService;

@Controller
@RequestMapping("/testSearch")
public class TestSearchController {

	@Reference
	private IElasticSearchService elasticSearchService;

	private static final Logger logger = Logger.getLogger(TestSearchController.class);

	/** 搜索 */
	@RequestMapping("/isearch")
	@SystemControllerLog(description = "搜索关键词")
	public @ResponseBody Object search(String queryString) {
		JSONObject jsonObject = null;
		try {
			jsonObject = elasticSearchService.searchAppWords(queryString, false,"");
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e,"isearch");
			jsonObject = new ResultJSONObject("search_exception", "搜索关键词失败");
			logger.error("搜索关键词失败", e);
		}
		return jsonObject;
	}

	/** 创建索引 */
	@RequestMapping("/icreateIndex")
	public @ResponseBody Object createIndex() {
		System.out.println("去创建索引");
		JSONObject jsonObject = null;
		try {
			jsonObject = elasticSearchService.createAppWordsIndex();
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e,"icreateIndex");
			
			jsonObject = new ResultJSONObject("createIndex_exception", "创建搜索关键词索引失败");
			logger.error("创建搜索关键词索引失败", e);
		}
		return jsonObject;
	}

	/** 搜索 */
	@RequestMapping("/isearchtest")
	@SystemControllerLog(description = "搜索关键词测试")
	public @ResponseBody Object searchtest(String queryString) {
		JSONObject jsonObject = null;
		try {
			jsonObject = elasticSearchService.searchAppWords(queryString, true,"");
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e,"isearchtest");
			
			jsonObject = new ResultJSONObject("search_exception", "搜索关键词测试失败");
			logger.error("搜索关键词测试失败", e);
		}
		return jsonObject;
	}

	/** 搜索 */
	@RequestMapping("/isearchGxfw")
	@SystemControllerLog(description = "搜索GXFW")
	public @ResponseBody Object isearchGxfw(String queryString) {
		System.out.println("搜索GXFW");
		JSONObject jsonObject = null;
		try {
			jsonObject = elasticSearchService.iGxfwSearch(queryString, false);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e,"isearchGxfw");
			
			jsonObject = new ResultJSONObject("isearchGxfw_exception", "搜索GXFW失败");
			logger.error("搜索GXFW失败", e);
		}
		return jsonObject;
	}

	/** 测试搜索 */
	@RequestMapping("/isearchGxfwtest")
	@SystemControllerLog(description = "测试搜索GXFW")
	public @ResponseBody Object isearchGxfwtest(String queryString) {
		JSONObject jsonObject = null;
		try {
			jsonObject = elasticSearchService.iGxfwSearch(queryString, true);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e,"isearchGxfwtest");
			jsonObject = new ResultJSONObject("isearchGxfw_exception", "测试搜索GXFW失败");
			logger.error("测试搜索GXFW失败", e);
		}
		return jsonObject;
	}

	/** 创建个性服务的索引 */
	@RequestMapping("/icreateGxfwIndex")
	@SystemControllerLog(description = "创建个性服务的索引")
	public @ResponseBody Object icreateGxfwIndex(String queryString) {
		System.out.println("创建个性服务的索引");
		JSONObject jsonObject = null;
		try {
			jsonObject = elasticSearchService.iCreateGxfwIndex();
		} catch (Exception e) {
			
			MsgTools.sendMsgOrIgnore(e,"icreateGxfwIndex");
			
			jsonObject = new ResultJSONObject("icreateGxfwIndex_exception", "创建个性服务的索引失败");
			logger.error("创建个性服务的索引失败", e);
		}
		return jsonObject;
	}

}

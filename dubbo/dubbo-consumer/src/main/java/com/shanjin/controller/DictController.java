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
import com.shanjin.service.ICommonService;
import com.shanjin.service.IDictionaryService;

/**
 * 字典数据 业务控制器
 * 
 * @author 李焕民
 * @version 2015-4-5
 *
 */

@Controller
@RequestMapping("/dictInfo")
public class DictController {
	@Reference
	private IDictionaryService dictionaryService;

	@Reference
	private ICommonService commonService;

	private Logger logger = Logger.getLogger(DictController.class);
	/** 获取城市信息 */
	@RequestMapping("/getCitys")
	@SystemControllerLog(description = "获取城市信息")
	public @ResponseBody Object getCitys(Long parentId) {
		JSONObject jsonObject = null;
		try {
			jsonObject= this.commonService.getCity(parentId);
		} catch (Exception e) {
			
			MsgTools.sendMsgOrIgnore(e,"getCitys");
			
			jsonObject = new ResultJSONObject("getCitys_exception", "获取城市信息失败");
			logger.error("获取城市信息失败", e);	
		}
		return jsonObject;
	}

	/** 获取地区信息列表，供移动端一次性选择城市 */
	@RequestMapping("/getAreaList")
	@SystemControllerLog(description = "获取地区列表")
	public @ResponseBody Object getAreaList() {
		JSONObject jsonObject = null;
		try {
			jsonObject= this.commonService.getAreaList();
		} catch (Exception e) {
			
			MsgTools.sendMsgOrIgnore(e,"getAreaList");
			
			jsonObject = new ResultJSONObject("getAreaList_exception", "获取地区信息列表，供移动端一次性选择城市失败");
			logger.error("获取地区信息列表，供移动端一次性选择城市失败", e);	
		}
		return jsonObject;
	}
	/** 获取城市列表 */
	@RequestMapping("/getAllCitys")
	@SystemControllerLog(description = "获取城市列表")
	public @ResponseBody Object getAllCitys() {
		JSONObject jsonObject = null;
		try {
			jsonObject= this.commonService.getAllCitys();
		} catch (Exception e) {
			
			MsgTools.sendMsgOrIgnore(e,"getAllCitys");
			
			jsonObject = new ResultJSONObject("getAllCitys_exception", "获取城市列表失败");
			logger.error("获取城市列表失败", e);	
		}
		return jsonObject;
	}
	/** 获取多级字典信息 */
	@RequestMapping("/getMultistageDict")
	@SystemControllerLog(description = "获取多级字典信息")
	public @ResponseBody Object getMultistageDict(String appType,String dictType ) {
		JSONObject jsonObject = null;
		try {
			jsonObject= this.dictionaryService.getMultistageDict(appType,dictType );
		} catch (Exception e) {
			
			MsgTools.sendMsgOrIgnore(e,"getMultistageDict");
			
			jsonObject = new ResultJSONObject("getMultistageDict_exception", "获取多级字典信息失败");
			logger.error("获取多级字典信息失败", e);	
		}
		return jsonObject;
	}
	/** 获取字典信息 */
	@RequestMapping("/getDicts")
	@SystemControllerLog(description = "获取字典信息")
	public @ResponseBody Object getDicts(String dictType, Long parentId) {
		JSONObject jsonObject = null;
		try {
			jsonObject= this.dictionaryService.getDict(dictType, parentId);
		} catch (Exception e) {
			
			MsgTools.sendMsgOrIgnore(e,"getDicts");
			
			jsonObject = new ResultJSONObject("getDicts_exception", "获取字典信息失败");
			logger.error("获取字典信息失败", e);	
		}
		return jsonObject;
	}

	/** 获取全部汽车型号列表 */
	@RequestMapping("/getUserCarBrandModel")
	@SystemControllerLog(description = "获取全部车辆品牌信息，包含字母索引")
	public @ResponseBody Object getUserCarBrandModel() {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.dictionaryService.getUserCarBrandModel();
		} catch (Exception e) {
			
			MsgTools.sendMsgOrIgnore(e,"getUserCarBrandModel");
			
			jsonObject = new ResultJSONObject("getUserCarBrandModel_exception", "获取全部汽车型号列表失败");
			logger.error("获取全部汽车型号列表失败", e);	
		}
		return jsonObject;
	}
	
	/** 获取表单通用链接列表 */
	@RequestMapping("/getFormDictList")
	@SystemControllerLog(description = "获取表单通用链接列表")
	public @ResponseBody Object getFormDictList(String dictType) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.dictionaryService.getFormDictList(dictType);
		} catch (Exception e) {
			
			MsgTools.sendMsgOrIgnore(e,"getFormDictList");
			
			jsonObject = new ResultJSONObject("getFormDictList_exception", "获取表单通用链接列表失败");
			logger.error("获取表单通用链接列表失败", e);	
		}
		return jsonObject;
	}

	/** 获取数据版本 */
	@RequestMapping("/getDataVersions")
	@SystemControllerLog(description = "获取数据版本")
	public @ResponseBody Object getDataVersions() {
		JSONObject jsonObject = null;
		try {
			jsonObject= this.dictionaryService.getDataVersion();
		} catch (Exception e) {
			
			MsgTools.sendMsgOrIgnore(e,"getDataVersions");
			
			jsonObject = new ResultJSONObject("getDataVersions_exception", "获取数据版本失败");
			logger.error("获取数据版本失败", e);	
		}
		return jsonObject;
	}

	/** 更新数据版本 ，仅限客户端开发者调用 */
	@RequestMapping("/updateDataVersions")
	@SystemControllerLog(description = "更新数据版本 ，仅限客户端开发者调用")
	public @ResponseBody Object updateDataVersions(String dataType) {
		int i = 0;
		try {
			i= this.dictionaryService.updateDataVersion(dataType);
		} catch (Exception e) {
			
			MsgTools.sendMsgOrIgnore(e,"updateDataVersions");
			
			logger.error("更新数据版本 ，仅限客户端开发者调用失败", e);	
		}
		return i;
	}

}

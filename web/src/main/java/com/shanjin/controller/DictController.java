package com.shanjin.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.aspect.SystemControllerLog;
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
	@Resource
	private IDictionaryService dictionaryService;

	@Resource
	private ICommonService commonService;

	/** 获取城市信息 */
	@RequestMapping("/getCitys")
	@SystemControllerLog(description = "获取城市信息")
	public @ResponseBody Object getCitys(Long parentId) {
		return this.commonService.getCity(parentId);
	}

	/** 获取地区信息列表，供移动端一次性选择城市 */
	@RequestMapping("/getAreaList")
	@SystemControllerLog(description = "获取地区列表")
	public @ResponseBody Object getAreaList() {
		return this.commonService.getAreaList();
	}

	/** 获取字典信息 */
	@RequestMapping("/getDicts")
	@SystemControllerLog(description = "获取字典信息")
	public @ResponseBody Object getDicts(String dictType, Long parentId) {
		return this.dictionaryService.getDict(dictType, parentId);
	}

	/** 获取全部汽车型号列表 */
	@RequestMapping("/getUserCarBrandModel")
	@SystemControllerLog(description = "获取全部车辆品牌信息，包含字母索引")
	public @ResponseBody Object getUserCarBrandModel() {
		JSONObject jsonObject = null;
		jsonObject = this.dictionaryService.getUserCarBrandModel();
		return jsonObject;
	}

	/** 获取数据版本 */
	@RequestMapping("/getDataVersions")
	@SystemControllerLog(description = "获取数据版本")
	public @ResponseBody Object getDataVersions() {
		return this.dictionaryService.getDataVersion();
	}

	/** 更新数据版本 ，仅限客户端开发者调用 */
	@RequestMapping("/updateDataVersions")
	@SystemControllerLog(description = "更新数据版本 ，仅限客户端开发者调用")
	public @ResponseBody Object updateDataVersions(String dataType) {
		return this.dictionaryService.updateDataVersion(dataType);
	}
}

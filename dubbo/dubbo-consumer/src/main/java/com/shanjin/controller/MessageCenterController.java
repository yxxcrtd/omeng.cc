package com.shanjin.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.service.IMessageCenterService;

/**
 * 消息中心
 */
@Controller
@RequestMapping("/msgCenter")
public class MessageCenterController {

	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(MessageCenterController.class);
	@Reference
	private IMessageCenterService messageCenterService;

	/**
	 * 消息列表
	 * 
	 * @param appType
	 * @param customerType
	 *            客户ID 1用户，2商户
	 * @param customerId
	 *            客户ID或商户ID
	 * @param isRead
	 *            是否已读 0未读，1已读
	 * @param pageNo
	 * @return
	 * @throws
	 */
	@RequestMapping("/msgList")
	@SystemControllerLog(description = "消息列表")
	public @ResponseBody Object msgList(String appType, int customerType, Long customerId, @RequestParam(required = false) Integer isRead, int pageNo) {

		JSONObject jsonObject = null;
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("customerType", customerType);
			paramMap.put("customerId", customerId);
			paramMap.put("isRead", isRead);
			paramMap.put("pageNo", pageNo);
			jsonObject = this.messageCenterService.msgList(paramMap);
		} catch (Exception e) {
			jsonObject = new ResultJSONObject("msgList_exception", "消息列表异常");
			logger.error("消息列表异常", e);
		}
		return jsonObject;
	}

	/**
	 * 消息标记为已读
	 * 
	 * @param id
	 * @return
	 * @throws
	 */
	@RequestMapping("/markReadMsg")
	@SystemControllerLog(description = "消息标记为已读")
	public @ResponseBody Object markReadMsg(Long id) {

		JSONObject jsonObject = null;
		try {
			jsonObject = this.messageCenterService.markReadMsg(id);
		} catch (Exception e) {
			jsonObject = new ResultJSONObject("markReadMsg_exception", "消息标记为已读异常");
			logger.error("消息标记为已读异常", e);
		}
		return jsonObject;
	}

//	/**
//	 * 查询未读消息数量
//	 * 
//	 * @param appType
//	 * @param customerType
//	 * @param customerId
//	 * @throws
//	 */
//	@RequestMapping("/getUnreadMsgCount")
//	@SystemControllerLog(description = "查询未读消息数量")
//	public @ResponseBody Object getUnreadMsgCount(String appType, int customerType, Long customerId) {
//
//		JSONObject jsonObject = null;
//		try {
//			Map<String, Object> paramMap = new HashMap<String, Object>();
//			paramMap.put("customerType", customerType);
//			paramMap.put("customerId", customerId);
//			jsonObject = this.messageCenterService.getUnreadMsgCount(paramMap);
//		} catch (Exception e) {
//			jsonObject = new ResultJSONObject("getUnreadMsgCount_exception", "查询未读消息数量");
//			logger.error("查询未读消息数量", e);
//		}
//		return jsonObject;
//	}

	/**
	 * 删除消息
	 * 
	 * @param id
	 * @return
	 * @throws
	 */
	@RequestMapping("/delMsg")
	@SystemControllerLog(description = "删除消息")
	public @ResponseBody Object delMsg(String ids, int isRead) {

		JSONObject jsonObject = null;
		try {
			jsonObject = this.messageCenterService.delMsg(ids, isRead);
		} catch (Exception e) {
			jsonObject = new ResultJSONObject("delMsg_exception", "删除消息异常");
			logger.error("删除消息异常", e);
		}
		return jsonObject;
	}

}

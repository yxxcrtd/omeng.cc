package com.shanjin.manager.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shanjin.common.util.StringUtil;
import com.shanjin.manager.Bean.BlackUser;
import com.shanjin.manager.Bean.UserFeedback;
import com.shanjin.manager.Bean.UserInfo;
import com.shanjin.manager.dao.UserInfoDao;
import com.shanjin.manager.dao.impl.UserInfoDaoImpl;
import com.shanjin.manager.service.ExcelExportUtil.Pair;
import com.shanjin.manager.service.IUserInfoService;


public class UserInfoServiceImpl implements IUserInfoService{
	
private UserInfoDao userInfoDao=new UserInfoDaoImpl();

	public List<UserInfo> getUserList(Map<String, String[]> param) {
		List<UserInfo> userList=userInfoDao.getUserList(param);
		return userList;
	}
	public Long total() {
		long total = userInfoDao.total();
		return total;
	}
	public Boolean editUser(Map<String, String[]> param) {
		Boolean flag = userInfoDao.editUser(param);
		return flag;
	}
	public Boolean deleteUser(String id) {
		Boolean flag = userInfoDao.deleteUser(id);
		return flag;
	}
	public Boolean deletetUserForever(String id) {
		Boolean flag=userInfoDao.deletetUserForever(id);
		return flag;
	}
	public List<UserInfo> getRecyUserList(Map<String, String[]> param) {
		List<UserInfo> userList=userInfoDao.getRecyUserList(param);
		return userList;
	}
	public List<UserInfo> exportUserList(Map<String, String[]> param) {
		List<UserInfo> userList=userInfoDao.getExportUserList(param);
		return userList;
	}
	public List<Pair> getExportTitles() {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("id", "编号"));	
		titles.add(new Pair("phone", "手机号"));
		titles.add(new Pair("name", "会员名"));
		titles.add(new Pair("sex", "性别"));
		titles.add(new Pair("user_status", "用户状态"));
		titles.add(new Pair("openMer", "已开店铺数"));
		titles.add(new Pair("focusMer", "关注店铺数"));
		titles.add(new Pair("orderCount", "发单数"));
		titles.add(new Pair("overOrderCount", "服务完成数"));
		titles.add(new Pair("join_time", "注册时间"));
		titles.add(new Pair("last_login_time", "最后登录时间"));
		return titles;
		
	}
	@Override
	public Map<String, Object> getUserDetail(Long userId) {
		return userInfoDao.getUserDetail(userId);
	}
	@Override
	public List<UserFeedback> getFeedBackList(Map<String, String[]> param) {
		return userInfoDao.getFeedBackList(param);
	}
	@Override
	public Boolean deletetUserFeedBack(Map<String, String[]> param) {
		return userInfoDao.deletetUserFeedBack(param);
	}
	@Override
	public List<UserFeedback> userFeedBackExport(Map<String, String[]> param) {
		return userInfoDao.userFeedBackExport(param);
	}
	@Override
	public List<Pair> getuserFeedBackTitles() {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("id", "编号"));	
		titles.add(new Pair("name", "用户名"));
		titles.add(new Pair("phone", "联系方式"));
		titles.add(new Pair("customer_type", "用户类型"));
		titles.add(new Pair("feedback_type", "反馈类型"));
		titles.add(new Pair("content", "内容"));
		titles.add(new Pair("feedback_time", "反馈时间"));
		titles.add(new Pair("status", "反馈状态"));
		return titles;
	}
	@Override
	public Map<String, Object> feedBackDetail(Map<String, String[]> param) {
		Map<String,Object> feedBackDetail =new HashMap<String,Object>();
		feedBackDetail.put("feedBackId", param.get("feedBackId")[0]);
		feedBackDetail.put("phone", StringUtil.null2Str(param.get("phone")[0]));
		feedBackDetail.put("album", param.get("album")[0]);
		Map<String, Object> detail=userInfoDao.getUserFeedBackContent(param.get("feedBackId")[0]);
		feedBackDetail.put("name", StringUtil.null2Str(detail.get("name")));
		feedBackDetail.put("content", StringUtil.null2Str(detail.get("content")));
		return feedBackDetail;
	}
	@Override
	public List<String> feedBackPicList(Long feedBackId) {
		return userInfoDao.feedBackPicList(feedBackId);
	}
	@Override
	public Boolean dealWithFeedBack(String id) {
		return userInfoDao.dealWithFeedBack(id);
	}
	@Override
	public List<BlackUser> getBlackUser(Map<String, String[]> param) {
		List<BlackUser> userList=userInfoDao.getBlackUser(param);
		return userList;
	}
	@Override
	public Boolean deleteBlackUser(Map<String, String[]> param) {
		return userInfoDao.deleteBlackUser(param);
	}
	@Override
	public Boolean addBlackUser(Map<String, String[]> param, String operUserName) {
		return userInfoDao.addBlackUser(param,operUserName);
	}
	@Override
	public Boolean deleteBlackUserByuserId(Map<String, String[]> param) {
		return userInfoDao.deleteBlackUserByuserId(param);
	}
}

package com.shanjin.manager.service;

import java.util.List;
import java.util.Map;

import com.shanjin.manager.Bean.BlackUser;
import com.shanjin.manager.Bean.OrderInfo;
import com.shanjin.manager.Bean.UserFeedback;
import com.shanjin.manager.Bean.UserInfo;
import com.shanjin.manager.service.ExcelExportUtil.Pair;

public interface IUserInfoService {

	List<UserInfo> getUserList(Map<String, String[]> param);

	Long total();

	Boolean editUser(Map<String, String[]> param);

	Boolean deleteUser(String id);

	Boolean deletetUserForever(String id);

	List<UserInfo> getRecyUserList(Map<String, String[]> param);

	List<UserInfo> exportUserList(Map<String, String[]> param);

	List<Pair> getExportTitles();

	Map<String, Object> getUserDetail(Long userId);

	List<UserFeedback> getFeedBackList(Map<String, String[]> param);

	Boolean deletetUserFeedBack(Map<String, String[]> param);

	List<UserFeedback> userFeedBackExport(Map<String, String[]> param);

	List<Pair> getuserFeedBackTitles();

	Map<String, Object> feedBackDetail(Map<String, String[]> param);

	List<String> feedBackPicList(Long feedBackId);

	Boolean dealWithFeedBack(String id);

	List<BlackUser> getBlackUser(Map<String, String[]> param);

	Boolean deleteBlackUser(Map<String, String[]> param);

	Boolean addBlackUser(Map<String, String[]> param, String operUserName);

	Boolean deleteBlackUserByuserId(Map<String, String[]> param);

}

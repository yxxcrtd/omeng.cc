package com.shanjin.manager.dao;

import java.util.List;
import java.util.Map;

import com.shanjin.manager.Bean.BlackUser;
import com.shanjin.manager.Bean.UserFeedback;
import com.shanjin.manager.Bean.UserInfo;

public interface UserInfoDao {
	
	List<UserInfo> getUserList(Map<String, String[]> param);

	long total();

	Boolean deleteUser(String id);

	Boolean editUser(Map<String, String[]> param);

	Boolean deletetUserForever(String id);

	List<UserInfo> getRecyUserList(Map<String, String[]> param);

	List<UserInfo> getExportUserList(Map<String, String[]> param);

	Map<String, Object> getUserDetail(Long userId);

	List<UserFeedback> getFeedBackList(Map<String, String[]> param);

	Boolean deletetUserFeedBack(Map<String, String[]> param);

	List<UserFeedback> userFeedBackExport(Map<String, String[]> param);

	List<String> feedBackPicList(Long feedBackId);

	Map<String, Object> getUserFeedBackContent(String id);

	Boolean dealWithFeedBack(String id);

	List<BlackUser> getBlackUser(Map<String, String[]> param);

	Boolean deleteBlackUser(Map<String, String[]> param);

	Boolean addBlackUser(Map<String, String[]> param, String operUserName);

	Boolean deleteBlackUserByuserId(Map<String, String[]> param);

}

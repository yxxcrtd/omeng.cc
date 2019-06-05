package com.shanjin.manager.dao;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.Agent;
import com.shanjin.manager.Bean.AgentCharge;
import com.shanjin.manager.Bean.AgentEmployee;
import com.shanjin.manager.Bean.AppInfo;
import com.shanjin.manager.Bean.FensiAddRanking;
import com.shanjin.manager.Bean.FensiAddTotal;
import com.shanjin.manager.Bean.MerPhotoStatic;
import com.shanjin.manager.Bean.MerchantsInfo;
import com.shanjin.manager.Bean.OrderRewardAccount;
import com.shanjin.manager.Bean.SystemUserInfo;

public interface ActivityDao {

	List<FensiAddTotal> getDayAddFensi(Map<String, String[]> param);

	List<FensiAddTotal> exportDayAddFensi(Map<String, String[]> param);

	List<FensiAddRanking> getDayFensiRanking(Map<String, String[]> param);

	List<FensiAddRanking> exportFensiAddRanking(Map<String, String[]> param);

	List<MerPhotoStatic> getDayMerPhoto(Map<String, String[]> param);

	List<MerPhotoStatic> exportMerPhotoExcel(Map<String, String[]> param);

	List<Record> getDayMerPhotoDetail(Map<String, String[]> param);

	List<Record> getOrderRewardList(Map<String, String[]> param);

	Boolean saveOrderReward(Map<String, String[]> param);

	Boolean deleteOrderReward(Map<String, String[]> param);

	int editOrderRewardRule(Map<String, String[]> param);

	List<Record> getOrderRewardOpenCity(Map<String, String[]> param);

	boolean addOrderRewardOpenCity(Map<String, String[]> param);

	boolean deleteOrderRewardOpenCity(Map<String, String[]> param);

	List<Record> getOrderRewardOpenService(Map<String, String[]> param);

	boolean addOrderRewardOpenService(Map<String, String[]> param);

	boolean deleteOrderRewardOpenService(Map<String, String[]> param);

	List<Record> getOrderRewardAccountList(Map<String, String[]> param);

	List<OrderRewardAccount> exportOrderRewardAccountExcel(Map<String, String[]> param);

	boolean delOrderReAccount(Map<String, String[]> param, String name);

	List<Record> getPhotimeByMerId(Map<String, String[]> param);

	boolean editOrderRewardAccount(Map<String, String[]> param, String operUserName);

}

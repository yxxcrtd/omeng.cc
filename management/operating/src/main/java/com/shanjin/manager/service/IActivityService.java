package com.shanjin.manager.service;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.FensiAddRanking;
import com.shanjin.manager.Bean.FensiAddTotal;
import com.shanjin.manager.Bean.MerPhotoStatic;
import com.shanjin.manager.Bean.OrderInfo;
import com.shanjin.manager.Bean.OrderRewardAccount;
import com.shanjin.manager.service.ExcelExportUtil.Pair;

public interface IActivityService {

	

	List<FensiAddTotal> getDayAddFensi(Map<String, String[]> param);

	List<FensiAddTotal> exportDayAddFensi(Map<String, String[]> filterParam);

	List<Pair> getExportTitles();

	List<FensiAddRanking> getDayFensiRanking(Map<String, String[]> param);

	List<FensiAddRanking> exportFensiAddRanking(Map<String, String[]> param);

	List<Pair> getExportFensiAddRankingTitles();

	List<MerPhotoStatic> getDayMerPhoto(Map<String, String[]> param);

	List<MerPhotoStatic> exportMerPhotoExcel(Map<String, String[]> param);

	List<Pair> getExportMerPhotoTitles();

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

	List<Pair> getExportOrderRewardAccountTitles();

	boolean delOrderReAccount(Map<String, String[]> param, String operUserName);

	List<Record> getPhotimeByMerId(Map<String, String[]> param);

	boolean editOrderRewardAccount(Map<String, String[]> param, String operUserName);
	
}

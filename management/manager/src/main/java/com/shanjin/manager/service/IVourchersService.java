package com.shanjin.manager.service;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.Voucher;
import com.shanjin.manager.service.ExcelExportUtil.Pair;

public interface IVourchersService {

	List<Voucher> getVouchers(Map<String, String[]> param);

	int addVouchers(Map<String, String[]> param, String resultPath);

	List<Record> getUseVouchers(Map<String, String[]> param);

	List<Record> getIssueVouchers(Map<String, String[]> param);

	boolean IssueVouchers(Map<String, String[]> param);

	List<Voucher> getPriceByVoucherType(String app_type, String coupons_type);

	List<Voucher> getCountByPrice(String app_type, String coupons_type,
			String price);

	boolean deleteVouchers(Map<String, String[]> param);

	boolean deleteVoucherIssue(Map<String, String[]> param);

	List<Record> getVouchersByMerId(Map<String, String[]> param);

	List<Voucher> getVoucherExportList(Map<String, String[]> param);

	List<Pair> getExportTitles();

	boolean startOrstopVouchers(Map<String, String[]> param);

	int editVouchers(Map<String, String[]> param, String resultPath);

	List<Voucher> getCanIsuseVouchers(Map<String, String[]> param);

}

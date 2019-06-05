package com.shanjin.manager.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.Voucher;
import com.shanjin.manager.dao.VourchersDao;
import com.shanjin.manager.dao.impl.VourchersDaoImpl;
import com.shanjin.manager.service.ExcelExportUtil.Pair;
import com.shanjin.manager.service.IVourchersService;

public class VourchersServiceImpl implements IVourchersService {
private VourchersDao vourchersDao=new VourchersDaoImpl();
	public List<Voucher> getVouchers(Map<String, String[]> param) {
		List<Voucher> vouchersList = vourchersDao.getVouchers(param);
		return vouchersList;
	}
	public int addVouchers(Map<String, String[]> param, String resultPath) {
		int flag=vourchersDao.addVouchers(param,resultPath);
		return flag;
	}
	public List<Record> getUseVouchers(Map<String, String[]> param) {
		List<Record> uservouchersList = vourchersDao.getUseVouchers(param);
		return uservouchersList;
	}
	public List<Record> getIssueVouchers(Map<String, String[]> param) {
		List<Record> issueVouchersList = vourchersDao.getIssueVouchers(param);
		return issueVouchersList;
	}
	public boolean IssueVouchers(Map<String, String[]> param) {
		boolean flag=vourchersDao.IssueVouchers(param);
		return flag;
	}
	public List<Voucher> getPriceByVoucherType(String app_type,
			String coupons_type) {
		List<Voucher> priceList= vourchersDao.getPriceByVoucherType(app_type,coupons_type);
		return priceList;
	}
	public List<Voucher> getCountByPrice(String app_type, String coupons_type,
			String price) {
		List<Voucher> countList= vourchersDao.getCountByPrice(app_type,coupons_type,price);
		return countList;
	}
	public boolean deleteVouchers(Map<String, String[]> param) {
		boolean flag=vourchersDao.deleteVouchers(param);
		return flag;
	}
	public boolean deleteVoucherIssue(Map<String, String[]> param) {
		boolean flag=vourchersDao.deleteVoucherIssue(param);
		return flag;
	}
	public List<Record> getVouchersByMerId(Map<String, String[]> param) {
		List<Record> vouchers= vourchersDao.getVouchersByMerId(param);
		return vouchers;
	}
	public List<Voucher> getVoucherExportList(Map<String, String[]> param) {
		List<Voucher> vouchersList = vourchersDao.getVoucherExportList(param);
		return vouchersList;
	}
	public List<Pair> getExportTitles() {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("coupons_type_name", "代金券类型"));
		titles.add(new Pair("price", "代金券金额"));
		titles.add(new Pair("cutoff_time", "截止时间"));
		titles.add(new Pair("is_del", "状态"));
		return titles;
	}
	public boolean startOrstopVouchers(Map<String, String[]> param) {
		boolean flag=vourchersDao.startOrstopVouchers(param);
		return flag;
		
	}
	@Override
	public int editVouchers(Map<String, String[]> param, String resultPath) {
		int flag=vourchersDao.editVouchers(param,resultPath);
		return flag;
	}
	@Override
	public List<Voucher> getCanIsuseVouchers(Map<String, String[]> param) {
		List<Voucher> vouchersList = vourchersDao.getCanIsuseVouchers(param);
		return vouchersList;
	}

}

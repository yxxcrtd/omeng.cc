package com.shanjin.manager.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.MerchantServiceTag;
import com.shanjin.manager.dao.ServiceTagDao;
import com.shanjin.manager.dao.impl.ServiceTagDaoImpl;
import com.shanjin.manager.service.ExcelExportUtil.Pair;
import com.shanjin.manager.service.IServiceTagService;

public class ServiceTagServiceImpl implements IServiceTagService{

	ServiceTagDao serviceTagDao = new ServiceTagDaoImpl();

	@Override
	public List<Record> getMerchantServiceTagList(Map<String, String[]> param) {
		return serviceTagDao.getMerchantServiceTagList(param);
	}

	@Override
	public boolean deleteMerchantServiceTag(String ids) {
		return serviceTagDao.deleteMerchantServiceTag(ids);
	}

	@Override
	public boolean auditMerchantServiceTag(String ids,String status, String calogId, String demand) {
		return serviceTagDao.auditMerchantServiceTag(ids,status,calogId,demand);
	}

	@Override
	public List<Record> getServiceTagList(Map<String, String[]> param) {
		return serviceTagDao.getServiceTagList(param);
	}

	@Override
	public boolean deleteServiceTag(String ids) {
		return serviceTagDao.deleteServiceTag(ids);
	}

	@Override
	public boolean auditServiceTag(String ids,String status) {
		return serviceTagDao.auditServiceTag(ids,status);
	}

	@Override
	public int saveServiceTag(Map<String, String[]> param) {
		return serviceTagDao.saveServiceTag(param);
	}

	@Override
	public long getMerchantServiceTagListCount(Map<String, String[]> param) {
		return serviceTagDao.getMerchantServiceTagListCount(param);
	}

	@Override
	public long getServiceTagListCount(Map<String, String[]> param) {
		return serviceTagDao.getServiceTagListCount(param);
	}

	@Override
	public List<MerchantServiceTag> exportExcel(Map<String, String[]> param) {
		return serviceTagDao.exportExcel(param);
	}

	@Override
	public List<Pair> getExportTitles() {
		List<Pair> titles = new ArrayList<Pair>();
		titles.add(new Pair("id", "标签ID"));
		titles.add(new Pair("merchant_name", "商户名称"));
		titles.add(new Pair("telephone", "联系方式"));
		titles.add(new Pair("location_address", "商户地址"));
		titles.add(new Pair("name", "标签"));
		titles.add(new Pair("price", "标签价格"));
		titles.add(new Pair("join_time", "创建时间"));
		titles.add(new Pair("isAudit", "审核状态"));
		return titles;
	}

	@Override
	public List<Record> getPersonalTagList(Map<String, String[]> param) {
		return serviceTagDao.getPersonalTagList(param);
	}

	@Override
	public long getPersonalTagListCount(Map<String, String[]> param) {
		return serviceTagDao.getPersonalTagListCount(param);
	}

	@Override
	public Boolean deletePersonalTag(String ids) {
		return serviceTagDao.deletePersonalTag(ids);
	}

	@Override
	public int savePersonalTag(Map<String, String[]> param) {
		return serviceTagDao.savePersonalTag(param);
	}

	@Override
	public Map getTagRepeat(String id) {
		return serviceTagDao.getTagRepeat(id);
	}
	
}

package com.shanjin.manager.service;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.MerchantServiceTag;
import com.shanjin.manager.service.ExcelExportUtil.Pair;

public interface IServiceTagService {
	
    public List<Record> getMerchantServiceTagList(Map<String, String[]> param);
    
    public long getMerchantServiceTagListCount(Map<String, String[]> param);
	
	public boolean deleteMerchantServiceTag(String ids);
	
	public boolean auditMerchantServiceTag(String ids,String status, String calogId, String demand);
	
	public List<Record> getServiceTagList(Map<String, String[]> param);
	
    public long getServiceTagListCount(Map<String, String[]> param);
		
    public boolean deleteServiceTag(String ids);
		
    public boolean auditServiceTag(String ids,String status);
    
    public int saveServiceTag(Map<String, String[]> param);

	public List<MerchantServiceTag> exportExcel(Map<String, String[]> param);

	public List<Pair> getExportTitles();

	public List<Record> getPersonalTagList(Map<String, String[]> param);

	public long getPersonalTagListCount(Map<String, String[]> param);

	public Boolean deletePersonalTag(String ids);

	public int savePersonalTag(Map<String, String[]> param);

	public Map getTagRepeat(String id);

}

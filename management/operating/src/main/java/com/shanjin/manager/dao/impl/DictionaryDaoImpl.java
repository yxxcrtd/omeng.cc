package com.shanjin.manager.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.impl.CommonCacheServiceImpl;
import com.shanjin.manager.Bean.Slider;
import com.shanjin.manager.Bean.Voucher;
import com.shanjin.manager.constant.Constant.PAGE;
import com.shanjin.manager.constant.Constant.SORT;
import com.shanjin.manager.dao.DictionaryDao;
import com.shanjin.manager.utils.BusinessUtil;
import com.shanjin.manager.utils.CommonUtil;
import com.shanjin.manager.utils.DateUtil;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

public class DictionaryDaoImpl implements DictionaryDao{
	private static ICommonCacheService commonCacheService = new CommonCacheServiceImpl();
	@Override
	public List<Record> getDictionaryList(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0].equals("")==true ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0].equals("")==true ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("d.dict_type", paramMap.get("dict_type"));
		strFilter.put("d.parent_dict_id", paramMap.get("parent_dict_id"));
		sql.append("select d.id,d.dict_type,d.dict_key,d.dict_value,d.remark,d.parent_dict_id,d.is_del,d.is_leaves,d.sort from dictionary d where d.is_del=0 and ");
		totalSql.append("select count(1) as total from dictionary d where d.is_del=0 and ");
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		
		long total =Db.find(totalSql.toString()).get(0).getLong("total");

		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<Record> dictionaryList = Db.find(sql.toString());

		if (dictionaryList.size() > 0) {
			dictionaryList.get(0).set("total", total);
		}

		return dictionaryList;
	}

	@Override
	public boolean addDictionary(Map<String, String[]> param) {
		String dictType=param.get("dict_type")[0];
		String dictKey=param.get("dict_key")[0];
		String dictValue=param.get("dict_value")[0];
		String remark=param.get("remark")[0].equals("")==true?null:param.get("remark")[0];
		String parentDictId=param.get("parent_dict_id")[0].equals("")==true?null:param.get("parent_dict_id")[0];
		String sort=param.get("sort")[0].equals("")==true?null:param.get("sort")[0];
		String isLeaves=param.get("is_leaves")[0].equals("")==true? "1" : param.get("is_leaves")[0];
		Record record=new Record();
		record.set("dict_type", dictType).set("dict_key", dictKey)
				.set("dict_value", dictValue).set("remark", remark)
				.set("parent_dict_id", parentDictId).set("sort", sort).set("is_leaves",isLeaves).set("is_del", 0);
		boolean flag=Db.save("dictionary", record);
		commonCacheService.delObjectContainsKey(CacheConstants.DICT_LIST_KEY+CacheConstants.JOIN+dictType, true);
		return flag;
	}
	
	@Override
	public boolean deleteDictionary(Map<String, String[]> param) {
		String[] ids=param.get("id")[0].split(",");
		boolean flag=false;
		Record record=new Record();
		
		for(int i=0;i<ids.length;i++){
			String sql="update dictionary set is_del=1 where id="+ids[i];
			Db.update(sql);
			flag=true;
			record=Db.findById("dictionary", ids[i]);
			if(record!=null){
			String dictType=record.getStr("dict_type");
			commonCacheService.delObjectContainsKey(CacheConstants.DICT_LIST_KEY+CacheConstants.JOIN+dictType, true);	
			}
		}
		
	   return flag;
	}

	@Override
	public boolean editDictionary(Map<String, String[]> param) {
		String id=param.get("id")[0];
		String dictType=param.get("dict_type")[0];
		String dictKey=param.get("dict_key")[0];
		String dictValue=param.get("dict_value")[0];
		String remark=param.get("remark")[0].equals("")==true?null:param.get("remark")[0];
		String parentDictId=param.get("parent_dict_id")[0].equals("")==true?null:param.get("parent_dict_id")[0];
		String sort=param.get("sort")[0].equals("")==true?null:param.get("sort")[0];
		String isLeaves=param.get("is_leaves")[0].equals("")==true? "1" : param.get("is_leaves")[0];
		//String sql="update dictionary set dict_type=?, dict_key=? , dict_value=? , remark=? , parent_dict_id=? , sort=? , is_leaves=? where id=?";
		Record record = Db.findById("dictionary", id).set("dict_type", dictType).set("dict_key", dictKey).set("dict_value", dictValue).set("remark", remark)
		                             .set("parent_dict_id", parentDictId).set("sort", sort).set("is_leaves", isLeaves);
		Db.update("dictionary", record);
		commonCacheService.delObjectContainsKey(CacheConstants.DICT_LIST_KEY+CacheConstants.JOIN+dictType, true);	
		return true;
	}

	@Override
	public List<Record> getServiceTypeList(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0].equals("")==true ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0].equals("")==true ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("s.service_type_name", paramMap.get("service_type_name"));
		strLikeFilter.put("s.app_type", paramMap.get("app_type"));
		strLikeFilter.put("s.app_name", paramMap.get("app_name"));
		intFilter.put("s.is_pub", paramMap.get("is_pub"));
		sql.append(" SELECT s.*,sa.path AS showIcon,oa.path AS orderIcon ");
		sql.append(" FROM service_type s ");
		sql.append(" LEFT JOIN service_type_attachment sa ON s.id=sa.service_type_id AND sa.attachment_style='showIcon' AND sa.is_del=0 ");
		sql.append(" LEFT JOIN service_type_attachment oa ON s.id=oa.service_type_id AND oa.attachment_style='orderIcon' AND oa.is_del=0 ");
		sql.append(" WHERE s.is_del=0 and ");
		totalSql.append("select count(1) as total from service_type s where s.is_del=0 and ");
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		
		long total =Db.find(totalSql.toString()).get(0).getLong("total");

		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<Record> serviceTypeList = Db.find(sql.toString());

		if (serviceTypeList.size() > 0) {
			serviceTypeList.get(0).set("total", total);
			String pic = "";
			for(Record r : serviceTypeList){
				pic = r.getStr("showIcon");
				if(!StringUtil.isNullStr(pic)){
					pic = BusinessUtil.getFileUrl(pic);
					r.set("showIcon", pic);
				}
				pic = r.getStr("orderIcon");
				if(!StringUtil.isNullStr(pic)){
					pic = BusinessUtil.getFileUrl(pic);
					r.set("orderIcon", pic);
				}
			}
		}

		return serviceTypeList;
		
	}

	@Override
	public boolean addServiceType(Map<String, String[]> param) {
		String serviceTypeId=param.get("service_type_id")[0];
		String serviceTypeName=param.get("service_type_name")[0];
		String appName=param.get("app_name")[0];
		String appType=param.get("app_type")[0];
		String parentDictId=param.get("parent_id")[0].equals("")==true?null:param.get("parent_id")[0];
		String isLeaves=param.get("is_leaves")[0].equals("")==true? "1" : param.get("is_leaves")[0];
		Record record=new Record();
		record.set("service_type_id", serviceTypeId).set("service_type_name", serviceTypeName)
				.set("app_name", appName).set("app_type", appType)
				.set("parent_id", parentDictId).set("is_leaves",isLeaves).set("is_del", 0);
		boolean flag=Db.save("service_type", record);
		return flag;
	}

	@Override
	public boolean editServiceType(Map<String, String[]> param) {
		String id=param.get("id")[0];
		String serviceTypeId=param.get("service_type_id")[0];
		String serviceTypeName=param.get("service_type_name")[0];
		String appName=param.get("app_name")[0];
		String appType=param.get("app_type")[0];
		String parentDictId=param.get("parent_id")[0].equals("")==true?null:param.get("parent_id")[0];
		String isLeaves=param.get("is_leaves")[0].equals("")==true? "1" : param.get("is_leaves")[0];
		String sql="update service_type set service_type_id=?, service_type_name=? , app_name=? , app_type=? , parent_id=? , is_leaves=? where id=?";
	
		int flag=Db.update(sql,serviceTypeId,serviceTypeName,appName,appType,parentDictId,isLeaves,id);
		
		return flag>0;
	}

	@Override
	public boolean deleteServiceType(Map<String, String[]> param) {
		String[] ids=param.get("id")[0].split(",");
		int flag=0;
		for(int i=0;i<ids.length;i++){
			CommonUtil.flushCataCache(ids[i]);
			CommonUtil.flushCataOrderCache(ids[i]);
			commonCacheService.deleteObject(CacheConstants.ORDER_FORM, ids[i]);
			String sql="update service_type set is_del=1 where id="+ids[i];
			flag=Db.update(sql);
		}
		commonCacheService.deleteObject(CacheConstants.CACHE_ALLSERVICETYPE);
		
	   return flag>0;
	}

	@Override
	public boolean saveServiceType(Map<String, String[]> param,
			String showIconPath, String orderIconPath) {
		String id = StringUtil.null2Str(param.get("id")[0]);
		boolean isUpdate = false ; 
		if(StringUtil.isNotNullMap(param, "id")){
			isUpdate = true;
		}
		//String serviceTypeId=param.get("service_type_id")[0];
		String serviceTypeName=param.get("service_type_name")[0];
		//String appName=param.get("app_name")[0];
		//String appType=param.get("app_type")[0];
		//String parentDictId=param.get("parent_id")[0].equals("")==true?null:param.get("parent_id")[0];
		//String isLeaves=param.get("is_leaves")[0].equals("")==true? "1" : param.get("is_leaves")[0];
		boolean flag = false;//0:失败，1：成功
		if(isUpdate){
			// 更新
			String sqlrep="select id from service_type where is_del=0 and service_type_name=?";
			List<Record> lis=Db.find(sqlrep,serviceTypeName);
			if(lis!=null&&lis.size()>0){
				String newId=lis.get(0).getLong("id").toString();
				if(!newId.equals(id)){
					return flag;
				}
				
			}
			
			Record record = Db.findById("service_type", id)
			.set("service_type_name", serviceTypeName);
			Db.update("service_type", record);
			List<Record> list = null;
			if(!StringUtil.isNullStr(showIconPath)){
				// 有图片上传
				String serviceAttShowIconSql = "SELECT * FROM service_type_attachment t WHERE t.is_del=0 AND t.attachment_type=1  AND t.attachment_style='showIcon' AND t.service_type_id="+record.getLong("id");
				list = Db.find(serviceAttShowIconSql);
				if(list!=null&&list.size()>0){
					Record showRecord = list.get(0);
					showRecord.set("path", showIconPath);
					Db.update("service_type_attachment", showRecord);
				}else{
					Record showRecord = new Record();
					showRecord.set("service_type_id", record.getLong("id")).set("attachment_style", "showIcon")
					.set("path", showIconPath).set("join_time", new Date());
					Db.save("service_type_attachment", showRecord);
				}
			}
			if(!StringUtil.isNullStr(orderIconPath)){
				// 有图片上传
				String serviceAttOrderIconSql = "SELECT * FROM service_type_attachment t WHERE t.is_del=0 AND t.attachment_type=1  AND t.attachment_style='orderIcon' AND t.service_type_id="+record.getLong("id");
				list = Db.find(serviceAttOrderIconSql);
				if(list!=null&&list.size()>0){
					Record orderRecord = list.get(0);
					orderRecord.set("path", orderIconPath);
					Db.update("service_type_attachment", orderRecord);
				}else{
					Record orderRecord = new Record();
					orderRecord.set("service_type_id", record.getLong("id")).set("attachment_style", "orderIcon")
					.set("path", orderIconPath).set("join_time", new Date());
					Db.save("service_type_attachment", orderRecord);
				}
			}
			
			CommonUtil.flushCataCache(id);
			CommonUtil.flushCataOrderCache(id);
		}else{
			// 新增
			String sqlrep="select id from service_type where is_del=0 and service_type_name=?";
			List<Record> lis=Db.find(sqlrep,serviceTypeName);
			if(lis!=null&&lis.size()>0){
				return flag;
			}
			Record record = new Record();
			record.set("service_type_name", serviceTypeName);	
			Db.save("service_type", record);
			if(!StringUtil.isNullStr(showIconPath)){
				// 有图片上传
				Record showRecord = new Record();
				showRecord.set("service_type_id", record.getLong("id")).set("attachment_style", "showIcon")
				.set("path", showIconPath).set("join_time", new Date());
				Db.save("service_type_attachment", showRecord);
			}
			if(!StringUtil.isNullStr(orderIconPath)){
				// 有图片上传
				Record orderRecord = new Record();
				orderRecord.set("service_type_id", record.getLong("id")).set("attachment_style", "orderIcon")
				.set("path", orderIconPath).set("join_time", new Date());
				Db.save("service_type_attachment", orderRecord);
			}
		}
		
		commonCacheService.deleteObject(CacheConstants.CACHE_ALLSERVICETYPE);
		flag = true;
		return flag;
	}


	@Override
	public List<Record> getConfigurationList(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0].equals("")==true ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0].equals("")==true ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("ci.config_value", paramMap.get("config_value"));
		strFilter.put("ci.config_key", paramMap.get("config_key"));
		sql.append("select ci.id,ci.config_key,ci.config_value,ci.remark,ci.standby_field1,ci.standby_field2,ci.standby_field3,ci.standby_field4,ci.standby_field5 from configuration_info ci where ci.is_show=1 and ");
		totalSql.append("select count(1) as total from configuration_info ci where ci.is_show=1 and ");
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		
		long total =Db.find(totalSql.toString()).get(0).getLong("total");

		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<Record> configurationList = Db.find(sql.toString());

		if (configurationList.size() > 0) {
			configurationList.get(0).set("total", total);
		}

		return configurationList;
		
		
		
	}

	@Override
	public boolean addConfiguration(Map<String, String[]> param) {
		String config_key=param.get("config_key")[0];
		String config_value=param.get("config_value")[0];
		String remark=param.get("remark")[0];
		String standby_field1=param.get("standby_field1")[0];
		String standby_field2=param.get("standby_field2")[0];
		String standby_field3=param.get("standby_field3")[0];
		String standby_field4=param.get("standby_field4")[0];
		String standby_field5=param.get("standby_field5")[0];
		
		Record record=new Record();
		record.set("config_key", config_key).set("config_value", config_value)
				.set("remark", remark).set("standby_field1", standby_field1)
				.set("standby_field2", standby_field2).set("standby_field3", standby_field3).set("standby_field4",standby_field4).set("standby_field5", standby_field5)
				.set("is_show", 1);
		boolean flag=Db.save("configuration_info", record);
		commonCacheService.delObjectContainsKey(CacheConstants.CONFIG_KEY, true);	
		commonCacheService.deleteObject(CacheConstants.PUSH_CONFIG_KEY);
		return flag;
	}

	@Override
	public boolean editConfiguration(Map<String, String[]> param) {
		String id=param.get("id")[0];
		String config_key=param.get("config_key")[0];
		String config_value=param.get("config_value")[0];
		String remark=param.get("remark")[0];
		String standby_field1=param.get("standby_field1")[0];
		String standby_field2=param.get("standby_field2")[0];
		String standby_field3=param.get("standby_field3")[0];
		String standby_field4=param.get("standby_field4")[0];
		String standby_field5=param.get("standby_field5")[0];
		Record record = Db.findById("configuration_info", id).set("config_key", config_key).set("config_value", config_value)
				.set("remark", remark).set("standby_field1", standby_field1)
				.set("standby_field2", standby_field2).set("standby_field3", standby_field3).set("standby_field4",standby_field4).set("standby_field5", standby_field5);
		Db.update("configuration_info", record);
		commonCacheService.delObjectContainsKey(CacheConstants.CONFIG_KEY, true);	
		commonCacheService.deleteObject(CacheConstants.PUSH_CONFIG_KEY);
		if(config_key.equals("opay_disable_withdraw")){
			commonCacheService.deleteObject(CacheConstants.OPAY_DISABLE_WITHDRAW);
		}
		return true;
	}

	@Override
	public boolean deleteConfigurationParam(Map<String, String[]> param) {
		String[] ids=param.get("id")[0].split(",");
		boolean flag=false;
		for(int i=0;i<ids.length;i++){
			Db.deleteById("configuration_info", ids[i]);
		}
		commonCacheService.delObjectContainsKey(CacheConstants.CONFIG_KEY, true);	
		commonCacheService.deleteObject(CacheConstants.PUSH_CONFIG_KEY);
		commonCacheService.deleteObject(CacheConstants.OPAY_DISABLE_WITHDRAW);
		flag=true;
	   return flag;
	}
	
	@Override
	public List<Record> getValueLabelList(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0].equals("")==true ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0].equals("")==true ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("v.name", paramMap.get("name"));
		strFilter.put("v.lable_type", paramMap.get("lable_type"));
		sql.append("select v.id,v.name,v.min_value,v.max_value,v.is_use,v.lable_type,v.create_time from value_lable v where ");
		totalSql.append("select count(1) as total from value_lable v where ");
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		
		long total =Db.find(totalSql.toString()).get(0).getLong("total");
		
		String property = "v.create_time";
		String direction = SORT.DESC;
		if(StringUtil.isNotNullMap(paramMap,"sort")){
			Map<String,String> sortMap = Util.sortMap(StringUtil.null2Str(paramMap.get("sort")[0]));
			if(sortMap!=null){
				if(!StringUtil.isNullStr(sortMap.get("property"))){
					property = StringUtil.null2Str(sortMap.get("property"));
				}
				if(!StringUtil.isNullStr(sortMap.get("direction"))){
					direction = StringUtil.null2Str(sortMap.get("direction"));
				}
			}
		}
		sql.append(" ORDER BY ").append(property).append(" ").append(direction);
		
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<Record> dictionaryList = Db.find(sql.toString());

		if (dictionaryList.size() > 0) {
			dictionaryList.get(0).set("total", total);
		}

		return dictionaryList;
	}

	@Override
	public boolean addValueLabel(Map<String, String[]> param) {
		String name=param.get("name")[0];
		String min_value=param.get("min_value")[0];
		String max_value=param.get("max_value")[0];
		String is_use=param.get("is_use")[0];
		String lable_type=param.get("lable_type")[0];
		
		Record record=new Record();
		record.set("name", name).set("min_value", min_value)
				.set("max_value", max_value).set("is_use", is_use)
				.set("lable_type", lable_type).set("create_time", new Date());
		boolean flag=Db.save("value_lable", record);
		return flag;
	}

	@Override
	public boolean editValueLabel(Map<String, String[]> param) {
		String id=param.get("id")[0];
		String name=param.get("name")[0];
		String min_value=param.get("min_value")[0];
		String max_value=param.get("max_value")[0];
		String is_use=param.get("is_use")[0];
		String lable_type=param.get("lable_type")[0];
		Record record = Db.findById("value_lable", id).set("name", name).set("min_value", min_value)
				.set("max_value", max_value).set("is_use", is_use)
				.set("lable_type", lable_type);
		Db.update("value_lable", record);
		return true;
	}

	@Override
	public boolean deleteValueLabel(Map<String, String[]> param) {
		String[] ids=param.get("id")[0].split(",");
		boolean flag=false;
		for(int i=0;i<ids.length;i++){
			Db.deleteById("value_lable", ids[i]);
			flag=true;
		}
	   return flag;
	}

	@Override
	public boolean startOrstopLabel(Map<String, String[]> param) {
		String id=param.get("id")[0];
		String is_use=param.get("is_use")[0];
		boolean flag=false;
		Record record=Db.findById("value_lable", id);
		record.set("is_use", is_use);
		Db.update("value_lable",record);
		flag=true;
		return flag;
	}

	@Override
	public int addSystemPicParam(Map<String, String[]> param,String resultPath) {
		int flag=0;
		String id = StringUtil.null2Str(param.get("id")[0]);
		boolean isUpdate = false ; 
		if(StringUtil.isNotNullMap(param, "id")){
			isUpdate = true;
		}
		String dictionary_id=param.get("dictionary_id")[0];
		String attachment_type=param.get("attachment_type")[0];
		String attachment_style=param.get("attachment_style")[0];
		if(StringUtil.isNullStr(resultPath)){
			// 未上传图片
			if(StringUtil.isNotNullMap(param, "pics_path")){
				resultPath = param.get("pics_path")[0];
				resultPath = BusinessUtil.getFilePath(resultPath);
			}
		}
		
		String sql="select id from dictionary_attachment da where da.dictionary_id=? and da.attachment_type=? and da.attachment_style=?";
		List<Record> list=Db.find(sql,dictionary_id,attachment_type,attachment_style);
		
		if(isUpdate){
			if(list!=null && list.size()>0){
				if(!id.equals(list.get(0).getLong("id").toString())){
					flag=1;
					return flag;
				}
				
			}
			Record rc=Db.findById("dictionary_attachment", id);
			rc.set("dictionary_id", dictionary_id).set("attachment_type", attachment_type).set("attachment_style", attachment_style)
			.set("path",resultPath);
			Db.update("dictionary_attachment", rc);
		}else{
			if(list!=null && list.size()>0){
				flag=2;
				return flag;
			}
			Record r=new Record();
			r.set("dictionary_id", dictionary_id).set("attachment_type", attachment_type).set("attachment_style", attachment_style)
			.set("path",resultPath).set("join_time",new Date()).set("is_del",0);
			Db.save("dictionary_attachment", r);
		}
		flag=3;
		return flag;
		
	}

	@Override
	public List<Record> getDictAttchList(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0].equals("")==true ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0].equals("")==true ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
	
		intFilter.put("d.dictionary_id", paramMap.get("dictionary_id"));
		sql.append("select d.id,d.dictionary_id,d.attachment_type,d.attachment_style,d.path from dictionary_attachment d where d.is_del=0 and ");
		totalSql.append("select count(1) as total from dictionary_attachment d where d.is_del=0 and ");
		sql.append(Util.getFilter(strFilter, intFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter));
		
		long total =Db.find(totalSql.toString()).get(0).getLong("total");

		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<Record> dictionaryList = Db.find(sql.toString());

		if (dictionaryList.size() > 0) {
			dictionaryList.get(0).set("total", total);
			String pic = "";
			for(Record d : dictionaryList){
				pic = d.getStr("path");
				if(!StringUtil.isNullStr(pic)){
					pic = BusinessUtil.getFileUrl(pic);
					d.set("path", pic);
				}
			}
		}

		return dictionaryList;
	}

	@Override
	public boolean deleteDictAttch(Map<String, String[]> param) {
		String[] ids=param.get("id")[0].split(",");
		boolean flag=false;
		for(int i=0;i<ids.length;i++){
			Db.deleteById("dictionary_attachment", ids[i]);
		}
		flag=true;
	   return flag;
	}

	

}

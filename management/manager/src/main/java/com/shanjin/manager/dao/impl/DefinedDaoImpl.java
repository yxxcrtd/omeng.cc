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
import com.shanjin.manager.constant.Constant.PAGE;
import com.shanjin.manager.dao.DefinedDao;
import com.shanjin.manager.utils.BusinessUtil;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

public class DefinedDaoImpl implements DefinedDao {
	private static ICommonCacheService commonCacheService = new CommonCacheServiceImpl();
	@Override
	public List<Record> getPlanTableList(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0].equals("")==true ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0].equals("")==true ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("st.service_type_name", paramMap.get("service_type_name"));
		
		sql.append("select o.object_id,o.remark,o.disabled,o.service_type_id,st.service_type_name ");
		sql.append(" from plan_object_table o left join service_type st on st.id=o.service_type_id where o.is_del=0 and ");
		totalSql.append("select count(1) as total from plan_object_table o where o.is_del=0 and ");
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		
		long total =Db.find(totalSql.toString()).get(0).getLong("total");

		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<Record> objectTableList = Db.find(sql.toString());

		if (objectTableList.size() > 0) {
			objectTableList.get(0).set("total", total);
		}

		return objectTableList;
	}

	@Override
	public Boolean deletePlanTable(Map<String, String[]> param) {
		String[] ids=param.get("id")[0].split(",");
		boolean flag=false;
		for(int i=0;i<ids.length;i++){
			String sql="update plan_object_table set is_del=1 where object_id="+ids[i];
			Db.update(sql);
			flag=true;
		}
		
	   return flag;
	}

	@Override
	public Boolean addPlanTable(Map<String, String[]> param) {
		String remark=param.get("remark")[0];
		String version=Util.getCurrentDate(new Date());
		String service_type_id=param.get("service_type_id")[0].equals("")==true ? "0" : param.get("service_type_id")[0];
		Record record=new Record();
		record.set("remark", remark).set("version", version)
				.set("service_type_id",service_type_id).set("is_del", 0);
		boolean flag=Db.save("plan_object_table", record);
		return flag;
	}
	
	@Override
	public Boolean editPlanTable(Map<String, String[]> param) {
		long id=Long.parseLong(param.get("id")[0]);
		String remark=param.get("remark")[0];
		String service_type_id=param.get("service_type_id")[0].equals("")==true ? "0" : param.get("service_type_id")[0];
		Record record = Db.findById("plan_object_table","object_id", id).set("remark", remark)
				.set("service_type_id", service_type_id);
		Db.update("plan_object_table","object_id", record);
		return true;
	}

	@Override
	public List<Record> getPlanModelList(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0].equals("")==true ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0].equals("")==true ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		
		String object_id = paramMap.get("object_id")[0];
		
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		
		strFilter.put("o.object_id", paramMap.get("object_id"));
		sql.append("SELECT o.*, s.service_type_name FROM plan_object_model o LEFT JOIN service_type s ON o.object_id = s.id WHERE ");
		totalSql.append("select count(1) as total from plan_object_model o where ");
		sql.append(Util.getFilter(strFilter, intFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter));
		
		long total =Db.find(totalSql.toString()).get(0).getLong("total");

		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<Record> objectTableList = Db.find(sql.toString());

		if (objectTableList.size() > 0) {
			objectTableList.get(0).set("total", total);
			String pic = "";
			for(Record s : objectTableList){
				pic = s.getStr("icon");
				if(!StringUtil.isNullStr(pic)){
					pic = BusinessUtil.getFileUrl(pic);
					s.set("icon", pic);
				}
			}
		}else{
			if(StringUtil.nullToLong(object_id).intValue()!=0){
				addDefaultPlanModel(StringUtil.nullToLong(object_id));
				objectTableList = getRefeshPlanModelList(paramMap);
			}
			
		}

		return objectTableList;
	}
	
	private List<Record> getRefeshPlanModelList(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0].equals("")==true ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0].equals("")==true ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		
		strFilter.put("o.object_id", paramMap.get("object_id"));
		sql.append("SELECT o.*, s.service_type_name FROM plan_object_model o LEFT JOIN service_type s ON o.object_id = s.id WHERE ");
		totalSql.append("select count(1) as total from plan_object_model o where ");
		sql.append(Util.getFilter(strFilter, intFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter));
		
		long total =Db.find(totalSql.toString()).get(0).getLong("total");

		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<Record> objectTableList = Db.find(sql.toString());

		if (objectTableList.size() > 0) {
			objectTableList.get(0).set("total", total);
			String pic = "";
			for(Record s : objectTableList){
				pic = s.getStr("icon");
				if(!StringUtil.isNullStr(pic)){
					pic = BusinessUtil.getFileUrl(pic);
					s.set("icon", pic);
				}
			}
		}

		return objectTableList;
	}
	
	private void addDefaultPlanModel(Long object_id) {
		Record record=null;
		record=new Record();
		record.set("col_name", "detail").set("col_desc", "内容描述").set("control_type", "DEMAND")
				.set("col_len",200).set("show_type", "").set("object_id", object_id)
		.set("icon","/resource/order/defined/detail/1456283942885.png").set("rank", 50).set("model_type", 1)
		.set("hint_value", "请输入内容描述").set("control_type_desc", "备注栏");
		Db.save("plan_object_model", record);
		
		record=new Record();
		record.set("col_name", "promise").set("col_desc", "服务承诺").set("control_type", "TEXT")
				.set("col_len",15).set("show_type", "1").set("object_id", object_id)
		.set("icon","/resource/order/defined/detail/1456283942986.png").set("rank", 51).set("model_type", 1)
		.set("hint_value", "请输入服务承诺").set("control_type_desc", "备注栏");
		Db.save("plan_object_model", record);
		
		record=new Record();
		record.set("col_name", "planPrice").set("col_desc", "原价").set("control_type", "TEXT")
		.set("col_len",7).set("show_type", "2").set("object_id", object_id)
		.set("icon","/resource/order/defined/detail/1456283931344.png").set("rank", 52).set("model_type", 2)
		.set("hint_value", "请输入原价").set("control_type_desc", "");
        Db.save("plan_object_model", record);
        
        record=new Record();
		record.set("col_name", "discountPrice").set("col_desc", "O盟报价").set("control_type", "TEXT")
		.set("col_len",7).set("show_type", "2").set("object_id", object_id)
		.set("icon","/resource/order/defined/detail/1456283931344.png").set("rank", 53).set("model_type", 2)
		.set("hint_value", "请输入O盟报价").set("control_type_desc", "");
        Db.save("plan_object_model", record);
	}

	@Override
	public Boolean deletePlanModel(Map<String, String[]> param) {
		String[] ids=param.get("id")[0].split(",");
		boolean flag=false;
		for(int i=0;i<ids.length;i++){
			Db.deleteById("plan_object_model", ids[i]);
			flag=true;
		}
	    return flag;
	}

	@Override
	public Boolean addPlanModel(Map<String, String[]> param, String resultPath) {
		String col_name=param.get("col_name")[0];
		String col_desc=param.get("col_desc")[0];
		String control_type=param.get("control_type")[0];
		String col_len=param.get("col_len")[0].equals("")==true ? "0" : param.get("col_len")[0];
		String control_type_desc=param.get("control_type_desc")[0];
		String show_type_desc=param.get("show_type_desc")[0];
		String hint_value=param.get("hint_value")[0];
		String min_val=param.get("min_val")[0];
		String max_val=param.get("max_val")[0];
		String default_value=param.get("default_value")[0];
		String link=param.get("link")[0];
		String unit=param.get("unit")[0];
		String show_type=param.get("show_type")[0].equals("")==true ? "0" : param.get("show_type")[0];
		String object_id=param.get("object_id")[0];
		String rank=param.get("rank")[0].equals("")==true ? "0" : param.get("rank")[0];
		String model_type=param.get("model_type")[0].equals("")==true ? "0" : param.get("model_type")[0];
		
		String sql="select * from plan_object_model where object_id=? and col_name=?";
		List<Record> repeName=Db.find(sql,object_id,col_name);
		if(repeName!=null&&repeName.size()>0){
			return false;
		}
		
		Record record=new Record();
		record.set("col_name", col_name).set("col_desc", col_desc).set("control_type", control_type)
				.set("col_len",col_len).set("show_type", show_type).set("object_id", object_id)
				.set("icon",resultPath).set("rank", rank).set("model_type", model_type)
				.set("hint_value", hint_value).set("link", link).set("unit", unit)
				.set("control_type_desc", control_type_desc).set("show_type_desc", show_type_desc)
                .set("min_val", min_val).set("max_val", max_val).set("default_value", default_value);
		boolean flag=Db.save("plan_object_model", record);
		
		return flag;
	}

	@Override
	public Boolean editPlanModel(Map<String, String[]> param, String resultPath) {
		String id=param.get("id")[0];
		String col_name=param.get("col_name")[0];
		String col_desc=param.get("col_desc")[0];
		String control_type=param.get("control_type")[0];
		String col_len=param.get("col_len")[0].equals("")==true ? "0" : param.get("col_len")[0];
		String control_type_desc=param.get("control_type_desc")[0];
		String show_type_desc=param.get("show_type_desc")[0];
		String hint_value=param.get("hint_value")[0];
		String min_val=param.get("min_val")[0];
		String link=param.get("link")[0];
		String unit=param.get("unit")[0];
		String max_val=param.get("max_val")[0];
		String default_value=param.get("default_value")[0];
		String show_type=param.get("show_type")[0];
		String object_id=param.get("object_id")[0];
		String rank=param.get("rank")[0].equals("")==true ? "0" : param.get("rank")[0];
		String model_type=param.get("model_type")[0];
		
		String sql="select * from plan_object_model where object_id=? and col_name=?";
		List<Record> repeName=Db.find(sql,object_id,col_name);
		if(repeName!=null&&repeName.size()>0){
			String findObject_id=repeName.get(0).getLong("object_id").toString();
			if(!findObject_id.equals(object_id)){
				return false;
			}
		}
		Record record = Db.findById("plan_object_model", id)
				.set("col_name", col_name).set("col_desc", col_desc).set("control_type", control_type)
				.set("col_len",col_len).set("show_type", show_type).set("object_id", object_id)
                .set("rank", rank).set("model_type", model_type)
                .set("hint_value", hint_value).set("link", link).set("unit", unit)
				.set("control_type_desc", control_type_desc).set("show_type_desc", show_type_desc)
				.set("min_val", min_val).set("max_val", max_val).set("default_value", default_value);
		if(!resultPath.equals("")){
			record.set("icon",resultPath);
		}
		
		Db.update("plan_object_model", record);
		return true;
	}

	@Override
	public List<Record> getPlanModelItemList(Map<String, String[]> paramMap) {

		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0].equals("")==true ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0].equals("")==true ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		
		strFilter.put("o.item_name", paramMap.get("item_name"));
		intFilter.put("o.model_id", paramMap.get("model_id"));
		
		sql.append("select o.id,om.col_name,o.model_id,o.item_name ");
		sql.append(" from plan_object_model_item o inner join plan_object_model om on o.model_id=om.id where ");
		totalSql.append("select count(1) as total from plan_object_model_item o inner join plan_object_model om on o.model_id=om.id where ");
		sql.append(Util.getFilter(strFilter, intFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter));
		
		long total =Db.find(totalSql.toString()).get(0).getLong("total");

		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<Record> objectTableList = Db.find(sql.toString());

		if (objectTableList.size() > 0) {
			objectTableList.get(0).set("total", total);
		}

		return objectTableList;
	}

	@Override
	public Boolean deletePlanModelItem(Map<String, String[]> param) {
		String[] ids=param.get("id")[0].split(",");
		boolean flag=false;
		for(int i=0;i<ids.length;i++){
			Db.deleteById("plan_object_model_item", ids[i]);
			flag=true;
		}
		
	   return flag;
	}

	@Override
	public Boolean addPlanModelItem(Map<String, String[]> param) {
		String model_id=param.get("model_id")[0].equals("")==true ? "0" : param.get("model_id")[0];
		String item_name=param.get("item_name")[0];
		Record record=new Record();
		record.set("model_id", model_id).set("item_name", item_name);
		boolean flag=Db.save("plan_object_model_item", record);
		return flag;
	}

	@Override
	public Boolean editPlanModelItem(Map<String, String[]> param) {
		String id=param.get("id")[0];
		String model_id=param.get("model_id")[0].equals("")==true ? "0" : param.get("model_id")[0];
		String item_name=param.get("item_name")[0];
		Record record = Db.findById("plan_object_model_item", id).set("model_id", model_id)
				          .set("item_name", item_name);
		Db.update("plan_object_model_item", record);
		return true;
	}
	
	@Override
	public List<Record> getObjectTableList(Map<String, String[]> paramMap) {
		
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0].equals("")==true ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0].equals("")==true ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("st.service_type_name", paramMap.get("service_type_name"));
		
		sql.append("select o.object_id,o.remark,o.is_publish,o.service_type_id,st.service_type_name ");
		sql.append(" from order_object_table o left join service_type st on st.id=o.service_type_id where o.is_del=0 and ");
		totalSql.append("select count(1) as total from order_object_table o where o.is_del=0 and ");
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		
		long total =Db.find(totalSql.toString()).get(0).getLong("total");

		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<Record> objectTableList = Db.find(sql.toString());

		if (objectTableList.size() > 0) {
			objectTableList.get(0).set("total", total);
		}

		return objectTableList;
	}

	@Override
	public Boolean deleteObjectTable(Map<String, String[]> param) {
		String[] ids=param.get("id")[0].split(",");
		boolean flag=false;
		if(ids!=null&&ids.length>0){
			Db.update("update order_object_table set is_del=1 where object_id in (?)",param.get("id")[0]);
			for(String id : ids){
				Record r = Db.findById("order_object_table", id);
				if(r!=null){
				    commonCacheService.deleteObject(CacheConstants.ORDER_FORM, r.getInt("service_type_id").toString());	
				}

			}
		}
		flag=true;
	   return flag;
	}

	@Override
	public Boolean addObjectTable(Map<String, String[]> param) {
		String remark=param.get("remark")[0];
		String version=Util.getCurrentDate(new Date());
		String service_type_id=param.get("service_type_id")[0].equals("")==true ? "0" : param.get("service_type_id")[0];
		Record record=new Record();
		record.set("remark", remark).set("version", version)
				.set("service_type_id",service_type_id).set("is_del", 0);
		boolean flag=Db.save("order_object_table","object_id", record);
		addDefaultModel(record.getLong("object_id"));
		return flag;
	}
	
	private void addDefaultModel(Long object_id) {
		Record record=null;
		record=new Record();
		record.set("col_name", "serviceTime").set("col_desc", "服务时间").set("control_type", "TIME")
				.set("col_len",0).set("show_type", "2").set("object_id", object_id)
				.set("icon","/resource/order/defined/detail/1462243653234.png").set("rank", 6).set("model_type", 3)
				.set("hint_value", "请选择服务时间")
				.set("control_type_desc", "时间框").set("show_type_desc", "时间选框（1:仅日期，2:日期和时分）");
		Db.save("order_object_model", record);
		
		record=new Record();
		record.set("col_name", "address").set("col_desc", "服务地址").set("control_type", "ADDRESS")
		.set("col_len",100).set("show_type", "1").set("object_id", object_id)
		.set("icon","/resource/order/defined/detail/1462265556337.png").set("rank", 7).set("model_type", 3)
		.set("hint_value", "请选择服务位置").set("control_type_desc", "地址栏");
        Db.save("order_object_model", record);
        
        record=new Record();
		record.set("col_name", "demand").set("col_desc", "补充说明").set("control_type", "DEMAND")
		.set("col_len",200).set("show_type", "").set("object_id", object_id)
		.set("icon","/resource/order/defined/detail/1462241199929.png").set("rank", 8).set("model_type", 3)
		.set("hint_value", "").set("control_type_desc", "备注栏");
        Db.save("order_object_model", record);
	}

	@Override
	public Boolean editObjectTable(Map<String, String[]> param) {
		long id=Long.parseLong(param.get("id")[0]);
		String remark=param.get("remark")[0];
		String service_type_id=param.get("service_type_id")[0].equals("")==true ? "0" : param.get("service_type_id")[0];
		Record record = Db.findById("order_object_table","object_id", id).set("remark", remark)
				.set("service_type_id", service_type_id);
		Db.update("order_object_table","object_id", record);
		//commonCacheService.deleteObject(CacheConstants.ORDER_FORM,service_type_id );
		return true;
	}

	@Override
	public List<Record> getObjectModelList(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0].equals("")==true ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0].equals("")==true ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		
		String object_id = paramMap.get("object_id")[0];
		
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		
		strFilter.put("o.object_id", paramMap.get("object_id"));
		sql.append("SELECT o.*, s.service_type_name FROM order_object_model o LEFT JOIN service_type s ON o.object_id = s.id WHERE ");
		totalSql.append("select count(1) as total from order_object_model o  where ");
		sql.append(Util.getFilter(strFilter, intFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter));
		
		long total =Db.find(totalSql.toString()).get(0).getLong("total");

		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<Record> objectTableList = Db.find(sql.toString());

		if (objectTableList.size() > 0) {
			objectTableList.get(0).set("total", total);
			String pic = "";
			for(Record s : objectTableList){
				pic = s.getStr("icon");
				if(!StringUtil.isNullStr(pic)){
					pic = BusinessUtil.getFileUrl(pic);
					s.set("icon", pic);
				}
			}
		}else{
			if(StringUtil.nullToLong(object_id).intValue()!=0){
				addDefaultModel(StringUtil.nullToLong(object_id));
				objectTableList = getReshObjectModelList(paramMap);
			}
		}

		return objectTableList;
	}

	private List<Record> getReshObjectModelList(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0].equals("")==true ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0].equals("")==true ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		
		
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		
		strFilter.put("o.object_id", paramMap.get("object_id"));
		sql.append("SELECT o.*, s.service_type_name FROM order_object_model o LEFT JOIN service_type s ON o.object_id = s.id WHERE ");
		totalSql.append("select count(1) as total from order_object_model o  where ");
		sql.append(Util.getFilter(strFilter, intFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter));
		
		long total =Db.find(totalSql.toString()).get(0).getLong("total");

		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<Record> objectTableList = Db.find(sql.toString());

		if (objectTableList.size() > 0) {
			objectTableList.get(0).set("total", total);
			String pic = "";
			for(Record s : objectTableList){
				pic = s.getStr("icon");
				if(!StringUtil.isNullStr(pic)){
					pic = BusinessUtil.getFileUrl(pic);
					s.set("icon", pic);
				}
			}
		}
		return objectTableList;
	}
	
	@Override
	public Boolean deleteObjectModel(Map<String, String[]> param) {
		String[] ids=param.get("id")[0].split(",");
		boolean flag=false;
		for(int i=0;i<ids.length;i++){
			Db.deleteById("order_object_model", ids[i]);
			flag=true;
//			Record rm=Db.findById("order_object_model", ids[i]);
//			if(!StringUtil.IsNull(rm)){
//			long object_id=rm.getLong("object_id");
//			Record r=Db.findById("order_object_table","object_id",object_id);
//			if(!StringUtil.IsNull(r)){
//			String version=Util.getCurrentDate(new Date());
//			r.set("version", version);
//			Db.update("order_object_table","object_id",r);
//			String service_type_id=r.getStr("service_type_id");
//			commonCacheService.deleteObject(CacheConstants.ORDER_FORM, service_type_id);
//		}
//			}
		}
		
	   return flag;
	}
	
	@Override
	public Boolean addObjectModel(Map<String, String[]> param, String resultPath) {
		String col_name=param.get("col_name")[0];
		String col_desc=param.get("col_desc")[0];
		String control_type=param.get("control_type")[0];
		String col_len=param.get("col_len")[0].equals("")==true ? "0" : param.get("col_len")[0];
		String control_type_desc=param.get("control_type_desc")[0];
		String show_type_desc=param.get("show_type_desc")[0];
		String hint_value=param.get("hint_value")[0];
		String min_val=param.get("min_val")[0];
		String max_val=param.get("max_val")[0];
		String default_value=param.get("default_value")[0];
		String link=param.get("link")[0];
		String unit=param.get("unit")[0];
		String show_type=param.get("show_type")[0].equals("")==true ? "0" : param.get("show_type")[0];
		String object_id=param.get("object_id")[0];
		String rank=param.get("rank")[0].equals("")==true ? "0" : param.get("rank")[0];
		String model_type=param.get("model_type")[0].equals("")==true ? "0" : param.get("model_type")[0];
		
		String sql="select * from order_object_model where object_id=? and col_name=?";
		List<Record> repeName=Db.find(sql,object_id,col_name);
		if(repeName!=null&&repeName.size()>0){
			return false;
		}
		
		Record record=new Record();
		record.set("col_name", col_name).set("col_desc", col_desc).set("control_type", control_type)
				.set("col_len",col_len).set("show_type", show_type).set("object_id", object_id)
				.set("icon",resultPath).set("rank", rank).set("model_type", model_type)
				.set("hint_value", hint_value).set("link", link).set("unit", unit)
				.set("control_type_desc", control_type_desc).set("show_type_desc", show_type_desc)
                .set("min_val", min_val).set("max_val", max_val).set("default_value", default_value);
		boolean flag=Db.save("order_object_model", record);
//		Record r=Db.findById("order_object_table","object_id", Long.parseLong(object_id));
//		if(!StringUtil.IsNull(r)){
//			String version=Util.getCurrentDate(new Date());
//			r.set("version", version);
//			Db.update("order_object_table","object_id",r);
//			String service_type_id=r.getInt("service_type_id").toString();
//			commonCacheService.deleteObject(CacheConstants.ORDER_FORM, service_type_id);
//		}
		
		return flag;
	}

	@Override
	public Boolean editObjectModel(Map<String, String[]> param, String resultPath) {
		String id=param.get("id")[0];
		String col_name=param.get("col_name")[0];
		String col_desc=param.get("col_desc")[0];
		String control_type=param.get("control_type")[0];
		String col_len=param.get("col_len")[0].equals("")==true ? "0" : param.get("col_len")[0];
		String control_type_desc=param.get("control_type_desc")[0];
		String show_type_desc=param.get("show_type_desc")[0];
		String hint_value=param.get("hint_value")[0];
		String min_val=param.get("min_val")[0];
		String link=param.get("link")[0];
		String unit=param.get("unit")[0];
		String max_val=param.get("max_val")[0];
		String default_value=param.get("default_value")[0];
		String show_type=param.get("show_type")[0];
		String object_id=param.get("object_id")[0];
		String rank=param.get("rank")[0].equals("")==true ? "0" : param.get("rank")[0];
		String model_type=param.get("model_type")[0];
		
		String sql="select * from order_object_model where object_id=? and col_name=?";
		List<Record> repeName=Db.find(sql,object_id,col_name);
		if(repeName!=null&&repeName.size()>0){
			String findObject_id=repeName.get(0).getLong("object_id").toString();
			if(!findObject_id.equals(object_id)){
				return false;
			}
		}
		Record record = Db.findById("order_object_model", id)
				.set("col_name", col_name).set("col_desc", col_desc).set("control_type", control_type)
				.set("col_len",col_len).set("show_type", show_type).set("object_id", object_id)
                .set("rank", rank).set("model_type", model_type)
                .set("hint_value", hint_value).set("link", link).set("unit", unit)
				.set("control_type_desc", control_type_desc).set("show_type_desc", show_type_desc)
				.set("min_val", min_val).set("max_val", max_val).set("default_value", default_value);
		if(!resultPath.equals("")){
			record.set("icon",resultPath);
		}
		
		Db.update("order_object_model", record);
//		Record r=Db.findById("order_object_table","object_id", Long.parseLong(object_id));
//		if(!StringUtil.IsNull(r)){
//			String version=Util.getCurrentDate(new Date());
//			r.set("version", version);
//			Db.update("order_object_table","object_id",r);
//			String service_type_id=r.getInt("service_type_id").toString();
//			commonCacheService.deleteObject(CacheConstants.ORDER_FORM, service_type_id);
//		}
		
		return true;
	}

	@Override
	public List<Record> getObjectModelItemList(Map<String, String[]> paramMap) {

		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0].equals("")==true ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0].equals("")==true ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		
		strFilter.put("o.item_name", paramMap.get("item_name"));
		intFilter.put("o.model_id", paramMap.get("model_id"));
		
		sql.append("select o.id,om.col_name,o.model_id,o.item_name ");
		sql.append(" from order_object_model_item o inner join order_object_model om on o.model_id=om.id where ");
		totalSql.append("select count(1) as total from order_object_model_item o inner join order_object_model om on o.model_id=om.id where ");
		sql.append(Util.getFilter(strFilter, intFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter));
		
		long total =Db.find(totalSql.toString()).get(0).getLong("total");

		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<Record> objectTableList = Db.find(sql.toString());

		if (objectTableList.size() > 0) {
			objectTableList.get(0).set("total", total);
		}

		return objectTableList;
	}

	@Override
	public Boolean deleteObjectModelItem(Map<String, String[]> param) {
		String[] ids=param.get("id")[0].split(",");
		boolean flag=false;
		for(int i=0;i<ids.length;i++){
			Db.deleteById("order_object_model_item", ids[i]);
			flag=true;
		}
		
	   return flag;
	}

	@Override
	public Boolean addObjectModelItem(Map<String, String[]> param) {
		
		String model_id=param.get("model_id")[0].equals("")==true ? "0" : param.get("model_id")[0];
		String item_name=param.get("item_name")[0];
		Record record=new Record();
		record.set("model_id", model_id).set("item_name", item_name);
		boolean flag=Db.save("order_object_model_item", record);
//		Record r=Db.findById("order_object_model", Long.parseLong(model_id));
//		if(r!=null){
//			Record re=Db.findById("order_object_table","object_id", r.getLong("object_id"));
//			commonCacheService.deleteObject(CacheConstants.ORDER_FORM, re.getInt("service_type_id").toString());
//		}
		
		return flag;
	}

	@Override
	public Boolean editObjectModelItem(Map<String, String[]> param) {
		String id=param.get("id")[0];
		String model_id=param.get("model_id")[0].equals("")==true ? "0" : param.get("model_id")[0];
		String item_name=param.get("item_name")[0];
		Record record = Db.findById("order_object_model_item", id).set("model_id", model_id)
				          .set("item_name", item_name);
		Db.update("order_object_model_item", record);
//		Record r=Db.findById("order_object_model", Long.parseLong(model_id));
//		if(r!=null){
//			Record re=Db.findById("order_object_table","object_id", r.getLong("object_id"));
//			commonCacheService.deleteObject(CacheConstants.ORDER_FORM, re.getInt("service_type_id").toString());
//		}
		return true;
	}

	@Override
	public Boolean pubObjectTable(String ids, String isPub) {
		boolean flag=false;
		if(!StringUtil.isNullStr(ids)){
			String [] oids = ids.split(",");
			if(oids!=null&&oids.length>0){
				//Db.update("update order_object_table set is_publish=? where  is_del=0 and object_id in (?)", isPub,ids);
				for(String id : oids){
					Record r = Db.findById("service_type", StringUtil.nullToLong(id));
					r.set("is_pub", isPub);
					String version=Util.getCurrentDate(new Date());
					r.set("version", version);
					Db.update("service_type", r);
					commonCacheService.deleteObject(CacheConstants.ORDER_FORM, id);
					commonCacheService.deleteObject(CacheConstants.PLAN_FORM, id);
					commonCacheService.deleteObject(CacheConstants.ORDER_FORM_VERSION, id);
					commonCacheService.deleteObject(CacheConstants.ORDER_BANNER, id);
//					Record r = Db.findById("order_object_table","object_id", StringUtil.nullToLong(id));
//					if(r!=null){
//						r.set("is_publish", isPub);
//						String version=Util.getCurrentDate(new Date());
//						r.set("version", version);
//						Db.update("order_object_table","object_id", r);
//						
//					}
				}
			}
		}
		commonCacheService.deleteObject(CacheConstants.CACHE_ALLSERVICETYPE);
		commonCacheService.delObjectContainsKey(CacheConstants.DICT_FORM_KEY, true);
	   flag=true;
	   return flag;
	}

	@Override
	public Boolean pubAllObjectTable(String isPub) {
		boolean flag=false;
		//Db.update("update order_object_table set is_publish=? where is_del=0", isPub);
		List<Record> list = Db.find("SELECT t.id FROM service_type t WHERE t.is_del=0");
		if(list!=null&&list.size()>0){
			for(Record r : list){
				r.set("is_pub", isPub);
				String version=Util.getCurrentDate(new Date());
				r.set("version", version);
				Db.update("service_type", r);
				commonCacheService.deleteObject(CacheConstants.ORDER_FORM, r.getLong("id").toString());
				commonCacheService.deleteObject(CacheConstants.PLAN_FORM, r.getLong("id").toString());
				commonCacheService.deleteObject(CacheConstants.ORDER_FORM_VERSION, r.getLong("id").toString());
				commonCacheService.deleteObject(CacheConstants.ORDER_BANNER, r.getLong("id").toString());
			}
		}
		commonCacheService.deleteObject(CacheConstants.CACHE_ALLSERVICETYPE);
		commonCacheService.delObjectContainsKey(CacheConstants.DICT_FORM_KEY, true);
	   flag=true;
	   return flag;
	}
	
}

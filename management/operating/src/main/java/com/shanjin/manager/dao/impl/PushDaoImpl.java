package com.shanjin.manager.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.impl.CommonCacheServiceImpl;
import com.shanjin.manager.Bean.Agent;
import com.shanjin.manager.constant.Constant.PAGE;
import com.shanjin.manager.constant.Constant.SORT;
import com.shanjin.manager.dao.PushDao;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

public class PushDaoImpl implements PushDao{
	private static ICommonCacheService commonCacheService = new CommonCacheServiceImpl();
	/**
	 * 
	 * @param param
	 * @param flag(true:查列表，false:查总数)
	 * @return
	 */
	private String pushListSql(Map<String, String[]> param,boolean flag){
		StringBuffer sql = new StringBuffer();
		if(flag){
			sql.append(" SELECT pi.*,st.service_type_name as service_name FROM push_info pi  ");
			sql.append("  LEFT JOIN service_type st ON pi.service_type_id=st.id ");

		}else{
			sql.append("  SELECT COUNT(1) FROM push_info pi LEFT JOIN service_type st ON pi.service_type_id=st.id");
		}
		sql.append("    WHERE 1=1 ");
		String service_name = "";
		if(StringUtil.isNotNullMap(param,"service_type_name")){
			service_name = StringUtil.null2Str(param.get("service_type_name")[0]);
			sql.append(" and st.service_type_name like '%").append(service_name).append("%'");
		}

		if(flag){
			String property = "id";
			String direction = SORT.DESC;
			if(StringUtil.isNotNullMap(param,"sort")){
				Map<String,String> sortMap = Util.sortMap(StringUtil.null2Str(param.get("sort")[0]));
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
			int start = 0;
			if(StringUtil.isNotNullMap(param,"start")){
				start = StringUtil.nullToInteger(param.get("start")[0]);
			}
			int pageSize = PAGE.PAGESIZE;
			if(StringUtil.isNotNullMap(param,"limit")){
				pageSize = StringUtil.nullToInteger(param.get("limit")[0]);
			}
			sql.append(" limit ");
			sql.append(start);
			sql.append(",");
			sql.append(pageSize);
		}
		return sql.toString();
	}
	
	@Override
	public List<Record> pushList(Map<String, String[]> param) {
		List<Record> list = new ArrayList<Record>();
		list = Db.find(pushListSql(param,true));
		return list;
	}

	@Override
	public long pushListSize(Map<String, String[]> param) {
		return Db.queryLong(pushListSql(param,false));
	}

	@Override
	public int savePush(Map<String, String[]> param) {
		int flag = 0 ;//0:保存失败，1：保存成功，2：同一service重复保存
		boolean isUpdate = false; // 标识是否是更新
		int id = 0;
		String service_id = "";
		if(StringUtil.isNotNullMap(param,"service_id")){
			service_id = StringUtil.null2Str(param.get("service_id")[0]);
		}
		if(StringUtil.isNotNullMap(param,"id")){
			isUpdate = true;
			id = StringUtil.nullToInteger(param.get("id")[0]);
		}

		int push_type = 0;
		if(StringUtil.isNotNullMap(param,"push_type")){
			push_type = StringUtil.nullToInteger(param.get("push_type")[0]);
		}
		int push_range = 0;
		if(StringUtil.isNotNullMap(param,"push_range")){
			push_range = StringUtil.nullToInteger(param.get("push_range")[0]);
		}
		if(checkApp(service_id,isUpdate,id)){
			flag = 2;
			return flag;
		}
		
		if(isUpdate){
			Record record = Db.findById("push_info", id).set("service_type_id", service_id).set("push_type", push_type).set("push_range", push_range);
			Db.update("push_info", record);
			flag = 1;
		}else{
			Record record = new Record();
			record.set("service_type_id", service_id).set("push_type", push_type).set("push_range", push_range);
			Db.save("push_info", record);
			flag = 1;
		}
		
		commonCacheService.deleteObject(CacheConstants.PUSH_RANGE);
		return flag;
	}
	
	/**
	 * 检查service类型是否存在
	 * @param service service
	 * @param isEdit  true:修改 ,false:新增
	 * @param id 更新的记录主键ID
	 * @return
	 */
	private boolean checkApp(String service_id,boolean isEdit,int id){
		boolean flag = false;
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(1) FROM push_info WHERE service_type_id=").append(service_id);
		if(isEdit){
			// 更新
		    sql.append(" and id<>").append(id);	
		}
		long count = Db.queryLong(sql.toString());
		if(count>0){
			flag = true;
		}
		return flag;
	}

	@Override
	public boolean deletePushs(String ids) {
		boolean flag = false;
		String sql = "DELETE FROM push_info WHERE id in("+ids+")";
		Db.update(sql);
		flag = true;
		return flag;
	}

	@Override
	public List<Record> getPushConfigList(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		sql.append("select p.id,p.service_type_id as gxfwServiceTypeId,p.relevance_service_type_id as serviceTypeId,gs.service_type_name as gxfwName,hs.service_type_name as serviceTypeName from push_config p left join service_type gs on p.service_type_id=gs.id and gs.is_del=0 left join service_type hs on p.relevance_service_type_id=hs.id and hs.is_del=0 where ");
		totalSql.append("select count(1) as total from push_config p left join service_type gs on p.service_type_id=gs.id and gs.is_del=0 left join service_type hs on p.relevance_service_type_id=hs.id and hs.is_del=0 where ");
		strLikeFilter.put("gs.service_type_name", paramMap.get("gxfwName"));
		strLikeFilter.put("hs.service_type_name", paramMap.get("serviceTypeName"));
		
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		
		
		long total = Db.find(totalSql.toString()).get(0).getLong("total");
		
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<Record> configs = Db.find(sql
				.toString());
		if (configs.size() > 0) {
			configs.get(0).set("total", total);
		}
		return configs;	
	}

	@Override
	public boolean deletePushConfig(String ids) {
		boolean flag = false;
		String[] id=ids.split(",");
		if (id.length > 0) {
			for (int i = 0; i < id.length; i++) {
				Db.deleteById("push_config", id[i]);
			}
		}
		commonCacheService.deleteObject(CacheConstants.RELEVANCE_SERVICE_TYPE_ID);
		flag = true;
		return flag;
	}

	@Override
	public int addPushConfig(Map<String, String[]> param) {
		int flag=0;
		String gxfwServiceTypeId=param.get("gxfwServiceTypeId")[0];
		String serviceTypeId=param.get("serviceTypeId")[0];
		if(gxfwServiceTypeId.equals(serviceTypeId)){
			flag=1;
			return flag;
		}
		String sql="select * from push_config p where p.service_type_id=? and p.relevance_service_type_id=?";
		List<Record> lis1=Db.find(sql,gxfwServiceTypeId,serviceTypeId);
		
		if(lis1!=null&&lis1.size()>0){
			flag=1;
			return flag;
		}
		
		Record r=new Record();
		r.set("service_type_id", gxfwServiceTypeId).set("relevance_service_type_id", serviceTypeId);
		Db.save("push_config", r);
		
		commonCacheService.deleteObject(CacheConstants.RELEVANCE_SERVICE_TYPE_ID);
		flag=2;
		return flag;
	}

	@Override
	public List<Record> getRestrictUpdate(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		
		sql.append("select r.* from restrict_update r ");
		totalSql.append("select count(1) as total from restrict_update r");
		
		long total = Db.find(totalSql.toString()).get(0).getLong("total");
		
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<Record> configs = Db.find(sql
				.toString());
		if (configs.size() > 0) {
			configs.get(0).set("total", total);
		}
		return configs;	
	}

	@Override
	public Boolean deleteRestrictUpdate(String ids) {
		boolean flag = false;
		String[] id=ids.split(",");
		if (id.length > 0) {
			for (int i = 0; i < id.length; i++) {
				Db.deleteById("restrict_update", id[i]);
			}
		}
		
		flag = true;
		return flag;
	}

	@Override
	public Boolean saveRestrictUpdate(Map<String, String[]> param) {
		boolean flag=false;
		String id = StringUtil.null2Str(param.get("id")[0]);
		String url=param.get("url")[0];
		String interval=param.get("interval")[0];
		
		boolean isUpdate = false; 
		if(StringUtil.isNotNullMap(param, "id")){
			isUpdate = true;
		}
		
		String sql="select * from restrict_update where url=?";
		List<Record> res=Db.find(sql,url);
 		Record re=null;
		if(isUpdate){
			if(res!=null&&res.size()>0){
				String newId=res.get(0).getInt("id").toString();
				if(!id.equals(newId)){
					return flag;
				}
			}
			re=	Db.findById("restrict_update", id).set("url", url).set("interval", interval);
			Db.update("restrict_update", re);
		}else{
			// 新增
			if(res!=null&&res.size()>0){
					return flag;
			}
			re=new Record();
			re.set("url", url).set("interval", interval);
			Db.save("restrict_update", re);
		}
		flag=true;
		return flag;
	}

	@Override
	public int startOrstopRestrictUpdate(Map<String, String[]> param) {
		int flag=0;
		String id = StringUtil.null2Str(param.get("id")[0]);
		String isPublished=param.get("isPublished")[0];
		Record re=null;
		if(isPublished.equals("0")){
		 re=Db.findById("restrict_update", id).set("isPublished", 0);
		Db.update("restrict_update", re);
		flag=1;
		}else{
		 re=Db.findById("restrict_update", id).set("isPublished", 1);
		Db.update("restrict_update", re);
		flag=2;
		}
		commonCacheService.deleteObject(CacheConstants.RESTRICT_UPDATE_URL);
		return flag;
	}

}

package com.shanjin.manager.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.impl.CommonCacheServiceImpl;
import com.shanjin.manager.Bean.MerchantServiceTag;
import com.shanjin.manager.Bean.MerchantsInfo;
import com.shanjin.manager.constant.Constant;
import com.shanjin.manager.constant.Constant.PAGE;
import com.shanjin.manager.constant.Constant.SORT;
import com.shanjin.manager.dao.ServiceTagDao;
import com.shanjin.manager.utils.HttpUtil;
import com.shanjin.manager.utils.MessageUtil;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

public class ServiceTagDaoImpl implements ServiceTagDao{
	private static ICommonCacheService commonCacheService = new CommonCacheServiceImpl();
	/**
	 * 
	 * @param param
	 * @param flag (true:查列表，false:查总数)
	 * @return
	 */
	private String merchantServiceTagListSql(Map<String, String[]> param ,boolean flag){
		StringBuffer sql = new StringBuffer();
		if(flag){
			sql.append(" SELECT st.*,mc.telephone,mi.name as merchant_name,mi.detail, ");
			sql.append(" mi.app_type,mi.province,mi.city,mi.location_address ");
			sql.append(" FROM service_type_apply st ");
			sql.append(" LEFT JOIN merchant_info mi ON st.merchant_id=mi.id ");
			sql.append(" LEFT JOIN merchant_contact mc ON st.merchant_id=mc.merchant_id ");
			sql.append(" WHERE st.is_del=0 ");
		}else{
			sql.append(" SELECT count(1) from service_type_apply st ");
			sql.append(" LEFT JOIN merchant_contact mc ON st.merchant_id=mc.merchant_id ");
			sql.append(" WHERE st.is_del=0  ");
		}
		String name = "";
		if(StringUtil.isNotNullMap(param,"name")){
			name = StringUtil.null2Str(param.get("name")[0]);
			sql.append(" and st.name like '%").append(name).append("%'");
		}
		if(StringUtil.isNotNullMap(param,"telephone")){
			String telephone = StringUtil.null2Str(param.get("telephone")[0]);
			sql.append(" and mc.telephone like '%").append(telephone).append("%'");
		}
		if(StringUtil.isNotNullMap(param,"is_audit")){
			int is_audit = StringUtil.nullToInteger(param.get("is_audit")[0]);
			sql.append("  and st.is_audit=").append(is_audit);
		}
		
		sql.append(Util.getdateFilter("st.join_time",
				param.get("start_time"), param.get("off_time")));
		
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
	public List<Record> getMerchantServiceTagList(Map<String, String[]> param) {
		List<Record> list=new ArrayList<Record>();
		list = Db.find(merchantServiceTagListSql(param,true));
		return list;
	}

	@Override
	public boolean deleteMerchantServiceTag(String ids) {
		String[] id=ids.split(",");
		boolean flag=false;
		Record re=null;
		for(int i=0;i<id.length;i++){
			// 更新
			re=	Db.findById("service_type_apply", id[i]).set("is_del", 1);
			Db.update("service_type_apply", re);
		 }
	   flag=true;
	   return flag;
	}

	@Override
	public boolean auditMerchantServiceTag(final String ids,final String status, String calogId, String demand) {
		Boolean flag = false;
		String[] id=ids.split(",");
		Record r=null;
		Record rc=null;
		Record rcat=null;
		String sql="select id from service_type where is_del=0 and service_type_name=?";
		if("1".equals(status)){
			if(calogId==null||calogId.equals("")){
				return flag;
			}
			Record rco=Db.findById("service_type_apply", id[0]);
			List<Record> lis=Db.find(sql,rco.getStr("name"));
			if(lis!=null&&lis.size()>0){
				return flag;
			}else{
				for(int i=0;i<id.length;i++){
				Record ro=Db.findById("service_type_apply", id[i]);
				if(ro==null||ro.getInt("is_audit")!=0){
					return flag;
				}
				}
				//所有审核状态未发生改变，并且没有相同的标签审核通过，开始加入标签
				r=new Record();
				r.set("service_type_name", rco.getStr("name")).set("service_type_id", 1).set("app_type", "gxfw")
				.set("app_name", "个性服务");
				Db.save("service_type", r);
				
				//把通过的标签加到个性服务的分类里
				
				rcat=new Record();
				rcat.set("catalog_id", calogId).set("service_id", r.getLong("id")).set("status", 0);
				Db.save("catalog_service", rcat);
				
				//标签加入成功，返回服务Id，开始更新服务商，和更新标签申请库，并且发送消息给商户
				for(int i=0;i<id.length;i++){
					Record re=Db.findById("service_type_apply", id[i]);
					
					String sqb="update service_type_apply set is_audit=? where id=?";
					Db.update(sqb.toString(),status,id[i]);
					
					rc=new Record();
					rc.set("merchant_id", re.getLong("merchant_id")).set("service_type_id", r.getLong("id")).set("app_type", "gxfw");
					Db.save("merchant_service_type", rc);
					//更新搜索引擎
					HttpUtil.SendGET(Constant.WEB_SERACH_URL+"addDocument","indexName=gxfwindex&ids="+id[i]);
					
					sendMessage(id[i],status,demand);
				}
				flushCaheCatlog(calogId);
				flag=true;
			}
			
			
		}else{
			String sqb="update service_type_apply set is_audit=?,demand=? where id=?";
			int len=id.length;
			for(int i=0;i<len;i++){
			Db.update(sqb.toString(),status,demand,id[i]);
			sendMessage(id[i],status,demand);
			}
			flag=true;
		}
		
		commonCacheService.deleteObject(CacheConstants.MERCHANT_SERVICES_LIST);
		return flag;
		
	}
	private void flushCaheCatlog(String catId){
		commonCacheService.deleteObject(CacheConstants.MERCHANT_CATALOG_TOP);
		commonCacheService.deleteObject(CacheConstants.USER_HOME_HOTSEARCH__SERVICES_UNDER_CATALOG,catId);
		commonCacheService.deleteObject(CacheConstants.CATALOG_AND_SERVICES,catId);
		commonCacheService.deleteObject(CacheConstants.CATALOG_AND_SERVICES_FOR_MERCHANT,catId);
	    List<Record> res=Db.find("select id from catalog where level=0 and alias='gxfw'");
	    if(res!=null&&res.size()>0){
	    	String gxfwId=res.get(0).getInt("id").toString();
	    	commonCacheService.deleteObject(CacheConstants.CATALOG_AND_SERVICES_FOR_MERCHANT,gxfwId);
	    }
	}
	
	private void sendMessage(String ids, String status, String demand) {
		if (!StringUtil.isNullStr(ids) && ids.length() > 0) {
			String[] idArr = ids.split(",");
			if (idArr != null && idArr.length > 0) {
				String title = "";
				String contentFront = "";
				String contentLast = "";
				if ("1".equals(status)) {
					// 通过
					title = "个性服务关键字审核通过";
					contentFront = "恭喜您，服务项目关键字【";
					contentLast = "】申请成功。";
				} else {
					// 不通过
					title = "个性服务关键字审核不通过";
					contentFront = "该关键字【";
					if(StringUtil.isNullStr(demand)){
						contentLast = "】申请失败，不符合服务规范或包含国家禁止内容，如果您对该审核结果有异议，可拨打我们的客服热线400-020-0505咨询反馈。";
					}else{
						contentLast = "】"+demand+"如果您对该审核结果有异议，可拨打我们的客服热线400-020-0505咨询反馈。";
					}
					
				}
				for (String id : idArr) {
					Record merchantServiceTag = Db.findById(
							"service_type_apply", id);
					if (merchantServiceTag != null) {
						MessageUtil.createCustomerMessage(
								null,
								1,
								1,
								merchantServiceTag.getLong("merchant_id"),
								title,
								concatContent(contentFront,
										merchantServiceTag.getStr("name"),
										contentLast));
					}
				}
			}
		}
	}
	
	private String concatContent(String front,String mid,String last){
		StringBuffer sb = new StringBuffer();
		sb.append(front).append(mid).append(last);
		return sb.toString();
	}
	
	/**
	 * 
	 * @param param
	 * @param flag (true:查列表，false:查总数)
	 * @return
	 */
	private String serviceTagListSql(Map<String, String[]> param ,boolean flag){
		StringBuffer sql = new StringBuffer();
		if(flag){
			sql.append(" SELECT st.* from service_tag st ");
		}else{
			sql.append(" SELECT count(1) from service_tag st ");
		}
	
		sql.append(" WHERE st.is_del=0  ");
		String tag = "";
		if(StringUtil.isNotNullMap(param,"tag")){
			tag = StringUtil.null2Str(param.get("tag")[0]);
			sql.append(" and st.tag like '%").append(tag).append("%'");
		}
		if(StringUtil.isNotNullMap(param,"is_audit")){
			int is_audit = StringUtil.nullToInteger(param.get("is_audit")[0]);
			sql.append("  and st.is_audit=").append(is_audit);
		}
		if(StringUtil.isNotNullMap(param,"is_recommend")){
			int is_recommend = StringUtil.nullToInteger(param.get("is_recommend")[0]);
			sql.append("  and st.is_recommend=").append(is_recommend);
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
	public List<Record> getServiceTagList(Map<String, String[]> param) {
		List<Record> list = Db.find(serviceTagListSql(param,true));
		return list;
	}

	@Override
	public boolean deleteServiceTag(String ids) {
		String[] id=ids.split(",");
		boolean flag=false;
		Record re=null;
		for(int i=0;i<id.length;i++){
			// 更新
			re=	Db.findById("service_tag", id[i]).set("is_del", 1);
			Db.update("service_tag", re);
		 }
	   flag=true;
	   commonCacheService.deleteObject(CacheConstants.MERCHANT_TRADE_TAG);
	   return flag;
	}
	@Override
	public boolean auditServiceTag(String ids,String status) {
		Boolean flag = false;
		String sql = "update service_tag set is_audit=? where id IN (?)";
		Db.update(sql,status,ids);
		flag = true;
		return flag;
	}

	@Override
	public int saveServiceTag(Map<String, String[]> param) {
		String id = StringUtil.null2Str(param.get("id")[0]);
		boolean isUpdate = false ; 
		if(StringUtil.isNotNullMap(param, "id")){
			isUpdate = true;
		}
		int is_audit = StringUtil.nullToInteger(param.get("is_audit")[0]);
		int is_recommend = StringUtil.nullToInteger(param.get("is_recommend")[0]);
		String tag=param.get("tag")[0];
		int flag = 0;//0:失败，1：成功，2：标签已存在
		if(checkTag(tag,isUpdate,id)){
			flag = 2;
			return flag;
		}
		if(isUpdate){
			// 更新
			Record record = Db.findById("service_tag", id).set("is_recommend", is_recommend)
			.set("tag", tag).set("is_audit",is_audit);
			Db.update("service_tag", record);
		}else{
			// 新增
			Record record = new Record();
			record.set("is_recommend", is_recommend).set("join_time", new Date())
			.set("tag", tag).set("is_audit",is_audit);	
			Db.save("service_tag", record);
		}
		flag = 1;
		commonCacheService.deleteObject(CacheConstants.MERCHANT_TRADE_TAG);
		return flag;
	}
	
	private boolean checkTag(String tag,boolean isUpdate,String id){
		boolean flag = false;
		long count = 0;
		String sql = "";
		if(isUpdate){
			sql = " select count(1) from service_tag t where t.is_del=0 and t.tag = ? and id<>? ";
			count = Db.queryLong(sql, tag, id);
		}else{
			sql = " select count(1) from service_tag t where t.is_del=0 and t.tag = ? ";
			count = Db.queryLong(sql, tag);
		}
		if(count>0)
			flag = true;
		return flag;
	}
	private boolean checkPersonalTag(String tag,boolean isUpdate,String id){
		boolean flag = false;
		long count = 0;
		String sql = "";
		if(isUpdate){
			sql = " select count(1) from personal_tag t where t.is_del=0 and t.tag = ? and id<>? ";
			count = Db.queryLong(sql, tag, id);
		}else{
			sql = " select count(1) from personal_tag t where t.is_del=0 and t.tag = ? ";
			count = Db.queryLong(sql, tag);
		}
		if(count>0)
			flag = true;
		return flag;
	}

	@Override
	public long getMerchantServiceTagListCount(Map<String, String[]> param) {
		long count = Db.queryLong(merchantServiceTagListSql(param,false));
		return count;
	}


	@Override
	public long getServiceTagListCount(Map<String, String[]> param) {
		long count = Db.queryLong(serviceTagListSql(param,false));
		return count;
	}


	@Override
	public List<MerchantServiceTag> exportExcel(Map<String, String[]> param) {
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT;  
		StringBuffer sql = new StringBuffer();
			sql.append(" SELECT st.*,(case st.is_audit when 1 then '已通过' when 2 then '未通过' else '待审核' end) as isAudit,mc.telephone,mi.name as merchant_name,mi.detail, ");
			sql.append(" mi.app_type,mi.province,mi.city,mi.location_address ");
			sql.append(" FROM service_type_apply st ");
			sql.append(" LEFT JOIN merchant_info mi ON st.merchant_id=mi.id ");
			sql.append(" LEFT JOIN merchant_contact mc ON st.merchant_id=mc.merchant_id ");
			sql.append(" WHERE st.is_del=0 ");
		
			
		String name = "";
		if(StringUtil.isNotNullMap(param,"name")){
			name = StringUtil.null2Str(param.get("name")[0]);
			sql.append(" and st.name like '%").append(name).append("%'");
		}
		if(StringUtil.isNotNullMap(param,"telephone")){
			String telephone = StringUtil.null2Str(param.get("telephone")[0]);
			sql.append(" and mc.telephone like '%").append(telephone).append("%'");
		}
		if(StringUtil.isNotNullMap(param,"is_audit")){
			int is_audit = StringUtil.nullToInteger(param.get("is_audit")[0]);
			sql.append("  and st.is_audit=").append(is_audit);
		}
	
		sql.append(Util.getExportdateFilter("st.join_time",
				param.get("start_time"), param.get("off_time")));
		
			sql.append(" limit ");
			sql.append(pageNumber);
			sql.append(",");
			sql.append(pageSize);
		
			List<MerchantServiceTag> merchantServiceTagList = MerchantServiceTag.dao.find(sql
					.toString());
			return merchantServiceTagList;
	}


	@Override
	public List<Record> getPersonalTagList(Map<String, String[]> param) {
		List<Record> list = Db.find(personalTagListSql(param,true));
		return list;
	}


	@Override
	public long getPersonalTagListCount(Map<String, String[]> param) {
		long count = Db.queryLong(personalTagListSql(param,false));
		return count;
	}


	@Override
	public Boolean deletePersonalTag(String ids) {
		String[] id=ids.split(",");
		boolean flag=false;
		Record re=null;
		for(int i=0;i<id.length;i++){
			// 更新
			re=	Db.findById("personal_tag", id[i]).set("is_del", 1);
			Db.update("personal_tag", re);
		 }
	   flag=true;
	   return flag;
		
	}


	@Override
	public int savePersonalTag(Map<String, String[]> param) {
		String id = StringUtil.null2Str(param.get("id")[0]);
		boolean isUpdate = false ; 
		if(StringUtil.isNotNullMap(param, "id")){
			isUpdate = true;
		}
		int is_audit = StringUtil.nullToInteger(param.get("is_audit")[0]);
		int is_recommend = StringUtil.nullToInteger(param.get("is_recommend")[0]);
		String tag=param.get("tag")[0];
		int flag = 0;//0:失败，1：成功，2：标签已存在
		if(checkPersonalTag(tag,isUpdate,id)){
			flag = 2;
			return flag;
		}
		if(isUpdate){
			// 更新
			Record record = Db.findById("personal_tag", id).set("is_recommend", is_recommend)
			.set("tag", tag).set("is_audit",is_audit);
			Db.update("service_tag", record);
		}else{
			// 新增
			Record record = new Record();
			record.set("is_recommend", is_recommend).set("join_time", new Date())
			.set("tag", tag).set("is_audit",is_audit);	
			Db.save("personal_tag", record);
		}
		flag = 1;
		return flag;
	}
	/**
	 * 
	 * @param param
	 * @param flag (true:查列表，false:查总数)
	 * @return
	 */
	private String personalTagListSql(Map<String, String[]> param ,boolean flag){
		StringBuffer sql = new StringBuffer();
		if(flag){
			sql.append(" SELECT st.* from personal_tag st ");
		}else{
			sql.append(" SELECT count(1) from personal_tag st ");
		}
	
		sql.append(" WHERE st.is_del=0  ");
		String tag = "";
		if(StringUtil.isNotNullMap(param,"tag")){
			tag = StringUtil.null2Str(param.get("tag")[0]);
			sql.append(" and st.tag like '%").append(tag).append("%'");
		}
		if(StringUtil.isNotNullMap(param,"is_audit")){
			int is_audit = StringUtil.nullToInteger(param.get("is_audit")[0]);
			sql.append("  and st.is_audit=").append(is_audit);
		}
		if(StringUtil.isNotNullMap(param,"is_recommend")){
			int is_recommend = StringUtil.nullToInteger(param.get("is_recommend")[0]);
			sql.append("  and st.is_recommend=").append(is_recommend);
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
	public Map getTagRepeat(String id) {
		String name=Db.findById("service_type_apply", id).getStr("name");
		String sql="select * from service_type_apply s where s.is_del=0 and s.is_audit=0 and s.name=? ";
		List<Record> res=Db.find(sql,name);
		StringBuffer ids = new StringBuffer();
		int i=0;
		Map map=new HashMap();
		if(res!=null&&res.size()>0){
			for(Record r:res){
			 if(i==0){
				 ids.append(r.getLong("id")); 
			 }else{
				 ids.append(",").append(r.getLong("id"));  
			 }
			 i++;
			}
		}
		map.put("id", ids.toString());
		map.put("total", i);
		return map;
	}
}

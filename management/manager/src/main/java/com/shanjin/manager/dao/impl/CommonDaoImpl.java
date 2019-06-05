package com.shanjin.manager.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;








import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.impl.CommonCacheServiceImpl;
import com.shanjin.manager.Bean.AppInfo;
import com.shanjin.manager.Bean.MerchantsInfo;
import com.shanjin.manager.Bean.SystemResource;
import com.shanjin.sso.bean.SystemUserInfo;
import com.shanjin.manager.constant.Constant;
import com.shanjin.manager.constant.Constant.PAGE;
import com.shanjin.manager.constant.Constant.SORT;
import com.shanjin.manager.dao.CommonDao;
import com.shanjin.manager.utils.BusinessUtil;
import com.shanjin.manager.utils.MqUtil;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

public class CommonDaoImpl implements CommonDao {

	@Override
	public List<Record> getAppTypeByPackType(String packageType) {
		String sql= "";
		if(!StringUtil.isNullStr(packageType)&&Constant.PACKAGE_TYPE_USER.equals(packageType)){
			// 用户版
			sql="select app_type,app_name from app_info where is_del=0";
		}else{
			// 商户版
			sql="select app_type,app_name from merchant_app_info where is_del=0";
		}
		List<Record> appType=Db.find(sql);
		return appType;
	}
	
	public List<Record> getAppType() {
		String sql="select app_type,app_name from merchant_app_info where is_del=0";
		List<Record> appType=Db.find(sql);
		return appType;
	}

	public List<Record> getMerOrderStatus() {
	String sql="select dict_key,dict_value from dictionary where dict_type='merchantOrderStatus' ";
	//String s=SqlKit.sql("common.getMerOrderStatus");
	List<Record> merOrderStatus=Db.find(sql);	
	return merOrderStatus;
	}

	public List<Record> getUserOrderStatus() {
		String sql="select dict_key,dict_value from dictionary where dict_type='managerOrderStatusMap' ";
		//String s=SqlKit.sql("permission.getUserOrderStatus");
		List<Record> userOrderStatus=Db.find(sql);
		return userOrderStatus;
	}

	public List<Record> getServiceType(String app_type) {
		List<Record> serviceType = new ArrayList<Record>();
		serviceType = getService(app_type);
		return serviceType;
	}


	private List<Record> getService(String app_type) {
		String sql="select service_type_id as service_type,service_type_name as service_name from service_type where is_del=0 and app_type=? and parent_id is null";
		List<Record> serviceType=Db.find(sql,app_type);
		return serviceType;
	}


	public List<Record> getWithdraw() {
		String sql="select id,dict_value from dictionary where dict_type='bank' ";
		List<Record> withdrawList=Db.find(sql);	
		return withdrawList;
	}

	public List<Record> getArea(Long parentId) {
		String sql = "SELECT * FROM area t WHERE t.parent_id IS NULL ORDER BY t.index_str";
		if(parentId!=null){
			sql = "SELECT * FROM area t WHERE t.parent_id ="+parentId;
		}
		List<Record> areaList=Db.find(sql);
		return areaList;
	}

	public List<Record> getAppList() {
		String sql="SELECT * from merchant_app_info t where t.is_del<>1 ";
		List<Record> appList=Db.find(sql);	
		return appList;
	}

	/**
	 * 查询用户资源权限
	 */
	public String getUserPermission(Long userId) {
		// 通过角色查询
		String perms = "";
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT ri.* FROM authority_resource_info ri WHERE ri.disabled<>1 AND ri.id in( ");
		sql.append(" SELECT rr.resId FROM authority_role_resource rr WHERE rr.roleId in( ");
		sql.append(" SELECT ur.roleId FROM authority_user_role ur WHERE ur.userId=").append(userId).append("))");
		List<Record> list=Db.find(sql.toString());	
		if(list!=null&&list.size()>0){
			for(Record r : list){
				perms = perms + StringUtil.null2Str(r.getStr("linkPath"))+",";
			}
		}
		// 通过群组查询
		StringBuffer groupSql = new StringBuffer();
		groupSql.append(" SELECT ri.* FROM authority_resource_info ri WHERE ri.disabled<>1 AND ri.id in( ");
		groupSql.append(" SELECT rr.resId FROM authority_role_resource rr WHERE rr.roleId in( ");
		groupSql.append(" SELECT gr.roleId FROM authority_group_role gr WHERE gr.groupId in( ");
		groupSql.append(" SELECT ug.groupId FROM authority_user_group ug WHERE ug.userId=").append(userId).append(")))");
		List<Record> groupList=Db.find(groupSql.toString());	
		if(groupList!=null&&groupList.size()>0){
			for(Record r : groupList){
				String link = StringUtil.null2Str(r.getStr("linkPath"));
				if(!isExist(link,list)){
					// 之前没有
					perms = perms + StringUtil.null2Str(r.getStr("linkPath"))+",";
				}
			}
		}
		if(perms!=null&&perms!=""){
			if(perms.endsWith(",")){
				perms = perms.substring(0, perms.length()-1);
			}
		}
		
		return perms;
	}
	
	/**
	 * 字符串link是否在list中存在
	 * @param link
	 * @param list
	 * @return
	 */
	private boolean isExist(String link,List<Record> list){
		boolean flag = false;
		if(list!=null&&list.size()>0){
			for(Record r : list){
				if(link.equals(r.getStr("linkPath"))){
					 flag = true;
					 break;
				}
			}
		}
		return flag;
	}
	
	public List<AppInfo> userApp(Long userId) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT a.* FROM merchant_app_info a WHERE a.is_del<>1 and a.id in(");
		sql.append(" SELECT up.appId FROM authority_user_app up LEFT JOIN authority_user_info u");
		sql.append(" ON up.userId=u.id WHERE u.userType=4 AND up.userId=").append(userId).append(")");
		List<AppInfo> appList=new ArrayList<AppInfo>();
		appList=AppInfo.dao.find(sql.toString());
		return appList;
	}

	public List<Record> getPredilection(String travel_predilection) {
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT group_concat(d.dict_value) AS travel_predilection FROM dictionary d WHERE d.dict_type='lxzTravelPredilection' and d.dict_key in (").append(travel_predilection).append(")");
		List<Record> predilectionList=Db.find(sql.toString());
		return predilectionList;
	}

	public List<Record> getCleanType(String service_item) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT group_concat(d.dict_value) AS service_item_value FROM dictionary d WHERE d.dict_type='zybCleanType' and d.dict_key in (").append(service_item).append(")");
		List<Record> cleanTypeList=Db.find(sql.toString(),service_item);
		return cleanTypeList;
	}

	public Map<String, Long> getAllData(SystemUserInfo sysUser) {
		// 1：公司员工  2：省代理  3：市代理  4：项目代理
		Map<String, Long> data=new HashMap<String, Long>();
		StringBuffer users=new StringBuffer();
		users.append("select count(1) as total from user_info where is_del=0 ");
		StringBuffer merchants=new StringBuffer();
		merchants.append("select count(1) as total from merchant_info where is_del=0 ");
		StringBuffer orders=new StringBuffer();
		orders.append("select count(1) as total from order_info where is_del=0 ");
		StringBuffer agents=new StringBuffer();
		agents.append("select count(1) as total from authority_user_info where userType in (2,3,4) and isDel=0 ");
		StringBuffer provinceAgents=new StringBuffer();
		provinceAgents.append("select count(1) as total from authority_user_info where userType=2 and isDel=0 ");
		StringBuffer cityAgents=new StringBuffer();
		cityAgents.append("select count(1) as total from authority_user_info where userType=3 and isDel=0 ");
		StringBuffer appAgents=new StringBuffer();
		appAgents.append("select count(1) as total from authority_user_info where userType=4 and isDel=0 ");
		int userType=sysUser.getInt("userType");
		if(Constant.ADMIN.equals(sysUser.getStr("userName"))){
			
		}else{
			if(userType==2){
				merchants.append(" and province='").append(sysUser.getStr("provinceDesc")).append("'");
				orders.append(" and province='").append(sysUser.getStr("provinceDesc")).append("'");
				agents.append(" and provinceDesc='").append(sysUser.getStr("provinceDesc")).append("'");
				provinceAgents.append(" and provinceDesc='").append(sysUser.getStr("provinceDesc")).append("'");
				cityAgents.append(" and provinceDesc='").append(sysUser.getStr("provinceDesc")).append("'");
				appAgents.append(" and provinceDesc='").append(sysUser.getStr("provinceDesc")).append("'");
		}else if(userType==3){
			merchants.append(" and province='").append(sysUser.getStr("provinceDesc")).append("'");
			String[] par=new String[1];
			par[0]=sysUser.getStr("provinceDesc");
			if(StringUtil.matchProvince(par)){
				String match="%"+sysUser.getStr("provinceDesc")+"市"+sysUser.getStr("cityDesc") +"%";
				merchants.append("  and location_address like '").append(match).append("'");
			}else{
			merchants.append(" and city='").append(sysUser.getStr("cityDesc")).append("'");
			}
			orders.append(" and province='").append(sysUser.getStr("provinceDesc")).append("' and city='");
			orders.append(sysUser.getStr("cityDesc")).append("'");
			agents.append(" and provinceDesc='").append(sysUser.getStr("provinceDesc")).append("' and city='");
			agents.append(sysUser.getStr("cityDesc")).append("'");
			provinceAgents.append(" and provinceDesc='").append(sysUser.getStr("provinceDesc")).append("' and cityDesc='");
			provinceAgents.append(sysUser.getStr("cityDesc")).append("'");
			cityAgents.append(" and provinceDesc='").append(sysUser.getStr("provinceDesc")).append("' and cityDesc='");
			cityAgents.append(sysUser.getStr("cityDesc")).append("'");
			appAgents.append(" and provinceDesc='").append(sysUser.getStr("provinceDesc")).append("' and cityDesc='");
			appAgents.append(sysUser.getStr("cityDesc")).append("'");
		}else if(userType==4){
			merchants.append(" and province='").append(sysUser.getStr("provinceDesc")).append("'");
			String[] par=new String[1];
			par[0]=sysUser.getStr("provinceDesc");
			if(StringUtil.matchProvince(par)){
				String match="%"+sysUser.getStr("provinceDesc")+"市"+sysUser.getStr("cityDesc") +"%";
				merchants.append("  and location_address like '").append(match).append("'");
			}else{
			merchants.append(" and city='").append(sysUser.getStr("cityDesc")).append("'");
			}
			merchants.append(" and app_type in (select app_type from merchant_app_info ap inner join authority_user_app au on ap.id=au.appId where au.userId=").append(sysUser.getLong("id")).append(")");
			orders.append(" and province='").append(sysUser.getStr("provinceDesc")).append("' and city='");
			orders.append(sysUser.getStr("cityDesc")).append("'");
			orders.append(" and app_type in (select app_type from merchant_app_info ap inner join authority_user_app au on ap.id=au.appId where au.userId=").append(sysUser.getLong("id")).append(")");
		}
			
		}
		
		List<Record> user=Db.find(users.toString());
		List<Record> merchant=Db.find(merchants.toString());
		List<Record> order=Db.find(orders.toString());
		List<Record> agent=Db.find(agents.toString());
		List<Record> provinceAgent=Db.find(provinceAgents.toString());
		List<Record> cityAgent=Db.find(cityAgents.toString());
		List<Record> appAgent=Db.find(appAgents.toString());
		if(user!=null){
			data.put("users", user.get(0).getLong("total"));
		}else{
			data.put("users", (long) 0);
		}
		if(merchant!=null){
			data.put("merchants", merchant.get(0).getLong("total"));
		}else{
			data.put("merchants", (long) 0);
		}
		if(order!=null){
			data.put("orders", order.get(0).getLong("total"));
		}else{
			data.put("orders", (long) 0);
		}
		if(agent!=null){
			if(userType==4){
			data.put("agents",(long) 1 );
			}else{
			data.put("agents", agent.get(0).getLong("total"));
			}
		}else{
			data.put("agents", (long) 0);
		}
		if(provinceAgent!=null){
			data.put("provinceAgents",StringUtil.nullToLong(provinceAgent.get(0).getLong("total")));
		}else{
			data.put("provinceAgents", (long) 0);
		}
		if(cityAgent!=null){
			data.put("cityAgents",StringUtil.nullToLong(cityAgent.get(0).getLong("total")));
		}else{
			data.put("cityAgents", (long) 0);
		}
		if(appAgent!=null){
			data.put("appAgents",StringUtil.nullToLong(appAgent.get(0).getLong("total")));
		}else{
			data.put("appAgents", (long) 0);
		}
		
		return data;
	}
	

	public List<Record> getNannyType(String service_item) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT group_concat(d.dict_value) AS service_item FROM dictionary d WHERE d.dict_type='zybNannyType' and d.dict_key in (").append(service_item).append(")");
		List<Record> cleanTypeList=Db.find(sql.toString());
		return cleanTypeList;
	}

	public List<Record> getDietPredilection(String diet_predilection) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT group_concat(d.dict_value) AS diet_predilection FROM dictionary d WHERE d.dict_type='zybDietPredilection' and d.dict_key in (").append(diet_predilection).append(")");
		List<Record> cleanTypeList=Db.find(sql.toString());
		return cleanTypeList;
	}

	public List<Record> getServiceFrequency(String service_frequency) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT group_concat(d.dict_value) AS service_frequency FROM dictionary d WHERE d.dict_type='zybServiceFrequency' and d.dict_key in (").append(service_frequency).append(")");
		List<Record> cleanTypeList=Db.find(sql.toString());
		return cleanTypeList;
	}

	public List<Record> getBeautifyService(String beautify_service_items) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT group_concat(d.dict_value) AS beautify_service_items FROM dictionary d WHERE d.dict_type='cbtBeautifyServiceItems' and d.dict_key in (").append(beautify_service_items).append(")");
		List<Record> cleanTypeList=Db.find(sql.toString());
		return cleanTypeList;
	}

	public List<Record> getRepairPlace(String repair_place) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT group_concat(d.dict_value) AS repair_place FROM dictionary d WHERE d.dict_type='cbtRepairItem' and d.dict_key in (").append(repair_place).append(")");
		List<Record> cleanTypeList=Db.find(sql.toString());
		return cleanTypeList;
	}

	public List<SystemResource> getAllResouse() {
		String sql="select ar.id,ar.resName,ar.remark,ar.disabled,ar.linkPath,ar.isCommon,ar.type from authority_resource_info ar where ar.disabled<>1";
		List<SystemResource> resouse=SystemResource.dao.find(sql);
		return resouse;
	}

	public List<Record> getHouseType(String house_type, String service_type) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT d.dict_value AS house_type FROM dictionary d WHERE d.dict_type=? and d.dict_key=?");
		List<Record> housetypeList=Db.find(sql.toString(),service_type,house_type);
		return housetypeList;
	}

	public List<Record> getPositionType(String position_type) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT group_concat(d.dict_value) AS position_type FROM dictionary d WHERE d.dict_type='ypPositionType' and d.dict_key in (").append(position_type).append(")");
		List<Record> positionTypeList=Db.find(sql.toString());
		return positionTypeList;
	}
//
//	public List<Record> getApplyjobPost(String apply_job_post) {
//		StringBuffer sql = new StringBuffer();
//		sql.append("SELECT group_concat(d.dict_value) AS position_type FROM dictionary d WHERE d.dict_type='ypApplyJobPost' and LOCATE(d.dict_key, ?) > 0 ");
//		List<Record> positionTypeList=Db.find(sql.toString(),apply_job_post);
//		return positionTypeList;
//	}

	public List<Record> getPxgwItem(String pxgw_service_item) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT group_concat(d.dict_value) as pxgw_item_detail FROM dictionary d WHERE d.dict_type='zsyPxgwItemDetail' and d.dict_key in (").append(pxgw_service_item).append(")");
		List<Record> positionTypeList=Db.find(sql.toString());
		return positionTypeList;
	}

	public List<Record> getFlzxdetail(String flzx_detail_content) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT group_concat(d.dict_value) as flzx_detail_content FROM dictionary d WHERE d.dict_type='zsyFlzxDetailContent' and d.dict_key in (").append(flzx_detail_content).append(")");
		List<Record> positionTypeList=Db.find(sql.toString());
		return positionTypeList;
	}

	public List<Record> getPosition_type(String position_type) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT group_concat(d.dict_value) as position_type FROM dictionary d WHERE y.dict_type='ypPositionType' and d.dict_key in (").append(position_type).append(")");
		List<Record> positionTypeList=Db.find(sql.toString());
		return positionTypeList;
	}

	public List<Record> getApplyJobPost(String apply_job_post) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT group_concat(d.dict_value) as apply_job_post FROM dictionary d WHERE d.dict_type='ypApplyJobPost' and d.dict_key in (").append(apply_job_post).append(")");
		List<Record> ApplyJobPost=Db.find(sql.toString());
		return ApplyJobPost;
	}

	public List<Record> getDecorationRange(String decoration_range) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT group_concat(d.dict_value) AS decoration_range FROM dictionary d WHERE d.dict_type='jzxDecorationRange' and d.dict_key in (").append(decoration_range).append(")");
		List<Record> decorationList=Db.find(sql.toString());
		return decorationList;
	}

	public List<Record> getData() {
	String sql="select name,sex from user_info where id=1 or id=3";
	List<Record> data=Db.find(sql);
	return data;
	}

	@Override
	public List<Record> getwashType(String wash_type) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT group_concat(d.dict_value) AS wash_type FROM dictionary d WHERE d.dict_type='zybWashType' and d.dict_key IN(").append(wash_type).append(")");
		List<Record> decorationList=Db.find(sql.toString());
		return decorationList;
	}

	@Override
	public List<Record> getAppendService(String append_service) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT group_concat(d.dict_value) AS append_service FROM dictionary d WHERE d.dict_type='swgAppendService' and d.dict_key IN(").append(append_service).append(")");
		List<Record> appendService=Db.find(sql.toString());
		return appendService;
	}

	@Override
	public List<Record> getServiceTypeForSearch() {
		return Db.find("SELECT * FROM service_type t WHERE t.is_del=0");
	}

	@Override
	public List<Record> getHssyShootStyle(String hssy_shoot_style) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT group_concat(d.dict_value) AS hssy_shoot_style FROM dictionary d WHERE d.dict_type='sypHssyShootStyle' and d.dict_key IN(").append(hssy_shoot_style).append(")");
		List<Record> hssyShootStyleList=Db.find(sql.toString());
		return hssyShootStyleList;
	}

	@Override
	public List<Record> getGrxzShootStyle(String grxz_shoot_style, String sex) {
		StringBuffer sqlnan = new StringBuffer();
		StringBuffer sqlnv = new StringBuffer();
		sqlnv.append("SELECT group_concat(d.dict_value) AS grxz_shoot_style FROM dictionary d WHERE d.dict_type='sypGrxzWomenShootStyle' and d.dict_key IN(").append(grxz_shoot_style).append(")");
		sqlnan.append("SELECT group_concat(d.dict_value) AS grxz_shoot_style FROM dictionary d WHERE d.dict_type='sypGrxzManShootStyle' and d.dict_key IN(").append(grxz_shoot_style).append(")");
		List<Record> hssyShootStyleList;
		if(sex.equals("1")){
		   hssyShootStyleList=Db.find(sqlnan.toString());
		}else{
			 hssyShootStyleList=Db.find(sqlnv.toString());
		}
		return hssyShootStyleList;
	}

	@Override
	public List<Record> getCommonName(String type, String dictType) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT group_concat(d.dict_value) AS name FROM dictionary d WHERE d.dict_type='").append(dictType).append("' and d.dict_key IN(").append(type).append(")");
		List<Record> nameList=Db.find(sql.toString());
		return nameList;
	}

	@Override
	public List<Record> getServiceName(String serviceId, String appType) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT group_concat(s.service_type_name) AS name FROM service_type s WHERE s.app_type='").append(appType).append("' and s.service_type_id IN(").append(serviceId).append(")");
		List<Record> nameList=Db.find(sql.toString());
		return nameList;
	}

	@Override
	public List<Record> getControlChildType(String control_type) {
		String sql = "SELECT c.control_show_type as type,c.control_show_name as name FROM control_show_type c WHERE c.control_type =? ORDER BY c.id";
		List<Record> areaList=Db.find(sql,control_type);
		return areaList;
	}

	@Override
	public List<Record> getChinaArea(String parentId) {
			String sql = "SELECT t.area as name,t.area FROM area t WHERE t.parent_id IS NULL ORDER BY t.index_str";
			if(parentId!=null){
				sql = "SELECT t.area as name,t.area FROM area t WHERE t.parent_id = (select a.id from area a where a.area='"+parentId+"')";
			}
			List<Record> areaList=Db.find(sql);
			return areaList;
		}

	@Override
	public List<Record> getControlType() {
		String sql = "SELECT c.control_type as type,c.control_name as name FROM control_type c ORDER BY c.id";
		List<Record> areaList=Db.find(sql);
		return areaList;
	}

	@Override
	public List<Record> getAllCatalog() {
		String sql = "SELECT c.name as app_name,c.alias as app_type FROM catalog c where c.level=0 order by c.id";
		List<Record> areaList=Db.find(sql);
		return areaList;
	}
	
	@Override
	public List<Record> getServiceByCatId(String app_type) {
		List<Record> res=null;
		StringBuffer sql = new StringBuffer();
		String	cataIds = getCataIdsByAlias(app_type);
		if(StringUtil.isNullStr(cataIds)){
			return res;
		}
		sql.append("select st.id as service_type,st.service_type_name as service_name from service_type st join catalog_service cs on st.id=cs.service_id and st.is_del=0 where cs.catalog_id in (" ).append(cataIds);
		sql.append(") ");
		res=Db.find(sql.toString());
		return res;
	}
	
	private String getCataIdsByAlias(String app_type) {
		String sql="select id,leaf from catalog where level=0 and alias=?";
		StringBuffer cataIds = new StringBuffer();
		
		List<Record> res=Db.find(sql,app_type);
		
		if(res!=null&&res.size()>0){
			for(Record re:res){
				if(re.getInt("leaf")==1){
					cataIds.append(re.getInt("id"));
					cataIds.append(",");
			}else{
				cataIds.append(re.getInt("id"));
				cataIds.append(",");
				recursiveAliasCataIds(cataIds,re.getInt("id"));
			}
			}
		}
		if(cataIds.length()>0&&cataIds.indexOf(",")!=0){
			return cataIds.substring(0,cataIds.lastIndexOf(","));
		}
		return cataIds.toString();
	}
	private void recursiveAliasCataIds(StringBuffer cataIds, Integer id) {
		String sql="select id,leaf from catalog where parentid=?";
		List<Record> res=Db.find(sql,id);
		if(res!=null&&res.size()>0){
			for(Record re:res){
				if(re.getInt("leaf")==1){
					cataIds.append(re.getInt("id"));
					cataIds.append(",");
				}else{
					recursiveAliasCataIds(cataIds,re.getInt("id"));
				}
			}
		}
		
	}

	@Override
	public List<Record> getGxfwCatalog() {
		String sqlGx="select id from catalog where alias='gxfw'";
		int id=Db.find(sqlGx).get(0).getInt("id");
		
	    String sql="select c.name,c.id from catalog c where c.leaf=1 and c.parentid=?";
	    List<Record> res=Db.find(sql,id);
	    
		return res;
	}

	@Override
	public List<Record> getActivityType() {
		String sql="select id as type,type_name as name from activity_type";
		List<Record> activityType=Db.find(sql);
		return activityType;
	}

	@Override
	public List<Record> getEntranceType() {
		String sql="select id as type,description as name from activity_entrance";
		List<Record> entranceType=Db.find(sql);
		return entranceType;
	}

	@Override
	public List<MerchantsInfo> exportExcel() {
		String sql="SELECT (SELECT me.phone FROM merchant_employees me WHERE me.merchant_id = mi.id AND me.employees_type = 1 ) AS phone,mi.name,mi.detail,mi.location_address,mi.join_time,mi.max_employee_num,mi.app_type,mi.invitation_code, mi.invitation_code, ("+
		"SELECT count(1) FROM user_merchant_collection uc where uc.merchant_id=mi.id group by uc.merchant_id) AS fensi FROM merchant_info mi where mi.join_time>='2016-05-10'";
		List<MerchantsInfo> lis=MerchantsInfo.dao.find(sql);
		return lis;
	}

	@Override
	public List<Record> getServiveCity(Long parentId) {
		List<Record> areaList=null;
	    String city=Db.queryStr("SELECT area FROM area t WHERE t.id =?",parentId);
		if(StringUtil.matchProvince(city)){
			areaList=new ArrayList<Record>();
			Record r=new Record();
			r.set("id", parentId).set("area", city);
			areaList.add(r);
		}else{
		String	sql = "SELECT * FROM area t WHERE t.parent_id ="+parentId;
		areaList=Db.find(sql);
		}
		return areaList;
	}

	@Override
	public List<Record> getNewArea(Long parentId) {
		String sql = "SELECT id,name as area FROM city_area t WHERE t.parent_id=1 ORDER BY t.id";
		if(parentId!=null){
			sql = "SELECT id,name as area FROM city_area t WHERE t.parent_id ="+parentId;
		}
		List<Record> areaList=Db.find(sql);
		return areaList;
	}

	@Override
	public List<Record> getOrderObjectStore(String group) {
		String sql = "SELECT id,remark as value FROM activity_order_reward_rule  WHERE rule_group=? ";
		List<Record> orderRuleList=Db.find(sql,group);
		for(int i=0;i<orderRuleList.size();i++){
			orderRuleList.get(i).set("id", orderRuleList.get(i).getLong("id").toString());
			}
		return orderRuleList;
	}

	@Override
	public List<Record> getAllProAndCity(Map<String, String[]> paramMap) {

		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0]==null ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0]==null ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		
		sql.append("select c.id,p.area as province,c.area as city from area p inner join area c on p.id=c.parent_id and c.parent_id is not null where p.parent_id is null and ");
		totalSql.append("select count(1) as total from area p inner join area c on p.id=c.parent_id and c.parent_id is not null where p.parent_id is null and ");

		Map<String,String[]> strFilter=new HashMap<String,String[]>();
		Map<String,String[]> intFilter=new HashMap<String,String[]>();
		strFilter.put("p.area", paramMap.get("province"));
		strFilter.put("c.area", paramMap.get("city"));
		
		sql.append(Util.getFilter(strFilter, intFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter));
        long total= Db.find(totalSql.toString()).get(0).getLong("total");
        
        String property = "p.id";
		String direction = SORT.ASC;
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
		
		List<Record> re=Db.find(sql.toString());
		if(re.size()>0){
			re.get(0).set("total", total);
		}
		return re;
	
	}

	@Override
	public List<Record> getRewardList(Map<String, String[]> param) {
		String sql = "SELECT id,title FROM activity_info  WHERE detail_table='activity_order_reward' ";
		List<Record> orderRuleList=Db.find(sql);
		for(int i=0;i<orderRuleList.size();i++){
			orderRuleList.get(i).set("id", orderRuleList.get(i).getLong("id").toString());
			}
		return orderRuleList;
	}

	@Override
	public List<Record> getMqSendFailureList(Map<String, String[]> paramMap) {


		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0]==null ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0]==null ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		
		sql.append("select mq.* from mq_send_failure mq where ");
		totalSql.append("select count(1) as total from mq_send_failure mq where  ");

		Map<String,String[]> strFilter=new HashMap<String,String[]>();
		Map<String,String[]> intFilter=new HashMap<String,String[]>();
		intFilter.put("mq.status", paramMap.get("status"));
		intFilter.put("mq.type", paramMap.get("type"));
		
		sql.append(Util.getFilter(strFilter, intFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter));
        long total= Db.find(totalSql.toString()).get(0).getLong("total");
        
        String property = "mq.join_time";
		String direction = SORT.ASC;
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
		
		List<Record> re=Db.find(sql.toString());
		if(re.size()>0){
			re.get(0).set("total", total);
		}
		return re;
	
	
	}

	@Override
	public boolean delOrderUserReAccount(Map<String, String[]> param, String operUserName) {
		String[] ids = param.get("id")[0].split(",");
		Record 	re;
		for(int i = 0; i < ids.length; i++){
			re=Db.findById("mq_send_failure", ids[i]);
			try {
				String msg=re.getStr("msg");
				MqUtil.writeToMQ("orderTemplate", re.getStr("queueName"),msg);
				re.set("status", 1).set("deal_time", new Date()).set("user_name", operUserName);
				Db.update("mq_send_failure", re);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public boolean editMqSendFailureList(Map<String, String[]> param) {
		String id= param.get("id")[0];
		String msg= param.get("msg")[0];
		Record re=Db.findById("mq_send_failure", id);
		if(re!=null){
			re.set("msg", msg);
			Db.update("mq_send_failure", re);
		}
		return true;
	}
}

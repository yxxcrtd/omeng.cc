package com.shanjin.manager.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.shanjin.manager.Bean.GroupRole;
import com.shanjin.manager.Bean.RoleResource;
import com.shanjin.manager.Bean.SystemResource;
import com.shanjin.manager.constant.Constant.PAGE;
import com.shanjin.manager.constant.Constant.SORT;
import com.shanjin.manager.dao.SystemResourceDao;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

public class SystemResourceDaoImpl implements SystemResourceDao{

	public List<SystemResource> systemResourceList(Map<String, String[]> param){
		StringBuffer sql = new StringBuffer();
		List<SystemResource> resourcelist=new ArrayList<SystemResource>();
		sql.append("SELECT * from authority_resource_info t where 1=1 ");
		String resName = "";
		if(StringUtil.isNotNullMap(param,"resName")){
			resName = StringUtil.null2Str(param.get("resName")[0]);
			sql.append(" and t.resName like '%").append(resName).append("%'");
		}
		String linkPath="";
		if(StringUtil.isNotNullMap(param,"linkPath")){
			linkPath = StringUtil.null2Str(param.get("linkPath")[0]);
			sql.append(" and t.linkPath like '%").append(linkPath).append("%'");
		}
		if(StringUtil.isNotNullMap(param,"type")){
			int	type = StringUtil.nullToInteger(param.get("type")[0]);
			sql.append(" and t.type=").append(type);
		}
		if(StringUtil.isNotNullMap(param,"fatherId")){
			Long fatherId = StringUtil.nullToLong(param.get("fatherId")[0]);
			sql.append(" and t.fatherId=").append(fatherId);
		}
		long total=SystemResource.dao.find(sql.toString()).size();
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
		resourcelist=SystemResource.dao.find(sql.toString());
		if(resourcelist.size()>0){
			resourcelist.get(0).setTotal(total);	
		}
		return resourcelist;
	}

	public Boolean deleteResource(String ids) {
		Boolean flag = false;
		String sql = "delete from authority_resource_info where id IN ("+ids+")";
		Db.update(sql);
		sql = "delete from authority_role_resource where resId IN ("+ids+")";
		Db.update(sql);
		flag = true;
		return flag;
	}

	public Boolean saveResource(Map<String, String[]> param) {
		boolean isUpdate = false; // 标识是否是更新
		Long id = 0L;
		if(StringUtil.isNotNullMap(param,"id")){
			isUpdate = true;
			id = StringUtil.nullToLong(param.get("id")[0]);
		}
		String resName = "";
		if(StringUtil.isNotNullMap(param,"resName")){
			resName = StringUtil.null2Str(param.get("resName")[0]);
		}
		String linkPath="";
		if(StringUtil.isNotNullMap(param,"linkPath")){
			linkPath = StringUtil.null2Str(param.get("linkPath")[0]);
		}
		String remark="";
		if(StringUtil.isNotNullMap(param,"remark")){
			remark = StringUtil.null2Str(param.get("remark")[0]);
		}
		int disabled = 0;
		if(StringUtil.isNotNullMap(param,"disabled")){
			disabled = StringUtil.nullToInteger(param.get("disabled")[0]);
		}
		int type = 0;
		if(StringUtil.isNotNullMap(param,"type")){
			type = StringUtil.nullToInteger(param.get("type")[0]);
		}
		int isLeaf = 0;
		if(StringUtil.isNotNullMap(param,"isLeaf")){
			isLeaf = StringUtil.nullToInteger(param.get("isLeaf")[0]);
		}
		int isCommon = 0;
		if(StringUtil.isNotNullMap(param,"isCommon")){
			isCommon = StringUtil.nullToInteger(param.get("isCommon")[0]);
		}
		int rank = 1;
		if(StringUtil.isNotNullMap(param,"rank")){
			rank = StringUtil.nullToInteger(param.get("rank")[0]);
		}
		Long fatherId = null;
		if(StringUtil.isNotNullMap(param,"fatherId")){
			fatherId = StringUtil.nullToLong(param.get("fatherId")[0]);
		}
		boolean flag = false;
		if(isUpdate){
			SystemResource.dao.findById(id).set("resName", resName).set("linkPath", linkPath)
			.set("remark", remark).set("type", type).set("disabled", disabled).set("rank", rank)
			.set("fatherId", fatherId).set("isLeaf", isLeaf).set("isCommon", isCommon).update();
		}else{
			SystemResource res = new SystemResource();
			res.set("resName", resName).set("linkPath", linkPath).set("remark", remark)
					.set("disabled", disabled).set("type", type).set("rank", rank)
					.set("fatherId", fatherId).set("isLeaf", isLeaf).set("isCommon", isCommon).save();
		}
		flag = true;
		return flag;
	}

	/**
	 * 获取系统所有可用资源
	 */
	public List<SystemResource> getAllResource() {
		List<SystemResource> resourcelist=new ArrayList<SystemResource>();
		String sql = "SELECT t.* FROM authority_resource_info t WHERE t.disabled<>1 ORDER BY t.resName ";
		resourcelist = SystemResource.dao.find(sql.toString());
		return resourcelist;
	}

	/**
	 * 获取角色资源
	 */
	public Map<String, String> getResourceByRoleId(Long roleId) {
		StringBuffer sql = new StringBuffer();
		List<SystemResource> resourcelist=new ArrayList<SystemResource>();
		sql.append(" SELECT res.* FROM authority_role_resource rr LEFT JOIN authority_resource_info  res ");
		sql.append(" ON rr.resId=res.id WHERE res.disabled<>1 AND rr.roleId=").append(roleId);
		Map<String,String> map = new HashMap<String,String>();
		resourcelist = SystemResource.dao.find(sql.toString());
		if(resourcelist!=null&&resourcelist.size()>0){
			for(SystemResource sr : resourcelist){
				map.put(StringUtil.null2Str(sr.getLong("id")), "1"); //value=1表示为角色资源
			}
		}
		return map;
	}

	/**
	 * 保存角色资源
	 */
	public Boolean saveRoleResources(Long roleId, String resIds) {
		Boolean flag = false;
		Db.update("delete from authority_role_resource where roleId = ?", roleId);
		if(resIds!=null&&resIds!=""&&resIds.endsWith(",")){
			resIds = resIds.substring(0, resIds.length()-1);
		}
		String[] ids = resIds.split(",");
		if(ids!=null&&ids.length>0){
			for(int i=0;i<ids.length;i++){
				if(ids[i]!=null&&ids[i]!=""&&StringUtil.nullToLong(ids[i])!=0L){
					RoleResource rr=new RoleResource();
					rr.set("roleId", roleId).set("resId", StringUtil.nullToLong(ids[i])).save();
				}
			}
		}
		flag = true;
		return flag;
	}

	public Boolean saveGroupRoles(Long groupId, String resIds) {
		Boolean flag = false;
		Db.update("delete from authority_group_role where groupId = ?", groupId);
		if(resIds!=null&&resIds!=""&&resIds.endsWith(",")){
			resIds = resIds.substring(0, resIds.length()-1);
		}
		String[] ids = resIds.split(",");
		if(ids!=null&&ids.length>0){
			for(int i=0;i<ids.length;i++){
				if(ids[i]!=null&&ids[i]!=""&&StringUtil.nullToLong(ids[i])!=0L){
					GroupRole gr=new GroupRole();
					gr.set("groupId", groupId).set("roleId", StringUtil.nullToLong(ids[i])).save();
				}
			}
		}
		flag = true;
		return flag;
	}
	
}

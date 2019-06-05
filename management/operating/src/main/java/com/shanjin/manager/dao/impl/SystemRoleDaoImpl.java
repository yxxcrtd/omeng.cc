package com.shanjin.manager.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.shanjin.manager.Bean.SystemRole;
import com.shanjin.manager.Bean.UserRole;
import com.shanjin.manager.constant.Constant.PAGE;
import com.shanjin.manager.constant.Constant.SORT;
import com.shanjin.manager.dao.SystemRoleDao;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

public class SystemRoleDaoImpl implements SystemRoleDao{

	public List<SystemRole> systemRoleList(Map<String, String[]> param) {
		StringBuffer sql = new StringBuffer();
		List<SystemRole> roleList=new ArrayList<SystemRole>();
		sql.append("SELECT t.id,t.roleName,t.createTime,t.createName,t.updateTime,t.updateName,t.disabled,t.remark from authority_role_info t where 1=1 ");
		String roleName = "";
		if(StringUtil.isNotNullMap(param,"roleName")){
			roleName = StringUtil.null2Str(param.get("roleName")[0]);
			sql.append(" and t.roleName like '%").append(roleName).append("%'");
		}
		if(StringUtil.isNotNullMap(param,"disabled")){
			int disabled = StringUtil.nullToInteger(param.get("disabled")[0]);
			sql.append(" and t.disabled=").append(disabled);
		}
		long total=SystemRole.dao.find(sql.toString()).size();
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
		roleList=SystemRole.dao.find(sql.toString());
		if(roleList.size()>0){
			roleList.get(0).setTotal(total);	
		}
		return roleList;
	}

	public Boolean deleteRole(String ids) {
		Boolean flag = false;
		String sql = "delete from authority_role_info where id IN ("+ids+")";
		Db.update(sql);
		flag = true;
		return flag;
	}

	/**
	 * 0:失败；1：成功；2：名称已存在
	 */
	public int saveRole(Map<String, String[]> param,String operUserName) {
		boolean isUpdate = false; // 标识是否是更新
		Long id = 0L;
		if(StringUtil.isNotNullMap(param,"id")){
			isUpdate = true;
			id = StringUtil.nullToLong(param.get("id")[0]);
		}
		String roleName = "";
		if(StringUtil.isNotNullMap(param,"roleName")){
			roleName = StringUtil.null2Str(param.get("roleName")[0]);
		}
		String remark="";
		if(StringUtil.isNotNullMap(param,"remark")){
			remark = StringUtil.null2Str(param.get("remark")[0]);
		}
		int disabled = 0;
		if(StringUtil.isNotNullMap(param,"disabled")){
			disabled = StringUtil.nullToInteger(param.get("disabled")[0]);
		}

		int flag = 0;
		if(checkName(roleName,id)){
			flag = 2;
			return flag;
		}
		
		if(isUpdate){
			SystemRole.dao.findById(id).set("roleName", roleName).set("updateName", operUserName).set("updateTime", new Date()).set("remark", remark).set("disabled", disabled).update();
			flag = 1;
		}else{
			SystemRole sr = new SystemRole();
			sr.set("roleName", roleName).set("createName", operUserName).set("createTime", new Date()).set("remark", remark).set("disabled", disabled).save();
			flag = 1;
		}
		return flag;
	}
	
	/**
	 * 检查名称是否存在
	 * @param name
	 * @param id
	 * @return
	 */
	private boolean checkName(String name,Long id){
		boolean flag = false;
		List<SystemRole> roleList=new ArrayList<SystemRole>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * from authority_role_info t where  t.roleName='").append(name).append("'");
		if(id!=null&&id.longValue()!=0){
			// 更新
		    sql.append(" and t.id<>").append(id);	
		}
		roleList=SystemRole.dao.find(sql.toString());
		if(roleList.size()>0){
			flag = true;
		}
		return flag;
	}

	public List<SystemRole> getOtherRolesByGroup(Long groupId) {
		// 查询群组未分配的角色
		List<SystemRole> roleList=new ArrayList<SystemRole>();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT DISTINCT(r.id),r.* FROM authority_role_info r WHERE r.id NOT IN (");
		sql.append(" SELECT DISTINCT(g.roleId) FROM authority_group_role g WHERE g.groupId=").append(groupId).append(")");
		sql.append(" AND r.disabled<>1 ORDER BY r.roleName");
		roleList = SystemRole.dao.find(sql.toString());
		if(roleList.size()>0){
			roleList.get(0).setTotal(StringUtil.nullToLong(roleList.size()));	
		}
		return roleList;
	}

	public List<SystemRole> getSelfRolesByGroup(Long groupId) {
		//  查询群组已经分配的角色
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT r.* FROM authority_group_role g LEFT JOIN authority_role_info r ON g.roleId=r.id");
		sql.append(" WHERE g.groupId="+groupId);
		sql.append(" AND r.disabled<>1");
		List<SystemRole> roleList = SystemRole.dao.find(sql.toString());
		if(roleList.size()>0){
			roleList.get(0).setTotal(StringUtil.nullToLong(roleList.size()));	
		}
		return roleList;
	}

	public List<SystemRole> getOtherRolesByUser(Long userId) {
		// 查询用户未分配的角色
		List<SystemRole> roleList=new ArrayList<SystemRole>();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT DISTINCT(r.id),r.* FROM authority_role_info r WHERE r.id NOT IN (");
		sql.append(" SELECT DISTINCT(g.roleId) FROM authority_user_role g WHERE g.userId=").append(userId).append(")");
		sql.append(" AND r.disabled<>1 ORDER BY r.roleName");
		roleList = SystemRole.dao.find(sql.toString());
		if(roleList.size()>0){
			roleList.get(0).setTotal(StringUtil.nullToLong(roleList.size()));	
		}
		return roleList;
	}

	public List<SystemRole> getSelfRolesByUser(Long userId) {
	    //  查询用户已经分配的角色
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT r.* FROM authority_user_role g LEFT JOIN authority_role_info r ON g.roleId=r.id");
			sql.append(" WHERE g.userId="+userId);
			sql.append(" AND r.disabled<>1");
			List<SystemRole> roleList = SystemRole.dao.find(sql.toString());
			if(roleList.size()>0){
				roleList.get(0).setTotal(StringUtil.nullToLong(roleList.size()));	
			}
			return roleList;
	}

	public Boolean saveUserRoles(Long userId, String roleIds) {
		Boolean flag = false;
		Db.update("delete from authority_user_role where userId = ?", userId);
		if(roleIds!=null&&roleIds!=""&&roleIds.endsWith(",")){
			roleIds = roleIds.substring(0, roleIds.length()-1);
		}
		String[] ids = roleIds.split(",");
		if(ids!=null&&ids.length>0){
			for(int i=0;i<ids.length;i++){
				if(ids[i]!=null&&ids[i]!=""&&StringUtil.nullToLong(ids[i])!=0L){
					UserRole ur=new UserRole();
					ur.set("userId", userId).set("roleId", StringUtil.nullToLong(ids[i])).save();
				}
			}
		}
		flag = true;
		return flag;
	}
	

}

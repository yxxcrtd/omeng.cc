package com.shanjin.manager.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.shanjin.manager.Bean.SystemGroup;
import com.shanjin.manager.Bean.UserGroup;
import com.shanjin.manager.constant.Constant.PAGE;
import com.shanjin.manager.constant.Constant.SORT;
import com.shanjin.manager.dao.SystemGroupDao;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

public class SystemGroupDaoImpl implements SystemGroupDao{

	public List<SystemGroup> systemGroupList(Map<String, String[]> param) {
		StringBuffer sql = new StringBuffer();
		List<SystemGroup> groupList=new ArrayList<SystemGroup>();
		sql.append("SELECT t.id,t.groupName,t.createTime,t.createName,t.updateTime,t.updateName,t.disabled, t.remark from authority_group_info t where 1=1 ");
		String groupName = "";
		if(StringUtil.isNotNullMap(param,"groupName")){
			groupName = StringUtil.null2Str(param.get("groupName")[0]);
			sql.append(" and t.groupName like '%").append(groupName).append("%'");
		}
		if(StringUtil.isNotNullMap(param,"disabled")){
			int disabled = StringUtil.nullToInteger(param.get("disabled")[0]);
			sql.append(" and t.disabled=").append(disabled);
		}
		long total=SystemGroup.dao.find(sql.toString()).size();
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
		groupList=SystemGroup.dao.find(sql.toString());
		if(groupList.size()>0){
			groupList.get(0).setTotal(total);	
		}
		return groupList;
	}

	public Boolean deleteGroup(String ids) {
		Boolean flag = false;
		String sql = "delete from authority_group_info where id IN ("+ids+")";
		Db.update(sql);
		flag = true;
		return flag;
	}

	/**
	 * 0:失败；1：成功；2：名称已存在
	 */
	public int saveGroup(Map<String, String[]> param,String operUserName) {
		boolean isUpdate = false; // 标识是否是更新
		Long id = 0L;
		if(StringUtil.isNotNullMap(param,"id")){
			isUpdate = true;
			id = StringUtil.nullToLong(param.get("id")[0]);
		}
		String groupName = "";
		if(StringUtil.isNotNullMap(param,"groupName")){
			groupName = StringUtil.null2Str(param.get("groupName")[0]);
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
		if(checkName(groupName,id)){
			flag = 2;
			return flag;
		}
		
		if(isUpdate){
			SystemGroup.dao.findById(id).set("groupName", groupName).set("updateName", operUserName).set("updateTime", new Date()).set("remark", remark).set("disabled", disabled).update();
			flag=1;
		}else{
			SystemGroup sr = new SystemGroup();
			sr.set("groupName", groupName).set("createName", operUserName).set("createTime", new Date()).set("remark", remark).set("disabled", disabled).save();
			flag=1;
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
		List<SystemGroup> groupList=new ArrayList<SystemGroup>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * from authority_group_info t where  t.groupName='").append(name).append("'");
		if(id!=null&&id.longValue()!=0){
			// 更新
		    sql.append(" and t.id<>").append(id);	
		}
		groupList=SystemGroup.dao.find(sql.toString());
		if(groupList.size()>0){
			flag = true;
		}
		return flag;
	}

	public List<SystemGroup> getOtherGroupsByUser(Long userId) {
		// 查询用户未分配的群组
		List<SystemGroup> groupList=new ArrayList<SystemGroup>();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT DISTINCT(g.id),g.* FROM authority_group_info g WHERE g.id NOT IN (");
		sql.append(" SELECT DISTINCT(u.groupId) FROM authority_user_group u WHERE u.userId=").append(userId).append(")");
		sql.append(" AND g.disabled<>1 ORDER BY g.groupName");
		groupList = SystemGroup.dao.find(sql.toString());
		if(groupList.size()>0){
			groupList.get(0).setTotal(StringUtil.nullToLong(groupList.size()));	
		}
		return groupList;
	}

	public List<SystemGroup> getSelfGroupsByUser(Long userId) {
		// 查询用户已分配的群组
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT g.* FROM authority_user_group u LEFT JOIN authority_group_info g ON u.groupId=g.id");
		sql.append(" WHERE u.userId="+userId);
		sql.append(" AND g.disabled<>1");
		List<SystemGroup> groupList = SystemGroup.dao.find(sql.toString());
		if(groupList.size()>0){
			groupList.get(0).setTotal(StringUtil.nullToLong(groupList.size()));	
		}
		return groupList;
	}

	public Boolean saveUserGroups(Long userId, String groupIds) {
		Boolean flag = false;
		Db.update("delete from authority_user_group where userId = ?", userId);
		if(groupIds!=null&&groupIds!=""&&groupIds.endsWith(",")){
			groupIds = groupIds.substring(0, groupIds.length()-1);
		}
		String[] ids = groupIds.split(",");
		if(ids!=null&&ids.length>0){
			for(int i=0;i<ids.length;i++){
				if(ids[i]!=null&&ids[i]!=""&&StringUtil.nullToLong(ids[i])!=0L){
					UserGroup ug=new UserGroup();
					ug.set("userId", userId).set("groupId", StringUtil.nullToLong(ids[i])).save();
				}
			}
		}
		flag = true;
		return flag;
	}
	

	

}

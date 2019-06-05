package com.shanjin.manager.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.shanjin.common.util.MD5Util;
import com.shanjin.manager.Bean.Area;
import com.shanjin.manager.Bean.SystemUserInfo;
import com.shanjin.manager.Bean.UserApp;
import com.shanjin.manager.constant.Constant;
import com.shanjin.manager.constant.Constant.PAGE;
import com.shanjin.manager.constant.Constant.SORT;
import com.shanjin.manager.dao.SystemUserInfoDao;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

public class SystemUserInfoDaoImpl  implements SystemUserInfoDao {

	public List<SystemUserInfo> systemUserList(Map<String, String> param) {
		StringBuffer sql = new StringBuffer();
		List<SystemUserInfo> userlist=new ArrayList<SystemUserInfo>();
		//sql.append("SELECT t.id,t.userName,t.realName,t.psw,t.pswHints,t.email,t.phone,t.createTime,t.createName,t.updateTime,t.updateName,t.disabled,t.remark,t.isAdmin,t.userType,t.province,t.city,t.accountName from authority_user_info t where t.isDel<>1 ");
		sql.append("SELECT * from authority_user_info t where t.isDel<>1 ");
		String userName = param.get("userName");
		if(!StringUtil.isNullStr(userName)){
			sql.append(" and t.userName like '%").append(userName).append("%'");
		}
		int userType = StringUtil.nullToInteger(param.get("userType")); // 1.公司员工；2.省代理；3.市代理；4.项目代理
		int uType = StringUtil.nullToInteger(param.get("uType"));//查询参数
		if(userType==4){
			Long userId = StringUtil.nullToLong(param.get("userId"));
			sql.append(" and t.userId=").append(userId);
		}else{
			if(uType!=0){
				sql.append(" and t.userType=").append(uType);
			}
		}

		String phone = param.get("phone");
		if(!StringUtil.isNullStr(phone)){
			sql.append(" and t.phone like '%").append(phone).append("%'");
		}
		if(!StringUtil.isNullStr(param.get("disabled"))){
			int disabled = StringUtil.nullToInteger(param.get("disabled"));
			sql.append(" and t.disabled=").append(disabled);
		}
		String province = param.get("province");
		if(!StringUtil.isNullStr(province)){
			sql.append(" and t.province=").append(province);
		}
		String city = param.get("city");
		if(!StringUtil.isNullStr(city)){
			sql.append(" and t.city=").append(city);
		}
		long total=SystemUserInfo.dao.find(sql.toString()).size();
		String property = "updateTime";
		String direction = SORT.DESC;
		if(!StringUtil.isNullStr(param.get("sort"))){
			Map<String,String> sortMap = Util.sortMap(StringUtil.null2Str(param.get("sort")));
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
		if(!StringUtil.isNullStr(param.get("start"))){
			start = StringUtil.nullToInteger(param.get("start"));
		}
		int pageSize = PAGE.PAGESIZE;
		if(!StringUtil.isNullStr(param.get("limit"))){
			pageSize = StringUtil.nullToInteger(param.get("limit"));
		}
		sql.append(" limit ");
		sql.append(start);
		sql.append(",");
		sql.append(pageSize);
		userlist=SystemUserInfo.dao.find(sql.toString());
		
		///设置省市名称
		if(userlist.size()>0){
			userlist.get(0).setTotal(total);	
			Map<String,String> map = areaMap();
			if(userlist!=null&&userlist.size()>0){
				for(SystemUserInfo u : userlist){
					if(u.getLong("province")!=null){
						String provinceDesc = map.get(StringUtil.null2Str(u.getLong("province")));
						u.set("provinceDesc", provinceDesc);
					}
					if(u.getLong("city")!=null){
						String cityDesc = map.get(StringUtil.null2Str(u.getLong("city")));
						u.set("cityDesc", cityDesc);
					}
				}
			}
		}
		return userlist;
	}
	
	private Map<String,String> areaMap(){
		Map<String,String> map = new HashMap<String,String>();
		List<Area> areaList=new ArrayList<Area>();
		areaList=Area.dao.find(" SELECT * FROM area ");
		if(areaList!=null&&areaList.size()>0){
			for(Area area : areaList){
				map.put(StringUtil.null2Str(area.getLong("id")), StringUtil.null2Str(area.getStr("area")));
			}
		}
		return map;
	}
	
	/**
	 * 删除后台系统用户（管理员用户不允许删除）
	 */
	public Boolean deleteUser(String ids) {
		Boolean flag = false;
		String sql = "UPDATE authority_user_info t SET t.isDel=1 WHERE isAdmin<>1 AND id IN ("+ids+")";
		Db.update(sql);
		flag = true;
		return flag;
	}

	/**
	 * 0:失败；1：成功；2：名称已存在
	 */
	public int saveUser(Map<String, String[]> param,String operUserName) {
		boolean isUpdate = false; // 标识是否是更新
		Long id = 0L;
		if(StringUtil.isNotNullMap(param,"id")){
			isUpdate = true;
			id = StringUtil.nullToLong(param.get("id")[0]);
		}
		String userName = "";
		if(StringUtil.isNotNullMap(param,"userName")){
			userName = StringUtil.null2Str(param.get("userName")[0]);
		}
		int flag = 0;
		if(checkName(userName,id)){
			flag = 2;
			return flag;
		}
		
		String realName = "";
		if(StringUtil.isNotNullMap(param,"realName")){
			realName = StringUtil.null2Str(param.get("realName")[0]);
		}
		String psw = "";
		String password = "";
		if(StringUtil.isNotNullMap(param,"psw")){
			psw = StringUtil.null2Str(param.get("psw")[0]);
			password=MD5Util.MD5_32(param.get("psw")[0]);
		}
		
		
		String pswHints = "";
		if(StringUtil.isNotNullMap(param,"pswHints")){
			pswHints = StringUtil.null2Str(param.get("pswHints")[0]);
		}
		String email = "";
		if(StringUtil.isNotNullMap(param,"email")){
			email = StringUtil.null2Str(param.get("email")[0]);
		}
		String phone = "";
		if(StringUtil.isNotNullMap(param,"phone")){
			phone = StringUtil.null2Str(param.get("phone")[0]);
		}
		String remark="";
		if(StringUtil.isNotNullMap(param,"remark")){
			remark = StringUtil.null2Str(param.get("remark")[0]);
		}
		int userType = 1;//1.公司员工；2.省代理；3.市代理；4.项目代理
		Long province = null;
		String provinceDesc = "";
		Long city= null;
		String cityDesc = "";
		String appIds="";
		if(StringUtil.isNotNullMap(param,"userType")){
			userType = StringUtil.nullToInteger(param.get("userType")[0]);
		}
		if(userType==2){
			// 省代理
			Map<String,String> map = areaMap();
			if(StringUtil.isNotNullMap(param,"province")){
				province = StringUtil.nullToLong(param.get("province")[0]);
				provinceDesc = map.get(StringUtil.null2Str(province));
			}else{
				return flag; //省代理未选择所代理的省，error
			}
		}else if(userType==3){
			// 市代理
			Map<String,String> map = areaMap();
			if(StringUtil.isNotNullMap(param,"province")){
				province = StringUtil.nullToLong(param.get("province")[0]);
				provinceDesc = map.get(StringUtil.null2Str(province));
			}else{
				return flag; //市代理未选择所代理的省，error
			}
			if(StringUtil.isNotNullMap(param,"city")){
				city = StringUtil.nullToLong(param.get("city")[0]);
				cityDesc = map.get(StringUtil.null2Str(city));
			}else{
				return flag; //市代理未选择所代理的市，error
			}
		}else if(userType==4){
			// 项目代理
			Map<String,String> map = areaMap();
			if(StringUtil.isNotNullMap(param,"province")){
				province = StringUtil.nullToLong(param.get("province")[0]);
				provinceDesc = map.get(StringUtil.null2Str(province));
			}else{
				return flag; //项目代理未选择所代理的省，error
			}
			if(StringUtil.isNotNullMap(param,"city")){
				city = StringUtil.nullToLong(param.get("city")[0]);
				cityDesc = map.get(StringUtil.null2Str(city));
			}else{
				return flag; //项目代理未选择所代理的市，error
			}
			if(StringUtil.isNotNullMap(param,"appIds")){
				appIds = StringUtil.null2Str(param.get("appIds")[0]);
			}else{
				return flag; //项目代理未选择所代理的app项目，error
			}
		}
		String address="";
		if(StringUtil.isNotNullMap(param,"address")){
			address = StringUtil.null2Str(param.get("address")[0]);
		}
		String accountName="";
		if(StringUtil.isNotNullMap(param,"accountName")){
			accountName = StringUtil.null2Str(param.get("accountName")[0]);
		}
		String accountBank = "";
		if(StringUtil.isNotNullMap(param,"accountBank")){
			accountBank = StringUtil.null2Str(param.get("accountBank")[0]);
		}
		String accountNumber="";
		if(StringUtil.isNotNullMap(param,"accountNumber")){
			accountNumber = StringUtil.null2Str(param.get("accountNumber")[0]);
		}

		int disabled = 0;
		if(StringUtil.isNotNullMap(param,"disabled")){
			disabled = StringUtil.nullToInteger(param.get("disabled")[0]);
		}
		
		int isAccount = 0;
		if(StringUtil.isNotNullMap(param,"isAccount")){
			isAccount = StringUtil.nullToInteger(param.get("isAccount")[0]);
		}
		
		if(isUpdate){
			SystemUserInfo.dao.findById(id).set("userName", userName).set("updateName", operUserName).set("updateTime", new Date()).set("remark", remark).set("disabled", disabled).set("isAccount", isAccount).
			set("realName", realName).set("psw", psw).set("password", password).set("pswHints", pswHints).set("email", email).set("phone", phone).set("province", province).set("provinceDesc", provinceDesc).set("cityDesc", cityDesc).
			set("city", city).set("accountName", accountName).set("accountBank", accountBank).set("accountNumber", accountNumber).set("userType", userType).set("address", address).update();
		}else{
			SystemUserInfo sr = new SystemUserInfo();
			sr.set("userName", userName).set("createName", operUserName).set("createTime", new Date()).set("updateTime", new Date()).set("remark", remark).set("disabled", disabled).set("isAccount", isAccount).
			set("realName", realName).set("psw", psw).set("password", password).set("pswHints", pswHints).set("email", email).set("phone", phone).set("province", province).set("provinceDesc", provinceDesc).set("cityDesc", cityDesc).
			set("city", city).set("accountName", accountName).set("accountBank", accountBank).set("accountNumber", accountNumber).set("userType", userType).set("address", address).save();
			id = StringUtil.nullToLong(sr.getLong("id"));
		}
		
		//************保存项目代理所代理的app项目信息**************
		if(userType==4){
			Db.update("delete from authority_user_app where userId = ?", id);
			if(appIds!=null&&appIds!=""&&appIds.endsWith(",")){
				appIds = appIds.substring(0, appIds.length()-1);
			}
			String[] ids = appIds.split(",");
			if(ids!=null&&ids.length>0){
				for(int i=0;i<ids.length;i++){
					if(ids[i]!=null&&ids[i]!=""&&StringUtil.nullToLong(ids[i])!=0L){
						UserApp ua=new UserApp();
						ua.set("userId", id).set("appId", StringUtil.nullToLong(ids[i])).save();
					}
				}
			}
		}
		
		flag = 1;
		
		return flag;
	}
	
	/**
	 * 检查用户账号是否存在
	 * @param name
	 * @param id
	 * @return
	 */
	private boolean checkName(String name,Long id){
		boolean flag = false;
		List<SystemUserInfo> roleList=new ArrayList<SystemUserInfo>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * from authority_user_info t where t.isDel=0 and t.userName='").append(name).append("'");
		if(id!=null&&id.longValue()!=0){
			// 更新
		    sql.append(" and t.id<>").append(id);	
		}
		roleList=SystemUserInfo.dao.find(sql.toString());
		if(roleList.size()>0){
			flag = true;
		}
		return flag;
	}

	public SystemUserInfo getUserByName(String userName) {
		List<SystemUserInfo> userList = new ArrayList<SystemUserInfo>();
		String sql = "SELECT * FROM authority_user_info t WHERE t.isDel=0 and t.userName='"+userName+"'";
		userList = SystemUserInfo.dao.find(sql.toString());
		if(userList!=null&&userList.size()>0){
			return userList.get(0);
		}
		return null;
	}

	public Boolean editSystemUserInfo(Map<String, String[]> param,String operUserName) {
		Long id = 0L;
		boolean flag = false;
		if(StringUtil.isNotNullMap(param,"user_id")){
			id = StringUtil.nullToLong(param.get("user_id")[0]);
		}
		String userName = "";
		if(StringUtil.isNotNullMap(param,"user_name")){
			userName = StringUtil.null2Str(param.get("user_name")[0]);
		}
		String realName = "";
		if(StringUtil.isNotNullMap(param,"user_realName")){
			realName = StringUtil.null2Str(param.get("user_realName")[0]);
		}
		String email = "";
		if(StringUtil.isNotNullMap(param,"user_email")){
			email = StringUtil.null2Str(param.get("user_email")[0]);
		}
		String phone = "";
		if(StringUtil.isNotNullMap(param,"user_phone")){
			phone = StringUtil.null2Str(param.get("user_phone")[0]);
		}
		String remark="";
		if(StringUtil.isNotNullMap(param,"user_remark")){
			remark = StringUtil.null2Str(param.get("user_remark")[0]);
		}
		SystemUserInfo.dao.findById(id).set("userName", userName).set("updateName", operUserName).set("updateTime", new Date()).
		set("remark", remark).set("realName", realName).set("email", email).set("phone", phone).update();
		flag = true;
		return flag;
	}

	public int modifyUserPwd(Long userId,String oldPwd,String newPwd, String pwdHints,String operUserName) {
		int flag = 0;
		SystemUserInfo user = SystemUserInfo.dao.findById(userId);
		if(user!=null){
			String pwd = user.getStr("password");
			oldPwd=MD5Util.MD5_32(oldPwd);
			if(!pwd.equals(oldPwd)){
				flag = 2;
				return flag;
			}
			SystemUserInfo.dao.findById(userId).set("psw", newPwd).set("password", MD5Util.MD5_32(newPwd)).set("updateName", operUserName).set("updateTime", new Date()).
			set("pswHints", pwdHints).update();
			flag = 1;
		}
		return flag;
	}

	@Override
	public List<SystemUserInfo> systemDelUserList(Map<String, String[]> param) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * from authority_user_info t where t.isDel=1 ");
		long total=SystemUserInfo.dao.find(sql.toString()).size();
		String property = "updateTime";
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
		List<SystemUserInfo> userlist=new ArrayList<SystemUserInfo>();
		userlist=SystemUserInfo.dao.find(sql.toString());
		
		///设置省市名称
		if(userlist.size()>0){
			userlist.get(0).setTotal(total);	
//			Map<String,String> map = areaMap();
//			if(userlist!=null&&userlist.size()>0){
//				for(SystemUserInfo u : userlist){
//					if(u.getLong("province")!=null){
//						String provinceDesc = map.get(StringUtil.null2Str(u.getLong("province")));
//						u.set("provinceDesc", provinceDesc);
//					}
//					if(u.getLong("city")!=null){
//						String cityDesc = map.get(StringUtil.null2Str(u.getLong("city")));
//						u.set("cityDesc", cityDesc);
//					}
//				}
//			}
		}
		return userlist;

	}

	@Override
	public Boolean deleteAbUser(String ids) {
		Boolean flag = false;
		StringBuffer sql = new StringBuffer();
		sql.append(" delete from authority_user_info ");
		sql.append(" WHERE userName<>'").append(Constant.ADMIN).append("'");
		sql.append(" AND id IN (").append(ids).append(")");
		Db.update(sql.toString());
		flag = true;
		return flag;
	}

	@Override
	public Boolean recoveryUser(String ids) {
		Boolean flag = false;
		String sql = "UPDATE authority_user_info SET isDel=0 WHERE id IN ("+ids+")";
		Db.update(sql);
		flag = true;
		return flag;
	}

	@Override
	public int setAccount(String id) {
		int flag = 0 ; // 0:失败 1：成功  2：已存在收益账户
		SystemUserInfo user = SystemUserInfo.dao.findById(id);
		if(user!=null){
			int userType = user.getInt("userType"); //2.省代理；3.市代理；4.项目代理
			Long province = user.getLong("province");
			Long city = user.getLong("city");
			String sql = "";
			long count = 0 ;
			if(userType==2){
				sql = "SELECT COUNT(*) FROM authority_user_info t WHERE t.isAccount=1 AND t.userType=? AND t.id!=? AND t.province=?";
				count = Db.queryLong(sql, userType,id,province);
			}else if(userType==3){
				sql = "SELECT COUNT(*) FROM authority_user_info t WHERE t.isAccount=1 AND t.userType=? AND t.id!=? AND t.province=? AND t.city=?";
				count = Db.queryLong(sql, userType,id,province,city);
			}else if(userType==4){
				// 默认同省市下同一项目只有一个代理商 ，不做校验
				//sql = "SELECT COUNT(*) FROM authority_user_info t WHERE t.isAccount=1 AND t.userType=? AND t.id!=? AND t.province=? AND t.city=?";
				//count = Db.queryLong(sql, userType,id,province,city);
			}
			if(count>0){
				// 已存在收益账号
				flag=2;
				return flag;
			}else{
				user.set("isAccount", 1).update();
				flag =1;
			}
		}
		return flag;
	}



}

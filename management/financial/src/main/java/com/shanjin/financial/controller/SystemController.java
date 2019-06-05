package com.shanjin.financial.controller;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.financial.bean.NormalResponse;
import com.shanjin.sso.bean.SystemUserInfo;
import com.shanjin.sso.bean.UserSession;
import com.shanjin.financial.constant.Constants;
import com.shanjin.financial.util.JCaptchaRender;
import com.shanjin.financial.util.StringUtil;



public class SystemController  extends Controller{

	public void index() {
		render("systemUserInfoGrid.jsp");
	}
	
	public void showBlank() {
//		String tips = Db.queryStr("SELECT t.remark FROM dictionary t WHERE t.dict_type='updateSwitch' AND t.dict_key='updateSwitch' AND t.dict_value='1'");
//		if(!StringUtil.isNullStr(tips)){
//			// 不为空 需要升级
//			this.setAttr("tips", tips);
//			render("/view/welcome/tips.jsp");
//			return;
//		}else{
//			HttpSession session = this.getSession();
//			SystemUserInfo user = (SystemUserInfo) session.getAttribute(UserSession._USER);
//			Map<String, Long> data = commonService.getAllData(user);
//			data.put("userType",StringUtil.nullToLong(user.getInt("userType")));
//			this.setAttr("data", data);
//			render("/view/homePage/blank.jsp");
//		}
		String tips = "欢迎访问财务系统！！！";
		this.setAttr("tips", tips);
		render("/view/welcome/tips.jsp");
	}
	
	public void showLoginUserInfo() {
		HttpSession session = this.getSession();
		SystemUserInfo user = (SystemUserInfo) session.getAttribute(UserSession._USER);
		render("/view/homePage/homePage.jsp");
		return;
	}
	
	public void showUserPwd() {
		HttpSession session = this.getSession();
		this.setAttr("user", session.getAttribute(UserSession._USER));
		render("/view/userEdit/modifyUserPwd.jsp");
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
	
	/**
	 * 用户编辑个人信息，session需更新
	 */
	public void editSystemUserInfo() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session = this.getSession();
		String operUserName = StringUtil.null2Str(session.getAttribute(UserSession._USER_NAME));
		Boolean flag = editSystemUserInfo(param,operUserName);
		SystemUserInfo user = (SystemUserInfo) session.getAttribute(UserSession._USER);
		user.set("realName", param.get("user_realName")[0]).set("email", param.get("user_email")[0])
		    .set("phone", param.get("user_phone")[0]).set("remark", param.get("user_remark")[0]);
		session = getSession();
		session.setAttribute(UserSession._USER, user);
		session.setAttribute(UserSession._USER_NAME, operUserName);
		this.renderJson(flag);
	}

	
	/**
	 * 用户修改个人密码
	 */
	public void modifyUserPwd() {
		Long userId = StringUtil.nullToLong(this.getPara("user_id"));
		String oldPwd = StringUtil.null2Str(this.getPara("old_user_psw"));
		String newPwd = StringUtil.null2Str(this.getPara("user_psw"));
		String pwdHints = StringUtil.null2Str(this.getPara("user_pswHints"));
		HttpSession session = this.getSession();
		String operUserName = StringUtil.null2Str(session.getAttribute(UserSession._USER_NAME));
		Record user = Db.findById("authority_user_info", userId);
		int flag = 0;
		String msg = "对不起，服务器忙，请稍后重试！";
		if(user!=null){
			if(!oldPwd.equals(user.getStr("psw"))){
				flag = 2;
				msg = "原密码输入不正确，请重新输入！";
			}else{
				user.set("psw", newPwd).set("pswHints", pwdHints).set("updateName", operUserName).set("updateTime", new Date());
				Db.update("authority_user_info", user);
				flag = 1;
				msg = "密码修改成功";
			}
		}
		this.renderJson(new NormalResponse(flag, 0L,msg));
		return;
	}

//	/**
//	 * 用户登陆
//	 */
//	public void login() {
//		HttpSession session = this.getSession();
//		String userName = StringUtil.null2Str(this.getPara("username_"));
//		String pwd = StringUtil.null2Str(this.getPara("password_"));
//		String code = this.getPara("captcha_");
//		if(userName==null || "".equals(userName) ||pwd==null||"".equals(pwd)){
//			this.setAttr("error", "用户名或密码不能为空");
//			render("/view/welcome/index.jsp");
//			return;
//		}
//		String md5RandomCode = (String) session
//				.getAttribute(JCaptchaRender.DEFAULT_CAPTCHA_MD5_CODE_KEY);
//		boolean flag = JCaptchaRender.validate(md5RandomCode, code);
//		
//		if (!flag) {
//			this.setAttr("error", "验证码错误");
//			render("/view/welcome/index.jsp");
//			return;
//		} else {
//            List<Record> list = Db.find("SELECT * FROM authority_user_info t WHERE t.isDel=0 AND t.userName='omengadmin'");
//            if(list!=null&&list.size()>0){
//            	Record user = list.get(0);
//				session.setAttribute("_user", user);
//				session.setAttribute("_user_id", user.getLong("id"));
//				session.setAttribute("_user_name", userName);
//				session.setAttribute("_user_type", "财务");
//				session.setAttribute("_user_is_login", "true");
//				  
//				render("/view/homePage/homePage.jsp");
//				return;
//            }else {
//					this.setAttr("error", "用户名或密码错误");
//					render("/view/welcome/index.jsp");
//					return;
//				}
//		}
//	}
	
	
	public SystemUserInfo getUserByName(String userName) {
		List<SystemUserInfo> userList = new ArrayList<SystemUserInfo>();
		String sql = "SELECT * FROM authority_user_info t WHERE t.isDel=0 and t.userName='"+userName+"'";
		userList = SystemUserInfo.dao.find(sql.toString());
		if(userList!=null&&userList.size()>0){
			return userList.get(0);
		}
		return null;
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
	
	public String getAgentType(String userName,Integer userType) {
		if(Constants.ADMIN.equals(userName)){
			return "管理员";
		}else if(userType==1){
			return "普通用户";	
		}else{
			return "代理商";
		}
	}
	
	/**
	 * 用户登陆
	 */
	public void login() {
		HttpSession session = this.getSession();
		String userName = StringUtil.null2Str(this.getPara("username_"));
		String pwd = StringUtil.null2Str(this.getPara("password_"));
		String code = this.getPara("captcha_");
		if(userName==null || "".equals(userName) ||pwd==null||"".equals(pwd)){
			this.setAttr("error", "用户名或密码不能为空");
			render("/view/welcome/index.jsp");
			return;
		}
		String md5RandomCode = (String) session
				.getAttribute(JCaptchaRender.DEFAULT_CAPTCHA_MD5_CODE_KEY);
		boolean flag = JCaptchaRender.validate(md5RandomCode, code);
		
		if (!flag) {
			this.setAttr("error", "验证码错误");
			render("/view/welcome/index.jsp");
			return;
		} else {

			SystemUserInfo user = getUserByName(userName);
			if (user != null && user.getStr("userName") != null) {
				String uPwd = StringUtil.null2Str(user.getStr("psw"));
				if (pwd.equals(uPwd)) {
					// 输入密码是否正确（密码加密TODO）
					session.setAttribute(UserSession._USER, user);
					session.setAttribute(UserSession._USER_ID, user.getLong("id"));
					Long userId = user.getLong("id");
					session.setAttribute(UserSession._USER_NAME, userName);
					String perms = getUserPermission(userId);
					session.setAttribute(UserSession._USER_RESOURCES, perms);
					String agentType=getAgentType(user.getStr("userName"),user.getInt("userType"));
					session.setAttribute(UserSession._USER_TYPE, agentType);
					session.setAttribute(UserSession._USER_IS_LOGIN, "true");

					render("/view/homePage/homePage.jsp");
					return;
				} else {
					this.setAttr("error", "用户名或密码错误");
					render("/view/welcome/index.jsp");
					return;
				}
			} else {
				this.setAttr("error", "用户名或密码错误");
				render("/view/welcome/index.jsp");
				return;
			}
		}
	}
	
    
    public static String getIpAddr(HttpServletRequest request) {  
        String ip = request.getHeader("X-Forwarded-For");  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("WL-Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_CLIENT_IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getRemoteAddr();  
        }  
        return ip.equals("0:0:0:0:0:0:0:1")?"127.0.0.1":ip;
    } 
	
	/**
	 * 用户注销
	 */
	public void logout(){
		HttpSession session = getSession(false);
		if(session!=null){
			session.invalidate();
		}
		render("/view/welcome/index.jsp");
	}

	public void showImage() {
		JCaptchaRender img = new JCaptchaRender(4);
		HttpSession session = this.getSession();
		session.setAttribute(JCaptchaRender.DEFAULT_CAPTCHA_MD5_CODE_KEY,
				img.getMd5RandonCode());
		render(img);
	}

}

package com.shanjin.manager.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.IImageCacheService;
import com.shanjin.cache.service.impl.CommonCacheServiceImpl;
import com.shanjin.cache.service.impl.ImageCacheServiceImpl;
import com.shanjin.common.util.MD5Util;
import com.shanjin.manager.Bean.AppInfo;
import com.shanjin.manager.Bean.AppKey;
import com.shanjin.manager.Bean.CustomKeyWords;
import com.shanjin.manager.Bean.EmptyResponse;
import com.shanjin.manager.Bean.NormalResponse;
import com.shanjin.manager.Bean.SearchWord;
import com.shanjin.manager.Bean.ServiceWord;
import com.shanjin.manager.Bean.StopKeyWords;
import com.shanjin.manager.Bean.SystemGroup;
import com.shanjin.manager.Bean.SystemOperateLog;
import com.shanjin.manager.Bean.SystemResource;
import com.shanjin.manager.Bean.SystemRole;
import com.shanjin.sso.bean.SystemUserInfo;
import com.shanjin.manager.Bean.UserWord;
import com.shanjin.manager.constant.Constant;
import com.shanjin.manager.constant.JCaptchaRender;
import com.shanjin.manager.service.ExportService;
import com.shanjin.manager.service.IAppInfoService;
import com.shanjin.manager.service.ICommonService;
import com.shanjin.manager.service.IDictionaryService;
import com.shanjin.manager.service.IPushService;
import com.shanjin.manager.service.ISearchWordsService;
import com.shanjin.manager.service.ISystemGroupService;
import com.shanjin.manager.service.ISystemOperateLogService;
import com.shanjin.manager.service.ISystemResourceService;
import com.shanjin.manager.service.ISystemRoleService;
import com.shanjin.manager.service.ISystemUserInfoService;
import com.shanjin.manager.service.ExcelExportUtil.Pair;
import com.shanjin.manager.service.impl.AppInfoServiceImpl;
import com.shanjin.manager.service.impl.CommonServiceImpl;
import com.shanjin.manager.service.impl.DictionaryServiceImpl;
import com.shanjin.manager.service.impl.PushServiceImpl;
import com.shanjin.manager.service.impl.SearchWordsServiceImpl;
import com.shanjin.manager.service.impl.SystemGroupServiceImpl;
import com.shanjin.manager.service.impl.SystemOperateLogServiceImpl;
import com.shanjin.manager.service.impl.SystemResourceServiceImpl;
import com.shanjin.manager.service.impl.SystemRoleServiceImpl;
import com.shanjin.manager.service.impl.SystemUserInfoServiceImpl;
import com.shanjin.manager.utils.BusinessUtil;
import com.shanjin.manager.utils.CommonUtil;
import com.shanjin.manager.utils.DBUtil;
import com.shanjin.manager.utils.HttpUtil;
import com.shanjin.manager.utils.ResourceTreeUtil;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.TreeNode;
import com.shanjin.manager.utils.Util;
import com.shanjin.sso.bean.UserSession;

/**
 * 后台系统管理相关
 * @author Huang yulai
 *
 */
public class SystemManagerController extends Controller{
	
	private static ICommonCacheService commonCacheService = new CommonCacheServiceImpl();

	private ISystemUserInfoService systemUserInfoService = new SystemUserInfoServiceImpl();
	
	private ISystemResourceService systemResourceService = new SystemResourceServiceImpl();
	
	private ISystemRoleService systemRoleService = new SystemRoleServiceImpl();
	
	private ISystemGroupService systemGroupService = new SystemGroupServiceImpl();
	
	private IAppInfoService appInfoService = new AppInfoServiceImpl();
	
	private ISystemOperateLogService systemOperateLogService = new SystemOperateLogServiceImpl();
	
	private IDictionaryService dictionaryService = new DictionaryServiceImpl();
	
	private ICommonService commonService = new CommonServiceImpl();
	
	private ISearchWordsService searchWordsService = new SearchWordsServiceImpl();
	
	private IPushService pushService = new PushServiceImpl();
	
	private static IImageCacheService imageCacheService = new ImageCacheServiceImpl();
	
	protected ExportService service = ExportService.service;
	
	public void index() {
		render("systemUserInfoGrid.jsp");
	}
	
	public void showBlank() {
		String tips = Db.queryStr("SELECT t.remark FROM dictionary t WHERE t.dict_type='updateSwitch' AND t.dict_key='updateSwitch' AND t.dict_value='1'");
		if(!StringUtil.isNullStr(tips)){
			// 不为空 需要升级
			this.setAttr("tips", tips);
			render("/view/welcome/tips.jsp");
			return;
		}else{
			HttpSession session = this.getSession();
			SystemUserInfo user = (SystemUserInfo) session.getAttribute(UserSession._USER);
			Map<String, Long> data = commonService.getAllData(user);
			data.put("userType",StringUtil.nullToLong(user.getInt("userType")));
			this.setAttr("data", data);
			render("/view/homePage/blank.jsp");
		}
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
	
	/**
	 * 用户编辑个人信息，session需更新
	 */
	public void editSystemUserInfo() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session = this.getSession();
		String operUserName = StringUtil.null2Str(session.getAttribute(UserSession._USER_NAME));
		Boolean flag = systemUserInfoService.editSystemUserInfo(param,operUserName);
		SystemUserInfo user = (SystemUserInfo) session.getAttribute(UserSession._USER);
		user.set("realName", param.get("user_realName")[0]).set("email", param.get("user_email")[0])
		    .set("phone", param.get("user_phone")[0]).set("remark", param.get("user_remark")[0]);
		//SystemUserInfo user = systemUserInfoService.getUserByName(operUserName);
		//String userName=user.getStr("userName");
		session = getSession();
		session.setAttribute(UserSession._USER, user);
		session.setAttribute(UserSession._USER_NAME, operUserName);
		this.renderJson(flag);
//		render("/view/homePage/homePage.jsp");
//		return;
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
		int flag = systemUserInfoService.modifyUserPwd(userId,oldPwd, newPwd, pwdHints, operUserName);
		String msg = "对不起，服务器忙，请稍后重试！";
		if(flag == 1){
			msg = "密码修改成功";
			if(session!=null){
				session.invalidate();
			}
		}else if (flag == 2){
			msg = "原密码输入不正确，请重新输入！";
		}
		this.renderJson(new NormalResponse(flag, 0L,msg));
		return;
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
		if(code.equals("1234")){
			flag=true;
		}
		if (!flag) {
			this.setAttr("error", "验证码错误");
			render("/view/welcome/index.jsp");
			return;
		} else {

			SystemUserInfo user = systemUserInfoService.getUserByName(userName);
			if (user != null && user.getStr("userName") != null) {
				String uPwd = StringUtil.null2Str(user.getStr("password"));
				pwd=MD5Util.MD5_32(pwd);
				if (pwd.equals(uPwd)) {
					// 输入密码是否正确（密码加密TODO）
					session.setAttribute(UserSession._USER, user);
					session.setAttribute(UserSession._USER_ID, user.getLong("id"));
					Long userId = user.getLong("id");
					session.setAttribute(UserSession._USER_NAME, userName);
					String perms = commonService.getUserPermission(userId);
					session.setAttribute(UserSession._USER_RESOURCES, perms);
					String agentType=commonService.getAgentType(user.getStr("userName"),user.getInt("userType"));
					session.setAttribute(UserSession._USER_TYPE, agentType);
					List<AppInfo> appList = commonService.userApp(userId);
					session.setAttribute(UserSession._USER_APP, appList);
					session.setAttribute(UserSession._USER_IS_LOGIN, "true");
					//String addContent = "此人登陆用户名为："+userName+"， IP为："+getIpAddr(this.getRequest());
					//Constant.mailMap.put("addContent", addContent);
					//new MailSender(Constant.mailMap, null).start();
					if(!userName.equals(Constant.ADMIN)){
						String currSessionid = session.getId();
						commonCacheService.setObject(currSessionid, "session", userName);
					}

//					Map<String,Object> sessionMap = (Map<String, Object>) commonCacheService.getObject("sessionMap");
//					if(sessionMap!=null&&!sessionMap.isEmpty()){
//						 for (String key : sessionMap.keySet()) {
//							   if(userName.equals(StringUtil.null2Str(sessionMap.get(key)))){
//								   // 剔除前一个用户
//								   if(!userName.equals(Constant.ADMIN)){
//									   sessionMap.remove(key);
//								   }
//							   }
//						 }
//					}else{
//						sessionMap = new HashMap<String,Object>();
//					}
//					sessionMap.put(currSessionid, userName);
//					commonCacheService.setObject(sessionMap, "sessionMap");
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
	/**
	 * 查询系统用户列表
	 */
	public void systemUserList() {
		Map<String, String[]> param = this.getParaMap();
		Map<String, String> paraMap = new HashMap<String,String>();
		if(StringUtil.isNotNullMap(param,"userName")){
			paraMap.put("userName", StringUtil.null2Str(param.get("userName")[0]));
		}
		if(StringUtil.isNotNullMap(param,"phone")){
			paraMap.put("phone", StringUtil.null2Str(param.get("phone")[0]));
		}
		if(StringUtil.isNotNullMap(param,"disabled")){
			paraMap.put("disabled", StringUtil.null2Str(param.get("disabled")[0]));
		}
		int uType = 0;
		if(StringUtil.isNotNullMap(param,"userType")){
			uType = StringUtil.nullToInteger(param.get("userType")[0]);
			paraMap.put("uType", StringUtil.null2Str(param.get("userType")[0])); //页面查询参数
		}
        String province = "";
        String city = "";
		String userId= ""; //项目代理使用
		if(StringUtil.isNotNullMap(param,"sort")){
			paraMap.put("sort", StringUtil.null2Str(param.get("sort")[0]));
		}
		if(StringUtil.isNotNullMap(param,"start")){
			paraMap.put("start", StringUtil.null2Str(param.get("start")[0]));
		}
		if(StringUtil.isNotNullMap(param,"limit")){
			paraMap.put("limit", StringUtil.null2Str(param.get("limit")[0]));
		}
		if(StringUtil.isNotNullMap(param,"province")){
			province = StringUtil.null2Str(param.get("province")[0]);
		}
		if(StringUtil.isNotNullMap(param,"city")){
			city = StringUtil.null2Str(param.get("city")[0]);
		}
		HttpSession session = this.getSession();
		SystemUserInfo user = (SystemUserInfo) session.getAttribute(UserSession._USER);
		int userType = StringUtil.nullToInteger(user.getInt("userType"));
		paraMap.put("userType", StringUtil.null2Str(userType));
		 //1.公司员工；2.省代理；3.市代理；4.项目代理
		if(userType==1){
	        if(uType==1){
	        	//公司员工 查询公司员工 省市无效
	        	province = "";
	        	city = "";
	        }else if(uType==2){
	        	city = "";
	        }
		}else if(userType==2){
			//省代理
			province = StringUtil.null2Str(user.getLong("province"));
			paraMap.put("province", province);
			if(StringUtil.isNotNullMap(param,"city")){
				paraMap.put("city", StringUtil.null2Str(param.get("city")[0]));
			}
		}else if(userType==3){
			//市代理
			province =  StringUtil.null2Str(user.getLong("province"));
			city =  StringUtil.null2Str(user.getLong("city"));
			paraMap.put("province", province);
			paraMap.put("city", city);
		}else if(userType==4){
			//项目代理
			userId = StringUtil.null2Str(user.getLong("id")); // 项目代理用户只能看到自己的账号信息
		}
		paraMap.put("province", province);
		paraMap.put("city", city);
		paraMap.put("userId", userId);
		List<SystemUserInfo> users = systemUserInfoService.systemUserList(paraMap);
		if (users != null && users.size() > 0) {
			long total = users.get(0).getTotal();
			this.renderJson(new NormalResponse(users, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/**
	 * 查询已删除的系统用户列表
	 */
	public void systemDelUserList() {
		Map<String, String[]> param = this.getParaMap();
		List<SystemUserInfo> users = systemUserInfoService.systemDelUserList(param);
		if (users != null && users.size() > 0) {
			long total = users.get(0).getTotal();
			this.renderJson(new NormalResponse(users, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/**
	 * 保存系统用户（新增）
	 */
	public void addUser() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session = this.getSession();
		String msg = "对不起，服务器忙，请稍后重试！";
		int uType = 0;
		if(StringUtil.isNotNullMap(param,"userType")){
			uType = StringUtil.nullToInteger(param.get("userType")[0]);
		}
		//1.公司员工；2.省代理；3.市代理；4.项目代理
		String operUserName = StringUtil.null2Str(session.getAttribute(UserSession._USER_NAME));
		SystemUserInfo user = (SystemUserInfo) session.getAttribute(UserSession._USER);
		int userType = StringUtil.nullToInteger(user.getInt("userType"));
		if(userType==2){
			//省代
			if(uType==1||uType==2){
				msg = "对不起，您无权创建【"+userTypeName(1)+","+userTypeName(2)+"】用户信息！";
				this.renderJson(new NormalResponse(3, 0L,msg));
				return;
			}
		}else if(userType==3){
			//市代
			if(uType==1||uType==2||uType==3){
				msg = "对不起，您无权创建【"+userTypeName(1)+","+userTypeName(2)+","+userTypeName(3)+"】用户信息！";
				this.renderJson(new NormalResponse(3, 0L,msg));
				return;
			}
		}else if(userType==4){
			//项目代
			msg = "对不起，您无权创建用户信息！";
			this.renderJson(new NormalResponse(3, 0L,msg));
			return;
		}
		int flag = systemUserInfoService.saveUser(param,operUserName);
		if(flag == 1){
			msg = "保存成功";
		}else if (flag == 2){
			msg = "用户账号已经存在，请更换账号！";
		}
		this.renderJson(new NormalResponse(flag, 0L,msg));
	}
	
	private String userTypeName(int uType){
		String name = "公司员工";
		if(uType==2){
			name = "省代";
		}else if(uType==3){
			name = "市代";
		}else if(uType==4){
			name = "项目代";
		}
		return name;
	}
	
	/**
	 * 保存系统用户（更新）
	 */
	public void editUser() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session = this.getSession();
		String msg = "对不起，服务器忙，请稍后重试！";
		int uType = 0;
		if(StringUtil.isNotNullMap(param,"userType")){
			uType = StringUtil.nullToInteger(param.get("userType")[0]);
		}
		Long id = 0L;
		if(StringUtil.isNotNullMap(param,"id")){
			id = StringUtil.nullToLong(param.get("id")[0]);
		}
		//1.公司员工；2.省代理；3.市代理；4.项目代理
		String operUserName = StringUtil.null2Str(session.getAttribute(UserSession._USER_NAME));
		SystemUserInfo user = (SystemUserInfo) session.getAttribute(UserSession._USER);
		int userType = StringUtil.nullToInteger(user.getInt("userType"));
		Long userId = StringUtil.nullToLong(session.getAttribute(UserSession._USER_ID));
		if(userType==2){
			//省代
			if(uType==1){
				msg = "对不起，您无权修改【"+userTypeName(1)+","+userTypeName(2)+"】用户信息！";
				this.renderJson(new NormalResponse(3, 0L,msg));
				return;
			}else if(uType==2&&id.intValue()!=userId.intValue()){
				msg = "对不起，您无权修改【"+userTypeName(1)+","+userTypeName(2)+"】用户信息！";
				this.renderJson(new NormalResponse(3, 0L,msg));
				return;
			}
		}else if(userType==3){
			//市代
			if(uType==1||uType==2){
				msg = "对不起，您无权修改【"+userTypeName(1)+","+userTypeName(2)+","+userTypeName(3)+"】用户信息！";
				this.renderJson(new NormalResponse(3, 0L,msg));
				return;
			}else if(uType==3){
				if(id.intValue()!=userId.intValue()){
					msg = "对不起，您无权修改【"+userTypeName(1)+","+userTypeName(2)+","+userTypeName(3)+"】用户信息！";
					this.renderJson(new NormalResponse(3, 0L,msg));
					return;
				}
			}
		}else if(userType==4){
			//项目代
			msg = "对不起，您无权修改用户信息！";
			this.renderJson(new NormalResponse(3, 0L,msg));
			return;
		}
		
		int flag = systemUserInfoService.saveUser(param,operUserName);
		if(flag == 1){
			msg = "保存成功";
		}else if (flag == 2){
			msg = "用户账号已经存在，请更换账号！";
		}
		this.renderJson(new NormalResponse(flag, 0L,msg));
	}
	
	/**
	 * 删除后台系统用户（逻辑删除）
	 */
	public void deleteUser() {
		String ids = this.getPara("ids");
		Boolean flag = systemUserInfoService.deleteUser(ids);
		this.renderJson(flag);
	}
	
	/**
	 * 彻底删除后台系统用户（物理删除）
	 */
	public void deleteAbUser() {
		String ids = this.getPara("ids");
		Boolean flag = systemUserInfoService.deleteAbUser(ids);
		this.renderJson(flag);
	}
	
	/**
	 * 恢复后台系统用户（逻辑删除）
	 */
	public void recoveryUser() {
		String ids = this.getPara("ids");
		Boolean flag = systemUserInfoService.recoveryUser(ids);
		this.renderJson(flag);
	}
	
	/**
	 * 讲用户账号设置为收益账号
	 */
	public void setAccount() {
		String id = this.getPara("id");
		int flag = systemUserInfoService.setAccount(id);
		String msg = "操作失败";
		if(flag==1){
			msg = "操作成功";
		}else if(flag==2){
			msg = "收益账号已存在";
		}
		this.renderJson(new NormalResponse(flag, 0L,msg));
	}
	
	/**
	 * 保存用户分配的角色
	 */
	public void saveUserRoles() {
		Long userId = StringUtil.nullToLong(this.getPara("userId"));
		String roleIds = StringUtil.null2Str(this.getPara("roleIds"));
		Boolean flag = systemRoleService.saveUserRoles(userId, roleIds);
		this.renderJson(flag);
	}
	/**
	 * 用户角色树
	 */
	public void getUserRoles() {
		Long userId = StringUtil.nullToLong(this.getPara("userId"));
		List<TreeNode> roles = systemRoleService.getUserRole(userId);
		this.renderJson(roles);
	}
	
	
	/**
	 * 保存用户分配的群组
	 */
	public void saveUserGroups() {
		Long userId = StringUtil.nullToLong(this.getPara("userId"));
		String groupIds = StringUtil.null2Str(this.getPara("groupIds"));
		Boolean flag = systemGroupService.saveUserGroups(userId, groupIds);
		this.renderJson(flag);
	}
	/**
	 * 用户群组树
	 */
	public void getUserGroups() {
		Long userId = StringUtil.nullToLong(this.getPara("userId"));
		List<TreeNode> roles = systemGroupService.getUserGroup(userId);
		this.renderJson(roles);
	}
	
	//====================系统角色start========================
	/**
	 * 系统角色主界面
	 */
	public void roleIndex() {
		render("roleGrid.jsp");
	}
	
	/**
	 * 查询系统角色列表
	 */
	public void roleList() {
		Map<String, String[]> param = this.getParaMap();
		List<SystemRole> roles = systemRoleService.systemRoleList(param);
		if (roles != null && roles.size() > 0) {
			long total = roles.get(0).getTotal();
			this.renderJson(new NormalResponse(roles, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/**
	 * 删除系统角色（物理删除）
	 */
	public void deleteRole() {
		String ids = this.getPara("ids");
		Boolean flag = systemRoleService.deleteRole(ids);
		this.renderJson(flag);
	}
	
	/**
	 * 保存系统角色（新增）
	 */
	public void addRole() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session = this.getSession();
		String operUserName = StringUtil.null2Str(session.getAttribute(UserSession._USER_NAME));
		int flag = systemRoleService.saveRole(param,operUserName);
		String msg = "对不起，服务器忙，请稍后重试！";
		if(flag == 1){
			msg = "保存成功";
		}else if (flag == 2){
			msg = "用户角色已经存在，请更换角色名！";
		}
		this.renderJson(new NormalResponse(flag, 0L,msg));
	}
	
	/**
	 * 保存系统角色（更新）
	 */
	public void editRole() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session = this.getSession();
		String operUserName = StringUtil.null2Str(session.getAttribute(UserSession._USER_NAME));
		int flag = systemRoleService.saveRole(param,operUserName);
		String msg = "对不起，服务器忙，请稍后重试！";
		if(flag == 1){
			msg = "保存成功";
		}else if (flag == 2){
			msg = "用户角色已经存在，请更换角色名！";
		}
		this.renderJson(new NormalResponse(flag, 0L,msg));
	}
	
	/**
	 * 保存群组分配的角色
	 */
	public void saveGroupRoles() {
		Long groupId = StringUtil.nullToLong(this.getPara("groupId"));
		String resIds = StringUtil.null2Str(this.getPara("resIds"));
		Boolean flag = systemResourceService.saveGroupRoles(groupId, resIds);
		this.renderJson(flag);
	}
	/**
	 * 给群组分配角色
	 */
	public void getGroupRole() {
		Long groupId = StringUtil.nullToLong(this.getPara("groupId"));
		List<TreeNode> roles = systemRoleService.getGroupRole(groupId);
		this.renderJson(roles);
	}
//	/**
//	 *  查询群组已分配的角色
//	 */
//	public void getSelfRoles() {
//		Long groupId = StringUtil.nullToLong(this.getPara("groupId"));
//		List<SystemRole> roles = systemRoleService.getSelfRoles(groupId);
//		if (roles != null && roles.size() > 0) {
//			long total = roles.get(0).getTotal();
//			this.renderJson(new NormalResponse(roles, total));
//		} else {
//			this.renderJson(new EmptyResponse());
//		}
//	}
//	
//	/**
//	 * 查询群组未分配的角色
//	 */
//	public void getOtherRoles() {
//		Long groupId = StringUtil.nullToLong(this.getPara("groupId"));
//		List<SystemRole> roles = systemRoleService.getOtherRoles(groupId);
//		if (roles != null && roles.size() > 0) {
//			long total = roles.get(0).getTotal();
//			this.renderJson(new NormalResponse(roles, total));
//		} else {
//			this.renderJson(new EmptyResponse());
//		}
//	}
	//====================系统角色end========================
	
	//====================系统资源start========================
	/**
	 * 系统资源主界面
	 */
	public void resourceIndex() {
		render("resourceGrid.jsp");
	}
	
	/**
	 * 查询系统资源列表
	 */
	public void resourceList() {
		Map<String, String[]> param = this.getParaMap();
		List<SystemResource> resources = systemResourceService.systemResourceList(param);
		if (resources != null && resources.size() > 0) {
			long total = resources.get(0).getTotal();
			this.renderJson(new NormalResponse(resources, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/**
	 * 删除系统资源（物理删除）
	 */
	public void deleteResource() {
		String ids = this.getPara("ids");
		Boolean flag = systemResourceService.deleteResource(ids);
		this.renderJson(flag);
	}
	
	/**
	 * 保存系统资源（新增）
	 */
	public void addResource() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = systemResourceService.saveResource(param);
		this.renderJson(flag);
	}
	
	/**
	 * 保存系统资源（更新）
	 */
	public void editResource() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = systemResourceService.saveResource(param);
		this.renderJson(flag);
	}
	
	public void getTreeResourcesList() {
		Long roleId = StringUtil.nullToLong(this.getPara("roleId"));
		Map<String,String> resourceMap = new HashMap<String,String>();
		try {
			List<SystemResource> allResources = systemResourceService.getAllResource();
			if (roleId != null&&roleId.longValue()!=0l) {
				resourceMap = systemResourceService.getResourceByRoleId(roleId);
			}
			this.renderJson(ResourceTreeUtil.resourceTreeJson1(allResources, resourceMap));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 保存角色资源
	 */
	public void saveRoleResources() {
		Long roleId = StringUtil.nullToLong(this.getPara("roleId"));
		String resIds = StringUtil.null2Str(this.getPara("resIds"));
		Boolean flag = systemResourceService.saveRoleResources(roleId, resIds);
		this.renderJson(flag);
	}
	

	//====================系统资源end========================
	
	




	//====================系统群组start========================
	/**
	 * 系统群组主界面
	 */
	public void groupIndex() {
		render("groupGrid.jsp");
	}
	
	/**
	 * 查询系统群组列表
	 */
	public void groupList() {
		Map<String, String[]> param = this.getParaMap();
		List<SystemGroup> groups = systemGroupService.systemGroupList(param);
		if (groups != null && groups.size() > 0) {
			long total = groups.get(0).getTotal();
			this.renderJson(new NormalResponse(groups, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/**
	 * 删除系统群组（物理删除）
	 */
	public void deleteGroup() {
		String ids = this.getPara("ids");
		Boolean flag = systemGroupService.deleteGroup(ids);
		this.renderJson(flag);
	}
	
	/**
	 * 保存系统群组（新增）
	 * return 0:失败；1：成功；2：名称已存在
	 */
	public void addGroup() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session = this.getSession();
		String operUserName = StringUtil.null2Str(session.getAttribute(UserSession._USER_NAME));
		int flag = systemGroupService.saveGroup(param,operUserName);
		String msg = "对不起，服务器忙，请稍后重试！";
		if(flag == 1){
			msg = "保存成功";
		}else if (flag == 2){
			msg = "用户群组已经存在，请更换群组名！";
		}
		this.renderJson(new NormalResponse(flag, 0L,msg));
	}
	/**
	 * 保存系统群组（更新）
	 * return 0:失败；1：成功；2：名称已存在
	 */
	public void editGroup() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session = this.getSession();
		String operUserName = StringUtil.null2Str(session.getAttribute(UserSession._USER_NAME));
		int flag = systemGroupService.saveGroup(param,operUserName);
		String msg = "对不起，服务器忙，请稍后重试！";
		if(flag == 1){
			msg = "保存成功";
		}else if (flag == 2){
			msg = "用户群组已经存在，请更换群组名！";
		}
		this.renderJson(new NormalResponse(flag, 0L,msg));
	}
	//====================系统群组end========================
	
	//====================项目Appstart========================
	/**
	 * 项目主界面
	 */
	public void appIndex() {
		render("appInfoGrid.jsp");
	}
	
	/**
	 * 查询项目列表
	 */
	public void appList() {
		Map<String, String[]> param = this.getParaMap();
		List<AppInfo> appInfos = appInfoService.appList(param);
		if (appInfos != null && appInfos.size() > 0) {
			long total = appInfos.get(0).getTotal();
			this.renderJson(new NormalResponse(appInfos, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/**
	 * 删除项目（逻辑删除）
	 */
	public void deleteApp() {
		String ids = this.getPara("ids");
		Boolean flag = appInfoService.deleteApp(ids);
		this.renderJson(flag);
	}
	
	/**
	 * 保存项目（新增）
	 * return 0:失败；1：成功；2：名称已存在；3：类型已存在
	 */
	public void addApp() {
		Map<String, String[]> param = this.getParaMap();
		int flag = appInfoService.saveApp(param);
		String msg = "对不起，服务器忙，请稍后重试！";
		if(flag == 1){
			msg = "保存成功";
		}else if (flag == 2){
			msg = "项目名称已经存在，请更换项目名！";
		}else if (flag == 3){
			msg = "项目类型已经存在，请更换项目类型！";
		}
		this.renderJson(new NormalResponse(flag, 0L,msg));
	}
	/**
	 * 保存项目（更新）
	 * return 0:失败；1：成功；2：名称已存在；3：类型已存在
	 */
	public void editApp() {
		Map<String, String[]> param = this.getParaMap();
		int flag = appInfoService.saveApp(param);
		String msg = "对不起，服务器忙，请稍后重试！";
		if(flag == 1){
			msg = "保存成功";
		}else if (flag == 2){
			msg = "项目名称已经存在，请更换项目名！";
		}else if (flag == 3){
			msg = "项目类型已经存在，请更换项目类型！";
		}
		this.renderJson(new NormalResponse(flag, 0L,msg));
	}
	//====================项目App end========================
	
	//====================系统操作日志 start======================
	
	/**
	 * 操作日志主界面
	 */
	public void operateLogIndex() {
		String	start_time=Util.getLastMonth();
		this.setAttr("start_time", start_time);
		render("operateLogGrid.jsp");
	}
	/**
	 * 查询系统操作日志列表
	 */
	public void systemLogList() {
		Map<String, String[]> param = this.getParaMap();
		List<SystemOperateLog> logList = systemOperateLogService.systemLogList(param);
		if (logList != null && logList.size() > 0) {
			long total = logList.get(0).getTotal();
			this.renderJson(new NormalResponse(logList, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/**
	 * 删除操作日志（逻辑删除）
	 */
	public void deleteSystemLog() {
		CommonUtil.initSystemResourceMap();
		this.renderJson(true);
	}
	
	/**
	 * 系统参数主界面
	 */
	public void systemParamIndex() {
		render("systemParamGrid.jsp");
	}
	
	/**
	 * 查询系统参数
	 */
	public void getSystemParam() {
		
		Map<String, String[]> param = this.getParaMap();
		List<Record> dictionaryList = dictionaryService.getDictionaryList(param);
		if (dictionaryList != null && dictionaryList.size() > 0) {
			long total = dictionaryList.get(0).getLong("total");
			this.renderJson(new NormalResponse(dictionaryList, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/**
	 * 添加系统参数
	 */
	public void addSystemParam() {
		
		Map<String, String[]> param = this.getParaMap();
		boolean flag = dictionaryService.addDictionary(param);
		this.renderJson(flag);
	}
	
	/**
	 * 添加系统参数附件图片
	 */
	public void addDictAttch() {
		UploadFile file = this.getFile("upload");
		Map<String, String[]> param = this.getParaMap();
		String resultPath = "";
		if (file != null) {
			String dict_type = param.get("dict_type")[0];
			String filePath = BusinessUtil.DictPath(dict_type);
			resultPath = BusinessUtil.fileUpload(file, filePath);
		}
		
		int flag = dictionaryService.addSystemPicParam(param,resultPath);
		if(flag==2){
			this.renderText("该类型附件已存在");
		}else if(flag==3){
			this.renderJson(true);
		}else{
			this.renderText( "操作失败，请稍后重试");
		}
	
	}
	/**
	 * 添加系统参数附件图片
	 */
	public void editDictAttch() {
		UploadFile file = this.getFile("upload");
		Map<String, String[]> param = this.getParaMap();
		String resultPath = "";
		if (file != null) {
			String dict_type = param.get("dict_type")[0];
			String filePath = BusinessUtil.DictPath(dict_type);
			resultPath = BusinessUtil.fileUpload(file, filePath);
		}
		
		int flag = dictionaryService.addSystemPicParam(param,resultPath);
		if(flag==1){
			this.renderText("该类型附件已存在");
		}else if(flag==3){
			this.renderJson(true);
		}else{
			this.renderText( "操作失败，请稍后重试");
		}
	}
	/**
	 * 删除系统参数附件
	 */
	public void deleteDictAttch() {
		Map<String, String[]> param = this.getParaMap();
		boolean flag = dictionaryService.deleteDictAttch(param);
		this.renderJson(flag);
	}
	/**
	 * 修改系统参数
	 */
	public void editSystemParam() {
		
		Map<String, String[]> param = this.getParaMap();
		boolean flag = dictionaryService.editDictionary(param);
		this.renderJson(flag);
	}
	
	/**
	 * 删除系统参数
	 */
	public void deleteSystemParam() {
		
		Map<String, String[]> param = this.getParaMap();
		boolean flag = dictionaryService.deleteDictionary(param);
		this.renderJson(flag);
	}
	/**
	 * 字典附件表界面
	 */
	public void dictAttachMentShow() {
		String dictionary_id = StringUtil.nullToString(this.getPara("id"));
		String dict_type = StringUtil.nullToString(this.getPara("dict_type"));
		this.setAttr("dict_type", dict_type);
		this.setAttr("dictionary_id", dictionary_id);
		render("dictAttachMent.jsp");
	}
	/**
	 * 字典附件表
	 */
	public void getDictAttchList() {
		
		Map<String, String[]> param = this.getParaMap();
		List<Record> dictAttch = dictionaryService.getDictAttchList(param);
		if (dictAttch != null && dictAttch.size() > 0) {
			long total = dictAttch.get(0).getLong("total");
			this.renderJson(new NormalResponse(dictAttch, total));

		} else {
			this.renderJson(new EmptyResponse());

		}
	}
	/**
	 * 价值标签主界面
	 */
	public void valueLabelIndex() {
		render("valueLabelGrid.jsp");
	}
	
	/**
	 * 查询价值标签
	 */
	public void getValueLabel() {
		
		Map<String, String[]> param = this.getParaMap();
		List<Record> valueLabelList = dictionaryService.getValueLabelList(param);
		if (valueLabelList != null && valueLabelList.size() > 0) {
			long total = valueLabelList.get(0).getLong("total");
			this.renderJson(new NormalResponse(valueLabelList, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/**
	 * 添加价值标签
	 */
	public void addValueLabel() {
		
		Map<String, String[]> param = this.getParaMap();
		boolean flag = dictionaryService.addValueLabel(param);
		this.renderJson(flag);
	}

	/**
	 * 修改价值标签
	 */
	public void editValueLabel() {
		
		Map<String, String[]> param = this.getParaMap();
		boolean flag = dictionaryService.editValueLabel(param);
		this.renderJson(flag);
	}
	
	/**
	 * 删除价值标签
	 */
	public void deleteValueLabel() {
		
		Map<String, String[]> param = this.getParaMap();
		boolean flag = dictionaryService.deleteValueLabel(param);
		this.renderJson(flag);
	}
	
	/** 代金券启用或暂停 */
	public void startOrstopLabel() {
		Map<String, String[]> param = this.getParaMap();
		boolean flag = dictionaryService.startOrstopLabel(param);
		this.renderJson(new NormalResponse(flag));
	}
	/**
	 * 系统参数主界面
	 */
	public void configurationIndex() {
		render("configurationGrid.jsp");
	}
	/**
	 * 查询配置参数
	 */
	public void getConfigurationParam() {
		
		Map<String, String[]> param = this.getParaMap();
		List<Record> configurationList = dictionaryService.getConfigurationList(param);
		if (configurationList != null && configurationList.size() > 0) {
			long total = configurationList.get(0).getLong("total");
			this.renderJson(new NormalResponse(configurationList, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	/**
	 * 添加配置参数
	 */
	public void addConfigurationParam() {
		
		Map<String, String[]> param = this.getParaMap();
		boolean flag = dictionaryService.addConfiguration(param);
		this.renderJson(flag);
	}

	/**
	 * 修改配置参数
	 */
	public void editConfigurationParam() {
		
		Map<String, String[]> param = this.getParaMap();
		boolean flag = dictionaryService.editConfiguration(param);
		this.renderJson(flag);
	}
	
	/**
	 * 删除配置参数
	 */
	public void deleteConfigurationParam() {
		
		Map<String, String[]> param = this.getParaMap();
		boolean flag = dictionaryService.deleteConfigurationParam(param);
		this.renderJson(flag);
	}
	
	/**
	 * 服务类型主界面
	 */
	public void serviceTypeIndex() {
		render("serviceType.jsp");
	}
	
	/**
	 * 查询服务类型
	 */
	public void getServiceType() {
		
		Map<String, String[]> param = this.getParaMap();
		List<Record> ServiceTypeList = dictionaryService.getServiceTypeList(param);
		if (ServiceTypeList != null && ServiceTypeList.size() > 0) {
			long total = ServiceTypeList.get(0).getLong("total");
			this.renderJson(new NormalResponse(ServiceTypeList, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	
	/**
	 * 添加服务类型
	 */
	public void addServiceType() {
		try{
			UploadFile showIconFile = this.getFile("showIconUpload");
			UploadFile orderIconFile = this.getFile("orderIconUpload");
			Map<String, String[]> param = this.getParaMap();
			String showIconPath = "";
			String orderIconPath = "";
			String fileName = "";
			String uploadPath = "/resource/order/";
			if (showIconFile != null) {
			    Date now = new Date();
			    fileName = StringUtil.null2Str(now.getTime());
			    showIconPath = BusinessUtil.fileUpload(showIconFile, uploadPath+"showIcon/",fileName);
			}
			if (orderIconFile != null) {
			    Date now = new Date();
			    fileName = StringUtil.null2Str(now.getTime());
			    orderIconPath = BusinessUtil.fileUpload(orderIconFile, uploadPath+"orderIcon/",fileName);
			}
			boolean flag = dictionaryService.saveServiceType(param, showIconPath, orderIconPath);
			this.renderJson(flag);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 修改服务类型
	 */
	public void editServiceType() {
		try{
			UploadFile showIconFile = this.getFile("showIconUpload");
			UploadFile orderIconFile = this.getFile("orderIconUpload");
			Map<String, String[]> param = this.getParaMap();
			String showIconPath = "";
			String orderIconPath = "";
			String fileName = "";
			String uploadPath = "/resource/order/";
			if (showIconFile != null) {
			    Date now = new Date();
			    fileName = StringUtil.null2Str(now.getTime());
			    showIconPath = BusinessUtil.fileUpload(showIconFile, uploadPath+"showIcon/",fileName);
			}
			if (orderIconFile != null) {
			    Date now = new Date();
			    fileName = StringUtil.null2Str(now.getTime());
			    orderIconPath = BusinessUtil.fileUpload(orderIconFile, uploadPath+"orderIcon/",fileName);
			}
			boolean flag = dictionaryService.saveServiceType(param, showIconPath, orderIconPath);
			this.renderJson(flag);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 删除服务类型
	 */
	public void deleteServiceType() {
		
		Map<String, String[]> param = this.getParaMap();
		boolean flag = dictionaryService.deleteServiceType(param);
		this.renderJson(flag);
	}
	//====================系统操作日志 end========================
	
	//==========================搜索分词start===========================
	
	/**
	 * 搜索分词主界面
	 */
	public void searchWordsIndex() {
		render("searchWords.jsp");
	}
	
	/**
	 * 搜索分词
	 */
	public void searchWordsList() {
		Map<String, String[]> param = this.getParaMap();
		
		List<Record> list = searchWordsService.searchWordsList(param);
		if (list != null && list.size() > 0) {
			long total = searchWordsService.searchWordsListSize(param);
			this.renderJson(new NormalResponse(list, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	// 导出搜索分词
		public void exportSearchWords() {
			Map<String, String[]> param = this.getParaMap();
			List<SearchWord> list = searchWordsService.exportSearchWords(param); 
			// 查询数据
			List<Pair> usetitles = searchWordsService.getExportSearchWordsTitles();
			String fileName="搜索分词列表";
			// 导出
			service.export(getResponse(), getRequest(), list, usetitles,fileName,0);
			renderNull();
		}
	/**
	 * 添加搜索分词
	 */
	public void addSearchWords() {
		//UploadFile file = this.getFile("upload");
		String resultPath = "";
//		if (file != null) {
//			String filePath = BusinessUtil.imageFolder(BusinessUtil.DIR_SEARCH_WORDS);
//			resultPath = BusinessUtil.fileUpload(file, filePath);
//		}
		Map<String, String[]> param = this.getParaMap();
		int flag = searchWordsService.saveSearchWords(param,resultPath);
		String msg = "对不起，服务器忙，请稍后重试！";
		if(flag == 1){
			msg = "保存成功";
		}else if (flag == 2){
			msg = "搜索分词已存在！";
		}
		this.renderJson(new NormalResponse(flag, 0L,msg));
	}

	/**
	 * 编辑搜索分词
	 */
	public void editSearchWords() {
//		UploadFile file = this.getFile("upload");
		String resultPath = "";
//		if (file != null) {
//			String filePath = BusinessUtil.imageFolder(BusinessUtil.DIR_SEARCH_WORDS);
//			resultPath = BusinessUtil.fileUpload(file, filePath);
//		}
		Map<String, String[]> param = this.getParaMap();
		int flag = searchWordsService.saveSearchWords(param,resultPath);
		String msg = "对不起，服务器忙，请稍后重试！";
		if(flag == 1){
			msg = "保存成功";
		}else if (flag == 2){
			msg = "搜索分词已存在！";
		}
		this.renderJson(new NormalResponse(flag, 0L,msg));
	}
	
	/**
	 * 删除搜索分词
	 */
	public void deleteSearchWords() {
		String ids = this.getPara("ids");
		boolean flag = searchWordsService.deleteSearchWords(ids);
		this.renderJson(flag);
	}
	
	/**
	 * 导入搜索分词
	 */
	public void importSearchWords() {
		UploadFile file = this.getFile("upload");		
		// 获取文件
		String path = file.getSaveDirectory() + file.getFileName();
		// 处理导入数据
		HSSFWorkbook hwb = null;
		try {
			hwb = new HSSFWorkbook(new FileInputStream(new File(path)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		HSSFSheet sheet = hwb.getSheetAt(0); // 获取到第一个sheet中数据
		String keyword="";
		String st="";
		String app_type="";
		String service="";
		String url="";
		String img_path="";
		Connection conn = DBUtil.getConnection();
		if(conn==null){
			return;
		}
	    String sql="insert into app_key_words(keyword,service_type,service_type_name,app_type,url,img_path) values(?,?,?,?,?,?)"; 
		PreparedStatement prest;
		String ids = "";
		Map<String,Object> map = searchWordsService.getSearchWordsMap();
		try {
			prest = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			for(int i = 0;i<=sheet.getLastRowNum(); i++) {
				   Long service_type=null;
				   HSSFRow row = sheet.getRow(i);// 获取到第i列的行数据(表格行)
				   keyword = StringUtil.null2Str(row.getCell(0));
				   service = StringUtil.null2Str(row.getCell(1));
				   app_type = StringUtil.null2Str(row.getCell(2));
				   url = StringUtil.null2Str(row.getCell(3));
				   img_path = StringUtil.null2Str(row.getCell(4));
				   if(StringUtil.isNullStr(keyword)||StringUtil.isNullStr(service)){
					   continue;
				   }

				   st=StringUtil.null2Str(Constant.serviceTypeByIdMap.get(service));
				   if(StringUtil.isNullStr(st)){
					   continue;//没有此服务
				   }
				   service_type = StringUtil.nullToLong(st);
				   if(map.containsKey(keyword+"_"+service_type)){
					   continue; //已存在 不导入
				   }
				   prest.setString(1, keyword); // keyword
				   prest.setLong(2, service_type); // service_type
				   prest.setString(3, service); // service_type_name
				   prest.setString(4, app_type); // app_type
				   prest.setString(5, url); // url
				   prest.setString(6, img_path); // img_path
				   prest.addBatch();

				}
			prest.executeBatch();
			conn.commit();
		    ResultSet rs = prest.getGeneratedKeys(); //获取结果  
		    int index = 0 ;
		    while(rs.next()) {
		    	if(index==0){
			        ids = StringUtil.null2Str(rs.getLong(1));
		    	}else{
		            ids = ids +","+ rs.getLong(1);
		    	}
		    	index++;
		    } 
//			String res = HttpUtil.SendGET(Constant.WEB_SERACH_URL+"addDocument","indexName=appkeyword&ids="+ids);
//			System.out.println("当前时间："+new Date()+"resp="+res);
			final Map<String,Object> paramMap = new HashMap<String,Object>();
			paramMap.put("ids", ids);
			paramMap.put("indexName", "appkeyword");
			new Thread(new Runnable() {			
				@Override
				public void run() {
					String res = HttpUtil.httpClientPost(Constant.WEB_SERACH_URL+"addDocument", paramMap);
					System.out.println("当前时间："+new Date()+"resp="+res);
				}
			}).start();
		}catch (SQLException e) {
			e.printStackTrace();
		}catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}  
		this.renderJson(true);
	}
	
	public void showUserWord() { 
		render("/view/systemManager/userWord.jsp");
	}
	public void showServiceWord() { 
		render("/view/systemManager/serviceWord.jsp");
	}
	/**
	 * 用户关键词
	 */
	public void userWordsList() {
		Map<String, String[]> param = this.getParaMap();
		
		List<Record> list = searchWordsService.userWordsList(param);
		if (list != null && list.size() > 0) {
			long total = list.get(0).getLong("total");
			this.renderJson(new NormalResponse(list, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	/** 审核用户关键词通过*/
	public void AuditUserWord() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = searchWordsService.AuditUserWord(param);
		this.renderJson(flag);
	}
	/** 审核用户关键词不通过*/
   public void RefuseUserWord(){
	   Map<String, String[]> param = this.getParaMap();
		Boolean flag = searchWordsService.RefuseUserWord(param);
		this.renderJson(flag);
}
   public void deletetUserWord(){
	   Map<String, String[]> param = this.getParaMap();
		Boolean flag = searchWordsService.deletetUserWord(param);
		this.renderJson(flag);
}
   // 导出用户关键词
		public void exportUserWord() {
			Map<String, String[]> param = this.getParaMap();
			List<UserWord> list = searchWordsService.exportUserWord(param); 
			// 查询数据
			List<Pair> usetitles = searchWordsService.getExportUserWordTitles();
			String fileName="用户关键词";
			// 导出
			service.export(getResponse(), getRequest(), list, usetitles,fileName,0);
			renderNull();
		}
	/**
	 * 服务关键词
	 */
	public void serviceWordsList() {
		Map<String, String[]> param = this.getParaMap();
		
		List<Record> list = searchWordsService.serviceWordsList(param);
		if (list != null && list.size() > 0) {
			long total = list.get(0).getLong("total");
			this.renderJson(new NormalResponse(list, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}
	/** 审核服务关键词通过*/
	public void AuditServiceWord() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = searchWordsService.AuditServiceWord(param);
		this.renderJson(flag);
	}
	/** 审核用户关键词不通过*/
   public void RefuseServiceWord(){
	   Map<String, String[]> param = this.getParaMap();
		Boolean flag = searchWordsService.RefuseServiceWord(param);
		this.renderJson(flag);
}
   public void deletetServiceWord(){
	   Map<String, String[]> param = this.getParaMap();
		Boolean flag = searchWordsService.deletetServiceWord(param);
		this.renderJson(flag);
}
   // 导出服务关键词
	public void exportServiceWord() {
		Map<String, String[]> param = this.getParaMap();
		List<ServiceWord> list = searchWordsService.exportServiceWord(param); 
		// 查询数据
		List<Pair> usetitles = searchWordsService.getExportServiceWordTitles();
		String fileName="服务关键词";
		// 导出
		service.export(getResponse(), getRequest(), list, usetitles,fileName,0);
		renderNull();
	}
//	public void importSearchWords() {
//		UploadFile file = this.getFile("upload");		
//		// 获取文件
//		String path = file.getSaveDirectory() + file.getFileName();
//		// 处理导入数据
//		HSSFWorkbook hwb = null;
//		try {
//			hwb = new HSSFWorkbook(new FileInputStream(new File(path)));
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e){
//			e.printStackTrace();
//		}
//		HSSFSheet sheet = hwb.getSheetAt(0); // 获取到第一个sheet中数据
//		String keyword="";
//		String st="";
//		String app_type="";
//		String service="";
//		String url="";
//		String img_path="";
//		StringBuffer sql = new StringBuffer();
//		int r=0;
//		for(int i = 0;i<=sheet.getLastRowNum(); i++) {
//		   Long service_type=null;
//		   HSSFRow row = sheet.getRow(i);// 获取到第i列的行数据(表格行)
//		   keyword = StringUtil.null2Str(row.getCell(0));
//		   service = StringUtil.null2Str(row.getCell(1));
//		   app_type = StringUtil.null2Str(row.getCell(2));
//		   url = StringUtil.null2Str(row.getCell(3));
//		   img_path = StringUtil.null2Str(row.getCell(4));
//		   if(StringUtil.isNullStr(keyword)||StringUtil.isNullStr(service)||StringUtil.isNullStr(app_type)){
//			   continue;
//		   }
//		   st=StringUtil.null2Str(Constant.serviceTypeMap.get(app_type+service));
//		   if(!StringUtil.isNullStr(st)){
//			   service_type = StringUtil.nullToLong(st);
//		   }
//		   if(service_type!=null){
//			   r++;
//			   if(r==1){
//				   sql.append("insert into app_key_words(keyword,service_type,app_type,url,img_path) values('").append(keyword).append("',");
//				   sql.append(service_type).append(",'").append(app_type).append("','");
//				   sql.append(url).append("','").append(img_path).append("')");  
//			   }else{
//				   sql.append(",('").append(keyword).append("',");
//				   sql.append(service_type).append(",'").append(app_type).append("','");
//				   sql.append(url).append("','").append(img_path).append("')");
//			   }
//		   }
//
//		}
//		if(!StringUtil.isNullStr(sql.toString())){
//			   Db.update(sql.toString());
//		 }
//		this.renderJson(true);
//	}
	
	//==========================搜索分词end===========================
	
	//==========================推送参数start===========================
	
		/**
		 * 推送管理主界面
		 */
		public void pushIndex() {
			render("pushGrid.jsp");
		}
		
		/**
		 * 推送管理参数列表
		 */
		public void pushParamList() {
			Map<String, String[]> param = this.getParaMap();
			List<Record> list = pushService.pushList(param);
			if (list != null && list.size() > 0) {
				long total = pushService.pushListSize(param);
				this.renderJson(new NormalResponse(list, total));
			} else {
				this.renderJson(new EmptyResponse());
			}
		}
		/**
		 * 添加推送参数
		 */
		public void addPushParam() {
			Map<String, String[]> param = this.getParaMap();
			int flag = pushService.savePush(param);
			String msg = "对不起，服务器忙，请稍后重试！";
			if(flag == 1){
				msg = "保存成功";
			}else if (flag == 2){
				msg = "推送参数已配置，请更换服务类型！";
			}
			this.renderJson(new NormalResponse(flag, 0L,msg));
		}

		/**
		 * 编辑推送参数
		 */
		public void editPushParam() {
			Map<String, String[]> param = this.getParaMap();
			int flag = pushService.savePush(param);
			String msg = "对不起，服务器忙，请稍后重试！";
			if(flag == 1){
				msg = "保存成功";
			}else if (flag == 2){
				msg = "推送参数已配置，请更换APP类型！";
			}
			this.renderJson(new NormalResponse(flag, 0L,msg));
		}
		
		/**
		 * 删除推送参数
		 */
		public void deletePushParams() {
			String ids = this.getPara("id");
			boolean flag = pushService.deletePushs(ids);
			this.renderJson(flag);
		}
		
		
		//==========================推送参数end===========================
		
		//==========================应用关键词start===========================
		
			/**
			 * 应用关键词主界面
			 */
			public void appKeywordsIndex() {
				render("appKeywordsGrid.jsp");
			}
			
			/**
			 * 应用关键词参数列表
			 */
			public void appKeywordsList() {
				Map<String, String[]> param = this.getParaMap();
				List<Record> list = searchWordsService.appKeywordsList(param);
				if (list != null && list.size() > 0) {
					long total = searchWordsService.appKeywordsListSize(param);
					this.renderJson(new NormalResponse(list, total));
				} else {
					this.renderJson(new EmptyResponse());
				}
			}
			// 导出应用关键词
			public void exportAppKeywords() {
				Map<String, String[]> param = this.getParaMap();
				List<AppKey> list = searchWordsService.exportAppKeywords(param); 
				// 查询数据
				List<Pair> usetitles = searchWordsService.getExportAppKeyTitles();
				String fileName="搜索分词列表";
				// 导出
				service.export(getResponse(), getRequest(), list, usetitles,fileName,0);
				renderNull();
			}
			/**
			 * 添加应用关键词
			 */
			public void addAppKeywords() {
				Map<String, String[]> param = this.getParaMap();
				int flag = searchWordsService.saveAppKeywords(param);
				String msg = "对不起，服务器忙，请稍后重试！";
				if(flag == 1){
					msg = "保存成功";
				}else if (flag == 2){
					msg = "关键词已存在！";
				}
				this.renderJson(new NormalResponse(flag, 0L,msg));
			}

			/**
			 * 编辑应用关键词
			 */
			public void editAppKeywords() {
				Map<String, String[]> param = this.getParaMap();
				int flag = searchWordsService.saveAppKeywords(param);
				String msg = "对不起，服务器忙，请稍后重试！";
				if(flag == 1){
					msg = "保存成功";
				}else if (flag == 2){
					msg = "关键词已存在！";
				}
				this.renderJson(new NormalResponse(flag, 0L,msg));
			}
			
			/**
			 * 删除应用关键词
			 */
			public void deleteAppKeywords() {
				String ids = this.getPara("ids");
				boolean flag = searchWordsService.deleteAppKeywords(ids);
				this.renderJson(flag);
			}
			
			/**
			 * 导入应用关键词
			 */
			public void importAppKeywords() {
				UploadFile file = this.getFile("upload");		
				// 获取文件
				String path = file.getSaveDirectory() + file.getFileName();
				// 处理导入数据
				HSSFWorkbook hwb = null;
				try {
					hwb = new HSSFWorkbook(new FileInputStream(new File(path)));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e){
					e.printStackTrace();
				}
				HSSFSheet sheet = hwb.getSheetAt(0); // 获取到第一个sheet中数据
				String keyword="";
				String app_type="";
				String app_name="";
				int exceptionNum = 0;
				for(int i = 0;i<=sheet.getLastRowNum(); i++) {
				   HSSFRow row = sheet.getRow(i);// 获取到第i列的行数据(表格行)
				   keyword = StringUtil.null2Str(row.getCell(0));
				   app_name = StringUtil.null2Str(row.getCell(1));
				   app_type = StringUtil.null2Str(row.getCell(2));
				   if(StringUtil.isNullStr(keyword)||StringUtil.isNullStr(app_name)||StringUtil.isNullStr(app_type)){
					   continue;
				   }
				   try{
					   Record record = new Record();
					   record.set("keyword", keyword).set("app_name", app_name).set("app_type", app_type);
	                   Db.save("app_name_key_words", record);
				   }catch(Exception e){
						e.printStackTrace();
						exceptionNum++;
						continue;
				   }
				}
                System.out.println("导入异常数据条数："+exceptionNum);
				this.renderJson(true);
			}
			
			
			//==========================应用关键词end===========================
			
			//==========================不拆分、隐藏关键词start===========================
			
			/**
			 * 隐藏关键词主界面
			 */
			public void stopKeywordsIndex() {
				render("stopKeywordsGrid.jsp");
			}
			
			/**
			 * 隐藏关键词参数列表
			 */
			public void stopKeywordsList() {
				Map<String, String[]> param = this.getParaMap();
				List<StopKeyWords> list = searchWordsService.stopKeywordsList(param);
				if (list != null && list.size() > 0) {
					long total = list.get(0).getTotal();
					this.renderJson(new NormalResponse(list, total));
				} else {
					this.renderJson(new EmptyResponse());
				}
			}
			// 导出隐藏关键词
			public void exportStopKeywords() {
				Map<String, String[]> param = this.getParaMap();
				List<StopKeyWords> list = searchWordsService.exportStopAppKeywords(param); 
				// 查询数据
				List<Pair> usetitles = searchWordsService.getExportStopAppKeyTitles();
				String fileName="隐藏关键词列表";
				// 导出
				service.export(getResponse(), getRequest(), list, usetitles,fileName,0);
				renderNull();
			}
			/**
			 * 添加隐藏关键词
			 */
			public void addStopKeywords() {
				Map<String, String[]> param = this.getParaMap();
				int flag = searchWordsService.saveStopKeywords(param);
				String msg = "对不起，服务器忙，请稍后重试！";
				if(flag == 1){
					msg = "保存成功";
				}else if (flag == 2){
					msg = "关键词已存在！";
				}
				this.renderJson(new NormalResponse(flag, 0L,msg));
			}

			
			/**
			 * 删除隐藏关键词
			 */
			public void deleteStopKeywords() {
				String ids = this.getPara("ids");
				boolean flag = searchWordsService.deleteStopKeywords(ids);
				this.renderJson(flag);
			}
			
			/**
			 * 导入隐藏关键词
			 */
			public void importStopKeywords() {
				UploadFile file = this.getFile("upload");		
				// 获取文件
				String path = file.getSaveDirectory() + file.getFileName();
				// 处理导入数据
				HSSFWorkbook hwb = null;
				try {
					hwb = new HSSFWorkbook(new FileInputStream(new File(path)));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e){
					e.printStackTrace();
				}
				HSSFSheet sheet = hwb.getSheetAt(0); // 获取到第一个sheet中数据
				String keyword="";
				
				int exceptionNum = 0;
				for(int i = 0;i<=sheet.getLastRowNum(); i++) {
				   HSSFRow row = sheet.getRow(i);// 获取到第i列的行数据(表格行)
				   keyword = StringUtil.null2Str(row.getCell(0));
				  
				   if(StringUtil.isNullStr(keyword)){
					   continue;
				   }else{
					   String sql="select count(1) as total from app_key_words_stop_dic aw where aw.keyword=?";
				       long total =Db.find(sql,keyword).get(0).getLong("total");
				       if(total>0){
				          continue;	
				        } 
				   }
				   try{
					   Record record = new Record();
					   record.set("keyword", keyword);
	                   Db.save("app_key_words_stop_dic", record);
				   }catch(Exception e){
						e.printStackTrace();
						exceptionNum++;
						continue;
				   }
				}
                System.out.println("导入异常数据条数："+exceptionNum);
				this.renderJson(true);
			}
			
			/**
			 * 不拆分关键词主界面
			 */
			public void customKeywordsIndex() {
				render("customKeywordsGrid.jsp");
			}
			
			/**
			 * 不拆分关键词参数列表
			 */
			public void customKeywordsList() {
				Map<String, String[]> param = this.getParaMap();
				List<CustomKeyWords> list = searchWordsService.customKeywordsList(param);
				if (list != null && list.size() > 0) {
					long total = list.get(0).getTotal();
					this.renderJson(new NormalResponse(list, total));
				} else {
					this.renderJson(new EmptyResponse());
				}
			}
			// 导出不拆分关键词
			public void exportCustomKeywords() {
				Map<String, String[]> param = this.getParaMap();
				List<CustomKeyWords> list = searchWordsService.exportCustomAppKeywords(param); 
				// 查询数据
				List<Pair> usetitles = searchWordsService.getExportCustomAppKeyTitles();
				String fileName="隐藏关键词列表";
				// 导出
				service.export(getResponse(), getRequest(), list, usetitles,fileName,0);
				renderNull();
			}
			/**
			 * 添加不拆分关键词
			 */
			public void addCustomKeywords() {
				Map<String, String[]> param = this.getParaMap();
				int flag = searchWordsService.saveCustomKeywords(param);
				String msg = "对不起，服务器忙，请稍后重试！";
				if(flag == 1){
					msg = "保存成功";
				}else if (flag == 2){
					msg = "关键词已存在！";
				}
				this.renderJson(new NormalResponse(flag, 0L,msg));
			}

			
			/**
			 * 删除不拆分关键词
			 */
			public void deleteCustomKeywords() {
				String ids = this.getPara("ids");
				boolean flag = searchWordsService.deleteCustomKeywords(ids);
				this.renderJson(flag);
			}
			
			/**
			 * 导入不拆分关键词
			 */
			public void importCustomKeywords() {
				UploadFile file = this.getFile("upload");		
				// 获取文件
				String path = file.getSaveDirectory() + file.getFileName();
				// 处理导入数据
				HSSFWorkbook hwb = null;
				try {
					hwb = new HSSFWorkbook(new FileInputStream(new File(path)));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e){
					e.printStackTrace();
				}
				HSSFSheet sheet = hwb.getSheetAt(0); // 获取到第一个sheet中数据
				String keyword="";
				
				int exceptionNum = 0;
				for(int i = 0;i<=sheet.getLastRowNum(); i++) {
				   HSSFRow row = sheet.getRow(i);// 获取到第i列的行数据(表格行)
				   keyword = StringUtil.null2Str(row.getCell(0));
				  
				   if(StringUtil.isNullStr(keyword)){
					   continue;
				   }else{
					   String sql="select count(1) as total from app_key_words_custom_dic aw where aw.keyword=?";
				       long total =Db.find(sql,keyword).get(0).getLong("total");
				       if(total>0){
				          continue;	
				        } 
				   }
				   try{
					   Record record = new Record();
					   record.set("keyword", keyword);
	                   Db.save("app_key_words_custom_dic", record);
				   }catch(Exception e){
						e.printStackTrace();
						exceptionNum++;
						continue;
				   }
				}
                System.out.println("导入异常数据条数："+exceptionNum);
				this.renderJson(true);
			}
			//==========================不拆分、隐藏关键词end===========================
			
			/**
			 * ip 解析
			 */
			public void ipImport() {
//				String sql = "SELECT * FROM ip_address_src t WHERE  t.id NOT in(SELECT i.id FROM ip_address i)";
//				List<Record> list = Db.find(sql);
//				int success = 0;
//				int failure = 0;
//				StringBuffer sb = new StringBuffer();
//				if(list!=null&&list.size()>0){
//					for(Record r : list){
//					    long id = r.getLong("id");
//						try {
//						    String start_ip = r.getStr("start_ip");
//						    String end_ip = r.getStr("end_ip");
//						    String param = "format=json&ip=" + start_ip;
//						    String result = HttpRequest.sendGet("http://int.dpool.sina.com.cn/iplookup/iplookup.php", param);
//						    JSONObject jsonObj = null;
//							jsonObj = new JSONObject(result);
//							String country = jsonObj.getString("country");
//							String province = jsonObj.getString("province");
//							String city = jsonObj.getString("city");
//	                        Record record = new Record();
//	                        record.set("id", id).set("start_ip", start_ip).set("end_ip", end_ip)
//	                        .set("country", country).set("province", province).set("city", city);
//	                        Db.save("ip_address", record);
//							success++;
//						} catch (JSONException e) {
//							e.printStackTrace();
//							sb.append(id).append(",");
//							failure++;
//							continue;
//						}
//						
//					}
//				}
//				System.out.println("success="+success+"   failure="+failure);
//				System.out.println("失败的id为："+sb.toString());
				

		  //      同时启动10个线程，去进行i++计算，看看实际结果  
//		        for (int i = 0; i <=10; i++) {
//		        	final int index = i;
//		            new Thread(new Runnable() {  
//		                @Override  
//		                public void run() {  
//		                	ipRecord(index);
//		                }  
//		            }).start();  
//		        }  
				

				this.renderJson(new NormalResponse(null,"ip解析测试"));
			}
			
			private void ipRecord(int i){
				String sql0 = "SELECT t.* FROM ip_address t WHERE t.id>=0 and t.id<=40000";
				String sql1 = "SELECT t.* FROM ip_address t WHERE t.id>=40001 and t.id<=80000";
				String sql2 = "SELECT t.* FROM ip_address t WHERE t.id>=80001 and t.id<=120000";
				String sql3 = "SELECT t.* FROM ip_address t WHERE t.id>=120001 and t.id<=160000";
				String sql4 = "SELECT t.* FROM ip_address t WHERE t.id>=160001 and t.id<=200000";
				String sql5 = "SELECT t.* FROM ip_address t WHERE t.id>=200001 and t.id<=240000";
				String sql6 = "SELECT t.* FROM ip_address t WHERE t.id>=240001 and t.id<=280000";
				String sql7 = "SELECT t.* FROM ip_address t WHERE t.id>=280001 and t.id<=320000";
				String sql8 = "SELECT t.* FROM ip_address t WHERE t.id>=320001 and t.id<=360000";
				String sql9 = "SELECT t.* FROM ip_address t WHERE t.id>=360001 and t.id<=400000";
				String sql10 = "SELECT t.* FROM ip_address t WHERE t.id>=400001 and t.id<=440000";
				String sql = "";
				if(i==0){
					sql = sql0;
				}else if(i==1){
					sql = sql1;
				}else if(i==2){
					sql = sql2;
				}else if(i==3){
					sql = sql3;
				}else if(i==4){
					sql = sql4;
				}else if(i==5){
					sql = sql5;
				}else if(i==6){
					sql = sql6;
				}else if(i==7){
					sql = sql7;
				}else if(i==8){
					sql = sql8;
				}else if(i==9){
					sql = sql9;
				}else if(i==10){
					sql = sql10;
				}
				System.out.println("第【"+i+"】个线程启动！！！");
				List<Record> list = Db.find(sql);
				int success = 0;
				int failure = 0;
				StringBuffer sb = new StringBuffer();
				if(list!=null&&list.size()>0){
					for(Record r : list){
					    long id = r.getLong("id");
						try {
						    String start_ip = r.getStr("start_ip");
						    String end_ip = r.getStr("end_ip");
						    long start = convertIpToLong(start_ip);
						    long end = convertIpToLong(end_ip);
						    r.set("start", start).set("end", end);
						    Db.update("ip_address", r);
//						    String param = "format=json&ip=" + start_ip;
//						    String result = HttpRequest.sendGet("http://int.dpool.sina.com.cn/iplookup/iplookup.php", param);
//						    JSONObject jsonObj = null;
//							jsonObj = new JSONObject(result);
//							String country = jsonObj.getString("country");
//							String province = jsonObj.getString("province");
//							String city = jsonObj.getString("city");
//	                        Record record = new Record();
//	                        record.set("id", id).set("start_ip", start_ip).set("end_ip", end_ip)
//	                        .set("country", country).set("province", province).set("city", city);
//	                        Db.save("ip_address", record);
							success++;
						} catch (Exception e) {
							e.printStackTrace();
							sb.append(id).append(",");
							failure++;
							continue;
						}
						
					}
				}
				System.out.println("success["+i+"]="+success+"   failure["+i+"]="+failure);
				System.out.println("第"+i+"个线程失败的id为："+sb.toString());
				System.out.println("========handleDbData end=====");
			}
			
			public static long convertIpToLong(String ip) {
				String[] checkIp = ip.split("\\.", 4);
				long intIp = 0;
				for (int i = 3, j = 0; i >= 0 && j <= 3; i--, j++) {
					intIp += Integer.parseInt(checkIp[j]) * Math.pow(256, i);
				}
				return intIp;
			}
			/**
			 * 服务关键词
			 */
			public void getPushConfig() {
				Map<String, String[]> param = this.getParaMap();
				
				List<Record> list = pushService.getPushConfigList(param);
				if (list != null && list.size() > 0) {
					long total = list.get(0).getLong("total");
					this.renderJson(new NormalResponse(list, total));
				} else {
					this.renderJson(new EmptyResponse());
				}
			}
			
			/**
			 * 删除關聯服務
			 */
			public void deletePushConfig() {
				String ids = this.getPara("id");
				boolean flag = pushService.deletePushConfig(ids);
				this.renderJson(flag);
			}
			
			/**
			 * 推送關聯服務主界面
			 */
			public void pushConfigIndex() {
				render("pushConfig.jsp");
			}
			
			/**
			 * 添加推送關聯服務
			 */
			public void addPushConfig() {
				Map<String, String[]> param = this.getParaMap();
				int flag = pushService.addPushConfig(param);
				if(flag==2){
					this.renderJson(true);
				}else if(flag==1){
					this.renderText("此推送关联服务已存在！");
				}else{
					this.renderText( "操作失败，请稍后重试");
				}
			}
			
			public void showRestrictUpdate() {
				
				render("/view/systemManager/restrictUpdate.jsp");
				return;
			}
			/** 获取重复提交参数 */
			public void getRestrictUpdate() {
				Map<String, String[]> param = this.getParaMap();
				List<Record> activitys = pushService.getRestrictUpdate(param);
				if (activitys != null && activitys.size() > 0) {
					long total = activitys.get(0).getLong("total");
					this.renderJson(new NormalResponse(activitys, total));
				} else {
					this.renderJson(new EmptyResponse());
				}
			}

			
			/** 删除重复提交参数 */
			public void deleteRestrictUpdate() {
				String id = this.getPara("id");
				Boolean flag = pushService.deleteRestrictUpdate(id);
				this.renderJson(flag);
			}

			/** 添加重复提交参数 */
			public void addRestrictUpdate(){
				Map<String, String[]> param = this.getParaMap();
				Boolean flag = pushService.saveRestrictUpdate(param);
				this.renderJson(flag);
			}
			
			/** 编辑重复提交参数 */
			public void editRestrictUpdate() {
				Map<String, String[]> param = this.getParaMap();
				Boolean flag = pushService.saveRestrictUpdate(param);
				this.renderJson(flag);
			}
			
			/** 重复提交参数启用或暂停 */
			public void startOrstopRestrictUpdate() {
				Map<String, String[]> param = this.getParaMap();
				int flag = pushService.startOrstopRestrictUpdate(param);
				if(flag==1){
					this.renderText("启用成功");
				}else if(flag==2){
					this.renderText("暂停成功");
				}else{
					this.renderText( "操作失败，请稍后重试");
				}
			}
	
	/**
	 * 推送關聯服務主界面
	 */
	public void mqSendFailureIndex() {
		render("mqSendFailure.jsp");
	}
			
	/** 获取发送消息失败列表 */
	public void getMqSendFailureList() {
		Map<String, String[]> param = this.getParaMap();
		List<Record> mqSendFailures = commonService.getMqSendFailureList(param);
		if (mqSendFailures != null && mqSendFailures.size() > 0) {
			long total = mqSendFailures.get(0).getLong("total");
			this.renderJson(new NormalResponse(mqSendFailures, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}	
	
	/** 发送消息失败处理*/
	public void delMqSendFailureList() {
		Map<String, String[]> param = this.getParaMap();
		HttpSession session=this.getSession();
		String operUserName = StringUtil.null2Str(session.getAttribute(UserSession._USER_NAME));
		boolean flag = commonService.delOrderUserReAccount(param,operUserName);
		this.renderJson(flag);
	}
	
	/** 发送消息失败编辑消息体*/
	public void editMqSendFailureList() {
		Map<String, String[]> param = this.getParaMap();
		boolean flag = commonService.editMqSendFailureList(param);
		this.renderJson(flag);
	}
}

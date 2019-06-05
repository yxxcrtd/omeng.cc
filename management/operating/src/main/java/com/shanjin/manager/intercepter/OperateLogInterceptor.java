package com.shanjin.manager.intercepter;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;



import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.core.Controller;
import com.shanjin.manager.Bean.SystemResource;
import com.shanjin.manager.Bean.UserSession;
import com.shanjin.manager.constant.Constant;
import com.shanjin.manager.service.ISystemOperateLogService;
import com.shanjin.manager.service.impl.SystemOperateLogServiceImpl;
import com.shanjin.manager.utils.ManagerUtil;
import com.shanjin.manager.utils.StringUtil;

/**
 * 后台用户操作日志记录
 * @author Huang yulai
 *
 */
public class OperateLogInterceptor implements Interceptor{
	private ISystemOperateLogService systemOperateLogService = new SystemOperateLogServiceImpl();
	public void intercept(ActionInvocation ai) {
		Controller controller = ai.getController(); 
		HttpServletRequest request = controller.getRequest();
		HttpSession session = request.getSession();
		String userName =StringUtil.null2Str(session.getAttribute(UserSession._USER_NAME)) ;
		String uri = request.getRequestURI();
		String ip = ManagerUtil.getIpAddr(request);
		// 登录，登出
		if(uri.equals("/systemManager/login")){
			Map<String, String> param = new HashMap<String,String>();
			param.put("source_name", "【用户登录】");
			param.put("source_url", uri);
			param.put("operate_type", "登录");
			param.put("operate_user", userName);
			param.put("operate_ip", ip);
			systemOperateLogService.addOperateLog(param);	
		}else if(uri.equals("/systemManager/logout")){
			Map<String, String> param = new HashMap<String,String>();
			param.put("source_name", "【用户登出】");
			param.put("source_url", uri);
			param.put("operate_type", "登出");
			param.put("operate_user", userName);
			param.put("operate_ip", ip);
			systemOperateLogService.addOperateLog(param);	
		}else{
			SystemResource sr = (SystemResource)Constant.sysResource.get(uri);
			if(sr!=null){
				// 筛选操作，只对某些（2：新增，3：更新，4：删除，5：上传，6：下载，7：导出）操作记录日志
				int operType = StringUtil.nullToInteger(sr.getLong("type"));
				if(operType>1){
					Map<String, String> param = new HashMap<String,String>();
					param.put("source_name", sr.getStr("resName"));
					param.put("source_url", uri);
					param.put("operate_type", geOperateName(operType));
					param.put("operate_user", userName);
					param.put("operate_ip", ip);
					systemOperateLogService.addOperateLog(param);
				}
			}
		}
	    ai.invoke();
	}
	
	
	
	private String geOperateName(int operType){
		String operName = "未知";
		switch(operType){
		   case 2 : operName="新增"; break;
		   case 3 : operName="更新"; break;
		   case 4 : operName="删除"; break;
		   case 5 : operName="上传"; break;
		   case 6 : operName="下载"; break;
		   case 7 : operName="导出"; break;
		   case 8 : operName="导入"; break;
		   default : operName = "未知";
		}
		return operName ;
	}
 
}

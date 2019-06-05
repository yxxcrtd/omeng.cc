package com.shanjin.sso.bean;

import java.io.Serializable;

/**
 * 用户session相关
 * @author Huang yulai
 *
 */
public class UserSession implements Serializable{
	/**  session 资源权限名称定义*/
	public static final String _USER_RESOURCES = "_user_resource";
	/**  session 用户账号名称定义*/
	public static final String _USER_NAME = "_user_name";
	/**  session 用户id定义*/
	public static final String _USER_ID = "_user_id";
	/**  session 用户信息定义*/
	public static final String _USER = "_user";
	/**  session 用户类型*/
	public static final String _USER_TYPE = "_user_type";
	/**  session 项目代理商代理的app信息定义*/
	public static final String _USER_APP = "_user_app";
	/**  session 是否登陆标识定义*/
	public static final String _USER_IS_LOGIN = "_user_is_login";
//	/**  session 资源权限名称定义*/
//	public static final String _USER_RESOURCES = "_user_resource";
//	/**  session 资源权限名称定义*/
//	public static final String _USER_RESOURCES = "_user_resource";
	
}

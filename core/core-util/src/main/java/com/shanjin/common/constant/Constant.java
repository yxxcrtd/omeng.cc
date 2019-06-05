package com.shanjin.common.constant;

import com.shanjin.outServices.aliOss.AliOssUtil;

public class Constant {

	/** 是否为开发模式 */
	public static final boolean DEVMODE = true;
	// public static final boolean DEVMODE = false;

    /** 压力测试 */
	public static boolean PRESSURETEST=false;

    private static final boolean TRUE = true;

    /** MQ消息加密的key */
    public static final String DYNAMIC_KEY = "367937E1967092280C56077755E4C65B";

    /** 是否将消息发送给 Wallet */
    public static final boolean IS_SEND_MQ_TO_WALLET = true;

    /** 是否将消息发送给 C_PLAN */
    public static final boolean IS_SEND_MQ_TO_C_PLAN = true;

    /** 是否完成现金发MQ消息 */
    public static final boolean IS_SEND_MQ_TO_CASH = true;

    /** 开通增值服务后给C_PLAN发MQ消息 */
    public static final boolean IS_SEND_MQ_TO_C_PLAN_WITH_OPEN_INCREASE_SERVICE = true;

    /** 是否给购买服务包含私人助理的商户发MQ消息 */
    public static final boolean IS_SEND_MQ_TO_C_PLAN_WITH_INCLUDE_CONSULTANT = true;

    /** 订单评价后给C_PLAN发MQ消息 */
    public static final boolean IS_SEND_MQ_TO_C_PLAN_AFTER_ORDER_COMMENT = true;

    /** 订单确认支付成功后是否给 C_PLAN 发消息 */
    public static final boolean IS_SEND_MQ_TO_C_PLAN_AFTER_ORDER_PAYMENT_SUCCESS = TRUE;

	// 是否使用阿里OSS服务 true 使用， false 不使用
	public static boolean USE_ALIOSS = true;

	static {
		AliOssUtil.setModel(DEVMODE);
	}

	/** 是否启用 kafka 日志 **/
	public static boolean KAFKA_LOG = false;

	/**
	 * 是否启用KAFAK消息平台采用异步处理优化
	 */
	public static boolean ASYNC_PROCESS = false;

	/**
	 * 是否用KAFKA 捕获异常
	 */
	public static boolean KAFKA_CAPTURE_EXCEPTION = false;


	public static final String TESTPHONE = "1400000";

	/** 性别 0：保密 */
	public static final int SECRECY = 0;

	/** 性别 1：男 */
	public static final int MALE = 1;

	/** 性别 2：女 */
	public static final int FEMALE = 2;

	/** 是否被删除 0：未删除 */
	public static final int NOT_DELETED = 0;

	/** 是否被删除 1：已删除 */
	public static final int DELETED = 1;

	/** 地址类型 0：家地址 */
	public static final int HOME = 0;

	/** 地址类型 1：公司地址 */
	public static final int COMPANY = 1;

	/** 地址类型 2：常用地址 */
	public static final int IN_COMMON_USE = 2;

	/** 附件类型 1：图片 */
	public static final int IMAGE = 1;

	/** 附件类型 1：图片 */
	public static final int USER_AVATER = 11;

	/** 附件类型 1：图片 */
	public static final int BUSINESS_IMAGE = 11;

	/** 附件类型 2：图片 */
	public static final int VOICE = 2;

	/** 附件类型 1：图片 */
	public static final int BUSINESS_VOICE = 21;

	/** 员工类型 1：老板 */
	public static final int BOSS = 1;

	/** 员工类型 2：普通员工 */
	public static final int GENERAL_STAFF = 2;

	/** 员工类型 3：财务 */
	public static final int FINANCIAL_STAFF = 3;

	/** 订单类型 0：全部 */
	public static final int ORDER_ALL = 0;

	/** 订单状态 1：预约成功 */
	public static final int BOOKING_SUCCESS = 1;

	/** 订单状态 2：选择服务商 */
	public static final int CHOICE_FACILITATOR = 2;

	/** 订单状态 3：确认服务商 */
	public static final int AFFIRM_FACILITATOR = 3;

	/** 订单状态 4：订单完成 */
	public static final int ORDER_COMPLETE = 4;

	/** 订单状态 5：支付完成 */
	public static final int PAY_COMPLETE = 5;

	/** 订单状态 6：过期订单 */
	public static final int OVERDUE_ORDER = 6;
	
	/** 订单状态9： 无报价方案的订单过期   商户端专用 **/
	public static final int NO_PLAN_ORDER = 9;

	/** 订单状态 7：无效订单 */
	public static final int INVALID_ORDER = 7;

	/** 支付方式 1：支付宝支付 */
	public static final int ALIPAY = 1;

	/** 支付方式 2：订单完成 */
	public static final int TENPAY = 2;

	/** 支付方式 3：现金支付 */
	public static final int CASHPAY = 3;

	/** 提现状态 0：失败 */
	public static final int FAILURE = 0;

	/** 提现状态 1：成功 */
	public static final int SUCCEED = 1;

	/** 提现状态 2：提取中 */
	public static final int EXTRACTING = 2;

	/** 逗号（英文半角） */
	public static final String COMMA_EN = ",";

	/** 空字符串 */
	public static final String EMPTY = "";

	/** 半角空格 */
	public static final String BLANK = " ";

	/** 文件分隔符 */
	public static final String FILE_EPARATOR = "/";

	/** 默认用户头像 */
	public static final String DEFAULT_USER_PORTRAIT_PTAH = "/resource/userInfo/image/default_user_portrait.png";

	/** 默认商户头像 */
	public static final String DEFAULT_MERCHANT_PORTRAIT_PTAH = "/resource/merchantInfo/image/default_merchant_portrait.png";

	/** 商户端 图片文件上传根路径 */
	public static final String MERCHANT_IMAGE_UPLOAD_BASE_PTAH = "/upload/merchantInfo/image/";

	/** 商户端 vip背景文件上传根路径 */
	public static final String MERCHANT_VIP_BG_UPLOAD_BASE_PTAH = "/manFile/image/";

	/** 商户端 图片文件上传根路径 */
	public static final String USER_IMAGE_UPLOAD_BASE_PTAH = "/upload/userInfo/image/";

	/** 商户端 声音文件上传根路径 */
	public static final String MERCHANT_VOICE_UPLOAD_BASE_PTAH = "/upload/merchantInfo/voice/";

	/** 多记录分页场合,需要显示的每页记录数 10 */
	/** 修改为12条记录 --2015年12月17日 */
	public static final int PAGESIZE = 12;

	/** 商户订单状态字典表类型 */
	public static final String MERCHANT_ORDER_STATUS_DICT_TYPE = "merchantOrderStatus";

	/** 推送设置 */
	public static final class PUSH_CONFIG {
		/** O盟 APPTYPE */
		public static final String APP_TYPE_OMENG = "omeng";

		/** O盟商户版 APPTYPE */
		public static final String APP_TYPE_OMENGP = "omengp";

	}

	/** 默认TOKEN名称 */
	public static final String DEFAULT_TOKEN_NAME = "data_api.token";

	/** 默认TOKEN有效期 */
	public static final int DEFAULT_TOKEN_TIME = 86400;

	/** 业务类型，用户版，商户版 */
	public static final String ACTION_YHB = "yhb";
	public static final String ACTION_SHB = "shb";

	/**
	 * Nginx跳转图片路径
	 */
	public static final String NGINX_PATH = "http://192.168.1.48:81";
	// public static final String NGINX_PATH = "http://120.55.119.232";
	// public static final String NGINX_PATH = "http://img.oomeng.cn";
	public static final String RESOURCE_ROOT_PATH = "/resource";
	public static final String IMAGE_STYLE_UNSELECTED = "/unselected";
	public static final String IMAGE_STYLE_SELECTED = "/selected";

	// public static final String NGINX_PATH = "http://120.26.55.135:81";

	// ********************客户端类型定义（1：安卓；2：ios）*******************
	public static final int PUSH_CLIENT_TYPE_ANDROID = 1; //
	public static final int PUSH_CLIENT_TYPE_IOS = 2;
	// ********************IOS推送证书参数定义*******************
	public static final String IOS_PUSH_KEYSTORE = "keystore";
	public static final String IOS_PUSH_PASSWORD = "password";
	public static final String IOS_PUSH_PRODUCTION = "production";
	public static final String IOS_PUSH_THREADS = "threads";

	public static final String USER = "user";
	public static final String MERCHANT = "merchant";

	public static final String IOS_CERT_TYPE = "iosCertType";
	public static final String IOS_USER_CERT = "iosUserCert";
	public static final String IOS_MERCHANT_CERT = "iosMerchantCert";

	// ios用户端推送证书
	public static final class CertOfUser {
		public static final String keystore = "/usr/local/omengUser.p12";
		public static final String password = "123456";
		public static final boolean production = false;
		public static final int threads = 30;
	}

	// ios商户端推送证书
	public static final class CertOfMerchant {
		public static final String keystore = "/usr/local/omengMerchant.p12";
		public static final String password = "123456";
		public static final boolean production = false;
		public static final int threads = 30;
	}

	public static final String WEB_PROXY_URL = "http://webProxy:8080";
	public static final String SEARCH_URL = "http://searchProxy:9200";

	public static final int VALUEADDED_VIP = 1;
	public static final int VALUEADDED_PUSH = 2;
	public static final int VALUEADDED_ADVISER = 3;
	public static final String OMENG_INCOME_USERNAME = "omengzongbu"; // 公司指定账号（用于计算收益的账号）
	
	
	// ********************支付回调地址*******************
	 //支付回调地址ip(本地)
//    public static final String PAY_BASE_URL_TEST = "http://60.173.220.146:8090/";
	//支付回调地址ip(开发)
	//public static final String PAY_BASE_URL_TEST = "http://60.173.220.146:8086/";
    //支付回调地址ip(测试)
    public static final String PAY_BASE_URL_TEST = "http://60.173.220.146:8088/";

	public static final String PAY_H5_BASE_URL_TEST = "http://wxtest.omeng.cc/";//测试环境h5微信支付回调地址ip
    //支付回调地址ip(线上)
    public static final String PAY_BASE_URL = "http://api.oomeng.cn/";
    
    public static final String PAY_ALI_NOTIFY_URL_TEST = PAY_BASE_URL_TEST + "alipay/alipay.jsp";//支付宝回调地址
    public static final String PAY_WX_NOTIFY_URL_TEST = PAY_BASE_URL_TEST + "wechat/weChat.jsp";//微信回调地址
    public static final String PAY_UNION_NOTIFY_URL_TEST = PAY_BASE_URL_TEST + "backRcvResponse";//银联测试回调地址
    public static final String PAY_WX_H5_NOTIFY_URL_TEST = PAY_H5_BASE_URL_TEST+"wechat/weChath5.jsp";//h5微信回调地址
    public static final String PAY_WX_KING_H5_NOTIFY_URL_TEST = PAY_H5_BASE_URL_TEST+"wechat/weChatKingh5.jsp";//h5王牌计划微信回调地址

    
    
    public static final String PAY_ALI_NOTIFY_URL = PAY_BASE_URL + "alipay/alipay.jsp";//支付宝回调地址
    public static final String PAY_WX_NOTIFY_URL = PAY_BASE_URL + "wechat/weChat.jsp";//微信回调地址
    public static final String PAY_UNION_NOTIFY_URL = PAY_BASE_URL + "backRcvResponse";//银联回调地址
    public static final String PAY_WX_H5_NOTIFY_URL = PAY_BASE_URL+"wechat/weChath5.jsp";//h5微信回调地址
    public static final String PAY_WX_KING_H5_NOTIFY_URL = PAY_BASE_URL+"wechat/weChatKingh5.jsp";//h5微信回调地址

    /** 阳光车险的开关 */
    public static final boolean IS_SUNSHINE_AUTO_INSURANCE = false;

}

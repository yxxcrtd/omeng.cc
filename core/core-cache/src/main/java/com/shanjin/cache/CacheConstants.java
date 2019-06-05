package com.shanjin.cache;

public class CacheConstants {

	/*********************** 定义缓存关键字 *********************/

	public static String JOIN = "_"; // 用于关键字连接符号

	public static String AREA_LIST_KEY = "area_list_key";// 地区列表key

	public static String APP_LIST_KEY = "app_list_key";// app列表key
	
	public static String SEARCH_STATICTIS_KEY = "search_statictis_key";// 搜索分词key

	public static String SERVICE_TYPE_LIST_KEY = "service_type_list_key";// 服务类型列表key

	public static String SLIDER_LIST_KEY = "slider_list_key";// 轮播图列表key

	public static String DICT_LIST_KEY = "dict_list_key";// 数据字典列表key

	public static String CONFIG_KEY = "configuration_info";// 配置项

	public static String PUSH_CONFIG_KEY = "push_configuration_info";// 配置项

	public static String HZ_DICT_LIST_KEY = "hz_dict_list_key";// 数据字典列表key

	public static final String MULTISTAGE_DICT_LIST_KEY = "multistage_dict_list_key";// 数据字典列表key

	public static String DICT_MAP_KEY = "dict_map_key";// 数据字典Map

	public static String USER_CAR_BRAND_MODEL_KEY = "user_car_brand_model";// 车辆信息列表key
	
	public static String DICT_FORM_KEY = "dict_form";// 表单通用key

	public static String PROVINCE_CITY_LIST_KEY = "province_city_list";// 地区列表key一次性查出所有地区信息

	public static String PROVINCE_CITY_LIST_KEY_2 = "province_city_list_2";// 地区列表key一次性查出所有地区信息

	public static int USER_PRE_VERIFY_TIMEOUT = 30 * 60; // 验证码三十分钟后超时

	public static String USER_PRE_VERIFY_KEY = "userPreVerify:"; // 用户预验证信息

	public static String PHONE_USER_KEY = "phoneUser"; // 电话号码--用户对应

	public static int TOP_USER_NUMBER = 10 * 10000; // top user 入口数量

	public static String TOP_USER_KEY = "topUser"; // top xxxx 用户入口。

	public static String TOP_USER_INFO_KEY = "userInfo:"; // 用户基本信息入口

	public static int ORDER_PER_USER_SIZE = 1000; // 每用户缓存的订单数量

	public static String ORDER_PER_USER_ENTRY_KEY = "orderPerUserEntry:"; // 每用户TOP
																			// 用户订单入口
	public static String SERVICE_TYPE_APPTYPE = "service_type_apptype:"; // 每用户TOP

	public static String ORDER_USER_KEY = "userOrder:"; // 用户的订单列表

    /** 订单状态和商户订单状态 */
    public static String USER_ORDER_STATUS_KEY = "userOrderStatus:";

    /** 设置订单状态和商户订单状态的过期时间（默认30分钟） */
    public static int USER_ORDER_STATUS_KEY_EXPIRE = 30 * 60;

	public static String ORDER_TO_USER = "orderToUser:"; // 订单对用户的对应关系

	public static String ORDER_VERSION_KEY = "order_cache_version"; // 用户订单列表缓存及版本对应关系

	public static String LOCK_ORDER_USER_PREFIX = "lock_user_order:"; // 用户订单锁

	public static String LOCK_USER_CACHE = "lock_user_cache"; // 用户缓存定时调度锁。

	public static String LOCK_CREATE_INDEX_CACHE = "lock_create_index_cache"; // 创建索引定时调度锁。

	public static String LOCK_ORDER_NO_BID_CLEAN = "lock_order_no_bid_expire"; // 过期无报价方案的订单调度锁
	
	public static String LOCK_PURIFY_NO_BID = "lock_purify_no_bid_expire"; // 清理过期无报价方案的推送记录及商户缓存调度锁
	
	public static String LOCK_ORDER_NO_CHOOSED_CLEAN = "lock_order_no_choosed_expire"; // 过期未选择报价方案调度订单锁
	
	public static String LOCK_EVALUATION_ORDER = "lock_evaluation_order_expire"; // 好评奖励调度订单锁
	
	public static String LOCK_PURIFY_NO_CHOOSED = "lock_purify_no_choosed_expire"; // 清理过期未选择报价方案的推送记录及商户缓存调度锁
	
	public static String LOCK_PURIFY_CANCEL_ORDER = "lock_purify_cancel_order"; // 清理主动取消订单的推送记录及商户缓存调度锁
	
	public static String LOCK_PURIFY_INPROCESS_ORDER = "lock_purify_inprocess_order"; // 清理进行中的订单的推送记录及商户缓存调度锁
	
	public static String LOCK_RETURN_BIDFEE_ORDER = "lock_return_bidfee_order"; // 过期报价方案换回抢单金处理锁

	public static String LOCK_DEAL_UN_SENT_MQ = "lock_deal_un_sent_mq"; // 处理未发送的MQ锁

	public static String ALL = "all";// 全量数据

	public static int MERCHANT_BASIC_INFO_TIMEOUT = 24 * 60 * 60; // 商户信息1天后超时

	public static String MERCHANT_BASIC_INFO = "merchant_basic_info";// 商户基本信息key
		
	public static String MERCHANT_OUTLINE="merchant_outline:";  //商户概要信息。 主要存放商户详情页的信息。
	
	public static int MERCHANT_OUTLINE_TIMEOUT = 24*60*60;   //商户概要信息，一天。
	
	public static String LOCK_MERCHANT_OUTLINE_PREFIX = "lock_merchant_outline:"; // 商户概要信息锁
	
	public static String MERCHANT_FANS="merchant_fans";  //商户粉丝数
	
	public static int MERCHANT_FANS_TIMEOUT = 24*60*60;  //商户粉丝数超时一天
	
	public static String MERCHANT_ESTIMATE="merchant_estimate";  //商户评价
	
	public static int MERCHANT_ESTIMATE_TIMEOUT=24*60*60;  //商户评价超时，一天
	
	public static String MERCHANT_SERVICES_LIST="merchant_services_list";  //商户服务
	
	public static int MERCHANT_SERVICES_TIMEOUT=24*60*60;  //商户服务，一天

	public static String MERCHANT_VIP_BG = "vip_background_url_list"; // VIP背景模板
	
	
//	public static String MERCHANT_BASIC_INFO_V23 = "merchant_basic_info_v23";// 商户基本信息key（V2.3版本）
	
//	public static String MERCHANT_BASIC_INFO_V24 = "merchant_basic_info_v24";// 商户基本信息key（V2.4版本）

//	public static String MERCHANT_BASIC_INFO_FOR_USER = "merchant_basic_info_for_user";// 商户基本(用户查看商户)信息key
	
//	public static String MERCHANT_BASIC_INFO_FOR_USER_V24 = "merchant_basic_info_for_user_v24";// 商户基本(用户查看商户)信息key（V2.4版本）

	public static String UPDATE_CLIENT_VERSION = "update_client_version";// 客户端版本更新key

	public static String LOADING = "loading";// 客户端启动页key

	public static int MERCHANT_PRE_VERIFY_TIMEOUT = 30 * 60; // 验证码三十分钟后超时

	public static String VALIDATE_VERIFICATION_CODE_KEY = "validateVerificationCode"; // 验证验证码

	public static String MERCHANT_PRE_VERIFY = "merchantPreVerify"; // 商户预验证信息

	public static String MERCHANT_USER_KEY = "phoneMerchant"; // 电话号码--商户对应

	public static int MERCHANT_INFO_FOR_LOGIN_TIMEOUT = 24 * 60 * 60; // 商户信息（登陆时使用的信息）1天后超时

	public static String MERCHANT_INFO_FOR_LOGIN = "merchantInfoForLogin"; // 商户信息（登陆时使用的信息）

	public static int MERCHANT_ADD_EMPLOYEE_TIMEOUT = 30 * 60; // 商户添加的员工信息(验证码)三十分钟后超时

	public static String MERCHANT_ADD_EMPLOYEE = "merchantAddEmployeeKey"; // 商户添加的员工信息

	public static final int VOUCHERS_TIMEOUT = 30 * 60;// 代金券超时时间

	public static final String MERCHANT_VOUCHERSINFO = "merchant_vouchersInfo";// 商户当前代金券

	public static final String MERCHANT_HISTORY_VOUCHERSINFO = "merchant_history_vouchersInfo";// 商户历史代金券

	public static final String IP_CITY_TREE_MAP = "ip_city_tree_map";// ip城市缓存key

	/**
	 * 银行卡绑定时手机验证码缓存
	 */
	public static final String VERIFICATIONCODE_CACHE = "verificationcode_cache";
	/**
	 * 银行卡绑定时手机验证码缓存时间 10分钟
	 */
	public static final int VERIFICATIONCODE_CACHE_TIMEOUT = 60 * 10;

	/**
	 * 推送设置的MAP
	 * */
	public static final String PUSH_MAP = "push_map";

	public static final String IP_TO_LOCATOIN = "ip_location:"; // 存放ip到地址 的
																// hashmap

	public static final String IP_RECENT_ACCESS = "recent_ip:"; // 存放最近访问的IP 地址
																// zset

	public static final int IP_MAX_ENTRY = 20 * 10000; // 最大缓存的的独立IP数量

	public static final String LOCK_IP_CACHE = "lock_ip_cache"; // IP-城市缓存锁

	public static String VALUE_ADD_SERVICE = "valueAddService"; // 增值服务列表

	public static String CHECK_DUPLICATE_SUBMISSION = "CheckDuplicateSubmission"; // 缓存避免重复提交

	public static int CHECK_DUPLICATE_SUBMISSION_TIME_OUT = 1; // 缓存避免重复提交失效时间

	public static String AMS_SERVICE_VERSION_CACHE_KEY = "ams_service_cache_version"; // 秘书服务缓存版本

	/**
	 * 图片缓存定义类
	 */
	public static final class IMAGE_CACHE {

		/** 下面定义业务类型 **/

		public static final String ORDER_ICON = "orderIcon"; // 服务类型附件 订单图标

		public static final String SHOW_ICON = "showIcon"; // 服务类型附件 显示图标

		public static final String VERSION = "Version";

		public static final String ORDER_ICON_VERSION = "orderIconVersion"; // 服务类型附件
																			// 订单图标版本号

		public static final String SHOW_ICON_VERSION = "showIconVersion"; // 服务类型附件
																			// 显示图标版本号

	}

	public static String SERVICE_TYPE_PARAS = "serviceTypeParas"; // 秘书服务缓存版本
	
	public static String PLAN_FORM_VERSION = "planFormVersion"; // 报价方案表单版本
	
	public static String PLAN_FORM = "planForm"; // 报价方案表单
	
	public static String ORDER_FORM_VERSION = "orderFormVersion"; // 订单表单版本
	
	public static String ORDER_FORM = "orderForm"; // 订单表单
	
	public static String ORDER_BANNER = "orderBanner"; // 订单消费引导
	
	public static String ORDER_SERVICE_NICK = "orderServiceNick"; // 订单服务别名
	
	
	public static String USER_HOME_PAGE_BANNER="USER_HOMEPAGE_BANNER"; //用户首页图片轮播
	
	public static String MERCHANT_HOME_PAGE_BANNER="MERCHANT_HOMEPAGE_BANNER"; //商户开始接单--图片轮播
	
	
	public static int USER_HOME_PAGE_BANNER_EXPIRTIME =10*60; //用户首页图片轮播缓存有效期
	
	
	public static int MERCHANT_HOME_PAGE_BANNER_EXPIRTIME =10*60; //商户首页图片轮播缓存有效期
	
	
	public static String USER_HOME_PAGE_RECOMMEND="USER_HOMEPAGE_RECOMMEND"; //用户首页推荐
	
	
	public static int USER_HOME_PAGE_RECOMMEND_EXPIRTIME =10*60; //用户首页推荐缓存有效期
	
	
	public static String USER_HOME_PAGE_NEARBY="USER_HOMEPAGE_NEARBY"; //用户首页附近商家
	
	
	public static int USER_HOME_PAGE_NEARBY_EXPIRTIME =10*60; //用户首页附近商家缓存有效期
	
	
	public static String USER_HOME_HOTSEARCH_ALL_HOT="USER_HOMEPAGE_HOTSEARCH_HOT"; //用户首页热门搜索，热门
	
	
	public static String USER_HOME_HOTSEARCH_ALL_TRADE="USER_HOMEPAGE_HOTSEARCH_TRADE"; //用户首页热门搜索，行业
	
	
	public static String USER_HOME_HOTSEARCH_ALL_CUSTOM="USER_HOMEPAGE_HOTSEARCH_CUSTOM"; //用户首页热门搜索，个性服务
	
	
	public static String USER_HOME_HOTSEARCH__SERVICES_UNDER_CATALOG="USER_HOMEPAGE_HOTSEARCH_SERVICES_UNDER_CATALOG"; //用户首页热门搜索，个性服务分类下所有服务项目
	
	
	public static String USER_HOME_HOTSEARCH_ALL_EXTERNAL="USER_HOMEPAGE_HOTSEARCH_EXTERNAL"; //用户首页热门搜索，其它
	
	
	public static String MERCHANT_TRADE_TAG="MERCHANT_TRADE_TAG"; //行业标签
	
	
	public static String MERCHANT_CUSTOM_TAG="MERCHANT_CUSTOM_TAG"; //自定义标签
	
	
	public static String CATALOG="CATALOG"; //分类
	
	
	public static String MERCHANT_CATALOG_TOP="MERCHANT_CATALOG_TOP"; //商户行业一级分类
	
	public static String CATALOG_FETCHTIME ="CATALOG_FETCHTIME"; //分类查询时间
	
	public static int CATALOG_EXPIRTIME =10*60; //分类超时时间
	
	public static String WEEK_STAR = "weekstar"; // 每周之星缓存版本
	
	public static int  WEEK_STAR_EXPIRTIME = 30*60;  //目录树及服务超时时间
	
	/**  commonService.getCatalogTreeAndService(catalogId)   使用   */
	
	public static final String CATALOG_AND_SERVICES="CATALOG_AND_SERVICES";  //目录树及服务
	
	/**  myMerchantervice.selectMerchantServiceForChoose(merchantId, merchantType, appType)   使用   */
	public static final String CATALOG_AND_SERVICES_FOR_MERCHANT="CATALOG_AND_SERVICES_FOR_MERCHANT";  //目录树及服务（商户侧用）
	
	public static int  CATALOG_AND_SERVICES_EXPIRTIME = 10*60;  //目录树及服务超时时间
	/**服务类型**/
	public  static final String CACHE_ALLSERVICETYPE="allServiceType";
	
	public static String MERCHANT_TOTAL = "MERCHANT_TOTAL"; // 商户总数

	public static int MERCHANT_TOTAL_EXPIRTIME = 12*60*60; // 商户总数超时时间
	
    public static String TIME_SAMP_KEY = "time_samp_key";// 时间戳key
    
    public static String URL_KEY = "url_key";// urlkey

    public static String JSP_TICKET_KEY = "jsp_ticket_key";// jspTicketkey
    
    public static String SHARE_HTML = "share_html";// 分享通用接口
    
    public static String STATIC_HTML = "static_html";// 静态页活动界面
    
    public static String INDEX_LIST = "index_list";// 首页活动
    
    public static String INDEX_LIST_1 = "index_list_1";// 首页活动
    
    public static String INDEX_LIST_2 = "index_list_2";// 首页活动
    
    public static String INDEX_LIST_3 = "index_list_3";// 首页活动
    
    public static String INDEX_LIST_4 = "index_list_4";// 首页活动
    
    public static String INDEX_LIST_5 = "index_list_5";// 首页活动
    
    public static String MERCHANT_INDEX = "merchant_index";// 商户主页
    
    public static String STRATEGY_INDEX = "strategy_index";// 经营攻略主页
    
    public static String ORDER_DETAIL = "order_detail";// 订单详情页
    
    /**活动的标签**/
	public  static final String LABEL_LIST="label_list";
	
	public  static final String WEIXIN_USER="weixin_user";//微信用户
	
	public  static final String MERCHANTINFO_CUTT="merchant_info_cutt";//商户剪彩信息
	
	public  static final String MERCHANTINFO_ADVER="merchant_info_advertise";//商户宣传信息
	
	public  static final String OPEN_CITY="open_city";//开放城市
	
	public  static final String FENSI_RANKING="fensi_ranking";//开放城市
	
	public  static final String ACTIVITY_SHARE="activity_share";//活动分享内容

	//行业对于所有原子性服务项目
	public static final String CATALOG_SERVICE_TYPE_LIST="catalog_service_type_list";
	//行业对于所有原子性服务项目
	public static final String ORDER_CATALOG_SERVICE_TYPE_LIST="ordre_catalog_service_type_list";
	/**两次获取验证码时间间隔**/
	public static final String VERIFICATIONCODE_OUTTIME_SMS="VERIFICATIONCODE_OUTTIME_SMS";
	public static final String VERIFICATIONCODE_OUTTIME_VOICE="VERIFICATIONCODE_OUTTIME_VOICE";
	public static final String VERIFICATIONCODE_OUTTIME="VERIFICATIONCODE_OUTTIME";
	public static final String VERIFICATIONCODE_OUTTIME_PHONE="VERIFICATIONCODE_OUTTIME_PHONE";
	public static final String VERIFICATIONCODE_OUTTIME_DEVICE="VERIFICATIONCODE_OUTTIME_DEVICE";
	public static final String VERIFICATIONCODE_OUTTIME_IP="VERIFICATIONCODE_OUTTIME_IP";
	
	
	/** 限制更新的URL **/
	public static final String RESTRICT_UPDATE_URL="RESTRICT_UPDATE_URL";
	
	public static final String CONPUTE_ORDER_PAST_TIME="conpute_order_past_time";
	
	public static final String ORDER_PAST_TIME="order_past_time";
	
	
	/** 过期配置的天数 **/
	public static final String ORDER_OVER_TIME="order_overdue_time::";
	
	public static final Integer ORDER_OVER_TIME_EXPIRETIME=24*60*60; //1天过期
	
	/**
	 * 商户侧订单缓存应保留在缓存中的天数
	 * 注意： 哪些数据从MERCHANT_PUSH_ORDER 移动到历史表，天数间隔应保持一致。
	 */
	public static final long KEEP_MERCHANT_PUSH_ORDER_PERIOD=31;
	
	
	/**
	 * 商户侧一月内订单列表 
	 */
	public static final String MERCHANT_PUSH_ORDER_PREFIX="merchant_push_order::";
	
	
	public static final int MERCHANT_PUSH_ORDER_EXPIRETIME=2*24*60*60;  //2天列表过期
	
	
	/**
	 * 商户侧一月内订单id的列表，按时间倒序排放
	 */
	public static final String MERCHANT_PUSH_ORDER_IDS ="merchant_push_order::ids::";
	
	
	/**
	 * 一月以上历史商户推送订单KEY
	 */
	public static final String HISTORY_PUSH_ORDER_PREFIX="history_merchant_push_order::";
	
	
	public static final int HISTORY_MERCHANT_PUSH_ORDER_EXPIRETIME=30*60; //30分钟过期
	
	
	public static String LOCK_PUSH_MERCHANT_ORDER_PREFIX = "lock_merchant_order::"; // 商户订单锁
	
	
	public static String LOCK_CLEAN_PUSH_MERCHANT_ORDER_CACHE = "lock_clean_merchant_order_cache"; // 商户缓存清理定时调度锁。
	
	
	/**
	 * 订单---推送商户对应缓存key
	 */
	public static final String ORDER_MERCHANTIDS_PREFIX="ORDER_MEARCHANTIDS::";
	
	
	public static final int ORDER_MERCHANTIDS_EXPIRETIME=2*24*60*60;  //2天过期
	
	public static final String PUSH_RANGE="push_range";
	
	public static final String RELEVANCE_SERVICE_TYPE_ID="relevance_service_type_id";
	
	public static final String ORDER_REWARD="order_reward:";
	
	public static final String ORDER_REWARD_SURPLUS_MER="order_reward_surplus_mer";
	
	public static final String ORDER_REWARD_SURPLUS="order_reward_surplus";
	
	public static final String ORDER_REWARD_LIST="order_reward_list";

	/**商户交易明细导航**/
	public static final String MERCHANT_PAYMENT_LIST_NAVI="merchant_payment_list_navi";
	public final static int MERCHANT_PAYMENT_LIST_NAVI_EXPIRETIME=1*24*60*60;

    /**
     * 商户认证类型
     */
    public static final String MERCHANT_AUTH_TYPE = "merchant_auth_type_";

    /**
     * 商户认证类型的缓存失效时间，默认3分钟
     */
    public static final int MERCHANT_AUTH_TYPE_EXPIRE = 3 * 60;

    /** 开通城市的Redis包 */
    public static final String SERVICE_CITY = "service_city:";

    /** 是否重复抢单的Redis包 */
    public static final String MERCHANT_PLAN = "merchant_plan:";

    /** 用户黑名单的缓存Key */
    public static final String USER_BLACKLIST = "user_blacklist";
    
    /** 订单状态描述缓存 */
    public static final String ORDER_STATUS_DESCRIBE="order_status_describe";

        
    /** 新订单显示TOP 推送商家的标志  **/
    public static final String ORDER_SHOW_TOP_PUSHMERCHANT="order_show_top_pushmerchant";

    /** 用户是否注册 */
    public static final String IS_REGISTER="is_register";
    
    /** 用户是否开店 */
    public static final String IS_OPENSHOP="is_openshop";

    /** 是否禁用商店里的体现功能*/
    public static final String OPAY_DISABLE_WITHDRAW="opay_disable_withdraw";
   
    /** 所有的服务类型*/
    public  static final String ACTIVITY_CACHE_ALLSERVICETYPE="allactivityServiceType";

    /**
     * 分城市统计服务商数量缓存KEY 前缀
     */
    public static  final String CITY_MERCHANT_SUMMARY="city_merchant_summary:";
    
    
    public static  final Integer CITY_MERCHANT_SUMMARY_EXPIRETIME=24*3600;

    /**
     * 用户首页需求提示KEY
     */
    public static  final String  USER_HOMEPAGE_REQUIRE_TIP="user_home_page_require_tip";

    
    /**
     * 模拟的用户购买信息存放的KEY
     */
    public static  final String  USER_HOMEPAGE_MOCKUP_BUYINFO="user_home_page_buy_info";
    
    /**
     * 模拟的用户购买信息缓存生效时间
     */
    public static  final Integer  USER_HOMEPAGE_MOCKUP_BUYINFO_EXPIRETIME=24*3600;
    
    
    /**
     * 商户评价统计
     */
    public static final String MERCHANT_EVALUATION_STATIS="merchantListByOrder_Scheduling_datas_M_E_Statis";//merchant_evaluation_statis
    
    /**
     * 用户是否有商店标志
     */
    public static  final String  USER_HOMEPAGE_HAS_SHOP_FLAG="user_home_page_has_shop_flag:";
    
    /**
     * 用户是否有商店标志缓存生效时间
     */
    public static  final Integer  USER_HOMEPAGE_HAS_SHOP_FLAG_EXPIRETIME=600;
    
    public static String VALUEADD_SERVICE_LIST = "valueadd_service_list";// 增值服务列表
    
    public static String PUSH_PRIVATEASSISTANT_LOCK="PUSH_PRIVATEASSISTANT_LOCK";//私人助理定时调度锁
    
    public static String VERIFICATIONCODE_LIST_PHONE="VERIFICATIONCODE_LIST_PHONE";
    
    public static String VERIFICATIONCODE_LIST_DEVICE="VERIFICATIONCODE_LIST_DEVICE";
    
    public static String VERIFICATIONCODE_LIST_IP="VERIFICATIONCODE_LIST_IP";
    
    /** 用户店铺列表缓存 **/
    public static String USER_SHOP_LIST="USER_SHOP_LIST";


	/***活动配置信息前置key**/
	public static final String PREKEY_ACT_CONFIG_INFO="act_config_info";
	
	/***系统帮助反馈key**/
	public static final String SYSTEM_HELP_FEEDBACK="system_help_feedback";

	/*** 活动相关key**/
	public static final String ACTIVITY_INFO = "ACTIVITY_INFO";//活动信息

	public static final String ACT_REWARD_ELEMENT = "ACT_REWARD_ELEMENT";//奖励配置信息

	public static final String ACT_REWARD_ELEMENT_BASIC = "BASIC";//基本信息

	public static final String ACT_REWARD_ELEMENT_CONFIG = "CONFIG";//奖励配置

	public static final String ACT_REWARD_ELEMENT_LIMITCOUNT = "LIMITCOUNT";//剩余数

	public static final String ACT_REWARD_ELEMENT_LOCK = "ACT_REWARD_ELEMENT_LOCK";//活动锁

	public static final String ACT_REWARD_ELEMENT_BUYCOUNT = "BUYCOUNT";//已卖数

	public static final String ACT_REWARD_ELEMENT_TEXT = "TEXT";//文案



	/**微信jsapi临时票据preKey**/
	public static final String WECHAT_JSAPI_TICKET_PREKEY = "wechat_jsapi_ticket:appid:";

	public static final String EXPIRES_IMMEDIATE_DAY = "expires_immediate_day";//立即服务订单逾期天数

	public static final String EXPIRES_NO_IMMEDIATE_DAY = "expires_no_immediate_day";//预约订单逾期天数
	
	
	public static String INVOICE_VALUE_ADD_SERVICE = "invoicevalueAddService"; // 开票申请增值服务列表
	public static final String MAX_INVOICE_NUM_STRING="max_invoice_num";
	
	
	
}

package com.shanjin.manager.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * @author lijie(Restructure)
 *
 * Description	: 常量列表
 * CreateTime	: ?
 * UpdateTime	: 2015-5-14 14:44:06
 */
public class Constant {
	
	public static final String ADMIN = "omengadmin";
	public static final String OMENG_INCOME_USERNAME = "omengzongbu"; //公司指定账号（用于计算收益的账号）
	//1：服务商vip开通；2：订单推送开通；3：顾问号开通
	public static final int VALUEADDED_VIP = 1;
	public static final int VALUEADDED_PUSH = 2;
	public static final int VALUEADDED_ADVISER = 3;
	
	public static final int VALUEADDED_VIP_PRICE = 5800; // 增值服务VIP年费5800元
	public static final int VALUEADDED_ADVISER_PRICE = 1500; //增值服务顾问号单价1500元
	
	public static final String SMS_KEY="807c8e393fa3a9aa889145c3e84c12ea";//短信发送key
	public static final String WEB_PROXY_URL = "http://webProxy:8080";
	
	public static String WEB_SERACH_URL = "http://192.168.1.202:8080/search/"; //搜索业务接口，默认测试接口，正式环境在congfig中配置
	
	public static final String PACKAGE_TYPE_MERCHANT = "1";
	public static final String PACKAGE_TYPE_USER = "2";
	
	public static final String WATER_MARK_PATH = "/mnt/waterMark/";
	public static final String WATER_MARK_IMG = "/mnt/waterMark/waterMark.png";
	
	//行业热门搜索常量
	public static final int HOT_CATALOG_ID=-1; //热门
	
	public static final int HOT_CUSTOM_ID=-2; //个性
		
	public static final int HOT_OTHER_ID=-3; //其他
	
//	public static final String WATER_MARK_PATH = "c:/";
//	public static final String WATER_MARK_IMG = "d:/480.png";
	public static final int EXPOTR_TYPE=1;
    /** AppType */
    public static final String APPTYPE_LXZ="lxz";
    public static final String APPTYPE_CBT="cbt";
    public static final String APPTYPE_YXT="yxt";
    public static final String APPTYPE_SXD="sxd";
    public static final String APPTYPE_ZYB="zyb";
    
    public static final String APPTYPE_FYB="fyb";
    public static final String APPTYPE_JZX="jzx";
    public static final String APPTYPE_HZ="hz";
    public static final String APPTYPE_YCT="yct";
    public static final String APPTYPE_DGF="dgf";
    public static final String APPTYPE_MST="mst";
    public static final String APPTYPE_ZSY="zsy";
    public static final String APPTYPE_QPL="qpl";
    public static final String APPTYPE_XHF="xhf";
    public static final String APPTYPE_YD="yd";
    public static final String APPTYPE_SYP="syp";
    public static final String APPTYPE_TS="ts";
    public static final String APPTYPE_JRJ="jrj";
    public static final String APPTYPE_AMS="ams";
    public static final String APPTYPE_XLB="xlb";
    public static final String APPTYPE_QZY="qzy";
    public static final String APPTYPE_ZYD="zyd";
    public static final String APPTYPE_HYT="hyt";
    public static final String APPTYPE_YDH="ydh";
    public static final String APPTYPE_YP="yp";
    
    public static Map<String,Object> mailMap = new HashMap<String,Object>(); // 邮件配置信息
    
    public static final String EMPTY = "";
    /**
     * 性别
     */
    public static final class SEX {
        /** 性别 0：保�?*/
        public static final int SECRECY = 0;

        /** 性别 1：男 */
        public static final int MALE = 1;

        /** 性别 2：女 */
        public static final int FEMALE = 2;
    }

    /**
     * 是否被删�?
     * 
     */
    public static final class IS_DELETED {
        /** 是否被删�?0：未删除 */
        public static final int NOT_DELETED = 0;

        /** 是否被删�?1：已删除 */
        public static final int DELETED = 1;
    }

    /**
     * 地址类型
     */
    public static final class ADDRESS_TYPE {
        /** 地址类型 0：家地址 */
        public static final int HOME = 0;

        /** 地址类型 1：公司地�?*/
        public static final int COMPANY = 1;

        /** 地址类型 2：常用地�?*/
        public static final int IN_COMMON_USE = 2;
    }

    /**
     * 附件类型
     */
    public static final class ATTACHMENT_TYPE {
        /** 附件类型 1：图�?*/
        public static final int IMAGES = 1;

        /** 附件类型 1：图�?*/
        public static final int USER_AVATER = 11;

        /** 附件类型 1：图�?*/
        public static final int BUSINESS_IMAGES = 11;

        /** 附件类型 2：图�?*/
        public static final int VOICE = 2;

        /** 附件类型 1：图�?*/
        public static final int BUSINESS_VOICE = 21;
    }

    /**
     * 员工类型
     */
    public static final class EMPLOYEE_TYPE {
        /** 员工类型 1：�?�?*/
        public static final int BOSS = 1;

        /** 员工类型 2：普通员�?*/
        public static final int GENERAL_STAFF = 2;

        /** 员工类型 3：财�?*/
        public static final int FINANCIAL_STAFF = 3;
    }

    /**
     * 订单类型
     */
    public static final class ORDER_TYPE {
        /** 订单类型 1：独自旅�?*/
        public static final int TRAVEL_ALONE = 1;
        
        /** 订单类型 2：亲子出�?*/
        public static final int FAMILY_TRIP = 2;
        
        /** 订单类型 3：�?人长�?*/
        public static final int ELDERLY_ELDERS = 3;
        
        /** 订单类型 4：朋友结�?*/
        public static final int FRIENDS = 4;
        
        /** 订单类型 5：蜜月旅�?*/
        public static final int HONEYMOON = 5;
 
    }

    /**
     * 订单状�?
     */
    public static final class ORDER_STATUS {
        /** 订单状�? 1：预约成�?*/
        public static final int BOOKING_SUCCESS = 1;

        /** 订单状�? 2：�?择服务商 */
        public static final int CHOICE_FACILITATOR = 2;

        /** 订单状�? 3：确认服务商 */
        public static final int AFFIRM_FACILITATOR = 3;

        /** 订单状�? 4：订单完�?*/
        public static final int ORDER_COMPLETE = 4;
    }

    /**
     * 支付方式
     */
    public static final class PAY_TYPE {
        /** 支付方式 1：支付宝支付 */
        public static final int ALIPAY = 1;

        /** 支付方式 2：订单完�?*/
        public static final int TENPAY = 2;

        /** 支付方式 3：现金支�?*/
        public static final int CASHPAY = 3;
    }

    /**
     * 代金券类�?
     */
    public static final class CASH_COUPON {
        /** 代金券类�?1：洗�?*/
        public static final int COUPON_WASH_CAR = 1;

        /** 代金券类�?2：维�?*/
        public static final int COUPON_REPAIR = 2;

        /** 代金券类�?3：保�?*/
        public static final int COUPON_UPKEEP = 3;
    }

    /**
     * 服务内容类型
     */
    public static final class SERVICE_TYPE {
        /** 服务内容类型 0：公共服务类 */
        public static final int PUBLIC_SERVICE = 0;

        /** 服务内容类型 1：专属服务类 */
        public static final int EXCLUSIVE_SERVICE = 1;
    }

    /**
     * 列表显示类型
     */
    public static final class LIST_TYPE {
        /** 列表显示类型 0：在填写订单首页显示 */
        public static final int WRITE_ORDER_HOME_PAGE_SHOW = 0;

        /** 列表显示类型 1：在订单服务类型列表项显�?*/
        public static final int ORDER_SERVICE_LIST_SHOW = 1;
    }

    /**
     * 提现状�?
     */
    public static final class WITHDRAW_DEPOSIT_STATUS {
        /** 提现状�? 0：失�?*/
        public static final int FAILURE = 0;

        /** 提现状�? 1：成�?*/
        public static final int SUCCEED = 1;

        /** 提现状�? 2：提取中 */
        public static final int EXTRACTING = 2;
    }

    /**
     * 特殊符号
     */
    public static final class CHARACTER {
        /** 逗号（英文半角） */
        public static final String COMMA_EN = ",";

        /** 空字符串 */
        public static final String EMPTY = "";

        /** 半角空格 */
        public static final String BLANK = " ";
    }

    /**
     * 默认头像
     */
    public static final class DEFAULT_PORTRAIT_PTAH {
        /** 默认用户头像 */
        public static final String USER = "/resource/userInfo/images/default_portrait.png";

        /** 默认商户头像 */
        public static final String MERCHANT = "/resource/merchantInfo/images/default_portrait.png";
    }

    /**
     * 分页
     */
    public static final class PAGE {
        /** 多记录分页场�?�?��显示的每页记录数 10 */
    	public static final int PAGENUMBER = 0;
        public static final int PAGESIZE = 20;
        
        public static final int PAGENUMBER_EXPORT = 0;
        public static final int PAGESIZE_EXPORT = 60000;
        public static final int PAGESIZE_EXPORT_MER = 5000;
        
        public static final int PAGESIZE_EXPORT_TIME = 10000;
    }

    /**
     * 排序
     */
    public static final class SORT {
        /** 排序方式（升序或降序） */
    	public static final String ASC = "ASC";
        public static final String DESC = "DESC";
    }
   
    /**
     * 文件服务器
     */
    public static class HTTP {
        /** 文件服务器地址 */
    	//public static  String port = "http://120.55.119.232";
    	public static  String port = "";
    }
    /**
     * FTP 配置
     */
    public static class FTPConfig {
        /** FTP 地址 */
    	//public static final String ADDR = "192.168.1.48";
    	//public static String ADDR = "ftpserver";
    	public static String ADDR = "";
        /** FTP 端口 */
        //public static int PORT = 21;
        public static int PORT;

        /** FTP 用户名 */
        //public static String USERNAME = "shanjin01";
        public static String USERNAME = "";

        /** FTP 密码 */
        //public static String PASSWORD = "shanjin01";
        public static String PASSWORD = "";

    }
    
    public static String FTP_MODE_OSS = "true";
    //  public static String FTP_MODE_OSS = "true";
    
    /** 是否为开发模式 */
	//public static final boolean DEVMODE = true;
	 public static  boolean DEVMODE = false;
	 
    public static final class ORDER {
        /** 订单详情表*/
        public static final String ORDER_table = "_order_info";
    }
    /**
     * 旅行者订单详情表
     */
    public static final class LXZ {
        /** 订单详情表*/
        public static final String lxz_table = "lxz_order_info";
    }
    /**
     * 易学堂订单详情表
     */
    public static final class YXT {
        /**订单详情 表*/
        public static final String yxt_table = "yxt_order_info";
    }
    /**
     * 车百通订单详情表
     */
    public static final class CBT {
        /** 洗车 */
        public static final String cbt_table_wash = "cbt_wash_service_info";
        /** 维修 */
        public static final String cbt_table_repair = "cbt_repair_service_info";
        /** 保养 */
        public static final String cbt_table_upkeep = "cbt_upkeep_service_info";
        /** 美容 */
        public static final String cbt_table_beautify = "cbt_beautify_service_info";
        /** 停车场 */
        public static final String cbt_table_park= "cbt_park_service_info";
        /** 代办 */
        public static final String cbt_agency_service= "cbt_agency_service_info";
        /**驾校*/
        public static final String cbt_drivingSchool= "cbt_drivingSchool_order_info";
        /** 租车 */
        public static final String cbt_rental= "cbt_rental_service_info";
    }
    /**
     * 上下贷订单详情表
     */
    public static final class SXD {
        /**  */
        public static final String sxd_table = "sxd_loan_info";
      
    }
    /**
     * 左右帮订单详情表
     */
    public static final class ZYB {
        /** 搬家 */
        public static final String zyb_table_remove = "zyb_remove_order_info";
        /** 保洁 */
        public static final String zyb_table_nanny = "zyb_nanny_order_info";
        /** 保姆 */
        public static final String zyb_table_clean = "zyb_clean_order_info";
        /** 维修 */
        public static final String zyb_table_repair = "zyb_repair_order_info";
        /** 洗衣 */
        public static final String zyb_table_wash = "zyb_wash_order_info";
        
        /** 爱宠物 */
        public static final String zyb_table_pet = "zyb_pet_order_info";
        /** 易生活 */
        public static final String zyb_table_liveService = "zyb_liveService_order_info";
        /** 回收 */
        public static final String zyb_table_recycle = "zyb_recycle_order_info";
        
        
        /** 爱宠物 */
        public static final String zyb_acw = "zyb_acw";
        /** 易生活 */
        public static final String zyb_ysh = "zyb_ysh";
        /** 数码修  */
        public static final String zyb_smx = "zyb_smx";
    }
    /**
     * 夜店订单详情表
     */
    public static final class YD {
        /** 会所 */
        public static final String YD_CLUB_ORDER_INFO = "yd_club_order_info";
        /** 洗浴桑拿 */
        public static final String YD_BATH_SAUNA_ORDER_INFO = "yd_bath_sauna_order_info";
        /** 酒吧 */
        public static final String YD_BAR_ORDER_INFO = "yd_bar_order_info";
        /** 夜场表演*/
        public static final String YD_EVEING_SHOW_ORDER_INFO = "yd_evening_show_order_info";
        /** 足疗 */
        public static final String YD_FOOT_MASSAGE_ORDER_INFO = "yd_foot_massage_order_info";
        
        /** KTV */
        public static final String YD_KTV_ORDER_INFO = "yd_ktv_order_info";
        /** 养生SPA */
        public static final String YD_SPA_ORDER_INFO = "yd_spa_order_info";
        
        /** 汗蒸 */
        public static final String YD_KHAN_ORDER_INFO = "yd_xxb_khanSteam_order_info";
        /** 保健按摩 */
        public static final String YD_CHEIR_ORDER_INFO = "yd_xxb_cheirapsis_order_info";
    }
    /**
     * 	喜乐吧订单详情表
     */
    public static final class XLB {
        /** 庆典活动 */
        public static final String XLB_CELE_MODEL = "xlb_cele_model_order_info";
        /** 婚车租赁 */
        public static final String XLB_WEDDING_CAR = "xlb_wedding_car_order_info";
        /** 婚礼策划 */
        public static final String XLB_WEDDING_ORDER = "xlb_wedding_order_info";
        /** 婚纱礼服 */
        public static final String XLB_DRESS_ORDER = "xlb_wedding_dress_order_info";
        /** 婚宴酒店 */
        public static final String XLB_FESST_ORDER = "xlb_wedding_feast_order_info";
        
       
    }
    /**
     * 左右帮服务项目
     */
    public static final class ZYB_SERVICE_ITEM {
       
        /** 保姆 */
        public static final String zyb_nanny_serviceItem = "nannyType";
        public static final String zyb_nanny_service = "3";
        /** 保洁 */
        public static final String zyb_clean_serviceItem = "cleanType";
        public static final String zyb_clean_service = "2";
    }
    
    /**
     * 房源宝订单详情表
     */
    public static final class FYB {
        /** 买房 */
        public static final String fyb_table_buyHouse = "fyb_buyhouse_order_info";
        /** 租房 */
        public static final String fyb_table_rentHouse = "fyb_renthouse_order_info";
        
        public static final String fyb_buyHouse_type = "fybBuyHouseType";
        public static final String fyb_rentHouse_type = "fybRentHouseType";
       
    }
    
    /**
     * 状师爷订单详情表
     */
    public static final class ZSY {
        /** 诉讼 */
        public static final String ZSY_table_LITIGATION = "zsy_litigation_order_info";
        /** 非讼*/
        public static final String ZSY_table_NOLITIGATIO = "zsy_nolitigatio_order_info";
       
    }
    /**
     * 妙手堂订单详情表
     */
    public static final class MST {
        /** 推拿 */
        public static final String mst_table_massage = "mst_massage_order_info";
        /** 理疗*/
        public static final String mst_table_physio = "mst_physio_order_info";
        /** 药浴*/
        public static final String mst_table_medicated = "mst_medicated_order_info";
       
    }
    /**
     * 代理商扣费类型
     */
    public static final class CHARGE {
        /** 充值 */
        public static final String AGENT_RECHARGE = "1";
        /** 扣费 */
        public static final String AGENT_CONSUMER = "2";
       
    }
    /**
     * 系统资源
     */
	public static Map<String,Object> sysResource = new HashMap<String,Object>(); //所有资源
	
	public static List<String> commonResourcePathList = new ArrayList<String>(); //公共资源列表，无权限约束
	
	public static List<String> reportResourcePathList = new ArrayList<String>(); //导出资源列表，拦截器，30秒访问一次
	
	public static Map<String,Object> serviceTypeMap = new HashMap<String,Object>(); //服务类型
	
	public static Map<String,Object> serviceTypeByIdMap = new HashMap<String,Object>(); //新服务类型（根据主键id唯一决定）
	
	public static Map<String,Object> serviceTypeNameMap = new HashMap<String,Object>(); //服务类型名称
	
	public static Map<String,Object> serviceTypeNameByIdMap = new HashMap<String,Object>(); //服务类型名称（根据主键id唯一决定）
	
	public static List<Map<String,Object>> merchantAppInfoMapList = new ArrayList<Map<String,Object>>(); //商户服务项目
	
	public static List<Map<String,Object>> userAppInfoMapList = new  ArrayList<Map<String,Object>>(); //用户服务项目
	
	public static Map<String,Object> merchantAppInfoMap = new HashMap<String,Object>(); //商户服务项目
	
	public static Map<String,Object> userAppInfoMap = new HashMap<String,Object>(); //用户服务项目
	
	public static Map<String,Object> thirdAppInfoMap = new HashMap<String,Object>(); //第三方服务项目
	
	public static Properties config = null; // config 文件
	
	public static Properties databaseConfig = null; // database 文件
}

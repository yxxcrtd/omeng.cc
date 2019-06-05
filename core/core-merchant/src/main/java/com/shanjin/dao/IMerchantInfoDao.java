package com.shanjin.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商户信息表Dao
 */
public interface IMerchantInfoDao {

	/** 注册新商户 */
	int insertMerchantInfo(Map<String, Object> paramMap);

	/** 查询店铺名称和地址 
	Map<String, Object> selectNameAndAddress(Map<String, Object> paramMap); 待删除*/

	/** 查询店铺名称和地址 */
	Map<String, Object> selectNameAndAddressForUser(Map<String, Object> paramMap);

	/** 检查店铺名称是否已被使用 */
	int checkNameSingle(Map<String, Object> paramMap);

	/** 注册时检查店铺名称是否已被使用 */
	int checkNameForReg(Map<String, Object> paramMap);

	/** 更新店铺名称和店铺详情介绍 */
	int updateNameAndDetail(Map<String, Object> paramMap);
	
	/** 更新店铺详情介绍 */
	int updateMerchantDetail(Map<String, Object> paramMap);

	/** 更新店铺的省份和地址 */
	int updateProvinceCity(Map<String, Object> paramMap);

	/** 更新店铺地理位置 */
	int updateLocation(Map<String, Object> paramMap);

	/** 保存提现信息 */
	int updateWithdrawInfo(Map<String, Object> paramMap);

	
	/** 更新VIP推送标志*/
	int updateMerchantVipPushStauts(Long merchantId);

	
	/** 查询商户提现密码 */
	String selectPayPassword(Map<String, Object> paramMap);

	/** 根据经纬度计算用户与商户之间的距离 */
	List<BigDecimal> selectCalcDistance(Map<String, Object> paramMap);

	/** 将商户记录的is_del设置成1 */
	int deleteMerchant(Map<String, Object> paramMap);

	/** 查询商户的最大员工数 */
	Integer selectMaxEmployeeNum(Map<String, Object> paramMap);

	/** 查询店铺基本信息 */
	Map<String, Object> selectMerchantInfo(Map<String, Object> paramMap);

	/** 查询店铺基本信息 
	Map<String, Object> selectMerchantInfoV23(Map<String, Object> paramMap); 待删除 */

	/** 查询店铺基本信息 
	Map<String, Object> selectMerchantInfoV24(Map<String, Object> paramMap); 待删除 */

	/** 查询店铺基本信息 
	Map<String, Object> selectMerchantDetailInfo(Map<String, Object> paramMap); 待删除 */

	/** 查询店铺基本信息FOR用户 */
	Map<String, Object> selectMerchantBasicInfo(Map<String, Object> paramMap);

	/** 根据ID查询商户信息 */
	Map<String, Object> selectMerchantInfoById(Long merchantsId);

	/** 更新微官网URL */
	int updateMicroWebsiteUrl(Map<String, Object> paramMap);

	/** 验证邀请码是否有效 */
	int checkInvitationCode(Map<String, Object> paramMap);

	/** 查找商户消息中心当前最大的消息ID */
	Object getMerchantMaxMsgId(Long merchantId);

	/** 验证商户信息完成度 */
	Map<String, Object> checkMerchantInfo(Map<String, Object> paramMap);
	
	/** 我的店铺列表 */
	List<Map<String, Object>> myMerchantList(Map<String, Object> paramMap);
	
	/** 更新appType */
	int updateAppType(Map<String, Object> paramMap);

	/** 查询店铺名称和地址 */
	String selectCatalogId(Map<String, Object> paramMap);

	/** 根据appType查询catalog表的主键ID，用于老版本商户入驻，新老版本的过渡性方案 */
	String selectCatalogIdFromCatalog(String appType);

	 /**
	  * 获取分类表中所有已审核未删除记录
	  * @return
	  */
	 List<Map<String,Object>> getCataLogs(int parentId);
	 
	 
	 /**
	  * 根据叶子级分类获取对应的服务列表
	  * @param catalogIds
	  * @return
	  */
	 List<Map<String,Object>> getServiceTypeByCatalogs(Map<String,Object> catalogIds);

	/** 获取某个分类的上级节点 */
	List<Map<String,Object>>  getCatalogById(Integer catalogId);

	/** 查询商户总数 */
	int selectMerchantTotal();

	/** 查询商户剪彩次数 */
	Integer selectCuttingNum(Map<String, Object> paramMap);

	/** 查询商户接单计划 */
	Integer selectAlreadySetOrderPlan(Map<String, Object> paramMap);
	
	/** 保存商户已经设置了接单计划*/
	int insertAlreadySetOrderPlanMerchant(Map<String, Object> paramMap);
	
	/** 查询商户被多少用户收藏 */
	int selectCollectionNum(Map<String, Object> paramMap);
	
	/**
	 *  获取商户的收藏列表
	 * @param paramMap
	 * @return
	 */
	List<Map<String,Object>> getCollections(Map<String,Object> paramMap);
	
	/**
	 * 
	 * @param paramMap
	 * @return
	 */
	int checkTransoutMerchantInfo(Map<String,Object> paramMap);
	
	/**
	 * 保存商户转出金额至钱包日志记录
	 * @param paramsMap
	 * @return
	 */
	int saveTransoutLogs(Map<String,Object> paramsMap);
	
	
	/**根据商铺的id查询商铺名称***/
	Map<String,Object> selectMerchantNameAndAuthStatusById(Long merchantId);

    /** 开通增值服务 */
    int openIncreaseService(Map<String, Object> params);

    /** 开通增值服务支付后的回调
    int finishOpenIncreaseService(Map<String, Object> params); */

    /** 根据包ID获取包的有效期 */
    Integer getEffictiveByPkgId(int pkgId);

    /** 检查是否重复支付 */
    int checkIsOpenIncreaseServiceByOrderNo(Map<String, Object> params);
    
    
    int checkIsOpenIncreaseServiceByInnerTradeNo(Map<String,Object> params);

    /** 根据包ID获取包的基本信息 */
    Map<String, Object> getPkgInfoById(int pkgId);

    /** 获取所有包含私人助理的包ID */
    List<Map<String, Object>> getConsultantPkgIds();
    
    /**
     * 获取包对应的原子项及规则内容
     * @param pkgId
     * @return
     */
    List<Map<String,Object>> pkgRuleItem(int pkgId);

    /**
     * 查询vip模板
     * @param paramMap
     * @return
     */
	List<Map<String, Object>> selectVipBackgroundUrlList(Map<String, Object> paramMap);

	/**
	 * 设置店铺vip模板
	 * @param paramMap
	 * @return
	 */
	int setVipBackgroundUrl(Map<String, Object> paramMap);
    
	/**
	 * 添加增值服务记录---有确认版本
	 * @param paramMap
	 * @return
	 */
	int addIncService(Map<String,Object> paramMap);
	
	
	/**
	 * 更新增值服务购买记录的状态为已确认
	 * @param paramMap
	 * @return
	 */
	int confirmIncServiceBuyRecord(Map<String,Object> paramMap);
}

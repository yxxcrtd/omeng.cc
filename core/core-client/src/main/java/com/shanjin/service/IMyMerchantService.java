package com.shanjin.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.exception.ApplicationException;

public interface IMyMerchantService {

	/**
	 * 获取商户验证码
	 * 
	 * @param mobileNumber
	 * @param mobileVersion
	 * @param handsetMakers
	 */
	public JSONObject getVerificationCode(String appType, String phone, String clientId, String ip) throws Exception;

	/** 获取语音验证码 */
	public JSONObject getVoiceVerificationCode(String appType, String phone, String clientId, String ip) throws Exception;

	/** 验证验证码 */
	public JSONObject validateVerificationCode(String appType, String phone, String verificationCode, String clientId, String clientType, String pushId);

	/** 开店 */
	public JSONObject openShop(String appType, String userId, String phone)throws Exception;

//	/** 我的店铺 */
//	public JSONObject selectMyMerchant(String appType, String phone, Long merchantId) throws Exception;

	/** 我的店铺 */
	public JSONObject selectMyMerchantV23(String appType, String phone, Long merchantId) throws Exception;

	/** 我的店铺 */
	public JSONObject selectMyMerchantV24(String appType, String phone, Long merchantId) throws Exception;

	/** 店铺信息详细 */
	public JSONObject merchantDetailInfo(Long merchantId) throws Exception;

	/** 店铺信息详细2 */
	public JSONObject merchantDetailInfo_2(Long merchantId) throws Exception;

	/** 检查是否认证 */
	public String checkIsNotAuth(Long merchantId);

	/** 魅力值信息查询 */
	public JSONObject charmValueInfo(String appType, Long merchantId) throws Exception;

	/** 获得商户信息，供用户端使用 */
	public JSONObject selectMyMerchantForUser(String appType, Long userId, Long merchantId) throws Exception;

	/** 获得商户信息，供用户端使用 */
	public JSONObject selectMyMerchantForUserV24(Long userId, Long merchantId, double longitude,
			double latitude) throws Exception;

	/** 获得商户信息2，供用户端使用 */
	public JSONObject selectMyMerchantForUserV24_2(Long userId, Long merchantId, Double longitude,
			Double latitude) throws Exception;

	/** 获得商户基本信息，供用户端使用 */
	public Map<String, Object> selectMerchantBasicForUser(Long merchantId);

    /** 获取商户基本信息 */
    Map<String, Object> selectMerchantBasicInfo(Long merchantId) throws Exception;

	/** 更新店铺名称 */
	public JSONObject updateNameAndDetail(String appType, Long Id, String name, String detail, String microWebsiteUrl, String invitationCode);

	/** 商户头像上传 */
	public JSONObject updateMerchantIcon(String appType, Long Id, String resultPath);

	/** 服务项目保存 */
	public JSONObject insertMerchantServiceType(String appType, String serviceTypes, Long Id, String phone);

	/** 服务项目保存 */
	public JSONObject insertMerchantServiceType(Long merchantId, String serviceTypeIds, String appType);

	/** 更新联系方式 */
	public JSONObject contactSave(String appType, Long merchantId, String telephone);

	/** 省市 保存 */
	public JSONObject citySave(String appType, Long merchantId, String province, String city);

	/** 更新地理位置 */
	public JSONObject updateLocation(String appType, Long merchantId, String province, String city, Double latitude, Double longitude, String mapType,
			String location, String detailAddress);

	/** 商户已经选择（1）未选择（0）的服务项目的编辑 */
	public JSONObject selectMerchantServiceForChoose(Long merchantId, String merchantType, String appType) throws Exception;

	/** 商户已经选择（1）未选择（0）的服务项目的编辑 */
	public JSONObject selectMerchantServiceForChoose(String appType, Long merchantId) throws Exception;

	/** 商户已经选择（1）未选择（0）的服务项目的编辑（多级服务类型） */
	public JSONObject selectMerchantServiceForChooseMultilevel(String appType, Long merchantId) throws Exception;

	/** 查询当前代金券信息 */
	public JSONObject selectCurrentVouchersInfo(String appType, Long merchantId, int pageNo) throws Exception;

	/** 查询历史代金券信息 */
	public JSONObject selectHistoryVouchersInfo(String appType, Long merchantId, int pageNo) throws Exception;

	/** 删除历史代金券信息 */
	public JSONObject deleteHistoryVouchers(String appType, String userPhone, Long id, Long merchantId);

	/** 代金券信息加载 */
	public JSONObject selectVouchersType(String appType, Long merchantId) throws Exception;

	/** 获取代金券剩余数量 */
	public JSONObject getSurplusVouchersNumber(Long vouchersId) throws Exception;

	/** 保存代金券信息 */
	public JSONObject insertMerchantVouchersPermissions(String appType, Long merchantId, Long vouchersId, String count, String cutoffTime);

	// /** 查看顾客评价 */
	// public JSONObject selectUserEvaluation(String appType, Long merchantId,
	// int pageNo);

	/** 申请认证的信息查询 */
	public JSONObject selectApplyAuthInfo(String appType, Long merchantId) throws Exception;

	/** 提交认证申请 */
	public JSONObject insertMerchantAuth(String appType, Long merchantId, int authType, String resultPath) throws Exception;

	/** 取消认证申请 */
	public JSONObject cancelAuth(String appType, Long merchantId) throws Exception;

	/** 查询相册 */
	public JSONObject selectAlbum(String appType, Long merchantId) throws Exception;

	/** 新建相册 */
	public JSONObject insertAlbum(String appType, Long merchantId, String albumName) throws Exception;

	/** 重命名相册 */
	public JSONObject updateAlbum(String appType, Long albumId, String albumName) throws Exception;

	/** 删除相册 */
	public JSONObject deleteAlbum(String appType, Long merchantId, Long albumId) throws Exception;

	/** 查询相片 */
	public JSONObject selectPhoto(String appType, Long albumId) throws Exception;

	/** 新建相片的校验 */
	public JSONObject insertPhotoVerify(String appType, Long merchantId, Long albumId);

	/** 新建相片 */
	public JSONObject insertPhoto(String appType, Long merchantId, Long albumId, String resultPath) throws Exception;

	/** 新建多个相片的校验 */
	public JSONObject insertPhotosVerify(String appType, Long merchantId, Long albumId, int newPhotoNums);

	/** 新建多个相片 */
	public JSONObject insertPhotos(String appType, Long merchantId, Long albumId, List<String> photoPaths) throws Exception;

	/** 删除相片 */
	public JSONObject deletePhoto(String appType, Long merchantId, String photoIds) throws Exception;

	/** 商户销户 */
	public JSONObject closeMerchant(String appType, Long merchantId);

	/** 员工列表显示 */
	public JSONObject employeesInfo(String appType, Long merchantId, int pageNo) throws Exception;

	/** 添加员工 获取验证码 */
	public JSONObject getVerificationCodeForAddEmployee(String appType, Long merchantId, String name, String phone,Integer type) throws Exception;

	/** 查询剩余可添加员工（顾问号）数 */
	public JSONObject surplusEmployeesNum(String appType, Long merchantId) throws Exception;

	/** 添加员工 确定 */
	public JSONObject addEmployeeConfirm(String appType, Long merchantId, String employeePhone, String verificationCode) throws Exception;

	/** 删除员工 */
	public JSONObject deleteEmployee(String appType,Long userId, Long merchantId, String phone) throws Exception;

	/** 员工老板身份转换 */
	public JSONObject updateBossEmployeeType(String appType, Long merchantId, String newBossPhone, String newEmployeePhone, String newEmployeeName) throws Exception;

	/** 增加员工数申请 */
	public JSONObject increaseEmployeeNumApply(int pkgId, String appType, Long merchantId, int increaseEmployeeNum, String money, int applyStatus, String payNo, String payType,Map<String,Object> paths) throws Exception;

	/** 会员申请 */
	public JSONObject vipApply(String appType, Long merchantId, String money, int applyStatus, String payNo, String payType, String tradeNo,Map<String,Object> paramMap) throws Exception;

	/** 会员申请状态 */
	public JSONObject vipApplyStatus(String appType, Long merchantId) throws Exception;

	/** 商户端退出应用 */
	public JSONObject deleteMerchantPush(String clientId) throws Exception;

	/** 根据经纬度计算用户与商户之间的距离 */
	public JSONObject selectCalcDistance(String appType, double longitude, double latitude, int range) throws Exception;

	/** 获取商户在当前应用程序中所能提供的服务项目 */
	public JSONObject selectMerchantProvideServiceType(String appType, Long merchantId, boolean allFlg) throws Exception;

	/** 验证用户是否登陆 */
	public JSONObject checkClient(String appType,Long userId, String clientId,String pushId, String phone, Long merchantId) throws Exception;

	/** 更改当前使用的设备记录的clientId */
	public JSONObject updateClientId(String appType, Long userId, String pushId, String phone, Long merchantId, String clientType, String clientId) throws Exception;

	/** 获取员工密钥 */
	public String getEmployeeKey(String phone);

	/** 更新微官网URL */
	public JSONObject updateMicroWebsiteUrl(String appType, Long merchantId, String microWebsiteUrl) throws Exception;

	/** 增值服务 */
	public JSONObject addedServices(String appType, Long merchantId) throws Exception;

	/** 查询商品分类信息 */
	public JSONObject selectGoodsClassificationInfo(String appType, Long merchantId,String isSelect) throws Exception;

	/** 新建商品分类 */
	public JSONObject addGoodsClassification(String appType, Long merchantId, String classificationName) throws Exception;

	/** 重命名商品分类 */
	public JSONObject renameGoodsClassification(String appType, Long merchantId, Long classificationId, String classificationName) throws Exception;

	/** 删除商品分类 */
	public JSONObject deleteGoodsClassification(String appType, Long merchantId, Long classificationId) throws ApplicationException,Exception;

	/** 查询商品信息 */
	public JSONObject selectGoodsInfo(String appType, Long merchantId, Long classificationId, int pageNo) throws Exception;

	/** 新建商品 */
	public JSONObject addGoods(String appType, Long merchantId, String classificationIds, String goodsName, String goodsPrice, String goodsDescribe,String goodsPriceUnit, List<String> paths)  throws ApplicationException,Exception;

	/** 更新商品信息 */
	public JSONObject updateGoodsInfo(String appType, Long merchantId, Long goodsId, String classificationId, String goodsName, String goodsPrice, String goodsDescribe,String goodsPriceUnit, List<String> paths) throws ApplicationException,Exception;

	/** 删除商品 */
	public JSONObject deleteGoods(String appType, Long merchantId, Long classificationId, String goodsId);

	/** 商品信息校验 */
	public JSONObject goodsInfoCheck(Long merchantId, Long goodsId, String goodsName, String classificationIds, int addOrUpdate);

	/** 查询最商户最大商品数 */
	public int getMaxGoodsNum(Long merchantId);
	
	/** 顾客评价（用户查看） */
	public JSONObject userEvaluationForUser(String appType, Long userId, Long merchantId, int pageNo);

	/** 服务标签 保存 */
	public JSONObject serviceTagSave(String tag, Long merchantId);

	/** 选择推荐的服务标签保存 */
	public JSONObject chooseServiceTagSave(String tags, Long merchantId);

	/** 个人申请的服务的保存 */
	public JSONObject personApplyServiceSave(String serviceNames, Long merchantId);

	/** 个人申请的服务的查询 */
	public JSONObject personApplyServiceQuery(Long merchantId);

	/** 个人申请的服务的删除 */
	public JSONObject personApplyServiceDelete(Long merchantId, Long id, int isAudit);

	/** 服务标签 删除 */
	public JSONObject deleteServiceTag(String tag, Long merchantId);

	/** 查询服务标签 */
	public JSONObject selectServiceTag(Long merchantId);

	/** 查询增值服务记录 */
	public JSONObject selectValueAddedService(Long merchantId, int pageNo) throws Exception;

	/** 删除增值服务记录记录 */
	public JSONObject delValueAddedService(Long merchantId, Long serviceId, String serviceType) throws Exception;

    JSONObject updateMerchantEmployeesNumApplyOpenTime(Long serviceId) throws Exception;

	/** 获得商户的位置信息 */
	public Map<String, Object> getLocationInfo(Long merchantId) throws Exception;

	/** 检查商户信息完成度 */
	public Map<String, Object> checkMerchantInfo(Long merchantId);

	/** 个性服务快速注册商户 */
	public JSONObject quickRegGxfwMerchantInfo(String phone, String name, String detial, String tags, String ip);
	
	/** 我的店铺列表 */
	public List<Map<String, Object>> myMerchantList(Long userId) throws Exception;

	/** 添加代金券（多条） 用于接单计划 */
	public JSONObject insertMerchantVouchersPermissions(Long merchantId, String vouchersIds, String counts, String cutoffTimes, Integer planNum);

	/** 接单计划 */
	public JSONObject orderPlan(Long merchantId);

	/** 个人服务（技能）搜索 */
	public JSONObject personServiceSearch(String keyword);
	
	/** 获取关注列表*/
	public JSONObject getCollections(Long merchantId,int pageNo);

	/** 商户编辑页信息查询 */
	public JSONObject merchantEditPageInfo(Long merchantId) throws Exception;
	
	/**修改店铺详情**/
	public JSONObject updateMerchantDetail(Long merchantId,String detail) throws Exception;
		
	/***查询商户账户余额信息***/
	public JSONObject selectMerchantAccountStatistics(Map<String,Object> params) throws Exception;
	
	/** 查询商户统计信息   Revoke Yu 2016.7.19 日   原实现类的private 改为public **/
	public Map<String, Object> statisticsInfoEdit(Long merchantId);
	
	public Map<String, Object> merchantBasicInfo(Long merchantId)throws Exception;	
	
	/** 取抢单金充值状态**/
	public int selectMerchantPayApplyStatus(Map paramMap);

	/**
	 * 产品上下架
	 * @param appType
	 * @param merchantId
	 * @param targetStatus 0-上架，1-上架
	 * @param goodsId
	 * @return
	 */
	public JSONObject changeGoodsStatus(String appType, Long merchantId, int targetStatus, String goodsId) throws ApplicationException,Exception;

	/**
	 * 查询产品图片
	 * @param appType
	 * @param merchantId
	 * @param goodsId
	 * @return
	 */
	public JSONObject selectGoodsDetail(String appType, Long merchantId,String goodsId) throws ApplicationException,Exception;

	/**
	 * 生成商品快照
	 * @param goodsId 商品id
	 * @param version 记录版本号
	 * @return
	 * @throws ApplicationException
	 * @throws Exception
	 */
	public Long createGoodsHistory(Long goodsId,int version) throws ApplicationException,Exception;

    /**
     * 用户查询商品列表
     * @param merchantId
     * @param classificationId
     * @param pageNo
     * @return
     * @throws Exception
     */
	JSONObject selectGoodsList(Long merchantId, Long classificationId,int pageNo) throws Exception;

	/**
	 * 查询商品快照信息
	 * @param goodsHistoryId
	 * @return
	 * @throws ApplicationException
	 * @throws Exception
	 */
	public JSONObject selectGoodsHistoryInfo(String goodsHistoryId,String goodsId,String version) throws ApplicationException,Exception;

    /** 开通增值服务 */
    JSONObject openIncreaseService(Map<String, Object> params) throws Exception;

    /** 根据包ID获取包的基本信息 */
    Map<String, Object> getPkgInfoById(int pkgId);

	public JSONObject selectVipBackgroundUrlList(String appType,long merchantId);

	public JSONObject setVipBackgroundUrl(Long merchantId,String vipBackgroundUrl);
	
	public Map<String,Object> selectpaymentStatus(Map<String,Object> params);
	
	/**
	 * 增加待确认的雇员服务申请记录
	 * @param params
	 * @return
	 */
	public JSONObject addEmployeeNumApplyNeedConfirm(Map<String,Object> params);
	
	
	/**
	 * 增加待确认的增值服务申请记录
	 * @param params
	 * @return
	 */
	public JSONObject addIncPackageNeedConfirm(Map<String,Object> params);

	/**保存商户二维码*/
	public String selectMerchantIcon(Long merchantId);

	/**查询商户二维码*/
	public String selectMerchantQrcode(Long merchantId);

	/**保存商户二维码*/
	public int insertMerchantQrcode(Long merchantId,String path);

}

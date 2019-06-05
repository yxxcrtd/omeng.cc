package com.shanjin.common.util;

public class ConfigurationUtil {
	/**
	 * 抢单最大数
	 */
	public static int orderPlanCount;
	public static String registerGiveMoney;
	public static String giveMoneyStartDate;
	public static String giveMoneyEndDate;

	/** 订单详情页，商品信息标题 */
	public static String orderGoodsInfoTitleDgf;
	public static String orderGoodsInfoTitleXhf;
	public static String orderGoodsInfoTitleYdc;
	public static String orderGoodsInfoTitleSwg;

	/** 商品分类数量最大值 */
	public static int goodsClassificationNumMax;

	/** 商品数量最大值 */
	public static int goodsNumMax;

	/** 相册数量最大值 */
	public static int albumNumMax;

	/** 相片数量最大值 */
	public static int photoNumMax;

	public static void setOrderPlanCount(int orderPlanCount) {
		
		ConfigurationUtil.orderPlanCount = orderPlanCount;
	}
	public static void setRegisterGiveMoney(String registerGiveMoney) {
		ConfigurationUtil.registerGiveMoney = registerGiveMoney;
	}

	public static void setGiveMoneyStartDate(String giveMoneyStartDate) {
		ConfigurationUtil.giveMoneyStartDate = giveMoneyStartDate;
	}

	public static void setGiveMoneyEndDate(String giveMoneyEndDate) {
		ConfigurationUtil.giveMoneyEndDate = giveMoneyEndDate;
	}

	public static void setOrderGoodsInfoTitleDgf(String orderGoodsInfoTitleDgf) {
		ConfigurationUtil.orderGoodsInfoTitleDgf = orderGoodsInfoTitleDgf;
	}

	public static void setOrderGoodsInfoTitleXhf(String orderGoodsInfoTitleXhf) {
		ConfigurationUtil.orderGoodsInfoTitleXhf = orderGoodsInfoTitleXhf;
	}

	public static void setOrderGoodsInfoTitleYdc(String orderGoodsInfoTitleYdc) {
		ConfigurationUtil.orderGoodsInfoTitleYdc = orderGoodsInfoTitleYdc;
	}

	public static void setOrderGoodsInfoTitleSwg(String orderGoodsInfoTitleSwg) {
		ConfigurationUtil.orderGoodsInfoTitleSwg = orderGoodsInfoTitleSwg;
	}

	public static void setGoodsClassificationNumMax(int goodsClassificationNumMax) {
		ConfigurationUtil.goodsClassificationNumMax = goodsClassificationNumMax;
	}

	public static void setGoodsNumMax(int goodsNumMax) {
		ConfigurationUtil.goodsNumMax = goodsNumMax;
	}

	public static void setAlbumNumMax(int albumNumMax) {
		ConfigurationUtil.albumNumMax = albumNumMax;
	}

	public static void setPhotoNumMax(int photoNumMax) {
		ConfigurationUtil.photoNumMax = photoNumMax;
	}
}

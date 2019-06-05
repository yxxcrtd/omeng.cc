

package com.shanjin.carinsur.model;

/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-08-31 23:56:12 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public class MsgTraInsurInfo extends AbsInsurInfo {

	public static final String TABLE_NAME = "msg_tra_insur_info";

	public static final String TABLE_ALIAS = "MsgTraInsurInfo";


	public MsgTraInsurInfo() {
		super();
	}

	public MsgTraInsurInfo(String orderNo) {
		super(orderNo);
	}
}


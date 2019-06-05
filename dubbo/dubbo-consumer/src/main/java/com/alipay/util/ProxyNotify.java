package com.alipay.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.alibaba.dubbo.common.store.support.SimpleDataStore;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.util.HttpXmlClient;

public class ProxyNotify {

	/**
	 * 验证消息是否是支付宝发出的合法消息
	 * 
	 * @param params
	 *            通知返回来的参数数组
	 * @return 验证结果
	 */
	public static boolean verify(Map<String, String> params) {
		System.out.println("====进入支付宝的验证====");
		
		System.out.println((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))+"-验证接受的参数:" + params);

		// params.put("discount", "0.00");
		// params.put("payment_type", "1");
		// params.put("subject", "测 试");
		// params.put("trade_no", "2013082244524842");
		// params.put("buyer_email", "dlwdgl@gmail.com");
		// params.put("gmt_create", "2013-08-22 14:45:23");
		// params.put("notify_type", "trade_status_sync");
		// params.put("quantity", "1");
		// params.put("out_trade_no", "082215222612710");
		// params.put("seller_id", "2088501624816263");
		// params.put("notify_time", "2013-08-22 14:45:24");
		// params.put("body", "测试测 试");
		// params.put("trade_status", "TRADE_SUCCESS");
		// params.put("is_total_fee_adjust", "N");
		// params.put("total_fee", "1.00");
		// params.put("gmt_payment", "2013-08-22 14:45:24");
		// params.put("seller_email", "xxx@alipay.com");
		// params.put("price", "1.00");
		// params.put("buyer_id", "2088602315385429");
		// params.put("notify_id", "64ce1b6ab92d00ede0ee56ade98fdf2f4c");
		// params.put("use_coupon", "N");
		// params.put("sign_type", "RSA");
		// params.put("sign",
		// "1glihU9DPWee+UJ82u3+mw3Bdnr9u01at0M/xJnPsGuHh+JA5bk3zbWaoWhU6GmLab3dIM4JNdktTcEUI9/FBGhgfLO39BKX/eBCFQ3bXAmIZn4l26fiwoO613BptT44GTEtnPiQ6+tnLsGlVSrFZaLB9FVhrGfipH2SWJcnwYs=");
        String url=Constant.WEB_PROXY_URL + "/alipayVeryfy";
		String responseTxt = HttpXmlClient.post(Constant.WEB_PROXY_URL + "/alipayVeryfy", params);
		System.out.println("支付宝支付验证结果："+responseTxt);
		boolean verify = Boolean.parseBoolean(responseTxt);
		System.out.println("verify："+verify);
		return verify;
	}
}

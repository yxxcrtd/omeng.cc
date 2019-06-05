package com.shanjin.service.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.alipay.config.AlipayConfig;
import com.shanjin.alipay.sign.RSA;
import com.shanjin.awechat.ConstantUtil;
import com.shanjin.awechat.GetWxOrderno;
import com.shanjin.awechat.MD5Util;
import com.shanjin.awechat.NameValuePair;
import com.shanjin.awechat.RequestHandler;
import com.shanjin.awechat.Sha1Util;
import com.shanjin.awechat.TenpayUtil;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.DateUtil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.dao.IMerchantPayDao;
import com.shanjin.service.IMerchantPayService;
import com.shanjin.unionpay.UnionPayUtil;


@Service("merchantPayService")
public class MerchantPayService implements IMerchantPayService {
	
	@Resource
	public IMerchantPayDao merchantPayDao;
	
	private Logger logger = Logger.getLogger(MerchantPayService.class);
	
	/**
	 * 得到商户订单余额
	 * @return
	 */
	public BigDecimal getOrderSurplusMoney(Map<String,Object> paramMap){
		BigDecimal orderSurplusMoney=null;
		Object obj=merchantPayDao.getOrderSurplusMoney(paramMap);
		 if(obj!=null){
			 orderSurplusMoney=new BigDecimal(StringUtil.null2Str(obj));
		 }
		 return orderSurplusMoney;
	}
	/**
	 * 查询抢单费用
	 * @return
	 */
	public BigDecimal getOrderPrice(Map<String,Object> paramMap){
		BigDecimal orderPrice=null;
		Object obj=merchantPayDao.getOrderPrice(paramMap);
		 if(obj!=null){
			 orderPrice=new BigDecimal(StringUtil.null2Str(obj));
		 }
		 return orderPrice;
	}
	/**
	 * 余额中扣除抢单费
	 * @param paramMap
	 * @return
	 */
	public int deductOrderMoney(Map<String,Object> paramMap){
		return merchantPayDao.deductOrderMoney(paramMap);
	}
	/**
	 * 生成扣费记录
	 * @return
	 */
	public int addMerchantOrderPaymentDetails(Map<String,Object> paramMap){
		//订单ID
		Long orderId=Long.parseLong(paramMap.get("orderId")==null?"0":paramMap.get("orderId").toString());
		//根据订单ID查找服务类型
		Object serviceTypeId=merchantPayDao.getServiceTypeIdByOrderId(orderId);
		paramMap.put("serviceTypeId", serviceTypeId);
		return merchantPayDao.addMerchantOrderPaymentDetails(paramMap);	
	}
	@Override
	public JSONObject getPayParm(Integer type, String orderMerId,
			Double totalFee, Integer payType, String subject,
			Integer employeeNumber,String appType,Integer clientType,Integer pkgId,Long userId,String openId,Double consumePrice,String inviteCode) throws Exception {
		JSONObject jsonObject=new JSONObject();
		totalFee=totalFee-consumePrice;
		if(totalFee<=0){
			jsonObject.put("resultCode", "050");
			jsonObject.put("message", "获取支付参数失败,支付金额错误");
		}
		if(StringUtil.isNullStr(appType)){
			appType="0";
		}
		String random=ConstantUtil.getRandomStr();
		//20161028增加consumePrice
		String outTradeNo="";
        orderMerId=orderMerId+random;
		if(type!=1){
            if(type==21){
            	int iconsumePrice=0;
                if(consumePrice>0){
                    consumePrice=consumePrice*100;
                    iconsumePrice=new BigDecimal(consumePrice).intValue();
                }
            	outTradeNo=type+"iii"+orderMerId+"iii"+userId+"iii"+consumePrice;
            }
            else{
			//20161028增加inviteCode
			outTradeNo=type+"iii"+orderMerId+"iii"+employeeNumber+"iii"+appType+"iii"+clientType+"iii"+pkgId+"iii"+inviteCode;
            }
			
		}else {
			 //订单微信支付,商户侧订单需特殊处理
			 //Integer version=merchantPayDao.getMerchantServiceRecordByID(orderMerId);
			//20161029  新版没有服务记录
//			 if (null!=version&&version>0){
//				  orderMerId=orderMerId+"iii"+version;
//			 }else{
//				 orderMerId=orderMerId+random;
//			 }
            //便于传递整数，支付宝不能传递小数点,回调除以100
            int iconsumePrice=0;
            if(consumePrice>0){
                consumePrice=consumePrice*100;
                iconsumePrice=new BigDecimal(consumePrice).intValue();
            }
			outTradeNo=type+"iii"+orderMerId+"iii"+employeeNumber+"iii"+appType+"iii"+iconsumePrice;

		}
		String aoutTradeNo=orderMerId;
		String notifyUrl="";
		String partner="";
		String sellerId="";
		String privateKey="";
		String appId="";
		String mchId="";
		String partnerKey="";
		String outTradeNoLog="";
		//支付类型
		if (payType == 1) {
			outTradeNoLog=outTradeNo;
			if(Constant.DEVMODE){
				partner=AlipayConfig.partner_test;
				sellerId=AlipayConfig.seller_id_test;
				privateKey=AlipayConfig.private_key_test;
				notifyUrl=Constant.PAY_ALI_NOTIFY_URL_TEST;
				
			}else{
				partner=AlipayConfig.partner;
				sellerId=AlipayConfig.seller_id;
				privateKey=AlipayConfig.private_key;
				notifyUrl=Constant.PAY_ALI_NOTIFY_URL;
			}
			
			// 支付宝支付
			String orderParms = "partner=" + "\"" + partner + "\"";
			orderParms += "&seller_id=" + "\"" + sellerId + "\"";
			orderParms += "&out_trade_no=" + "\"" + outTradeNo+"\"";
			orderParms += "&subject=" + "\"" + subject + "\"";
			// orderParms += "&body=" + "\"" + payInfo.getOrder_type() + "\"";
			orderParms += "&body=" + "\"" + subject + "\"";
			orderParms += "&total_fee=" + "\"" + totalFee + "\"";
			orderParms += "&notify_url=" + "\"" + notifyUrl + "\"";
			orderParms += "&service=" + "\"" + "mobile.securitypay.pay" + "\"";
			orderParms += "&payment_type=" + "\"" + "1" + "\"";
			orderParms += "&_input_charset=" + "\"" + "utf-8" + "\"";
			orderParms += "&it_b_pay=" + "\"" + "30s" + "\"";
			orderParms += "&sign_type=" + "\"" + "RSA" + "\"";
			String sign = RSA.sign(orderParms, privateKey, "utf-8");
			try {
				sign = java.net.URLEncoder.encode(sign, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				logger.error("signerror", e);
				jsonObject.put("resultCode", "001");
				jsonObject.put("message", "获取支付参数失败");
				jsonObject.put("payType", 1);
				jsonObject.put("dataAlipay", "");
				return jsonObject;
			}
			orderParms += "&sign=" + "\"" + sign + "\"";
			orderParms += "&sign_type=" + "\"" + "RSA" + "\"";
			jsonObject.put("dataAlipay", orderParms);
			jsonObject.put("payType", 1);
			jsonObject.put("outTradeNo", orderMerId);
		} else if (payType == 2) {
			outTradeNoLog=aoutTradeNo;
			// 微信支付
			// 接收财付通通知的URL
			if(Constant.DEVMODE){
				appId=ConstantUtil.APP_ID_TEST;
				mchId=ConstantUtil.PARTNER_TEST;
				partnerKey=ConstantUtil.PARTNER_KEY_TEST;
				notifyUrl=Constant.PAY_WX_NOTIFY_URL_TEST;
			}else{
				appId=ConstantUtil.APP_ID;
				mchId=ConstantUtil.PARTNER;
				partnerKey=ConstantUtil.PARTNER_KEY;
				notifyUrl=Constant.PAY_WX_NOTIFY_URL;
			}
			//金额*100之后再装为转为整数
			String  itotalFee=new DecimalFormat("#").format(totalFee*100);
			String spbill_create_ip = "127.0.0.1";
			String trade_type = "APP";
			subject="a";
			Map map = this.getWeChat(appId,mchId,partnerKey,outTradeNo,aoutTradeNo, itotalFee, subject, spbill_create_ip, notifyUrl, trade_type);
			String returnCode=map.get("returnCode").toString();
			if("000".equals(returnCode)){
				jsonObject.put("resultCode", "000");
				jsonObject.put("message", "获取支付参数成功");
			}else if("040".equals(returnCode)){
				jsonObject.put("resultCode", "040");
				jsonObject.put("message", "交易已经支付，请勿重新付款。");
			}else{
				jsonObject.put("resultCode", "041");
				jsonObject.put("message", "获取支付参数失败");
			}
			map.remove("returnCode");
			jsonObject.put("dataWechat", map);
			jsonObject.put("payType", 2);
			jsonObject.put("outTradeNo", orderMerId);
			BusinessUtil.writeLog("wxpay","返回支付参数-"+DateUtil.getNowYYYYMMDDHHMMSS() + ",支付订单号=" + outTradeNoLog+",返回结果="+jsonObject);
			return jsonObject;
		}else if (payType == 3) {
			Map<String,Object> map=new HashMap<>();
			String  itotalFee=new DecimalFormat("#").format(totalFee*100);
			if(Constant.DEVMODE){
				notifyUrl=Constant.PAY_UNION_NOTIFY_URL_TEST;
			}else{
				notifyUrl=Constant.PAY_UNION_NOTIFY_URL;
			}
			map= UnionPayUtil.getTn(outTradeNo,aoutTradeNo,itotalFee,notifyUrl);
			BusinessUtil.writeLog("unionpay","返回支付参数-"+DateUtil.getNowYYYYMMDDHHMMSS() + ",支付订单号=" + outTradeNoLog+",返回结果="+jsonObject);
			String returnCode=map.get("respCode").toString();
			String tn=map.get("tn").toString();
			
			if("00".equals(returnCode)){
				jsonObject.put("resultCode", "000");
				jsonObject.put("message", "获取支付参数成功");
			}else if("12".equals(returnCode)){
				jsonObject.put("resultCode", "041");
				jsonObject.put("message", "交易已经支付，请勿重新付款。");
			}else{
				jsonObject.put("resultCode", "010");
				jsonObject.put("message", "获取支付参数失败。");
			}
			jsonObject.put("dataUnionPay", tn);
			jsonObject.put("payType", 3);
			return jsonObject;
		} else if (6==payType||7==payType) {
			outTradeNoLog=aoutTradeNo;
			String  itotalFee=new DecimalFormat("#").format(totalFee);
			// 微信支付
			// 接收财付通通知的URL
			if(Constant.DEVMODE){
				appId=ConstantUtil.APP_ID_H5_TEST;
				mchId=ConstantUtil.PARTNER_H5_TEST;
				partnerKey=ConstantUtil.PARTNER_KEY_H5_TEST;
				notifyUrl=Constant.PAY_WX_H5_NOTIFY_URL_TEST;
                if(7==payType){
                    notifyUrl=Constant.PAY_WX_KING_H5_NOTIFY_URL_TEST;
                }
				itotalFee="0.01";
			}else{
				appId=ConstantUtil.APP_ID_H5;
				mchId=ConstantUtil.PARTNER_H5;
				partnerKey=ConstantUtil.PARTNER_KEY_H5;
				notifyUrl=Constant.PAY_WX_H5_NOTIFY_URL;
                if(7==payType){
                    notifyUrl=Constant.PAY_WX_KING_H5_NOTIFY_URL;
                }
			}
			
			String spbill_create_ip = "127.0.0.1";
			String trade_type = "JSAPI";
			Map map = this.getH5WeChat(payType,appId,mchId,partnerKey,outTradeNo,aoutTradeNo, itotalFee, subject, spbill_create_ip, notifyUrl, trade_type,openId);
			String returnCode=map.get("returnCode").toString();
			if("000".equals(returnCode)){
				jsonObject.put("resultCode", "000");
				jsonObject.put("message", "获取支付参数成功");
			}else if("041".equals(returnCode)){
				jsonObject.put("resultCode", "041");
				jsonObject.put("message", "交易已经支付，请勿重新付款。");
			}else if("040".equals(returnCode)){
				jsonObject.put("resultCode", "040");
				jsonObject.put("message", "获取支付参数失败");
			}
			map.remove("returnCode");
			jsonObject.put("dataWechat", map);
			jsonObject.put("payType", payType);
			BusinessUtil.writeLog("wxpayh5","返回支付参数-"+DateUtil.getNowYYYYMMDDHHMMSS() + ",支付订单号=" + outTradeNoLog+",返回结果="+jsonObject);
			return jsonObject;
		}
		jsonObject.put("resultCode", "000");
		jsonObject.put("message", "获取支付参数成功");
		logger.info("jsonObject="+jsonObject);
		BusinessUtil.writeLog("alipay","返回支付参数-"+DateUtil.getNowYYYYMMDDHHMMSS() + ",支付订单号=" + outTradeNoLog+",返回结果="+jsonObject);
		return jsonObject;
	}

    @Override
    public int updateMerchantOrderSurplusPrice(Map<String, Object> paramMap) {
        return merchantPayDao.updateMerchantOrderSurplusPrice(paramMap);
    }
    
    /**
  	 * 获取app微信支付参数
  	 * 
  	 * @param 
  	 * @return
  	 */
    public Map<String, Object> getWeChat(String  appId,String mchId,String  partnerKey,String attach,String outTradeNo, String totleFee, String body, String spbill_create_ip,
			String notify_url, String trade_type) {
		//返回给客户端的参数
		Map<String, Object> paramInfo = new HashMap<String, Object>();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		// 公众账号ID
	      params.add(new NameValuePair("appid", appId));
	      //附加数据
	      params.add(new NameValuePair("attach", attach));
	      //商品描述
	      params.add(new NameValuePair("body", body));
	      //商户号
	      params.add(new NameValuePair("mch_id", mchId));
	      // 随机字符串
		  String nonce_str = ConstantUtil.getNonceStr();
	      params.add(new NameValuePair("nonce_str", nonce_str));
	      //通知地址
	      params.add(new NameValuePair("notify_url", notify_url));
	
	      //订单号
	      params.add(new NameValuePair("out_trade_no", outTradeNo));
	      //终端IP
	      params.add(new NameValuePair("spbill_create_ip", spbill_create_ip));
	      //总金额
	      params.add(new NameValuePair("total_fee", totleFee));
	      //交易类型
	      params.add(new NameValuePair("trade_type", trade_type));
	      //签名
	      String sign=genPackageSign(params,partnerKey);
	      params.add(new NameValuePair("sign", sign));
	      //拼成xml
	      String xmlStr=toXml(params);
	      // 接口链接
	    String createOrderURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
		String prepay_id="";
		String returnCode="";
		Map<String,Object> map=new HashMap<>();
		// 代理生成生成预订单
		try {
			xmlStr = new String(xmlStr.getBytes(), "ISO8859-1");
			map= ConstantUtil.getPrepayId(createOrderURL, xmlStr);
			System.out.println("获取到的预支付map：" + map);
			returnCode=map.get("returnCode").toString();
			
			if("000".equals(returnCode)){
				prepay_id=map.get("returnData").toString();
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 本地生成预订单
//		try {
//			xmlStr = new String(xmlStr.getBytes(), "ISO8859-1");
//			map= GetWxOrderno.getPayNo(createOrderURL, xmlStr);
//			returnCode=map.get("returnCode").toString();
//			if("000".equals(returnCode)){
//				prepay_id=map.get("returnData").toString();
//			}
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
		System.out.println("获取到的预支付prepay_id：" + prepay_id);
		// 获取prepay_id后,进行二次签名
		// 二次签名sign
		SortedMap<String, Object> packageParamsSec = new TreeMap<String, Object>();
		// 公众账号ID
		packageParamsSec.put("appid", appId);
		// 商户号
		packageParamsSec.put("partnerid", mchId);
		// 预支付交易会话ID
		packageParamsSec.put("prepayid", prepay_id);
		// 扩展字段
		final String str = "Sign=WXPay";
		packageParamsSec.put("package", str);
		// 得出当前时间戳
		String currTimeSec = TenpayUtil.getCurrTime();
		// 8位日期
		String strTimeSec = currTimeSec.substring(8, currTimeSec.length());
		// 四位随机数
		String strRandomSec = TenpayUtil.buildRandom(4) + "";
		// 10位序列号,可以自行调整。
		String strReqSec = strTimeSec + strRandomSec;
		// 随机数
		String nonce_strSec = strReqSec;
		// 随机字符串
		packageParamsSec.put("noncestr", nonce_strSec);
		// 时间戳
		long timestamp = System.currentTimeMillis() / 1000;
		packageParamsSec.put("timestamp", timestamp);
		// 新签名算法
		String signSec = getSign(packageParamsSec,partnerKey);
		paramInfo.put("appid", appId);
		paramInfo.put("partnerid", mchId);
		paramInfo.put("prepayid", prepay_id);
		paramInfo.put("packageValue", str);
		paramInfo.put("noncestr", nonce_strSec);
		paramInfo.put("timestamp", timestamp);
		paramInfo.put("sign", signSec);
		paramInfo.put("returnCode", returnCode);
		paramInfo.put("totalFee", totleFee);
		return paramInfo;
	}
    
    /**
	 * 获取h5微信支付参数
	 * 
	 * @param 
	 * @return
	 */
    public Map<String, Object> getH5WeChat(Integer payType,String  appId,String mchId,String  partnerKey,String attach,String outTradeNo, String totleFee, String body, String spbill_create_ip,
			String notify_url, String trade_type,String openId) {
		// 网页授权后获取传递的参数
		
		// 金额转化为分为单位
		float sessionmoney = Float.parseFloat(totleFee);
		String finalmoney = String.format("%.2f", sessionmoney);
		finalmoney = finalmoney.replace(".", "");

		// 商户相关资料
		

		
		// 获取openId后调用统一支付接口https://api.mch.weixin.qq.com/pay/unifiedorder
		String currTime = TenpayUtil.getCurrTime();
		// 8位日期
		String strTime = currTime.substring(8, currTime.length());
		// 四位随机数
		String strRandom = TenpayUtil.buildRandom(4) + "";
		// 10位序列号,可以自行调整。
		String strReq = strTime + strRandom;

		// 商户号
		String mch_id = mchId;
		// 子商户号 非必输
		// String sub_mch_id="";
		// 设备号 非必输
		String device_info = "";
		// 随机数
		String nonce_str = strReq;
		// 商品描述
		// String body = describe;

		int intMoney = Integer.parseInt(finalmoney);
       
		// 总金额以分为单位，不带小数点
		String total_fee = String.valueOf(intMoney);
		// 订单生成的机器 IP
//		String spbill_create_ip = "127.0.0.1";
		// 订 单 生 成 时 间 非必输
		// String time_start ="";
		// 订单失效时间 非必输
		// String time_expire = "";
		// 商品标记 非必输
		// String goods_tag = "";

		// 这里notify_url是 支付完成后微信发给该链接信息，可以判断会员是否支付成功，改变订单状态等。
//		String notify_url = Constant.notify_url;

//		String trade_type = "JSAPI";
		String openid = openId;
		// 非必输
		// String product_id = "";
		SortedMap<String, String> packageParams = new TreeMap<String, String>();
		packageParams.put("appid", appId);
		packageParams.put("mch_id", mch_id);
		packageParams.put("nonce_str", nonce_str);
		packageParams.put("body", body);
		packageParams.put("attach", attach);
		packageParams.put("out_trade_no", outTradeNo);

		// 金额为单位为 分
		packageParams.put("total_fee", total_fee);
		packageParams.put("spbill_create_ip", spbill_create_ip);
		packageParams.put("notify_url", notify_url);

		packageParams.put("trade_type", trade_type);
		packageParams.put("openid", openid);

		RequestHandler reqHandler = new RequestHandler(null, null);
		reqHandler.init(appId, appId, partnerKey);

		String sign = reqHandler.createSign(packageParams);
		String xmlStr = "<xml>" + "<appid>" + appId + "</appid>" + "<mch_id>"
				+ mch_id + "</mch_id>" + "<nonce_str>" + nonce_str
				+ "</nonce_str>" + "<sign>" + sign + "</sign>"
				+ "<body><![CDATA[" + body + "]]></body>" + "<attach>" + attach
				+ "</attach>"
				+ "<out_trade_no>"
				+ outTradeNo
				+ "</out_trade_no>"
				+"<total_fee>"
				+ total_fee
				+ "</total_fee>"
				+"<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>"
				+ "<notify_url>" + notify_url + "</notify_url>"
				+ "<trade_type>" + trade_type + "</trade_type>" + "<openid>"
				+ openid + "</openid>" + "</xml>";
		String allParameters = "";
		try {
			allParameters = reqHandler.genPackage(packageParams);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  // 接口链接
	    String createOrderURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
		String prepay_id="";
		String returnCode="";
		Map<String,Object> map=new HashMap<>();
		// 代理生成生成预订单
		try {
			xmlStr = new String(xmlStr.getBytes(), "ISO8859-1");
			map= ConstantUtil.getPrepayId(createOrderURL, xmlStr);
			System.out.println("获取到的预支付map：" + map);
			returnCode=map.get("returnCode").toString();
			
			if("000".equals(returnCode)){
				prepay_id=map.get("returnData").toString();
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String, Object> paramInfo = new HashMap<String, Object>();
		paramInfo.put("returnCode", returnCode);
		System.out.println("获取到的预支付prepay_id：" + prepay_id);
		SortedMap<String, String> finalpackage = new TreeMap<String, String>();
		String appid2 = appId;
		String timestamp = Sha1Util.getTimeStamp();
		String nonceStr2 = nonce_str;
		String prepay_id2 = "prepay_id=" + prepay_id;
		String packages = prepay_id2;
		finalpackage.put("appId", appid2);
		finalpackage.put("timeStamp", timestamp);
		finalpackage.put("nonceStr", nonceStr2);
		finalpackage.put("package", packages);
		finalpackage.put("signType", "MD5");
		String finalsign = reqHandler.createSign(finalpackage);
		
		System.out.println("finalsign="+finalsign);
		paramInfo.put("appid", appid2);
		paramInfo.put("timestamp", timestamp);
		paramInfo.put("noncestr", nonceStr2);
		paramInfo.put("signType", "MD5");
		paramInfo.put("prepayid", prepay_id);
		paramInfo.put("sign", finalsign);
		return paramInfo;
	
	}

	/**
	 * 新签名算法
	 * 
	 * @param map
	 * @return
	 */
	public static String getSign(Map<String, Object> map,String partnerKey) {
		ArrayList<String> list = new ArrayList<String>();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (entry.getValue() != "") {
				list.add(entry.getKey() + "=" + entry.getValue() + "&");
			}
		}
		int size = list.size();
		String[] arrayToSort = list.toArray(new String[size]);
		Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			sb.append(arrayToSort[i]);
		}
		String result = sb.toString();
		result += "key=" + partnerKey;
		result = MD5Util.MD5Encode(result, "UTF-8").toUpperCase();
		return result;
	}
	
	/**
	 * 拼成xml
	 * 
	 * @param 
	 * @return
	 */
	public static String toXml(List<NameValuePair> params) {
	    StringBuilder sb = new StringBuilder();
	    sb.append("<xml>");
	    for (int i = 0; i < params.size(); i++) {
	      sb.append("<" + params.get(i).getName() + ">");
	      sb.append(params.get(i).getValue());
	      sb.append("</" + params.get(i).getName() + ">");
	    }
	    sb.append("</xml>");

	    return sb.toString();
	  }
	
	/**
	 * 获取加密sign
	 * 
	 * @params
	 * @return
	 */
	public static String genPackageSign(List<NameValuePair> params,String partnerKey) {
	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < params.size(); i++) {
	      sb.append(params.get(i).getName());
	      sb.append('=');
	      sb.append(params.get(i).getValue());
	      sb.append('&');
	    }
	    sb.append("key=");
	    sb.append(partnerKey);
	    String sign = MD5Util.MD5Encode(sb.toString(), "UTF-8").toUpperCase();
	    return sign;
	  }

	
	
	public static void main(String[] args){
		String outTradeNo="1212121212123";
		double totalFee=12;
		String subject="开分";
		//本地测试
		//System.out.println(new MerchantPayService().getPayParm(outTradeNo,totalFee,2,subject));
		System.out.println("1469587000147631550500".substring(0, 18));
	}
	
}

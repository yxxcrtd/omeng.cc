package com.alipay.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.DateUtil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.controller.AppOrderSvrManager;
import com.shanjin.service.IKingService;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.shanjin.common.MQTools.sendMQ;

public class AlipayOrder {

    // 日志
    private static final Logger logger = Logger.getLogger(AlipayOrder.class);

	@Reference
    private static AlipayMerchantInfoService alipayMerchantInfoService;
    @Reference
    private static IKingService kingService;

	public static boolean finishAliPayOrder(String outTradeNo,String tradeNo) throws Exception {
		String[] paras = outTradeNo.split("iii");
		Long orderId = 0L;
		String appType = "";
		// 检查参数
		if (paras[0] != null) {
			orderId = Long.parseLong(StringUtil.null2Str(paras[0]));
		} else {
			return false;
		}
		if (paras[1] != null) {
			appType = StringUtil.null2Str(paras[1]);
		} else {
			return false;
		}
        return wrapSendMQ(orderId, tradeNo,null,null, null, 0.0, null, appType);
	}

	public static boolean totalFinishAliPayOrder(String outTradeNo,String totalFee,String tradeNo,String payDate,String buyerNo) {
		BusinessUtil.writeLog("alipay","时间="+DateUtil.getNowYYYYMMDDHHMMSS()+",进入支付宝业务回调开始,支付订单号="+outTradeNo);
		String paras[]=outTradeNo.split("iii");
		Integer type=0;
		Integer employeeNumber=0;
		Boolean flag=false;
		String appType="";
		Long orderMerId=0l;
		JSONObject jsonobject=null;
		Integer clientType=0;
		String orderNo="";
        int pkgId = 0;
//        Long userId = 0L;
        Double consumePrice = 0.0;
        String inviteCode="";
		try {
			// 检查参数
			if (paras[0] != null) {
				type = Integer.parseInt(StringUtil.null2Str(paras[0]));
			} else {
				return false;
			}
			if (paras[1] != null) {
				orderNo = StringUtil.null2Str(paras[1]);
			} else {
				return false;
			}
			if (paras[2] != null) {
				employeeNumber =Integer.parseInt(StringUtil.null2Str(paras[2]));
			} else {
				return false;
			}
			if (paras[3] != null) {
				appType =StringUtil.null2Str(paras[3]);
			} else {
				return false;
			}
            if (paras[4] != null) {
                consumePrice = StringUtil.nullToDouble(paras[4]);
                DecimalFormat df = new DecimalFormat("######0.00");
                Double mvalue = Double.valueOf(consumePrice);
                mvalue = mvalue / 100;
                String sconsumePrice = df.format(mvalue).toString();
                consumePrice=Double.valueOf(sconsumePrice);
            } else {
                return false;
            }
			if(type!=1){
                   String oldOrderNo=orderNo.substring(0, 18);//截取商户号
				   orderMerId=Long.parseLong(oldOrderNo);
				   clientType=StringUtil.stringToInteger(paras[4]);
                pkgId = StringUtil.stringToInteger(paras[5]);
//                userId = StringUtil.stringToLong(paras[6]);
                inviteCode=StringUtil.nullToString(paras[6]);
            }
			System.out.println("type="+type+"outTradeNo="+outTradeNo+"employeeNumber="+employeeNumber+"appType"+appType+"tradeNo="+tradeNo+"clientType"+clientType+"payDate"+payDate + ", consumePrice:" + consumePrice);
			BusinessUtil.writeLog("alipay","时间="+DateUtil.getNowYYYYMMDDHHMMSS()+",进入支付宝业务回调之前,支付订单号="+outTradeNo);
			if(type==1){
                String oldOrderNo=orderNo;
				//根据orderNo查询orderId
				orderNo=orderNo.substring(0, 20);//截取商户号
				//orderMerId=Long.parseLong(orderNo);
				Long orderId=AppOrderSvrManager.getNewOrderService().getOrderIdByOrderNo(orderNo);

//				int result = AppOrderSvrManager.getNewOrderService().finishAliPayOrder(orderId,tradeNo);
//				if (result > 0) {
//		            // 返回
//					return true;
//				}
                Double totlaFree1 = Double.parseDouble(totalFee);
                Boolean fBoolean=wrapSendMQ(orderId, tradeNo,payDate,buyerNo, consumePrice, totlaFree1, oldOrderNo, appType);
                BusinessUtil.writeLog("alipay","时间="+DateUtil.getNowYYYYMMDDHHMMSS()+",进入支付宝业务回调结束,业务类型="+type+",支付订单号="+outTradeNo+",业务处理结果"+fBoolean+ ",金额：" + totalFee);
                return fBoolean;
			}else if(type==2){
				String payNo = "alipayiii" + outTradeNo;
				Map<String,Object> param= new HashMap<String,Object>(3);
				param.put("tradeNo", tradeNo);
				param.put("clientType", clientType);
				param.put("openTime", payDate);
				param.put("buyerNo", buyerNo);
				param.put("buyConfirm", 1);
				param.put("innerTradeNo", orderNo);
				param.put("inviteCode", inviteCode);
				jsonobject=AppOrderSvrManager.getMyMerchantService().increaseEmployeeNumApply(pkgId, appType, orderMerId, employeeNumber, totalFee, 2, payNo, "1", param);
				BusinessUtil.writeLog("alipay","时间="+DateUtil.getNowYYYYMMDDHHMMSS()+",进入支付宝业务回调结束,业务类型="+type+",支付订单号="+outTradeNo+",业务处理结果"+jsonobject+ ",金额：" + totalFee);				
				if (jsonobject.getString("resultCode").equals("000")) {
					return true;
				}
			}else if(type==3){
				Map<String, Object> requestParamMap = new HashMap<String, Object>();
				requestParamMap.put("merchantId", orderMerId);
				requestParamMap.put("appType", appType);
				requestParamMap.put("money", totalFee);
		        requestParamMap.put("applyStatus", 2);
		        requestParamMap.put("payNo", "alipayiii" + outTradeNo);
		        requestParamMap.put("payType", "1");
		        requestParamMap.put("tradeNo", tradeNo);
		        requestParamMap.put("clientType", clientType);
		        requestParamMap.put("openTime", payDate);
		        requestParamMap.put("buyerNo", buyerNo);
		        requestParamMap.put("buyConfirm", 1);
		        requestParamMap.put("innerTradeNo", orderNo);
		        requestParamMap.put("inviteCode", inviteCode);
		        jsonobject=AppOrderSvrManager.getMyIncomeService().topupApply(requestParamMap);
		        BusinessUtil.writeLog("alipay","时间="+DateUtil.getNowYYYYMMDDHHMMSS()+",进入支付宝业务回调结束,业务类型="+type+",支付订单号="+outTradeNo+",业务处理结果"+jsonobject+ ",金额：" + totalFee);		        
		        if (jsonobject.getString("resultCode").equals("000")) {
					return true;
				}
//			}else if(type==4){
//				String payNo = "alipayiii" + outTradeNo;
//				Map<String, Object> requestParamMap = new HashMap<String, Object>();
//				requestParamMap.put("clientType", clientType);
//				jsonobject=AppOrderSvrManager.getMyMerchantService().vipApply(appType, orderMerId, totalFee, 2, payNo, "1", tradeNo, requestParamMap);
//				BusinessUtil.writeLog("alipay","时间="+DateUtil.getNowYYYYMMDDHHMMSS()+",进入支付宝业务回调结束,业务类型="+type+",支付订单号="+outTradeNo+",业务处理结果"+jsonobject);
//				if (jsonobject.getString("resultCode").equals("000")) {
//					return true;
//				}
            } else if (4 == type || 5 == type || 6 == type) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("pkgId", pkgId);
//                map.put("userId", userId);
                map.put("merchantId", orderMerId);
                map.put("payType", 1); // 1-支付宝支付 2-微信支付 3-现金支付
                map.put("tradeAmount", totalFee); // 金额
                map.put("orderNo", outTradeNo);
                map.put("tradeNo", tradeNo);
				map.put("payTime", payDate);
				map.put("buyerNo", buyerNo);
				map.put("buyConfirm", 1);
				map.put("innerTradeNo",orderNo);
				map.put("inviteCode", inviteCode);
				jsonobject = AppOrderSvrManager.getMyMerchantService().openIncreaseService(map);
                BusinessUtil.writeLog("alipay", "时间=" + DateUtil.getNowYYYYMMDDHHMMSS() + ", 进入支付宝业务回调结束,业务类型="+type+",支付订单号="+outTradeNo+",业务处理结果"+jsonobject + ",金额：" + totalFee);
                if (jsonobject.getString("resultCode").equals("000")) {
                    return true;
                }
            } else if (20 == type) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("userId", orderMerId);
                map.put("orderNo", orderNo);
                map.put("bizNo", StringUtil.null2Str(pkgId));
                map.put("inviteCode", inviteCode);
                BigDecimal   btotalFee   =   new   BigDecimal(totalFee);
                btotalFee.setScale(2,   BigDecimal.ROUND_HALF_UP);
                map.put("payAmount", btotalFee);
                map.put("orderAmount", btotalFee);
                map.put("payType", 2);
                //创建时间
                map.put("createTime", new Date());
                map.put("modifyTime", new Date());
                map.put("creator", "");
                map.put("modifier", "");
                map.put("bizType", "KING_ACT");
                map.put("payTime", DateUtil.convertStringToDate(DateUtil.DATE_TIME_NO_SPACE_PATTERN,payDate));
                map.put("status", 2);
                map.put("remark", "");
               jsonobject = AppOrderSvrManager.getNewOrderService().buyKingPlan(map);
                BusinessUtil.writeLog("alipay", "时间=" + DateUtil.getNowYYYYMMDDHHMMSS() + ", 进入支付宝业务回调结束,业务类型="+type+",支付订单号="+outTradeNo+",业务处理结果"+jsonobject + ",金额：" + totalFee);
                if (jsonobject.getString("resultCode").equals("000")) {
                    return true;
                }
            }
        }catch (Exception e) {
			e.printStackTrace();
			BusinessUtil.writeLog("alipay","时间="+DateUtil.getNowYYYYMMDDHHMMSS()+",进入支付宝业务回调结束异常,业务类型="+type+",支付订单号="+outTradeNo+",业务处理失败"+e.getMessage());
			return false;
		}
		return flag;
	}
	
	public  static void main(String[] args){
		String str="1iii14706378815119964916iii0iii0";
		splitStr(str);
	}

	public  static Boolean  splitStr(String outTradeNo){	
		String paras[]=outTradeNo.split("iii");
		Integer type=0;
		Integer employeeNumber=0;
		Boolean flag=false;
		String appType="";
        Double consumePrice = 0.0;
			// 检查参数
			if (paras[0] != null) {
				type = Integer.parseInt(StringUtil.null2Str(paras[0]));
			} else {
				return false;
			}
			if (paras[1] != null) {
				outTradeNo = StringUtil.null2Str(paras[1]);
			} else {
				return false;
			}
			if (paras[2] != null) {
				employeeNumber =Integer.parseInt(StringUtil.null2Str(paras[2]));
			} else {
				return false;
			}
			if (paras[3] != null) {
				appType =StringUtil.null2Str(paras[3]);
			} else {
				return false;
			}
            if (paras[4] != null) {
                consumePrice = StringUtil.nullToDouble(paras[4]);
            } else {
                return false;
            }
			System.out.println("type="+type+"outTradeNo="+outTradeNo+"employeeNumber="+employeeNumber+"appType"+appType + ",consumePrice:" + consumePrice);
			return true;
	}


    private static boolean wrapSendMQ(Long orderId, String tradeNo,String payDate,String buyerNo, Double consumePrice, Double totalFee, String outTradeNo, String appType) throws Exception {
        int result = 0;
        JSONObject jsonObject = null;
        try {
            if ("0".equals(appType)) {
                jsonObject = AppOrderSvrManager.getNewOrderService().finishAliPayOrder(orderId, tradeNo, payDate, buyerNo);
            } else {
                jsonObject = AppOrderSvrManager.getNewOrderService().finishAliPayOrderV1110(orderId, tradeNo, payDate, buyerNo, consumePrice, totalFee, outTradeNo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (null != jsonObject.get("result")) {
            result = Integer.parseInt(String.valueOf(jsonObject.get("result")));
        }

        if (result > 0) {
            sendMQ(jsonObject, orderId);
            return true;
        } else {
            return false;
        }
    }

}

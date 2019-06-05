package com.unionpay;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.DateUtil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.controller.AppOrderSvrManager;
import com.shanjin.service.IMyIncomeService;
import com.shanjin.service.IMyMerchantService;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import static com.shanjin.common.MQTools.sendMQ;

public class UnionPayService {
	@Reference
	private static IMyMerchantService myMerchantService;
	@Reference
	private static IMyIncomeService myIncomeService;
	private static String resXml = "";

	/**
	 * description: 解析微信回调方法入口
	 * 
	 * @return
	 * @see
	 */
	public static boolean totalUnionPay(Map<String, String> valideData)  {
			BusinessUtil.writeLog("unionpay","时间="+DateUtil.getNowYYYYMMDDHHMMSS()+",进入银联支付回调开始,参数="+valideData);
			
			String totalFee = valideData.get("txnAmt");
			double dtotalFee=Double.parseDouble(totalFee);
			//保留2位小数
			totalFee=new DecimalFormat("0.00").format(dtotalFee/100);
			Integer clientType=0;
			String tradeNo = valideData.get("queryId");
			String payDate =valideData.get("txnTime");
			String outTradeNo=valideData.get("reqReserved");
			String[] paras = outTradeNo.split("iii");
			BusinessUtil.writeLog("unionpay","时间="+DateUtil.getNowYYYYMMDDHHMMSS()+",进入银联业务回调开始,支付订单号="+outTradeNo);
			Integer type=0;
			Integer employeeNumber=0;
			Boolean flag=false;
			String appType="";
			Long orderMerId=0l;
			JSONObject jsonobject=null;
			String orderNo="";
	        int pkgId = 0;
	//		        Long userId = 0L;
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
				if(type!=1){
					   orderNo=orderNo.substring(0, 18); //截取商户号
					   orderMerId=Long.parseLong(orderNo);
					   clientType=StringUtil.stringToInteger(paras[4]);
	                pkgId = StringUtil.stringToInteger(paras[5]);
	//		                userId = StringUtil.stringToLong(paras[6]);
	            }
				System.out.println("type="+type+"outTradeNo="+outTradeNo+"employeeNumber="+employeeNumber+"appType"+appType+"tradeNo="+tradeNo+"clientType"+clientType+"payDate"+payDate);
				BusinessUtil.writeLog("unionpay","时间="+DateUtil.getNowYYYYMMDDHHMMSS()+",进入银联业务回调之前,支付订单号="+outTradeNo);
				if(type==1){
					//根据orderNo查询orderId
					Long orderId=AppOrderSvrManager.getNewOrderService().getOrderIdByOrderNo(orderNo);
	
	//						int result = AppOrderSvrManager.getNewOrderService().finishAliPayOrder(orderId,tradeNo);
	//						if (result > 0) {
	//				            // 返回
	//							return true;
	//						}
	                Boolean fBoolean=wrapSendMQ(orderId, tradeNo,payDate);
	                BusinessUtil.writeLog("unionpay","时间="+DateUtil.getNowYYYYMMDDHHMMSS()+",进入银联业务回调结束,业务类型="+type+",支付订单号="+outTradeNo+",业务处理结果"+jsonobject);
	                return fBoolean;
				}else if(type==2){
					String payNo = "unionpayiii" + outTradeNo;
					Map<String,Object> param= new HashMap<String,Object>(3);
					param.put("tradeNo", tradeNo);
					param.put("clientType", clientType);
					param.put("openTime", payDate);
					jsonobject=AppOrderSvrManager.getMyMerchantService().increaseEmployeeNumApply(pkgId, appType, orderMerId, employeeNumber, totalFee, 2, payNo, "5", param);
					BusinessUtil.writeLog("unionpay","时间="+DateUtil.getNowYYYYMMDDHHMMSS()+",进入银联业务回调结束,业务类型="+type+",支付订单号="+outTradeNo+",业务处理结果"+jsonobject);				
					if (jsonobject.getString("resultCode").equals("000")) {
						return true;
					}
				}else if(type==3){
					Map<String, Object> requestParamMap = new HashMap<String, Object>();
					requestParamMap.put("merchantId", orderMerId);
					requestParamMap.put("appType", appType);
					requestParamMap.put("money", totalFee);
			        requestParamMap.put("applyStatus", 2);
			        requestParamMap.put("payNo", "unionpayiii" + outTradeNo);
			        requestParamMap.put("payType", "5"); // 1-支付宝支付 2-微信支付 3-现金支付5:银联支付
			        requestParamMap.put("tradeNo", tradeNo);
			        requestParamMap.put("clientType", clientType);
			        requestParamMap.put("openTime", payDate);
			        jsonobject=AppOrderSvrManager.getMyIncomeService().topupApply(requestParamMap);
			        BusinessUtil.writeLog("unionpay","时间="+DateUtil.getNowYYYYMMDDHHMMSS()+",进入银联业务回调结束,业务类型="+type+",支付订单号="+outTradeNo+",业务处理结果"+jsonobject);		        
			        if (jsonobject.getString("resultCode").equals("000")) {
						return true;
					}
	//					}else if(type==4){
	//						String payNo = "alipayiii" + outTradeNo;
	//						Map<String, Object> requestParamMap = new HashMap<String, Object>();
	//						requestParamMap.put("clientType", clientType);
	//						jsonobject=AppOrderSvrManager.getMyMerchantService().vipApply(appType, orderMerId, totalFee, 2, payNo, "1", tradeNo, requestParamMap);
	//						BusinessUtil.writeLog("alipay","时间="+DateUtil.getNowYYYYMMDDHHMMSS()+",进入支付宝业务回调结束,业务类型="+type+",支付订单号="+outTradeNo+",业务处理结果"+jsonobject);
	//						if (jsonobject.getString("resultCode").equals("000")) {
	//							return true;
	//						}
	            } else if (4 == type || 5 == type || 6 == type) {
	                Map<String, Object> map = new HashMap<String, Object>();
	                map.put("pkgId", pkgId);
	//		                map.put("userId", userId);
	                map.put("merchantId", orderMerId);
	                map.put("payType", 5); // 1-支付宝支付 2-微信支付 3-现金支付5:银联支付
	                map.put("tradeAmount", totalFee); // 金额
	                map.put("orderNo", outTradeNo);
	                map.put("tradeNo", tradeNo);
					map.put("payTime", payDate);
					jsonobject = AppOrderSvrManager.getMyMerchantService().openIncreaseService(map);
					BusinessUtil.writeLog("unionpay","时间="+DateUtil.getNowYYYYMMDDHHMMSS()+",进入银联业务回调结束,业务类型="+type+",支付订单号="+outTradeNo+",业务处理结果"+jsonobject + ",金额：" + totalFee);
	                if (jsonobject.getString("resultCode").equals("000")) {
	                    return true;
	                }
	            }
	        }catch (Exception e) {
				e.printStackTrace();
				BusinessUtil.writeLog("unionpay","时间="+DateUtil.getNowYYYYMMDDHHMMSS()+",进入银联业务回调结束失败,业务类型="+type+",支付订单号="+outTradeNo+",业务处理结果"+e.getMessage());
				return false;
			}
			return flag;
	}
		
		  private static boolean wrapSendMQ(Long orderId, String tradeNo,String payDate) throws Exception {
		        int result = 0;
		        JSONObject jsonObject = null;
		        try {
		            jsonObject = AppOrderSvrManager.getNewOrderService().finishUnionOrder(orderId, tradeNo, payDate);
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


	public IMyMerchantService getMyMerchantService() {
		return myMerchantService;
	}

	public void setMyMerchantService(IMyMerchantService myMerchantService) {
		UnionPayService.myMerchantService = myMerchantService;
	}

	public IMyIncomeService getMyIncomeService() {
		return myIncomeService;
	}

	public void setMyIncomeService(IMyIncomeService myIncomeService) {
		UnionPayService.myIncomeService = myIncomeService;
	}
}

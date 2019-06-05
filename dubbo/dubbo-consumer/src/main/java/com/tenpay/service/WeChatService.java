package com.tenpay.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.DateUtil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.controller.AppOrderSvrManager;
import com.shanjin.service.ICustomOrderService;
import com.shanjin.service.IMyIncomeService;
import com.shanjin.service.IMyMerchantService;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedOutputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.shanjin.common.MQTools.sendMQ;

public class WeChatService {
	@Reference
	private static IMyMerchantService myMerchantService;
	@Reference
	private static IMyIncomeService myIncomeService;
	private static String resXml = "";

    @Reference
    private static ICustomOrderService customOrderService;

	/**
	 * description: 解析微信回调方法入口
	 * 
	 * @return
	 * @see
	 */
	public static boolean totalWechat(HttpServletRequest request,
			HttpServletResponse response,String payType) throws Exception {
		try {
			Map m = getXmlMap(request, response);
			if("2".equals(payType)){
				BusinessUtil.writeLog("wxpay","时间="+DateUtil.getNowYYYYMMDDHHMMSS()+",进入微信支付回调开始,参数="+m);
			}else if("6".equals(payType)){
				BusinessUtil.writeLog("wxpayh5","时间="+DateUtil.getNowYYYYMMDDHHMMSS()+",进入微信支付回调开始,参数="+m);
			}else if("7".equals(payType)){
                BusinessUtil.writeLog("wxpaykingh5","时间="+DateUtil.getNowYYYYMMDDHHMMSS()+",进入微信支付回调开始,参数="+m);
            }
			String[] result = getOutTradeNo(m);
			String totalFee = getMoney(m);
			Integer clientType=0;
            String inviteCode="";
			if (StringUtil.isNull(result[0])) {
				resXml = "<xml>"
						+ "<return_code><![CDATA[FAIL]]></return_code>"
						+ "<return_msg><![CDATA[报文为空]]></return_msg>"
						+ "</xml> ";
				if("2".equals(payType)){
					BusinessUtil.writeLog("wxpay","时间="+DateUtil.getNowYYYYMMDDHHMMSS()+",进入微信支付回调获取微信回调报文为空="+m);
					}else if("6".equals(payType)){
						BusinessUtil.writeLog("wxpayh5","时间="+DateUtil.getNowYYYYMMDDHHMMSS()+",进入微信支付回调获取微信回调报文为空="+m);
					}else if("7".equals(payType)){
                    BusinessUtil.writeLog("wxpaykingh5","时间="+DateUtil.getNowYYYYMMDDHHMMSS()+",进入微信支付回调获取微信回调报文为空="+m);
                }
				BufferedOutputStream out = new BufferedOutputStream(
						response.getOutputStream());
				out.write(resXml.getBytes());
				out.flush();
				out.close();
				return true;
			} else {
				System.out.println("outTradeNo:" + result[0]);
				String attach = result[0];
				String tradeNo = result[1];
				String endTime = result[2];
				String outTradeNo=result[3];
				String openId=result[4];
				String[] paras = attach.split("iii");
				Integer type = 0;
				Long orderMerId = 0L;
                int pkgId = 0;
                Double consumePrice = 0.0;
                Long userId = 0L;
				Integer increaseEmployeeNum = 0;
				String appType = "";
				String orderNo="";
				String olderOrderNo="";
				if (paras[0] != null) {
					type = Integer.parseInt(StringUtil.null2Str(paras[0]));
				} else {
					return false;
				}
				// 检查参数
				if (paras[1] != null) {
					orderNo =StringUtil.null2Str(paras[1]);
				} else {
					return false;
				}
				if (paras[2] != null) {
					increaseEmployeeNum = Integer.parseInt(StringUtil
							.null2Str(paras[2]));
				} else {
					return false;
				}
				if (paras[3] != null) {
					appType = StringUtil.null2Str(paras[3]);
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
				if(type!=1) {
					olderOrderNo=orderNo.substring(0, 18);//截取商户号
                    orderMerId=Long.parseLong(olderOrderNo);
                    clientType = StringUtil.stringToInteger(paras[4]);
                    pkgId = StringUtil.stringToInteger(paras[5]);
                //    userId = StringUtil.stringToLong(paras[6]);
                    inviteCode=StringUtil.nullToString(paras[6]);
				}
				System.out.println("type="+type+"outTradeNo"+outTradeNo+"orderMerId="+orderMerId+"increaseEmployeeNum="+increaseEmployeeNum+"appType"+appType+"tradeNo="+tradeNo+"clientType="+clientType + ", consumePrice:" + consumePrice);
				if (type == 1) {
                    orderNo=orderNo.substring(0, 20);//截取商户号
                    //orderMerId=Long.parseLong(orderNo);
					Long orderId=AppOrderSvrManager.getNewOrderService().getOrderIdByOrderNo(orderNo);
					Double totalFee1 = Double.parseDouble(totalFee);
					resXml = weChatOrder(orderId, tradeNo,endTime,openId, consumePrice, totalFee1,outTradeNo, appType);
					System.out.println("resXml1"+resXml);
				} else if (type == 2) {
					resXml = increaseEmployeeNumApply(pkgId, outTradeNo, orderMerId,
							increaseEmployeeNum, appType, totalFee,tradeNo,clientType,endTime,payType,openId,inviteCode,orderNo);
					System.out.println("resXml2"+resXml);
				} else if (type == 3) {
					resXml = topupApply(outTradeNo, orderMerId, totalFee,
							tradeNo, appType,clientType,endTime,payType,openId,inviteCode,orderNo);
					System.out.println("resXml3"+resXml);
//				} else if (type == 4) {
//					resXml = vipApply(outTradeNo, orderMerId, totalFee,
//							tradeNo, appType,clientType);
//					System.out.println("resXml4"+resXml);
                } else if (4 == type || 5 == type || 6 == type) {
                    resXml = increaseService(pkgId, userId, orderMerId, totalFee, outTradeNo, tradeNo, endTime,payType,openId,inviteCode,orderNo);
                    System.out.println("resXml 4、5、6：" + resXml);
                } else if (20 == type) {
                    resXml = buyKingPlan(pkgId, userId, orderMerId, totalFee, outTradeNo, tradeNo, endTime, payType, inviteCode);
                    System.out.println("resXml 20：" + resXml);
                }
                BusinessUtil.writeLog("wxpay","时间="+DateUtil.getNowYYYYMMDDHHMMSS()+",进入微信支付回调结束,业务类型="+type+",支付订单号="+outTradeNo+",业务处理结果"+resXml);
				BufferedOutputStream out = new BufferedOutputStream(
						response.getOutputStream());
				out.write(resXml.getBytes());
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			BusinessUtil.writeLog("wxpay","时间="+DateUtil.getNowYYYYMMDDHHMMSS()+",进入微信支付回调失败,异常信息="+e.getMessage()+",业务处理结果=false");
			return false;
		}
		return true;
	}

	public static String weChatOrder(Long orderMerId,  String tradeNo,String endTime,String openId, Double consumePrice, Double totalFee,String outTradeNo, String appType) throws Exception {
        JSONObject jsonObject = null;
        int result = 0;
        String resXml = "";
        try {
            if ("0".equals(appType)) {
                jsonObject = AppOrderSvrManager.getNewOrderService().finishWeChatOrder(orderMerId, tradeNo,endTime,openId);
            } else {
                jsonObject = AppOrderSvrManager.getNewOrderService().finishWeChatOrderV1110(orderMerId, tradeNo,endTime,openId, consumePrice, totalFee,outTradeNo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (null != jsonObject.get("result")) {
            result = Integer.parseInt(String.valueOf(jsonObject.get("result")));
        }

        if (result > 0) {
            resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>" + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
            sendMQ(jsonObject, orderMerId);
        }
        return resXml;
	}

	public static String vipApply(String outTradeNo, Long orderMerId,
			String totalFee, String tradeNo, String appType,Integer clientType) {
		try {
			String payNo = "weChatiii" + outTradeNo;
			Map<String, Object> parms= new HashMap<String, Object>();
			parms.put("clientType", clientType);
			JSONObject jsonobject = AppOrderSvrManager.getMyMerchantService().vipApply(appType, orderMerId, totalFee, 2, payNo, "2", tradeNo, parms);
			resXml = "";
			if (jsonobject.getString("resultCode").equals("000")) {
				resXml = "<xml>"
						+ "<return_code><![CDATA[SUCCESS]]></return_code>"
						+ "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
			}
		} catch (Exception e) {
			return "";
		}
		return resXml;
	}

	public static String topupApply(String outTradeNo, Long orderMerId,
			String totalFee, String tradeNo, String appType,Integer clientType,String endTime,String payType,String buyerNo,String inviteCode,String orderNo)  {
		try {
			Map<String, Object> requestParamMap = new HashMap<String, Object>();
			requestParamMap.put("merchantId", orderMerId);
			requestParamMap.put("appType", appType);
			requestParamMap.put("money", totalFee);
			requestParamMap.put("applyStatus", 2);
			requestParamMap.put("payNo", "weChatiii" + outTradeNo);
			requestParamMap.put("payType", payType);
			requestParamMap.put("tradeNo", tradeNo);
			requestParamMap.put("clientType", clientType);
			requestParamMap.put("openTime", endTime);
			requestParamMap.put("buyerNo", buyerNo);
			requestParamMap.put("buyConfirm", 1);
			requestParamMap.put("innerTradeNo", orderNo);
			requestParamMap.put("inviteCode", inviteCode);
			JSONObject jsonobject = AppOrderSvrManager.getMyIncomeService().topupApply(requestParamMap);
			if (jsonobject.getString("resultCode").equals("000")) {
				resXml = "<xml>"
						+ "<return_code><![CDATA[SUCCESS]]></return_code>"
						+ "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
			}
		} catch (Exception e) {
			return "";
		}
		return resXml;
	}

	public static String increaseEmployeeNumApply(int pkgId, String outTradeNo,
			Long orderMerId, Integer increaseEmployeeNum, String appType,
			String totalFee,String tradeNo,Integer clientType,String endTime,String payType,String buyerNo,String inviteCode,String orderNo) {
		try {
			System.out.println("increaseEmployeeNumApplyjsonobject进入"+"outTradeNo="+outTradeNo+"orderMerId="+orderMerId+"increaseEmployeeNum="+increaseEmployeeNum+"appType"+appType);
			String payNo = "weChatiii" + outTradeNo;
			System.out.println("payNo="+payNo);
			Map<String,Object> param=new HashMap<String,Object>(3);
			param.put("tradeNo", tradeNo);
			param.put("clientType", clientType);
			param.put("openTime", endTime);
			param.put("buyerNo", buyerNo);
			param.put("buyConfirm", 1);
			param.put("innerTradeNo", orderNo);
			param.put("inviteCode", inviteCode);
			JSONObject jsonobject = AppOrderSvrManager.getMyMerchantService().increaseEmployeeNumApply(pkgId, appType, orderMerId, increaseEmployeeNum, totalFee, 2, payNo, payType, param);
			System.out.println("increaseEmployeeNumApplyjsonobject结束"+jsonobject);
			if (jsonobject.getString("resultCode").equals("000")) {
				resXml = "<xml>"
						+ "<return_code><![CDATA[SUCCESS]]></return_code>"
						+ "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
			}
		} catch (Exception e) {
			return "";
		}
		return resXml;
	}

    public static String increaseService(int pkgId, Long userId, Long orderMerId, String totalFee, String outTradeNo, String tradeNo, String payDate,String payType,String buyerNo,String inviteCode,String orderNo) {
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("pkgId", pkgId);
            map.put("userId", userId);
            map.put("merchantId", orderMerId);
            map.put("payType", payType); // 1-支付宝支付 2-微信支付 3-现金支付4:免单5:银联支付6:h5微信支付
            map.put("tradeAmount", totalFee); // 金额
            map.put("orderNo", outTradeNo);
            map.put("tradeNo", tradeNo);
			map.put("payTime", payDate);
			map.put("buyerNo", buyerNo);
			map.put("buyConfirm", 1);
			map.put("inviteCode", inviteCode);
			map.put("innerTradeNo", orderNo);
            JSONObject jsonobject = AppOrderSvrManager.getMyMerchantService().openIncreaseService(map);
            if ("000".equals(jsonobject.getString("resultCode"))) {
                resXml = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg>" + "</xml>";
            }
        } catch (Exception e) {
            return "";
        }
        return resXml;
    }

	/**
	 * description: 解析微信通知xml
	 * 
	 * @param xml
	 * @return
	 * @see
	 */
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	private static Map parseXmlToList(String xml) {
		Map retMap = new HashMap();
		try {
			StringReader read = new StringReader(xml);
			// 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
			InputSource source = new InputSource(read);
			// 创建一个新的SAXBuilder
			SAXBuilder sb = new SAXBuilder();
			// 通过输入源构造一个Document
			Document doc = (Document) sb.build(source);
			Element root = doc.getRootElement();// 指向根节点
			List<Element> es = root.getChildren();
			if (es != null && es.size() != 0) {
				for (Element element : es) {
					retMap.put(element.getName(), element.getValue());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retMap;
	}

	private static Map getXmlMap(HttpServletRequest request,
			HttpServletResponse response) {
		String outTradeNo = null;
		// 示例报文
		String inputLine;
		String notityXml = "";
		try {
			while ((inputLine = request.getReader().readLine()) != null) {
				notityXml += inputLine;
			}
			request.getReader().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("接收到的报文：" + notityXml);
		Map m = parseXmlToList(notityXml);
		return m;
	}

	private static String[] getOutTradeNo(Map m) {
		String[] result = new String[5];
		if ("SUCCESS".equals(m.get("result_code").toString())) {
			result[0] = m.get("attach").toString();
			result[1] = m.get("transaction_id").toString();
			result[2]=m.get("time_end").toString();
			result[3]=m.get("out_trade_no").toString();
			result[4]=m.get("openid").toString();
		}
		return result;
	}

	private static String getMoney(Map m) {
		String money = null;

		System.out.println("TotalFee:" + m.get("total_fee").toString());

		if ("SUCCESS".equals(m.get("result_code").toString())) {
			money = m.get("total_fee").toString();
			DecimalFormat df = new DecimalFormat("######0.00");
			Double mvalue = Double.valueOf(money);
			mvalue = mvalue / 100;
			money = df.format(mvalue).toString();
		}
		return money;
	}

	public IMyMerchantService getMyMerchantService() {
		return myMerchantService;
	}

	public void setMyMerchantService(IMyMerchantService myMerchantService) {
		WeChatService.myMerchantService = myMerchantService;
	}

	public IMyIncomeService getMyIncomeService() {
		return myIncomeService;
	}

	public void setMyIncomeService(IMyIncomeService myIncomeService) {
		WeChatService.myIncomeService = myIncomeService;
	}

    private static String buyKingPlan(int pkgId, Long userId, Long orderMerId, String totalFee, String outTradeNo, String tradeNo, String payDate,String payType,String inviteCode) {
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("userId", orderMerId);
            map.put("orderNo", outTradeNo);
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
            JSONObject jsonobject = AppOrderSvrManager.getNewOrderService().buyKingPlan(map);
            if ("000".equals(jsonobject.getString("resultCode"))) {
                resXml = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
            }
        } catch (Exception e) {
            return "";
        }
        return resXml;
    }

}

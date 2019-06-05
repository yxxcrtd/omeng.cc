package com.tenpay.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.util.StringUtil;
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
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeChartMerchantInfoService {
	@Reference
	private static IMyMerchantService myMerchantService;
	@Reference
	private static IMyIncomeService myIncomeService;

	public static boolean vipApply(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String resXml = "";
		Map m = getXmlMap(request, response);
		String outTradeNo = getOutTradeNo(m);
		String money = getMoney(m);

		if (StringUtil.isNull(outTradeNo)) {
			resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
			BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
			out.write(resXml.getBytes());
			out.flush();
			out.close();
			return true;
		} else {
			System.out.println("outTradeNo:" + outTradeNo);
			String[] paras = outTradeNo.split("iii");
			String payNo = "weChatiii" + outTradeNo;
			Long merchantId = 0L;
			String appType = "";
            String tradeNo = paras[4];
            // 检查参数
			if (paras[0] != null) {
				merchantId = Long.parseLong(StringUtil.null2Str(paras[0]));
			} else {
				return false;
			}
			if (paras[1] != null) {
				appType = StringUtil.null2Str(paras[1]);
			} else {
				return false;
			}
			JSONObject jsonobject = myMerchantService.vipApply(appType, merchantId, money, 2, payNo, "2", tradeNo,null);
			if (jsonobject.getString("resultCode").equals("000")) {
				resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>" + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
			}
			BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
			out.write(resXml.getBytes());
			out.flush();
			out.close();
			return true;
		}
	}

	public static boolean topupApply(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String resXml = "";
		Map m = getXmlMap(request, response);
		String outTradeNo = getOutTradeNo(m);
		String money = getMoney(m);
		if (StringUtil.isNull(outTradeNo)) {
			resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
			BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
			out.write(resXml.getBytes());
			out.flush();
			out.close();
			return true;
		} else {
			System.out.println("outTradeNo:" + outTradeNo);
			String[] paras = outTradeNo.split("iii");
			Long merchantId = 0L;
			String appType = "";
			// 检查参数
			if (paras[0] != null) {
				merchantId = Long.parseLong(StringUtil.null2Str(paras[0]));
			} else {
				return false;
			}
			if (paras[1] != null) {
				appType = StringUtil.null2Str(paras[1]);
			} else {
				return false;
			}

			Map<String, Object> requestParamMap = new HashMap<String, Object>();
			requestParamMap.put("merchantId", merchantId);
			requestParamMap.put("appType", appType);
			requestParamMap.put("money", money);
			requestParamMap.put("applyStatus", 2);
			requestParamMap.put("payNo", "weChatiii" + outTradeNo);
			requestParamMap.put("payType", "2");
			JSONObject jsonobject = myIncomeService.topupApply(requestParamMap);
			if (jsonobject.getString("resultCode").equals("000")) {
				resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>" + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
			}
			BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
			out.write(resXml.getBytes());
			out.flush();
			out.close();
			return true;
		}
	}

	public static boolean increaseEmployeeNumApply(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String resXml = "";
		Map m = getXmlMap(request, response);
		String outTradeNo = getOutTradeNo(m);
		String money = getMoney(m);
        int pkgId = 0;
		if (StringUtil.isNull(outTradeNo)) {
			resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
			BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
			out.write(resXml.getBytes());
			out.flush();
			out.close();
			return true;
		} else {
			System.out.println("outTradeNo:" + outTradeNo);
			String[] paras = outTradeNo.split("iii");
			String payNo = "weChatiii" + outTradeNo;
			Long merchantId = 0L;
			Integer increaseEmployeeNum = 0;
			String appType = "";
			// 检查参数
			if (paras[0] != null) {
				merchantId = Long.parseLong(StringUtil.null2Str(paras[0]));
			} else {
				return false;
			}
			if (paras[1] != null) {
				increaseEmployeeNum = Integer.parseInt(StringUtil.null2Str(paras[1]));
			} else {
				return false;
			}
			if (paras[2] != null) {
				appType = StringUtil.null2Str(paras[2]);
			} else {
				return false;
			}
            pkgId = StringUtil.stringToInteger(paras[5]);

			JSONObject jsonobject = myMerchantService.increaseEmployeeNumApply(pkgId, appType, merchantId, increaseEmployeeNum, money, 2, payNo, "2",null);
			if (jsonobject.getString("resultCode").equals("000")) {
				resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>" + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
			}
			BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
			out.write(resXml.getBytes());
			out.flush();
			out.close();
			return true;
		}
	}

	private static Map getXmlMap(HttpServletRequest request, HttpServletResponse response) {
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
		Map m = parseXmlToList2(notityXml);
		return m;
	}

	private static String getOutTradeNo(Map m) {
		String outTradeNo = null;
		System.out.println("OutTradeNo:" + m.get("attach").toString());

		if ("SUCCESS".equals(m.get("result_code").toString())) {
			outTradeNo = m.get("attach").toString();
		}
		return outTradeNo;
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

	/**
	 * description: 解析微信通知xml
	 * 
	 * @param xml
	 * @return
	 * @author ex_yangxiaoyi
	 * @see
	 */
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	private static Map parseXmlToList2(String xml) {
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

	public IMyMerchantService getMyMerchantService() {
		return myMerchantService;
	}

	public void setMyMerchantService(IMyMerchantService myMerchantService) {
		WeChartMerchantInfoService.myMerchantService = myMerchantService;
	}

	public IMyIncomeService getMyIncomeService() {
		return myIncomeService;
	}

	public void setMyIncomeService(IMyIncomeService myIncomeService) {
		WeChartMerchantInfoService.myIncomeService = myIncomeService;
	}
}

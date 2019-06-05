package com.tenpay.service;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.util.StringUtil;
import com.shanjin.controller.AppOrderSvrManager;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.shanjin.common.MQTools.sendMQ;

public class WeChatOrder {

    // 日志
    private static final Logger logger = Logger.getLogger(WeChatOrder.class);

	public static boolean weChatOrder(HttpServletRequest request, HttpServletResponse response) {
		try {
			String resXml = "";
			String[] weixinReply = getOutTradeNo(request, response);
			if (weixinReply==null || StringUtil.isNull(weixinReply[0])) {
				resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
				BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
				out.write(resXml.getBytes());
				out.flush();
				out.close();
				return true;
			} else {
				String[] paras = weixinReply[0].split("iii");
				Long orderId = 0L;
				String appType = "";
				int result = 0;
				// 检查参数
				if (paras[0] != null) {
					orderId = Long.parseLong(StringUtil.null2Str(paras[0]));
				}
				if (paras[1] != null) {
					appType = StringUtil.null2Str(paras[1]);
				}

				// 调用服务
			    // result = AppOrderSvrManager.getOrderServiceByAppType(appType).finishWeChatOrder(appType, orderId);
				// result = AppOrderSvrManager.getNewOrderService().finishWeChatOrder(orderId,weixinReply[1]);
                resXml = wrapSendMQ(orderId, weixinReply[1]);
				BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
				out.write(resXml.getBytes());
				out.flush();
				out.close();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private static String[] getOutTradeNo(HttpServletRequest request, HttpServletResponse response) {
		String[] result =null;
		
		StringBuffer traceInfo = new StringBuffer(); 
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
		Map m = parseXmlToList2(notityXml);

		traceInfo.append("回调时间:")
			.append(new java.util.Date(System.currentTimeMillis()))
			.append(" o盟内部订单号：").append(m.get("attach").toString())
			.append(" 返回码：").append(m.get("result_code").toString())
			.append(" 微信支付订单号：").append(m.get("transaction_id"));
		
		System.out.println(traceInfo.toString());
		
		System.out.println("OutTradeNo:" + m.get("attach").toString());

		if ("SUCCESS".equals(m.get("result_code").toString())) {
			result = new String[2];
			
			// 支付成功
			outTradeNo = m.get("attach").toString();
			
			result[0]= outTradeNo;
			
			result[1] = (String) m.get("transaction_id");

		}
		return result;
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

    private static String wrapSendMQ(Long orderId, String tradeNo) throws Exception {
        JSONObject jsonObject = null;
        int result = 0;
        String resXml = "";
        try {
            jsonObject = AppOrderSvrManager.getNewOrderService().finishWeChatOrder(orderId, tradeNo,null,null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (null != jsonObject.get("result")) {
            result = Integer.parseInt(String.valueOf(jsonObject.get("result")));
        }

        if (result > 0) {
            resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>" + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
            sendMQ(jsonObject, orderId);
        }
        return resXml;
    }

}

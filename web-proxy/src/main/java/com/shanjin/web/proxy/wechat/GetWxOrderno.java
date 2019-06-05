package com.shanjin.web.proxy.wechat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.shanjin.common.util.BusinessUtil;
import com.shanjin.web.proxy.wechat.http.HttpClientConnectionManager;

public class GetWxOrderno {
	public static DefaultHttpClient httpclient;

	static {
		httpclient = new DefaultHttpClient();
		httpclient = (DefaultHttpClient) HttpClientConnectionManager.getSSLInstance(httpclient);
	}

	/**
	 * description:获取预支付id
	 *
	 * @param url
	 * @param xmlParam
	 * @return
	 * @author ex_yangxiaoyi
	 * @see
	 */
	public static Map<String, Object> getPayNo(String url, String xmlParam) {
		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		HttpPost httpost = HttpClientConnectionManager.getPostMethod(url);
		Map<String,Object> returnMap=new HashMap();
		String returnData="";
		try {
			//httpost.setEntity(new StringEntity(xmlParam, "UTF-8"));
			httpost.setEntity(new StringEntity(xmlParam));
			HttpResponse response = httpclient.execute(httpost);
			String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
			Map map = doXMLParse(jsonStr);
			System.out.println("60getPayNo-jsonStr="+jsonStr);
			BusinessUtil.writeLog("webwxpay","prepay_id输入参数="+xmlParam+"--输出参数"+map);
			
			if (jsonStr.indexOf("FAIL") != -1) {
				returnData=(String) map.get("err_code");
				//重复支付
				if("ORDERPAID".equals(returnData)){
					returnMap.put("returnCode","041");
					returnMap.put("returnData",returnData);
					return returnMap;
				}else{
					//其他错误
					returnMap.put("returnCode","040");
					returnMap.put("returnData",returnData);
				}
				return returnMap;
			}
			returnData =map.get("prepay_id").toString();
			returnMap.put("returnCode","000");
			returnMap.put("returnData",returnData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			returnMap.put("returnCode",020);
		}
		return returnMap;
	}

	/**
	 * description:获取扫码支付连接
	 *
	 * @param url
	 * @param xmlParam
	 * @return
	 * @author ex_yangxiaoyi
	 * @see
	 */
	public static String getCodeUrl(String url, String xmlParam) {
		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		HttpPost httpost = HttpClientConnectionManager.getPostMethod(url);
		String code_url = "";
		try {
			httpost.setEntity(new StringEntity(xmlParam, "UTF-8"));
			HttpResponse response = httpclient.execute(httpost);
			String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
			if (jsonStr.indexOf("FAIL") != -1) {
				return code_url;
			}
			Map map = doXMLParse(jsonStr);
			code_url = (String) map.get("code_url");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return code_url;
	}

	/**
	 * 解析xml,返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据。
	 * 
	 * @param strxml
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static Map doXMLParse(String strxml) throws Exception {
		if (null == strxml || "".equals(strxml)) {
			return null;
		}

		Map m = new HashMap();
		InputStream in = String2Inputstream(strxml);
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(in);
		Element root = doc.getRootElement();
		List list = root.getChildren();
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Element e = (Element) it.next();
			String k = e.getName();
			String v = "";
			List children = e.getChildren();
			if (children.isEmpty()) {
				v = e.getTextNormalize();
			} else {
				v = getChildrenText(children);
			}

			m.put(k, v);
		}

		// 关闭流
		in.close();

		return m;
	}

	/**
	 * 获取子结点的xml
	 * 
	 * @param children
	 * @return String
	 */
	public static String getChildrenText(List children) {
		StringBuffer sb = new StringBuffer();
		if (!children.isEmpty()) {
			Iterator it = children.iterator();
			while (it.hasNext()) {
				Element e = (Element) it.next();
				String name = e.getName();
				String value = e.getTextNormalize();
				List list = e.getChildren();
				sb.append("<" + name + ">");
				if (!list.isEmpty()) {
					sb.append(getChildrenText(list));
				}
				sb.append(value);
				sb.append("</" + name + ">");
			}
		}

		return sb.toString();
	}

	public static InputStream String2Inputstream(String str) {
		return new ByteArrayInputStream(str.getBytes());
	}

}
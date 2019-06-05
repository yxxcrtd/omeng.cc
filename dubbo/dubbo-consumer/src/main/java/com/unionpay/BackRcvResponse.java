package com.unionpay;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.unionpay.util.AcpService;
import com.unionpay.util.LogUtil;
import com.unionpay.util.SDKConfig;
import com.unionpay.util.SDKConstants;




/**
 * 重要：联调测试时请仔细阅读注释！
 * 
 * 产品：手机控件支付产品<br>
 * 功能：后台通知接收处理示例 <br>
 * 日期： 2015-09<br>
 * 版本： 1.0.0 
 * 版权： 中国银联<br>
 * 说明：以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己需要，按照技术文档编写。该代码仅供参考。<br>
 * 交易说明：成功的交易才会发送后台通知，建议此交易与交易状态查询交易结合使用确定交易是否成功
 */
@WebServlet("/backRcvResponse")
public class BackRcvResponse extends HttpServlet{

	@Override
	public void init() throws ServletException {
		/**
		 * 请求银联接入地址，获取证书文件，证书路径等相关参数初始化到SDKConfig类中
		 * 在java main 方式运行时必须每次都执行加载
		 * 如果是在web应用开发里,这个方法可使用监听的方式写入缓存,无须在这出现
		 */
		//这里已经将加载属性文件的方法挪到了web/AutoLoadServlet.java中
		SDKConfig.getConfig().loadPropertiesFromSrc(); //从classpath加载acp_sdk.properties文件
		super.init();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		LogUtil.writeLog("BackRcvResponse接收后台通知开始");

		String encoding = req.getParameter(SDKConstants.param_encoding);
		// 获取银联通知服务器发送的后台通知参数
		Map<String, String> reqParam = getAllRequestParam(req);

		LogUtil.printRequestLog(reqParam);

		Map<String, String> valideData = null;
		if (null != reqParam && !reqParam.isEmpty()) {
			Iterator<Entry<String, String>> it = reqParam.entrySet().iterator();
			valideData = new HashMap<String, String>(reqParam.size());
			while (it.hasNext()) {
				Entry<String, String> e = it.next();
				String key = (String) e.getKey();
				String value = (String) e.getValue();
				value = new String(value.getBytes(encoding), encoding);
				valideData.put(key, value);
			}
		}
        
		//重要！验证签名前不要修改reqParam中的键值对的内容，否则会验签不过
		if (!AcpService.validate(valideData, encoding)) {
			LogUtil.writeLog("验证签名结果[失败].");
			//验签失败，需解决验签问题
			resp.getWriter().print("fail");
		} else {
			LogUtil.writeLog("验证签名结果[成功].");
			//【注：为了安全验签成功才应该写商户的成功处理逻辑】交易成功，更新商户订单状态
			String respCode =valideData.get("respCode"); //获取应答码，收到后台通知了respCode的值一般是00，可以不需要根据这个应答码判断。
		    if(!"00".equals(respCode)){
		    	resp.getWriter().print("fail");
		    }else{
		    	 if(UnionPayService.totalUnionPay(valideData)){
		    		 resp.getWriter().print("ok");
		    	 }
			}
		}
		LogUtil.writeLog("BackRcvResponse接收后台通知结束");
		//返回给银联服务器http 200  状态码
		
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		this.doPost(req, resp);
	}

	/**
	 * 获取请求参数中所有的信息
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, String> getAllRequestParam(final HttpServletRequest request) {
		Map<String, String> res = new HashMap<String, String>();
		Enumeration<?> temp = request.getParameterNames();
		if (null != temp) {
			while (temp.hasMoreElements()) {
				String en = (String) temp.nextElement();
				String value = request.getParameter(en);
				res.put(en, value);
				//在报文上送时，如果字段的值为空，则不上送<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字段>
				//System.out.println("ServletUtil类247行  temp数据的键=="+en+"     值==="+value);
				if (null == res.get(en) || "".equals(res.get(en))) {
					res.remove(en);
				}
			}
		}
		return res;
	}
}

package com.shanjin.web.proxy.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.chinaums.xm.macUtil.Base64;
import com.chinaums.xm.macUtil.DESPlus;
import com.chinaums.xm.macUtil.MACGenerator;
import com.chinaums.xm.macUtil.Translation;
import com.shanjin.web.proxy.unionpay.UnionpayConstants;
import com.shanjin.web.proxy.unionpay.UnionpayHttp;

/**
 * 银联 银行卡三要素实名认证
 * @author xmsheng
 * @creatTime 2016/9/18 10:15
 * @version V1.0
 *
 */
@WebServlet(name = "unionpayVerfyBgService", urlPatterns = { "/comVerifyBg" })
public class UnionpayVerfyBgServlet extends HttpServlet {
	
	private static Logger logger = Logger.getLogger(UnionpayVerfyBgServlet.class);
	
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		PrintWriter pw = null;
		Map<String,String> resutMap = null;
		try {
			String realName = request.getParameter("realName");
			String certNo = request.getParameter("certNo");
			String cardNo = request.getParameter("cardNo");
			String transNo = request.getParameter("transNo");
			logger.info("银联三要素认证,业务参数--> "+"[transNo]="+transNo+",[realName]="+realName+",[certNo]="+certNo+",[cardNo]="+cardNo);
			//组装HTTP请求参数
			String inputDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
			String inputTime = new SimpleDateFormat("hhmmss").format(new Date());
			String unicodeRealName = Translation.toUnicode(realName);
			DESPlus des = new DESPlus(UnionpayConstants.MER_KEY);
			String desCardNo = des.encrypt(cardNo);
			String desCertNo = des.encrypt(certNo);
			String private1 = "";
		    Map<String,String> paramsMap = new HashMap<String,String>();
		    paramsMap.put("transNo", transNo); //交易流水号
		    paramsMap.put("realName", unicodeRealName); //真实姓名
		    paramsMap.put("inputDate", inputDate);//日期
		    paramsMap.put("inputTime",inputTime); //时间
		    paramsMap.put("certNo", desCertNo); //身份证号
		    paramsMap.put("cardNo", desCardNo); //银行卡号
		    paramsMap.put("merId", UnionpayConstants.MERCHANT_ID); //o盟在银联商户号
		    paramsMap.put("private1", private1);//私有域,传空
		    StringBuffer sBuffer = new StringBuffer();
		    sBuffer.append(transNo).append(UnionpayConstants.MERCHANT_ID);
		    sBuffer.append(inputDate).append(inputTime);
		    sBuffer.append(unicodeRealName).append(desCertNo);
		    sBuffer.append(desCardNo).append(private1);
			String reqMac = MACGenerator.getANSIX99MAC(sBuffer.toString(), UnionpayConstants.MER_KEY);
			String chkValue = new String(Base64.encode(reqMac.getBytes()));
			paramsMap.put("chkValue", chkValue);//签名
			String textEntity = UnionpayHttp.httpsClientPost(UnionpayConstants.UNIONPAY_COMVERIFYBG_URL, paramsMap);
			//返回数据签名验证
			resutMap= UnionpayHttp.parseUnionpayVerifyBgResponse(UnionpayConstants.PUB_KEY,  textEntity);
			if(null == resutMap){
				resutMap = new HashMap<String,String>();
				resutMap.put("system_error", "系统繁忙，请稍后重试！");
			}
			pw = response.getWriter();
			pw.print(JSONObject.toJSONString(resutMap));
			pw.flush();			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("银行卡实名认证异常{}",e);
		}finally{
			if(pw !=null){
				pw.close();
			}
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}

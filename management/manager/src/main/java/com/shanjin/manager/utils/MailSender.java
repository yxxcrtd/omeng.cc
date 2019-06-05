package com.shanjin.manager.utils;

import java.util.Map;

import org.apache.log4j.Logger;

import com.shanjin.manager.constant.Constant;

public class MailSender extends Thread{
	public static Logger log = Logger.getLogger(MailSender.class);
	private Map<String,Object>  msg ;
	private String mailTo;
	
	public MailSender(Map<String,Object> emsg, String emailTo){
		msg = emsg;
		mailTo = emailTo;
	}
	public void run(){
		sendMail(msg, mailTo);
	}
	
	private void sendMail(Map<String,Object>  msg,String mailTo){
		try{
			String mailTitle = StringUtil.null2Str(Constant.mailMap.get("mailTitle"));
			String addContent = StringUtil.null2Str(Constant.mailMap.get("addContent"));
			String mailContent = StringUtil.null2Str(Constant.mailMap.get("mailContent"));
			String mailFrom = StringUtil.null2Str(Constant.mailMap.get("mailFrom"));
			String userName = StringUtil.null2Str(Constant.mailMap.get("userName"));
			String mailPwd = StringUtil.null2Str(Constant.mailMap.get("mailPwd"));
			String mailServer = StringUtil.null2Str(Constant.mailMap.get("mailServer"));
			Integer mailPort = StringUtil.nullToInteger(Constant.mailMap.get("mailPort"));
			boolean mailSsl = StringUtil.nullToBoolean(Constant.mailMap.get("mailSsl"));
			String mailTos = StringUtil.null2Str(Constant.mailMap.get("mailTos"));
			int result = 0;
			if(!StringUtil.isNullStr(mailTos)){
				String[] tos = mailTos.split(",");
				if(tos!=null&&tos.length>0){
					for(String to : tos){
						result = EmailUtil.sendMail(mailFrom, to, mailTitle, mailContent+addContent, mailServer, userName, mailPwd, mailPort,mailSsl);
						result++;
					}
				}
			}
			System.out.println("send email result:"+result);
		 }catch (Exception e) {
			log.error("send email failure , to :" + mailTo ,e);
		 }
	}
	

	

}

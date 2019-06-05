package com.shanjin;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.util.StringUtil;
import com.shanjin.other.Util;
import com.shanjin.service.IPushService;


public class MqMsg  {
	
	private RabbitTemplate sendTemplate;
	
	public RabbitTemplate getSendTemplate() {
		return sendTemplate;
	}


	public void setSendTemplate(RabbitTemplate sendTemplate) {
		this.sendTemplate = sendTemplate;
	}


	public void getMQMsg(String msg){
		//1 从新订单对列读取一条订单信息
		Util.writeLog("pushMerchants","从MQ中得到消息："+msg);
		
		if(StringUtil.isEmpty(msg) || msg.equals("{}")){
			System.out.println("msg is null");
			return ;
		}
		JSONObject  jobj=null;		
		
		//2 通过dubbo 调用 provider方法，获取推送信息
		try{
			JSONObject pushParams=JSONObject.parseObject(msg);
		
			if(StringUtil.isEmpty(pushParams) || pushParams.toString().equals("{}")){
				System.out.println("pushParams is null");
				return ;
			}
			
			int pushType=StringUtil.nullToInteger(pushParams.get("pushType"));
			if(pushType==0){
				return ;
			}	
		
			IPushService pushService =(IPushService) (PushMerchantsMain.context).getBean("pushService");	
			if(pushService==null){
				return ;
			}
			
			switch (pushType) {
				case 1://订单推送
					jobj=pushService.orderPush(pushParams);
					break;
				case 8://多终端登录推送
					jobj=pushService.repeatLoginPush(pushParams);
					break;
				case 20://私人助理推送
					jobj=pushService.privateAssistantPush(pushParams);
					break;
				case 200://推送订单给私人助理APP
					jobj=pushService.pushOrderToAssistants(pushParams);
					break;
				default:
					//共推送，一对一推送
					jobj=pushService.commonPush(pushParams);  
					break;
			}	
			if(StringUtil.isEmpty(jobj) || jobj.toString().equals("{}")){
				return ;
			}
			
//			System.out.println("查找的推送商户："+StringUtil.null2Str(jobj)+"\n");
			
			//3。 将推送信息写入推送队列。
			sendTemplate.send(getMsg(StringUtil.null2Str(jobj)));

		}catch(Exception e){
			Util.writeLog("pushMerchants-fail",msg);//推送失败的订单，需要重新推送一次
		}
		
	}
	public Message getMsg(String msg) throws Exception{
		return MessageBuilder.withBody(msg.getBytes("UTF-8"))
		.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN).build();
	}
}

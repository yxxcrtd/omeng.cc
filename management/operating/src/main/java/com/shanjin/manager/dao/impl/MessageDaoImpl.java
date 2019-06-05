package com.shanjin.manager.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.common.util.DateUtil;
import com.shanjin.manager.Bean.Message;
import com.shanjin.manager.constant.Constant.PAGE;
import com.shanjin.manager.constant.Constant.SORT;
import com.shanjin.manager.dao.MessageDao;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

public class MessageDaoImpl implements MessageDao{

	@Override
	public List<Message> getMessageList(Map<String, String[]> param) {
		StringBuffer sql = new StringBuffer();
		List<Message> messageList=new ArrayList<Message>();
//		int package_type = 1; //1:商户版，2：用户版
//		if(StringUtil.isNotNullMap(param,"package_type")){
//			package_type = StringUtil.nullToInteger(param.get("package_type")[0]);
//		}
		sql.append(" SELECT (select name from catalog where alias=mc.app_type and level=0) as app_name,mc.* FROM message_center mc ");
//		if(package_type==1){
//			sql.append(" left join merchant_app_info ai on ml.app_type=ai.app_type ");
//		}else if(package_type==2){
//			sql.append(" left join app_info ai on ml.app_type=ai.app_type ");
//		}
		sql.append(" WHERE mc.is_del=0  ");

		//sql.append(" and ml.package_type =").append(package_type);


		if(StringUtil.isNotNullMap(param,"app_type")){
			String app_type = StringUtil.null2Str(param.get("app_type")[0]);
			sql.append(" and mc.app_type ='").append(app_type).append("'");
		}
//		if(StringUtil.isNotNullMap(param,"is_pub")){
//			int is_pub = StringUtil.nullToInteger(param.get("is_pub")[0]);
//			sql.append(" and ml.is_pub =").append(is_pub);
//		}
		long total=Message.dao.find(sql.toString()).size();
		String property = "id";
		String direction = SORT.DESC;
		if(StringUtil.isNotNullMap(param,"sort")){
			Map<String,String> sortMap = Util.sortMap(StringUtil.null2Str(param.get("sort")[0]));
			if(sortMap!=null){
				if(!StringUtil.isNullStr(sortMap.get("property"))){
					property = StringUtil.null2Str(sortMap.get("property"));
				}
				if(!StringUtil.isNullStr(sortMap.get("direction"))){
					direction = StringUtil.null2Str(sortMap.get("direction"));
				}
			}
		}
		sql.append(" ORDER BY ").append(property).append(" ").append(direction);
		int start = 0;
		if(StringUtil.isNotNullMap(param,"start")){
			start = StringUtil.nullToInteger(param.get("start")[0]);
		}
		int pageSize = PAGE.PAGESIZE;
		if(StringUtil.isNotNullMap(param,"limit")){
			pageSize = StringUtil.nullToInteger(param.get("limit")[0]);
		}
		sql.append(" limit ");
		sql.append(start);
		sql.append(",");
		sql.append(pageSize);
		messageList=Message.dao.find(sql.toString());
		if(messageList.size()>0){
			messageList.get(0).setTotal(total);
		}
		return messageList;
	}


	@Override
	public boolean saveMessage(Map<String, String[]> param) {
		String id = StringUtil.null2Str(param.get("id")[0]);
		boolean isUpdate = false ; 
		if(StringUtil.isNotNullMap(param, "id")){
			isUpdate = true;
		}
		int customer_type = StringUtil.nullToInteger(param.get("customer_type")[0]);
		String province=param.get("province")[0];
		String city=param.get("city")[0];
		if(!StringUtil.isNullStr(province)){
			province=Db.queryStr("select area from area where id="+province);
		}
		if(!StringUtil.isNullStr(city)){
			city=Db.queryStr("select area from area where id="+city);
		}
		String app_type = param.get("app_type")[0];
		int is_vip = StringUtil.nullToInteger(param.get("is_vip")[0]);
		String title=param.get("title")[0];
		String content=param.get("content")[0];
		String send_time = param.get("send_time")[0];
		Date sendTime = null;
		if(StringUtil.isNullStr(send_time)){
			sendTime=null;
		}else{
			sendTime = DateUtil.parseDate(DateUtil.DATE_TIME_PATTERN, send_time);
		}
		int push_type = StringUtil.nullToInteger(param.get("push_type")[0]);
		int send_type = StringUtil.nullToInteger(param.get("send_type")[0]);
		boolean flag = false;
		if(isUpdate){
			// 更新
			 Message.dao.findById(id).set("customer_type", customer_type).set("province", province)
			.set("city", city).set("app_type", app_type).set("is_vip", is_vip)
			.set("title", title).set("content", content).set("send_time",sendTime)
			.set("push_type", push_type).set("send_type",send_type).update();
		}else{
			// 新增
			Message message=new Message();
			message.set("customer_type", customer_type).set("province", province)
			.set("city", city).set("app_type", app_type).set("is_vip", is_vip).set("join_time", new Date())
			.set("title", title).set("content", content).set("send_time",sendTime)
			.set("push_type", push_type).set("send_type",send_type)
			.save();	
		}
		flag = true;
		return flag;
	}

	@Override
	public boolean deleteMessage(String ids) {
		Boolean flag = false;
		String sql = "update message_center set is_del=1 where id IN ("+ids+")";
		Db.update(sql);
		flag = true;
		return flag;
	}

	@Override
	public boolean sendMessage(String id) {
		//:TODO 发送消息具体实现
		boolean flag = false;
		Long msgId = StringUtil.nullToLong(id);
		final Message message =  Message.dao.findById(msgId);
		new Thread(new Runnable() {			
			@Override
			public void run() {
				createUserMessageList(message);
			}
		}).start();
		message.set("is_use", 1).update();
		flag = true;
	    return flag;
	}
	
	/**
	 * 生成用户消息列表
	 * @param msg
	 */
	private void createUserMessageList(Message msg){
		if(msg==null||msg.getLong("id")==null){
			return;
		}
		Long msgId = msg.getLong("id");
		Integer pushType = msg.getInt("push_type");//0：全部用户，1：区域用户，2：指定用户
		Integer customerType = msg.getInt("customer_type");//客户类型：1商户端，2用户端，
		Integer sendType = msg.getInt("send_type");//发送类型（0：不主动通知、只生成消息详情，1：推送通知，2：短信通知）
		Integer isVip = msg.getInt("is_vip");//是否只发送给vip：0所有，1只VIP
		String appType = msg.getStr("app_type");
		String province = msg.getStr("province");
		String city = msg.getStr("city");
		StringBuffer sql = new StringBuffer();
		if(customerType==1){
			//1商户端
			if(pushType==0){
				//全部用户
				sql.append(" SELECT mi.id FROM merchant_info mi WHERE mi.is_del=0 ");
				if(isVip==1){
					sql.append(" AND mi.vip_level=1 ");
				}
				if(!StringUtil.isNullStr(appType)){
					sql.append(" AND mi.app_type='").append(appType).append("'");
				}
				List<Record> list = Db.find(sql.toString());
				if(list!=null&&list.size()>0){
					for(Record r : list){
						Record mr = new Record();
						mr.set("message_id", msgId);
						mr.set("customer_type", customerType);
						mr.set("customer_id", r.getLong("id"));
						mr.set("title", msg.getStr("title"));
						mr.set("content", msg.getStr("content"));
						mr.set("join_time", new Date());
						Db.save("customer_message_center", mr);
					}
				}
			}else if(pushType==1){
				//区域用户
				
			}else if(pushType==2){
				//指定用户
				
			}
		}else if(customerType==2){
			//2用户端
			if(pushType==0){
				//全部用户
				
			}else if(pushType==1){
				//区域用户
				
			}else if(pushType==2){
				//指定用户
				
			}
		}
	}

}

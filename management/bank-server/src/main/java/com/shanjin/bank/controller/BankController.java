package com.shanjin.bank.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.bank.bean.NormalResponse;
import com.shanjin.bank.bean.ReqParamKit;
import com.shanjin.bank.config.Constant;
import com.shanjin.bank.service.OpayService;
import com.shanjin.bank.socket.Client;
import com.shanjin.bank.util.AESUtil;
import com.shanjin.bank.util.ClientLogUtil;
import com.shanjin.bank.util.CodeMsgUtil;
import com.shanjin.bank.util.DateUtil;
import com.shanjin.bank.util.PropUtil;
import com.shanjin.bank.util.StringUtil;



public class BankController extends Controller{
	Client client = new Client();
	OpayService opayService = new OpayService();
	private final Logger log = Logger.getLogger(BankController.class);
	private static Properties resultCodeProp = CodeMsgUtil.getPro();
	private static String custName;
	public void audit(){
		ReqParamKit paramKit = ReqParamKit.getInstance(this.getParaMap());
		String encryptedParams = paramKit.getString("encryptedParams");
		log.debug("前置机解密财务后台传入的加密参数:"+AESUtil.decrypt(encryptedParams, "100FF0D086A0A9AF44376C14624616CC"));
		JSONObject parmsObj = JSONObject.parseObject(AESUtil.decrypt(encryptedParams, "100FF0D086A0A9AF44376C14624616CC"));
		
		custName = parmsObj.getString("custName");
		log.debug("前置机解析财务后台传入参数:|custName="+custName);
		if(StringUtil.isNullStr(custName)){
			this.renderJson("传入custName不能为空");
			return;
		}
		JSONObject js=new JSONObject();
		js.put("custName", custName);
		this.setAttr("custName", custName);
		this.render("audit.jsp");
		ClientLogUtil.insertLog("财务后台","前置机",this.getRequest().getRequestURL().toString(),JSON.toJSONString(js),"audit.jsp");
    }
	
	public void queryTransfer(){
		
		ReqParamKit paramKit = ReqParamKit.getInstance(this.getParaMap());
		String encryptedParams = paramKit.getString("encryptedParams");
		log.debug("前置机解密财务后台传入的加密参数:"+AESUtil.decrypt(encryptedParams, "100FF0D086A0A9AF44376C14624616CC"));
		JSONObject parmsObj = JSONObject.parseObject(AESUtil.decrypt(encryptedParams, "100FF0D086A0A9AF44376C14624616CC"));
		
		custName = parmsObj.getString("custName");
		log.debug("前置机解析财务后台传入参数:|custName="+custName);
		if(StringUtil.isNullStr(custName)){
			this.renderJson("传入custName不能为空");
			return;
		}
		JSONObject js=new JSONObject();
		js.put("custName", custName);
		this.render("queryTransfer.jsp");
		ClientLogUtil.insertLog("财务后台","前置机",this.getRequest().getRequestURL().toString(),JSON.toJSONString(js),"queryTransfer");
		
    }
	
	//查询所有可提现申请记录列表
	public void findAllApplyRecodList(){
		try {
			//交易代码
			log.debug("====================1,查询所有可提现申请记录列表执行开始====================\n");
			
			ReqParamKit paramKit = ReqParamKit.getInstance(this.getParaMap());
			Integer pageSize = 10;
			if(!StringUtil.isNullStr(paramKit.getString("limit"))){
				pageSize = Integer.parseInt(paramKit.getString("limit"));
			}
			
			Integer pageNo = 0;
			if(!StringUtil.isNullStr(paramKit.getString("page"))){
				pageNo = Integer.parseInt(paramKit.getString("page"))-1;
			}
			
			Integer status = Constant.WITHDRAW_STATUS_APPLAY;
			if(!StringUtil.isNullStr(paramKit.getString("status"))){
				status = Integer.parseInt(paramKit.getString("status"));
			}
			
			String name = paramKit.getString("name");
			String phone = paramKit.getString("phone");
			
			String startTime = "";
			if(!StringUtil.isNullStr(paramKit.getString("startTime"))&&paramKit.getString("startTime").contains("T")){
				startTime = paramKit.getString("startTime").split("T")[0]+" 00:00";
			}
			
			String endTime = "";
			if(!StringUtil.isNullStr(paramKit.getString("endTime"))&&paramKit.getString("endTime").contains("T")){
				endTime = paramKit.getString("endTime").split("T")[0]+" 23:59";
			}
			
			
			Map<String, Object> reslutMap = getApplyRecodList(pageSize,pageNo,status,name,phone,startTime,endTime);
			log.debug("=从opay接口查询到的数据"+JSON.toJSONString(reslutMap)+"=");
			this.renderJson(new NormalResponse(reslutMap.get("list"), Long.parseLong(reslutMap.get("totalNumber").toString())));
			log.debug("====================1,查询所有可提现申请记录列表执行结束====================\n");
		} 
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	//批量转账交易结果查询
	public void queryBatchTransferResult(){
		try {
			//交易代码
			log.debug("====================2,批量转账交易结果查询执行开始====================\n");
			ReqParamKit paramKit = ReqParamKit.getInstance(this.getParaMap());
			String entryDate = paramKit.getString("entryDate");
			if(!StringUtil.isNullStr(entryDate)&&entryDate.contains("T")){
				entryDate = DateUtil.strToYMDString(entryDate.split("T")[0]);
			}
			
			String batchNo = paramKit.getString("batchNo");
			log.debug("前置机解析传入参数:"+"entryDate="+entryDate+"|batchNo="+batchNo);
			
			String REQ_XHJ5002 = Constant.QUERY_BATCH_TRAN_CODE+"#"+entryDate+"#"+batchNo+"#"+"@@@@";
			log.debug("发送请求报文:"+"REQ_XHJ5002="+REQ_XHJ5002);
			
			String msg = client.send(REQ_XHJ5002);
			log.debug("获取银行返回结果:"+"msg="+msg);
			
			//分割银行服务器返回的字符串
			String []serverMsgArr = msg.split("#");
			Map<String,Object> map = new HashMap<String, Object>();
	  		Integer batchStatus = Constant.STATUS_FAIL;
	  	  	Integer status = Constant.STATUS_FAIL;
	  	  	String batchMessage = "";
			String message = "";
			List<Map<String,Object>> notifylist = new ArrayList<Map<String,Object>>();
			//当银行返回code为成功:000000时准备通知opay
			if(Constant.SUCCESS_CODE.equals(serverMsgArr[Constant.RESULT_CODE_INDEX])){
				batchMessage = "批处理成功";
				//封装用户列表提现的详细信息
				batchStatus = Constant.STATUS_SUCCESS;
				for(int i=Constant.DETAIL_BEGIN_INDEX;i<serverMsgArr.length;i++){
					String str = serverMsgArr[i];
					Map<String,Object> notfiyMap = new HashMap<String,Object>();
					if(str.contains("|")){
						String[] strArr = str.split("\\|");
						if(Constant.SUCCESS_CODE.equals(strArr[Constant.DETAIL_RESULT_CODE_INDEX])){
							status = Constant.STATUS_SUCCESS;
							message = "交易成功";
							//message = prop.getProperty(strArr[Constant.DETAIL_RESULT_CODE_INDEX]);
						}
						else{
							message = resultCodeProp.getProperty(strArr[Constant.DETAIL_RESULT_CODE_INDEX]);
						}
						notfiyMap.put("userName", strArr[Constant.USER_NAME_INDEX]);
						notfiyMap.put("bankCode", strArr[Constant.BANK_CODE_INDEX]);
						notfiyMap.put("money", strArr[Constant.MONEY_INDEX]);
						notfiyMap.put("message", message);
						notfiyMap.put("status", status);
						notifylist.add(notfiyMap);
					}
					
				}
			}
			else{
				batchMessage = resultCodeProp.getProperty(serverMsgArr[Constant.RESULT_CODE_INDEX]);
			}
			
			map.put("batchNo", batchNo);
			map.put("message", batchMessage);
			map.put("status", batchStatus);
			map.put("list", notifylist);
			log.debug("封装银行返回的结果:"+"map="+map);
			this.renderJson(map);
			log.debug("====================2,批量转账交易结果查询执行结束====================\n");
		} 
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	
	//跨行对公向对私转账
	public void batchTransfer(){
		log.debug("====================3,跨行对公向对私转账执行开始====================\n");
		List<Map<String, Object>> paramList = new ArrayList<Map<String, Object>>();
		
		ReqParamKit paramKit = ReqParamKit.getInstance(this.getParaMap());
		String ids = paramKit.getString("ids");
		if(StringUtil.isNullStr(ids)){
			log.error("未勾选提现记录");
			this.renderJson("请勾选提现记录");
		}
		
		if(StringUtil.isNullStr(custName)){
			
			log.error("财务后台未传入经办人");
			this.renderJson("访问url没有经办人");
		}
		
		log.debug("前置机解析财务后台传入参数:"+"ids="+ids+"|custName="+custName);
		
		paramList = getApplyRecodListByIds(ids,custName);
		log.debug("opay根据勾选ids查询结果:"+"paramList="+paramList);
		
		JSONObject js=new JSONObject();
		if(CollectionUtils.isEmpty(paramList)){
			this.renderJson("未查询到提现记录");
		}
		
		try{
			List<Map<String, Object>> notHandledList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> handledList = new ArrayList<Map<String, Object>>();
			
			Map<String, Object> handledMap = new HashMap<String, Object>();
			for(Map<String, Object> paramMap : paramList){
				if(Constant.WITHDRAW_STATUS_APPLAY==(int)paramMap.get("status")){
					notHandledList.add(paramMap);
				}
				else{
					handledMap.put("银行账户名", paramMap.get("userName"));
					handledMap.put("银行卡号", paramMap.get("bankCode"));
					handledList.add(handledMap);
				}
			}
			
			
			if(!CollectionUtils.isEmpty(handledList)){
				js.put("已处理账户列表", handledList);
			}
			
			
			if(!CollectionUtils.isEmpty(notHandledList)){
				Integer status = Constant.STATUS_FAIL;
				String remarks = "";
				String date = DateUtil.getNowStr();
				String batchNo = "";
				try {
					//交易代码
					String tran_code = Constant.BATCH_TRANSFER_TRAN_CODE;
					
					//批次号:企业号＋日期＋号码(系统唯一)
					batchNo = Constant.COMPANY_NO+StringUtil.getBatchNo();
					
					//付款账号
					String acctNo = Constant.ACCTNO;
					
					//总金额
					String amount = "";
					Double amountDouble = 0.00;
					
					//记录数
					String sendNumber=String.valueOf(notHandledList.size());
					
					//摘要
					String summary = "跨行批量转账";
					
					String cyc_str = "";
					
					int rownumberer = 0;
					for(Map<String, Object> paramMap : notHandledList){
						amountDouble = amountDouble + Double.parseDouble(paramMap.get("money").toString());
		
						rownumberer++;
						String str =String.valueOf(rownumberer) + "|"+paramMap.get("bankReferenceCode")+"|"+
						
								
								paramMap.get("bankCode").toString()+"|" + 
								paramMap.get("userName").toString()+"|"+"C200|02001|附言:提现|"+
								paramMap.get("money").toString()+"|"+"备注:提现|"+"#";
						cyc_str = cyc_str + str;
					}
				
					amount = String.format("%1$.2f", amountDouble);
				
					String REQ_XJH2001 = tran_code+"#"+custName+"#"+batchNo+"#"+acctNo+"#"+amount+"#"+sendNumber+"#"+summary+"#"+cyc_str+"@@@@";
					log.debug("发送请求报文:"+"REQ_XJH2001="+REQ_XJH2001);
					
					String serverMsg = client.send(REQ_XJH2001);
					log.debug("获取银行返回结果:"+"serverMsg="+serverMsg);
					
					String []serverMsgArr = serverMsg.split("#");
					
					if(Constant.SUCCESS_CODE.equals(serverMsgArr[Constant.RESULT_CODE_INDEX])){
						status = Constant.STATUS_SUCCESS;
						remarks = "批处理转账申请成功";
						
					}
					else{
						remarks = resultCodeProp.getProperty(serverMsgArr[Constant.RESULT_CODE_INDEX]);
					}
					
					js.put("ids", ids);
					js.put("batchNo", batchNo);
					js.put("custName", custName);
					js.put("remarks", remarks);
					js.put("date", date);
					notifyTransfer(ids,custName,status,remarks,batchNo,date);
					
				} 
				catch (Exception e) {
					log.error(e.getMessage(), e);
					remarks = e.getMessage();
					js.put("ids", ids);
					js.put("batchNo", batchNo);
					js.put("custName", custName);
					js.put("remarks", remarks);
					js.put("date", date);
					notifyTransfer(ids,custName,status,remarks,batchNo,date);
				} 
			}
		}
		catch(Exception e){
			log.error(e.getMessage(), e);
		}
		finally{
			this.renderJson(js.toJSONString());
			log.debug("====================3,跨行对公向对私转账执行结束====================\n");
		}
		
	}
	
	/**opay需要提供的接口*/
	
	//1:查询所有提现申请记录列表
	public Map<String,Object>  getApplyRecodList(Integer pageSize,Integer pageNo,Integer status,String name,String phone,String startTime,String endTime){
		//请求opay拿提现申请记录列表
		Map<String,Object> resultMap = new HashMap<String,Object>();
		String seviceName="/wallet/financial/queryWithDrawLists";
		JSONObject js=new JSONObject();
		js.put("pageSize", pageSize);
		js.put("pageNo", pageNo);
		js.put("status", status);
		js.put("name", name);
		js.put("phone", phone);
		js.put("startTime", startTime);
		js.put("endTime", endTime);
		String paramsPlainText = js.toJSONString();
	 	try {
			JSONObject result = (JSONObject) JSONObject.parse(opayService.sendRequestOpay(seviceName, paramsPlainText));
			resultMap.put("totalPage", result.get("totalPage"));
			resultMap.put("list", result.get("withDrawLists"));
			resultMap.put("totalNumber", result.get("totalNumber"));
			return resultMap;
		} catch (Exception e) {
			log.error("查询所有提现申请记录列表:"+e.getMessage(), e);
		}
		
		return null;
	}
	
	
	//2:根据勾选ids查询所有提现申请记录列表
	public List<Map<String, Object>> getApplyRecodListByIds(String ids,String operateUser){
		//请求opay拿提现申请记录列表
		String seviceName="/wallet/financial/queryWithDrawDetailLists";
		JSONObject js=new JSONObject();
		js.put("ids", ids);
		js.put("operateUser", operateUser);
		String paramsPlainText = js.toJSONString();
	 	try {
			JSONObject result = (JSONObject) JSONObject.parse(opayService.sendRequestOpay(seviceName, paramsPlainText));
			return (List<Map<String, Object>>)result.get("list");
		} catch (Exception e) {
			log.error("根据勾选ids查询所有提现申请记录列表:"+e.getMessage(), e);
		}
		
		return null;
		
	}
	
	
	//5:将批量转账的银行处理结果以参数形式通知opay
	public void notifyTransfer(String ids,String operateUser,Integer status,String remark,String batchNo,String entryDate){
		//请求opay拿提现申请记录列表
		String seviceName="/wallet/financial/applyOfTransferFeedback";
		JSONObject js=new JSONObject();
		js.put("ids", ids);
		js.put("operateUser", operateUser);
		js.put("status", status);
		js.put("remark", remark);
		js.put("batchNo", batchNo);
		js.put("entryDate", entryDate);
		String paramsPlainText = js.toJSONString();
	 	try {
	 		opayService.sendRequestOpay(seviceName, paramsPlainText);
		} catch (Exception e) {
			log.error("将批量转账的银行处理结果以参数形式通知opay:"+e.getMessage(), e);
		}
				
	}
	
	
}

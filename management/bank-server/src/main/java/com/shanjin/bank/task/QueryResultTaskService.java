package com.shanjin.bank.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.bank.config.Constant;
import com.shanjin.bank.service.OpayService;
import com.shanjin.bank.socket.Client;
import com.shanjin.bank.util.CodeMsgUtil;

public class QueryResultTaskService  extends TimerTask{

	private final Logger log = Logger.getLogger(QueryResultTaskService.class);
	Client client = new Client();
	OpayService opayService = new OpayService();
	private static Properties prop = CodeMsgUtil.getPro();
	
	@Override  
    public void run() {
		try {
	        String currentTime = new SimpleDateFormat("yyy-MM-dd hh:mm:ss").format(new Date());  
	        log.debug("===================="+currentTime+">>定时查询批量转账交易结果执行开始====================\n");
	    	//交易代码
	        String tran_code = Constant.QUERY_BATCH_TRAN_CODE;
	  		
	  		//得到opya受理中的提现记录
	  		List<Map<String, Object>> list = handledRecodList();
	  		log.debug("=从opay中查询到的受理中的提现记录列表="+JSON.toJSONString(list));
	  		
	  		if(!CollectionUtils.isEmpty(list)){
	  			for(Map<String, Object> resultMap : list){
		  			
		  			//向银行服务器发送请求拿到批次号下的银行处理结果
		  			String entryDate = resultMap.get("entryDate").toString();
	  	  			String batchNo = resultMap.get("withdrawBankBatchNo").toString();
	  	  			String REQ_XHJ5002 = tran_code+"#"+entryDate+"#"+batchNo+"#"+"@@@@";
	  	  			
					String serverMsg = client.send(REQ_XHJ5002);
					
					//分割银行服务器返回的字符串
					String []serverMsgArr = serverMsg.split("#");
					
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
									status = Constant.STATUS_FAIL;
									message = prop.getProperty(strArr[Constant.DETAIL_RESULT_CODE_INDEX]);
								}
								notfiyMap.put("userName", strArr[Constant.USER_NAME_INDEX]);
								notfiyMap.put("bankCode", strArr[Constant.BANK_CODE_INDEX]);
								notfiyMap.put("money", strArr[Constant.MONEY_INDEX]);
								notfiyMap.put("message", message);
								notfiyMap.put("status", status);
								notifylist.add(notfiyMap);
							}
							
						}
						log.debug("封装银行返回的结果:"+"notifylist="+notifylist);
					}
					else{
						batchMessage = prop.getProperty(serverMsgArr[Constant.RESULT_CODE_INDEX]);
					}
					
					map.put("batchNo", batchNo);
					map.put("message", batchMessage);
					map.put("status", batchStatus);
					map.put("list", notifylist);
					
					//查询批次号下的银行处理结果，并将结果以参数形式通知opay
					notifyBatchNoHandle(map);
				}
	  		}
	  		else{
	  			log.debug("=得到opya受理中的提现记录列表为空=\n");
	  		}
	  		log.debug("===================="+currentTime+">>定时查询批量转账交易结果执行结束====================\n");
	  		
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
  		
    }
	
	//opay3:获取opya受理中的提现记录
	public List<Map<String, Object>> handledRecodList(){
		String seviceName="/wallet/financial/queryProcessWithDrawLists";
	 	String paramsPlainText = "{}";
	 	try {
			JSONObject result = (JSONObject) JSONObject.parse(opayService.sendRequestOpay(seviceName, paramsPlainText));
			return (List<Map<String, Object>>)result.get("list");
		} catch (Exception e) {
			log.error("获取opya受理中的提现记录:"+e.getMessage(), e);
		}
		
		return null;
	}
	
	//opay4:查询批次号下的银行处理结果，并将结果以参数形式通知opay
	public void notifyBatchNoHandle(Map<String,Object> map){
		//请求opay拿提现申请记录列表
		String seviceName="/wallet/financial/withDrawFeedback";
		JSONObject js=new JSONObject();
		js.put("batchNo", map.get("batchNo"));
		js.put("resultlist", map.get("list"));
		js.put("status", map.get("status"));
		js.put("message", map.get("message"));
		String paramsPlainText = js.toJSONString();
	 	try {
	 		opayService.sendRequestOpay(seviceName, paramsPlainText);
		} catch (Exception e) {
			log.error("查询批次号下的银行处理结果，并将结果以参数形式通知opay:"+e.getMessage(), e);
		}
	}
}

package com.shanjin.bank.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.shanjin.bank.service.OpayService;
import com.shanjin.bank.socket.Client;
import com.shanjin.bank.util.AESUtil;
import com.shanjin.bank.util.CodeMsgUtil;


public class TestController extends Controller{

	public static Client client = new Client();
	
	private final static Logger log = Logger.getLogger(TestController.class);
	static OpayService opayService = new OpayService();
	
	//跨行对公向对私转账
		public static void batchTransfer(List<Map<String, Object>> paramList) throws UnknownHostException, IOException{
			if(!CollectionUtils.isEmpty(paramList)){
				
				//交易代码
				String tran_code = "xhj2001";
				
				//经办人
				String custName = "150153";
				
				//批次号:企业号＋日期＋号码(系统唯一)
				String batchNo = "15015320160713000039";
				
				//付款账号
				String acctNo = "10250000001327463";
				
				//总金额
				String amount = "";
				Double amountDouble = 0.00;
				
				//记录数
				String sendNumber=String.valueOf(paramList.size());
				
				//摘要
				String summary = "跨行批量转账";
				
				String cyc_str = "";
				
				
				
				for(Map<String, Object> paramMap : paramList){
					amountDouble = amountDouble + Double.parseDouble(paramMap.get("withdraw_price").toString());

					String str =paramMap.get("rownumberer").toString() + "|"+"308584000013|"+
							paramMap.get("withdraw_no").toString()+"|" + 
							paramMap.get("name").toString()+"|"+"C200|02001|附言:提现|"+
							paramMap.get("withdraw_price").toString()+"|"+"备注:提现|"+"#";
					cyc_str = cyc_str + str;
				}
				
				amount = String.format("%1$.2f", amountDouble);
				
				String REQ_XJH2001 = tran_code+"#"+custName+"#"+batchNo+"#"+acctNo+"#"+amount+"#"+sendNumber+"#"+summary+"#"+cyc_str+"@@@@";
				
				//String REQ_XJH2001 = "xhj2001#150153#15015320160713000028#10250000001327463#2.00#2#跨行批量#1|308584000013|6230210010768038|靳南心|C200|02001|提现|1.00|备注1|#2|308584000013|6226300119090453|王冉|C200|02001|提现|1.00|备注2|#@@@@";
				client.send(REQ_XJH2001);
				//String REQ_XJH2001 = "xjh2001#渣渣#15015320261130000010#10250000001327463#2.00#2#跨行批量#"+"5648|308584000013|6225885519165252|汤军|C200|02001|附言1|1.00|备注1#6895|308584000013|6225885519165253|汤军|C200|02001|附言2|1.00|备注2#";
			}
			
			
		}	
			
		//批量转账交易结果查询
		public static void queryBatchTransferResult(String entryDate,String batchNo) throws UnknownHostException, IOException{
			
			//交易代码
			String tran_code = "xhj5002";
			
			String REQ_XJH5002 = tran_code+"#"+entryDate+"#"+batchNo+"#"+"@@@@";
			
			client.send(REQ_XJH5002);
			
			
		}
		
		
		
		public static void main(String [] args) throws UnknownHostException, IOException{
			//String filePath = System.getProperty("user.dir")+"\\MANAGER_ACCESS_LOG.txt";
	        //readTxtFile(filePath);
	        
	        JSONObject js = new JSONObject();
	        js.put("custName", "huangyulai");
	        String paramsPlainText = JSON.toJSONString(js);
	              String encryptedParams = AESUtil.parseByte2HexStr(AESUtil.encrypt(paramsPlainText, "100FF0D086A0A9AF44376C14624616CC"));
	        System.out.println(encryptedParams);
		}
		
		
		//opay3:获取opya受理中的提现记录
		public static List<Map<String, Object>> handledRecodList(){
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
		public static void notifyBatchNoHandle(Map<String,Object> map){
			//请求opay拿提现申请记录列表
			String seviceName="/wallet/financial/withDrawFeedback";
			JSONObject js=new JSONObject();
			js.put("batchNo", map.get("batchNo"));
			js.put("resultlist", map.get("list"));
			String paramsPlainText = js.toJSONString();
		 	try {
		 		opayService.sendRequestOpay(seviceName, paramsPlainText);
			} catch (Exception e) {
				log.error("查询批次号下的银行处理结果，并将结果以参数形式通知opay:"+e.getMessage(), e);
			}
		}
		
		/**
	     * 功能：Java读取txt文件的内容
	     * 步骤：1：先获得文件句柄
	     * 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
	     * 3：读取到输入流后，需要读取生成字节流
	     * 4：一行一行的输出。readline()。
	     * 备注：需要考虑的是异常情况
	     * @param filePath
	     */
	    public static void readTxtFile(String filePath){
	        try {
	                String encoding="UTF-8";
	                File file=new File(filePath);
	                if(file.isFile() && file.exists()){ //判断文件是否存在
	                    InputStreamReader read = new InputStreamReader(
	                    new FileInputStream(file),encoding);//考虑到编码格式
	                    BufferedReader bufferedReader = new BufferedReader(read);
	                    String lineTxt = null;
	                    Integer lineNumber = 0;
	                    Map<String,String> map = new LinkedHashMap<String, String>();
	                    List<Map<String,String>> list = new ArrayList<Map<String,String>>();
	                    while((lineTxt = bufferedReader.readLine()) != null){
	                    	if(!lineTxt.contains("=======")){
	                    		lineNumber++;
	                    		String[] arr = lineTxt.split(">>");
	                    		if(lineNumber<=6){
	                    			map.put(arr[0], arr[1]);
	                    		}
	                    		else{
	                    			list.add(map);
	                    			map = new LinkedHashMap<String, String>();
	                    			lineNumber=0;
	                    		}
	                    		
	                    	}
	                        
	                    }
	                    for(Map<String,String> mapInfo:list){
	                    	System.out.println(JSON.toJSONString(mapInfo));
	                    }
	                    read.close();
	        }else{
	            System.out.println("找不到指定的文件");
	        }
	        } catch (Exception e) {
	            System.out.println("读取文件内容出错");
	            e.printStackTrace();
	        }
	     
	    }
	
	
}

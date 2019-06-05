package com.shanjin.manager.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.jfinal.core.Controller;
import com.shanjin.manager.Bean.EmptyResponse;
import com.shanjin.manager.Bean.Message;
import com.shanjin.manager.Bean.NormalResponse;
import com.shanjin.manager.service.IMessageService;
import com.shanjin.manager.service.impl.MessageServiceImpl;

public class MessageController extends Controller{
	
	private IMessageService messageService = new MessageServiceImpl();
	
	public void messageIndex() {
		render("messageGrid.jsp");
	}
	
	/** 获取主消息列表 */
	public void messageList() {
		Map<String, String[]> param = this.getParaMap();
		List<Message> list = messageService.getMessageList(param);
		if (list != null && list.size() > 0) {
			long total = list.get(0).getTotal();
			this.renderJson(new NormalResponse(list, total));
		} else {
			this.renderJson(new EmptyResponse());
		}
	}

	/** 删除主消息记录 */
	public void deleteMessage() {
		String ids = this.getPara("ids");
		Boolean flag = messageService.deleteMessage(ids);
		this.renderJson(flag);
	}
	
	/** 发送主消息 */
	public void sendMessage() {
		String id = this.getPara("id");
		Boolean flag = messageService.sendMessage(id);
		this.renderJson(flag);
	}

	/** 添加主消息*/
	public void addMessage(){
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = messageService.saveMessage(param);
		this.renderJson(flag);
	}

	/** 编辑主消息*/
	public void editMessage() {
		Map<String, String[]> param = this.getParaMap();
		Boolean flag = messageService.saveMessage(param);
		this.renderJson(flag);
	}
	
	
	
	public static void main(String[] args){
		String[] str = new String[]{"{呲牙}","{难过}","{微笑}","{色}","{可怜}","{大笑}","{流泪}","{擦汗}"};
		//{呲牙}
		HttpPost httpPost;
		CloseableHttpClient httpClient;
		httpClient = HttpClients.createDefault();
		System.getProperties().setProperty("http.proxyHost", "10.22.40.32");
		System.getProperties().setProperty("http.proxyPort", "8080");
		//String url = "http://app.ayou.tv/clt/clt/shake.msp?loginName=sbsb@126.com&psw=123457&WD_UUID=123456789";
		//369852147@qq.com&psw=123457&WD_UUID=369852147
		String url = "http://app.ayou.tv/clt/clt/comment.msp";
		String param = "c=1139691&WD_UUID=369852147&content=";
		int len = str.length;
		for(int i=0;i<8;i++){
		    int num = (int)(Math.random() * len);
			//String s = url + str[num];
			String res = SendGET(url,param+"少有好男人");
			System.out.println("resp="+res);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("循环结束");
//		httpPost = new HttpPost(URL);
//
//
//		CloseableHttpResponse resp;
//		try {
//			resp = httpClient.execute(httpPost);
//			//StatusLine status = resp.getStatusLine();
//			//String responseJson = EntityUtils.toString(resp.getEntity(), "UTF8");
//			System.out.println("resp="+resp);
//			resp.close();
//			httpClient.close();
//		} catch (ClientProtocolException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} 


	}
	public static String SendGET(String url,String param){
		   String result="";//访问返回结果
		   BufferedReader read=null;//读取访问结果
		   try {
		    //创建url
		    URL realurl=new URL(url+"?"+param);
		    //打开连接
		    URLConnection connection=realurl.openConnection();
		     // 设置通用的请求属性
		             connection.setRequestProperty("accept", "*/*");
		             connection.setRequestProperty("connection", "Keep-Alive");
		             connection.setRequestProperty("user-agent","Mozilla/5.0 (Windows NT 6.3; WOW64; rv:40.0) Gecko/20100101 Firefox/40.0");
		             //建立连接
		             connection.connect();
		          // 获取所有响应头字段
		            // Map<String, List<String>> map = connection.getHeaderFields();
		             // 遍历所有的响应头字段，获取到cookies等
		             //for (String key : map.keySet()) {
		                // System.out.println(key + "--->" + map.get(key));
		            // }
		             // 定义 BufferedReader输入流来读取URL的响应
		             read = new BufferedReader(new InputStreamReader(
		                     connection.getInputStream(),"UTF-8"));
		             String line;//循环读取
		             while ((line = read.readLine()) != null) {
		                 result += line;
		             }
		   } catch (IOException e) {
		        e.printStackTrace(); 
		   }finally{
		    if(read!=null){//关闭流
		     try {
		      read.close();
		     } catch (IOException e) {
		      e.printStackTrace();
		     }
		    }
		   }
		     
		   return result; 
		 }
	
}

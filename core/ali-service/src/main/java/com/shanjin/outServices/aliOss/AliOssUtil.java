package com.shanjin.outServices.aliOss;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;

/**
 * 封装 阿里OSS的相关服务
 * @author Revoke Yu
 *
 */

public class AliOssUtil {
	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(AliOssUtil.class);
	
	// true 开发环境   false 生产环境
	private static  boolean DEV_MODE = true;
	
	//阿里云存储秘钥对
	static final String ACCESS_ID = "s3cTcaxCIqwgnoGQ";
	static final String ACCESS_KEY = "BniyKvkAtneaEnb1Ehl6Oyz3oquf9k";
	
	//测试环境 CDN bucket 名称
	private static final String DEV_CDN_BUCKET="omdevcdn";
			
	//测试环境   bucket 名称
	private static final String DEV_BUCKET="omdev";
	
	//测试环境  cdn bucket 绑定的域名
	private static final String DEV_CDN_DOMAIN="omg.oomeng.cn";
	
	//测试环境  cdn bucket 绑定的域名
	private static final String DEV_DOMAIN="omg2.oomeng.cn";
	
	//测试环境 cdn bucket 对应的网址前缀
	private static final String DEV_CDN_HTTP_PREFIX="http://"+DEV_CDN_DOMAIN;
	
	
	//生产环境 CDN bucket 名称
    private static final String PROD_CDN_BUCKET="omprodcdn";
	
	//生产环境 bucket 名称
	private static final String PROD_BUCKET= "omprod";
	
			
	//生产环境  cdn bucket 绑定的域名
	private static final String PROD_CDN_DOMAIN="pmg.oomeng.cn";
		
	//生产环境  cdn bucket 绑定的域名
	private static final String PROD_DOMAIN="pmg2.oomeng.cn";
	
	
	//生产环境 cdn bucket 对应的网址前缀
	private static final String PROD_CDN_HTTP_PREFIX="http://"+PROD_CDN_DOMAIN;
		
	
	
	/**
	 * 上传文件到阿里OSS中
	 * @param path			文件存放路径
	 * @param fileName		文件名称
	 * @param fileStream	文件流
	 * @return
	 */
	public static boolean upload(String path, String fileName, InputStream fileStream){
		try{
				long beginTime = System.currentTimeMillis();
				String bucketName = getBucketName(path);
				ClientConfiguration config = new ClientConfiguration();
				config.setSocketTimeout(50*1000);
				config.setConnectionTimeout(50*1000);
				OSSClient client = new OSSClient(getOssEndPoit(), ACCESS_ID, ACCESS_KEY,config);
				uploadFile(client,bucketName,path,fileName,fileStream);
				
				long endTime = System.currentTimeMillis();
				
				StringBuffer msg = new StringBuffer(" upload to oss for ").append(fileName).append(" cost: ");
				msg.append((endTime-beginTime)/1000);
				
				logger.info(msg.toString());
				
		}catch(Exception e){
				logger.error("阿里OSS上传异常："+e.getMessage(),e);
				throw new RuntimeException(e.getMessage(),e);
		}
		return true;
	}
	
	/**
	 * 删除阿里OSS中文件
	 * @param fullpath			文件存放路径及文件名
	 */
	public static boolean delete(String fullPath){
		try{
			String bucketName = getBucketName(fullPath);
			ClientConfiguration config = new ClientConfiguration();
			config.setSocketTimeout(50*1000);
			config.setConnectionTimeout(50*1000);
			OSSClient client = new OSSClient(getOssEndPoit(), ACCESS_ID, ACCESS_KEY,config);
			deleteFile(client,bucketName,fullPath);
			
			long endTime = System.currentTimeMillis();
			
	}catch(Exception e){
			logger.error("阿里OSS文件删除异常："+e.getMessage(),e);
			throw new RuntimeException(e.getMessage(),e);
	}
	return true;
		
	}
	
	
	/**
	 * 生成阿里OSS查看文件的URL。
	 * @param originalPath
	 * @return
	 */
	public static String getViewUrl(String originalPath) {
		StringBuffer result= new StringBuffer("http://");
		result.append(getDnsName(originalPath));
		result.append(originalPath);
		return result.toString();
	}
	
	
	/**
	 *  获取CDN加速buket 对应的网址前缀
	 */
	public static String getCDNPrefix(){
		   return	DEV_MODE ? DEV_CDN_HTTP_PREFIX:PROD_CDN_HTTP_PREFIX;
		
	}
	
	
	
	public static void setModel(boolean isDevModel){
		DEV_MODE = isDevModel;
	}
	
	
	
	private static String  getOssEndPoit(){
		 return DEV_MODE ? "http://oss.aliyuncs.com/" :"http://oss-cn-hangzhou-internal.aliyuncs.com/";
		
	}
	  // 上传文件
    private static void uploadFile(OSSClient client, String bucketName, String path, String filename,InputStream fileStream) throws IOException
            {
    	String key = path+filename;
    	key = removeBackSlash(key);
        ObjectMetadata objectMeta = new ObjectMetadata();
        objectMeta.setContentLength(fileStream.available());
        // 可以在metadata中标记文件类型
        objectMeta.setContentType(getContenType(filename));
        client.putObject(bucketName, key, fileStream, objectMeta);
    }
    
    // 删除文件
    private static void deleteFile(OSSClient client, String bucketName, String fullPath) throws IOException
            {
    	String key = removeBackSlash(fullPath);
    	client.deleteObject(bucketName, key);
    }
    
    
    
    
    //根据文件路径，判断OSS上存放到哪个bucket中。
    private static String getBucketName(String filePath) {
    	  String result = null;
    	  if (filePath.startsWith("/resource") || filePath.startsWith("/manFile")){
    		  result = DEV_MODE?DEV_CDN_BUCKET:PROD_CDN_BUCKET;
    	  }else if (filePath.startsWith("/upload/merchantInfo/image/order") || filePath.startsWith("/upload/merchantInfo/voice") || filePath.startsWith("/upload/userInfo") || filePath.startsWith("/upload/feedback")){
    		  result =DEV_MODE?DEV_BUCKET:PROD_BUCKET;
    	  }else{
    		  result = DEV_MODE?DEV_CDN_BUCKET:PROD_CDN_BUCKET;
    	  }
    	  return result;
    }
    
    
    //根据文件路径，返回对应的DNS
    private static String getDnsName(String filePath) {
    	 String result=null;
    	 String bucketName = getBucketName(filePath);
    	 switch(bucketName){
    	 	case DEV_BUCKET: 
    	 					result = DEV_DOMAIN;
    	 					break;
    	 	case DEV_CDN_BUCKET:
    	 					result = DEV_CDN_DOMAIN;
    	 					break;
    	 	case PROD_CDN_BUCKET:
    	 					result = PROD_CDN_DOMAIN;
    	 					break;
    	 	case PROD_BUCKET:
    	 					result = PROD_DOMAIN;
    	 					break;
    		 
    	 }
    	 return result;
    }
    
    //根据文件名后缀，返回对应的contentType
    private static String getContenType(String fileName) {
    	  String result=null;
    	  String postfix =  fileName.substring(fileName.lastIndexOf(".")+1);
    	  switch(postfix){
    	  		case "png":
    	  					result="image/png";
    	  					break;
    	  		case "jpg":
    	  		case "jpeg":
    	  					result="image/jpeg";
							break;
    	  		case "gif":
  							result="image/gif";
  							break;
    	  		case "pic":
    	  					result="image/pic";
    	  					break;
    	  		case "bmp":
  							result="image/bmp";
  							break;	
    	  		case "amr":
    	  					result="audio/amr";
    	  					break;
    	  		default:
    	  					result="todo";
    	  					break;
    	  }
    	  return result;
    }
    
    
    //去除前置/，阿里的object 不运行以/开头
    private static String removeBackSlash(String path){
    	return path.startsWith("/")?path.substring(1):path; 
    }


    public static void main(String args[]) {
    	     System.out.println(args.length);
    	     for (int i=0;i<args.length;i++){
    	    	 System.out.println(args[i]);
    	     }
    		String fileName= "/test.jpg";
			InputStream in = AliOssUtil.class.getResourceAsStream(fileName);
			AliOssUtil.setModel(args[args.length-2].equals("true")?true:false);
			if (in==null){
				System.out.println("文件未找到");
				return;
			}
			String path="/ossTest";
			path= "/"+args[args.length-1]+path;
			System.out.println("end point is :"+getOssEndPoit());
			upload(path, fileName, in);
    		
    }
	
}

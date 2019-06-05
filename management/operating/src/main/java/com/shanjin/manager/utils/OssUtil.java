package com.shanjin.manager.utils;

import java.io.FileInputStream;
import java.io.InputStream;

import com.jfinal.upload.UploadFile;
import com.shanjin.outServices.aliOss.AliOssUtil;


/**
 * 阿里OSS接口封装工具类
 * @author Huang yulai
 *
 */
public class OssUtil {
	
	public static boolean upload(UploadFile uploadFile,String filePath){
		boolean flag = false;
	   	InputStream in = AliOssUtil.class.getResourceAsStream(filePath);
		return true;
	}
	

}

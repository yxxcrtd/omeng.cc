package com.shanjin.outService.aliOss;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Test;

import com.shanjin.outServices.aliOss.AliOssUtil;

public class AliOssUtilsTest {
	
	@Test
	public void upload(){
		boolean devMod=true;
		String path = "/upload/userInfo/image/portrait/201510/";
		String fileName="test.jpg";
		InputStream fileStream;
		fileStream = AliOssUtilsTest.class.getResourceAsStream("/test.jpg");
		AliOssUtil.upload(path, fileName, fileStream);
		
	}

	@Test
	public void delete(){
		boolean devMod=true;
		String path = "/upload/userInfo/image/portrait/201510/test.jpg";
		AliOssUtil.delete(path);
		
	}

	
	@Test
	public void getViewPath(){
		String originalPath="/upload/userInfo/image/portrait/201510/645654f9-4f18-4736-abf1-99beb51bf81f.jpg";
		AliOssUtil.setModel(true);
		AliOssUtil.getViewUrl(originalPath);
	}
	
}

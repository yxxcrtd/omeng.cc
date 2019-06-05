package com.shanjin.other;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.shanjin.common.util.DateUtil;

public class Util {
	
	public static void writeLog(String fileName,String content) {
		String os = System.getProperty("os.name");
		String path = "";
		if (os.toLowerCase().contains("windows")) {
			path = "c:/logs/"+fileName+"Log/";
		} else if (os.toLowerCase().contains("linux")) {
			path = "/mnt/logs/"+fileName+"Log/";
		}
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
		String nowDate=DateUtil.getNowYYYYMMDD();
		path = path + fileName+"_"+nowDate + ".log";
		writeFile(path, content);
	}
	public static void writeFile(String path, String content) {
		FileWriter fw=null;
		try {
			fw = new FileWriter(path,true);
			fw.write(content+"\n");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(fw!=null){
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

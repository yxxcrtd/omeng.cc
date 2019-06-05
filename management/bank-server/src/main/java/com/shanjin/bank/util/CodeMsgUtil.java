package com.shanjin.bank.util;
import java.util.Properties;

import com.jfinal.config.Plugins;


public class CodeMsgUtil{
	
	private static Properties prop = null;

	public static void configPlugins() {
		prop=PropUtil.getPropUtil("resultCode.properties");
	}
	public static Properties getPro() {
		if(prop==null){
		   prop=PropUtil.getPropUtil("resultCode.properties");
		}
		return prop;
		
	}

}

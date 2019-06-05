package com.shanjin.financial.util;


public class BusinessUtil {
    
    /**
     * 查找请求的url是否在资源权限resources里面
     * @param url
     * @param resources
     * @return
     */
    public static boolean havePerm(String url,String resources){
    	boolean haveperm = false ;
		if(resources!=null&&resources!=""){
			String[] perms=resources.split(",");
			if(perms!=null&&perms.length>0){
				for(String per:perms){
					if(!per.equals("")&&per.equals(url)){
						haveperm = true;
						break;
					}
				}
			}
		}
    	return haveperm ;
    }


}

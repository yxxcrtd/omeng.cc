package com.shanjin.financial.util;

import java.util.List;
import org.apache.log4j.Logger;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.financial.constant.Constants;


/**
 * 初始化系统数据
 * @author Huang yulai
 *
 */
public class CommonUtil {
	private  static final Logger log = Logger.getLogger(CommonUtil.class);

	/**
	 * 初始化系统操作资源
	 */
	public static void initSystemResourceMap(){
		System.out.println("========initSystemResourceMap start=====");
		try{
			String sql="select ar.id,ar.resName,ar.remark,ar.disabled,ar.linkPath,ar.isCommon,ar.type from authority_resource_info ar where ar.disabled<>1";
			List<Record> list= Db.find(sql);
			Constants.sysResource.clear();
			Constants.commonResourcePathList.clear();
			if(list!=null&&list.size()>0){
				for(Record sr : list){
					String linkPath = sr.getStr("linkPath");
					Constants.sysResource.put(linkPath, sr);
					int isCommon = StringUtil.nullToInteger(sr.getInt("isCommon"));
					if(isCommon==1){
						Constants.commonResourcePathList.add(linkPath);
					}
					
				}
			}
			System.out.println("========initSystemResourceMap end=====");
		} catch (Exception e) {
			log.error("init initSystemResourceMap failed,because of"+e.getMessage(),e);
		}
	}
	
	
}

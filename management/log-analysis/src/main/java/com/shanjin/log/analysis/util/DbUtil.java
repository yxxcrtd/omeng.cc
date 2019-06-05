package com.shanjin.log.analysis.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.common.util.DateUtil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.log.analysis.Constant;

/**
 * 数据库操作工具类
 * @author Huang yulai
 *
 */
public class DbUtil {
	
	/**
	 * 获取批量插入的数据库链接
	 * @return
	 */
	public static Connection getConnection(){
		String dbUrl = Constant.DB.URL+"&rewriteBatchedStatements=true";
		String userName = Constant.DB.USERNAME;
		String password = Constant.DB.PASSWORD;
		String driverClass = Constant.DB.DRIVERCLASS;
		Connection conn = null;
		try {
			Class.forName(driverClass);
			conn = DriverManager.getConnection(dbUrl, userName, password);
			conn.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		    return conn;
		}
        return conn;
	}
	
	/**
	 * 批量插入访问日志
	 * @param dataList
	 * @param rows
	 * @param dataNum
	 * @param fileName
	 */
	public static void batchInsertVisitLog(List<String> dataList,int rows,int dataNum,String fileName) {
		if(dataList==null||dataList.size()<1){
			return;
		}
		Connection conn = getConnection();
		if(conn==null){
			return;
		}
        long s = System.currentTimeMillis();
		try {
			/**  `id` bigint(20) NOT NULL AUTO_INCREMENT,
			  `uuid` varchar(30) DEFAULT NULL,
			  `logTime` datetime DEFAULT NULL,
			  `url` varchar(50) DEFAULT NULL,
			  `referer` varchar(50) DEFAULT NULL,
			  `clientType` smallint(1) DEFAULT NULL COMMENT '客户端类型（1：安卓，2：ios）',
			  `ua` varchar(100) DEFAULT NULL,
			  `phone` varchar(15) DEFAULT NULL,
			  `appType` varchar(15) DEFAULT NULL,
			  `clientFlag` smallint(1) DEFAULT NULL COMMENT '客户端标识（1：商户，2：用户）',
			  `version` varchar(15) DEFAULT NULL,
			  `channel` varchar(20) DEFAULT NULL,
			  `ip` varchar(50) DEFAULT NULL,
			  `province` varchar(15) DEFAULT NULL,
			  `city` varchar(15) DEFAULT NULL,
			  `model` varchar(50) DEFAULT NULL,
			  `resolution` varchar(50) DEFAULT NULL,
			  `system` varchar(50) DEFAULT NULL,
			  `createTime` datetime DEFAULT NULL,
			  PRIMARY KEY (`id`) **/
			//logTime|uuid|url|referer|phone|clientType|appType|clientFlag|ua|version|channel|ip|
			//INSERT INTO `record_visit_log` VALUES ('1', '235423432', null, null, null, null, null, null, 'omeng', null, null, null, null, '安徽', '合肥', null, null, null, null);
			String sql = "insert into record_visit_log(logTime,uuid,url,referer,phone,clientType,appType,clientFlag,ua,version,channel,ip,province,city,model,resolution,system,createTime) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement prest = conn.prepareStatement(sql);
			for(int i = 0;i<dataList.size();i++){
				handleLineData(dataList.get(i),prest);
//				try{
//					prest.addBatch();
//				}catch(Exception e){
//					e.printStackTrace();
//					System.out.println("出错的数据行为："+dataList.get(i));
//				}

			}
			prest.executeBatch();
			conn.commit();
			createImportLogByVisitLog(rows,dataNum,fileName,1);

		} catch (Exception ex) {
			ex.printStackTrace();
			createImportLogByVisitLog(rows,dataNum,fileName,0);
			return;
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	        long e = System.currentTimeMillis();
	        System.out.println("批量插入访问日志"+dataList.size()+"条，耗时 ："+(e-s)+"ms");
		}
	} 
	
	
	private static void handleLineData(String lineData, PreparedStatement prest) {
		String[] str = lineData.split("\\|");
		try {
			//logTime,uuid,url,referer,phone,clientType,appType,clientFlag,ua,version,channel,ip,province,city,model,resolution,system,createTime
			// logTime|uuid|url|referer|phone|clientType|appType|clientFlag|ua|version|channel|ip||queryStr;
			String uuid = str[1]; 
			if(!StringUtil.isNullStr(uuid)&&str.length==18){
				// 设备uuid为空不入库
				prest.setString(1, StringUtil.null2Str(str[0])); // logTime
				prest.setString(2, StringUtil.null2Str(str[1])); // uuid
				String url = StringUtil.null2Str(str[2]);
				url = url.length()>=100?url.substring(0, 99):url;
				prest.setString(3, url); // url
				String referer = StringUtil.null2Str(str[3]);
				referer = referer.length()>=100?referer.substring(0, 99):referer;
				prest.setString(4, referer); // referer
				prest.setString(5, StringUtil.null2Str(str[4])); // phone
				prest.setInt(6, StringUtil.nullToInteger(str[5])); // clientType
				prest.setString(7, StringUtil.null2Str(str[6])); // appType
				prest.setInt(8, StringUtil.nullToInteger(str[7])); // clientFlag
				prest.setString(9, str[8].length()>100? str[8].substring(0,99):str[8]); // ua
				prest.setString(10, StringUtil.null2Str(str[9])); // version
				prest.setString(11, StringUtil.null2Str(str[10])); // channel
				prest.setString(12, StringUtil.null2Str(str[11])); // ip
				prest.setString(13, ""); // province
				prest.setString(14, ""); // city
				prest.setString(15, ""); // model
				prest.setString(16, ""); // resolution
				prest.setString(17, ""); // system
				prest.setString(18, DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN, new Date())); // createTime
				if (str.length == 14) {
					// logTime|uuid|url|referer|phone|clientType|appType|clientFlag|ua|version|channel|ip|province|city|queryStr;

				} else if (str.length == 18) {
					// 时间|uuid|请求相对路径|源页面绝对路径|登陆手机号|客户端类型|appType|clientFlag|ua|客户端版本|渠道ID|客户端ip地址|省|市|手机型号|分辨率|操作系统|queryStr
					prest.setString(13, StringUtil.null2Str(str[12]));
					prest.setString(14, StringUtil.null2Str(str[13]));
					prest.setString(15, StringUtil.null2Str(str[14]));
					prest.setString(16, StringUtil.null2Str(str[15]));
					prest.setString(17, StringUtil.null2Str(str[16]));
				}
				prest.addBatch();
			}
		} catch (SQLException e) {
			System.out.println("出错的数据行："+lineData);
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成导入日志
	 * @param rows
	 * @param dataNum
	 * @param fileName
	 * @param flag 1：成功 ，0：失败
	 */
	public static void createImportLogByVisitLog(int rows,int dataNum,String fileName,int flag){
		Record r = new Record();
		r.set("targetTable", Constant.VISIT_LOG_TABLE_NAME).set("rowsNo", rows-dataNum+1).set("rowsNum", dataNum).set("fileName", fileName)
		.set("successFlag", flag).set("createTime", new Date());
		Db.save("record_import_log", r);
	}
	

}

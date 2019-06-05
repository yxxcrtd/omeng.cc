package com.shanjin.bulkReplication;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 回放捕获的SQL到从库
 * @author Revoke
 *
 */
public class PlayBack {
	private static final Logger logger = Logger.getLogger(PlayBack.class);
	
	private static String  getSql = "select * from replicate_trace where isProcessed=0 order by addTime asc limit 100";
	
	private static String  updateSql="update replicate_trace set isProcessed=1 where id=?";
	
	private static String  cleanSql = " delete  from replicate_trace where isProcessed=1 and addTime < date_sub(now(),interval 2 day)";
	
	
	public static void main(String[] args) {
			
				PlayBack playBack = new PlayBack();
				playBack.process();
		
	}
	
	private void  process() {
			Connection conn =null;
			long  i = 1;
			while(true) {
				try {
					  conn = getConnection();
				} catch (Exception e) {
					logger.error("数据库连接获取失败"+e.getMessage(), e);
					e.printStackTrace();
					mySleep(10);
					continue;
				}
				
				relaySql(conn,i++);
				
				clean(conn);
				
				mySleep(50);
			}
		
		
	}
	
	/**
	 * 按时间顺序依次执行捕获在replicate_trace 中的SQL 记录
	 * @param conn
	 * @throws SQLException
	 */
	private void relaySql(Connection conn,Long i){
			try {
					PreparedStatement stmt=conn.prepareStatement(getSql,ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
					Statement  relayStmt = conn.createStatement();
					ResultSet result = stmt.executeQuery();
					while (result.next()){
							String  toExecuteSql=result.getString("content");
							Long  id = result.getLong("id");
							String  tableName = result.getString("tableName");
							result.updateString("isProcessed", "1");
							result.updateRow();
							relayStmt.execute(toExecuteSql);
					}
					conn.commit();
			}catch(Exception e){
				    try {
						conn.rollback();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				    logger.error("回放SQL执行失败"+e.getMessage(),e);
				    e.printStackTrace();
				    return;
			}
			
			if (i%100==0) {
				logger.info("本次SQL回放完成");
			}
	}
	
	/**
	 * 清理
	 * @param conn
	 * @throws SQLException
	 */
	private void clean(Connection conn) {
		 try{
		   	PreparedStatement stmt = conn.prepareStatement(cleanSql);
			stmt.execute();
			conn.commit();
		 }catch(SQLException e){
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			logger.error("捕获的已回放SQL清理错误"+e.getMessage(), e); 
		 }
		 
	}
	

	private Connection getConnection() throws Exception {
		
	//	 dumpClassLoad();
		 
			Connection result =null;
			
			Properties properties = new Properties();
			properties.load(PlayBack.class.getResourceAsStream("/database.properties"));
			DriverManager.class.forName(properties.getProperty("mysql.driverClassName"));
			result = DriverManager.getConnection(properties.getProperty("mysql.url"),
					properties.getProperty("mysql.username"), properties.getProperty("mysql.password"));
			
			if(result!=null){
				 result.setAutoCommit(false);
			}
			return result;
	}
	
	
	private void dumpClassLoad(){
		 		ClassLoader loader=PlayBack.class.getClassLoader();
				System.out.println(loader);
				System.exit(1);
	}
	
	private void  mySleep(long millionSeconds){
		try {
			Thread.sleep(millionSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

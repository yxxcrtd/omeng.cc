package com.shanjin.log.analysis.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.common.util.DateUtil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.log.analysis.dao.VisitLogHandleDao;
import com.shanjin.log.analysis.util.DbUtil;

public class VisitLogHandleDaoImpl implements VisitLogHandleDao{

	@Override
	public void insertTerminalStatistics(Date logDate) {
		batchInsertTerminalStatistics(logDate);
	}
	
	/**
	 * 批量插入终端数据，每日从访问日志表中提取，按日统计pv和uv量，
	 * 分别从不同的维度生成统计数据（clientFlag,clientType,province,city,channel）
	 * @param logDate
	 */
	private void batchInsertTerminalStatistics(Date logDate) {
		batchInsertTerminalUvStatistics(logDate);
		batchInsertTerminalPvStatistics(logDate);
	}
	
	private void batchInsertTerminalUvStatistics(Date logDate) {
		System.out.println("开始批量插入终端uv统计信息");
		StringBuffer uvSb = new StringBuffer();
		uvSb.append(" SELECT t.clientFlag,t.clientType,t.province,t.city,t.channel,count(DISTINCT (t.uuid)) AS count ");
		uvSb.append(" FROM record_visit_log t ");
		uvSb.append(" WHERE DATE_FORMAT(t.logTime,'%Y-%m-%d')='").append(DateUtil.formatDate(DateUtil.DATE_YYYY_MM_DD_PATTERN, logDate)).append("'");
		uvSb.append(" GROUP BY t.clientFlag,t.clientType,t.province,t.city,t.channel ");
		
		List<Record> uvList = Db.find(uvSb.toString());
		Connection conn = DbUtil.getConnection();
		if(conn==null){
			return;
		}
        long s = System.currentTimeMillis();
		try {
			if(uvList!=null&&uvList.size()>0){
				String sql = "insert into record_terminal_uv_statistics(terminalNum,clientType,clientFlag,province,city,channel,visitTime,createTime) values(?,?,?,?,?,?,?,?)";
				PreparedStatement prest = conn.prepareStatement(sql);
				for(Record r:uvList){
					prest.setInt(1, StringUtil.nullToInteger(r.getLong("count"))); // terminalNum
					prest.setInt(2, r.getInt("clientType")); // clientType
					prest.setInt(3,  r.getInt("clientFlag")); // clientFlag
					prest.setString(4, r.getStr("province")); // province
					prest.setString(5, r.getStr("city")); // city
					prest.setString(6, r.getStr("channel")); // channel
					prest.setString(7, DateUtil.formatDate(DateUtil.DATE_YYYY_MM_DD_PATTERN, logDate)); // visitTime
					prest.setString(8, DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN, new Date())); // createTime	
					prest.addBatch();
				}
				prest.executeBatch();
				conn.commit();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	    long e = System.currentTimeMillis();
        System.out.println("批量插入终端uv统计信息"+uvList.size()+"条，耗时 ："+(e-s)+"ms");
	}
	
	private void batchInsertTerminalPvStatistics(Date logDate) {
		System.out.println("开始批量插入终端pv统计信息");
		StringBuffer pvSb = new StringBuffer();
		pvSb.append(" SELECT  t.uuid,t.clientFlag,t.clientType,t.province,t.city,t.channel,COUNT(1) AS count ");
		pvSb.append(" FROM record_visit_log t ");
		pvSb.append(" WHERE DATE_FORMAT(t.logTime,'%Y-%m-%d')='").append(DateUtil.formatDate(DateUtil.DATE_YYYY_MM_DD_PATTERN, logDate)).append("'");
		pvSb.append(" GROUP BY t.uuid,t.clientFlag,t.clientType,t.province,t.city,t.channel ");
		
		List<Record> pvList = Db.find(pvSb.toString());
		Connection conn = DbUtil.getConnection();
		if(conn==null){
			return;
		}
        long s = System.currentTimeMillis();
		try {
			if(pvList!=null&&pvList.size()>0){
				String sql = "insert into record_terminal_pv_statistics(visitNum,clientType,clientFlag,province,city,channel,visitTime,createTime) values(?,?,?,?,?,?,?,?)";
				PreparedStatement prest = conn.prepareStatement(sql);
				for(Record r:pvList){
					prest.setInt(1, StringUtil.nullToInteger(r.getLong("count"))); // visitNum
					prest.setInt(2, r.getInt("clientType")); // clientType
					prest.setInt(3,  r.getInt("clientFlag")); // clientFlag
					prest.setString(4, r.getStr("province")); // province
					prest.setString(5, r.getStr("city")); // city
					prest.setString(6, r.getStr("channel")); // channel
					prest.setString(7, DateUtil.formatDate(DateUtil.DATE_YYYY_MM_DD_PATTERN, logDate)); // visitTime
					prest.setString(8, DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN, new Date())); // createTime	
					prest.addBatch();
				}
				prest.executeBatch();
				conn.commit();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	    long e = System.currentTimeMillis();
        System.out.println("批量插入终端pv统计信息"+pvList.size()+"条，耗时 ："+(e-s)+"ms");
	}

	@Override
	public void insertFirstVisitTerminal(Date logDate) {
		batchInsertFirstVisitTerminal(logDate);
	}
	
	/**
	 * 批量插入每日新增终端数据，从访问日志表中提取，对比过去已经访问的终端数据，
	 * 过去已访问，不记录，新的终端才记录入库，作为新终端，对o盟平台的用户和商户端区分
	 * 终端以uuid区分标识，以clientFlag区分用户或商户
	 * @param logDate
	 */
	private void batchInsertFirstVisitTerminal(Date logDate) {
		System.out.println("批量插入新增终端记录");
		StringBuffer logDateSb = new StringBuffer();
		logDateSb.append(" SELECT * FROM record_visit_log t WHERE DATE_FORMAT(t.logTime,'%Y-%m-%d')='").append(DateUtil.formatDate(DateUtil.DATE_YYYY_MM_DD_PATTERN, logDate)).append("'");
		logDateSb.append(" GROUP BY t.uuid,t.clientFlag ");
		List<Record> visitTerminalList = Db.find(logDateSb.toString()); // logDate这一天访问的终端用户
		if(visitTerminalList==null||visitTerminalList.size()<1){
			return;
		}
		
		StringBuffer hisSb = new StringBuffer();
		hisSb.append(" SELECT * FROM record_first_visit_terminal t WHERE DATE_FORMAT(t.firstVisitTime,'%Y-%m-%d')<'").append(DateUtil.formatDate(DateUtil.DATE_YYYY_MM_DD_PATTERN, logDate)).append("'");
		hisSb.append(" GROUP BY t.uuid,t.clientFlag ");
		List<Record> historyTerminalList = Db.find(hisSb.toString()); // logDate之前的终端记录，已入库的终端信息
		Map<String,String> map = new HashMap<String,String>();
		if(historyTerminalList!=null&&historyTerminalList.size()>0){
			for(Record r : historyTerminalList){
				map.put(r.getStr("uuid")+"_"+r.getInt("clientFlag"), "1");
			}
		}

		Connection conn = DbUtil.getConnection();
		if(conn==null){
			return;
		}
        long s = System.currentTimeMillis();
        int num = 0; // 新增终端记录数
		try {
			if(visitTerminalList!=null&&visitTerminalList.size()>0){
				String sql = "insert into record_first_visit_terminal(uuid,ua,firstVisitTime,clientFlag,clientType,channel,province,city,model,resolution,system,version,createTime) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
				PreparedStatement prest = conn.prepareStatement(sql);
				String key = "";
				for(Record r:visitTerminalList){
					key = r.getStr("uuid")+"_"+r.getInt("clientFlag");
					if(!map.containsKey(key)){
						// 该终端不在历史终端列表中，为新终端，需入库
						prest.setString(1, r.getStr("uuid")); // uuid
						prest.setString(2, r.getStr("ua")); // ua
						prest.setString(3, DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN,logDate)); // firstVisitTime
						prest.setInt(4,  r.getInt("clientFlag")); // clientFlag
						prest.setInt(5, r.getInt("clientType")); // clientType
						prest.setString(6, r.getStr("channel")); // channel
						prest.setString(7, r.getStr("province")); // province
						prest.setString(8, r.getStr("city")); // city
						prest.setString(9, r.getStr("model")); // model
						prest.setString(10, r.getStr("resolution")); // resolution
						prest.setString(11, r.getStr("system")); // system
						prest.setString(12, r.getStr("version")); // version
						prest.setString(13, DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN, new Date())); // createTime	
						prest.addBatch();
						num++;
					}
				}
				if(num>0){
					prest.executeBatch();
					conn.commit();	
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	    long e = System.currentTimeMillis();
        System.out.println("批量插入新增终端记录"+num+"条，耗时 ："+(e-s)+"ms");
	}
	
	public static void main(String[] args){
		int num = 1000000;
		Map<String,String> map = new HashMap<String,String>();
		long b = System.currentTimeMillis();
		for(int i=0;i<num;i++){
			map.put("clientFlag_"+i, "clientFlag"+i);
		}
		int size = map.size();
		System.out.println("map大小为："+size);
		long a = System.currentTimeMillis();
		System.out.println("循环"+num+"次耗时："+(a-b));
		String h = map.get("clientFlag_100");
        long e = System.currentTimeMillis();
		System.out.println("取map耗时："+(e-a));
		String n = map.get("clientFlag100");
		System.out.println("h = "+h+"  n="+n);
	}

}

package com.shanjin.manager.dao.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.constant.Constant.PAGE;
import com.shanjin.manager.constant.Constant.SORT;
import com.shanjin.manager.dao.MerchantTerminalDao;
import com.shanjin.manager.utils.DateUtil;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

public class MerchantTerminalDaoImpl implements MerchantTerminalDao{
	public static final int MER_CLIENT_FLAG = 1; //客户端标识（1：商户，2：用户）
	@Override
	public List<Record> merchantAreaList(Map<String, String[]> param) {
		StringBuffer addSb = new StringBuffer();
		StringBuffer activitySb = new StringBuffer();
		addSb.append(" SELECT t.province,COUNT(1) as addNum FROM record_first_visit_terminal t ");
		addSb.append("  WHERE t.clientFlag=").append(MER_CLIENT_FLAG);
		activitySb.append(" SELECT t.province,COUNT(DISTINCT(t.uuid)) as activityNum FROM record_day_visit_terminal t ");
		activitySb.append("  WHERE t.clientFlag=").append(MER_CLIENT_FLAG);
		if(StringUtil.isNotNullMap(param,"startTime")){
			String startTime = StringUtil.null2Str(param.get("startTime")[0]);
			startTime = startTime.substring(0, 10);
			addSb.append("  AND DATE_FORMAT(t.firstVisitTime,'%Y-%m-%d')>='").append(startTime).append("'");
			activitySb.append("  AND DATE_FORMAT(t.visitTime,'%Y-%m-%d')>='").append(startTime).append("'");
		}
		if(StringUtil.isNotNullMap(param,"endTime")){
			String endTime = StringUtil.null2Str(param.get("endTime")[0]);
			endTime = endTime.substring(0, 10);
			addSb.append("  AND DATE_FORMAT(t.firstVisitTime,'%Y-%m-%d')<='").append(endTime).append("'");
			activitySb.append("  AND DATE_FORMAT(t.visitTime,'%Y-%m-%d')<='").append(endTime).append("'");
		}
		addSb.append("  GROUP BY t.province");
		activitySb.append("  GROUP BY t.province");
	    List<Record> addList = Db.use("ana_main").find(addSb.toString());
	    List<Record> activityList = Db.use("ana_main").find(activitySb.toString());
	    List<Record> list = new ArrayList<Record>();
	    if(addList!=null&&addList.size()>0){
	    	for(Record r : addList){
				if(StringUtil.isNullStr(r.getStr("province"))){
					r.set("province", "未知");
				}
	    		r.set("activityNum", 0);
	    		list.add(r);
	    	}
	    }
	    if(activityList!=null&&activityList.size()>0){
	    	for(Record r : activityList){
				if(StringUtil.isNullStr(r.getStr("province"))){
					r.set("province", "未知");
				}
	    		if(!isExistInProvince(r,list)){
		    		r.set("addNum", 0);	
		    		list.add(r);
	    		}
	    	}
	    }
	    if(list!=null&&list.size()>0){
	    	list.get(0).set("total", StringUtil.nullToLong(list.size()));
	    }
		return list;
	}
	private boolean isExistInProvince(Record record, List<Record> list){
		boolean flag = false;
		if(list!=null&&list.size()>0){
			for(Record r:list){
				if(record.getStr("province").equals(r.getStr("province"))){
					r.set("activityNum", record.getLong("activityNum"));
					flag = true;
					break;
				}
			}
		}
		return flag;
	}

	@Override
	public List<Record> merchantAreaPieList(Map<String, String[]> param) {
		return null;
	}

	@Override
	public List<Record> merchantAreaColList(Map<String, String[]> param) {
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT t.province,COUNT(1) as num FROM record_first_visit_terminal t WHERE t.clientFlag=").append(MER_CLIENT_FLAG);;
		sb.append(" GROUP BY t.province");
		List<Record> list = Db.use("ana_main").find(sb.toString());
		if(list!=null&&list.size()>0){
			for(Record r :list){
				if(StringUtil.isNullStr(r.getStr("province"))){
					r.set("province", "未知");
				}
			}
	         list.get(0).set("total", StringUtil.nullToLong(list.size()));
		}
		return list;
	}

	@Override
	public List<Record> merchantVersionList(Map<String, String[]> param) {
		return getMerchantVersionList(param);
	}

	private List<Record> getMerchantVersionList(Map<String, String[]> param){
		List<Record> list = new ArrayList<Record>();
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT t.currentVersion as version,COUNT(1) as num FROM record_first_visit_terminal t  WHERE t.clientFlag=").append(MER_CLIENT_FLAG);
		if(StringUtil.isNotNullMap(param,"version")){
			String version = StringUtil.null2Str(param.get("version")[0]);
			sb.append(" AND t.currentVersion LIKE '").append(version).append("%'");
		}
		sb.append(" GROUP BY t.currentVersion");
		list=Db.use("ana_main").find(sb.toString());
		if(list!=null&&list.size()>0){
	         list.get(0).set("total", StringUtil.nullToLong(list.size()));
		}
		return list;
	}
	
	@Override
	public List<Record> merchantVersionPieList(Map<String, String[]> param) {
		return getMerchantVersionList(param);
	}

	@Override
	public List<Record> merchantVersionColList(Map<String, String[]> param) {
		return getMerchantVersionList(param);
	}

	@Override
	public List<Record> merchantDeviceList(Map<String, String[]> param) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql = new StringBuffer();
		totalSql.append(" SELECT COUNT(1) FROM (SELECT count(1) AS num,t.model FROM record_first_visit_terminal t ");
		totalSql.append(" WHERE t.clientFlag=").append(MER_CLIENT_FLAG);
		sql.append(" SELECT COUNT(1) AS num,t.model FROM record_first_visit_terminal t ");
		sql.append(" WHERE t.clientFlag=").append(MER_CLIENT_FLAG);
		if(StringUtil.isNotNullMap(param,"model")){
			String model = StringUtil.null2Str(param.get("model")[0]);
			sql.append(" and t.model LIKE '%").append(model).append("%'");
			totalSql.append(" and t.model LIKE '%").append(model).append("%'");
		}
		totalSql.append("  GROUP BY t.model ) a ");
		sql.append(" GROUP BY t.model ");
		String property = "num";
		String direction = SORT.DESC;
		if(StringUtil.isNotNullMap(param,"sort")){
			Map<String,String> sortMap = Util.sortMap(StringUtil.null2Str(param.get("sort")[0]));
			if(sortMap!=null){
				if(!StringUtil.isNullStr(sortMap.get("property"))){
					property = StringUtil.null2Str(sortMap.get("property"));
				}
				if(!StringUtil.isNullStr(sortMap.get("direction"))){
					direction = StringUtil.null2Str(sortMap.get("direction"));
				}
			}
		}
		
		long total=Db.use("ana_main").queryLong(totalSql.toString());
		sql.append(" ORDER BY ").append(property).append(" ").append(direction);
		int start = 0;
		if(StringUtil.isNotNullMap(param,"start")){
			start = StringUtil.nullToInteger(param.get("start")[0]);
		}
		int pageSize = PAGE.PAGESIZE;
		if(StringUtil.isNotNullMap(param,"limit")){
			pageSize = StringUtil.nullToInteger(param.get("limit")[0]);
		}
		sql.append(" limit ");
		sql.append(start);
		sql.append(",");
		sql.append(pageSize);

        List<Record> list = Db.use("ana_main").find(sql.toString());
        if(list!=null&&list.size()>0){
            long totalDevice = Db.use("ana_main").queryLong(" SELECT COUNT(1) AS num FROM record_first_visit_terminal t WHERE t.clientFlag=?",MER_CLIENT_FLAG);
        	for(Record r:list){
    			if(totalDevice>0){
    				r.set("rate", r.getLong("num")*100/totalDevice+"%");
    			}else{
    				r.set("rate", "0");
    			}
        	}
            list.get(0).set("total", total);
        }
		return list;
	}

	@Override
	public List<Record> merchantDevicePieList(Map<String, String[]> param) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT COUNT(1) as num, (CASE t.clientType WHEN 1 THEN 'aphone' ELSE 'iphone' END) AS clientType FROM record_first_visit_terminal t  ");
		sql.append(" WHERE t.clientFlag=").append(MER_CLIENT_FLAG);
		sql.append(" GROUP BY t.clientType ");
        List<Record> list = Db.use("ana_main").find(sql.toString());
		return list;
	}

	@Override
	public List<Record> merchantDeviceColList(Map<String, String[]> param) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT COUNT(1) AS num,t.model FROM record_first_visit_terminal t ");
		sql.append(" WHERE t.clientFlag=").append(MER_CLIENT_FLAG);
		sql.append(" GROUP BY t.model ORDER BY num DESC ");
        List<Record> list = Db.use("ana_main").find(sql.toString());
        List<Record> deviceStatisticList = new ArrayList<Record>(); 
        if(list!=null&&list.size()>0){
        	long totalNum = 0; // 安装商户端app 的总终端数量
        	int index = 0 ;
        	long otherNum = 0; // top 10以外的机型数量
        	for(Record r:list){
        		totalNum = totalNum + StringUtil.nullToLong(r.getLong("num"));
        		index++;
        		if(index<=10){
        			// 只统计top 10
        			deviceStatisticList.add(r);
        		}else{
        			// top 10以外
        			otherNum = otherNum + StringUtil.nullToLong(r.getLong("num"));
        		}
        	}
        	if(index>10&&otherNum>0){
        		// 其他机型
        		Record other = new Record();
        		other.set("model", "其他").set("num", otherNum);
      			deviceStatisticList.add(other);
        	}
        	if(deviceStatisticList!=null&&deviceStatisticList.size()>0){
        		for(Record r :deviceStatisticList){
        			if(totalNum>0){
        				r.set("rate", r.getLong("num")*100/totalNum);
        			}else{
        				r.set("rate", 0);
        			}
        		}
        	}
        }
		return deviceStatisticList;
	}

	@Override
	public List<Record> merchantChannelList(Map<String, String[]> param) {
		StringBuffer addSql = new StringBuffer(); // 新增用户
		StringBuffer activitySql = new StringBuffer(); // 活跃用户
		String startTime = "";
		if(StringUtil.isNotNullMap(param,"startTime")){
			startTime = StringUtil.null2Str(param.get("startTime")[0]);
			startTime = startTime.substring(0, 10);
		}
		if(StringUtil.isNullStr(startTime)){
			//  没有默认日，设置为昨天
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.DATE, -1);
			Date yesterday = calendar.getTime();
			startTime = DateUtil.formatDate(DateUtil.DATE_YYYY_MM_DD_PATTERN, yesterday);
		}
		addSql.append(" SELECT t.channel,count(1) AS addNum  FROM record_channel_terminal t ");
		addSql.append(" WHERE t.clientFlag=").append(MER_CLIENT_FLAG);
		addSql.append(" AND DATE_FORMAT(t.logTime,'%Y-%m-%d')>='").append(startTime).append("'");
		if(StringUtil.isNotNullMap(param,"endTime")){
			String endTime = StringUtil.null2Str(param.get("endTime")[0]);
			endTime = endTime.substring(0, 10);
			addSql.append(" AND DATE_FORMAT(t.logTime,'%Y-%m-%d')<='").append(endTime).append("'");
		}
		addSql.append(" GROUP BY t.channel ");
		
		activitySql.append(" SELECT t.channel,COUNT(1) AS activityNum FROM record_day_visit_terminal t  ");
		activitySql.append(" WHERE t.clientFlag=").append(MER_CLIENT_FLAG);
		activitySql.append(" AND DATE_FORMAT(t.visitTime,'%Y-%m-%d')>='").append(startTime).append("'");
		if(StringUtil.isNotNullMap(param,"endTime")){
			String endTime = StringUtil.null2Str(param.get("endTime")[0]);
			endTime = endTime.substring(0, 10);
			activitySql.append(" AND DATE_FORMAT(t.visitTime,'%Y-%m-%d')<='").append(endTime).append("'");
		}
		activitySql.append(" GROUP BY t.channel ");
	    List<Record> addList = Db.use("ana_main").find(addSql.toString());
	    List<Record> activityList = Db.use("ana_main").find(activitySql.toString());
	    List<Record> list = new ArrayList<Record>();
	    if(addList!=null&&addList.size()>0){
	    	for(Record r : addList){
	    		r.set("activityNum", 0);
	    		list.add(r);
	    	}
	    }
	    if(activityList!=null&&activityList.size()>0){
	    	for(Record r : activityList){
	    		if(!isExistInChannel(r,list)){
	    			// 
		    		r.set("addNum", 0);	
		    		list.add(r);
	    		}
	    	}
	    }
	    if(list!=null&&list.size()>0){
	    	list.get(0).set("total", StringUtil.nullToLong(list.size()));
	    }
		return list;
	}
	
	private boolean isExistInChannel(Record record, List<Record> list){
		boolean flag = false;
		if(list!=null&&list.size()>0){
			for(Record r:list){
				if(record.getStr("channel").equals(r.getStr("channel"))){
					r.set("activityNum", record.getLong("activityNum"));
					flag = true;
					break;
				}
			}
		}
		return flag;
	}

	@Override
	public List<Record> merchantChannelPieList(Map<String, String[]> param) {
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT t.channel,COUNT(1) AS num FROM record_channel_terminal t WHERE t.clientFlag=").append(MER_CLIENT_FLAG);
		sb.append(" GROUP BY t.channel ");
	    List<Record> list = Db.use("ana_main").find(sb.toString());
		return list;
	}
	
	@Override
	public List<Record> merchantChannelColList(Map<String, String[]> param) {
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT t.channel,COUNT(1) AS num FROM record_channel_terminal t WHERE t.clientFlag=").append(MER_CLIENT_FLAG);
		sb.append(" GROUP BY t.channel ");
	    List<Record> list = Db.use("ana_main").find(sb.toString());
		return list;
	}

	@Override
	public List<Record> merchantStartUpList(Map<String, String[]> param) {
		StringBuffer sb = new StringBuffer();
		StringBuffer totalSb = new StringBuffer();
		totalSb.append(" SELECT COUNT(1) FROM (SELECT count(1) ");
		totalSb.append(" FROM record_start_up_statistic t ");
		totalSb.append(" WHERE t.clientFlag=").append(MER_CLIENT_FLAG);
		
		sb.append(" SELECT m.*,IF(a.aphoneStartNum IS NULL,0,a.aphoneStartNum) AS aphoneStartNum ,IF(b.iphoneStartNum IS NULL,0,b.iphoneStartNum) AS iphoneStartNum FROM ");
		sb.append(" (SELECT SUM(t.startNum) AS startNum,DATE_FORMAT(t.visitDay,'%Y-%m-%d') AS visitDay FROM record_start_up_statistic t WHERE t.clientFlag=").append(MER_CLIENT_FLAG);
		sb.append(" GROUP BY DATE_FORMAT(t.visitDay,'%Y-%m-%d')) m  LEFT JOIN ");
		
		sb.append(" (SELECT SUM(t.startNum) AS aphoneStartNum,DATE_FORMAT(t.visitDay,'%Y-%m-%d') AS visitDay FROM record_start_up_statistic t WHERE t.clientFlag=").append(MER_CLIENT_FLAG);
		sb.append(" AND t.clientType=1 GROUP BY DATE_FORMAT(t.visitDay,'%Y-%m-%d')) a ");
		
		sb.append(" ON m.visitDay=a.visitDay LEFT JOIN  ");
		sb.append(" (SELECT SUM(t.startNum) AS iphoneStartNum,DATE_FORMAT(t.visitDay,'%Y-%m-%d') AS visitDay FROM record_start_up_statistic t WHERE t.clientFlag=").append(MER_CLIENT_FLAG);
		sb.append(" AND t.clientType=2 GROUP BY DATE_FORMAT(t.visitDay,'%Y-%m-%d')) b ");
		sb.append(" ON m.visitDay=b.visitDay ");
		sb.append(" WHERE 1=1 ");
		if(StringUtil.isNotNullMap(param,"startTime")){
			String startTime = StringUtil.null2Str(param.get("startTime")[0]);
			startTime = startTime.substring(0, 10);
			sb.append(" and DATE_FORMAT(m.visitDay,'%Y-%m-%d')>='").append(startTime).append("'");
			totalSb.append(" and DATE_FORMAT(t.visitDay,'%Y-%m-%d')>='").append(startTime).append("'");
		}
		
		if(StringUtil.isNotNullMap(param,"endTime")){
			String endTime = StringUtil.null2Str(param.get("endTime")[0]);
			endTime = endTime.substring(0, 10);
			sb.append(" and DATE_FORMAT(m.visitDay,'%Y-%m-%d')<='").append(endTime).append("'");
			totalSb.append(" and DATE_FORMAT(t.visitDay,'%Y-%m-%d')<='").append(endTime).append("'");
		}

		totalSb.append(" GROUP BY DATE_FORMAT(t.visitDay,'%Y-%m-%d')) a ");
		long total=Db.use("ana_main").queryLong(totalSb.toString());
		String property = "visitDay";
		String direction = SORT.DESC;
		if(StringUtil.isNotNullMap(param,"sort")){
			Map<String,String> sortMap = Util.sortMap(StringUtil.null2Str(param.get("sort")[0]));
			if(sortMap!=null){
				if(!StringUtil.isNullStr(sortMap.get("property"))){
					property = StringUtil.null2Str(sortMap.get("property"));
				}
				if(!StringUtil.isNullStr(sortMap.get("direction"))){
					direction = StringUtil.null2Str(sortMap.get("direction"));
				}
			}
		}
		sb.append(" ORDER BY ").append(property).append(" ").append(direction);
		int start = 0;
		if(StringUtil.isNotNullMap(param,"start")){
			start = StringUtil.nullToInteger(param.get("start")[0]);
		}
		int pageSize = PAGE.PAGESIZE;
		if(StringUtil.isNotNullMap(param,"limit")){
			pageSize = StringUtil.nullToInteger(param.get("limit")[0]);
		}
		sb.append(" limit ");
		sb.append(start);
		sb.append(",");
		sb.append(pageSize);
		List<Record> list = Db.use("ana_main").find(sb.toString());
		if(list!=null&&list.size()>0){
	        list.get(0).set("total", total);
		}
		return list;
	}

	@Override
	public List<Record> merchantStartUpLineList(Map<String, String[]> param) {
		String visitDay = "";
		if(StringUtil.isNotNullMap(param,"visitDay")){
			visitDay = StringUtil.null2Str(param.get("visitDay")[0]);
		}
		if(StringUtil.isNullStr(visitDay)){
			//  没有默认日，设置为昨天
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.DATE, -1);
			Date yesterday = calendar.getTime();
			visitDay = DateUtil.formatDate(DateUtil.DATE_YYYY_MM_DD_PATTERN, yesterday);
		}
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT m.*,IF(a.aphoneStartNum IS NULL,0,a.aphoneStartNum) AS aphone ,IF(b.iphoneStartNum IS NULL,0,b.iphoneStartNum) AS iphone  FROM ");
		sb.append(" (SELECT SUM(t.startNum) AS total,t.`hour` FROM record_start_up_statistic t WHERE t.clientFlag=").append(MER_CLIENT_FLAG);
		sb.append(" AND DATE_FORMAT(t.visitDay,'%Y-%m-%d')='").append(visitDay).append("'  GROUP BY t.`hour`) m LEFT JOIN ");
		
		sb.append(" (SELECT SUM(t.startNum) AS aphoneStartNum,t.`hour` FROM record_start_up_statistic t WHERE t.clientFlag=").append(MER_CLIENT_FLAG);
		sb.append(" AND DATE_FORMAT(t.visitDay,'%Y-%m-%d')='").append(visitDay).append("'");
		
		sb.append(" AND t.clientType=1 GROUP BY t.`hour`) a ON m.`hour`=a.`hour` LEFT JOIN  ");
		sb.append(" (SELECT SUM(t.startNum) AS iphoneStartNum,t.`hour` FROM record_start_up_statistic t WHERE t.clientFlag=").append(MER_CLIENT_FLAG);
		sb.append(" AND DATE_FORMAT(t.visitDay,'%Y-%m-%d')='").append(visitDay).append("'");
		
		sb.append(" AND t.clientType=2 GROUP BY t.`hour`) b ON m.`hour`=b.`hour` ");
		List<Record> list = Db.use("ana_main").find(sb.toString());
		Map<String,String[]> map = new HashMap<String,String[]>();//查询数据库得到某日各小时的启动次数
		if(list!=null&&list.size()>0){
			for(Record r : list){
				map.put(StringUtil.null2Str(r.getInt("hour")),
						new String[]{StringUtil.null2Str(r.getBigDecimal("total")),StringUtil.null2Str(r.getBigDecimal("aphone")),
					    StringUtil.null2Str(r.getBigDecimal("iphone"))} );
			}
		}
		List<Record> resultList = new ArrayList<Record>();
		for(int i =0 ;i<24;i++){
			//设置一天24小时的启动次数，没有启动，补0
			Record r = new Record();
			r.set("hour", i+"");
			if(map.containsKey(i+"")){
				String [] nums = map.get(i+"");
				r.set("total", StringUtil.nullToInteger(nums[0]));
				r.set("aphone", StringUtil.nullToInteger(nums[1]));
				r.set("iphone", StringUtil.nullToInteger(nums[2]));
			}else{
				r.set("total", 0);
				r.set("aphone", 0);
				r.set("iphone", 0);
			}
			resultList.add(r);
		}
		return resultList;
	}

}

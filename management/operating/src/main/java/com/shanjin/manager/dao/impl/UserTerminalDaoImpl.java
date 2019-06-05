package com.shanjin.manager.dao.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.UserChannel;
import com.shanjin.manager.Bean.UserStartUp;
import com.shanjin.manager.Bean.UserVisit;
import com.shanjin.manager.constant.Constant.PAGE;
import com.shanjin.manager.constant.Constant.SORT;
import com.shanjin.manager.dao.UserTerminalDao;
import com.shanjin.manager.utils.DateUtil;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

/**
 * 用户终端统计实现类
 * 项目名称：manager
 * 类名称：UserTerminalDaoImpl 
 * 类描述：
 * 创建人：Huang yulai
 * 创建时间：2016年3月1日 下午2:38:17
 * 修改人：
 * 修改时间：
 * 修改备注：
 * @version V1.0
 */
public class UserTerminalDaoImpl implements UserTerminalDao{
	@Override
	public List<Record> userAreaList(Map<String, String[]> param) {
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT a.province,a.city,accumulateNum,  IFNULL(addNum,0) AS addNum,IFNULL(activityNum,0) AS activityNum FROM ");
		StringBuffer addSb = new StringBuffer();
		StringBuffer activitySb = new StringBuffer();
		StringBuffer accumulateSb = new StringBuffer();
		addSb.append(" SELECT (CASE t.province WHEN '' THEN '未知' ELSE t.province END ) AS province,(CASE t.city WHEN '' THEN '未知' ELSE t.city END ) AS city,COUNT(1) as addNum ");
		addSb.append(" FROM record_first_visit_terminal t ");
		addSb.append("  WHERE 1=1 ");
		accumulateSb.append(" SELECT (CASE t.province WHEN '' THEN '未知' ELSE t.province END ) AS province,(CASE t.city WHEN '' THEN '未知' ELSE t.city END ) AS city,COUNT(1) as accumulateNum ");
		accumulateSb.append(" FROM record_first_visit_terminal t ");
		accumulateSb.append("  WHERE 1=1 ");
		activitySb.append(" SELECT (CASE t.province WHEN '' THEN '未知' ELSE t.province END ) AS province,(CASE t.city WHEN '' THEN '未知' ELSE t.city END ) AS city,COUNT(DISTINCT(t.uuid)) as activityNum ");
		activitySb.append(" FROM record_day_visit_terminal t ");
		activitySb.append("  WHERE 1=1 ");
		if(StringUtil.isNotNullMap(param,"province")){
			String province = StringUtil.null2Str(param.get("province")[0]);
			addSb.append("  AND t.province='").append(province).append("'");
			accumulateSb.append("  AND t.province='").append(province).append("'");
			activitySb.append("  AND t.province='").append(province).append("'");
		}
		if(StringUtil.isNotNullMap(param,"city")){
			String city = StringUtil.null2Str(param.get("city")[0]);
			addSb.append("  AND t.city='").append(city).append("'");
			accumulateSb.append("  AND t.city='").append(city).append("'");
			activitySb.append("  AND t.city='").append(city).append("'");
		}
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
		if(StringUtil.isNotNullMap(param,"dimension")){
			// 有分组
			String[] dimension = param.get("dimension");
			int group = 0; // 0:省，1：市，2：省&市
			if(dimension!=null&&dimension.length>0){
				group = dimension.length;
				addSb.append("  GROUP BY ");
				accumulateSb.append("  GROUP BY ");
				activitySb.append("  GROUP BY ");
				for(int i=0;i<dimension.length;i++){
					if(group!=2){
						if(dimension[i].equals("city")){
							group = 1;
						}
					}
					addSb.append(" t.").append(dimension[i]);
					accumulateSb.append(" t.").append(dimension[i]);
					activitySb.append(" t.").append(dimension[i]);
					if(i!=dimension.length-1){
						addSb.append(",");
						accumulateSb.append(",");
						activitySb.append(",");
					}
				}
				sb.append(" (").append(accumulateSb).append(") a ");
				sb.append(" LEFT JOIN ");
				sb.append(" (").append(addSb).append(") b ");
				if(group==0){
					sb.append(" ON a.province=b.province LEFT JOIN ");
				}else if(group==1){
					sb.append(" ON a.city=b.city LEFT JOIN ");
				}else if(group==2){
					sb.append(" ON a.province=b.province AND a.city=b.city LEFT JOIN ");
				}
				sb.append(" (").append(activitySb).append(") c ");
				if(group==0){
					sb.append(" ON a.province=c.province ");
				}else if(group==1){
					sb.append(" ON a.city=c.city ");
				}else if(group==2){
					sb.append(" ON a.province=c.province AND a.city=c.city ");
				}
			}
		}else{
			// 无分组
			sb.append(" (").append(accumulateSb).append(") a, ");
			sb.append(" (").append(addSb).append(") b, ");
			sb.append(" (").append(activitySb).append(") c ");
		}
		
	    List<Record> list = new ArrayList<Record>();
	    list = Db.use("ana_main").find(sb.toString());
	    if(list!=null&&list.size()>0){
	    	list.get(0).set("total", StringUtil.nullToLong(list.size()));
	    }
		return list;
	}

	@Override
	public List<Record> userAreaPieList(Map<String, String[]> param) {
		return null;
	}

	@Override
	public List<Record> userAreaColList(Map<String, String[]> param) {
		StringBuffer sb = new StringBuffer();
		StringBuffer sqlOther = new StringBuffer();
		sb.append(" SELECT COUNT(1) as num,t.");
		if(StringUtil.isNotNullMap(param,"dimension")){
			String dimension = param.get("dimension")[0];
			sb.append(dimension).append(" AS areaName");
		}else{
			sb.append(" province AS areaName");
		}
		sb.append(" FROM record_first_visit_terminal t WHERE 1=1 ");
		if(StringUtil.isNotNullMap(param,"dimension")){
			
			String dimension = param.get("dimension")[0];
			sb.append(" AND t.").append(dimension).append("<>''");
			sb.append(" GROUP BY t.").append(dimension);
		}else{
			sb.append(" AND t.province<>'' GROUP BY t.province");
		}
		sb.append(" order by num desc limit 0,10");  // 统计数据量前十的
		sqlOther.append("select count(1) as num from record_first_visit_terminal t where 1=1");
		Long otherNum = Db.use("ana_main").queryLong(sqlOther.toString());
		List<Record> list = Db.use("ana_main").find(sb.toString());
		if(list!=null&&list.size()>0){
			for(Record r :list){
				otherNum = otherNum - StringUtil.nullToLong(r.getLong("num"));
			}
			Record rec=new Record();
			rec.set("areaName", "其他");
			rec.set("num", otherNum);
			list.add(rec);
	        list.get(0).set("total", StringUtil.nullToLong(list.size()+1));
		}
		return list;
	}

	@Override
	public List<Record> userVersionList(Map<String, String[]> param) {
		List<Record> list = new ArrayList<Record>();
		StringBuffer sb = new StringBuffer();
	//	sb.append(" SELECT t.currentVersion as version,COUNT(1) as num FROM record_first_visit_terminal t  WHERE t.clientFlag=").append(USER_CLIENT_FLAG);
		sb.append(" SELECT t.currentVersion as version,COUNT(1) as num FROM record_first_visit_terminal t  WHERE 1=1 ");
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
	public List<Record> userVersionPieList(Map<String, String[]> param) {
		return userVersionList(param);
	}

	@Override
	public List<Record> userVersionColList(Map<String, String[]> param) {
		return userVersionList(param);
	}

	@Override
	public List<Record> userDeviceList(Map<String, String[]> param) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql = new StringBuffer();
		totalSql.append(" SELECT COUNT(1) FROM ( ");

		sql.append(" SELECT COUNT(1) AS num,t.model,t.system, ");
		sql.append(" (CASE t.clientType WHEN 1 THEN 'aphone' ELSE 'iphone' END ) AS clientType ");
		sql.append(" FROM record_first_visit_terminal t ");
		sql.append(" WHERE 1=1 ");
		if(StringUtil.isNotNullMap(param,"model")){
			String model = StringUtil.null2Str(param.get("model")[0]);
			sql.append(" and t.model LIKE '%").append(model).append("%'");
		}
		if(StringUtil.isNotNullMap(param,"clientType")){
			int clientType = StringUtil.nullToInteger(param.get("clientType")[0]);
			sql.append(" and t.clientType=").append(clientType);
		}
		if(StringUtil.isNotNullMap(param,"system")){
			String system = StringUtil.null2Str(param.get("system")[0]);
			sql.append(" and t.system LIKE '%").append(system).append("%'");
		}
		if(StringUtil.isNotNullMap(param,"dimension")){
			String[] dimension=param.get("dimension");
			 sql.append(" GROUP BY ");
			 if(dimension!=null&&dimension.length>0){
				 for(int i = 0;i<dimension.length;i++){
					 sql.append("t."+dimension[i]);
					 if(i!=dimension.length-1){
						 sql.append(",");
					 }
				 }
			 }
		}
		totalSql.append(sql);
		totalSql.append(") a");
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
      //      long totalDevice = Db.use("ana_main").queryLong(" SELECT COUNT(1) AS num FROM record_first_visit_terminal t WHERE t.clientFlag=?",USER_CLIENT_FLAG);
            long totalDevice = Db.use("ana_main").queryLong(" SELECT COUNT(1) AS num FROM record_first_visit_terminal t WHERE 1=1 ");
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
	public List<Record> userDevicePieList(Map<String, String[]> param) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT COUNT(1) as num, (CASE t.clientType WHEN 1 THEN 'aphone' ELSE 'iphone' END) AS clientType FROM record_first_visit_terminal t  ");
		sql.append(" WHERE 1=1 ");
		//sql.append(" WHERE t.clientFlag=").append(USER_CLIENT_FLAG);
		sql.append(" GROUP BY t.clientType ");
        List<Record> list = Db.use("ana_main").find(sql.toString());
		return list;
	}

	@Override
	public List<Record> userDeviceColList(Map<String, String[]> param) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT COUNT(1) AS num,t.model FROM record_first_visit_terminal t ");
		//sql.append(" WHERE t.clientFlag=").append(USER_CLIENT_FLAG);
		sql.append(" WHERE 1=1 ");
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
	public List<Record> userStartUpList(Map<String, String[]> param) {
		StringBuffer sb = new StringBuffer();
		StringBuffer totalSb = new StringBuffer();
		totalSb.append(" SELECT COUNT(1) FROM (SELECT count(1) ");
		totalSb.append(" FROM record_start_up_statistic t ");
		//totalSb.append(" WHERE t.clientFlag=").append(USER_CLIENT_FLAG);
		totalSb.append(" WHERE 1=1 ");
		
		sb.append(" SELECT m.*,IF(a.aphoneStartNum IS NULL,0,a.aphoneStartNum) AS aphoneStartNum ,IF(b.iphoneStartNum IS NULL,0,b.iphoneStartNum) AS iphoneStartNum FROM ");
	//	sb.append(" (SELECT SUM(t.startNum) AS startNum,DATE_FORMAT(t.visitDay,'%Y-%m-%d') AS visitDay FROM record_start_up_statistic t WHERE t.clientFlag=").append(USER_CLIENT_FLAG);
		sb.append(" (SELECT SUM(t.startNum) AS startNum,DATE_FORMAT(t.visitDay,'%Y-%m-%d') AS visitDay FROM record_start_up_statistic t WHERE 1=1 ");
		sb.append(" GROUP BY DATE_FORMAT(t.visitDay,'%Y-%m-%d')) m  LEFT JOIN ");
		
		//sb.append(" (SELECT SUM(t.startNum) AS aphoneStartNum,DATE_FORMAT(t.visitDay,'%Y-%m-%d') AS visitDay FROM record_start_up_statistic t WHERE t.clientFlag=").append(USER_CLIENT_FLAG);
		sb.append(" (SELECT SUM(t.startNum) AS aphoneStartNum,DATE_FORMAT(t.visitDay,'%Y-%m-%d') AS visitDay FROM record_start_up_statistic t WHERE 1=1 ");
		sb.append(" AND t.clientType=1 GROUP BY DATE_FORMAT(t.visitDay,'%Y-%m-%d')) a ");
		
		sb.append(" ON m.visitDay=a.visitDay LEFT JOIN  ");
		//sb.append(" (SELECT SUM(t.startNum) AS iphoneStartNum,DATE_FORMAT(t.visitDay,'%Y-%m-%d') AS visitDay FROM record_start_up_statistic t WHERE t.clientFlag=").append(USER_CLIENT_FLAG);
		sb.append(" (SELECT SUM(t.startNum) AS iphoneStartNum,DATE_FORMAT(t.visitDay,'%Y-%m-%d') AS visitDay FROM record_start_up_statistic t WHERE 1=1 ");
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
	public List<Record> userStartUpLineList(Map<String, String[]> param) {
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
		//sb.append(" (SELECT SUM(t.startNum) AS total,t.`hour` FROM record_start_up_statistic t WHERE t.clientFlag=").append(USER_CLIENT_FLAG);
		sb.append(" (SELECT SUM(t.startNum) AS total,t.`hour` FROM record_start_up_statistic t WHERE 1=1 ");
		sb.append(" AND DATE_FORMAT(t.visitDay,'%Y-%m-%d')='").append(visitDay).append("'  GROUP BY t.`hour`) m LEFT JOIN ");
		
		//sb.append(" (SELECT SUM(t.startNum) AS aphoneStartNum,t.`hour` FROM record_start_up_statistic t WHERE t.clientFlag=").append(USER_CLIENT_FLAG);
		sb.append(" (SELECT SUM(t.startNum) AS aphoneStartNum,t.`hour` FROM record_start_up_statistic t WHERE 1=1 ");
		sb.append(" AND DATE_FORMAT(t.visitDay,'%Y-%m-%d')='").append(visitDay).append("'");
		
		sb.append(" AND t.clientType=1 GROUP BY t.`hour`) a ON m.`hour`=a.`hour` LEFT JOIN  ");
	//	sb.append(" (SELECT SUM(t.startNum) AS iphoneStartNum,t.`hour` FROM record_start_up_statistic t WHERE t.clientFlag=").append(USER_CLIENT_FLAG);
		sb.append(" (SELECT SUM(t.startNum) AS iphoneStartNum,t.`hour` FROM record_start_up_statistic t WHERE 1=1 ");
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

	@Override
	public List<Record> userChannelList(Map<String, String[]> param) {
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
	//	addSql.append(" WHERE t.clientFlag=").append(USER_CLIENT_FLAG);
		addSql.append(" WHERE 1=1 ");
		addSql.append(" AND DATE_FORMAT(t.logTime,'%Y-%m-%d')>='").append(startTime).append("'");
		if(StringUtil.isNotNullMap(param,"endTime")){
			String endTime = StringUtil.null2Str(param.get("endTime")[0]);
			endTime = endTime.substring(0, 10);
			addSql.append(" AND DATE_FORMAT(t.logTime,'%Y-%m-%d')<='").append(endTime).append("'");
		}
		addSql.append(" GROUP BY t.channel ");
		
		activitySql.append(" SELECT t.channel,COUNT(1) AS activityNum FROM record_day_visit_terminal t  ");
		//activitySql.append(" WHERE t.clientFlag=").append(USER_CLIENT_FLAG);
		activitySql.append(" WHERE 1=1 ");
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
	public List<Record> userChannelPieList(Map<String, String[]> param) {
		StringBuffer sb = new StringBuffer();
		//sb.append(" SELECT t.channel,COUNT(1) AS num FROM record_channel_terminal t WHERE t.clientFlag=").append(USER_CLIENT_FLAG);
		sb.append(" SELECT t.channel,COUNT(1) AS num FROM record_channel_terminal t WHERE 1=1 ");
		sb.append(" GROUP BY t.channel ");
	    List<Record> list = Db.use("ana_main").find(sb.toString());
		return list;
	}

	@Override
	public List<Record> userChannelColList(Map<String, String[]> param) {
		StringBuffer sb = new StringBuffer();
		//sb.append(" SELECT t.channel,COUNT(1) AS num FROM record_channel_terminal t WHERE t.clientFlag=").append(USER_CLIENT_FLAG);
		sb.append(" SELECT t.channel,COUNT(1) AS num FROM record_channel_terminal t WHERE 1=1 ");
		sb.append(" GROUP BY t.channel ");
	    List<Record> list = Db.use("ana_main").find(sb.toString());
		return list;
	}

	@Override
	public List<UserVisit> exportUserAreaListExcel(Map<String, String[]> param) {
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT a.province,a.city,accumulateNum,  IFNULL(addNum,0) AS addNum,IFNULL(activityNum,0) AS activityNum FROM ");
		StringBuffer addSb = new StringBuffer();
		StringBuffer activitySb = new StringBuffer();
		StringBuffer accumulateSb = new StringBuffer();
		addSb.append(" SELECT (CASE t.province WHEN '' THEN '未知' ELSE t.province END ) AS province,(CASE t.city WHEN '' THEN '未知' ELSE t.city END ) AS city,COUNT(1) as addNum ");
		addSb.append(" FROM record_first_visit_terminal t ");
		addSb.append("  WHERE 1=1 ");
		accumulateSb.append(" SELECT (CASE t.province WHEN '' THEN '未知' ELSE t.province END ) AS province,(CASE t.city WHEN '' THEN '未知' ELSE t.city END ) AS city,COUNT(1) as accumulateNum ");
		accumulateSb.append(" FROM record_first_visit_terminal t ");
		accumulateSb.append("  WHERE 1=1 ");
		activitySb.append(" SELECT (CASE t.province WHEN '' THEN '未知' ELSE t.province END ) AS province,(CASE t.city WHEN '' THEN '未知' ELSE t.city END ) AS city,COUNT(DISTINCT(t.uuid)) as activityNum ");
		activitySb.append(" FROM record_day_visit_terminal t ");
		activitySb.append("  WHERE 1=1 ");
		if(StringUtil.isNotNullMap(param,"province")){
			String province = StringUtil.null2Str(param.get("province")[0]);
			addSb.append("  AND t.province='").append(province).append("'");
			accumulateSb.append("  AND t.province='").append(province).append("'");
			activitySb.append("  AND t.province='").append(province).append("'");
		}
		if(StringUtil.isNotNullMap(param,"city")){
			String city = StringUtil.null2Str(param.get("city")[0]);
			addSb.append("  AND t.city='").append(city).append("'");
			accumulateSb.append("  AND t.city='").append(city).append("'");
			activitySb.append("  AND t.city='").append(city).append("'");
		}
		if(StringUtil.isNotNullMap(param,"startTime")){
			String startTime = StringUtil.null2Str(param.get("startTime")[0]);
			startTime = Util.getChangeDayDate(startTime);
			addSb.append("  AND DATE_FORMAT(t.firstVisitTime,'%Y-%m-%d')>='").append(startTime).append("'");
			activitySb.append("  AND DATE_FORMAT(t.visitTime,'%Y-%m-%d')>='").append(startTime).append("'");
		}
		if(StringUtil.isNotNullMap(param,"endTime")){
			String endTime = StringUtil.null2Str(param.get("endTime")[0]);
			endTime = Util.getChangeDayDate(endTime);
			addSb.append("  AND DATE_FORMAT(t.firstVisitTime,'%Y-%m-%d')<='").append(endTime).append("'");
			activitySb.append("  AND DATE_FORMAT(t.visitTime,'%Y-%m-%d')<='").append(endTime).append("'");
		}
		if(StringUtil.isNotNullMap(param,"dimension")){
			// 有分组
			String[] dimension = param.get("dimension");
			int group = 0; // 0:省，1：市，2：省&市
			if(dimension!=null&&dimension.length>0){
				group = dimension.length;
				addSb.append("  GROUP BY ");
				accumulateSb.append("  GROUP BY ");
				activitySb.append("  GROUP BY ");
				for(int i=0;i<dimension.length;i++){
					if(group!=2){
						if(dimension[i].equals("city")){
							group = 1;
						}
					}
					addSb.append(" t.").append(dimension[i]);
					accumulateSb.append(" t.").append(dimension[i]);
					activitySb.append(" t.").append(dimension[i]);
					if(i!=dimension.length-1){
						addSb.append(",");
						accumulateSb.append(",");
						activitySb.append(",");
					}
				}
				sb.append(" (").append(accumulateSb).append(") a ");
				sb.append(" LEFT JOIN ");
				sb.append(" (").append(addSb).append(") b ");
				if(group==0){
					sb.append(" ON a.province=b.province LEFT JOIN ");
				}else if(group==1){
					sb.append(" ON a.city=b.city LEFT JOIN ");
				}else if(group==2){
					sb.append(" ON a.province=b.province AND a.city=b.city LEFT JOIN ");
				}
				sb.append(" (").append(activitySb).append(") c ");
				if(group==0){
					sb.append(" ON a.province=c.province ");
				}else if(group==1){
					sb.append(" ON a.city=c.city ");
				}else if(group==2){
					sb.append(" ON a.province=c.province AND a.city=c.city ");
				}
			}
		}else{
			// 无分组
			sb.append(" (").append(accumulateSb).append(") a, ");
			sb.append(" (").append(addSb).append(") b, ");
			sb.append(" (").append(activitySb).append(") c ");
		}
		
	    List<UserVisit> list = UserVisit.dao.find(sb.toString());
	   
		return list;
	}

	@Override
	public List<UserVisit> exportUserDeviceListExcel(Map<String, String[]> param) {
		StringBuffer sql = new StringBuffer();
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT;
		sql.append(" SELECT COUNT(1) AS num,t.model,t.system,t.firstVersion, ");
		sql.append(" (CASE t.clientType WHEN 1 THEN 'aphone' ELSE 'iphone' END ) AS clientType ");
		sql.append(" FROM record_first_visit_terminal t ");
		sql.append(" WHERE 1=1 ");
		if(StringUtil.isNotNullMap(param,"model")){
			String model = StringUtil.null2Str(param.get("model")[0]);
			sql.append(" and t.model LIKE '%").append(model).append("%'");
		}
		if(StringUtil.isNotNullMap(param,"clientType")){
			int clientType = StringUtil.nullToInteger(param.get("clientType")[0]);
			sql.append(" and t.clientType=").append(clientType);
		}
		if(StringUtil.isNotNullMap(param,"system")){
			String system = StringUtil.null2Str(param.get("system")[0]);
			sql.append(" and t.system LIKE '%").append(system).append("%'");
		}
		if(StringUtil.isNotNullMap(param,"dimension")){
			String[] dimension=param.get("dimension");
			 sql.append(" GROUP BY ");
			 if(dimension!=null&&dimension.length>0){
				 for(int i = 0;i<dimension.length;i++){
					 sql.append("t."+dimension[i]);
					 if(i!=dimension.length-1){
						 sql.append(",");
					 }
				 }
			 }
		}
		
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
		
		sql.append(" ORDER BY ").append(property).append(" ").append(direction);
		
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);

        List<UserVisit> list = UserVisit.dao.find(sql.toString());
        if(list!=null&&list.size()>0){
          long totalDevice = Db.use("ana_main").queryLong(" SELECT COUNT(1) AS num FROM record_first_visit_terminal t WHERE 1=1 ");
          for(int i=0;i<list.size();i++){
        	  if(totalDevice>0){	
        		  list.get(i).set("firstVersion", list.get(i).getLong("num")*100/totalDevice+"%");
  			}else{
  				list.get(i).set("firstVersion", "0");
  			}
        	
  			}
          
        }
		return list;
	}

	@Override
	public List<UserVisit> exportUserVersionListExcel(
			Map<String, String[]> param) {
		List<UserVisit> list = new ArrayList<UserVisit>();
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT t.currentVersion as version,COUNT(1) as num FROM record_first_visit_terminal t  WHERE 1=1 ");
		if(StringUtil.isNotNullMap(param,"version")){
			String version = StringUtil.null2Str(param.get("version")[0]);
			sb.append(" AND t.currentVersion LIKE '").append(version).append("%'");
		}
		sb.append(" GROUP BY t.currentVersion");
		list=UserVisit.dao.find(sb.toString());
		
		return list;
	}

	@Override
	public List<UserStartUp> exportUserStartUpListExcel(
			Map<String, String[]> param) {
		StringBuffer sb = new StringBuffer();
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT;
		
		sb.append(" SELECT m.*,IF(a.aphoneStartNum IS NULL,0,a.aphoneStartNum) AS aphoneStartNum ,IF(b.iphoneStartNum IS NULL,0,b.iphoneStartNum) AS iphoneStartNum FROM ");
		sb.append(" (SELECT SUM(t.startNum) AS startNum,DATE_FORMAT(t.visitDay,'%Y-%m-%d') AS visitDay FROM record_start_up_statistic t WHERE 1=1 ");
		sb.append(" GROUP BY DATE_FORMAT(t.visitDay,'%Y-%m-%d')) m  LEFT JOIN ");
		
		sb.append(" (SELECT SUM(t.startNum) AS aphoneStartNum,DATE_FORMAT(t.visitDay,'%Y-%m-%d') AS visitDay FROM record_start_up_statistic t WHERE 1=1 ");
		sb.append(" AND t.clientType=1 GROUP BY DATE_FORMAT(t.visitDay,'%Y-%m-%d')) a ");
		
		sb.append(" ON m.visitDay=a.visitDay LEFT JOIN  ");
		sb.append(" (SELECT SUM(t.startNum) AS iphoneStartNum,DATE_FORMAT(t.visitDay,'%Y-%m-%d') AS visitDay FROM record_start_up_statistic t WHERE 1=1 ");
		sb.append(" AND t.clientType=2 GROUP BY DATE_FORMAT(t.visitDay,'%Y-%m-%d')) b ");
		sb.append(" ON m.visitDay=b.visitDay ");
		sb.append(" WHERE 1=1 ");
		if(StringUtil.isNotNullMap(param,"startTime")){
			String startTime = StringUtil.null2Str(param.get("startTime")[0]);
			startTime = Util.getChangeDayDate(startTime);
			sb.append(" and DATE_FORMAT(m.visitDay,'%Y-%m-%d')>='").append(startTime).append("'");
				}
		
		if(StringUtil.isNotNullMap(param,"endTime")){
			String endTime = StringUtil.null2Str(param.get("endTime")[0]);
			endTime = Util.getChangeDayDate(endTime);
			sb.append(" and DATE_FORMAT(m.visitDay,'%Y-%m-%d')<='").append(endTime).append("'");
			}

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
		
		sb.append(" limit ");
		sb.append(pageNumber);
		sb.append(",");
		sb.append(pageSize);
		List<UserStartUp> list =UserStartUp.dao.find(sb.toString());
		
		return list;
	}

	@Override
	public List<UserChannel> exportUserChannelListExcel(
			Map<String, String[]> param) {
		StringBuffer addSql = new StringBuffer(); // 新增用户
		StringBuffer activitySql = new StringBuffer(); // 活跃用户
		String startTime = "";
		if(StringUtil.isNotNullMap(param,"startTime")){
			startTime = StringUtil.null2Str(param.get("startTime")[0]);
			startTime = Util.getChangeDayDate(startTime);
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
	
		addSql.append(" WHERE 1=1 ");
		addSql.append(" AND DATE_FORMAT(t.logTime,'%Y-%m-%d')>='").append(startTime).append("'");
		if(StringUtil.isNotNullMap(param,"endTime")){
			String endTime = StringUtil.null2Str(param.get("endTime")[0]);
			endTime =  Util.getChangeDayDate(endTime);
			addSql.append(" AND DATE_FORMAT(t.logTime,'%Y-%m-%d')<='").append(endTime).append("'");
		}
		addSql.append(" GROUP BY t.channel ");
		
		activitySql.append(" SELECT t.channel,COUNT(1) AS activityNum FROM record_day_visit_terminal t  ");
	
		activitySql.append(" WHERE 1=1 ");
		activitySql.append(" AND DATE_FORMAT(t.visitTime,'%Y-%m-%d')>='").append(startTime).append("'");
		if(StringUtil.isNotNullMap(param,"endTime")){
			String endTime = StringUtil.null2Str(param.get("endTime")[0]);
			endTime = Util.getChangeDayDate(endTime);
			activitySql.append(" AND DATE_FORMAT(t.visitTime,'%Y-%m-%d')<='").append(endTime).append("'");
		}
		activitySql.append(" GROUP BY t.channel ");
	    List<Record> addList = Db.use("ana_main").find(addSql.toString());
	    List<Record> activityList = Db.use("ana_main").find(activitySql.toString());
	    List<Record> list = new ArrayList<Record>();
	    List<UserChannel> ulist = new ArrayList<UserChannel>();
	    if(addList!=null&&addList.size()>0){
	    	for(Record r : addList){
	    		r.set("activityNum", 0L);
	    		list.add(r);
	    	}
	    }
	    if(activityList!=null&&activityList.size()>0){
	    	for(Record r : activityList){
	    		if(!isExistInChannel(r,list)){
	    			// 
		    		r.set("addNum", 0L);	
		    		list.add(r);
	    		}
	    	}
	    }
	    UserChannel uc=null;
	    for(Record r:list){
	    	uc=new UserChannel();
	    	uc.set("channel", r.getStr("channel"));
	    	uc.set("model", r.getLong("addNum"));
	    	uc.set("resolution", r.getLong("activityNum"));
	    	ulist.add(uc);
	    }
		return ulist;
	}

}

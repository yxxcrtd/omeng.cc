package com.shanjin.manager.dao.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.manager.Bean.MerchantsInfo;
import com.shanjin.manager.Bean.OrderInfo;
import com.shanjin.manager.Bean.UserInfo;
import com.shanjin.manager.constant.Constant.PAGE;
import com.shanjin.manager.constant.Constant.SORT;
import com.shanjin.manager.dao.StatisticDao;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

public class StatisticDaoImpl implements StatisticDao {

	@Override
	public List<Record> getLoginMerchantList(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		String[] dimension=paramMap.get("dimension");
		sql.append("select count(1) as terminalNum,m.province,m.city,(select ma.app_name from merchant_app_info ma where ma.app_type=m.app_type) as app_type,m.join_time from merchant_info m where m.is_del=0 ");
		totalSql.append("select count(1) as total from (SELECT m.id,m.join_time from merchant_info m where m.is_del=0 ");
		sql.append(Util.getdateFilter("m.join_time",paramMap.get("start_time"), paramMap.get("off_time")));
		totalSql.append(Util.getdateFilter("m.join_time",paramMap.get("start_time"), paramMap.get("off_time")));
		if(StringUtil.nullToBoolean(dimension)){
        	 sql.append("group by ");
        	 totalSql.append("group by ");
        	 for(String s:dimension){
        		 sql.append("m."+s);
        		 totalSql.append("m."+s);
        		 sql.append(",");
        		 totalSql.append(",");
        	 }
        	 sql.deleteCharAt(sql.length()-1);
        	 totalSql.deleteCharAt(totalSql.length()-1);
         }
         sql.append(Util.getStatisticFilter(paramMap,dimension));
         totalSql.append(Util.getStatisticFilter(paramMap,dimension));
         totalSql.append(") s");
         
		long total = Db.find(totalSql.toString()).get(0).getLong("total");
		
		String property = "terminalNum";
		String direction = SORT.DESC;
		if(StringUtil.isNotNullMap(paramMap,"sort")){
			Map<String,String> sortMap = Util.sortMap(StringUtil.null2Str(paramMap.get("sort")[0]));
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
		List<Record> terminalList = Db.find(sql
				.toString());
		if (terminalList.size() > 0) {
			terminalList.get(0).set("total", total);
		}
		return terminalList;
	}
	@Override
	public List<Record> getMerchantPia(Map<String, String[]> param) {
		StringBuffer sql = new StringBuffer();
		StringBuffer sqlOther = new StringBuffer();
		String dimension=param.get("dimension")[0];
		if(!StringUtil.strToBoolean(dimension)||dimension.equals("app_type")){
			sql.append("select count(1) as data,(select ma.app_name from merchant_app_info ma where ma.app_type=m.app_type) as name from merchant_info m group by app_type ");
		}else if(dimension.equals("province")){
			sql.append("select count(1) as data,m.province as name from merchant_info m group by m.province having m.province is not null and m.province!='' ");
		}else{
			sql.append("select count(1) as data,m.city as name from merchant_info m group by m.city having m.city is not null and m.city!=''");
		}
		sql.append(" order by data desc limit 0,10");
		sqlOther.append("select count(1) as data from merchant_info m where m.is_del=0");
		
		List<Record> topList=Db.find(sql.toString());
		List<Record> othList=Db.find(sqlOther.toString());
		if(topList.size()>0){
			long oth=othList.get(0).getLong("data");
			for(Record rec:topList ){
				oth=oth-rec.getLong("data");
			}
			Record rec=new Record();
			rec.set("name", "其他");
			rec.set("data", oth);
	    topList.add(rec);
		}
		return topList;
	}
	
	@Override
	public List<Record> getMerchantByTime(Map<String, String[]> param) {
		StringBuffer sql = new StringBuffer();
		String dimension=param.get("dimension")[0];
		String startTime=param.get("startTime")[0];
		String endTime=param.get("endTime")[0];
		if(!StringUtil.strToBoolean(dimension)||dimension.equals("day")){
			sql.append("select count(1) as data,m.join_time as joinTime,DATE_FORMAT(m.join_time,'%Y-%m-%d') as join_time from merchant_info m group by DATE_FORMAT(m.join_time,'%Y-%m-%d') having TO_DAYS(m.join_time) BETWEEN TO_DAYS(?)  and TO_DAYS(?)");
		}else if(dimension.equals("month")){
			sql.append("select count(1) as data,m.join_time as joinTime,DATE_FORMAT(m.join_time,'%Y-%m') as join_time from merchant_info m group by DATE_FORMAT(m.join_time,'%Y-%m') having TO_DAYS(m.join_time) BETWEEN TO_DAYS(?)  and TO_DAYS(?)");
		}else{
			sql.append("select count(1) as data,m.join_time as joinTime,DATE_FORMAT(m.join_time,'%Y') as join_time from merchant_info m group by DATE_FORMAT(m.join_time,'%Y') having TO_DAYS(m.join_time) BETWEEN TO_DAYS(?)  and TO_DAYS(?)");
		}
		if(!StringUtil.strToBoolean(endTime)){
			Date date=new Date();
			DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
			endTime=format.format(date); 
		}
		if(!StringUtil.strToBoolean(startTime)){
			startTime=StringUtil.lastMonth(); 
		}
		List<Record> topList=Db.find(sql.toString(),startTime,endTime);
		return topList;
	}
	@Override
	public List<Record> getLoginUserList(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		String[] dimension=paramMap.get("dimension");
		sql.append("select count(1) as terminalNum,u.province,u.city,u.join_time from user_info u where u.is_del=0 ");
		totalSql.append("select count(1) as total from (SELECT u.id,u.join_time from user_info u where u.is_del=0 ");
		sql.append(Util.getdateFilter("u.join_time",paramMap.get("start_time"), paramMap.get("off_time")));
		totalSql.append(Util.getdateFilter("u.join_time",paramMap.get("start_time"), paramMap.get("off_time")));
		if(StringUtil.nullToBoolean(dimension)){
        	 sql.append("group by ");
        	 totalSql.append("group by ");
        	 for(String s:dimension){
        		 sql.append("u."+s);
        		 totalSql.append("u."+s);
        		 sql.append(",");
        		 totalSql.append(",");
        	 }
        	 sql.deleteCharAt(sql.length()-1);
        	 totalSql.deleteCharAt(totalSql.length()-1);
         }
         sql.append(Util.getStatisticUserFilter(paramMap,dimension));
         totalSql.append(Util.getStatisticUserFilter(paramMap,dimension));
         totalSql.append(") s");
         
		long total = Db.find(totalSql.toString()).get(0).getLong("total");
		
		String property = "terminalNum";
		String direction = SORT.DESC;
		if(StringUtil.isNotNullMap(paramMap,"sort")){
			Map<String,String> sortMap = Util.sortMap(StringUtil.null2Str(paramMap.get("sort")[0]));
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
		List<Record> terminalList = Db.find(sql
				.toString());
		if (terminalList.size() > 0) {
			terminalList.get(0).set("total", total);
		}
		return terminalList;
	}
	@Override
	public List<Record> getUserPia(Map<String, String[]> param) {
		StringBuffer sql = new StringBuffer();
		StringBuffer sqlOther = new StringBuffer();
		String dimension=param.get("dimension")[0];
		if(dimension.equals("province")){
			sql.append("select count(1) as data,u.province as name from user_info u group by u.province having u.province is not null and u.province!='' ");
		}else{
			sql.append("select count(1) as data,u.city as name from user_info u group by u.city having u.city is not null and u.city!='' ");
		}
		sql.append(" order by data desc limit 0,10");
		sqlOther.append("select count(1) as data from user_info u where u.is_del=0");
		
		List<Record> topList=Db.find(sql.toString());
		List<Record> othList=Db.find(sqlOther.toString());
		if(topList.size()>0){
			long oth=othList.get(0).getLong("data");
			for(Record rec:topList ){
				oth=oth-rec.getLong("data");
			}
			Record rec=new Record();
			rec.set("name", "其他");
			rec.set("data", oth);
	        topList.add(rec);
		}
		return topList;
	}
	@Override
	public List<Record> getUserByTime(Map<String, String[]> param) {
		StringBuffer sql = new StringBuffer();
		String dimension=param.get("dimension")[0];
		String startTime=param.get("startTime")[0];
		String endTime=param.get("endTime")[0];
		if(!StringUtil.strToBoolean(dimension)||dimension.equals("day")){
			sql.append("select count(1) as data,m.join_time as joinTime,DATE_FORMAT(m.join_time,'%Y-%m-%d') as join_time from user_info m group by DATE_FORMAT(m.join_time,'%Y-%m-%d') having TO_DAYS(m.join_time) BETWEEN TO_DAYS(?) and TO_DAYS(?)");
		}else if(dimension.equals("month")){
			sql.append("select count(1) as data,m.join_time as joinTime,DATE_FORMAT(m.join_time,'%Y-%m') as join_time from user_info m group by DATE_FORMAT(m.join_time,'%Y-%m') having TO_DAYS(m.join_time) BETWEEN TO_DAYS(?) and TO_DAYS(?)");
		}else{
			sql.append("select count(1) as data,m.join_time as joinTime,DATE_FORMAT(m.join_time,'%Y') as join_time from user_info m group by DATE_FORMAT(m.join_time,'%Y') having TO_DAYS(m.join_time) BETWEEN TO_DAYS(?) and TO_DAYS(?)");
		}
		
		if(!StringUtil.strToBoolean(endTime)){
			Date date=new Date();
			DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
			endTime=format.format(date); 
		}
		if(!StringUtil.strToBoolean(startTime)){
			startTime=StringUtil.lastMonth(); 
		}
		List<Record> topList=Db.find(sql.toString(),startTime,endTime);
		return topList;
	}
	@Override
	public List<Record> getUserOrderList(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		String[] dimension=paramMap.get("dimension");
		sql.append("select count(1) as terminalNum,m.province,m.city,m.order_status,(select ma.app_name from merchant_app_info ma where ma.app_type=m.app_type) as app_type,m.join_time from order_info m where m.is_del=0 ");
		totalSql.append("select count(1) as total from (SELECT m.id,m.join_time from order_info m where m.is_del=0 ");
		sql.append(Util.getdateFilter("m.join_time",paramMap.get("start_time"), paramMap.get("off_time")));
		totalSql.append(Util.getdateFilter("m.join_time",paramMap.get("start_time"), paramMap.get("off_time")));
		if(StringUtil.nullToBoolean(dimension)){
        	 sql.append("group by ");
        	 totalSql.append("group by ");
        	 for(String s:dimension){
        		 sql.append("m."+s);
        		 totalSql.append("m."+s);
        		 sql.append(",");
        		 totalSql.append(",");
        	 }
        	 sql.deleteCharAt(sql.length()-1);
        	 totalSql.deleteCharAt(totalSql.length()-1);
         }
         sql.append(Util.getStatisticFilter(paramMap,dimension));
         totalSql.append(Util.getStatisticFilter(paramMap,dimension));
         totalSql.append(") s");
         
		long total = Db.find(totalSql.toString()).get(0).getLong("total");
		
		String property = "terminalNum";
		String direction = SORT.DESC;
		if(StringUtil.isNotNullMap(paramMap,"sort")){
			Map<String,String> sortMap = Util.sortMap(StringUtil.null2Str(paramMap.get("sort")[0]));
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
		List<Record> terminalList = Db.find(sql
				.toString());
		if (terminalList.size() > 0) {
			terminalList.get(0).set("total", total);
		}
		return terminalList;
	}
	@Override
	public List<Record> getUserOrderPia(Map<String, String[]> param) {
		StringBuffer sql = new StringBuffer();
		StringBuffer sqlOther = new StringBuffer();
		String dimension=param.get("dimension")[0];
		if(!StringUtil.strToBoolean(dimension)||dimension.equals("app_type")){
			sql.append("select count(1) as data,(select ma.app_name from merchant_app_info ma where ma.app_type=m.app_type) as name from order_info m group by app_type ");
		}else if(dimension.equals("province")){
			sql.append("select count(1) as data,m.province as name from order_info m group by m.province having m.province is not null and m.province!='' ");
		}else if(dimension.equals("city")){
			sql.append("select count(1) as data,m.city as name from order_info m group by m.city having m.city is not null and m.city!='' ");
		}else if(dimension.equals("order_status")){
			sql.append("select count(1) as data,(select d.dict_value from dictionary d where d.dict_type='userOrderStatus' and d.dict_key=m.order_status) as name from order_info m group by m.order_status ");
		}
		sql.append(" order by data desc limit 0,10");
		sqlOther.append("select count(1) as data from order_info m where m.is_del=0");
		
		List<Record> topList=Db.find(sql.toString());
		List<Record> othList=Db.find(sqlOther.toString());
		if(topList.size()>0){
			long oth=othList.get(0).getLong("data");
			for(Record rec:topList ){
				oth=oth-rec.getLong("data");
			}
			Record rec=new Record();
			rec.set("name", "其他");
			rec.set("data", oth);
	    topList.add(rec);
		}
		return topList;
	}
	@Override
	public List<Record> getUserOrderByTime(Map<String, String[]> param) {
		StringBuffer sql = new StringBuffer();
		String dimension=param.get("dimension")[0];
		String startTime=param.get("startTime")[0];
		String endTime=param.get("endTime")[0];
		if(!StringUtil.strToBoolean(dimension)||dimension.equals("day")){
			sql.append("select count(1) as data,m.join_time as joinTime,DATE_FORMAT(m.join_time,'%Y-%m-%d') as join_time from order_info m group by DATE_FORMAT(m.join_time,'%Y-%m-%d') having TO_DAYS(m.join_time) BETWEEN TO_DAYS(?)  and TO_DAYS(?)");
		}else if(dimension.equals("month")){
			sql.append("select count(1) as data,m.join_time as joinTime,DATE_FORMAT(m.join_time,'%Y-%m') as join_time from order_info m group by DATE_FORMAT(m.join_time,'%Y-%m') having TO_DAYS(m.join_time) BETWEEN TO_DAYS(?)  and TO_DAYS(?)");
		}else{
			sql.append("select count(1) as data,m.join_time as joinTime,DATE_FORMAT(m.join_time,'%Y') as join_time from order_info m group by DATE_FORMAT(m.join_time,'%Y') having TO_DAYS(m.join_time) BETWEEN TO_DAYS(?)  and TO_DAYS(?)");
		}

		if(!StringUtil.strToBoolean(endTime)){
			Date date=new Date();
			DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
			endTime=format.format(date); 
		}
		if(!StringUtil.strToBoolean(startTime)){
			startTime=StringUtil.lastMonth(); 
		}
		List<Record> topList=Db.find(sql.toString(),startTime,endTime);
		return topList;
	}
	@Override
	public List<OrderInfo> exportOrderTrendExcel(Map<String, String[]> param) {
		StringBuffer sql = new StringBuffer();
		String dimension=param.get("dimension")[0];
		String startTime=param.get("startTime")[0];
		String endTime=param.get("endTime")[0];
		if(!StringUtil.strToBoolean(dimension)||dimension.equals("day")){
			sql.append("select count(1) as data,m.join_time as joinTime,DATE_FORMAT(m.join_time,'%Y-%m-%d') as join_time from order_info m group by DATE_FORMAT(m.join_time,'%Y-%m-%d') having TO_DAYS(m.join_time) BETWEEN TO_DAYS(?)  and TO_DAYS(?)");
		}else if(dimension.equals("month")){
			sql.append("select count(1) as data,m.join_time as joinTime,DATE_FORMAT(m.join_time,'%Y-%m') as join_time from order_info m group by DATE_FORMAT(m.join_time,'%Y-%m') having TO_DAYS(m.join_time) BETWEEN TO_DAYS(?)  and TO_DAYS(?)");
		}else{
			sql.append("select count(1) as data,m.join_time as joinTime,DATE_FORMAT(m.join_time,'%Y') as join_time from order_info m group by DATE_FORMAT(m.join_time,'%Y') having TO_DAYS(m.join_time) BETWEEN TO_DAYS(?)  and TO_DAYS(?)");
		}

		if(!StringUtil.strToBoolean(endTime)){
			Date date=new Date();
			DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
			endTime=format.format(date); 
		}else{
			endTime=Util.getChangeDate(endTime);
		}
		if(!StringUtil.strToBoolean(startTime)){
			startTime=StringUtil.lastMonth(); 
		}else{
			startTime=Util.getChangeDate(startTime);
		}
		List<OrderInfo> orderList=OrderInfo.dao.find(sql.toString(),startTime,endTime);
		return orderList;
	}
	@Override
	public List<UserInfo> exportUserTrendExcel(Map<String, String[]> param) {
		StringBuffer sql = new StringBuffer();
		String dimension=param.get("dimension")[0];
		String startTime=param.get("startTime")[0];
		String endTime=param.get("endTime")[0];
		if(!StringUtil.strToBoolean(dimension)||dimension.equals("day")){
			sql.append("select count(1) as data,m.join_time as joinTime,DATE_FORMAT(m.join_time,'%Y-%m-%d') as join_time from user_info m group by DATE_FORMAT(m.join_time,'%Y-%m-%d') having TO_DAYS(m.join_time) BETWEEN TO_DAYS(?) and TO_DAYS(?)");
		}else if(dimension.equals("month")){
			sql.append("select count(1) as data,m.join_time as joinTime,DATE_FORMAT(m.join_time,'%Y-%m') as join_time from user_info m group by DATE_FORMAT(m.join_time,'%Y-%m') having TO_DAYS(m.join_time) BETWEEN TO_DAYS(?) and TO_DAYS(?)");
		}else{
			sql.append("select count(1) as data,m.join_time as joinTime,DATE_FORMAT(m.join_time,'%Y') as join_time from user_info m group by DATE_FORMAT(m.join_time,'%Y') having TO_DAYS(m.join_time) BETWEEN TO_DAYS(?) and TO_DAYS(?)");
		}
		
		if(!StringUtil.strToBoolean(endTime)){
			Date date=new Date();
			DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
			endTime=format.format(date); 
		}else{
			endTime=Util.getChangeDate(endTime);
		}
		if(!StringUtil.strToBoolean(startTime)){
			startTime=StringUtil.lastMonth(); 
		}else{
			startTime=Util.getChangeDate(startTime);
		}
		List<UserInfo> topList=UserInfo.dao.find(sql.toString(),startTime,endTime);
		return topList;
	}
	@Override
	public List<MerchantsInfo> exportMerchantTrendExcel(
			Map<String, String[]> param) {
		StringBuffer sql = new StringBuffer();
		String dimension=param.get("dimension")[0];
		String startTime=param.get("startTime")[0];
		String endTime=param.get("endTime")[0];
		if(!StringUtil.strToBoolean(dimension)||dimension.equals("day")){
			sql.append("select count(1) as data,m.join_time as joinTime,DATE_FORMAT(m.join_time,'%Y-%m-%d') as join_time from merchant_info m group by DATE_FORMAT(m.join_time,'%Y-%m-%d') having TO_DAYS(m.join_time) BETWEEN TO_DAYS(?)  and TO_DAYS(?)");
		}else if(dimension.equals("month")){
			sql.append("select count(1) as data,m.join_time as joinTime,DATE_FORMAT(m.join_time,'%Y-%m') as join_time from merchant_info m group by DATE_FORMAT(m.join_time,'%Y-%m') having TO_DAYS(m.join_time) BETWEEN TO_DAYS(?)  and TO_DAYS(?)");
		}else{
			sql.append("select count(1) as data,m.join_time as joinTime,DATE_FORMAT(m.join_time,'%Y') as join_time from merchant_info m group by DATE_FORMAT(m.join_time,'%Y') having TO_DAYS(m.join_time) BETWEEN TO_DAYS(?)  and TO_DAYS(?)");
		}
		if(!StringUtil.strToBoolean(endTime)){
			Date date=new Date();
			DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
			endTime=format.format(date); 
		}else{
			endTime=Util.getChangeDate(endTime);
		}
		if(!StringUtil.strToBoolean(startTime)){
			startTime=StringUtil.lastMonth(); 
		}else{
			startTime=Util.getChangeDate(startTime);
		}
		List<MerchantsInfo> topList=MerchantsInfo.dao.find(sql.toString(),startTime,endTime);
		return topList;
	}
	@Override
	public List<MerchantsInfo> exportLoginMerchantListExcel(
			Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT;
		String dimension=paramMap.get("dimension")[0];
		sql.append("select count(1) as terminalNum,m.province,m.city,(select ma.app_name from merchant_app_info ma where ma.app_type=m.app_type) as app_type,m.join_time from merchant_info m where m.is_del=0 ");
		sql.append(Util.getExportdateFilter("m.join_time",paramMap.get("start_time"), paramMap.get("off_time")));
		String[] dim = null;
		if(!StringUtil.isNullStr(dimension)){
			dim=dimension.split(",");
		if(StringUtil.nullToBoolean(dim)){
        	 sql.append("group by ");
        	 for(String s:dim){
        		 sql.append("m."+s);
        		 sql.append(",");
        		 }
        	 sql.deleteCharAt(sql.length()-1);
        	   }
		}
	
         sql.append(Util.getStatisticFilter(paramMap,dim));
        	
		String property = "terminalNum";
		String direction = SORT.DESC;
		if(StringUtil.isNotNullMap(paramMap,"sort")){
			Map<String,String> sortMap = Util.sortMap(StringUtil.null2Str(paramMap.get("sort")[0]));
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
		List<MerchantsInfo> merchantsInfoList = MerchantsInfo.dao.find(sql
				.toString());
		return merchantsInfoList;
	}
	@Override
	public List<UserInfo> exportLoginUserListExcel(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT;
		String dimension=paramMap.get("dimension")[0];
		sql.append("select count(1) as terminalNum,u.province,u.city,u.join_time from user_info u where u.is_del=0 ");
		sql.append(Util.getExportdateFilter("u.join_time",paramMap.get("start_time"), paramMap.get("off_time")));
		String[] dim = null;
		if(!StringUtil.isNullStr(dimension)){
			dim=dimension.split(",");
		if(StringUtil.nullToBoolean(dim)){
        	 sql.append("group by ");
        	 for(String s:dim){
        		 sql.append("u."+s);
        		 sql.append(",");
        		 }
        	 sql.deleteCharAt(sql.length()-1);
        	 }
		}
         sql.append(Util.getStatisticUserFilter(paramMap,dim));
       	
		String property = "terminalNum";
		String direction = SORT.DESC;
		if(StringUtil.isNotNullMap(paramMap,"sort")){
			Map<String,String> sortMap = Util.sortMap(StringUtil.null2Str(paramMap.get("sort")[0]));
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
		List<UserInfo> userInfoList = UserInfo.dao.find(sql
				.toString());
		
		return userInfoList;
	}
	@Override
	public List<OrderInfo> exportUserOrderListExcel(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT;
		String dimension=paramMap.get("dimension")[0];
		sql.append("select count(1) as terminalNum,m.province,m.city,m.order_status,(select d.dict_value from dictionary d where d.dict_type='userOrderStatus' and d.dict_key=m.order_status) as orderStatus,(select ma.app_name from merchant_app_info ma where ma.app_type=m.app_type) as app_type,m.join_time from order_info m where m.is_del=0 ");
		sql.append(Util.getExportdateFilter("m.join_time",paramMap.get("start_time"), paramMap.get("off_time")));
		String[] dim = null;
		if(!StringUtil.isNullStr(dimension)){
			dim=dimension.split(",");
		if(StringUtil.nullToBoolean(dim)){
        	 sql.append("group by ");
        	 for(String s:dim){
        		 sql.append("m."+s);
        		 sql.append(",");
        		 }
        	 sql.deleteCharAt(sql.length()-1);
        	 }
		}
         sql.append(Util.getStatisticFilter(paramMap,dim));
        
		String property = "terminalNum";
		String direction = SORT.DESC;
		if(StringUtil.isNotNullMap(paramMap,"sort")){
			Map<String,String> sortMap = Util.sortMap(StringUtil.null2Str(paramMap.get("sort")[0]));
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
		List<OrderInfo> orderInfoList = OrderInfo.dao.find(sql
				.toString());
		
		return orderInfoList;
	}
}

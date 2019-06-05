package com.shanjin.manager.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.shanjin.manager.Bean.FensiAddRanking;
import com.shanjin.manager.Bean.FensiAddTotal;
import com.shanjin.manager.Bean.MerPhotoStatic;
import com.shanjin.manager.Bean.OrderRewardAccount;
import com.shanjin.manager.constant.Constant.PAGE;
import com.shanjin.manager.constant.Constant.SORT;
import com.shanjin.manager.dao.ActivityDao;
import com.shanjin.manager.utils.AESUtil;
import com.shanjin.manager.utils.BusinessUtil;
import com.shanjin.manager.utils.DateUtil;
import com.shanjin.manager.utils.MqUtil;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;
public class ActivityDaoImpl implements ActivityDao {

	@Override
	public List<FensiAddTotal> getDayAddFensi(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		
		sql.append("select rdm.* from report_day_merchant_fans rdm where ");
		totalSql.append("select count(1) as total from report_day_merchant_fans rdm where ");
		strFilter.put("rdm.province", paramMap.get("province"));
		strFilter.put("rdm.city", paramMap.get("city"));
	
		
		sql.append(Util.getFilter(strFilter, intFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter));
		
		if(!paramMap.get("head_date")[0].equals("")){
			String head_date=DateUtil.getDateparse(paramMap.get("head_date")[0].toString());
			sql.append(" and rdm.head_date='").append(head_date).append("' ");
			totalSql.append(" and rdm.head_date='").append(head_date).append("' ");
		}
		
		long total = FensiAddTotal.dao.find(totalSql.toString()).get(0).getLong("total");
		
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<FensiAddTotal> fensiAddTotal = FensiAddTotal.dao.find(sql
				.toString());
		if (fensiAddTotal.size() > 0) {
			fensiAddTotal.get(0).setTotal(total);
		}
		return fensiAddTotal;
		
	}

	@Override
	public List<FensiAddTotal> exportDayAddFensi(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT;
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		
		sql.append("select rdm.* from report_day_merchant_fans rdm where ");
		
		strFilter.put("rdm.province", paramMap.get("province"));
		strFilter.put("rdm.city", paramMap.get("city"));
		
		sql.append(Util.getFilter(strFilter, intFilter));
		
		if(paramMap.get("head_date")[0]!=null&&!paramMap.get("head_date")[0].equals("")&&!paramMap.get("head_date")[0].equals("null")){
			String head_date=Util.getChangeFensiDate(paramMap.get("head_date")[0]);
			sql.append(" and rdm.head_date='").append(head_date).append("' ");
		}
		
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<FensiAddTotal> fensiAddTotal = FensiAddTotal.dao.find(sql
				.toString());
		
		return fensiAddTotal;
		
	}

	@Override
	public List<FensiAddRanking> getDayFensiRanking(Map<String, String[]> paramMap) {

		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		
		sql.append("select rft.* from report_fans_top rft where ");
		totalSql.append("select count(1) as total from report_fans_top rft where ");
		strFilter.put("rft.province", paramMap.get("province"));
		strFilter.put("rft.city", paramMap.get("city"));

		sql.append(Util.getFilter(strFilter, intFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter));
		
		long total = FensiAddRanking.dao.find(totalSql.toString()).get(0).getLong("total");
		
		if(!paramMap.get("head_date")[0].equals("")){
			String head_date=DateUtil.getDateparse(paramMap.get("head_date")[0].toString()); 
			sql.append(" and rft.head_date='").append(head_date).append("' ");
			totalSql.append(" and rft.head_date='").append(head_date).append("' ");
		}
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<FensiAddRanking> fensiAddRanking = FensiAddRanking.dao.find(sql
				.toString());
		if (fensiAddRanking.size() > 0) {
			fensiAddRanking.get(0).setTotal(total);
		}
		return fensiAddRanking;
		
	
	}

	@Override
	public List<FensiAddRanking> exportFensiAddRanking(Map<String, String[]> paramMap) {

		StringBuffer sql = new StringBuffer();
		
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT;
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		
		sql.append("select rft.* from report_fans_top rft where ");
		
		strFilter.put("rft.province", paramMap.get("province"));
		strFilter.put("rft.city", paramMap.get("city"));
		
		
		sql.append(Util.getFilter(strFilter, intFilter));
		if(paramMap.get("head_date")[0]!=null&&!paramMap.get("head_date")[0].equals("")&&!paramMap.get("head_date")[0].equals("null")){
			String head_date=Util.getChangeFensiDate(paramMap.get("head_date")[0]);
			sql.append(" and rft.head_date='").append(head_date).append("' ");
		}
		
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<FensiAddRanking> fensiAddRanking = FensiAddRanking.dao.find(sql
				.toString());
		
		return fensiAddRanking;
	}

	@Override
	public List<MerPhotoStatic> getDayMerPhoto(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		
		sql.append("select rdm.* from merchat_photo_statistical rdm where ");
		totalSql.append("select count(1) as total from merchat_photo_statistical rdm where ");
		strFilter.put("rdm.province", paramMap.get("province"));
		strFilter.put("rdm.city", paramMap.get("city"));
		strFilter.put("rdm.area", paramMap.get("area"));
		
		sql.append(Util.getFilter(strFilter, intFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter));
		
		if(!paramMap.get("head_date")[0].equals("")){
			String head_date=DateUtil.getDateparse(paramMap.get("head_date")[0].toString());
			sql.append(" and rdm.head_date='").append(head_date).append("' ");
			totalSql.append(" and rdm.head_date='").append(head_date).append("' ");
		}
		
		long total = MerPhotoStatic.dao.find(totalSql.toString()).get(0).getLong("total");
		
		String property = "rdm.head_date";
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
		List<MerPhotoStatic> merPhotoStatic = MerPhotoStatic.dao.find(sql
				.toString());
		if (merPhotoStatic.size() > 0) {
			merPhotoStatic.get(0).setTotal(total);
		}
		return merPhotoStatic;
	}

	@Override
	public List<MerPhotoStatic> exportMerPhotoExcel(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT;
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		
		sql.append("select rdm.* from merchat_photo_statistical rdm where ");
		
		strFilter.put("rdm.province", paramMap.get("province"));
		strFilter.put("rdm.city", paramMap.get("city"));
		strFilter.put("rdm.area", paramMap.get("area"));
		
		sql.append(Util.getFilter(strFilter, intFilter));
		
		if(paramMap.get("head_date")[0]!=null&&!paramMap.get("head_date")[0].equals("")&&!paramMap.get("head_date")[0].equals("null")){
			String head_date=Util.getChangeFensiDate(paramMap.get("head_date")[0]);
			sql.append(" and rdm.head_date='").append(head_date).append("' ");
		}
		
		 String property = "rdm.head_date";
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
		List<MerPhotoStatic> merPhotoStatic = MerPhotoStatic.dao.find(sql
				.toString());
		
		return merPhotoStatic;
		
	}

	@Override
	public List<Record> getDayMerPhotoDetail(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		if(paramMap.get("phone")[0].equals("")){
			sql.append("select m.*,(select name from catalog where alias=m.app_type and level=0) as app_name,(select me.phone from merchant_employees me where me.merchant_id=m.id and me.employees_type=1 limit 1) as phone from merchant_info m where m.id in ");
			totalSql.append("select count(1) as total from merchant_info m where m.id in ");
			
		}else{
			sql.append("select m.*,(select name from catalog where alias=m.app_type and level=0) as app_name,me.phone from merchant_info m inner join merchant_employees me on me.merchant_id=m.id and me.employees_type=1 where m.id in ");
			totalSql.append("select count(1) as total from merchant_info m inner join merchant_employees me on me.merchant_id=m.id and me.employees_type=1 where m.id in ");
				
		}
		
		sql.append("(select ma.merchant_id from merchant_photo mp join merchant_album ma on mp.album_id=ma.id and mp.join_time>'2016-06-01 00:00:00' ");
		totalSql.append("(select ma.merchant_id from merchant_photo mp join merchant_album ma on mp.album_id=ma.id and mp.join_time>'2016-06-01 00:00:00' ");
		sql.append(Util.getdateFilter("mp.join_time",
					paramMap.get("start_time"), paramMap.get("off_time")));
		totalSql.append(Util.getdateFilter("mp.join_time",
					paramMap.get("start_time"), paramMap.get("off_time")));
		
		sql.append(" group by ma.merchant_id ) and ");
		totalSql.append(" group by ma.merchant_id ) and ");
		
		strFilter.put("m.province", paramMap.get("province"));
		strFilter.put("m.city", paramMap.get("city"));
		strFilter.put("m.invitation_code", paramMap.get("invitation_code"));
		strFilter.put("m.app_type", paramMap.get("app_type"));
		strFilter.put("me.phone", paramMap.get("phone"));
		strLikeFilter.put("m.location_address", paramMap.get("area"));
		strLikeFilter.put("m.name", paramMap.get("name"));
		
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter));
		
		
		long total = Db.find(totalSql.toString()).get(0).getLong("total");
		
		 
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<Record> merPhotoStatic =  Db.find(sql
				.toString());
		if (merPhotoStatic.size() > 0) {
			merPhotoStatic.get(0).set("total", total);
			for(int i=0;i<merPhotoStatic.size();i++){
			merPhotoStatic.get(i).set("id", merPhotoStatic.get(i).getLong("id").toString());
			List<Record> photoTime=Db.find("SELECT mp.join_time,mp.path FROM merchant_photo mp JOIN merchant_album ma ON mp.album_id = ma.id where  ma.merchant_id = ? ORDER BY mp.join_time desc limit 1",merPhotoStatic.get(i).getStr("id"));
			if(photoTime!=null&&photoTime.size()>0){
				merPhotoStatic.get(i).set("photo_time", photoTime.get(0).getDate("join_time"));
				String pic=photoTime.get(0).getStr("path");
				if(!StringUtil.isNullStr(pic)){
					pic = BusinessUtil.getFileUrl(pic);
					merPhotoStatic.get(i).set("path", pic);
				}
			}	
			}
		}
		return merPhotoStatic;
	}

	@Override
	public List<Record> getOrderRewardList(Map<String, String[]> paramMap) {

		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		
		sql.append("select aor.*,'' as orderObject,'' as orderObjectValue,'' as orderRewardLimit,'' as orderRewardLimitValue from activity_order_reward aor where ");
		totalSql.append("select count(1) as total from activity_order_reward aor where ");
		
		intFilter.put("aor.activity_id", paramMap.get("activity_id"));
		
		sql.append(Util.getFilter(strFilter, intFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter));
		
		
		long total =  Db.find(totalSql.toString()).get(0).getLong("total");
		
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<Record> orderReward = Db.find(sql
				.toString());
		if (orderReward.size() > 0) {
			orderReward.get(0).set("total", total);
			String sqlRule="select * from activity_order_reward_rule_detail where activity_id=?";
			for(int i=0;i<orderReward.size();i++){
			 long activity_id=orderReward.get(i).getLong("activity_id");
			 List<Record> ruleList=Db.find(sqlRule.toString(),activity_id);
			 List<Record> ruleSingleList=new ArrayList<Record>();
			 if(ruleList!=null&&ruleList.size()>0){
				 for(int j=0;j<ruleList.size();j++){
					if(ruleList.get(j).getInt("rule_group")==1) {
						orderReward.get(i).set("orderRewardLimit", ruleList.get(j).getLong("rule_id"));
						orderReward.get(i).set("orderRewardLimitValue", ruleList.get(j).getStr("extend1"));
					}else if(ruleList.get(j).getInt("rule_group")==2){
						orderReward.get(i).set("orderObject", ruleList.get(j).getLong("rule_id"));
						orderReward.get(i).set("orderObjectValue", ruleList.get(j).getStr("extend1"));
				
					}else if(ruleList.get(j).getInt("rule_group")==3){
						ruleSingleList.add(ruleList.get(j));
					}
				 } 
			 }
			 orderReward.get(i).set("singleRule", ruleSingleList);
			}
		}
		return orderReward;
	
	}

	@Override
	public Boolean saveOrderReward(Map<String, String[]> param) {
		String id = StringUtil.null2Str(param.get("id")[0]);
		boolean isUpdate = false ; 
		if(StringUtil.isNotNullMap(param, "id")){
			isUpdate = true;
		}
		String activity_id=param.get("activity_id")[0];
		String reward_type=param.get("reward_type")[0];
		String handle_type=param.get("handle_type")[0];
		String sql="select id from activity_order_reward where activity_id=?";
		List<Record> list=Db.find(sql,activity_id);
		Record re=null;
		boolean flag = false;
		if(isUpdate){
			// 更新
			if(list!=null&&list.size()>0){
				long oldId=list.get(0).getLong("id");
				if(!id.equals(String.valueOf(oldId))){
					return flag;
				}
			}
			re=	Db.findById("activity_order_reward", id).set("activity_id", activity_id).set("reward_type", reward_type)
			      .set("handle_type", handle_type);
			Db.update("activity_order_reward", re);
		}else{
			// 新增
			if(list!=null&&list.size()>0){
				return flag;
			}
			re=new Record();
			re.set("activity_id", activity_id).set("reward_type", reward_type)
		      .set("handle_type", handle_type);
			Db.save("activity_order_reward", re);
		}
		flag = true;
		return flag;
	}

	@Override
	public Boolean deleteOrderReward(Map<String, String[]> param) {
		boolean flag = false;
		String[] ids = param.get("id")[0].split(",");
		for (int i = 0; i < ids.length; i++) {
			Db.deleteById("activity_order_reward", ids[i]);
		}
		flag = true;
		return flag;
	}

	@Override
	public int editOrderRewardRule(Map<String, String[]> param) {
		int flag = 0;
		String activity_id = StringUtil.null2Str(param.get("activity_id")[0]);
	    int count=StringUtil.nullToInteger(param.get("count")[0]);
		String orderObject=param.get("orderObject")[0];
		String orderObjectValue=param.get("orderObjectValue")[0];
		String orderRewardLimit=param.get("orderRewardLimit")[0];
		String orderRewardLimitValue=param.get("orderRewardLimitValue")[0];
		
		String sql="delete from activity_order_reward_rule_detail where activity_id=?";
		Db.update(sql,activity_id);
		Record re=null;
		
		re=new Record();
		re.set("rule_id", orderObject).set("rule_group", 2).set("activity_id", activity_id).set("extend1", orderObjectValue);
		Db.save("activity_order_reward_rule_detail", re);
		
		re=new Record();
		re.set("rule_id", orderRewardLimit).set("rule_group", 1).set("activity_id", activity_id).set("extend1", orderRewardLimitValue);
		Db.save("activity_order_reward_rule_detail", re);
		
		if(count>0){
			for(int i=0;i<count;i++){
				re=new Record();
				String extend1=StringUtil.null2Str(param.get("orderCountlimt"+i)[0]);
				if(i>0){
				  int p=i-1; 
				  int orderCountlimt2=Integer.parseInt(extend1);
				  int orderCount1=Integer.parseInt(param.get("orderCount"+p)[0]);
				  if(orderCountlimt2<=orderCount1){
					  continue;
				  }
				}
				String extend2=StringUtil.null2Str(param.get("orderCount"+i)[0]);
				if(Integer.parseInt(extend2)<=Integer.parseInt(extend1)){
					 continue;
				}
				String extend3=StringUtil.null2Str(param.get("orderMoneyLimit"+i)[0]);
				String extend4=StringUtil.null2Str(param.get("lessOrderMoney"+i)[0]);
				String extend5=StringUtil.null2Str(param.get("moreOrderMoney"+i)[0]);
				re.set("rule_id", 6).set("rule_group", 3).set("activity_id", activity_id).set("extend1", extend1)
				.set("extend2", extend2).set("extend3", extend3).set("extend4", extend4).set("extend5", extend5);
				Db.save("activity_order_reward_rule_detail", re);
			}
		}
		flag=1;
		return flag;
	}

	@Override
	public List<Record> getOrderRewardOpenCity(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		
		sql.append("select aor.* from activity_order_reward_city aor where ");
		totalSql.append("select count(1) as total from activity_order_reward_city aor where ");
		
		intFilter.put("aor.activity_id", paramMap.get("activity_id"));
		strFilter.put("aor.province", paramMap.get("province"));
		strFilter.put("aor.city", paramMap.get("city"));
		
		sql.append(Util.getFilter(strFilter, intFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter));
		
		
		long total =  Db.find(totalSql.toString()).get(0).getLong("total");
		

        String property = "aor.id";
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
		List<Record> orderReward = Db.find(sql
				.toString());
		if (orderReward.size() > 0) {
			orderReward.get(0).set("total", total);
		}
		return orderReward;
	
	
	}

	@Override
	public boolean addOrderRewardOpenCity(Map<String, String[]> param) {
		boolean flag=false;
		String activity_id=param.get("activity_id")[0];
		String city_id=param.get("id")[0];
		String[] enIds=city_id.split(",");
		Record re=null;
		List<Record> lists=null;
		List<Record> listCitys=null;
		List<Record> listSpecil=null;
		String sql="select id from activity_order_reward_city where activity_id=? and province=? and city=?";
		String sqlCity="select p.area as city,(select area from area where id=p.parent_id) as province from area p where id=?";
		String sqlOther="select count(1) from activity_order_reward_city where province=? and city=? and activity_id in (select id from activity_info where is_pub=1 and now() BETWEEN stime and etime)";
		
		for(int i=0;i<enIds.length;i++){
			listCitys=Db.find(sqlCity,enIds[i]);
			String province=listCitys.get(0).getStr("province");
			String city=listCitys.get(0).getStr("city");
			
			if (StringUtil.matchProvince(province)) {
				listSpecil = Db.find(sql, activity_id, province, province);
				if (listSpecil != null && listSpecil.size() > 0) {
					continue;
				}
			long cout=Db.queryLong(sqlOther, province, province);	
			if (cout > 0) {
				continue;
			}	
				re=new Record();
				re.set("activity_id", activity_id);
				re.set("province", province);
				re.set("city", province);
				Db.save("activity_order_reward_city", re);
				flag=true;
			} else {
				lists = Db.find(sql, activity_id, province, city);
				if (lists != null && lists.size() > 0) {
					continue;
				}
				
				long cout=Db.queryLong(sqlOther, province, city);	
				if (cout > 0) {
					continue;
				}	
				
			    re=new Record();
			    re.set("activity_id", activity_id);
			    re.set("province", province);
			    re.set("city", city);
			    Db.save("activity_order_reward_city", re);
			    flag=true;
			}
		}
		return flag;
	}

	@Override
	public boolean deleteOrderRewardOpenCity(Map<String, String[]> param) {
		boolean flag = false;
		String[] ids = param.get("id")[0].split(",");
		for (int i = 0; i < ids.length; i++) {
			Db.deleteById("activity_order_reward_city", ids[i]);
		}
		flag = true;
		return flag;
	}

	@Override
	public List<Record> getOrderRewardOpenService(Map<String, String[]> paramMap) {

		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		
		sql.append("select aor.*,(select service_type_name from service_type where id=aor.service_id) as service_name,(SELECT t.name FROM catalog t WHERE t.alias=aor.app_type and t.level=0) AS app_name from activity_order_reward_service aor where ");
		totalSql.append("select count(1) as total from activity_order_reward_service aor where ");
		
		intFilter.put("aor.activity_id", paramMap.get("activity_id"));
		intFilter.put("aor.service_id", paramMap.get("service_id"));
		strFilter.put("aor.app_type", paramMap.get("app_type"));
		
		sql.append(Util.getFilter(strFilter, intFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter));
		
		
		long total =  Db.find(totalSql.toString()).get(0).getLong("total");
		

        String property = "aor.app_type";
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
		List<Record> orderReward = Db.find(sql
				.toString());
		if (orderReward.size() > 0) {
			orderReward.get(0).set("total", total);
		}
		return orderReward;
	
	}

	@Override
	public boolean addOrderRewardOpenService(Map<String, String[]> param) {
		boolean flag=false;
		String activity_id=param.get("activity_id")[0];
		String service_id=param.get("service_id")[0];
		String app_type=param.get("app_type")[0];
		Record re=null;
		List<Record> lists=null;
		List<Record> listSers=null;
		String sql="select id from activity_order_reward_service where activity_id=? and service_id=?";
		String sqlOther="select count(1) from activity_order_reward_service where service_id=? and activity_id in (select id from activity_info where is_pub=1 and now() BETWEEN stime and etime)";
		
		if(service_id.equals("")){
			String	cataIds = getCataIdsByAlias(app_type);
			StringBuffer sqlSer = new StringBuffer();
			if(StringUtil.isNullStr(cataIds)){
				return flag;
			}
			sqlSer.append("select st.id as service_id,st.service_type_name as service_name from service_type st join catalog_service cs on st.id=cs.service_id where cs.catalog_id in (" ).append(cataIds);
			sqlSer.append(") ");
			listSers=Db.find(sqlSer.toString());
			if(listSers!=null&&listSers.size()>0){
				for(int i=0;i<listSers.size();i++){
					lists=Db.find(sql,activity_id,listSers.get(i).getLong("service_id"));
					if(lists!=null&&lists.size()>0){
						continue;
					}
					long cout=Db.queryLong(sqlOther, listSers.get(i).getLong("service_id"));	
					if (cout > 0) {
						continue;
					}
					
					re=new Record();
					re.set("activity_id", activity_id);
					re.set("service_id", listSers.get(i).getLong("service_id"));
					re.set("app_type", app_type);
					Db.save("activity_order_reward_service", re);
					flag=true;
				}
			}
			
		}else{
			lists=Db.find(sql,activity_id,service_id);
			if(lists!=null&&lists.size()>0){
				return flag;
			}
			long cout=Db.queryLong(sqlOther, service_id);	
			if (cout > 0) {
				return flag;
			}
			re=new Record();
			re.set("activity_id", activity_id);
			re.set("service_id", service_id);
			re.set("app_type", app_type);
			Db.save("activity_order_reward_service", re);
			flag=true;
		}
		return flag;
	}

	@Override
	public boolean deleteOrderRewardOpenService(Map<String, String[]> param) {
		boolean flag = false;
		String[] ids = param.get("id")[0].split(",");
		for (int i = 0; i < ids.length; i++) {
			Db.deleteById("activity_order_reward_service", ids[i]);
		}
		flag = true;
		return flag;
	}
	private String getCataIdsByAlias(String app_type) {
		String sql="select id,leaf from catalog where level=0 and alias=?";
		StringBuffer cataIds = new StringBuffer();
		
		List<Record> res=Db.find(sql,app_type);
		
		if(res!=null&&res.size()>0){
			for(Record re:res){
				if(re.getInt("leaf")==1){
					cataIds.append(re.getInt("id"));
					cataIds.append(",");
			}else{
				cataIds.append(re.getInt("id"));
				cataIds.append(",");
				recursiveAliasCataIds(cataIds,re.getInt("id"));
			}
			}
		}
		if(cataIds.length()>0&&cataIds.indexOf(",")!=0){
			return cataIds.substring(0,cataIds.lastIndexOf(","));
		}
		return cataIds.toString();
	}
	private void recursiveAliasCataIds(StringBuffer cataIds, Integer id) {
		String sql="select id,leaf from catalog where parentid=?";
		List<Record> res=Db.find(sql,id);
		if(res!=null&&res.size()>0){
			for(Record re:res){
				if(re.getInt("leaf")==1){
					cataIds.append(re.getInt("id"));
					cataIds.append(",");
				}else{
					recursiveAliasCataIds(cataIds,re.getInt("id"));
				}
			}
		}
		
	}

	@Override
	public List<Record> getOrderRewardAccountList(Map<String, String[]> paramMap) {
		if(!paramMap.get("userPhone")[0].equals("")|| !paramMap.get("merPhone")[0].equals("")){
			return getOrderRewardAccountbyPhoneList(paramMap);
		}
		
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		
		sql.append("select mi.name as merchant_name,aor.*,(select title from activity_info where id=aor.activity_id) as activity_name,(SELECT ROUND((e.attitude_evaluation+e.speed_evaluation+e.quality_evaluation)/3,1) from evaluation e where e.order_id= aor.order_id) as evaluation,(select service_type_name from service_type where id=aor.service_id) as service_name,(SELECT t.name FROM catalog t WHERE t.alias=mi.app_type and t.level=0) AS app_name"
				+ ",(select order_pay_type from order_info where id=aor.order_id) as order_pay_type,(select phone from user_info where id=aor.user_id) as userPhone ,(select phone from merchant_employees where merchant_id=aor.merchant_id and employees_type=1 limit 1) as merPhone from activity_order_reward_detail aor inner join merchant_info mi on aor.merchant_id=mi.id and mi.is_del=0 where ");
		totalSql.append("select count(1) as total from activity_order_reward_detail aor inner join merchant_info mi on aor.merchant_id=mi.id and mi.is_del=0 where ");
		
		strFilter.put("mi.name", paramMap.get("merchant_name"));
		strFilter.put("aor.province", paramMap.get("province"));
		strFilter.put("aor.city", paramMap.get("city"));
		intFilter.put("aor.activity_id", paramMap.get("activity_id"));
		intFilter.put("aor.service_id", paramMap.get("service_id"));
		intFilter.put("aor.is_transfer", paramMap.get("is_transfer"));
		strFilter.put("aor.order_no", paramMap.get("order_no"));
		strFilter.put("mi.app_type", paramMap.get("app_type"));
		
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getdateFilter("aor.pay_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		totalSql.append(Util.getFilter(strFilter, intFilter)).append(Util.getdateFilter("aor.pay_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		
		long total =  Db.find(totalSql.toString()).get(0).getLong("total");
		
        String property = "aor.pay_time";
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
		List<Record> orderReward = Db.find(sql
				.toString());
		if (orderReward.size() > 0) {
			orderReward.get(0).set("total", total);
		}
		return orderReward;
	}

	private List<Record> getOrderRewardAccountbyPhoneList(Map<String, String[]> paramMap) {

		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		
		sql.append("select mi.name as merchant_name,me.phone as merPhone,ui.phone as userPhone,aor.*,(select title from activity_info where id=aor.activity_id) as activity_name,(SELECT ROUND((e.attitude_evaluation+e.speed_evaluation+e.quality_evaluation)/3,1) from evaluation e where e.order_id= aor.order_id) as evaluation,(select service_type_name from service_type where id=aor.service_id) as service_name,(SELECT t.name FROM catalog t WHERE t.alias=mi.app_type and t.level=0) AS app_name"
				+ ",(select order_pay_type from order_info where id=aor.order_id) as order_pay_type from activity_order_reward_detail aor inner join merchant_info mi on aor.merchant_id=mi.id and mi.is_del=0 join merchant_employees me on me.merchant_id=aor.merchant_id join user_info ui on ui.id=aor.user_id where ");
		totalSql.append("select count(1) as total from activity_order_reward_detail aor inner join merchant_info mi on aor.merchant_id=mi.id and mi.is_del=0 join merchant_employees me on me.merchant_id=aor.merchant_id join user_info ui on ui.id=aor.user_id where ");
		
		strFilter.put("mi.name", paramMap.get("merchant_name"));
		strFilter.put("aor.province", paramMap.get("province"));
		strFilter.put("aor.city", paramMap.get("city"));
		intFilter.put("aor.activity_id", paramMap.get("activity_id"));
		intFilter.put("aor.service_id", paramMap.get("service_id"));
		intFilter.put("aor.is_transfer", paramMap.get("is_transfer"));
		strFilter.put("aor.order_no", paramMap.get("order_no"));
		strFilter.put("mi.app_type", paramMap.get("app_type"));
		strFilter.put("ui.phone", paramMap.get("userPhone"));
		strFilter.put("me.phone", paramMap.get("merPhone"));
		
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getdateFilter("aor.pay_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		totalSql.append(Util.getFilter(strFilter, intFilter)).append(Util.getdateFilter("aor.pay_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		
		long total =  Db.find(totalSql.toString()).get(0).getLong("total");
		
        String property = "aor.pay_time";
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
		List<Record> orderReward = Db.find(sql
				.toString());
		if (orderReward.size() > 0) {
			orderReward.get(0).set("total", total);
		}
		return orderReward;
	
	}

	@Override
	public List<OrderRewardAccount> exportOrderRewardAccountExcel(Map<String, String[]> paramMap) {
		
		if(!paramMap.get("userPhone")[0].equals("")|| !paramMap.get("merPhone")[0].equals("")){
			return exportOrderRewardAccountbyPhoneExcel(paramMap);
		}
		
		StringBuffer sql = new StringBuffer();
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT;
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		
		sql.append("select mi.name as merchant_name,DATE_FORMAT(aor.pay_time, '%Y-%m-%d %H:%i:%s') as payTime,aor.*,(case aor.is_transfer when 0 then '未到账' when 1 then '已到账' when 1 then '已到账' when 2 then '异常' else '未知' end) as isTransfer,(select title from activity_info where id=aor.activity_id) as activity_name,(select service_type_name from service_type where id=aor.service_id) as service_name,(SELECT t.name FROM catalog t WHERE t.alias=mi.app_type and t.level=0) AS app_name"
				+ ",(select (case oi.order_pay_type when 1 then '支付宝支付' when 2 then '微信支付' when 3 then '现金支付' when 4 then '免单' else '' end) as orderPayType from order_info oi where oi.id=aor.order_id) as orderPayType,(SELECT ROUND((e.attitude_evaluation+e.speed_evaluation+e.quality_evaluation)/3,1) from evaluation e where e.order_id= aor.order_id) as evaluation "
				+ ",(select phone from user_info where id=aor.user_id) as userPhone ,(select phone from merchant_employees where merchant_id=aor.merchant_id and employees_type=1) as merPhone from activity_order_reward_detail aor inner join merchant_info mi on aor.merchant_id=mi.id where ");
		
		strFilter.put("mi.name", paramMap.get("merchant_name"));
		strFilter.put("aor.province", paramMap.get("province"));
		strFilter.put("aor.city", paramMap.get("city"));
		intFilter.put("aor.activity_id", paramMap.get("activity_id"));
		intFilter.put("aor.service_id", paramMap.get("service_id"));
		intFilter.put("aor.is_transfer", paramMap.get("is_transfer"));
		strFilter.put("aor.order_no", paramMap.get("order_no"));
		strFilter.put("mi.app_type", paramMap.get("app_type"));
		strFilter.put("ui.phone", paramMap.get("userPhone"));
		strFilter.put("me.phone", paramMap.get("merPhone"));
		
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getExportdateFilter("aor.pay_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<OrderRewardAccount> orderRewardTotal = OrderRewardAccount.dao.find(sql
				.toString());
		
		return orderRewardTotal;
	}

	private List<OrderRewardAccount> exportOrderRewardAccountbyPhoneExcel(Map<String, String[]> paramMap) {

		StringBuffer sql = new StringBuffer();
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT;
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		
		sql.append("select mi.name as merchant_name,DATE_FORMAT(aor.pay_time, '%Y-%m-%d %H:%i:%s') as payTime,aor.*,(case aor.is_transfer when 0 then '未到账' when 1 then '已到账' when 2 then '异常' else '未知' end) as isTransfer,(select title from activity_info where id=aor.activity_id) as activity_name,(select service_type_name from service_type where id=aor.service_id) as service_name,(SELECT t.name FROM catalog t WHERE t.alias=mi.app_type and t.level=0) AS app_name"
				+ ",(SELECT ROUND((e.attitude_evaluation+e.speed_evaluation+e.quality_evaluation)/3,1) from evaluation e where e.order_id= aor.order_id) as evaluation "
				+ ",(select (case oi.order_pay_type when 1 then '支付宝支付' when 2 then '微信支付' when 3 then '现金支付' when 4 then '免单' else '' end) as orderPayType from order_info oi where oi.id=aor.order_id) as orderPayType,(select phone from user_info where id=aor.user_id) as userPhone ,(select phone from merchant_info where id=aor.merchant_id and employees_type=1) as merPhone from activity_order_reward_detail aor inner join merchant_info mi on aor.merchant_id=mi.id "
				+ " join merchant_employees me on me.merchant_id=aor.merchant_id join user_info ui on ui.id=aor.user_idwhere ");
		
		strFilter.put("mi.name", paramMap.get("merchant_name"));
		strFilter.put("aor.province", paramMap.get("province"));
		strFilter.put("aor.city", paramMap.get("city"));
		intFilter.put("aor.activity_id", paramMap.get("activity_id"));
		intFilter.put("aor.service_id", paramMap.get("service_id"));
		intFilter.put("aor.is_transfer", paramMap.get("is_transfer"));
		strFilter.put("aor.order_no", paramMap.get("order_no"));
		strFilter.put("mi.app_type", paramMap.get("app_type"));
		
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getExportdateFilter("aor.pay_time",
				paramMap.get("start_time"), paramMap.get("off_time")));
		
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<OrderRewardAccount> orderRewardTotal = OrderRewardAccount.dao.find(sql
				.toString());
		
		return orderRewardTotal;
	
	}

	@Override
	@Before(Tx.class)
	public boolean delOrderReAccount(Map<String, String[]> param,String name) {
		boolean flag = false;
		String sql="update merchant_statistics set total_income_price=total_income_price+?,surplus_price=surplus_price+? where merchant_id=?";
        Record re=null;
        Record reDetail=null;
		String[] ids = param.get("id")[0].split(",");
		for (int i = 0; i < ids.length; i++) {
			re=Db.findById("activity_order_reward_detail", ids[i]);
			if(re!=null){
				int is_transfer=re.getInt("is_transfer");
				BigDecimal amount=re.getBigDecimal("amount");
				long merchant_id=re.getLong("merchant_id");
				int compare=amount.compareTo(BigDecimal.ZERO);
				if(is_transfer==1){
					continue;
				}else if(compare==1){
					Db.update(sql, re.getBigDecimal("amount"),re.getBigDecimal("amount"),merchant_id);
					//插入到帐明细
					reDetail=new Record();
					reDetail.set("merchant_id",merchant_id).set("payment_type", 5).set("business_id", re.getLong("order_id"))
					.set("payment_price", amount).set("app_type", re.getStr("app_type")).set("payment_time", new Date());
					Db.save("merchant_payment_details", reDetail);
					
					re.set("account_time", new Date()).set("is_transfer", 1).set("user_name",name );
					Db.update("activity_order_reward_detail", re);
					flag = true;
					writeMq(re);
				}else if(compare==0){
					re.set("account_time", new Date()).set("is_transfer", 1).set("user_name",name );
					Db.update("activity_order_reward_detail", re);
					flag = true;
				}else{
					continue;
				}
			}
		}
		return flag;
	}
	private static void writeMq(Record re) {
		Map<String, Object> configMap = new HashMap<String, Object>();
		String sql = "SELECT me.user_id,me.phone,(select mi.name from merchant_info mi where mi.id=me.merchant_id) as name"
				+ " from merchant_employees me where me.employees_type=1 and me.merchant_id=? limit 1";

		List<Record> merInfo = Db.find(sql, re.getLong("merchant_id"));
		if (merInfo != null && merInfo.size() > 0) {
			configMap.put("merchantId", re.getLong("merchant_id"));
			configMap.put("merchantUserId", merInfo.get(0).getLong("user_id"));
			configMap.put("merchantUserPhone", merInfo.get(0).getStr("phone"));
			configMap.put("merchantName", merInfo.get(0).getStr("name"));
			configMap.put("userName", "");
			configMap.put("orderId", re.getLong("order_id"));
			configMap.put("rewardTime", DateUtil.getDate());
			configMap.put("transSeq", re.getLong("id"));
			configMap.put("transAmount", re.getBigDecimal("amount"));
			configMap.put("remark", "订单奖励");
		}
		String msg = JSONObject.toJSONString(configMap);
		try {
			String encryptedText= AESUtil.parseByte2HexStr(AESUtil.encrypt(msg, "367937E1967092280C56077755E4C65B"));
			MqUtil.writeToMQ("orderTemplate", "opay.orderRewardExchange", encryptedText);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public List<Record> getPhotimeByMerId(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		
		String merchant_id=paramMap.get("merchantsid")[0];
		sql.append("select ma.merchant_id,mp.join_time FROM merchant_photo mp JOIN merchant_album ma ON mp.album_id = ma.id AND mp.join_time > '2016-06-01'	WHERE ma.merchant_id =?");
		totalSql.append("select count(1) as total from merchant_photo mp JOIN merchant_album ma ON mp.album_id = ma.id AND mp.join_time > '2016-06-01'	WHERE ma.merchant_id =? ");
		
		long total =  Db.find(totalSql.toString(),merchant_id).get(0).getLong("total");
		
        String property = "mp.join_time";
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
		List<Record> orderReward = Db.find(sql
				.toString(),merchant_id);
		if (orderReward.size() > 0) {
			orderReward.get(0).set("total", total);
		}
		return orderReward;
	
	}

	@Override
	public boolean editOrderRewardAccount(Map<String, String[]> param, String operUserName) {
		boolean flag=false;
		String id=param.get("id")[0];
		String is_transfer=param.get("is_transfer")[0];
		String remark=param.get("remark")[0];
		Record re=Db.findById("activity_order_reward_detail", id);
		if(re!=null){
			if(re.getInt("is_transfer")==1){
				return flag;
			}
			if(is_transfer.equals("1")){
				return flag;
			}
			re.set("is_transfer", is_transfer).set("remark", remark).set("user_name", operUserName);
			Db.update("activity_order_reward_detail", re);
		}
		flag=true;
		return flag;
	}
}

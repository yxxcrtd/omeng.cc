package com.shanjin.manager.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.impl.CommonCacheServiceImpl;
import com.shanjin.manager.Bean.BlackUser;
import com.shanjin.manager.Bean.MerchantsInfo;
import com.shanjin.manager.Bean.UserFeedback;
import com.shanjin.manager.Bean.UserInfo;
import com.shanjin.manager.constant.Constant.PAGE;
import com.shanjin.manager.constant.Constant.SORT;
import com.shanjin.manager.dao.UserInfoDao;
import com.shanjin.manager.utils.BusinessUtil;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

public class UserInfoDaoImpl implements UserInfoDao{
	private static ICommonCacheService commonCacheService = new CommonCacheServiceImpl();
	public List<UserInfo> getUserList(Map<String, String[]> paramMap) {
		int pageNumber = paramMap.get("start")[0]==null ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0]==null ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		sql.append("select u.*,(select count(1) from black_user bu where bu.user_id=u.id) as blackCount from user_info u where u.is_del=0 and ");
		totalSql.append("select count(1) as total from user_info u where u.is_del=0 and ");
		Map<String,String[]> strFilter=new HashMap<String,String[]>();
		Map<String,String[]> intFilter=new HashMap<String,String[]>();
		
		strFilter.put("u.phone", paramMap.get("phone"));
		strFilter.put("u.province", paramMap.get("province"));
		strFilter.put("u.city", paramMap.get("city"));
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getdateFilter("u.join_time", paramMap.get("start_time"),paramMap.get("off_time") )).append(Util.getdateFilter("u.last_login_time", paramMap.get("login_start_time"),paramMap.get("login_off_time") ));
		totalSql.append(Util.getFilter(strFilter, intFilter)).append(Util.getdateFilter("u.join_time", paramMap.get("start_time"),paramMap.get("off_time") )).append(Util.getdateFilter("u.last_login_time", paramMap.get("login_start_time"),paramMap.get("login_off_time") ));
		
		long total=UserInfo.dao.find(totalSql.toString()).get(0).getLong("total");
		
		 String property = "u.join_time";
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
	    List<UserInfo> userList =UserInfo.dao.find(sql.toString());
	    if(userList.size()>0){
	    	userList.get(0).setTotal(total);
	    	for(int i=0;i<userList.size();i++){
	    		userList.get(i).set("id", userList.get(i).getLong("id").toString());
				}
	    }
		return userList;
	}

	public long total() {
		String sql = "SELECT count(1) FROM user_info ";
		long total=UserInfo.dao.find(sql).size();
		return total;
	}

	public Boolean deleteUser(String id) {
		boolean flag=false;
		UserInfo.dao.findById(id).set("is_del", 1).update();
		flag=true;
		return flag;
	}

	public Boolean editUser(Map<String, String[]> param) {
		boolean flag=false;
		String id=param.get("id")[0];
		System.out.println(id);
		String name=param.get("name")[0];
		String sex=param.get("sex")[0];
		String phone=param.get("phone")[0];
		UserInfo.dao.findById(id).set("name", name).set("sex", sex).set("phone", phone).update();
		flag=true;
		return flag;
	}
	public Boolean deletetUserForever(String id) {
		boolean flag=false;
		UserInfo.dao.deleteById(id);
		flag=true;
		return flag;
	}

	public List<UserInfo> getRecyUserList(Map<String, String[]> paramMap) {
		int pageNumber = paramMap.get("start")[0]==null ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0]==null ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		sql.append("select u.id,u.name,u.sex,u.phone,u.join_time,u.remark,u.verification_code,u.verification_time,u.is_verification,u.user_type from user_info u where u.is_del=1 ");
		totalSql.append("select count(1) as total from user_info u where u.is_del=1 ");
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("u.phone", paramMap.get("phone"));
		sql.append(Util.getLikeFilter(strLikeFilter)).append(Util.getdateFilter("join_time", paramMap.get("start_time"),paramMap.get("off_time") ));;
		totalSql.append(Util.getLikeFilter(strLikeFilter)).append(Util.getdateFilter("join_time", paramMap.get("start_time"),paramMap.get("off_time") ));;
		
		String property = "u.join_time";
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
		long total=UserInfo.dao.find(totalSql.toString()).get(0).getLong("total");
		
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
	    List<UserInfo> userList =UserInfo.dao.find(sql.toString());
	    if(userList.size()>0){
	    	userList.get(0).setTotal(total);
	    	for(int i=0;i<userList.size();i++){
	    		userList.get(i).set("id", userList.get(i).getLong("id").toString());
				}
	    }
		return userList;
	}

	public List<UserInfo> getExportUserList(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;  
		int pageSize = PAGE.PAGESIZE_EXPORT;
		sql.append("select u.id,u.name,(select count(1) from user_merchant_collection umc where umc.user_id=u.id) as focusMer,"
				+ "(select count(1) from merchant_employees me where me.user_id=u.id and me.employees_type=1 and me.is_del=0) as openMer,"
				+ "(select count(1) from order_info oi where oi.user_id=u.id ) as orderCount,"
				+ "(select count(1) from order_info oi where oi.user_id=u.id and oi.order_status=5) as overOrderCount,"
				
				+ "(case (select count(1) from black_user bu where bu.user_id=u.id) when 0 then '正常用户' else '黑名单' end) as user_status,(case u.user_type when 0 then '普通用户' when 1 then '商铺用户' else '' end) as user_type,(case u.sex when 1 then '男' when 2 then '女' else '保密' end) as sex,u.phone,DATE_FORMAT(u.join_time, '%Y-%m-%d %H:%i:%s') as join_time,u.remark,u.verification_code,u.verification_time,u.is_verification,DATE_FORMAT(u.last_login_time, '%Y-%m-%d %H:%i:%s') as last_login_time from user_info u where u.is_del=0 and ");
		Map<String,String[]> strFilter=new HashMap<String,String[]>();
		Map<String,String[]> intFilter=new HashMap<String,String[]>();
		strFilter.put("u.phone", paramMap.get("phone"));
		strFilter.put("u.province", paramMap.get("province"));
		strFilter.put("u.city", paramMap.get("city"));
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getExportdateFilter("u.join_time", paramMap.get("start_time"),paramMap.get("off_time") ))
		.append(Util.getExportdateFilter("u.last_login_time", paramMap.get("login_start_time"),paramMap.get("login_off_time") ));
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		List<UserInfo> userList =UserInfo.dao.find(sql.toString());
		return userList;
	}

	@Override
	public Map<String, Object> getUserDetail(Long userId) {
		Map<String, Object> resMap = new HashMap<String,Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("select u.name,(case u.sex when 1 then '男' when 2 then '女' else '保密' end) as sex,u.phone,u.province,u.city,"
				+ "(select count(1) from user_merchant_collection umc where umc.user_id=?) as focusMer,"
				+ "(select sum(s.bespeak_frequency) from user_statistics s group by user_id having user_id=?) as bespeakfrequency,"
				+ "(select sum(s.service_frequency) from user_statistics s group by user_id having user_id=?) as servicefrequency,"
				+ "(select sum(s.total_pay_price) from user_statistics s group by user_id having user_id=?) as totalPayPrice,"
				+ "(select sum(s.total_actual_price) from user_statistics s group by user_id having user_id=?) as totalActualPrice from user_info u where u.id=?");
		List<Record> userList = Db.find(sql.toString(),userId,userId,userId,userId,userId,userId);
		if(userList!=null&&userList.size()>0){
			Record user=userList.get(0);
			resMap.put("userName",StringUtil.null2Str(user.getStr("name")));
			resMap.put("sex",StringUtil.null2Str(user.getStr("sex")));
			resMap.put("telephone",StringUtil.null2Str(user.getStr("phone")));
			resMap.put("province", StringUtil.null2Str(user.getStr("province")));
			resMap.put("city",StringUtil.null2Str(user.getStr("city")));
			resMap.put("focusMer",StringUtil.null2Str(user.getLong("focusMer")));
			resMap.put("bespeakfrequency",StringUtil.nullToBigDe(user.getBigDecimal("bespeakfrequency")));
			resMap.put("servicefrequency",StringUtil.nullToBigDe(user.getBigDecimal("servicefrequency")));
			resMap.put("totalPayPrice",StringUtil.nullToBigDe(user.getBigDecimal("totalPayPrice")));
			resMap.put("totalActualPrice",StringUtil.nullToBigDe(user.getBigDecimal("totalActualPrice")));
		}
		
		String merchantName="select m.name from  merchant_employees me join merchant_info m on me.merchant_id=m.id where me.user_id=?";
		List<Record> merchantList=Db.find(merchantName,userId);
		if(merchantList!=null&&merchantList.size()>0){
			StringBuffer merchantNameList = new StringBuffer(); 
			for(Record r:merchantList){
				merchantNameList.append(r.getStr("name")).append(",");
			}
			merchantNameList.substring(0,merchantNameList.length()-1);
			resMap.put("merchantNameList",StringUtil.null2Str(merchantNameList));
		}else{
			resMap.put("merchantNameList","无");
		}
		
		String order="select count(1) as total from order_info o where o.user_id=? and o.order_status=?";
		String orderAll="select count(1) as total from order_info o where o.user_id=? ";
		resMap.put("newBooking",Db.queryLong(order,userId,1));
		resMap.put("ongoing",Db.queryLong(order,userId,2));
		resMap.put("completed",Db.queryLong(order,userId,5));
		resMap.put("timeOut",Db.queryLong(order,userId,6));
		resMap.put("closed",Db.queryLong(order,userId,7));
		resMap.put("allOrderCount",Db.queryLong(orderAll,userId));
		
		return resMap;
	}

	@Override
	public List<UserFeedback> getFeedBackList(Map<String, String[]> paramMap) {
		int pageNumber = paramMap.get("start")[0]==null ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0]==null ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		Map<String,String[]> strFilter=new HashMap<String,String[]>();
		Map<String,String[]> intFilter=new HashMap<String,String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		sql.append("select f.id,f.customer_id,f.customer_type,f.feedback_type,f.phone,f.feedback_time,f.content,f.app_type,f.status,(SELECT count(1) FROM feedback_attachment fa WHERE fa.feedback_id=f.id) as album, ");
		if(StringUtil.isNotNullMap(paramMap,"customer_type")){
			if(paramMap.get("customer_type")[0].equals("1")){
				sql.append("(select m.name from merchant_info m where m.id=f.customer_id) as name,a.app_name ");
				sql.append(" from feedback f left join merchant_app_info a on f.app_type=a.app_type where f.is_del=0 and ");
				totalSql.append("select count(1) as total from feedback f left join merchant_app_info a on f.app_type=a.app_type where f.is_del=0 and ");
				strFilter.put("a.app_type", paramMap.get("app_type"));
			}else{
				sql.append("(select u.name from user_info u where u.id=f.customer_id) as name,'O盟' as app_name ");
				sql.append(" from feedback f where f.is_del=0 and ");
				totalSql.append("select count(1) as total from feedback f where f.is_del=0 and ");
				
			}
		}else{
			sql.append("(select u.name from user_info u where u.id=f.customer_id) as name,'O盟' as app_name ");
			sql.append(" from feedback f where f.is_del=0 and ");
			totalSql.append("select count(1) as total from feedback f where f.is_del=0 and ");
			
		}
		
	
		strLikeFilter.put("f.phone", paramMap.get("phone"));
		strFilter.put("f.customer_id", paramMap.get("customer_id"));
		intFilter.put("f.feedback_type", paramMap.get("feedback_type"));
		intFilter.put("f.customer_type", paramMap.get("customer_type"));
		intFilter.put("f.status", paramMap.get("status"));
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getdateFilter("feedback_time", paramMap.get("start_time"),paramMap.get("off_time"))).append(Util.getLikeFilter(strLikeFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter)).append(Util.getdateFilter("feedback_time", paramMap.get("start_time"),paramMap.get("off_time") )).append(Util.getLikeFilter(strLikeFilter));
		
		String property = "f.feedback_time";
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
		long total=UserFeedback.dao.find(totalSql.toString()).get(0).getLong("total");
		
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
	    List<UserFeedback> feedBackList =UserFeedback.dao.find(sql.toString());
	    System.out.println(sql);
	    if(feedBackList.size()>0){
	    	feedBackList.get(0).setTotal(total);
	    }
		return feedBackList;
	}

	@Override
	public Boolean deletetUserFeedBack(Map<String, String[]> param) {
		boolean flag = false;
		String[] ids = param.get("id")[0].split(",");
		for (int i = 0; i < ids.length; i++) {
			UserFeedback.dao.findById(ids[i]).set("is_del", 1).update();
		}
		flag = true;
		return flag;
		
	}

	@Override
	public List<UserFeedback> userFeedBackExport(Map<String, String[]> paramMap) {
		int pageNumber =  PAGE.PAGENUMBER_EXPORT;
		int pageSize = PAGE.PAGESIZE_EXPORT;
		StringBuffer sql = new StringBuffer();
		Map<String,String[]> strFilter=new HashMap<String,String[]>();
		Map<String,String[]> intFilter=new HashMap<String,String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		sql.append("select f.id,f.customer_id,f.feedback_type,f.phone,DATE_FORMAT(f.feedback_time, '%Y-%m-%d %H:%i:%s') as feedback_time,f.content,(case f.status when 0 then '未处理' when 1 then '已处理' else '' end) as status,f.app_type, ");
		sql.append("(case f.customer_type when 1 then '商户' when 2 then '用户' else '未知' end) as customer_type,(case f.feedback_type when 1 then '建议' when 2 then '咨询' when 3 then '故障' when 4 then '新需求' when 5 then '闪退、卡顿或界面错位' else '其他' end) as feedback_type,");
		if(StringUtil.isNotNullMap(paramMap,"customer_type")){
			if(paramMap.get("customer_type")[0].equals("1")){
				sql.append("(select m.name from merchant_info m where m.id=f.customer_id) as name,a.app_name ");
				sql.append(" from feedback f left join merchant_app_info a on f.app_type=a.app_type where f.is_del=0 and ");
				strFilter.put("a.app_type", paramMap.get("app_type"));
			}else{
				sql.append("(select u.name from user_info u where u.id=f.customer_id) as name,'O盟' as app_name ");	
				sql.append(" from feedback f where f.is_del=0 and ");
			}
		}else{
			sql.append("(select u.name from user_info u where u.id=f.customer_id) as name,'O盟' as app_name ");	
			sql.append(" from feedback f where f.is_del=0 and ");
		}
        
		strLikeFilter.put("f.phone", paramMap.get("phone"));
		strFilter.put("f.customer_id", paramMap.get("customer_id"));
		intFilter.put("f.feedback_type", paramMap.get("feedback_type"));
		intFilter.put("f.customer_type", paramMap.get("customer_type"));
		intFilter.put("f.status", paramMap.get("status"));
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getExportdateFilter("feedback_time", paramMap.get("start_time"),paramMap.get("off_time"))).append(Util.getLikeFilter(strLikeFilter));
		String property = "f.feedback_time";
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
	    List<UserFeedback> feedBackList =UserFeedback.dao.find(sql.toString());
	    
		return feedBackList;
	}

	@Override
	public List<String> feedBackPicList(Long feedBackId) {
		List<String> resList = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT f.path FROM feedback_attachment f WHERE f.feedback_id=").append(feedBackId);
		List<Record> list = Db.find(sql.toString());
		if(list!=null&&list.size()>0){
			for(Record r : list){
				String pic = StringUtil.null2Str(r.getStr("path"));
				if(!StringUtil.isNullStr(pic)){
					pic = BusinessUtil.getFileUrl(pic);
					resList.add(pic);
				}
			}
		}
		return resList;
	}

	@Override
	public Map<String, Object> getUserFeedBackContent(String id) {
		Map<String, Object> detail=new HashMap<String, Object>();
		String name="";
		String content="";
		if(!StringUtil.isNull(id)){
			UserFeedback uf=UserFeedback.dao.findById(id);
			if(uf != null){
				content=uf.getStr("content");
				name=getNameById(uf);
			}
		}
		detail.put("name", name);
		detail.put("content", content);
		return detail;
	}

	private String getNameById(UserFeedback uf) {
		long id=uf.getLong("customer_id");
		String name="";
		if(uf.getInt("customer_type")==2){
			Record us=Db.findById("user_info", id);
			if(us != null){
			name=us.getStr("name");
			}
		}else if(uf.getInt("customer_type")==1){
			MerchantsInfo ms=MerchantsInfo.dao.findById(id);
			if(ms != null){
			name=ms.getStr("name");
			}
		}
		return name;
	}

	@Override
	public Boolean dealWithFeedBack(String id) {
		boolean flag=false;
		UserFeedback.dao.findById(id).set("status", 1).update();
		flag=true;
		return flag;
	}

	@Override
	public List<BlackUser> getBlackUser(Map<String, String[]> paramMap) {

		int pageNumber = paramMap.get("start")[0]==null ? PAGE.PAGENUMBER : Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0]==null ? PAGE.PAGESIZE : Integer.parseInt(paramMap.get("limit")[0]);
		Map<String,String[]> strFilter=new HashMap<String,String[]>();
		Map<String,String[]> intFilter=new HashMap<String,String[]>();
		
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		sql.append("select u.* from black_user u where ");
		totalSql.append("select count(1) as total from black_user u where ");
		
		strFilter.put("u.phone", paramMap.get("phone"));
		sql.append(Util.getFilter(strFilter,intFilter)).append(Util.getdateFilter("u.join_time", paramMap.get("start_time"),paramMap.get("off_time") ));;
		totalSql.append(Util.getFilter(strFilter,intFilter)).append(Util.getdateFilter("u.join_time", paramMap.get("start_time"),paramMap.get("off_time") ));;
		
		String property = "u.join_time";
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
		long total=BlackUser.dao.find(totalSql.toString()).get(0).getLong("total");
		
		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
	    List<BlackUser> userList =BlackUser.dao.find(sql.toString());
	    if(userList.size()>0){
	    	userList.get(0).setTotal(total);
	    	for(int i=0;i<userList.size();i++){
	    		userList.get(i).set("id", userList.get(i).getLong("id").toString());
				}
	    }
		return userList;
	
	}

	@Override
	public Boolean deleteBlackUser(Map<String, String[]> param) {
		boolean flag = false;
		String[] ids = param.get("id")[0].split(",");
		Record user;
		Map<String,Object> map=null;
		for (int i = 0; i < ids.length; i++) {
			user=Db.findById("black_user", ids[i]);
			map=(Map<String, Object>) commonCacheService.getObject(CacheConstants.USER_BLACKLIST);
			if(map!=null&&map.size()>0){
				map.remove(user.getLong("user_id").toString());
				commonCacheService.setObject(map,CacheConstants.USER_BLACKLIST);
			}
			Db.delete("black_user", user);
		}
		flag = true;
		return flag;
	}

	@Override
	public Boolean addBlackUser(Map<String, String[]> param, String operUserName) {
		boolean flag = false;
		String[] ids = param.get("id")[0].split(",");
		String sql="select count(1) from black_user where user_id=?";
		Record user=null;
		Map<String,Object> map=null;
		for (int i = 0; i < ids.length; i++) {
			user=Db.findById("user_info", ids[i]);
			if(Db.queryLong(sql,ids[i])==0 && user!=null){
			Record re=new Record();
			re.set("user_id", user.getLong("id")).set("phone", user.getStr("phone"))
			   .set("join_time", new Date()).set("operat_name", operUserName);
			Db.save("black_user", re);
			map=(Map<String, Object>) commonCacheService.getObject(CacheConstants.USER_BLACKLIST);
			if(map!=null&&map.size()>0){
				map.put(ids[i], "1");
			}else{
				map=new HashMap<String,Object>();
				map.put(ids[i], "1");
			}
			commonCacheService.setObject(map,CacheConstants.USER_BLACKLIST);
			}else{
				continue;
			}
		}
		flag = true;
		return flag;
	}
	
	@Override
	public Boolean deleteBlackUserByuserId(Map<String, String[]> param) {
		boolean flag = false;
		String[] ids = param.get("id")[0].split(",");
		String sql="delete from black_user where user_id=?";
		Map<String,Object> map=null;
		for (int i = 0; i < ids.length; i++) {
			map=(Map<String, Object>) commonCacheService.getObject(CacheConstants.USER_BLACKLIST);
			if(map!=null&&map.size()>0){
				map.remove(ids[i]);
				commonCacheService.setObject(map,CacheConstants.USER_BLACKLIST);
			}
			Db.update(sql, ids[i]);
		}
		flag = true;
		return flag;
	}
}

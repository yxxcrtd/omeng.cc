package com.shanjin.manager.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.shanjin.manager.Bean.Voucher;
import com.shanjin.manager.constant.Constant.PAGE;
import com.shanjin.manager.constant.Constant.SORT;
import com.shanjin.manager.dao.VourchersDao;
import com.shanjin.manager.utils.BusinessUtil;
import com.shanjin.manager.utils.DateUtil;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

public class VourchersDaoImpl implements VourchersDao {

	public List<Voucher> getVouchers(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		sql.append("select v.id,v.service_type_id,v.cutoff_time,v.price,v.icon_path,v.app_type,v.count,v.is_del,st.service_type_name as coupons_type_name");
		sql.append(" from vouchers_info v inner join service_type st on st.id=v.service_type_id where st.is_del=0 and ");
		totalSql.append("select count(1) as total from vouchers_info v inner join service_type st on st.id=v.service_type_id where st.is_del=0 and ");
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("st.service_type_name", paramMap.get("service_type_name"));
		intFilter.put("v.price", paramMap.get("price"));
		intFilter.put("v.is_del", paramMap.get("is_del"));
		intFilter.put("v.count", paramMap.get("count"));
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter)).append(Util.getdateFilter("v.cutoff_time", paramMap.get("start_time"),paramMap.get("off_time") ));
		totalSql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter)).append(Util.getdateFilter("v.cutoff_time", paramMap.get("start_time"),paramMap.get("off_time") ));
		
		long total = Voucher.dao.find(totalSql.toString()).get(0).getLong("total");

		String property = "v.id";
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

		List<Voucher> vouchers = Voucher.dao.find(sql.toString());
		if (vouchers.size() > 0) {
			vouchers.get(0).setTotal(total);
			String pic = "";
			for(Voucher s : vouchers){
				pic = s.getStr("icon_path");
				if(!StringUtil.isNullStr(pic)){
					pic = BusinessUtil.getFileUrl(pic);
					s.set("icon_path", pic);
				}
			}
		}
		return vouchers;

	}

	public int addVouchers(Map<String, String[]> param, String resultPath) {
		int flag=0;
		
		String service_id = param.get("service_id")[0];
		String price=param.get("price")[0];
		String app_type=param.get("app_type")[0];
		String cutoff_time=param.get("cutoff_time")[0];
		Voucher voucher;
		String selectSql="select cutoff_time from vouchers_info where is_del=0  and service_type_id=? and price=?";
		voucher =new Voucher();
		List<Voucher> vou=voucher.find(selectSql,service_id,price);
		if(vou.size()>0){
			Date date=new Date();
			if(vou.get(0).getDate("cutoff_time").getTime()>date.getTime()){
				flag=2;
				return flag;
			};
		}
		voucher.set("service_type_id", service_id).set("price", price).set("cutoff_time",cutoff_time).set("icon_path", resultPath).set("app_type", app_type).save();
		flag=1;
		return flag;
	}

	public List<Record> getUseVouchers(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);

		sql.append("select ui.phone,oi.vouchers_id,oi.user_id,oi.join_time,vi.service_type_id as coupons_type,(select st.service_type_name from service_type st where  st.id=vi.service_type_id) as voucher_name,(select name from merchant_info where id=oi.merchant_id) as merchants_name from vouchers_info vi,order_info oi,user_info ui where vi.id=oi.vouchers_id and oi.user_id=ui.id and oi.vouchers_id is not null and ");
		totalSql.append("select count(1) as total from order_info oi inner join user_info ui on oi.user_id=ui.id where oi.vouchers_id is not null and ");	
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("ui.phone", paramMap.get("phone"));
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter)).append(Util.getdateFilter("oi.join_time", paramMap.get("start_time"),paramMap.get("off_time") ));
		totalSql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter)).append(Util.getdateFilter("oi.join_time", paramMap.get("start_time"),paramMap.get("off_time") ));
	       
		long total =Db.find(totalSql.toString()).get(0).getLong("total");

		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);
		
		List<Record> userVouchers=Db.find(sql.toString());
		if(userVouchers.size()>0){
			userVouchers.get(0).set("total", total);
			String pic = "";
			for(Record s : userVouchers){
				pic = s.getStr("icon_path");
				if(!StringUtil.isNullStr(pic)){
					pic = BusinessUtil.getFileUrl(pic);
					s.set("icon_path", pic);
				}
			}
		}
		return userVouchers;
	}

	public List<Record> getIssueVouchers(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);

		sql.append("select id,coupons_name,issue_total,issue_time,price from manager_issue_vouchers where 0=0");
		totalSql.append("select count(1) as total from manager_issue_vouchers where 0=0");	
		
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("coupons_name", paramMap.get("coupons_name"));
		sql.append(Util.getLikeFilter(strLikeFilter)).append(Util.getdateFilter("issue_time", paramMap.get("start_time"),paramMap.get("off_time") ));
		totalSql.append(Util.getLikeFilter(strLikeFilter)).append(Util.getdateFilter("issue_time", paramMap.get("start_time"),paramMap.get("off_time") ));
	       
		long total =Db.find(totalSql.toString()).get(0).getLong("total");

		String property = "issue_time";
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
		
		List<Record> vouchers=Db.find(sql.toString());
		if(vouchers.size()>0){
			vouchers.get(0).set("total", total);
		}
	
		return vouchers;
	}
	@Before(Tx.class)
	public boolean IssueVouchers(Map<String, String[]> param) {
	   boolean flag=false;
		String userids=param.get("userid")[0];
	    String[] userid=userids.split(",");
	    String vouchersid=param.get("voucher_id")[0];
	   
	    String price=param.get("price")[0];
	    String couponsname=param.get("coupons_name")[0];
	    String issue_time=DateUtil.getDate();
	    Record record;
	    
	    for(int i=0;i<userid.length;i++){
	      record= new Record();
	      record.set("user_id", userid[i]).set("vouchers_id", vouchersid).set("get_time", issue_time);
	      Db.save("user_vouchers_info", record);
	    }
	    record=new Record();
	    record.set("coupons_name", couponsname).set("issue_total",userid.length).set("issue_time", issue_time).set("price", price);
	    flag=Db.save("manager_issue_vouchers", record);
	    return flag;
	}

	public List<Voucher> getPriceByVoucherType(String app_type,
			String coupons_type) {
		String sql="select price from vouchers_info where app_type=? and coupons_type=?";
		List<Voucher> priceList=Voucher.dao.find(sql,app_type,coupons_type);
		return priceList;
	}

	public List<Voucher> getCountByPrice(String app_type, String coupons_type,
			String price) {
		String sql="select id,count from vouchers_info where app_type=? and coupons_type=? and price=?";
		List<Voucher> countList=Voucher.dao.find(sql,app_type,coupons_type,price);
		return countList;
	}

	public boolean deleteVouchers(Map<String, String[]> param) {
	String ids=param.get("id")[0];
	String[] id=ids.split(",");
	boolean flag=false;
	Voucher voucher;
	for(int i=0;i<id.length;i++){
		voucher=new Voucher();
		voucher.deleteById(id[i]);
	}
	flag=true;
		return flag;
	}

	public boolean deleteVoucherIssue(Map<String, String[]> param) {
		
		String ids=param.get("id")[0];
		String[] id=ids.split(",");
		boolean flag=false;
		for(int i=0;i<id.length;i++){
			Db.deleteById("manager_issue_vouchers", id[i]);
		}
		flag=true;
		return flag;
	}

	public List<Record> getVouchersByMerId(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
	
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		String merchantsis=paramMap.get("merchantsid")[0];
		sql.append("select DISTINCT mv.vouchers_id,vi.price,vi.service_type_id,vi.app_type,(select st.service_type_name from service_type st where st.id=vi.service_type_id) as service_type_name from merchant_vouchers_permissions mv inner join vouchers_info vi on mv.vouchers_id=vi.id  where mv.is_del=0 and mv.merchant_id=? ");
		long total = Voucher.dao.find(sql.toString(),merchantsis).size();

		sql.append(" limit ");
		sql.append(pageNumber);
		sql.append(",");
		sql.append(pageSize);

		List<Record> vouchers = Db.find(sql.toString(),merchantsis);
		if (vouchers.size() > 0) {
			vouchers.get(0).set("total", total);
		}
		return vouchers;

	}

	public List<Voucher> getVoucherExportList(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		int pageNumber = PAGE.PAGENUMBER;
		int pageSize = PAGE.PAGESIZE_EXPORT;
		sql.append("select v.id,v.service_type_id,v.cutoff_time,v.price,v.icon_path,v.count,(case v.is_del when 0 then '启用' when 1 then '暂停' else '' end) as is_del,st.service_type_name as coupons_type_name ");
		sql.append(" from vouchers_info v inner join service_type st on st.id=v.service_type_id where st.is_del=0 and ");
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("st.service_type_name", paramMap.get("service_type_name"));
		intFilter.put("v.price", paramMap.get("price"));
		intFilter.put("v.is_del", paramMap.get("is_del"));
		intFilter.put("v.count", paramMap.get("count"));
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter)).append(Util.getExportdateFilter("v.cutoff_time", paramMap.get("start_time"),paramMap.get("off_time") ));
		String property = "v.id";
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

		List<Voucher> vouchers = Voucher.dao.find(sql.toString());
		if (vouchers.size() > 0) {
			String pic = "";
			for(Voucher s : vouchers){
				pic = s.getStr("icon_path");
				if(!StringUtil.isNullStr(pic)){
					pic = BusinessUtil.getFileUrl(pic);
					s.set("icon_path", pic);
				}
			}
		}
		return vouchers;
	}

	public boolean startOrstopVouchers(Map<String, String[]> param) {
	String id=param.get("id")[0];
	String is_del=param.get("is_del")[0];
	Voucher voucher=new Voucher();
	boolean flag=false;
	voucher.findById(id).set("is_del", is_del).update();
	flag=true;
	return flag;
	}

	@Override
	public int editVouchers(Map<String, String[]> param, String resultPath) {
		int flag=0;
		String id=param.get("id")[0];
		String service_id = param.get("service_id")[0];
		String app_type = param.get("app_type")[0];
		String price=param.get("price")[0];
		String cutoff_time=param.get("cutoff_time")[0];
		Voucher voucher;
		String selectSql="select id,cutoff_time from vouchers_info where is_del=0  and service_type_id=? and price=?";
		voucher =new Voucher();
		List<Voucher> vou=voucher.find(selectSql,service_id,price);
		if(vou.size()>0&&!id.equals(vou.get(0).getLong("id").toString())){
			Date date=new Date();
			if(vou.get(0).getDate("cutoff_time").getTime()>date.getTime()){
				flag=2;
				return flag;
			};
		}
		
		if(resultPath.equals("")){
			voucher.findById(id).set("service_type_id", service_id).set("app_type", app_type).set("price", price).set("cutoff_time",cutoff_time).update();
		}else{
		    voucher.findById(id).set("service_type_id", service_id).set("app_type", app_type).set("price", price).set("cutoff_time",cutoff_time).set("icon_path", resultPath).update();
		}
		flag=1;
		return flag;
	}

	@Override
	public List<Voucher> getCanIsuseVouchers(Map<String, String[]> paramMap) {
		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		sql.append("select v.id,v.service_type_id,v.cutoff_time,v.price,v.icon_path,v.app_type,v.count,v.is_del,st.service_type_name as coupons_type_name");
		sql.append(" from vouchers_info v inner join service_type st on st.id=v.service_type_id where st.is_del=0 and v.cutoff_time>now() and ");
		totalSql.append("select count(1) as total from vouchers_info v inner join service_type st on st.id=v.service_type_id where st.is_del=0 and v.cutoff_time>now() and ");
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		Map<String, String[]> strLikeFilter = new HashMap<String, String[]>();
		strLikeFilter.put("st.service_type_name", paramMap.get("service_type_name"));
		intFilter.put("v.price", paramMap.get("price"));
		intFilter.put("v.is_del", paramMap.get("is_del"));
		intFilter.put("v.count", paramMap.get("count"));
		sql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter)).append(Util.getdateFilter("v.cutoff_time", paramMap.get("start_time"),paramMap.get("off_time") ));
		totalSql.append(Util.getFilter(strFilter, intFilter)).append(Util.getLikeFilter(strLikeFilter)).append(Util.getdateFilter("v.cutoff_time", paramMap.get("start_time"),paramMap.get("off_time") ));
		
		long total = Voucher.dao.find(totalSql.toString()).get(0).getLong("total");

		String property = "v.id";
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

		List<Voucher> vouchers = Voucher.dao.find(sql.toString());
		if (vouchers.size() > 0) {
			vouchers.get(0).setTotal(total);
			String pic = "";
			for(Voucher s : vouchers){
				pic = s.getStr("icon_path");
				if(!StringUtil.isNullStr(pic)){
					pic = BusinessUtil.getFileUrl(pic);
					s.set("icon_path", pic);
				}
			}
		}
		return vouchers;

	}

}

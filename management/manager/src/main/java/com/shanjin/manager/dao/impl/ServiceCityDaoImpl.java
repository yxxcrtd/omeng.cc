package com.shanjin.manager.dao.impl;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.impl.CommonCacheServiceImpl;
import com.shanjin.manager.constant.Constant.PAGE;
import com.shanjin.manager.constant.Constant.SORT;
import com.shanjin.manager.dao.ServiceCityDao;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

public class ServiceCityDaoImpl implements ServiceCityDao{
	private static ICommonCacheService commonCacheService = new CommonCacheServiceImpl();
	/**
	 * 查询服务城市列表sql
	 * @param param
	 * @param flag true为查列表 false为查总数
	 * @return
	 */
	private String getQuerySql(Map<String, String[]> param,boolean flag){
		StringBuffer sb = new StringBuffer();
		if(flag){
			sb.append(" SELECT * FROM service_city t WHERE 1=1 ");
		}else{
			sb.append(" SELECT count(1) FROM service_city t WHERE 1=1 ");
		}

		if(StringUtil.isNotNullMap(param,"is_open")){
			int isOpen = StringUtil.nullToInteger(param.get("is_open")[0]);
			sb.append(" AND t.is_open=").append(isOpen);
		}

		if(StringUtil.isNotNullMap(param,"province")){
			String province = StringUtil.null2Str(param.get("province")[0]);
			sb.append(" AND t.province='").append(province).append("'");
		}
		
		if(StringUtil.isNotNullMap(param,"city")){
			String city = StringUtil.null2Str(param.get("city")[0]);
			sb.append(" AND t.city='").append(city).append("'");
		}
		if(flag){
			String property = "id";
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
		}
		return sb.toString();
	}
	
	@Override
	public List<Record> getServiceCityList(Map<String, String[]> param) {
		List<Record> list = Db.find(getQuerySql(param,true));
		long total = Db.queryLong(getQuerySql(param,false));
		if(list!=null&&list.size()>0){
			list.get(0).set("total", total);
		}
		return list;
	}

	@Override
	public int saveServiceCity(Map<String, String[]> param) {
		int flag = 0; //0:失败，1：成功，2：已存在
		String id = StringUtil.null2Str(param.get("id")[0]);
		boolean isUpdate = false ; 
		if(StringUtil.isNotNullMap(param, "id")){
			isUpdate = true;
		}
		String province=param.get("province")[0];
		String city = param.get("city")[0];
		boolean isExist = serviceCityIsExist(id,province,city,isUpdate);
		if(isExist){
			flag = 2;
			return flag;
		}
		if(isUpdate){
			// 更新
			Record r = Db.findById("service_city", id);
			r.set("province", province).set("city", city);
			Db.update("service_city", r);
		}else{
			// 新增
			Record r = new Record();
			r.set("province", province).set("city", city);
			Db.save("service_city", r);
		}
		commonCacheService.deleteObject(CacheConstants.SERVICE_CITY + province,city);
		
		flag = 1;
		return flag;
	}
	
	private boolean serviceCityIsExist(String id,String province,String city,boolean isUpdate){
		boolean flag = false;
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT count(1) FROM service_city t WHERE 1=1 ");
		sb.append(" AND t.province='").append(province).append("'");
		sb.append(" AND t.city='").append(city).append("'");
		if(isUpdate){
			sb.append(" AND t.id<>").append(id);
		}
		long num = Db.queryLong(sb.toString());
		if(num>0)
			flag = true;
		return flag;
	}

	@Override
	public void delServiceCity(String ids) {
		String[] id=ids.split(",");
		String sqlProCiy="select province,city from service_city where id=?";
		String sql = "DELETE FROM service_city WHERE id=?";
		for(int i=0;i<id.length;i++){
			List<Record> res=Db.find(sqlProCiy,id[i]);
			if(res!=null&&res.size()>0){
				commonCacheService.deleteObject(CacheConstants.SERVICE_CITY+res.get(0).getStr("province"),res.get(0).getStr("city"));	
			}
			Db.update(sql,id[i]);
		}
	}

	@Override
	public boolean updateOpenStatus(String ids, String status) {
		String sql = "update service_city set is_open=? WHERE id in(?)";
		String[] id=ids.split(",");
		String sqlProCiy="select province,city from service_city where id=?";
		for(int i=0;i<id.length;i++){
			List<Record> res=Db.find(sqlProCiy,id[i]);
			if(res!=null&&res.size()>0){
				commonCacheService.deleteObject(CacheConstants.SERVICE_CITY+res.get(0).getStr("province"),res.get(0).getStr("city"));	
			}
		}
		Db.update(sql,status,ids);
		return true;
	}

}

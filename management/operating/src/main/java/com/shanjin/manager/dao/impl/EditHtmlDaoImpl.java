package com.shanjin.manager.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.impl.CommonCacheServiceImpl;
import com.shanjin.manager.Bean.FensiAddTotal;
import com.shanjin.manager.constant.Constant.PAGE;
import com.shanjin.manager.constant.Constant.SORT;
import com.shanjin.manager.dao.EditHtmlDao;
import com.shanjin.manager.utils.BusinessUtil;
import com.shanjin.manager.utils.DateUtil;
import com.shanjin.manager.utils.StringUtil;
import com.shanjin.manager.utils.Util;

public class EditHtmlDaoImpl implements EditHtmlDao {
	private static ICommonCacheService commonCacheService = new CommonCacheServiceImpl();
	@Override
	public List<Record> getEditHtmlList(Map<String, String[]> paramMap) {

		StringBuffer sql = new StringBuffer();
		StringBuffer totalSql= new StringBuffer();
		int pageNumber = paramMap.get("start")[0] == null ? PAGE.PAGENUMBER
				: Integer.parseInt(paramMap.get("start")[0]);
		int pageSize = paramMap.get("limit")[0] == null ? PAGE.PAGESIZE
				: Integer.parseInt(paramMap.get("limit")[0]);
		Map<String, String[]> strFilter = new HashMap<String, String[]>();
		Map<String, String[]> intFilter = new HashMap<String, String[]>();
		
		sql.append("select cg.*,st.service_type_name as service_name from html_content cg left join service_type st on st.id=cg.business_id where ");
		totalSql.append("select count(1) as total from html_content cg left join service_type st on st.id=cg.business_id where ");
		strFilter.put("cg.title", paramMap.get("title"));
		strFilter.put("st.service_type_name", paramMap.get("service_name"));
		sql.append(Util.getFilter(strFilter, intFilter));
		totalSql.append(Util.getFilter(strFilter, intFilter));
		
		long total = Db.find(totalSql.toString()).get(0).getLong("total");
		
		String property = "cg.create_time";
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
		List<Record> editHtmlList = Db.find(sql
				.toString());
		if (editHtmlList.size() > 0) {
			editHtmlList.get(0).set("total", total);
			String pic="";
			for(int i=0;i<editHtmlList.size();i++){
				pic = editHtmlList.get(i).getStr("image");
				if(!StringUtil.isNullStr(pic)){
					pic = BusinessUtil.getFileUrl(pic);
					editHtmlList.get(i).set("image",pic);
				}
			}
		}
		return editHtmlList;
		
	
	}

	@Override
	public Boolean saveEditHtml(Map<String, String[]> param,String resultPath) {

		String id = StringUtil.null2Str(param.get("id")[0]);
		boolean isUpdate = false ; 
		if(StringUtil.isNotNullMap(param, "id")){
			isUpdate = true;
		}
		if(StringUtil.isNullStr(resultPath)){
			// 未上传图片
			if(StringUtil.isNotNullMap(param, "image")){
				resultPath = param.get("image")[0];
				resultPath = BusinessUtil.getFilePath(resultPath);
			}
		}
		String title=param.get("title")[0];
		String share_title=param.get("share_title")[0];
		String share_abstract=param.get("share_abstract")[0];
		String business_id=param.get("business_id")[0];
		String cont_type=param.get("cont_type")[0];
		
		String sqlRepeat="select id from html_content where business_id=? and cont_type=?";
		List<Record> list=Db.find(sqlRepeat,business_id,cont_type);
		String sql="select content from html_mode where cont_type=?";
		
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
			re=	Db.findById("html_content", id).set("title", title)
					.set("share_title", share_title).set("share_abstract", share_abstract)
					.set("business_id", business_id).set("cont_type", cont_type).set("image", resultPath);
			Db.update("html_content", re);
			
			deleteCache(business_id);
		}else{
			// 新增
			if(list!=null&&list.size()>0){
				return flag;
			}
			List<Record> contents=Db.find(sql,cont_type);
			
			re=new Record();
			re.set("title", title).set("create_time", new Date())
			 .set("share_title", share_title).set("share_abstract", share_abstract)
			 .set("business_id", business_id).set("cont_type", cont_type).set("image", resultPath);
			
			if(contents!=null&&contents.size()>0){
				re.set("content", contents.get(0).getStr("content"));	
			}
			Db.save("html_content", re);
		}
		flag = true;
		return flag;
	
	}

	@Override
	public Boolean deleteEditHtml(Map<String, String[]> param) {
		boolean flag = false;
		String[] ids = param.get("id")[0].split(",");
		for (int i = 0; i < ids.length; i++) {
			int serviceId=Db.findById("html_content", ids[i]).getInt("business_id");
			deleteCache(serviceId);
			Db.deleteById("html_content", ids[i]);
		}
		flag = true;
		
		return flag;
	}

	private void deleteCache(Object serviceId) {
	    
		commonCacheService.deleteObject(CacheConstants.ORDER_BANNER, String.valueOf(serviceId));
		
	}

	@Override
	public Boolean cancelRecordAll(Map<String, String[]> param) {
		boolean flag = false;
		Record re=null;
		String[] ids = param.get("id")[0].split(",");
		for (int i = 0; i < ids.length; i++) {
			 re=Db.findById("html_content", ids[i]);
			 re.set("is_pub", 0);
			 Db.update("html_content",re);
			 deleteCache(re.getInt("business_id"));
		}
		flag = true;
		return flag;
	}

}

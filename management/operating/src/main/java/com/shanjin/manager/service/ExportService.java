package com.shanjin.manager.service;


import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.plugin.activerecord.Model;
import com.shanjin.manager.service.ExcelExportUtil.Pair;


public class ExportService{
	
	public static ExportService service = new ExportService();

	/**
	 * 导出数据
	 * @param response
	 * @param request
	 * @param member
	 */
	public <M extends Model<M>> void export(HttpServletResponse response, HttpServletRequest request, List<M> records,List<Pair> titles,String fileName,int type) {
		
		
		ExcelExportUtil.exportByRecord(response, request, fileName, titles , records,type);
	}
  public  void downLoad( HttpServletResponse response, HttpServletRequest request,String path) throws IOException {
		ExcelExportUtil.download(path, response, request);
	}
  
  public <M extends Model<M>> void exportMonth(HttpServletResponse response, HttpServletRequest request,List<Pair> titles,String fileName,int type, List<M>... records) {
		
		ExcelExportUtil.exportByRecordMonth(response, request, fileName, titles ,type, records);
	}
}

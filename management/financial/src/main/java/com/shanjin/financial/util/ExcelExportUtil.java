
package com.shanjin.financial.util;

import com.jfinal.plugin.activerecord.Model;
import com.shanjin.financial.constant.Constants;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

public class ExcelExportUtil
 {

	/**
	 * @param response
	 * @param request
	 * @param filename	导出的文件名
	 * @param titles 标题列和列名的对应.column:列名,title标题名
	 * @param records 记录
	 */
	@SuppressWarnings("unchecked")
	public static <M extends Model<M>> void exportByRecord(HttpServletResponse response,HttpServletRequest request,String filename,List<Pair> titles, List<M> records,int type){
		exportByRecord(response, request, filename,type, new SheetData<M>(titles, records));
	}

	/**
	 * @param response
	 * @param request
	 * @param filename	导出的文件名
	 * @param sheetDatas 产生一个sheet需要的数据
	 */
	public static <M extends Model<M>> void exportByRecord(HttpServletResponse response,HttpServletRequest request,String filename,int type,SheetData<M>... sheetDatas){

		XSSFWorkbook  wb = new XSSFWorkbook ();								

		//标题行的style
		CellStyle titleCellStyle = wb.createCellStyle();					
		titleCellStyle.setAlignment(CellStyle.ALIGN_CENTER);				//居中
		titleCellStyle.setWrapText(true);									//自动换行	
		Font font = wb.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);						//加粗
		font.setFontName("微软雅黑");
		titleCellStyle.setFont(font);

		//内容行的style
		CellStyle cellStyle = wb.createCellStyle();					
		cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);	//垂直居中
		cellStyle.setWrapText(true);	
		Font font2 = wb.createFont();
		font2.setFontName("微软雅黑");
		cellStyle.setFont(font2);	
		
		//多张sheet
		for (SheetData<M> sheetData : sheetDatas) {
			List<Pair> titles = sheetData.titles;
			
			List<M> records = sheetData.records;
			
			XSSFSheet sheet = wb.createSheet();
			
			int rowIndex = 0,cellIndex=0;
			
			XSSFRow row = null;
			XSSFCell cell = null;

			//创建标题行
			row = sheet.createRow(rowIndex);
			row.setHeight((short)850);
			rowIndex++;
			
			for (Pair pair : titles) {
				
				cell = row.createCell(cellIndex);
				cell.setCellStyle(titleCellStyle);				//设置样式
				cellIndex++;
				
				cell.setCellValue(pair.title);
			}
			
			//处理每一行
			for (M record : records) {

				row = sheet.createRow(rowIndex);
				row.setHeight((short)850);
				rowIndex++;
				cellIndex = 0;

				
				for (Pair pair : titles) {
					
					cell = row.createCell(cellIndex);
					cell.setCellStyle(cellStyle);				//设置样式
					cellIndex++;
					
					Object value = record.get(pair.column);
					
					if(value!=null){
							
						cell.setCellValue(value.toString());
					}
				}
			}
		}
		
		//序号
		writeStream(filename, wb, response,request,type);
	}

	/**
	 * 写到输出流
	 */
	private static void writeStream(String filename, XSSFWorkbook wb, HttpServletResponse response, HttpServletRequest request,int type)
	{

		try
		{
			filename += ".xlsx";

			filename.replaceAll("/", "-");
			
			if(request==null){
				filename = new String(filename.getBytes("utf-8"));
			}else{
			
				String agent = request.getHeader("USER-AGENT");

			if (agent.toLowerCase().indexOf("firefox")>0)
			{
				filename = new String(filename.getBytes("utf-8"), "iso-8859-1");
			}else{
				filename = URLEncoder.encode(filename, "UTF-8");
			}
			}
            if(Constants.EXPOTR_TYPE == type){
            	File file=new File("excel");
            	if(!file.exists()){
            		file.mkdir();
            	}
            	FileOutputStream fout = new FileOutputStream("excel/"+filename);
            	wb.write(fout);
            	File ftpfile=new File("excel/"+filename);
            	//BusinessUtil.fileUpload(ftpfile);
            	fout.flush();
            	fout.close();
            	ftpfile.delete();
            	file.delete();
            }else{
			response.reset();
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Content-Disposition", "attachment; filename=" + filename);
			response.setContentType("application/octet-stream;charset=UTF-8");
			OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
			wb.write(outputStream);
			outputStream.flush();
			outputStream.close();
            }
			
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}
	
	/**
	 * 标题列和列名的对应
	 */
	public static class Pair {
		public String column;
		
		public String title;
		
		public Pair(String column,String title){
			this.column = column;
			
			this.title = title;
			
		}
	}
	
	/**
	 * 创建一个sheet需要的数据
	 */
	public static class SheetData<M extends Model<M>>{
		public List<Pair> titles;
		public List<M> records;
		
		public SheetData(List<Pair> titles, List<M> records) {
			this.titles = titles;
			
			this.records = records;
		}
	}
	
	
	public static void download(String path,HttpServletResponse response, HttpServletRequest request) throws IOException{
		
		BufferedOutputStream out = null;
		String realPath="http://192.168.1.48:81"+path;
		String filename = path.substring(path.lastIndexOf("/")+1);
		filename = new String (filename.getBytes("utf-8"),"iso8859-1");
		response.setContentType("APPLICATION/DOWNLOAD");
		response.setHeader("Content-Disposition","attachment; filename="+ filename);
		out = new BufferedOutputStream(response.getOutputStream());
		byte[] buffer = new byte[8 * 1024];
		 URL u;
		 URLConnection connection = null;
		 try {
		 u = new URL(realPath);
		 connection = u.openConnection();
		 }catch (Exception e) {
		 System.out.println("ERR:" + realPath);
		 }
		 connection.setReadTimeout(100000);
		 InputStream is = null;
		 try {
		 is = connection.getInputStream();
		 ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
		 int len = 0;
		 while ((len = is.read(buffer)) != -1) {
		 bytestream.write(buffer, 0, len);
		 }
		 bytestream.writeTo(out);
		 out.flush();
		 }catch (Exception e) {
		 e.printStackTrace();
		 }finally {
		 if (is != null) {
		 try {
		 is.close();
		 
		 }catch (IOException e) {
		 }
		 }
		 if(out!=null){
		 out.close();
		 }
		 }
		}
}

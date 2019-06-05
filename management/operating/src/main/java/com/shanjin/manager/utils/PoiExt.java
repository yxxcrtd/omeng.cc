package com.shanjin.manager.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.jfinal.plugin.activerecord.Db;

public class PoiExt {
	public static Map<String, Object> ReadExcel(String filename, String sql,int ColCount) {
		Map<String, Object> map = null;
		try {
			@SuppressWarnings("resource")
			HSSFWorkbook wookbook = new HSSFWorkbook(new FileInputStream(filename));
			HSSFSheet sheet = wookbook.getSheet("Sheet1");
			int rows = sheet.getPhysicalNumberOfRows();
			Object[][] paras = new Object[rows][ColCount];
			for (int i = 1; i < rows; i++) {
				HSSFRow row = sheet.getRow(i);
				if (row != null) {
					int cells = row.getPhysicalNumberOfCells();
					for (int j = 0; j < cells; j++) {
						HSSFCell cell = row.getCell(j);
						if (cell != null) {
							switch (cell.getCellType()){
							case HSSFCell.CELL_TYPE_FORMULA:
								break;
							case HSSFCell.CELL_TYPE_NUMERIC:
								paras[i][j] = cell.getNumericCellValue();
								break;
							case HSSFCell.CELL_TYPE_STRING:
								paras[i][j] = cell.getStringCellValue();
								break;
							default:
								paras[i][j] = null;
								break;
							}
						}
					}
				}
			}
			int[] ret = Db.batch(sql, paras, 100);
			int s = 0, l = 0;
			for (int i = 0; i < ret.length; i++)
				if (ret[i] == 1)
					s++;
				else
					l++;
			map = new HashMap<String, Object>();
			map.put("success", s);
			map.put("lost", l);
			map.put("count", ret.length);
			wookbook = null;
		} catch (IOException e){
			e.printStackTrace();
		}
		return map;
	}
}

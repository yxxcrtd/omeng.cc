package com.shanjin.financial.util;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import com.shanjin.financial.constant.Constants;

/**
 * 搜索文件工具类
 * @author Huang yulai
 *
 */
public class SearchFileUtil {

	/**
	 * 某文件目录下搜索账单文件
	 * @param fileDir 文件目录
	 * @param dateStr 日期
	 * @param billKey 账单文件关键字
	 * @return 账单文件所在的详细目录
	 */
	public static String getBillFilePah(String fileDir, String dateStr,
			String billKey) {
		String path = "";
		File dir = new File(fileDir); // fileDir是文件夹路经
		File[] listOfFiles = dir.listFiles();
		if (listOfFiles != null && listOfFiles.length > 0) {
			// 有文件
			String key = "";
			if (StringUtil.isNullStr(dateStr)) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.DATE, -1); // 默认昨日账单
				Date yesterday = calendar.getTime();
				dateStr = DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN,
						yesterday);
			}
			key = dateStr + billKey;

			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					if (listOfFiles[i].getName().contains(key)) {
						path = fileDir + "/" + listOfFiles[i].getName();
						break;
					}
				}
			}
		}
		return path;
	}
	
	public static String getAliDayBillFilePath(String dateStr){
		return getBillFilePah(Constants.ali_bill_file_path,dateStr,Constants.ali_day_bill_name);
	}
	
	public static String getAliDetailBillFilePath(String dateStr){
		return getBillFilePah(Constants.ali_bill_file_path,dateStr,Constants.ali_detail_bill_name);
	}

	public static void main(String[] args) {
		System.out.println("Find it! 支付宝日账单汇总："
				+ getBillFilePah(Constants.ali_bill_file_path, "20160705",
						Constants.ali_day_bill_name));
		System.out.println("Find it! 支付宝日账单详细："
				+ getBillFilePah(Constants.ali_bill_file_path, "20160705",
						Constants.ali_detail_bill_name));
	}
}

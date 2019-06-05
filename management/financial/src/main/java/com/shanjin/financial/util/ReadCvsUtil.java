package com.shanjin.financial.util;

import java.io.*;


import com.csvreader.CsvReader;
import com.shanjin.financial.constant.Constants;

/**
 * 读取cvs文件工具类（用于支付宝和微信账单读取）
 * @author Huang yulai
 *
 */
public class ReadCvsUtil {

	/**
	 * 导入支付宝日账单汇总到数据库
	 * @param dateStr
	 */
	public static void importAliDayBillToDB(String dateStr){
		String filePath = SearchFileUtil.getAliDayBillFilePath(dateStr);
		System.out.println("支付宝日账单汇总表文件名 :"+filePath);
		if(!StringUtil.isNullStr(filePath)){
			// 文件找到
			int aliNum = 0; // 订单总笔数
			float aliTotal = 0.00f;
			// =========================获取支付宝订单笔数和总额start=========================
			try {
				String[] stringList;
				File file = new File(filePath);
				String charset = "GBK";
				if (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
					charset = "GBK";
				} else if (System.getProperty("os.name").toLowerCase()
						.indexOf("linux") >= 0) {
					charset = "UTF-8";
				}
				
				InputStreamReader isr = new InputStreamReader(new FileInputStream(
						file), charset);
				CsvReader reader = new CsvReader(isr); // 默认是逗号分隔符，UTF-8编码

				/*
				 * readRecord()判断是否还有记录，getValues()读取当前记录，然后指针下移
				 */
				reader.readRecord();
				int row = 0; // 行数计数 
				while (reader.readRecord()) {
					if(row==5){
						stringList = reader.getValues();
						if(stringList.length>6){
							aliNum = StringUtil.nullToInteger(stringList[2]);
							aliTotal = StringUtil.nullToFloat(stringList[4]);
							System.out.println("交易订单总笔数:"+aliNum);	
							System.out.println("合计订单金额（元）:"+aliTotal);	
						}
					}
					row ++;
				}
				reader.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			// =========================获取支付宝订单笔数和总额end=========================
			// =========================支付宝订单笔数和总额入库操作start=========================
			//TODO:sth
			// =========================支付宝订单笔数和总额入库操作end=========================
		}
	}
	
	/**
	 * 导入支付宝日账单详情到数据库
	 * @param dateStr
	 */
	public static void importAliDetailBillToDB(String dateStr){
		String filePath = SearchFileUtil.getAliDetailBillFilePath(dateStr);
		System.out.println("支付宝日账单详细表文件名 :"+filePath);
		if(!StringUtil.isNullStr(filePath)){
			// 文件找到
			int aliNum = 0; // 订单总笔数
			float aliTotal = 0.00f;
			// =========================获取支付宝订单笔数和总额start=========================
			try {
				String[] stringList;
				File file = new File(filePath);
				String charset = "GBK";
				if (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
					charset = "GBK";
				} else if (System.getProperty("os.name").toLowerCase()
						.indexOf("linux") >= 0) {
					charset = "UTF-8";
				}
				
				InputStreamReader isr = new InputStreamReader(new FileInputStream(
						file), charset);
				CsvReader reader = new CsvReader(isr); // 默认是逗号分隔符，UTF-8编码

				/*
				 * readRecord()判断是否还有记录，getValues()读取当前记录，然后指针下移
				 */
				reader.readRecord();
				int row = 0; // 行数计数 
				while (reader.readRecord()) {
					if(row==5){
						stringList = reader.getValues();
						if(stringList.length>6){
							aliNum = StringUtil.nullToInteger(stringList[2]);
							aliTotal = StringUtil.nullToFloat(stringList[4]);
							System.out.println("交易订单总笔数:"+aliNum);	
							System.out.println("合计订单金额（元）:"+aliTotal);	
						}
					}
					row ++;
				}
				reader.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			// =========================获取支付宝订单笔数和总额end=========================
			// =========================支付宝订单笔数和总额入库操作start=========================
			//TODO:sth
			// =========================支付宝订单笔数和总额入库操作end=========================
		}
	}
	
	
	public static void Csv() {
		try {
			String[] stringList;
			String sourceFilePath = "C:\\Users\\Administrator\\Documents\\20889113860800040156_20160705_业务明细.csv"; // 源文件
			File file = new File(sourceFilePath);
			String charset = "GBK";
			if (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
				charset = "GBK";
			} else if (System.getProperty("os.name").toLowerCase()
					.indexOf("linux") >= 0) {
				charset = "UTF-8";
			}
			
			InputStreamReader isr = new InputStreamReader(new FileInputStream(
					file), charset);
			// String destFilePath= "D:\\111\\前海自身ok.csv"; //目标文件
			CsvReader reader = new CsvReader(isr); // 默认是逗号分隔符，UTF-8编码
			// CsvWriter writer =new
			// CsvWriter(destFilePath,',',Charset.forName("GBK"));

			/*
			 * readRecord()判断是否还有记录，getValues()读取当前记录，然后指针下移
			 */
			reader.readRecord();
			// writer.writeRecord(reader.getValues()); //读取表头

			while (reader.readRecord()) {
				stringList = reader.getValues();
				String out = "";
				for (String s : stringList) {
					out = out + " " + s + " ";
				}
				System.out.println(out);
				// writer.writeRecord(stringList );
			}
			reader.close();
			// writer.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		importAliDayBillToDB("20160705");
		importAliDetailBillToDB("20160705");
	}
}

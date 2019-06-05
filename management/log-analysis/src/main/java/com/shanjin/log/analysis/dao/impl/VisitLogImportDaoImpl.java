package com.shanjin.log.analysis.dao.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.shanjin.common.util.DateUtil;
import com.shanjin.log.analysis.Constant;
import com.shanjin.log.analysis.dao.VisitLogImportDao;
import com.shanjin.log.analysis.util.DbUtil;

public class VisitLogImportDaoImpl implements VisitLogImportDao{
	private final Log log = LogFactory.getLog(this.getClass());
	private RandomAccessFile ranFile;
	@Override
	public void importVisitLog(Date logDate) {
		// 取得文件列表
		List<File> files = getLogFiles(logDate);
		// 执行文件导入
		doImportFiles(files);
	}
	
	/**
	 * 导入访问log文件到DB
	 * @param files
	 *            要导入的文件列表
	 */
	protected void doImportFiles(List<File> files) {
		log.info("Do import files begin at : "
				+ DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN, new Date()));
		// 循环取出文件，并且取出最多1000行记录
		if (files != null && !files.isEmpty()) {
			// 文件行号，第1行为1
			int rows = 0;
			// 每次保存的数据条数
			int dataNum = 0;
			// 文件名
			//String fileName;
			// 保存行数据
			String lineData;

			for (File f : files) {
				log.info("Do import files : ["
						+ f.getAbsolutePath()
						+ "] at  "
						+ DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN,
								new Date()));
				// 行号清0，第一行行号为1
				rows = 0;
				//fileName = f.getName();
				try {
					ranFile = new RandomAccessFile(f, "r");

					List<String> dataList = new ArrayList<String>();
					while ((lineData = ranFile.readLine()) != null) {
						// 读取一行数据
						rows++;
						lineData = new String(lineData.getBytes("ISO8859-1"),Constant.ENCODING);
						dataList.add(lineData);
						dataNum = dataList.size();
						if(dataNum==Constant.BATCH_NUM){
							// 读取日志文件行数达到批量提交数，提交数据库插入
							DbUtil.batchInsertVisitLog(dataList,rows,dataNum,f.getAbsolutePath());
							dataList = new ArrayList<String>();
						}
					}
					// *************日志文件最后一次提交未达到批量提交数量，提交数据库插入start*************
					dataNum = dataList.size();
					if(dataNum>0){
						// 读取日志文件行数达到批量提交数，提交数据库插入
						DbUtil.batchInsertVisitLog(dataList,rows-1,dataNum,f.getAbsolutePath());
						dataList = new ArrayList<String>();
					}
					// *************日志文件最后一次提交未达到批量提交数量，提交数据库插入end*************

				} catch (FileNotFoundException e1) {
					log.error("file done not exists :　" + f.getAbsolutePath(),
							e1);
				} catch (IOException e2) {
					log.error("IOException :　" + f.getAbsolutePath(), e2);
				}
			}

		} else {
			log.warn("No file to import !");
		}

		log.info("Do import files end at : "
				+ DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN, new Date()));
	}

	
	
	protected boolean isAcceptFile(String fileName, Date logDate) {
		String logDateStr = DateUtil.formatDate(DateUtil.DATE_YYYYMMDD_PATTERN, logDate);
		// 名称的后八位（最后一个"."后面的）为年月日并且为logDate日期,如: "CLT_ACCESS_LOG.txt.20121206"
		return fileName.endsWith(logDateStr);
	}
	
	/**
	 * 得到要处理的log文件列表
	 * 
	 * @param logDate
	 * @return
	 */
	private List<File> getLogFiles(Date logDate) {
		String[] dirs = Constant.VISIT_LOG_DIRS;
		if (dirs == null || dirs.length == 0) {
			return null;
		}

		final Date fLogDate = logDate;

		List<File> files = new ArrayList<File>();
		for (String dir : dirs) {
			File dirFile = new File(dir);
			File[] fileArray = dirFile.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return isAcceptFile(name, fLogDate);
				}
			});

			if (fileArray != null && fileArray.length > 0) {
				Collections.addAll(files, fileArray);
			}
		}

		return files;
	}

}

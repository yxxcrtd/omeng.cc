package com.shanjin.manager.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;

import javax.servlet.ServletContext;

import java.io.*;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class FileUtil {

	public static final String CONT = "publish/cont";
	public static final String IMAGE = "publish/image";
	public static final String SEPARATOR = "/";
	public static final int PUB_DIRNUM_MAX = 1000; // 发布文件夹数量最大值
	
	public static String idToPath(Long cont_Id) {
		int k = PUB_DIRNUM_MAX;// 1000
		return cont_Id / k / k % k + SEPARATOR + cont_Id / k % k;
	}

	public static String idToName(Long cont_Id) {
		int k = PUB_DIRNUM_MAX;// 1000
		return cont_Id / k / k % k + SEPARATOR + cont_Id / k % k
				+ SEPARATOR + cont_Id % k;
	}

	/**
	 * 根据内容ID，转换成图片存放路径，如6100000226L，转换后为'610/0/226'
	 * 
	 * @param cont_Id
	 * @return 路径
	 */
	public static String idToImagePath(Long cont_Id) {
		return cont_Id / 10000000 + SEPARATOR + cont_Id / 10000
				% PUB_DIRNUM_MAX + SEPARATOR + cont_Id % 10000;
	}

	/**
	 * 根据内容ID，转换成图片存放路径，如6100000226L，转换后为'610/0/226'
	 * 
	 * @param cont_Id
	 * @return 路径
	 */
	public static String idToFullImagePath(Long cont_Id, String fileName) {
		return cont_Id / 10000000 + SEPARATOR + cont_Id / 10000
				% PUB_DIRNUM_MAX + SEPARATOR + cont_Id % 10000
				+ SEPARATOR + fileName + SEPARATOR;
	}

	public static String idToShortName(Long id) {
		int k = PUB_DIRNUM_MAX;// 1000
		return id % k + "";
	}

	public static String idToFullPathImage(Long id, String type) {
		return IMAGE + SEPARATOR + idToName(id) + "." + type;
	}

	public static String idToFullPathContent(Long id, String type) {
		return CONT + SEPARATOR + idToName(id) + "." + type;
	}
	
	public static String uploadFile(File file, String root, String originalName) throws Exception {
		StringBuffer fileName = new StringBuffer(String.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS%1$tL" + originalName.substring(originalName.lastIndexOf(".")).toLowerCase(), new Date()));
		String dest = new StringBuffer(root).append(fileName).toString();
		File destFile = new File(dest);
		if (!destFile.getParentFile().exists()) {
			if (!destFile.getParentFile().mkdirs()) {
				return "";
			}
		}
		file.renameTo(destFile);
		return fileName.toString();
	}


	@SuppressWarnings("deprecation")
	public static void generateHTML(String folderName, String ftl, String htmlName, Map<String, Object> map, ServletContext servletContext, String path) throws Exception {
		Configuration cfg = new Configuration();
		cfg.setServletContextForTemplateLoading(servletContext, File.separator + folderName);
		cfg.setEncoding(Locale.getDefault(), "UTF-8");
		Template template = cfg.getTemplate(ftl);
		template.setEncoding("UTF-8");
		File pathFile = new File(path);
		if (!pathFile.exists()) {
			pathFile.mkdir();
		}
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path + htmlName));
		File htmlFile = new File(path + htmlName);
		Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(htmlFile), "UTF-8"));
		template.process(map, out);
		bufferedWriter.close();
		out.flush();
		out.close();
	}
	

	
	

}

package com.shanjin.manager.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

import com.jfinal.upload.UploadFile;
import com.shanjin.manager.constant.Constant;
import com.shanjin.outServices.aliOss.AliCdnUtil;
import com.shanjin.outServices.aliOss.AliOssUtil;

public class BusinessUtil {
	public static final String MAN_FILE = "manFile"; // 运营管理文件根目录
	public static final String DIR_IMAGE = "image";
	public static final String PATH_SEPARATOR = "/";
	public static final String DIR_STATISTIC = "statistic";

	//****************后台上传图片类型定义start*****************
	public final static String DIR_BANNER = "banner";// banner广告图目录
	public final static String DIR_ACTIVITY = "activity";// banner广告图目录
	public final static String DIR_ACTIVITY_DETAIL = "activityDetail";// banner广告图目录
	public final static String DIR_RECOMMEND = "recommend";// 热门推荐目录
	public final static String DIR_CATALOG = "catalog";// 分类图标
	public final static String DIR_ORDER_CATALOG = "ordercatalog";// 表单分类图标
	public final static String ACTIVITY = "activity";// 活动相关图片
	public final static String DIR_SEARCH_STATISTIC = "searchApp";// 搜索页行业图标
	public final static String DIR_SEARCH_WORDS = "searchWords";// searchWords搜索关键词目录
	public final static String DIR_LOADING = "loading";// 启动页目录
	public static final String PATH_DEFINED = "defined";
	public final static String DIR_ICO = "ico"; // ico
	public final static String DIR_SERVICE_ITEM = "serviceItem"; // 服务项目配图目录
	//****************后台上传图片类型定义end*****************
	public final static String DIR_VOUCHER = "/resource/voucher/";// banner广告图目录
	
	public final static String DIR_OREDER = "/resource/order/";// 订单banner图路径
	
	public final static String DIR_DICT = "/resource/dict/";// 字典表附件图路径
	
	public final static String NEW_DIR_VOUCHER = "voucher";// banner广告图目录
	
	public final static String DEFINED = "detail";// 自定义表单banner图
	
	public final static String DIR_PACKAGE = "package";// 安装包目录
	
	public final static String DIR_MERCHANT = "merchant";// 商户目录
	
	public final static String ORDER = "order";// 商户目录
	
	public final static String DIR_USER = "user";// 用户目录
	
	
	/**
	 * 后台上传的主文件目录 （/manFile/）
	 * @return
	 */
	public static String manFolder(){
		return PATH_SEPARATOR + MAN_FILE + PATH_SEPARATOR;
	}
	
	/**
	 * 后台上传的包目录（/manFile/package/……）
	 * @return
	 */
	public static String packageFolder(String packType){
		String path = manFolder() + DIR_PACKAGE + PATH_SEPARATOR;
		if(!StringUtil.isNullStr(packType)){
			if(packType.equals(Constant.PACKAGE_TYPE_MERCHANT)){
				path = path + DIR_MERCHANT + PATH_SEPARATOR;
			}else{
				path = path + DIR_USER + PATH_SEPARATOR;
			}
		}
		return path;
	}
	
	/**
	 * 后台上传的图片目录（/manFile/image/……）
	 * @return
	 */
	public static String imageFolder(String imgType){
		String path = manFolder() + DIR_IMAGE + PATH_SEPARATOR;
		if(!StringUtil.isNullStr(imgType)){
			path = path + imgType + PATH_SEPARATOR;
		}
		return path;
	}
	/**
	 * 自定义表单（/manFile/image/……）
	 * @return
	 */
	public static String definedFolder(){
		String path = DIR_OREDER;
		path=path+PATH_DEFINED+PATH_SEPARATOR;
		
		return path;
	}
	public static String VoucherPath(){
		return imageFolder(NEW_DIR_VOUCHER);
	}
    
	public static String DefinedPath(){
		return definedFolder() + DEFINED + PATH_SEPARATOR;
	}
	
	public static String DictPath(String dict_type){
		if(!StringUtil.isNullStr(dict_type)){
			return   DIR_DICT + dict_type + PATH_SEPARATOR;
		   }else{
		    return   DIR_DICT;
		}
	}
	public static String replaceSeparator(String path) {
		return (path == null) ? null : path.replaceAll("\\\\", "/").replaceAll(
				"//", "/");
	}
	
	public static void fileUpload(File file,String filePath,String fileName) {
		// 文件在磁盘上的真实路径
		String realPath = file.getPath();
		// 判断文件夹是否存在,如果不存在则创建文件夹
		File file1 = new File(realPath);
		if (!file1.exists()) {
			file1.mkdir();
		}
		
		if("true".equals(Constant.FTP_MODE_OSS)){
			// 阿里OSS
		    InputStream in = null;
			try {
				in = new FileInputStream(file);
				if (in==null){
					System.out.println("文件未找到");
				}
				AliOssUtil.upload(filePath, fileName, in);
				deleteFile(file1);
//				try {
//					AliCdnUtil.refresh(path,false);
//				} catch (ApiException e) {
//					e.printStackTrace();
//				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}   
		}else{
			// 直接上传至服务器
			try {
				if (FTPUtil.uploadFile(filePath, file, fileName)) {
					deleteFile(file1);
				} else {
					if (FTPUtil.uploadFile(filePath, file, fileName)) {
						deleteFile(file1);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				deleteFile(file);
				filePath = Constant.EMPTY;
			}
		}
	}

	/**
     * 定时任务，每天上传商户运营信息到服务器中，供代理商下载查看
     * @param File 文件
     * @return
     */
	public static void fileUpload(File file) {
		// 相对路径
		String filePath = manFolder()+DIR_STATISTIC+PATH_SEPARATOR+DIR_MERCHANT+PATH_SEPARATOR;
		// 上传文件的文件全名（包含文件后缀名）
		String fileName = file.getName();  // 上传文件原始文件名
		if(fileName.startsWith("订单")){
			 filePath= manFolder()+DIR_STATISTIC+PATH_SEPARATOR+ORDER+PATH_SEPARATOR;
		}
		// 文件在磁盘上的真实路径
		String realPath = file.getPath();
		// 判断文件夹是否存在,如果不存在则创建文件夹
		File file1 = new File(realPath);
		if (!file1.exists()) {
			file1.mkdir();
		}
		
		if("true".equals(Constant.FTP_MODE_OSS)){
			// 阿里OSS
		    InputStream in = null;
			try {
				in = new FileInputStream(file);
				if (in==null){
					System.out.println("文件未找到");
				}
				AliOssUtil.upload(filePath, fileName, in);
				deleteFile(file1);
//				try {
//					AliCdnUtil.refresh(path,false);
//				} catch (ApiException e) {
//					e.printStackTrace();
//				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}   
		}else{
			// 直接上传至服务器
			try {
				if (FTPUtil.uploadFile(filePath, file, fileName)) {
					deleteFile(file1);
				} else {
					if (FTPUtil.uploadFile(filePath, file, fileName)) {
						deleteFile(file1);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				deleteFile(file);
				filePath = Constant.EMPTY;
			}
		}
	}
	/**
     * 文件上传工具类
     * @param uploadFile 文件
     * @param filePath 文件上传路径
     * @return
     */
	public static String fileUpload(UploadFile uploadFile, String filePath) {
		// 相对路径
		String path = null;
		// 上传文件的文件全名（包含文件后缀名）
		String originalName = uploadFile.getFileName();  // 上传文件原始文件名
		String fileName = uploadFile.getFileName();                          // 上传服务器的文件名
		String suffix = fileName.substring(fileName.lastIndexOf("."));
		Date now = new Date();
		fileName = now.getTime() + suffix;
		path = new StringBuffer(filePath).append(fileName).toString();
		// 文件在磁盘上的真实路径
		String realPath = uploadFile.getSaveDirectory();
		// 判断文件夹是否存在,如果不存在则创建文件夹
		File file = new File(realPath);
		if (!file.exists()) {
			file.mkdir();
		}
		File realfile = uploadFile.getFile();
		
		if("true".equals(Constant.FTP_MODE_OSS)){
			// 阿里OSS
		    InputStream in = null;
			try {
				in = new FileInputStream(realfile);
				if (in==null){
					System.out.println("文件未找到");
					return path;
				}
				AliOssUtil.upload(filePath, fileName, in);
				deleteFile(realfile);
//				try {
//					AliCdnUtil.refresh(path,false);
//				} catch (ApiException e) {
//					e.printStackTrace();
//				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}   
		}else{
			// 直接上传至服务器
			try {
				if (FTPUtil.uploadFile(filePath, realfile, fileName)) {
					deleteFile(realfile);
				} else {
					if (FTPUtil.uploadFile(filePath, realfile, fileName)) {
						deleteFile(realfile);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				deleteFile(realfile);
				path = Constant.EMPTY;
			}
		}
		return path;
	}
    
	/**
     * 文件上传工具类
     * @param uploadFile 文件
     * @param filePath 文件上传路径
     * @param fileName 文件生成名
     * @return
     */
    public static String fileUpload(UploadFile uploadFile, String filePath,String fileName) {
        // 相对路径
        String path = null;
        String originalName = uploadFile.getFileName();
        String suffix = originalName.substring(originalName.lastIndexOf("."));
        fileName = fileName + suffix;
        path = new StringBuffer(filePath).append(fileName).toString();
        // 文件在磁盘上的真实路径
        String realPath = uploadFile.getSaveDirectory();
        // 判断文件夹是否存在,如果不存在则创建文件夹
        File file = new File(realPath);
        if (!file.exists()) {
            file.mkdir();
        }
        File realfile=uploadFile.getFile();
      
		if("true".equals(Constant.FTP_MODE_OSS)){
			// 阿里OSS
		    InputStream in = null;
			try {
				in = new FileInputStream(realfile);
				if (in==null){
					System.out.println("文件未找到");
					return path;
				}
				AliOssUtil.upload(filePath, fileName, in);
				deleteFile(realfile);
//				try {
//					AliCdnUtil.refresh(path,false);
//				} catch (ApiException e) {
//					e.printStackTrace();
//				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}  
		}else{
			// 直接上传至服务器
			try {
				if (FTPUtil.uploadFile(filePath, realfile, fileName)) {
					deleteFile(realfile);
				} else {
					if (FTPUtil.uploadFile(filePath, realfile, fileName)) {
						deleteFile(realfile);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				deleteFile(realfile);
				path = Constant.EMPTY;
			}
		}
        return path;
    }
    
    /**
     * 根据数据库中记录的相对路径获取文件的url
     * @param path 相对路径
     * @return
     */
    public static String getFileUrl(String path){
    	AliOssUtil.setModel(Constant.DEVMODE);
    	String url = "";
    	if(!StringUtil.isNullStr(path)){
        	if("true".equals(Constant.FTP_MODE_OSS)){
        		url = AliOssUtil.getViewUrl(path);
        	}else{
        		url = Constant.HTTP.port + path;
        	}
    	}
    	return url;
    }
   
    
    /**
     * 根据文件的url获取相对路径
     * @param url 文件地址
     * @return
     */
    public static String getFilePath(String url){
    	String path = url;
    	if(!StringUtil.isNullStr(url)){
    		if(url.contains("http://")){
    			url = url.substring(7);
    			int i = url.indexOf("/");
    			if(i!=-1){
    				path = url.substring(i);
    			}
    		}
    			
    	}
    	System.out.print("path="+path);
    	return path;
    }
    
    public static void main(String[] args){
    	String url = "http://omg.oomeng.cn/manFile/image/banner/1447056622838.jpg";
    	getFilePath(url);
    }
    
    
    

    /** 文件上传过程中出错或文件路径保存到数据库失败的场合,将已上传的文件删除 */
    public static void deleteFile(String realPath) {
        deleteFile(new File(realPath));
    }

    /** 文件上传过程中出错或文件路径保存到数据库失败的场合,将已上传的文件删除 */
    public static void deleteFile(File delFile) {
        if (delFile.isFile() && delFile.exists()) {
        delFile.delete();
        }
    }
    
    
    /**
     * 查找请求的url是否在资源权限resources里面
     * @param url
     * @param resources
     * @return
     */
    public static boolean havePerm(String url,String resources){
    	boolean haveperm = false ;
		if(resources!=null&&resources!=""){
			String[] perms=resources.split(",");
			if(perms!=null&&perms.length>0){
				for(String per:perms){
					if(!per.equals("")&&per.equals(url)){
						haveperm = true;
						break;
					}
				}
			}
		}
    	return haveperm ;
    }


}

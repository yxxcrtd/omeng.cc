package com.shanjin.manager.utils;
//
//import java.io.File;
//import java.io.InputStream;
//import java.util.Date;
//
//
//
//
//
//import com.jfinal.upload.UploadFile;
//import com.shanjin.manager.constant.Constant;
//import com.shanjin.outServices.aliOss.AliOssUtil;
//
public class ImageUtil {
//	
//	public static final String MAN_FILE = "manFile"; // 运营管理文件根目录
//	public static final String DIR_IMAGE = "image";
//	public static final String PATH_SEPARATOR = "/";
//
//
//	//****************后台上传图片类型定义start*****************
//	public final static String DIR_BANNER = "banner";// banner广告图目录
//	public final static String DIR_SEARCH_WORDS = "searchWords";// searchWords搜索关键词目录
//	public final static String DIR_LOADING = "loading";// 启动页目录
//	
//	public final static String DIR_ICO = "ico"; // ico
//	public final static String DIR_SERVICE_ITEM = "serviceItem"; // 服务项目配图目录
//	//****************后台上传图片类型定义end*****************
//	public final static String DIR_VOUCHER = "/resource/voucher/";// banner广告图目录
//	
//	public final static String NEW_DIR_VOUCHER = "voucher";// banner广告图目录
//	
//	public final static String DIR_PACKAGE = "package";// 安装包目录
//	
//	public final static String DIR_MERCHANT = "merchant";// 商户目录
//	
//	public final static String DIR_USER = "user";// 用户目录
//	
//	
//	/**
//	 * 后台上传的主文件目录 （/manFile/）
//	 * @return
//	 */
//	public static String manFolder(){
//		return PATH_SEPARATOR + MAN_FILE + PATH_SEPARATOR;
//	}
//	
//	/**
//	 * 后台上传的包目录（/manFile/package/……）
//	 * @return
//	 */
//	public static String packageFolder(String packType){
//		String path = manFolder() + DIR_PACKAGE + PATH_SEPARATOR;
//		if(!StringUtil.isNullStr(packType)){
//			if(packType.equals(Constant.PACKAGE_TYPE_MERCHANT)){
//				path = path + DIR_MERCHANT + PATH_SEPARATOR;
//			}else{
//				path = path + DIR_USER + PATH_SEPARATOR;
//			}
//		}
//		return path;
//	}
//	
//	/**
//	 * 后台上传的图片目录（/manFile/image/……）
//	 * @return
//	 */
//	public static String imageFolder(String imgType){
//		String path = manFolder() + DIR_IMAGE + PATH_SEPARATOR;
//		if(!StringUtil.isNullStr(imgType)){
//			path = path + imgType + PATH_SEPARATOR;
//		}
//		return path;
//	}
//	
//	
//	/**
//	 * 根据业务类型拼接图片文件路径
//	 * @param useType
//	 * @return
//	 */
//
////	public static String folderPath(String imgType){
////		return PATH_SEPARATOR + MAN_FILE + PATH_SEPARATOR + DIR_IMAGE + PATH_SEPARATOR + imgType + PATH_SEPARATOR;
////	}
//
//	public static String VoucherPath(String appType){
//		return imageFolder(NEW_DIR_VOUCHER) + appType + PATH_SEPARATOR;
//	}
//
//	public static String replaceSeparator(String path) {
//		return (path == null) ? null : path.replaceAll("\\\\", "/").replaceAll(
//				"//", "/");
//	}
//	
//    /**
//     * 文件上传工具类
//     * @param uploadFile 文件
//     * @param filePath 文件上传路径
//     * @return
//     */
////	public static String fileUpload(UploadFile uploadFile, String filePath) {
////		// 相对路径
////		String path = null;
////		// 上传文件的文件全名（包含文件后缀名）
////		String originalName = uploadFile.getOriginalFileName();  // 上传文件原始文件名
////		String fileName = originalName;                          // 上传服务器的文件名
////		String suffix = fileName.substring(fileName.lastIndexOf("."));
////		Date now = new Date();
////		fileName = now.getTime() + suffix;
////		path = new StringBuffer(filePath).append(fileName).toString();
////		// 文件在磁盘上的真实路径
////		String realPath = uploadFile.getSaveDirectory();
////		// 判断文件夹是否存在,如果不存在则创建文件夹
////		File file = new File(realPath);
////		if (!file.exists()) {
////			file.mkdir();
////		}
////		File realfile = uploadFile.getFile();
////		
////		if("true".endsWith(Constant.FTP_MODE_OSS)){
////			// 阿里OSS
////		   	InputStream in = AliOssUtil.class.getResourceAsStream(originalName);
////			if (in==null){
////				System.out.println("文件未找到");
////				return path;
////			}
////			AliOssUtil.upload(filePath, fileName, in);
////		}else{
////			// 直接上传至服务器
//////			try {
//////				if (FTPUtil.uploadFile(filePath, realfile, fileName)) {
//////					deleteFile(realfile);
//////				} else {
//////					if (FTPUtil.uploadFile(filePath, realfile, fileName)) {
//////						deleteFile(realfile);
//////					}
//////				}
//////			} catch (Exception ex) {
//////				ex.printStackTrace();
//////				deleteFile(realfile);
//////				path = Constant.EMPTY;
//////			}
////		}
////		return path;
////	}
//    
//    /** 文件上传 */
////    public static String fileUpload(UploadFile uploadFile, String filePath) {
////        // 相对路径
////        String path = null;
////        // 上传文件的文件全名（包含文件后缀名）
////      String fileName = uploadFile.getOriginalFileName();
////      String suffix = fileName.substring(fileName.lastIndexOf("."));
////      //fileName = UUID.randomUUID().toString() + suffix;
////      Date now = new Date();
////      fileName = now.getTime() + suffix;
////      path = new StringBuffer(filePath).append(fileName).toString();
////        // 文件在磁盘上的真实路径
////        String realPath = uploadFile.getSaveDirectory();
////        // 判断文件夹是否存在,如果不存在则创建文件夹
////        File file = new File(realPath);
////        if (!file.exists()) {
////            file.mkdir();
////        }
////        File realfile=uploadFile.getFile();
////      
////        try {
////            if (FTPUtil.uploadFile(filePath, realfile,fileName)) {
////                deleteFile(realfile);
////            } else {
////                if (FTPUtil.uploadFile(filePath, realfile,fileName)) {
////                    deleteFile(realfile);
////                }
////            }
////        } catch (Exception ex) {
////            ex.printStackTrace();
////            deleteFile(realfile);
////            path = Constant.EMPTY;
////        }
////        return path;
////    }
//	
//
//
//    /** 文件上传过程中出错或文件路径保存到数据库失败的场合,将已上传的文件删除 */
//    public static void deleteFile(String realPath) {
//        deleteFile(new File(realPath));
//    }
//
//    /** 文件上传过程中出错或文件路径保存到数据库失败的场合,将已上传的文件删除 */
//    public static void deleteFile(File delFile) {
//        if (delFile.isFile() && delFile.exists()) {
//            delFile.delete();
//        }
//    }
//    
//    
//    
}

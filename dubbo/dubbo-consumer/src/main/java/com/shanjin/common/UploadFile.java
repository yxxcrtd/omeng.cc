package com.shanjin.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.shanjin.common.constant.Constant;
import com.shanjin.common.util.BusinessUtil;

public class UploadFile {
	
	/**
	 * 上传附件公用方法
	 * @param request
	 * @param paths
	 * @return
	 */
	public static List<String> uplaodFile(HttpServletRequest request,String voiceUPloadPath,String imageUploadPath){
		//保存附件
		List<String> paths = new ArrayList<String>();
		MultipartFile voice = null;
		MultipartFile picture0 = null;
		MultipartFile picture1 = null;
		MultipartFile picture2 = null;
		MultipartFile picture3 = null;
		MultipartFile picture4 = null;
		if (request instanceof MultipartHttpServletRequest) {
			try {
				MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
				voice = multipartRequest.getFile("voice");
				picture0 = multipartRequest.getFile("picture0");
				picture1 = multipartRequest.getFile("picture1");
				picture2 = multipartRequest.getFile("picture2");
				picture3 = multipartRequest.getFile("picture3");
				picture4 = multipartRequest.getFile("picture4");

				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
				String voiceFilePath =voiceUPloadPath+Constant.FILE_EPARATOR+sdf.format(new Date())+Constant.FILE_EPARATOR;
				String imageFilePath =imageUploadPath+Constant.FILE_EPARATOR+sdf.format(new Date())+Constant.FILE_EPARATOR;
				
				if (voice != null && !voice.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(voice, voiceFilePath);
					if (resultPath.length() > 1) {
						paths.add(resultPath);
					}
				}
				if (picture0 != null && !picture0.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(picture0, imageFilePath);
					if (resultPath.length() > 1) {
						paths.add(resultPath);
					}
				}
				if (picture1 != null && !picture1.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(picture1, imageFilePath);
					if (resultPath.length() > 1) {
						paths.add(resultPath);
					}
				}
				if (picture2 != null && !picture2.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(picture2, imageFilePath);
					if (resultPath.length() > 1) {
						paths.add(resultPath);
					}
				}
				if (picture3 != null && !picture3.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(picture3, imageFilePath);
					if (resultPath.length() > 1) {
						paths.add(resultPath);
					}
				}
				if (picture4 != null && !picture4.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(picture4, imageFilePath);
					if (resultPath.length() > 1) {
						paths.add(resultPath);
					}
				}

			} catch (Exception e) {
				MsgTools.sendMsgOrIgnore(e, "saveMerchantServiceRecord");
			}
		}
		return paths;
	}
	
	/**
	 * 上传附件公用方法
	 * @param request
	 * @param paths
	 * @return
	 */
	public static List<String> uplaodFile4Goods(HttpServletRequest request,String voiceUPloadPath,String imageUploadPath,List<String> paths){
		//保存附件
		MultipartFile voice = null;
		MultipartFile picture0 = null;
		MultipartFile picture1 = null;
		MultipartFile picture2 = null;
		MultipartFile picture3 = null;
		MultipartFile picture4 = null;
		if (request instanceof MultipartHttpServletRequest) {
			try {
				MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
				voice = multipartRequest.getFile("voice");
				picture0 = multipartRequest.getFile("picture0");
				picture1 = multipartRequest.getFile("picture1");
				picture2 = multipartRequest.getFile("picture2");
				picture3 = multipartRequest.getFile("picture3");
				picture4 = multipartRequest.getFile("picture4");

				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
				String voiceFilePath =voiceUPloadPath+Constant.FILE_EPARATOR+sdf.format(new Date())+Constant.FILE_EPARATOR;
				String imageFilePath =imageUploadPath+Constant.FILE_EPARATOR+sdf.format(new Date())+Constant.FILE_EPARATOR;
				
				if (voice != null && !voice.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(voice, voiceFilePath);
					if (resultPath.length() > 1) {
						paths.add(resultPath);
					}
				}
				if (picture0 != null && !picture0.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(picture0, imageFilePath);
					if (resultPath.length() > 1) {
						paths.set(0,resultPath);
					}
				}
				if (picture1 != null && !picture1.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(picture1, imageFilePath);
					if (resultPath.length() > 1) {
						paths.set(1,resultPath);
					}
				}
				if (picture2 != null && !picture2.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(picture2, imageFilePath);
					if (resultPath.length() > 1) {
						paths.set(2,resultPath);
					}
				}
				if (picture3 != null && !picture3.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(picture3, imageFilePath);
					if (resultPath.length() > 1) {
						paths.set(3,resultPath);
					}
				}
				if (picture4 != null && !picture4.isEmpty()) {
					String resultPath = BusinessUtil.fileUpload(picture4, imageFilePath);
					if (resultPath.length() > 1) {
						paths.set(4,resultPath);
					}
				}

			} catch (Exception e) {
				MsgTools.sendMsgOrIgnore(e, "saveMerchantServiceRecord");
			}
		}
		return paths;
	}
}

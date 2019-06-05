package com.shanjin.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class EditImage {
	public static Logger log = LoggerFactory.getLogger(EditImage.class);
	
	public static BufferedImage resize(BufferedImage source, double scale) {
		int targetW = (int) (source.getWidth() * scale);
		int targetH = (int) (source.getHeight() * scale);
		return compressPic(source,targetW,targetH);
//		return resize(source,targetW,targetH);
	}

	// 图片处理 
	public static BufferedImage compressPic(BufferedImage source, int targetW,
			int targetH) { 
		return compressPic(source, targetW, targetH,true);
	}
	 public static BufferedImage compressPic(BufferedImage source, int targetW,
				int targetH,boolean proportion) { 
		 try { 
			 //获得源文件 
			 Image img = source; 
			 // 判断图片格式是否正确 
			 if (img.getWidth(null) == -1) {
				 System.out.println(" can't read,retry!" + "<BR>"); 
				 return null; 
			 } else { 
				 int newWidth; int newHeight; 
				 // 判断是否是等比缩放 
				 if (proportion) { 
					 // 为等比缩放计算输出的图片宽度及高度 
					 double rate1 = ((double) img.getWidth(null)) / (double) targetW + 0.1; 
					 double rate2 = ((double) img.getHeight(null)) / (double) targetH + 0.1; 
					 // 根据缩放比率大的进行缩放控制 
					 double rate = rate1 > rate2 ? rate1 : rate2; 
					 newWidth = (int) (((double) img.getWidth(null)) / rate); 
					 newHeight = (int) (((double) img.getHeight(null)) / rate); 
				 } else { 
					 newWidth = targetW; // 输出的图片宽度 
					 newHeight = targetH; // 输出的图片高度 
				 } 
			 	BufferedImage tag = new BufferedImage((int) newWidth, (int) newHeight, BufferedImage.TYPE_INT_RGB); 
			 	
			 	/*
				 * Image.SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的
				 * 优先级比速度高 生成的图片质量比较好 但速度慢
				 */ 
			 	tag.getGraphics().drawImage(img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0, null);

			 	return tag;
			 } 
		 } catch (Exception ex) { 
			 ex.printStackTrace(); 
		 } 
		 return null; 
	}

	public static void saveImageAsJpg(String fromFileStr, String saveToFileStr,
			int width, int hight) throws Exception {
		BufferedImage srcImage;
		// String ex =
		// fromFileStr.substring(fromFileStr.indexOf("."),fromFileStr.length());
		String imgType = "JPEG";
		if (fromFileStr.toLowerCase().endsWith(".png")) {
			imgType = "PNG";
		}
		File saveFile = new File(saveToFileStr);
		File fromFile = new File(fromFileStr);
		srcImage = ImageIO.read(fromFile);
		if (width > 0 || hight > 0) {
			srcImage = compressPic(srcImage, width, hight);
		}
//		ImageIO.write(srcImage, imgType, saveFile);
		
//		FileOutputStream out = new FileOutputStream(saveFile);
//	 	// JPEGImageEncoder可适用于其他图片类型的转换
//	 	JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//	 	encoder.encode(srcImage);
//	 	out.close();

		saveImage(srcImage,saveToFileStr);
	}

	static void saveImage(BufferedImage dstImage, String dstName) throws IOException {
		String formatName = dstName.substring(dstName.lastIndexOf(".") + 1);
		//FileOutputStream out = new FileOutputStream(dstName);
		//JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
		//encoder.encode(dstImage);
		ImageIO.write(dstImage, /*"GIF"*/ formatName /* format desired */ , new File(dstName) /* target */ );
	}

}

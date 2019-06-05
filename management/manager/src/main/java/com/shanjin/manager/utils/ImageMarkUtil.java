package com.shanjin.manager.utils;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.shanjin.manager.constant.Constant;
import com.shanjin.outServices.aliOss.AliCdnUtil;
import com.shanjin.outServices.aliOss.AliOssUtil;
import com.taobao.api.ApiException;
/**
 * 图片水印
 */
public class ImageMarkUtil {
    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
 //       String srcImgPath = "d:/qiye.jpg";   
        String iconPath = "d:/480.png"; 
//        String targerPath = "d:/mmkk.jpg";
        //String srcImgPath = "http://d.3987.com/wzqxs_130822/002.jpg"; 
        String srcImgPath = "http://t-1.tuzhan.com/54774b6ce86c/c-1/l/2012/09/21/00/141a23c78bba460d9c7d0bbddb514e29.jpg"; 
        
        //String iconPath = "http://192.168.1.48:81/waterMark/waterMark.png"; 
        String targerPath = "d:/mmkk11.jpg";
        String name = getNetImageName(srcImgPath);
        targerPath = "c:/"+name;
        // 给图片添加水印
        //String path = ImageMarkUtil.watermarkDestPath(iconPath, srcImgPath);
        ImageMarkUtil.waterMarkImageByIcon(iconPath, srcImgPath, targerPath, 0, 0,0, 0.9f);
        File file = new File(targerPath);
        
        FTPUtil.uploadFile(BusinessUtil.manFolder(),file, name);
        BusinessUtil.deleteFile(targerPath);
        // 给图片添加水印,水印旋转-45
        // ImageMarkLogoByIcon.markImageByIcon(iconPath, srcImgPath,
        // targerPath2, -45);
    }
    
    public static void ftpWaterMark(String fileName,String targetPath,String srcPath) throws Exception{
 	    File file = new File(targetPath);
   		if("true".equals(Constant.FTP_MODE_OSS)){
			// 阿里OSS
		    InputStream in = null;
			try {
				in = new FileInputStream(file);
				if (in==null){
					System.out.println("文件未找到");
					return;
				}
				AliOssUtil.upload(srcPath, fileName, in);
				try {
				AliCdnUtil.refresh(srcPath+fileName,false);
			} catch (ApiException e) {
				e.printStackTrace();
			}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} 
			BusinessUtil.deleteFile(targetPath);
		}else{
	           FTPUtil.uploadFile(srcPath,file, fileName);
	           BusinessUtil.deleteFile(targetPath);
		}

    }
    
    public static String getNetImageName(String imageUrl){
    	String name = "";
    	if(!StringUtil.isNullStr(imageUrl)){
    		int last = imageUrl.lastIndexOf("/"); //最后一个“/”的位置
    		if(last!=-1){
        		name = imageUrl.substring(last+1);
    		}
    	}
    	return name;
    }
    
    public static String getImagePath(String imageUrl){
    	String path = "";
    	if(!StringUtil.isNullStr(imageUrl)){
    		if(imageUrl.contains("http://")){
    			imageUrl = imageUrl.substring(7);
    		}
    		int last = imageUrl.lastIndexOf("/"); //最后一个“/”的位置
    		if(last!=-1){
    			path = imageUrl.substring(0, last+1);
    		}
    		int first = path.indexOf("/");
    		if(first!=-1){
    			path = path.substring(first);
    		}
    	}
    	return path;
    }
    
    
    public static Image readImage(String imageUrl){
    	Image image = null;
    	URL url;
    	InputStream is = null;
		try {
			url = new URL(imageUrl);
	        URLConnection con;
			con = url.openConnection();
			   //不超时  
	        con.setConnectTimeout(0);  
	           
	         //不允许缓存  
	         con.setUseCaches(false);  
	         con.setDefaultUseCaches(false);  
	         is = con.getInputStream();  
	         // 先读入内存  
	         ByteArrayOutputStream buf = new ByteArrayOutputStream(8192);  
	         byte[] b = new byte[1024];  
	         int len;  
	         while ((len = is.read(b)) != -1) {  
	             buf.write(b, 0, len);  
	         }  
	         is.close();  
	       
	         System.out.println(buf.size());  
	         byte[] arr = buf.toByteArray();  
	        /* //保存到文件  
	         FileOutputStream out = new FileOutputStream(new File(String.valueOf(System  
	                 .currentTimeMillis())));  
	         out.write(arr);  
	         out.close();  */
	         // 读图像  
	         ByteArrayInputStream in = new ByteArrayInputStream(buf.toByteArray());  
	         image = ImageIO.read(in);  
	         in.close();  
		} catch (IOException e) {
			e.printStackTrace();
		} finally {  
		    if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}  
		} 
      return image;
    }
    
    
    /**
     * 给图片添加水印、可设置水印图片旋转角度
     *
     * @param srcImgPath
     *            源图片路径
     * @param degree
     *            水印图片旋转角度
     * @param width
     *            宽度(与左相比)
     * @param height
     *            高度(与顶相比)
     * @param clarity
     *            透明度(小于1的数)越接近0越透明
     */
    public static void waterMarkImage(String srcImgPath,Integer degree, Integer width, Integer height,
            float clarity) {
        OutputStream os = null;
        String iconPath = Constant.WATER_MARK_IMG;
        String targerPath = Constant.WATER_MARK_PATH;
        try {
            //Image srcImg = ImageIO.read(new File(srcImgPath));
        	Image srcImg = readImage(srcImgPath);
            BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null),
                    srcImg.getHeight(null), BufferedImage.TYPE_INT_RGB);
            // 得到画笔对象
            // Graphics g= buffImg.getGraphics();
            Graphics2D g = buffImg.createGraphics();
            // 设置对线段的锯齿状边缘处理
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(
                    srcImg.getScaledInstance(srcImg.getWidth(null),
                            srcImg.getHeight(null), Image.SCALE_SMOOTH), 0, 0,
                    null);
            if (null != degree) {
                // 设置水印旋转
                g.rotate(Math.toRadians(degree),
                        (double) buffImg.getWidth() / 2,
                        (double) buffImg.getHeight() / 2);
            }
            // 水印图象的路径 水印一般为gif或者png的，这样可设置透明度
            ImageIcon imgIcon = new ImageIcon(iconPath);
            int iconWidth = imgIcon.getIconWidth();
            int iconHeight = imgIcon.getIconHeight();
            // 得到Image对象。
            Image img = imgIcon.getImage();
            // 计算水印居中时所要偏移的 高度和左边距
            int srcImgWidth = srcImg.getWidth(null);
            int srcImgHeight = srcImg.getHeight(null);
            if(srcImgWidth>iconWidth){
            	width = (int) ((srcImgWidth-iconWidth)*1.0/2);
            }
            if(srcImgHeight>iconHeight){
            	height = (int) ((srcImgHeight-iconHeight)*1.0/2);
            }
            float alpha = clarity; // 透明度
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
                    alpha));
            // 表示水印图片的位置
            g.drawImage(img, width, height, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
            g.dispose();
            
            String fileName = getNetImageName(srcImgPath);
            targerPath = targerPath+fileName;
            os = new FileOutputStream(targerPath);
            // 生成图片
            ImageIO.write(buffImg, "JPG", os);
            System.out.println("添加水印图片完成!");
            
            // ftp 文件
            String srcPath = getImagePath(srcImgPath);
            ftpWaterMark(fileName,targerPath,srcPath);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 给图片添加水印、可设置水印图片旋转角度
     *
     * @param iconPath
     *            水印图片路径
     * @param srcImgPath
     *            源图片路径
     * @param targerPath
     *            目标图片路径
     * @param degree
     *            水印图片旋转角度
     * @param width
     *            宽度(与左相比)
     * @param height
     *            高度(与顶相比)
     * @param clarity
     *            透明度(小于1的数)越接近0越透明
     */
    public static void waterMarkImageByIcon(String iconPath, String srcImgPath,
            String targerPath, Integer degree, Integer width, Integer height,
            float clarity) {
        OutputStream os = null;
        try {
            //Image srcImg = ImageIO.read(new File(srcImgPath));
        	Image srcImg = readImage(srcImgPath);
            BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null),
                    srcImg.getHeight(null), BufferedImage.TYPE_INT_RGB);
            // 得到画笔对象
            // Graphics g= buffImg.getGraphics();
            Graphics2D g = buffImg.createGraphics();
            // 设置对线段的锯齿状边缘处理
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(
                    srcImg.getScaledInstance(srcImg.getWidth(null),
                            srcImg.getHeight(null), Image.SCALE_SMOOTH), 0, 0,
                    null);
            if (null != degree) {
                // 设置水印旋转
                g.rotate(Math.toRadians(degree),
                        (double) buffImg.getWidth() / 2,
                        (double) buffImg.getHeight() / 2);
            }
            // 水印图象的路径 水印一般为gif或者png的，这样可设置透明度
            ImageIcon imgIcon = new ImageIcon(iconPath);
            int iconWidth = imgIcon.getIconWidth();
            int iconHeight = imgIcon.getIconHeight();
            // 得到Image对象。
            Image img = imgIcon.getImage();
            // 计算水印居中时所要偏移的 高度和左边距
            int srcImgWidth = srcImg.getWidth(null);
            int srcImgHeight = srcImg.getHeight(null);
//            int iconWidth = img.getWidth(null);
//            int iconHeight = img.getHeight(null);
            System.out.println("源图片width:" + srcImgWidth);
            System.out.println("源图片height:" + srcImgHeight);
            System.out.println("水印图片width:" + iconWidth);
            System.out.println("水印图片height:" + iconHeight);
            if(srcImgWidth>iconWidth){
            	width = (int) ((srcImgWidth-iconWidth)*1.0/2);
            }
            if(srcImgHeight>iconHeight){
            	height = (int) ((srcImgHeight-iconHeight)*1.0/2);
            }
            System.out.println("偏移width:" + width);
            System.out.println("偏移height:" + height);
            float alpha = clarity; // 透明度
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
                    alpha));
            // 表示水印图片的位置
            g.drawImage(img, width, height, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
            g.dispose();
            os = new FileOutputStream(targerPath);
            // 生成图片
            ImageIO.write(buffImg, "JPG", os);
            System.out.println("添加水印图片完成!");
            
            // ftp 文件
//            String fileName = getNetImageName(srcImgPath);
//            targerPath = targerPath+fileName;
//            String srcPath = getImagePath(srcImgPath);
//            ftpWaterMark(fileName,targerPath,srcPath);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 给图片添加水印、可设置水印图片旋转角度
     *
     * @param logoText
     *            水印文字
     * @param srcImgPath
     *            源图片路径
     * @param targerPath
     *            目标图片路径
     * @param degree
     *            水印图片旋转角度
     * @param width
     *            宽度(与左相比)
     * @param height
     *            高度(与顶相比)
     * @param clarity
     *            透明度(小于1的数)越接近0越透明
     */
    public static void waterMarkByText(String logoText, String srcImgPath,
            String targerPath, Integer degree, Integer width, Integer height,
            Float clarity) {
        // 主图片的路径
        InputStream is = null;
        OutputStream os = null;
        try {
            Image srcImg = ImageIO.read(new File(srcImgPath));
            BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null),
                    srcImg.getHeight(null), BufferedImage.TYPE_INT_RGB);
            // 得到画笔对象
            // Graphics g= buffImg.getGraphics();
            Graphics2D g = buffImg.createGraphics();
            // 设置对线段的锯齿状边缘处理
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(
                    srcImg.getScaledInstance(srcImg.getWidth(null),
                            srcImg.getHeight(null), Image.SCALE_SMOOTH), 0, 0,
                    null);
            if (null != degree) {
                // 设置水印旋转
                g.rotate(Math.toRadians(degree),
                        (double) buffImg.getWidth() / 2,
                        (double) buffImg.getHeight() / 2);
            }
            // 设置颜色
            g.setColor(Color.red);
            // 设置 Font
            g.setFont(new Font("宋体", Font.BOLD, 30));
            float alpha = clarity;
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
                    alpha));
            // 第一参数->设置的内容，后面两个参数->文字在图片上的坐标位置(x,y) .
            g.drawString(logoText, width, height);
            g.dispose();
            os = new FileOutputStream(targerPath);
            // 生成图片
            ImageIO.write(buffImg, "JPG", os);
            System.out.println("添加水印文字完成!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != is)
                    is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (null != os)
                    os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



/**
     * 图片缩放(图片等比例缩放为指定大小，空白部分以白色填充)
     *
     * @param srcPath
     *            源图片路径
     * @param destPath
     *            缩放后图片路径
     */
    public static void zoomImage(String srcPath, String destPath, int destHeight, int destWidth) {
        try {
            BufferedImage srcBufferedImage = ImageIO.read(new File(srcPath));
            int imgWidth = destWidth;
            int imgHeight = destHeight;
            int srcWidth = srcBufferedImage.getWidth();
            int srcHeight = srcBufferedImage.getHeight();
            if (srcHeight >= srcWidth) {
                imgWidth = (int) Math.round(((destHeight * 1.0 / srcHeight) * srcWidth));
            } else {
                imgHeight = (int) Math.round(((destWidth * 1.0 / srcWidth) * srcHeight));
            }
            BufferedImage destBufferedImage = new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = destBufferedImage.createGraphics();
           // graphics2D.setBackground(Color.WHITE);
            graphics2D.setBackground(new Color(255, 255, 255, 0));
            
            graphics2D.clearRect(0, 0, destWidth, destHeight);
            graphics2D.drawImage(srcBufferedImage.getScaledInstance(imgWidth, imgHeight, Image.SCALE_SMOOTH), (destWidth / 2) - (imgWidth / 2), (destHeight / 2) - (imgHeight / 2), null);
            graphics2D.dispose();
            ImageIO.write(destBufferedImage, "PNG", new File(destPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 根据水印源文件和源图片的大小缩放生成对应水印图片，并返回目标水印路径
     * @param iconPath
     * @param srcImgPath
     * @return
     */
    public static String watermarkDestPath(String iconPath, String srcImgPath){
    	String path = iconPath;
        try {
            BufferedImage srcBufferedImage = ImageIO.read(new File(srcImgPath));

            BufferedImage waterSrcImg = ImageIO.read(new File(iconPath));

            int waterWidth = waterSrcImg.getWidth();
            int waterHeight = waterSrcImg.getHeight();
            int srcWidth = srcBufferedImage.getWidth();
            int srcHeight = srcBufferedImage.getHeight();
            int waterDestWidth = srcWidth;
            int waterDestHeight = (int) Math.round(((waterDestWidth * 1.0 / waterWidth) * waterHeight));
            System.out.println("源图片文件width:" + srcWidth);
            System.out.println("源图片文件height:" + srcHeight);
            System.out.println("源水印图片文件width:" + waterWidth);
            System.out.println("源水印图片文件height:" + waterHeight);
            System.out.println("目标水印图片文件width:" + waterDestWidth);
            System.out.println("目标水印图片文件height:" + waterDestHeight);
            String fileName = waterDestWidth+""+waterDestHeight;
            path = path.replace("shuiyin", fileName);
            System.out.println("目标水印图片文件:" + path);
            zoomImage(iconPath,path,waterDestHeight,waterDestWidth);
        } catch (IOException e) {
            e.printStackTrace();
        }
    	return path;
    }
    
    
}
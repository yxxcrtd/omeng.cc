package com.shanjin.common.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * QRcode 二维码
 * @author Huang yulai
 *
 */
public class QRCodeUtil {
    
    private static final int BLACK = 0xFF000000;  
	private static final int WHITE = 0xFFFFFFFF;   
	
	  /**
     *  生成二维码
     */
    public static String createQRcode(String content,String imgPath){
           int width = 300;
           int height = 300;
         //  imgPath = "D:/code/qrcode.png";
           String format = "png";
          // content = "http://www.vip.com/";
           // 定义二维码参数
           HashMap hints = new HashMap();
           hints.put(EncodeHintType.CHARACTER_SET , "utf-8");
           hints.put(EncodeHintType.ERROR_CORRECTION , ErrorCorrectionLevel.M );
           hints.put(EncodeHintType.MARGIN , 2);
           // 生成二维码
           try {
			   BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
			   Path file =  new File(imgPath).toPath();
			   MatrixToImageWriter. writeToPath(bitMatrix, format, file);
		   } catch (Exception e) {
			   e.printStackTrace();
			   return null;
		   }
           return imgPath;
    }
    
    /**
     *  读取二维码
     */
    public static void readQRcode(){
           try {
                MultiFormatReader formatReader = new MultiFormatReader();
                File file = new File( "D:/code/qrcode.png");
                BufferedImage image = ImageIO. read(file);
                BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image )));
                 // 定义二维码参数
                 HashMap hints = new HashMap();
                 hints.put(EncodeHintType.CHARACTER_SET , "utf-8");
                Result result = formatReader.decode( binaryBitmap,hints );
                System. out.println( "解析结果：" +result.toString());
                System. out.println( "二维码格式类型：" +result.getBarcodeFormat());
                System. out.println( "二维码文本内容：" +result.getText());
          } catch (Exception e) {
                 e.printStackTrace();
          }

    }
    
//    public static void main(String[] arg){
//           createQRcode();
//           readQRcode();
//    }

	      
	    /** 
	     *  最终调用该方法生成二维码图片 
	     * @param url 要生成二维码的url 
	     * @param imgPath 二维码生成的绝对路径 
	     * @param logoPath 二维码中间logo绝对地址 
	     * @throws Exception 
	     */  
	    public static String createQRcodeWithLogo(String url,String imgPath,String logoPath) throws Exception{  
	        String format = "png";  
	        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();   
	        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");    
	        BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, 300, 300, hints);  
	        File qrcodeFile = new File(imgPath);    
	        writeToFile(bitMatrix, format, qrcodeFile, logoPath); 
	        return imgPath;
	    }  
	      
	    /** 
	     *  
	     * @param matrix 二维码矩阵相关 
	     * @param format 二维码图片格式 
	     * @param file 二维码图片文件 
	     * @param logoPath logo路径 
	     * @throws IOException 
	     */  
	    public static void writeToFile(BitMatrix matrix,String format,File file,String logoPath) throws IOException {  
	        BufferedImage image = toBufferedImage(matrix);  
	        Graphics2D gs = image.createGraphics();  
	          
	        //载入logo  
	        Image img = ImageIO.read(new File(logoPath));  
	        gs.drawImage(img, 125, 125, null);  
	        gs.dispose();  
	        img.flush();  
	        if(!ImageIO.write(image, format, file)){  
	            throw new IOException("Could not write an image of format " + format + " to " + file);    
	        }  
	    }  
	      
	    public static BufferedImage toBufferedImage(BitMatrix matrix){  
	        int width = matrix.getWidth();  
	        int height = matrix.getHeight();  
	        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);  
	          
	        for(int x=0;x<width;x++){  
	            for(int y=0;y<height;y++){  
	                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);  
	            }  
	        }  
	        return image;     
	    }  
	  
	      
	    public static void main(String[] args) {  
	        try {  
	        	createQRcodeWithLogo("www.baidu.com","D:/code/qrcodewithlogo.png","D:/code/logo.png");  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }   
	    }  

	
}

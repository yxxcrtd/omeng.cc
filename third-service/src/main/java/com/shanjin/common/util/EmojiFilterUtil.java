package com.shanjin.common.util;

/**
 * 字符串中表情过滤
 * @author Huang yulai
 *
 */
public class EmojiFilterUtil {

	  /** 
     * 将emoji表情替换成* 
     *  
     * @param source 
     * @return 过滤后的字符串 
     */  
//    public static String filterEmoji(String source) {  
//        if(StringUtil.isNullStr(source)){  
//            return source.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", "*");  
//        }else{  
//            return source;  
//        }  
//    }  
    public static void main(String[] arg ){  
        try{  
            String text = "This is a smiley \uD83C\uDFA6 face\uD860\uDD5D \uD860\uDE07 \uD860\uDEE2 \uD863\uDCCA \uD863\uDCCD \uD863\uDCD2 \uD867\uDD98 ";  
            System.out.println(text);  
            System.out.println(text.length());  
            System.out.println(text.replaceAll("[\\ud83c\\udc00-\\ud83c\\udfff]|[\\ud83d\\udc00-\\ud83d\\udfff]|[\\u2600-\\u27ff]", "*"));  
            System.out.println(filterEmoji(text));  
        }catch (Exception ex){  
            ex.printStackTrace();  
        }  
    }
    
    private static boolean isNotEmojiCharacter(char codePoint)
    {
    	return (codePoint == 0x0) ||
    		(codePoint == 0x9) ||
    		(codePoint == 0xA) ||
    		(codePoint == 0xD) ||
    		((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
    		((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
    		((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }

    /**
     * 过滤emoji 或者 其他非文字类型的字符
     * @param source
     * @return
     */
    public static String filterEmoji(String source)
    {
    	int len = source.length();
    	StringBuilder buf = new StringBuilder(len);
    	for (int i = 0; i < len; i++)
    	{
    		char codePoint = source.charAt(i);
    		if (isNotEmojiCharacter(codePoint))
    		{
    			buf.append(codePoint);
    		}
    	}
    	return buf.toString();
    }
	
}

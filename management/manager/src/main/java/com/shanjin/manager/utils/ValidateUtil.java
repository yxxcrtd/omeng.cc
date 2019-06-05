package com.shanjin.manager.utils;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 合法性检查
 * @author Huang yulai
 *
 */
public class ValidateUtil {
	
	public static boolean isCharactNumber(String value,int minLen,int maxLen){
		boolean flag=false;
		if (value != null && !"".equals(value)) {
			Pattern pattern = Pattern.compile("^[abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890]{" + minLen + "," + maxLen + "}$");
			Matcher matcher = pattern.matcher(value);
			flag = matcher.matches();
		}
		return flag;
	}
	
	public static boolean isFileName(String value,int minLen,int maxLen){
		boolean flag = false;
		if (value != null && !"".equals(value) && !StringUtil.null2Str(value).startsWith(".")) {
			Pattern pattern = Pattern.compile("^[abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890._-]{" + minLen + "," + maxLen + "}$");
			Matcher matcher = pattern.matcher(value);
			flag = matcher.matches();
		}
		return flag;
	}
	
	/**
	 * 判断各字段是否为null或者为""
	 * 
	 * @param str
	 *            各字段组成的数组
	 * @return 任意一个字段为null或者"",返回false，否则返回 true
	 */
	public static boolean isValidate(String[] str) {
		for (String string : str) {
			if (string == null || "".equals(string.trim()))
				return false;
		}
		return true;
	}

	/**
	 * 判断各参数是否为null或者为""
	 * 
	 * @param str
	 *            各字段组成的数组
	 * @return 任意一个字段为null或者"",返回false，否则返回 true
	 */
	public static boolean isValidate(Long[] str) {
		for (Long string : str) {
			if (string == null)
				return false;
		}
		return true;
	}

	/**
	 * 判断参数是否为null或者为""
	 * 
	 * @return 任意一个字段为null或者"",返回false，否则返回 true
	 */
	public static boolean isValidate(Long str) {
		if (str == null)
			return false;
		return true;
	}

	/**
	 * 判断参数是否为null或者为""
	 * 
	 * @return 字段为null或者"",返回false，否则返回 true
	 */
	public static boolean isValidate(String str) {
		if (str == null || "".equals(str.trim()))
			return false;
		return true;
	}

	/**
	 * 手机号码验证
	 * 
	 * @param mobileCode
	 *            被验证的手机号码
	 * @return 返回正确的手机号码，否则返回null
	 */
	public static String trimMobile(String mobileCode) {
		if (mobileCode != null)
			mobileCode = mobileCode.trim();
		String mobile = mobileCode;
		if (mobileCode == null || mobileCode.length() == 12
				|| mobileCode.length() < 11 || mobileCode.length() > 14)
			return "";
		if (mobileCode.length() == 14) {
			if (!mobileCode.startsWith("+86"))
				return "";
			else
				mobile = mobileCode.substring(3);
		}
		if (mobileCode.length() == 13) {
			if (!mobileCode.startsWith("86"))
				return "";
			else
				mobile = mobileCode.substring(2);
		}
		// if (!mobile.startsWith("13") && !mobile.startsWith("15") &&
		// !mobile.startsWith("18"))
		// return null;
		try {
			Long.parseLong(mobile.trim());
		} catch (Exception e) {
			return "";
		}
		if (!mobile.startsWith("1")) {
			return "";
		}
		return mobile;
	}

	public static String addAdrToMobile(String mobileCode) {
		String firstcode = "86";
		if (mobileCode != null)
			mobileCode = mobileCode.trim();
		String mobile = mobileCode;
		if (mobileCode == null || mobileCode.length() == 12
				|| mobileCode.length() < 11 || mobileCode.length() > 14
				|| mobileCode.length() == 0)
			return "";
		if (mobileCode.length() == 14) {
			if (!mobileCode.startsWith("+86"))
				return "";
			else
				mobile = mobileCode.substring(3);
		}
		if (mobileCode.length() == 13) {
			if (!mobileCode.startsWith("86"))
				return "";
			else
				mobile = mobileCode.substring(2);
		}
		try {
			Long.parseLong(mobile.trim());
		} catch (Exception e) {
			return "";
		}
		if (!mobile.startsWith("1")) {
			return "";
		}
		return firstcode + mobile;
	}

	/**
	 * 判断各字段是否为数字或者为字母 ，长度
	 * 
	 * @param str
	 *            需要判断的字符串
	 * @param wapType
	 *            wap 类型
	 * @return 字段是数字或者为字母长度符合规则,返回true，否则返回 false
	 */
	public static boolean isChannelId(String str) {

		if (str != null && !"".equals(str)) {
			// 如果是默认的channelId则返回成功
			if (str.equals("0_10010001001")) {
				return true;
			}

			String strs[] = str.split("_");
			if (strs.length == 3 || strs.length == 2) {
				for (int i = 0; i < strs.length; i++) {
					// 判断各字段是否为数字或者为字母
					if (!isChar(strs[i], i))
						return false;
				}
			} else if (strs.length == 1) {
				if (strs[0].length() != 11 && strs[0].length() != 4)
					return false;

				for (int i = 0; i < strs[0].length(); i++) {
					if (!(strs[0].charAt(i) >= '0' && strs[0].charAt(i) <= '9')) {
						return false;
					}
				}
			} else {
				return false;
			}
		} else {
			return false;
		}

		return true;
	}

	/**
	 * 判断各字段是否为数字或者为字母
	 * 
	 * @param str
	 *            需要判断的字符串
	 * @param wapType
	 *            wap 类型
	 * @return 字段是数字或者为字母长度符合规则,返回true，否则返回 false
	 */
	private static boolean isChar(String str, int index) {
		// 判断WAP1为0 ，WAP2为1001
		if (index == 0) {
			if (str.length() == 4) {
				for (int i = 0; i < str.length(); i++) {
					if (!(str.charAt(i) >= '0' && str.charAt(i) <= '9')) {
						return false;
					}
				}
				return true;
			}

			if (str.equals("0")) {
				return true;
			} else {
				return false;
			}
		}
		// 判断11位渠道ID为数字
		if (index == 1) {
			// 如果长度不为11 ，则不合法
			if (str.length() != 11) {
				return false;
			} else {// 如果标识不为数字 ，则不合法
				for (int i = 0; i < str.length(); i++) {
					if (!(str.charAt(i) >= '0' && str.charAt(i) <= '9')) {
						return false;
					}
				}
			}

		}
		// 判断6位标识为数字
		else if (index == 2) {
			// 如果长度不为6 ，则不合法
			if (str.length() != 6) {
				return false;
			} else {// 如果标识不为数字 ，则不合法
				for (int i = 0; i < str.length(); i++) {
					if (!(str.charAt(i) >= '0' && str.charAt(i) <= '9')) {
						return false;
					}
				}
			}
		}

		return true;
	}
	
	/**
	 * 判断Long.parseLong(s)是否抛NumberFormatException异常
	 * @param s
	 * @return
	 */
	public static boolean hasExceptionParseLong(String s){
		try{
			Long.parseLong(s);
		}catch(NumberFormatException e){
			return true;
		}
		return false;
	}
	
	public static boolean isCharactNumber(String value, int leastLen) {
		boolean flag = false;
		if (value != null && !"".equals(value)) {
			Pattern pattern = Pattern
					.compile("^[abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890]{"
							+ leastLen + ",}$");
			Matcher matcher = pattern.matcher(value);
			flag = matcher.matches();
		}
		return flag;
	}

	public static boolean isCharactNumberNickName(String value, int minLen,
			int maxLen) {
		boolean flag = false;
		if (value != null && !"".equals(value)) {
			Pattern pattern = Pattern
					.compile("^[^`~!@#$%^&*()+=|:,.<>/?]{1}[^`~!@#$%^&()+=|:;,.<>?]{"
							+ minLen + "," + maxLen + "}$");
			Matcher matcher = pattern.matcher(value);
			flag = matcher.matches();
		}
		return flag;
	}
	
	public static boolean isMail(String mail) {
		boolean flag = false;
		if (mail != null && !"".equals(mail)) {
			Pattern pattern = Pattern
					.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
			Matcher matcher = pattern.matcher(mail);
			flag = matcher.matches();
		}
		return flag;
	}

	public static boolean isMobile(String mobileCode) {
		boolean isMobile = false;
		String mobile = trimMobile(mobileCode);
		if (!"".equals(mobile)) {
			Pattern pattern = Pattern
					.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
			Matcher matcher = pattern.matcher(mobile);
			isMobile = matcher.matches();
		}
		return isMobile;
	}
}

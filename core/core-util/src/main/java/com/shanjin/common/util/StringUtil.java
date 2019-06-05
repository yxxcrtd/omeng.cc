package com.shanjin.common.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.text.Collator;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(StringUtil.class);
	
	public static String[] alphabet=new String[]{"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

	/**
	 * 判断是否为合法的日期时间字符串
	 * 
	 * @param str_input
	 * @return boolean;符合为true,不符合为false
	 */
	public static boolean isDate(String str_input, String rDateFormat) {
		if (!isNull(str_input)) {
			SimpleDateFormat formatter = new SimpleDateFormat(rDateFormat);
			formatter.setLenient(false);
			try {
				formatter.format(formatter.parse(str_input));
			} catch (Exception e) {
				logger.error("", e);
				return false;
			}
			return true;
		}
		return false;
	}

	public static boolean isNull(String str) {
		if (str == null)
			return true;
		else
			return false;
	}

	public static boolean isNullStr(String str) {
		if (null==str  || "".equals(str.trim()))
			return true;
		else
			return false;
	}

	/**
	 * 判断是否为空
	 * @param val
	 * @return    
	 * boolean   
	 * @throws
	 */
	public static boolean isEmpty(Object val){
		return !isNotEmpty(val);
	}

	/**
	 * 判断是否为空
	 * @param val
	 * @return    
	 * boolean   
	 * @throws
	 */
	public static boolean isNotEmpty(Object val){
		if(val==null){
			return false;
		}else{
			if(val.toString().equals("") || val.toString().toLowerCase().equals("null")){
				return false;
			}else{
				return true;
			}
		}
	}
	// 将NULL转换成空字符串
	public static String null2Str(Object value) {
		return value == null || "null".equals(value.toString()) ? "" : value.toString();
	}

	public static String null2Str(String value) {
		return value == null || "null".equals(value) ? "" : value.trim();
	}

	public static String nullToString(String value) {
		return value == null || "null".equals(value) ? "" : value.trim();
	}

	public static String nullToString(Object value) {
		return value == null ? "" : value.toString();
	}

	public static Long nullToLong(Object value) {
		return value == null || "null".equals(value.toString()) ? 0L : stringToLong(value.toString().trim());
	}

	// public static Long nullToCloneLong(Object value) {
	// return value == null || NumberUtils.isNumber(value.toString()) == false ?
	// null
	// : stringToLong(value.toString());
	// }

	public static Integer nullToInteger(Object value) {
		return value == null || "null".equals(value.toString()) ? 0 : stringToInteger(value.toString());
	}

	public static Boolean nullToBoolean(Object value) {
		if (value == null || "null".equals(value.toString()))
			return false;
		if ("1".equals(value.toString().trim()) || "true".equalsIgnoreCase(value.toString().trim()))
			return true;
		if ("1".equals(value.toString().trim()) || "yes".equalsIgnoreCase(value.toString().trim()))
			return true;
		return false;
	}

	public static Float nullToFloat(Object value) {
		return value == null || "null".equals(value.toString()) || "".equals(value.toString()) ? 0.0F : stringToFloat(value.toString().trim());
	}


	public static Double nullToDouble(Object value) {
		return value == null || "null".equals(value.toString()) || "".equals(value.toString()) ? 0 : stringToDouble(value.toString().trim());
	}
	public static Float stringToFloat(String value) {
		Float f;
		value = nullToString(value);
		if ("".equals(value)) {
			f = 0.0F;
		} else {
			try {
				f = Float.valueOf(value);

			} catch (Exception e) {
				f = 0.0F;
				logger.error("", e);
			}
		}
		return f;
	}
	public static Double stringToDouble(String value) {
		Double d;
		value = nullToString(value);
		if ("".equals(value)) {
			d = 0D;
		} else {
			try {
				d =Double.valueOf(value);

			} catch (Exception e) {
				d = 0D;
				logger.error("", e);
			}
		}
		return d;
	}
	public static Long stringToLong(String value) {
		Long l;
		value = nullToString(value);
		if ("".equals(value)) {
			l = 0L;
		} else {
			try {
				l = Long.valueOf(value);

			} catch (Exception e) {
				l = 0L;
				logger.error("", e);
			}
		}
		return l;
	}

	public static Integer stringToInteger(String value) {
		Integer l;
		value = nullToString(value);
		if ("".equals(value)) {
			l = 0;
		} else {
			try {
				l = Integer.valueOf(value);

			} catch (Exception e) {
				l = 0;
				logger.error("", e);
			}
		}
		return l;
	}

	public static List<Long> stringToLongArray(String value) throws Exception {
		List<Long> ls = new ArrayList<Long>();
		if (StringUtil.isNullStr(value)) {
			return ls;
		}

		try {
			String[] ids = value.split(",");
			for (int i = 0; i < ids.length; i++) {
				if (StringUtil.isNumber(ids[i])) {
					ls.add(Long.parseLong(ids[i]));
				}
			}
		} catch (Exception e) {
			logger.error("", e);
			throw e;
		}
		return ls;
	}

	public static List<Long> stringToLongArray(Object value) throws Exception {
		List<Long> ls = new ArrayList<Long>();
		if (value == null || StringUtil.isNullStr(value.toString())) {
			return ls;
		}

		try {
			String[] ids = value.toString().split(",");
			for (int i = 0; i < ids.length; i++) {
				if (StringUtil.isNumber(ids[i])) {
					ls.add(Long.parseLong(ids[i]));
				}
			}
		} catch (Exception e) {
			logger.error("", e);
			throw e;
		}
		return ls;
	}

	public static List<String> strToStrArray(String value, String splitChar) throws Exception {
		List<String> ls = new ArrayList<String>();
		if (value == null || "".equals(value)) {
			return ls;
		}

		try {
			String[] ids = value.split(splitChar);
			for (int i = 0; i < ids.length; i++) {
				if (!StringUtil.isNullStr(ids[i])) {
					ls.add(ids[i].trim());
				}
			}
		} catch (Exception e) {
			logger.error("", e);
			throw e;
		}
		return ls;
	}

	public static String strArrayToString(List<String> list, String splitChar) throws Exception {
		StringBuffer sb = new StringBuffer("");
		if (list == null || list.size() == 0) {
			return sb.toString();
		}

		try {
			for (Iterator<String> it = list.iterator(); it.hasNext();) {
				String value = it.next();
				sb.append(value);
				if (it.hasNext()) {
					sb.append(splitChar);
				}
			}
		} catch (Exception e) {
			logger.error("", e);
			throw e;
		}
		return sb.toString();
	}

	/**
	 * 将map输出为String
	 * 
	 * @param map
	 * @param splitChar
	 * @return
	 */
	public static String mapToString(Map map, String splitChar) {
		StringBuffer sb = new StringBuffer("");
		if (map == null || map.size() == 0) {
			return "";
		}

		for (Object entry : map.entrySet()) {
			sb.append(entry);
			sb.append(splitChar);
		}
		String str = sb.toString();
		if (str.endsWith(splitChar)) {
			str = str.substring(0, str.lastIndexOf(splitChar));
		}

		return str;
	}

	public static String strSetToString(Set<String> set, String splitChar) throws Exception {
		StringBuffer sb = new StringBuffer("");
		if (set == null || set.size() == 0) {
			return sb.toString();
		}

		try {
			for (Iterator<String> it = set.iterator(); it.hasNext();) {
				String value = it.next();
				sb.append(value);
				if (it.hasNext()) {
					sb.append(splitChar);
				}
			}
		} catch (Exception e) {
			logger.error("", e);
			throw e;
		}
		return sb.toString();
	}

	public static List<Long> setToList(Set<Long> set) {
		List<Long> list = new ArrayList<Long>();
		if (set != null && set.size() > 0) {
			for (Iterator<Long> it = set.iterator(); it.hasNext();) {
				list.add(it.next());
			}
		}
		return list;
	}

	public static String longArrayToString(List<Long> values) {
		StringBuffer sb = new StringBuffer();
		if (values == null || values.size() == 0) {
			return sb.toString();
		}

		try {
			boolean isFirst = true;
			Set<Long> setLong = new HashSet<Long>();
			for (Iterator<Long> it = values.iterator(); it.hasNext();) {
				Long value = it.next();
				if (setLong == null || !setLong.contains(value)) {
					if (isFirst == false)
						sb.append(",");
					sb.append(value);
					isFirst = false;
				}
			}
		} catch (Exception e) {
			logger.error("", e);
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static String longArray2String(List<Long> values) {
		boolean isChangeDate = false;
		StringBuffer sb = new StringBuffer();
		if (values == null || values.size() == 0)
			return null;

		try {
			Set<Long> setLong = new HashSet<Long>();
			for (Iterator<Long> it = values.iterator(); it.hasNext();) {
				Long value = it.next();
				if (setLong == null || !setLong.contains(value)) {
					sb.append("'" + value + "',");
					setLong.add(value);
					isChangeDate = true;
				}
			}
		} catch (Exception e) {
			logger.error("", e);
			e.printStackTrace();
		}
		return isChangeDate ? sb.toString().substring(0, sb.toString().length() - 1) : sb.toString();
	}

	public static List<Long> strListToLongList(List<String> strList) throws Exception {
		List<Long> longList = new ArrayList<Long>();
		try {
			for (String str : strList) {
				longList.add(Long.valueOf(str));
			}
		} catch (Exception e) {
			logger.error("", e);
			throw e;
		}
		return longList;
	}

	public static Long stringToLong(Object value) {
		Long l;
		value = nullToString(value);
		if ("".equals(value)) {
			l = 0L;
		} else {
			try {
				l = Long.valueOf(value.toString());
			} catch (Exception e) {
				logger.error("", e);
				l = 0L;
			}
		}
		return l;
	}

	/**
	 * 判断字符串是否是整数
	 */
	public static boolean isInteger(String value) {
		try {
			Integer.parseInt(value.trim());
			return true;
		} catch (NumberFormatException e) {
			logger.error("", e);
			return false;
		}
	}

	public static int parseInteger(String value) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			logger.error("", e);
			return 0;
		}
	}

	/**
	 * 判断字符串是否是浮点数
	 */
	public static boolean isDouble(String value) {
		try {
			Double d = Double.parseDouble(value.trim());
			String tempD = d.toString();
			if (tempD.contains("."))
				return true;
			return false;
		} catch (NumberFormatException e) {
			logger.error("", e);
			return false;
		}
	}

	/**
	 * 判断字符串是否是数字
	 */
	public static boolean isNumber(String value) {
		if (value == null || StringUtil.isNullStr(value))
			return false;
		return isInteger(value) || isDouble(value);
	}

	/**
	 * 判断字符串是否布尔
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isBoolean(String value) {
		try {
			Boolean b = Boolean.parseBoolean(value);
			return true;
		} catch (Exception e) {
			logger.error("", e);
			return false;
		}
	}

	/** 判断是否为时间 * */
	public static boolean isDate(String value) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			sdf.parse(value);
			return true;
		} catch (NumberFormatException e) {
			logger.error("", e);
			return false;
		}
	}

	/**
	 * 
	 * 中文转换--文章换行的转换
	 * 
	 * @param str
	 * 
	 * @return
	 */

	public static String getText(String str) {
		if (str == null)
			return ("");
		if (str.equals(""))
			return ("");
		// 建立一个StringBuffer来处理输入数据
		StringBuffer buf = new StringBuffer(str.length() + 6);
		char ch = '\n';
		for (int i = 0; i < str.length(); i++) {
			ch = str.charAt(i);
			if (ch == '\r') {
				buf.append(" ");
			} else if (ch == '\n') {
				buf.append(" ");
			} else if (ch == '\t') {
				buf.append("    ");
			} else if (ch == ' ') {
				buf.append(" ");
			} else if (ch == '\'') {
				buf.append("\\'");
			} else if (ch == '<') {
				buf.append("\\<");
			} else if (ch == '>') {
				buf.append("\\>");
			} else {
				buf.append(ch);
			}
		}
		return buf.toString();
	}

	// 清除特殊字符
	public static String getescapeText(String str) {
		if (str == null)
			return ("");
		if (str.equals(""))
			return ("");
		// 建立一个StringBuffer来处理输入数据
		StringBuffer buf = new StringBuffer(str.length() + 6);
		char ch = '\n';
		for (int i = 0; i < str.length(); i++) {
			ch = str.charAt(i);
			if (ch == '\r') {
				buf.append("");
			} else if (ch == '\n') {
				buf.append("");
			} else if (ch == '\t') {
				buf.append("");
			} else if (ch == ' ') {
				buf.append("");
			} else if (ch == '\'') {
				buf.append("");
			} else {
				buf.append(ch);
			}
		}
		return buf.toString();
	}

	/**
	 * ASCII码表第48～57号为0～9十个阿拉伯数字；65～90号为26个大写英文字母，97～122号为26个小写英文字母
	 */
	public static boolean isCharAndNum(String source) {
		StringCharacterIterator sci = new StringCharacterIterator(source);

		for (char c = sci.first(); c != StringCharacterIterator.DONE; c = sci.next()) {
			if ((c > 47 && c < 58) || (c > 64 && c < 91) || (c > 96 && c < 123))
				continue;
			else
				return false;
		}
		return true;
	}

	/**
	 * 清除所有特殊字符，只保留中英文字符和数字
	 * 
	 * @param str
	 * @return
	 */
	public static String getEscapeText(String str) {
		try {
			String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(str);
			return m.replaceAll("");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("", e);
			return null;
		}
	}

	/**
	 * 清除所有特殊字符，只保留中英文字符和数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEscapeText(String str) {
		boolean flag = false;
		try {
			String regEx = "[`~!@#$%^&*()+=|{}':;',…\\[\\].<>/?~！@#￥%…&*（）——+|{}【】‘；：”“’。，、？]";
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(str);
			if (m.find())
				flag = true;
		} catch (Exception e) {
			logger.error("", e);
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 判断字符串中是否包含除中英文字符和数字外的特殊字符，包含返回true
	 * 
	 * @param str
	 * @return
	 */
	public static boolean haveEscapeText(String str) {
		if (str.replaceAll("[\u4e00-\u9fa5]*[a-z]*[A-Z]*\\d*-*_*\\s*", "").length() == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 是否包含中文和&符号
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isContainChinese(String str) {
		if (str == null || "".equals(str))
			return false;

		String regEx = "[\u4e00-\u9fa5]|&";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		while (m.find()) {
			return true;
		}
		return false;
	}

	public static boolean isAvalidPhoneNumbers(String phoneNumbers) {
		if (phoneNumbers == null || "".equals(phoneNumbers))
			return true;

		String regEx = "^([0-9]|\\|)*$";
		if (phoneNumbers.replaceAll(regEx, "").length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * 根据转义列表对字符串进行转义(escape)。
	 * 
	 * @param source
	 *            待转义的字符串
	 * 
	 * @param escapeCharMap
	 *            转义列表
	 * 
	 * @return 转义后的字符串
	 */

	public static String escapeCharacter(String source, HashMap escapeCharMap) {

		if (source == null || source.length() == 0) {

			return source;

		}

		if (escapeCharMap.size() == 0) {

			return source;

		}

		StringBuffer sb = new StringBuffer(source.length() + 100);

		StringCharacterIterator sci = new StringCharacterIterator(source);

		for (char c = sci.first();

		c != StringCharacterIterator.DONE;

		c = sci.next()) {

			String character = String.valueOf(c);

			if (escapeCharMap.containsKey(character)) {

				character = (String) escapeCharMap.get(character);

			}

			sb.append(character);

		}

		return sb.toString();

	}

	/**
	 * 
	 * 中文转换--文章换行的转换
	 * 
	 * @param str
	 * 
	 * @return
	 */

	public static String changeEnter(String str) {
		if (str == null)
			return ("");
		if (str.equals(""))
			return ("");
		// 建立一个StringBuffer来处理输入数据
		StringBuffer buf = new StringBuffer(str.length() + 6);
		char ch = '\n';
		for (int i = 0; i < str.length(); i++) {
			ch = str.charAt(i);
			if (ch == '\r') {
				buf.append("|");
			} else if (ch == '\n') {
				buf.append("|");
			} else {
				buf.append(ch);
			}
		}
		return buf.toString();
	}

	// 截掉url左边的一级目录名,如/wap/news/index.xml -> /news/index.xml
	public static String trimLeftNode(String str) {
		if (str == null)
			return "";

		if (str.startsWith("/")) {
			int ind = str.indexOf("/", 1);
			if (ind > 0)
				return str.substring(ind);
		}
		return str;
	}

	// 截掉url右边的一级目录名,如/wap/news/index.xml -> /wap/news/
	public static String trimRightNode(String str) {
		if (str == null)
			return "";

		if (str.endsWith("/")) {
			str = str.substring(0, str.length() - 1);
		}

		int index = 0;
		if ((index = str.lastIndexOf("/")) > 0) {
			return str.substring(0, index);
		}
		return str;
	}

	public static String generatedUrl(int pageType, List<String> sourceList, String nodestr, int maxint) {
		List<String> nodeList = new ArrayList<String>();
		Random rmd = new Random();
		String rstr = "";
		Set<String> cpSet = new HashSet<String>();
		Set<Integer> distNum = new HashSet<Integer>();
		Set<String> distCp = new HashSet<String>();
		for (int i = 0; i < sourceList.size(); i++) {
			String tmpstr = sourceList.get(i);
			if (getSpstr(tmpstr, 1).equals(nodestr)) {
				nodeList.add(tmpstr);
				cpSet.add(getSpstr(tmpstr, 3));
			}
		}
		if (nodeList.size() > maxint) {
			for (int i = 0; i < maxint;) {
				int tmpint = rmd.nextInt(nodeList.size());
				String tmpstr = nodeList.get(tmpint);
				if ((distCp.add(getSpstr(tmpstr, 3)) || distCp.size() >= cpSet.size()) && distNum.add(tmpint)) {
					rstr += "<a href='" + getSpstr(tmpstr, 4) + "'>" + getSpstr(tmpstr, 2) + "</a><br/>";
					i++;
				}
			}
		} else {
			for (int i = 0; i < nodeList.size(); i++) {
				String tmpstr = nodeList.get(i);
				rstr += "<a href='" + getSpstr(tmpstr, 4) + "'>" + getSpstr(tmpstr, 2) + "</a><br/>";
			}
		}
		return rstr;
	}

	public static String getSpstr(String spstr, int level) {
		String rstr = "";
		for (int i = 0; i < level; i++) {
			if (spstr.indexOf("|*") == -1) {
				rstr = spstr;
				return rstr;
			} else {
				rstr = spstr.substring(0, spstr.indexOf("|*"));
			}
			spstr = spstr.substring(spstr.indexOf("|*") + 2, spstr.length());
		}
		return rstr;
	}

	public static String toString(Object obj) {
		try {
			return obj.toString();
		} catch (Exception e) {
			logger.error("", e);
			return "";
		}
	}

	// public static String getEncrypt(String mobile, String pid) {
	// StringBuffer buf = new StringBuffer();
	// buf.append(mobile);
	// buf.append(pid);
	// buf.append("MDN2000");
	// // buf.append(CmsConstants.SYSTEMCONFIG.get("PORTAL_MDN"));//"MDN2000"
	// String md5String = buf.toString();
	// MD5 md5 = new MD5();
	// byte[] byteone = md5String.getBytes();
	// return md5.md5Str(byteone, 0, byteone.length);
	// }

	private static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * 把byte[]数组转换成十六进制字符串表示形式
	 * 
	 * @param tmp
	 *            要转换的byte[]
	 * @return 十六进制字符串表示形式
	 */
	public static String byteToHexString(byte[] tmp) {
		if (tmp == null) {
			throw new NullPointerException();
		}
		int len = tmp.length;
		char str[] = new char[len * 2];
		int i = 0;
		for (byte b : tmp) {
			str[i * 2] = hexDigits[b >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
			str[i * 2 + 1] = hexDigits[b & 0xf]; // 取字节中低 4 位的数字转换
			i++;
		}
		return new String(str);
	}

	/**
	 * 得到一个Long值的指定长度的字符串形式
	 * 
	 * @param l
	 * @param len
	 * @return
	 */
	public static String getStringByAppointLen(Long l, int len) {
		return getStringByAppointLen(l.toString(), len, true);
	}

	/**
	 * 得到一个Integer值的指定长度的字符串形式 NOTE: 不足的前面添'0'
	 * 
	 * @param i
	 * @param len
	 * @return
	 */
	public static String getStringByAppointLen(Integer i, int len) {
		return getStringByAppointLen(i.toString(), len, true);
	}

	/**
	 * 得到一个String值的指定长度的字符串形式 NOTE: 不足的前面添'0'
	 * 
	 * @param s
	 * @param len
	 * @param cutHead
	 *            当s的长度大于len时，截取方式：true,截掉头部；否则从截掉尾部
	 *            例如getStringByAppointLen("12345",3,true) ---> "345"
	 * @return
	 */
	public static String getStringByAppointLen(String s, int len, boolean cutHead) {
		if (s == null || len <= 0) {
			s = "";
		}
		if (len > s.length()) {
			int size = len - s.length();
			StringBuffer sb = new StringBuffer();
			while (size-- > 0) {
				sb.append("0");
			}
			sb.append(s);
			return sb.toString();
		} else if (len == s.length()) {
			return s;
		} else {
			if (cutHead) {
				return s.substring(s.length() - len, s.length());
			} else {
				return s.substring(0, len);
			}
		}
	}

	/**
	 * 通过ID生成存储路径
	 * 
	 * @param id
	 * @return
	 */
	public static String getFileDirPathById(Long id) {
		String s = StringUtil.getStringByAppointLen(id, 12);
		StringBuffer sb = new StringBuffer();
		sb.append(s.substring(0, 3)).append("/").append(s.substring(3, 6)).append("/").append(s.substring(6, 9)).append("/").append(s.substring(9, 12)).append("/");
		return sb.toString();
	}

	public static Boolean string2Boolean(String str) {
		try {
			if ("0".equals(str))
				return Boolean.FALSE;
			else if ("1".equals(str))
				return Boolean.TRUE;
			else if ("false".equalsIgnoreCase(str))
				return Boolean.FALSE;
			else if ("true".equalsIgnoreCase(str))
				return Boolean.TRUE;
		} catch (Exception e) {
			logger.error("", e);
			return Boolean.FALSE;
		}
		return Boolean.FALSE;
	}

	public static String trimSqlIds(String ids) {
		String tmpIds = ids;
		if (tmpIds != null && !"".equals(tmpIds)) {
			if (tmpIds.endsWith(","))
				tmpIds = tmpIds.substring(0, tmpIds.length() - 1);
			if (tmpIds.startsWith(","))
				tmpIds = tmpIds.substring(1, tmpIds.length());
		} else
			tmpIds = "";
		return tmpIds;
	}

	public static String formatData(String tpl, double data) {
		DecimalFormat df = new DecimalFormat(tpl);
		String format = df.format(data);

		return format;
	}

	static final int GB_SP_DIFF = 160;// 国标码和区位码转换常量
	// 存放国标一级汉字不同读音的起始区位码
	static final int[] SEC_POS_VALUE_LIST = { 1601, 1637, 1833, 2078, 2274, 2302, 2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858, 4027, 4086, 4390, 4558, 4684, 4925, 5249, 5600 };
	// 存放国标一级汉字不同读音的起始区位码对应读音
	static final char[] FIRST_LETTER = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'W', 'X', 'Y', 'Z' };

	// 获取一个字符串的拼音码
	public static String firstLetter(String oriStr) {
		String str = oriStr.toLowerCase();
		StringBuffer buffer = new StringBuffer();
		char ch;
		char[] temp;
		for (int i = 0; i < str.length(); i++) { // 依次处理str中每个字符
			ch = str.charAt(i);
			temp = new char[] { ch };
			try {
				byte[] uniCode = new String(temp).getBytes("GB2312");
				if (uniCode[0] < 128 && uniCode[0] > 0) { // 非汉字
					buffer.append(temp);
				} else {
					buffer.append(convert(uniCode));
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("", e);
			}

		}
		return buffer.toString();
	}

	/**
	 * 获取一个汉字的拼音首字母。
	 * 
	 * 　　* GB码两个字节分别减去160，转换成10进制码组合就可以得到区位码
	 * 
	 * 　　* 例如汉字“你”的GB码是0xC4/0xE3，分别减去0xA0（160）就是0x24/0x43
	 * 
	 * 　　* 0x24转成10进制就是36，0x43是67，那么它的区位码就是3667，在对照表中读音为‘n’
	 * 
	 * 　　
	 */
	static char convert(byte[] bytes) {
		char result = '-';
		int secPosValue = 0;
		int i;
		for (i = 0; i < bytes.length; i++) {
			bytes[i] -= GB_SP_DIFF;
		}

		secPosValue = bytes[0] * 100 + bytes[1];
		for (i = 0; i < 23; i++) {
			if (secPosValue >= SEC_POS_VALUE_LIST[i] && secPosValue < SEC_POS_VALUE_LIST[i + 1]) {
				result = FIRST_LETTER[i];
				break;
			}
		}
		return result;
	}

	/**
	 * 在URL后面拼接参数值
	 * 
	 * @param srcUrl
	 * @param key
	 * @param value
	 * @return
	 */
	public static String addParamToUrl(String srcUrl, String key, String value) {
		String url = "";
		if (StringUtil.isNullStr(srcUrl) || srcUrl.equalsIgnoreCase("null") || StringUtil.isNullStr(key)) {
			return srcUrl;
		}

		if (srcUrl.indexOf("?") < 0) {
			url = srcUrl + "?" + key + "=" + value;
		} else {
			url = srcUrl + "&" + key + "=" + value;
		}

		return url;
	}

	/**
	 * 解析Url参数
	 * 
	 * @param url
	 * @return
	 */
	public static Map<String, String> parseUrlParams(String url) {
		Map<String, String> paramsMap = new HashMap<String, String>();
		String paramsStr = url.contains("?") ? url.substring(url.indexOf('?') + 1) : url;
		String[] params = paramsStr.split("&");

		for (int i = 0; i < params.length; i++) {
			String subStr = params[i];
			if (!StringUtil.isNullStr(subStr)) {
				String[] values = subStr.split("=");
				if (values.length == 2) {
					paramsMap.put(values[0], values[1]);
				}
			}
		}
		return paramsMap;
	}

	public static Map<String, String> convertMap(Map<String, Object> map) {
		Map<String, String> result = new HashMap<String, String>(map.size());
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			Object value = entry.getValue();
			if (value == null)
				result.put(entry.getKey(), "");
			else
				result.put(entry.getKey(), entry.getValue().toString());
		}

		return result;

	}
	/**
	 * mybatis不支持英文$符号修改，则替换成中文$符号
	 */
	public static String formatDollarSign(String val){
		if (val != null && val.contains("$")) {
			val = val.replace("$", "＄");
		}
		return val;
	}
	
	public static int[] getRandamArry(){
		int[] randamArr = new int[7];
		int[] num = new int[7];
		for (int i = 1; i <= 7; i++) {
		num[i - 1] = i;
		}
		List<Integer> list = new ArrayList<Integer>();
		for (int i : num) {
		list.add(i);
		}
		int out = 0;
		int outIndex = 0;
		for (int i = 0; i < num.length; i++) {
		Random ran = new Random();
		outIndex = ran.nextInt(list.size());
		out = list.get(outIndex);
		list.remove(outIndex);
		randamArr[i]=out;
		}

      return randamArr;
	}
	
	public static Map<String, Object> formatDollarSign(Map<String, Object> orderInfoMap){
		
		for (String key : orderInfoMap.keySet()) {
			String val=orderInfoMap.get(key)==null?"":orderInfoMap.get(key).toString();
			if (val.contains("$")) {
				val = val.replace("$", "＄");
			}
			orderInfoMap.put(key, val);
		}		
		return orderInfoMap;
	}
	
	
	public static boolean matchProvince(String str) {
		if("北京".equals(str)||"重庆".equals(str)||"上海".equals(str)||"天津".equals(str)){
			return true;
		}
		return false;
	}

    /**
     * 过滤四字节的字符或表情符号
     *
     * @param targetString 四字节字符串
     * @return 过滤后的字符串
     */
    public static String filterFourCharString(String targetString) {
//    	if(targetString==null){
//    		return targetString;
//    	}
//        byte[] b = targetString.getBytes();
//        for (int i = 0; i < b.length; i++) {
//            if ((b[i] & 0xF8) == 0xF0) {
//                for (int j = 0; j < 4; j++) {
//                    b[i + j] = 0x30;
//                }
//                i += 3;
//            }
//        }
//        targetString = new String(b);
//        return targetString.replaceAll("0000", "");

        if (StringUtils.isNotBlank(targetString)) {
            return targetString.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", "");
        } else {
            return targetString;
        }
    }

	public static String formatPhoneStr(String str, int startIndex, int endIndex) {
		if (StringUtil.isEmpty(str)) {
			return "";
		}
		if (endIndex < startIndex) {
			return str;
		}
		if (str.length() < endIndex) {
			return str;
		}
		int length = endIndex - startIndex ;
		String replaceStr = "";
		for (int index = 0; index < length; index++) {
			replaceStr += "*";
		}
		return str.substring(0, startIndex) + replaceStr + str.substring(endIndex, str.length());
	}

	/***
	 * transNo=001473148033668&merId=898350200000003 解析成map
	 * @param str
	 * @return
	 */
	public static Map<String,String> parseUrlStr2Map(String str){
		if(StringUtil.isEmpty(str)){
			return null;
		}
		Map<String,String> resultMap = new HashMap<String,String>();
		String itmeStrs[] = str.split("&");
		if(itmeStrs.length > 0 ){
		    String itemStr = null ;	    
		  for(int index=0; index<itmeStrs.length ;index++){
				itemStr = itmeStrs[index];
				int splitIndex = itemStr.indexOf("=");
				if(splitIndex == -1){
					resultMap.put(itemStr, "");
				}else{
					String childKeyStr = itemStr.substring(0, splitIndex);
					String childValueStr = itemStr.substring(splitIndex+1,itemStr.length());
					  if(!StringUtil.isEmpty(childKeyStr)){
				    	resultMap.put(childKeyStr, childValueStr);
				    }
				}
				
			}
		}
		return resultMap;
	}



	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Comparator<Object> com = Collator.getInstance(java.util.Locale.SIMPLIFIED_CHINESE);
		String[] newArray = { "中山", "汕头", "广州", "阳江", "南京", "武汉", "北京", "安阳", "北方", "安庆", "SDETV" };
		Arrays.sort(newArray, com);
		for (String i : newArray) {
			System.out.print(i + "  ");
		}

        String title = "123中A😁B?C\uD83D\uDC4CD?E文";
        System.out.println(filterFourCharString(title).replaceAll("0000", ""));
	}
}

package com.shanjin.financial.util;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;


/**
 * 序列化对象为JSON格式 遵循JSON组织公布标准
 * 
 * @date 2008/05/07
 * @version 1.0.0
 */
public class JsonUtil {
	public static final String timeFormatPattern = "yyyy-MM-dd HH:mm:ss";
	/** Commons Logging instance. */


	/**
	 * @param obj
	 *            任意对象
	 * @return String
	 */
	public static String object2json(Object obj) {
		StringBuilder json = new StringBuilder();
		if (obj == null) {
			json.append("\"\"");
		} else if (obj instanceof String || obj instanceof Integer
				|| obj instanceof Float || obj instanceof Boolean
				|| obj instanceof Short || obj instanceof Double
				|| obj instanceof Long || obj instanceof BigDecimal
				|| obj instanceof BigInteger || obj instanceof Byte) {
			json.append("\"").append(string2json(obj.toString())).append("\"");
		} else if (obj instanceof Object[]) {
			json.append(array2json((Object[]) obj));
		} else if (obj instanceof List) {
			json.append(list2json((List<?>) obj));
		} else if (obj instanceof Map) {
			json.append(map2json((Map<?, ?>) obj));
		} else if (obj instanceof Set) {
			json.append(set2json((Set<?>) obj));
		} else if(obj instanceof Date){
			return "\""+DateUtil.formatDate(timeFormatPattern,(Date)obj)+"\"";
		}else {
			json.append(bean2json(obj));
		}
		return json.toString();
	}

	/**
	 * @param bean
	 *            bean对象
	 * @return String
	 */
	public static String bean2json(Object bean) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		PropertyDescriptor[] props = null;
		try {
			props = Introspector.getBeanInfo(bean.getClass(), Object.class)
					.getPropertyDescriptors();
		} catch (IntrospectionException e) {
		}
		if (props != null) {
			for (int i = 0; i < props.length; i++) {
				try {
					String name = object2json(props[i].getName());
					String value = object2json(props[i].getReadMethod().invoke(
							bean));
					json.append(name);
					json.append(":");
					json.append(value);
					json.append(",");
				} catch (Exception e) {
				}
			}
			json.setCharAt(json.length() - 1, '}');
		} else {
			json.append("}");
		}
		return json.toString();
	}
	
	
//	public static String bean2jsonNoSet(Object bean, String beanName) {
//		StringBuilder json = new StringBuilder();
//		json.append("{");
//		PropertyDescriptor[] props = null;
//		try {
//			props = Introspector.getBeanInfo(bean.getClass(), Object.class)
//					.getPropertyDescriptors();
//		} catch (IntrospectionException e) {
//		}
//		if (props != null) {
//			for (int i = 0; i < props.length; i++) {
//				try {
//					String name = object2jsonNoSet(beanName + "."
//							+ props[i].getName());
//					String value = object2jsonNoSet(props[i].getReadMethod().invoke(
//							bean));
//					json.append(name);
//					json.append(":");
//					json.append(value);
//					json.append(",");
//				} catch (Exception e) {
//				}
//			}
//			json.setCharAt(json.length() - 1, '}');
//		} else {
//			json.append("}");
//		}
//		return json.toString();
//	}
	
	/**
	 * @param bean
	 *            bean对象
	 * @return String
	 */
//	public static String bean2jsonNoSet(Object bean) {
//		StringBuilder json = new StringBuilder();
//		json.append("{");
//		PropertyDescriptor[] props = null;
//		try {
//			props = Introspector.getBeanInfo(bean.getClass(), Object.class)
//					.getPropertyDescriptors();
//		} catch (IntrospectionException e) {
//		}
//		if (props != null) {
//			for (int i = 0; i < props.length; i++) {
//				try {
//					String name = object2jsonNoSet(props[i].getName());
//					String value = object2jsonNoSet(props[i].getReadMethod().invoke(
//							bean));
//					json.append(name);
//					json.append(":");
//					json.append(value);
//					json.append(",");
//				} catch (Exception e) {
//				}
//			}
//			json.setCharAt(json.length() - 1, '}');
//		} else {
//			json.append("}");
//		}
//		return json.toString();
//	}
	
	/**
	 * @param list
	 *            list对象
	 * @return String
	 */
//	public static String list2jsonNoSet(List<?> list) {
//		StringBuilder json = new StringBuilder();
//		json.append("[");
//		if (list != null && list.size() > 0) {
//			for (Object obj : list) {
//				json.append(object2jsonNoSet(obj));
//				json.append(",");
//			}
//			json.setCharAt(json.length() - 1, ']');
//		} else {
//			json.append("]");
//		}
//		return json.toString();
//	}
	
//	public static String object2jsonNoSet(Object obj) {
//		StringBuilder json = new StringBuilder();
//		if (obj == null) {
//			json.append("\"\"");
//		} else if (obj instanceof String || obj instanceof Integer
//				|| obj instanceof Float || obj instanceof Boolean
//				|| obj instanceof Short || obj instanceof Double
//				|| obj instanceof Long || obj instanceof BigDecimal
//				|| obj instanceof BigInteger || obj instanceof Byte) {
//			json.append("\"").append(string2json(obj.toString())).append("\"");
//		} else if (obj instanceof Object[]) {
//			json.append("\"").append("").append("\"");
//		} else if (obj instanceof List) {
//			json.append("\"").append("").append("\"");
//		} else if (obj instanceof HashSet) {
//			json.append("\"").append("").append("\"");
//		} else if (obj instanceof PersistentSet) {
//			json.append("\"").append("").append("\"");
//		} else if (obj instanceof Set) {
//			json.append("\"").append("").append("\"");
//		} else if(obj instanceof Date){
//			return "\""+DateUtil.formatDate(timeFormatPattern,(Date)obj)+"\"";
//		}else {
//			json.append(bean2jsonNoSet(obj));
//		}
//		return json.toString();
//	}
	/**
	 * @param bean
	 *            bean对象 beanName 对象名称
	 * @return String
	 */
	public static String bean2json(Object bean, String beanName) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		PropertyDescriptor[] props = null;
		try {
			props = Introspector.getBeanInfo(bean.getClass(), Object.class)
					.getPropertyDescriptors();
		} catch (IntrospectionException e) {
		}
		if (props != null) {
			for (int i = 0; i < props.length; i++) {
				try {
					String name = object2json(beanName + "."
							+ props[i].getName());
					String value = object2json(props[i].getReadMethod().invoke(
							bean));
					json.append(name);
					json.append(":");
					json.append(value);
					json.append(",");
				} catch (Exception e) {
				}
			}
			json.setCharAt(json.length() - 1, '}');
		} else {
			json.append("}");
		}
		return json.toString();
	}

	/**
	 * @param list
	 *            list对象
	 * @return String
	 */
	public static String list2json(List<?> list) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				json.append(object2json(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	/**
	 * @param array
	 *            对象数组
	 * @return String
	 */
	public static String array2json(Object[] array) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (array != null && array.length > 0) {
			for (Object obj : array) {
				json.append(object2json(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	/**
	 * @param map
	 *            map对象
	 * @return String
	 */
	public static String map2json(Map<?, ?> map) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		if (map != null && map.size() > 0) {
			for (Object key : map.keySet()) {
				json.append(object2json(key));
				json.append(":");
				json.append(object2json(map.get(key)));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, '}');
		} else {
			json.append("}");
		}
		return json.toString();
	}

	/**
	 * @param set
	 *            集合对象
	 * @return String
	 */
	public static String set2json(Set<?> set) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (set != null && set.size() > 0) {
			for (Object obj : set) {
				json.append(object2json(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	/**
	 * @param s
	 *            参数
	 * @return String
	 */
	public static String string2json(String s) {
		if (s == null)
			return "";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			switch (ch) {
			case '"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '/':
				sb.append("\\/");
				break;
			default:
				if (ch >= '\u0000' && ch <= '\u001F') {
					String ss = Integer.toHexString(ch);
					sb.append("\\u");
					for (int k = 0; k < 4 - ss.length(); k++) {
						sb.append('0');
					}
					sb.append(ss.toUpperCase());
				} else if (ch != '\u200b') { //双字节空白符，有些手机没发渲染
					sb.append(ch);
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * @param s
	 *            参数(加上单引号)
	 * @return String
	 */
	public static String str2json(String s) {
		if (s == null)
			return "";
		StringBuilder sb = new StringBuilder();
		boolean isLeftQuote = true;
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			switch (ch) {
			case '\'':
				// 单引号，全部改为半角引号
				if(isLeftQuote){
					sb.append("‘");
					isLeftQuote = false;
				}else{
					sb.append("’");
					isLeftQuote = true;
				}
//				sb.append("\\\'");
				break;
			case '"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
//			case '/':
//				sb.append("\\/");
//				break;
			default:
				if (ch >= '\u0000' && ch <= '\u001F') {
					String ss = Integer.toHexString(ch);
					sb.append("\\u");
					for (int k = 0; k < 4 - ss.length(); k++) {
						sb.append('0');
					}
					sb.append(ss.toUpperCase());
				} else if (ch != '\u200b') {//双字节空白符，有些手机没发渲染
					sb.append(ch);
				}
			}
		}
		return sb.toString();
	}
	
	
	public static String simpleMap2Json(Map<String,String> rawData,String keyFlag,String valueFlag){
        StringBuilder sb = new StringBuilder();		
        sb.append("{data:[");
        for(Object oneKey : rawData.keySet()){
        	sb.append("{'"+keyFlag+"':'"+oneKey+"','"+valueFlag+"':'"+rawData.get(oneKey)+"'},");
        }
        if(sb.lastIndexOf(",") != -1){sb.deleteCharAt(sb.lastIndexOf(",") );}  //去掉最后一个逗号
        sb.append("]}");
		return sb.toString();
	}
	
	public static String createSuccesJson(){
		return "{success:true,message:''}";
	}
	
	public static String createSuccesJson(String message){
		return "{success:true,message:'"+message+"'}";
	}
	
	public static String createFailureJson(String message){
		return "{success:false,message:'"+message+"'}";
	}
	
	public static String createFailureJson(){
		return "{success:false}";
	}
}
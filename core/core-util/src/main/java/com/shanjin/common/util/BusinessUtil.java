package com.shanjin.common.util;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.outServices.aliOss.AliOssUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

public class BusinessUtil {
	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(BusinessUtil.class);
	
	private static String localIp="";
	
	//单实例中 日志ID生成器
	private static AtomicLong LOG_ID = new AtomicLong(System.currentTimeMillis());
	
	static {
	   try {
			 localIp= Inet4Address.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
	 }
	
	

	/** 处理单个文件路径,补全路径 */
	public static void disposePath(Map<String, Object> mapInfo, String... args) {
		if (mapInfo == null) {
			return;
		}
		for (int i = 0; i < args.length; i++) {
			if (mapInfo.containsKey(args[i])) {
				if (mapInfo.get(args[i]) != null) {
					String tempPath = String.valueOf(mapInfo.get(args[i]));
					if (tempPath != null && tempPath.length() > 0) {
						// tempPath = ServletUtil.getBasePath() + tempPath;
						if (Constant.USE_ALIOSS)
							tempPath = AliOssUtil.getViewUrl(tempPath);
						else
							tempPath = Constant.NGINX_PATH + tempPath;
					}
					mapInfo.put(args[i], tempPath);
				}
			}
		}
	}

	public static String disposeImagePath(String originalPath) {
			if (Constant.USE_ALIOSS)
			   return 	AliOssUtil.getViewUrl(originalPath);
			else
				return Constant.NGINX_PATH + originalPath;
	}

	/** 处理多个文件路径,补全路径 （多文件路径以逗号分割的形式存在于一个项目中） */
	public static void disposeManyPath(Map<String, Object> mapInfo, String... args) {
		if (mapInfo == null) {
			return;
		}
		for (int i = 0; i < args.length; i++) {
			if (mapInfo.containsKey(args[i])) {
				if (mapInfo.get(args[i]) != null) {
					String tempPath = String.valueOf(mapInfo.get(args[i]));
					if (tempPath != null && tempPath.length() > 0) {
						if (tempPath.contains(Constant.COMMA_EN)) {
							String[] paths = tempPath.split(Constant.COMMA_EN);
							StringBuffer sb = new StringBuffer();
							for (String path : paths) {
								if (Constant.USE_ALIOSS)
									sb.append(AliOssUtil.getViewUrl(path)).append(Constant.COMMA_EN);
								else
									sb.append(Constant.NGINX_PATH + path).append(Constant.COMMA_EN);
							}
							mapInfo.put(args[i], sb.substring(0, sb.length() - 1));
						} else {
							disposePath(mapInfo, args[i]);
						}
					}
				}
			}
		}
	}
	
	public static void disposeManyPath(List<Map<String,Object>> infos,String... args) {
		
		 if (infos==null || infos.size()<1)
			 	return;
		 for (Map<String,Object> info:infos){
			 disposeManyPath(info,args);
		 }
		
	}
	

	/** 格式化银行卡号 */
	public static void formatCardNo(Map<String, Object> mapInfo, String... args) {
		if (mapInfo == null) {
			return;
		}
		for (int i = 0; i < args.length; i++) {
			if (mapInfo.containsKey(args[i])) {
				String cardNo = String.valueOf(mapInfo.get(args[i]));
				if (cardNo != null && cardNo.length() > 0) {
					cardNo = cardNo.replaceAll("([\\d]{4})(?=\\d)", "$1 ");
				}
				mapInfo.put(args[i], cardNo);
			}
		}
	}

	/** 计算分页场合的总页面数 */
	public static int totalPageCalc(int count) {
		return (int) Math.ceil(count / (double) Constant.PAGESIZE);
	}

	/** 去除最后一个逗号 */
	public static String deleteLastComma(String str) {
		return StringUtils.isEmpty(str) ? Constant.EMPTY : str.substring(0, str.length() - 1);
	}

	/** 去除最后一个逗号 */
	public static StringBuffer deleteLastComma(StringBuffer str) {
		if (str.toString().endsWith(",")) {
			return new StringBuffer(StringUtils.isEmpty(str) ? Constant.EMPTY : str.substring(0, str.length() - 1));
		} else {
			return str;
		}
	}

	/** 文件上传 */
	public static String fileUpload(MultipartFile uploadFile, String filePath) {
		// 相对路径
		String path = null;
		// 上传文件的文件全名（包含文件后缀名）
		String fileName = uploadFile.getOriginalFilename();
		String suffix = fileName.substring(fileName.lastIndexOf("."));
		fileName = UUID.randomUUID().toString() + suffix;

		// 文件在磁盘上的真实路径
		String realPath = ServletUtil.getServletContext().getRealPath(filePath);
		// 判断文件夹是否存在,如果不存在则创建文件夹
		File file = new File(realPath);
		if (!file.exists()) {
			file.mkdir();
		}
		File realfile = null;
		try {
			// 存储到数据库的相对路径(包括文件名及后缀名)
			path = new StringBuffer(filePath).append(fileName).toString();

			if (Constant.USE_ALIOSS) {
				AliOssUtil.upload(filePath,fileName,uploadFile.getInputStream());
				deleteFile(realPath);
			}else{
				if (FTPUtil.uploadFile(filePath, fileName, uploadFile.getInputStream())) {
					deleteFile(realPath);
				} 
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			deleteFile(realfile);
			path = Constant.EMPTY;
			logger.error("", ex);
		}
		return path;
	}

	/**
	 * 删除阿里OSS服务上的某个文件
	 * @param fullPath   含文件名的全路径
	 */
	public static void deleteOssFile(String fullPath){
		AliOssUtil.delete(fullPath);
		
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

	/** 生成验证码 */
	public static String createVerificationCode(String phone) {
		Integer randomCode;
		if (phone.startsWith(Constant.TESTPHONE)) {
			randomCode = 123456;
		} else {
			if (Constant.DEVMODE) {
				randomCode = 123456;
			} else {
				randomCode = (int) ((Math.random() * 9 + 1) * 100000);
			}
		}
		return randomCode.toString();
	}

	/** 发送验证码 */
	/** 手机号 验证码 类型（1-短信 2-语言） */
	public static JSONObject sendVerificationCode(String phone, String verificationCode, Integer type) {
		JSONObject smsResult = new ResultJSONObject("000", "验证码已经发送,请耐心等候");
		if (phone.startsWith(Constant.TESTPHONE)) {
		} else {
			if (Constant.DEVMODE) {
			} else {
				if (type == 1) {
					smsResult = SmsUtil.sendSms(phone, "验证码为【" + verificationCode + "】，如非本人操作，请忽略本短信。本验证码在30分钟内有效。");
				} else if (type == 2) {
					smsResult = SmsUtil.sendVoice(phone, verificationCode);
				}
			}
		}
		return smsResult;
	}
	
	/** 发送验证码 */
	/** 手机号 验证码 类型（1-短信 2-语言） */
	/**  content 短信内容*/
	public static JSONObject sendVerificationCode(String phone, String verificationCode, Integer type,String content) {
		JSONObject smsResult = new ResultJSONObject("000", "验证码已经发送,请耐心等候");
		if(StringUtil.isNullStr(content)){
			// 短信内容为空,默认
			content =  "验证码为【" + verificationCode + "】，如非本人操作，请忽略本短信。本验证码在30分钟内有效。";
		}
		if (phone.startsWith(Constant.TESTPHONE)) {
		} else {
			if (Constant.DEVMODE) {
			} else {
				if (type == 1) {
					smsResult = SmsUtil.sendSms(phone, content);
				} else if (type == 2) {
					smsResult = SmsUtil.sendVoice(phone, verificationCode);
				}
			}
		}
		return smsResult;
	}
	

	/** 发送支付密码 */
	public static boolean sendPayPassword(String phone, String payPassword) {
		boolean isSuccess = false;
		if(!Constant.DEVMODE){
			JSONObject smsResult = SmsUtil.sendSms(phone, "支付密码【" + payPassword + "】，请第一时间修改你的支付密码。如非本人操作，请联系客服。");
			if (StringUtil.null2Str(smsResult.get("resultCode")).equals("000")) {
				isSuccess = true;
			} else {
				isSuccess = false;
			}
		}		
		return isSuccess;
	}

	public static List<String> phoneList() {
		List<String> phoneList = new ArrayList<String>();
		phoneList.add("14000000000");
		phoneList.add("14000000001");
		phoneList.add("14000000002");
		phoneList.add("14000000003");
		phoneList.add("14000000004");
		phoneList.add("14000000005");
		phoneList.add("14000000006");
		phoneList.add("14000000007");
		phoneList.add("14000000008");
		phoneList.add("14000000009");
		return phoneList;
	}

	/**
	 * 筛选返回的数据
	 */
	public static Map<String, Object> getReturnValues(String[] returnValueKeys, Map<String, Object> orderText) {

		Map<String, Object> map = new LinkedHashMap<String, Object>();

		if (orderText == null || orderText.values().size() == 0) {
			return map;
		}

		for (int i = 0; i < returnValueKeys.length; i++) {
			for (String key : orderText.keySet()) {
				if (key.equals(returnValueKeys[i])) {
					map.put(key, orderText.get(key));
				}
			}
		}
		return map;
	}

	/** 多级服务类型 **/
	public static List<Map<String, Object>> handlerMultilevelServiceType(List<Map<String, Object>> serviceTypeList) {

		List<Map<String, Object>> childsList = getChildsServiceType(serviceTypeList, null);

		return childsList;

	}

	/** 多级服务类型 **/
	public static List<Map<String, Object>> getChildsServiceType(List<Map<String, Object>> serviceTypeList, Object parentId) {
		List<Map<String, Object>> childsList_ = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : serviceTypeList) {
			Map<String, Object> serviceMap1 = new LinkedHashMap<String, Object>();
			Object parentId_ = map.get("parentId");

			if ((parentId == null && parentId_ == null) || (parentId != null && parentId_ != null && parentId_.toString().equals(parentId.toString()))) {
				serviceMap1.put("id", map.get("id"));
				serviceMap1.put("serviceTypeName", map.get("serviceTypeName"));
				serviceMap1.put("parentId", map.get("parentId"));
				serviceMap1.put("chose", map.get("chose"));
				serviceMap1.put("serviceTypeList", getChildsServiceType(serviceTypeList, map.get("id")));
				childsList_.add(serviceMap1);
			}
		}
		return childsList_;
	}

	/**
	 * 多级字典
	 */
	public static List<Map<String, Object>> handlerMultistageDict(List<Map<String, Object>> dictList) {

		List<Map<String, Object>> childsList = getChildsDict(dictList, null);

		return childsList;

	}

	/**
	 * 多级字典
	 */
	public static List<Map<String, Object>> getChildsDict(List<Map<String, Object>> dictList, Object parentId) {
		List<Map<String, Object>> childsList_ = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : dictList) {
			Map<String, Object> serviceMap1 = new LinkedHashMap<String, Object>();
			Object parentId_ = map.get("parentDictId");
			String path = map.get("path") == null ? "" : map.get("path").toString();
			if (!path.toString().equals("")) {
				if (path.contains("selected:")) {
					if (Constant.USE_ALIOSS){
							path = path.replace("selected:", "selected:" + AliOssUtil.getCDNPrefix());
					}else{
							path = path.replace("selected:", "selected:" + Constant.NGINX_PATH);
					}
				}
				if (path.contains("image:")) {
					if (Constant.USE_ALIOSS){
							path = path.replace("image:", "image:" + AliOssUtil.getCDNPrefix());
					}else{
							path = path.replace("image:", "image:" + Constant.NGINX_PATH);
					}
				}

			}
			if ((parentId == null && parentId_ == null) || (parentId != null && parentId_ != null && parentId_.toString().equals(parentId.toString()))) {
				serviceMap1.put("id", map.get("id"));
				serviceMap1.put("dictType", map.get("dictType"));
				serviceMap1.put("dictKey", map.get("dictKey"));
				serviceMap1.put("dictValue", map.get("dictValue"));
				serviceMap1.put("parentDictId", map.get("parentDictId"));
				serviceMap1.put("path", path);
				serviceMap1.put("dictList", getChildsDict(dictList, map.get("id")));
				childsList_.add(serviceMap1);
			}
		}
		return childsList_;
	}

	public static List<Map<String, Object>> getChildsServiceTypeForUser(List<Map<String, Object>> serviceList, Object parentId) {
		List<Map<String, Object>> childsList_ = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : serviceList) {
			Map<String, Object> serviceMap1 = new LinkedHashMap<String, Object>();
			Object parentId_ = map.get("parentId");

			if (parentId == null || String.valueOf(parentId_).equals(String.valueOf(parentId))) {
				serviceMap1.put("id", map.get("id"));
				serviceMap1.put("serviceName", map.get("serviceName"));
				serviceMap1.put("serviceType", map.get("serviceType"));
				serviceMap1.put("path", map.get("path"));
				serviceMap1.put("parentId", map.get("parentId"));
				serviceMap1.put("isLeaves", map.get("isLeaves"));
				serviceMap1.put("serviceList", getChildsServiceTypeForUser(serviceList, map.get("serviceType")));
				childsList_.add(serviceMap1);
			}
		}
		return childsList_;
	}

	public static Map<String,String> getShowIconMap(List<Map<String, Object>> listShowIcon){
		Map<String,String> map= new HashMap<String,String> ();
		for(Map<String, Object> mapShowIcon:listShowIcon){
			Object serviceTypeId=mapShowIcon.get("serviceTypeId");
			Object path=mapShowIcon.get("path");
			if(serviceTypeId!=null && path!=null){
				map.put(serviceTypeId.toString(), path.toString());
			}
		}
		return map;
	};
	/**
	 * 从缓存中获取图片信息
	 * @param results
	 * @param orderIcon_map 
	 * @throws
	 */
	@SuppressWarnings({"unchecked" })
	public static void getImageInfoFromCache(List<Map<String, Object>> results,Map<String, String> orderIcon_map){
		for(Map<String,Object> map:results){
			String id=map.get("id")==null?"":map.get("id").toString();
			String path=orderIcon_map.get(id);
			map.put("path", path);
			disposeManyPath(map, "path");//处理图片路径
			List<Map<String, Object>> serviceList=(List<Map<String, Object>>)map.get("serviceList");
			getImageInfoFromCache(serviceList, orderIcon_map);
		}
	}

	public static String addSpace(String item) {

		if (item.length() < 4) {
			char[] arrays = item.toCharArray();
			if (item.length() == 2) {
				item = arrays[0] + "                                    " + arrays[1];
			}
			if (item.length() == 3) {
				item = arrays[0] + "   " + arrays[1] + "   " + arrays[2];
			}
		}

		return item;
	}

	/** 递归形式处理单个文件路径,补全路径 */
	public static void disposePathForRecursion(List<Map<String, Object>> mapListInfo, String childMapListName, String... args) {
		if (mapListInfo == null) {
			return;
		}
		for (Map<String, Object> map : mapListInfo) {
			BusinessUtil.disposeManyPath(map, args);
			List<Map<String, Object>> childResults = (List<Map<String, Object>>) map.get(childMapListName);
			disposePathForRecursion(childResults, childMapListName, args);
		}
	}

	/**
	 * 对省市做特殊处理
	 * 
	 * @param province
	 * @param city
	 * @return
	 */
	public static String[] handlerProvinceAndCity(String province, String city) {
		String[] provinceAndCity = new String[2];

		if (province == null) {
			province = "";
		}
		if (city == null) {
			city = "";
		}

		if (province.endsWith("省") || province.endsWith("市")) {
			province = province.substring(0, province.length() - 1);
		}
		if (city.endsWith("市") || city.endsWith("区")) {
			city = city.substring(0, city.length() - 1);
		}

		if (province.contains("宁夏")) {
			province = "宁夏";
		}
		if (province.contains("广西")) {
			province = "广西";
		}
		if (province.contains("内蒙古")) {
			province = "内蒙古";
		}
		if (province.contains("西藏")) {
			province = "西藏";
		}
		if (province.contains("新疆")) {
			province = "新疆";
		}
		if (province.contains("香港")) {
			province = "香港";
			city = "香港";
		}
		if (province.contains("澳门")) {
			province = "澳门";
			city = "澳门";
		}
		provinceAndCity[0] = province;
		provinceAndCity[1] = city;
		return provinceAndCity;
	}

	/**
	 * 判断是否要充值送钱
	 */
	public static String registerGiveMoney(Map<String, Object> moneyMap) {
		if (moneyMap == null) {
			return "0";
		}
		String money = moneyMap.get("config_value") == null ? "0" : moneyMap.get("config_value").toString();
		String startDate = moneyMap.get("standby_field1") == null ? "0" : moneyMap.get("standby_field1").toString();
		String endDate = moneyMap.get("standby_field2") == null ? "0" : moneyMap.get("standby_field2").toString();

		if (StringUtils.isNotEmpty(money) && StringUtils.isNotEmpty(startDate) && StringUtils.isNotEmpty(endDate)) {
			// 判断是否在有效期内
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String nowDate = sdf.format(new Date());
			int startDateInt = Integer.parseInt(startDate.replace("-", ""));
			int endDateInt = Integer.parseInt(endDate.replace("-", ""));
			int nowDateInt = Integer.parseInt(nowDate.replace("-", ""));
			if (startDateInt > nowDateInt || nowDateInt > endDateInt) {// 不在有效期内
				money = "0";
			}
		} else {
			money = "0";
		}
		return money;
	}

	/**
	 * 判断是否要赠送员工
	 */
	public static String registerGiveEmployeesNum(Map<String, Object> employeesMap) {
		if (employeesMap == null) {
			return "0";
		}
		String employeesNum = employeesMap.get("config_value") == null ? "0" : employeesMap.get("config_value").toString();
		String startDate = employeesMap.get("standby_field1") == null ? "0" : employeesMap.get("standby_field1").toString();
		String endDate = employeesMap.get("standby_field2") == null ? "0" : employeesMap.get("standby_field2").toString();

		if (StringUtils.isNotEmpty(employeesNum) && StringUtils.isNotEmpty(startDate) && StringUtils.isNotEmpty(endDate)) {
			// 判断是否在有效期内
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String nowDate = sdf.format(new Date());
			int startDateInt = Integer.parseInt(startDate.replace("-", ""));
			int endDateInt = Integer.parseInt(endDate.replace("-", ""));
			int nowDateInt = Integer.parseInt(nowDate.replace("-", ""));
			if (startDateInt > nowDateInt || nowDateInt > endDateInt) {// 不在有效期内
				employeesNum = "0";
			}
		} else {
			employeesNum = "0";
		}
		return employeesNum;
	}

	/**
	 * 根据IP查询省市
	 * 
	 * @param ip
	 * @return
	 */
	public static String[] getProvinceAndCityByIp(String ip) {
		String[] provinceAndCity = new String[2];
		// {"content":{"point":{"y":"29.15949412","x":"119.95720242"},"address":"浙江省","address_detail":{"street":"","province":"浙江省","street_number":"","city_code":29,"district":"","city":""}},"status":0,"address":"CN|浙江|None|None|OTHER|0|0"}
		String province = "";
		String city = "";
		if (StringUtils.isNotEmpty(ip)) {
			JSONObject jsonObjectIp = IPutil.getIpLocationBySina(ip);
			if (jsonObjectIp != null) {
				province = (String) jsonObjectIp.get("province");
				city = (String) jsonObjectIp.get("city");
			} else {
				jsonObjectIp = IPutil.getIpLocationByBaidu(ip);
				if (jsonObjectIp != null) {
					int status = (int) jsonObjectIp.get("status");
					if (status == 0) {
						JSONObject content = (JSONObject) jsonObjectIp.get("content");
						JSONObject address_detail = (JSONObject) content.get("address_detail");
						province = (String) address_detail.get("province");
						city = (String) address_detail.get("city");
					}
				}
			}

		}
		provinceAndCity[0] = province == null ? "" : province;
		provinceAndCity[1] = city == null ? "" : city;
		return provinceAndCity;
	}

	/**
	 * 将Map中为空的移除
	 * 
	 * @param orderText
	 *            void
	 * @throws
	 */
	public static void removeNullValueFromMap(Map<String, Object> orderText) {
		if (orderText != null) {
			Iterator<Entry<String, Object>> it = orderText.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Object> entry = it.next();
				String key = entry.getKey();
				Object value = orderText.get(key);
				if (value == null || value.toString().equals("null") || value.toString().equals("NULL") || value.toString().trim().equals("") || value.toString().lastIndexOf(":") + 1 == value.toString().trim().length()) {
					it.remove();
				} else {
					String[] strs = null;
					if (value.toString().contains("：")) {
						strs = value.toString().split("：");
					} else {
						if (value.toString().contains(":")) {
							logger.error("error：" + value + " 包含英文冒号");
							strs = value.toString().split(":");
						}else{
							logger.error("error：此行没有冒号 "+value.toString());
							it.remove();
							continue;
						}
					}
					if (strs.length == 1) {
						it.remove();
					} else if (strs.length == 2){
						if (strs[1].trim().equals("null") || strs[1].trim().equals("NULL") || strs[1].trim().equals("")) {
							it.remove();
						}
					}else{
						logger.info("----------------------------订单详情将值为空的移除："+value.toString());
					}
				}
			}
		}
	}

	

	/**
	 * 易学堂 订单详情数据处理
	 * 
	 * @param orderText
	 *            void
	 * @throws
	 */
	public static void yxtOrderTextHandler(Map<String, Object> orderText) {
		String serviceType = orderText.get("serviceType") == null ? "" : orderText.get("serviceType").toString();
		String serviceItem = orderText.get("serviceItem") == null ? "" : orderText.get("serviceItem").toString();
		String serviceClass = orderText.get("serviceClass") == null ? "" : orderText.get("serviceClass").toString();

		String serviceTypeValue = serviceType.split("：")[1].replace(" ", "");

		if (serviceItem.equals("")) {
			return;
		}

		String serviceItemKey = serviceItem.split("：")[0];
		String serviceItemValue = serviceItem.split("：")[1];
		if (serviceTypeValue.length() == 2) {// 一级2个字，则作为二级标题+“类型”，改为4个字
			serviceItemKey = serviceTypeValue + "类型";
		} else if (serviceTypeValue.length() == 3) {// 一级3个字，则每个字之间加2个空格，作为二级标题
			serviceItemKey = serviceTypeValue.substring(0, 1) + "  " + serviceTypeValue.substring(1, 2) + "  " + serviceTypeValue.substring(2, 3);
		} else {
			serviceItemKey = serviceTypeValue;
		}
		serviceItem = serviceItemKey + "：" + serviceItemValue;
		orderText.put("serviceItem", serviceItem.trim());

		if (serviceClass.equals("")) {
			return;
		}

		String serviceClassKey = serviceClass.split("：")[0];
		String serviceClassValue = serviceClass.split("：")[1];
		if (serviceItemValue.length() == 2) {// 二级2个字，则作为三级标题+“类型”，改为4个字
			serviceClassKey = serviceItemValue + "类型";
		} else if (serviceItemValue.length() == 3) {// 二级3个字，则每个字之间加2个空格，作为三级标题
			serviceClassKey = serviceItemValue.substring(0, 1) + "  " + serviceItemValue.substring(1, 2) + "  " + serviceItemValue.substring(2, 3);
		} else {
			serviceClassKey = serviceItemValue;
		}
		serviceClass = serviceClassKey + "：" + serviceClassValue;
		orderText.put("serviceClass", serviceClass);

		// 如果二级和三级相同，则移除三级
		if (serviceItemValue.equals(serviceClassValue)) {
			orderText.remove("serviceClass");
		}
	}

	/*public static void writeSqlLog(String nowDate, String content) {
		String os = System.getProperty("os.name");
		String path = "";
		if (os.toLowerCase().contains("windows")) {
			path = "c:/logs/sqlLog/";
		} else if (os.toLowerCase().contains("linux")) {
			path = "/mnt/logs/sqlLog/";
		}
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
		path = path + "sql_"+nowDate + ".log";
		writeFile(path, content);
	}
	public static void writeInterfaceLog(String content) {
		String os = System.getProperty("os.name");
		String path = "";
		if (os.toLowerCase().contains("windows")) {
			path = "c:/logs/interfaceLog/";
		} else if (os.toLowerCase().contains("linux")) {
			path = "/mnt/logs/interfaceLog/";
		}
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
		String nowDate=DateUtil.getNowYYYYMMDD();
		path = path + "interface_"+nowDate + ".log";
		writeFile(path, content);
	}
	public static void writeExceptionLog(String content) {
		String os = System.getProperty("os.name");
		String path = "";
		if (os.toLowerCase().contains("windows")) {
			path = "c:/logs/exceptionLog/";
		} else if (os.toLowerCase().contains("linux")) {
			path = "/mnt/logs/exceptionLog/";
		}
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
		String nowDate=DateUtil.getNowYYYYMMDD();
		path = path + "exception_"+nowDate + ".log";
		writeFile(path, content);
	}

	public static void writePushLog(String content) {
		String os = System.getProperty("os.name");
		String path = "";
		if (os.toLowerCase().contains("windows")) {
			path = "c:/logs/pushLog/";
		} else if (os.toLowerCase().contains("linux")) {
			path = "/mnt/logs/pushLog/";
		}
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
		String nowDate=DateUtil.getNowYYYYMMDD();
		path = path + "push_"+nowDate + ".log";
		writeFile(path, content);
	}
	
	
	public static void writeTransoutException(String content) {
		String os = System.getProperty("os.name");
		String path = "";
		if (os.toLowerCase().contains("windows")) {
			path = "c:/logs/transoutLog/";
		} else if (os.toLowerCase().contains("linux")) {
			path = "/mnt/logs/transoutLog/";
		}
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
		String nowDate=DateUtil.getNowYYYYMMDD();
		path = path + "transout_"+nowDate + ".log";
		writeFile(path, content);
	}*/
	public static void writeLog(String fileName,String content) {
		String os = System.getProperty("os.name");
		String path = "";
		if (os.toLowerCase().contains("windows")) {
			path = "c:/logs/"+fileName+"Log/";
		} else if (os.toLowerCase().contains("linux")) {
			path = "/mnt/logs/"+fileName+"Log/";
		}
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
		String nowDate=DateUtil.getNowYYYYMMDD();
		path = path + fileName+"_"+nowDate + ".log";
		writeFile(path, content);
	}
	public static void writeFile(String path, String content) {
		FileWriter fw=null;
		try {
			fw = new FileWriter(path,true);
			fw.write(content+"\n");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(fw!=null){
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 保存文件，获取文件路径
	 * */
	public static String saveOrderFile(MultipartFile file, String type) {
		String refilePath = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		String filePath = "/upload/userInfo/" + type + "/order/" + sdf.format(new Date()) + "/";
		refilePath = fileUpload(file, filePath);
		return refilePath;
	}
	public static String saveFeedbackFile(MultipartFile file, int customerType) {
		String refilePath = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		String filePath = "/upload/feedback/";
		if(customerType==1){//商户
			filePath+= "merchant/";
		}
		if(customerType==2){//用户
			filePath+= "user/";
		}
		filePath+= sdf.format(new Date()) + "/";
		refilePath = fileUpload(file, filePath);
		return refilePath;
	}
	public static String getIpAddress() {
		return localIp;
	}
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Real-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if(ip.contains(",")){
			ip = ip.split(",")[0];
		}
		return ip;
	}
	
	public static String getLogId(){
		 return LOG_ID.incrementAndGet()+"";
	}
	
	public static void handlerOrderServiceTime(List<Map<String, Object>> cachedOrders){
		for(Map<String, Object> map : cachedOrders){
			String[] value=getServiceTimeInfo(map);
			map.put("isPastDate", value[0]);
			map.put("serviceTimeStr", value[1]);
//			map.put("orderStatus", value[2]);
		}
		
	}
	public static void handlerOrderServiceTime(Map<String, Object> map){
		String[] value=getServiceTimeInfo(map);
		map.put("isPastDate", value[0]);
		map.put("serviceTimeStr", value[1]);
//		map.put("orderStatus", value[2]);
	}
	public static String[] getServiceTimeInfo(Map<String, Object> map){
		String[] value=new String[3];
		String isPastDate="0";
		String str="";
		long nowTime_long=0;
		String nowTime=DateUtil.getNowYYYYMMDDHHMM();
		String overdue=map.get("overdue")==null?"":map.get("overdue").toString();
//		int orderStatus=Integer.parseInt(map.get("orderStatus")==null?"0":map.get("orderStatus").toString());
//		if(orderStatus!=1){
//			value[0]=isPastDate;
//			value[1]=str;
//			value[2]=orderStatus+"";
//			return value;
//		}
		if(!overdue.equals("")){
			overdue=overdue.replace(" 00:00", "");
			double days=0;
			long serviceDate_long=Long.parseLong(overdue.replace("-", "").replace(" ", "").replace(":", ""));
			String[] overdueTimeArray=overdue.split(" ");
			if(overdueTimeArray.length==1){//服务时间 yyyy-MM-dd
				//判断是否过期
				nowTime_long=Long.parseLong(nowTime.split(" ")[0].replace("-", ""));
				days=DateUtil.getDaysBetweenDAY1andDAY2(nowTime, overdue);
				
			}else if(overdueTimeArray.length==2){//服务时间 yyyy-MM-dd HH:mm
				nowTime_long=Long.parseLong(nowTime.replace("-", "").replace(" ", "").replace(":", ""));
				String nowTime_=nowTime.split(" ")[0];
				String serviceTime_=overdueTimeArray[0];
				if(nowTime_.equals(serviceTime_)){//日期相同，但是时间不同
					if(nowTime_long > serviceDate_long){
						days=-0.5;//过期
					}else{
						days=0;
					}
				}else{
					days=DateUtil.getDaysBetweenDAY1andDAY2(nowTime, overdue);					
				}
			}else{
				
			}
			if(nowTime_long > serviceDate_long){
				isPastDate="1";//过期
			}else{
				isPastDate="0";
			}
		
			//获得预约天数			
			if(days == 0){
				str="预约今天";
			}else if(days == 1){
				str="预约明天";
//			}else if(days > 1){
//				str="预约"+(int)days+"天后";
//			}else if(days < 0 ){
//				str="已过期";
//			}else if(days <= -1){
//				str="已过期"+(int)days*-1+"天";
			}else{
				
			}
			
			
		}else{
			//logger.error("用户端查询订单列表：serviceTime="+serviceTime);
		}
		value[0]=isPastDate;
		value[1]=str;
//		value[2]=orderStatus+"";
		return value;
	}
	@SuppressWarnings("deprecation")
	public static boolean isPastDateofServiceTime(Map<String, Object> orderInfo){
		if(orderInfo.get("serviceTimeStamp")==null){
			return true;
		}
		String overdue=StringUtil.null2Str(orderInfo.get("overdue"));
		if(StringUtil.isEmpty(overdue)){
			return true;
		}
		String format="yyyy/MM/dd HH:mm";
		if(overdue.contains(" 00:00")){
			format="yyyy/MM/dd";
		}
		long serviceTimeLong=(Long)orderInfo.get("serviceTimeStamp");
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		String nowDate=sdf.format(new Date());
		Date date=new Date();
		long nowDateLong=Long.parseLong((date.parse(nowDate)+"").substring(0,10));
		if(serviceTimeLong < nowDateLong){
			return false;
		}
		return true;
	}
	public static String formatServiceTime(Date serviceTime,String appType,String serviceType){
		String format="";
		if(appType.contains("ams_")){
			if(appType.equals("ams_szx") ){
				format=DateUtil.DATE_YYYY_MM_DD_PATTERN;
			}else{
				format=DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN;
			}
		}else if(appType.contains("cbt_")){
			if(serviceType.equals("6") || serviceType.equals("8")   ){
				format=DateUtil.DATE_YYYY_MM_DD_PATTERN;
			}else{
				format=DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN;
			}
		}else if(appType.equals("dgf")){
			format=DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN;			
		}else if(appType.equals("fyb")){
			format=DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN;			
		}else if(appType.equals("gxfw")){
			format=DateUtil.DATE_YYYY_MM_DD_PATTERN;			
		}else if(appType.equals("hyt")){
			format=DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN;			
		}else if(appType.contains("hz_")){
			format=DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN;			
		}else if(appType.contains("jrj_")){
			format=DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN;			
		}else if(appType.equals("jzx")){
			if(serviceType.equals("4")){
				format=DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN;	
			}else{
				format=DateUtil.DATE_YYYY_MM_DD_PATTERN;	
			}		
		}else if(appType.equals("lxz")){
			format=DateUtil.DATE_YYYY_MM_DD_PATTERN;			
		}else if(appType.equals("mst")){
			format=DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN;			
		}else if(appType.equals("qzy")){
			format=DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN;			
		}else if(appType.equals("swg")){
			format=DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN;			
		}else if(appType.equals("sxd")){
			format=DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN;			
		}else if(appType.equals("syp")){
			if(serviceType.equals("5")){
				format=DateUtil.DATE_YYYY_MM_DD_PATTERN;	
			}else{
				format=DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN;	
			}			
		}else if(appType.equals("ts")){
			format=DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN;			
		}else if(appType.equals("xhf")){
			format=DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN;			
		}else if(appType.equals("xlb")){
			format=DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN;			
		}else if(appType.contains("yd_")){
			format=DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN;			
		}else if(appType.equals("ydc")){
			format=DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN;			
		}else if(appType.equals("ydh")){
			format=DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN;			
		}else if(appType.contains("yxt_")){
			format=DateUtil.DATE_YYYY_MM_DD_PATTERN;			
		}else if(appType.equals("zdf")){
			format=DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN;			
		}else if(appType.equals("zsy")){
			format=DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN;			
		}else if(appType.contains("zyb_")){
			format=DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN;			
		}else if(appType.equals("zyd")){
			format=DateUtil.DATE_YYYY_MM_DD_PATTERN;			
		}else{
			format=DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN;
		}
		return DateUtil.formatDate(format, serviceTime);
	}
	public static String getAddress(Map<String,Object> paramMap,Map<String,Object> orderInfo){
		String flag=paramMap.get("flag")==null?"":paramMap.get("flag").toString();
		String orderStatus=paramMap.get("orderStatus")==null?"":paramMap.get("orderStatus").toString();
		String address=orderInfo.get("address")==null?"":orderInfo.get("address").toString();
		String detailAddress=orderInfo.get("detailAddress")==null?"":orderInfo.get("detailAddress").toString();
		if(flag.equals("1")){//用户端查看订单详情
			return address+detailAddress;
		}
		if(orderStatus.equals("0") || orderStatus.equals("1") || orderStatus.equals("6") || orderStatus.equals("7")){
			return address+"***";
		}
		return address+detailAddress;
		
	};
	public static void main(String[] args) {
		
		
		
	}

	/** 金额统一保持两位小数，不足补0 */
	public static String moneyFormat(BigDecimal money) {
		DecimalFormat myformat = new DecimalFormat("#.00");
		return myformat.format(money);
	}

	public static String calcDistance(Double long1, Double lat1, Double long2, Double lat2) {
		String formatDistance = null;
		if (StringUtil.isNotEmpty(long1) && StringUtil.isNotEmpty(lat1) && StringUtil.isNotEmpty(long2) && StringUtil.isNotEmpty(lat2)) {
			// 如果不为空则查询距离
			Double distance = LocationUtil.getDistance(long1, lat1, long2, lat2);
			Long distanceValue = Math.round(distance);
			if (distanceValue >= 1000) {
				BigDecimal bd = new BigDecimal(distance).divide(new BigDecimal(1000));
				bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
				formatDistance = "距离" + bd + "公里";
			} else {
				formatDistance = "距离" + Math.round(distance) + "米";
			}
		} else {
			// 值为-1的时候代表无经纬度数据，无法计算距离
			formatDistance = "-1";
		}
		return formatDistance;
	}
	
    /**
     * 判断当前日期是星期几
     * 返回值dayForWeek
     * 1 星期一
     * 2 星期二
     * 3 星期三
     * 4 星期四
     * 5 星期五
     * 6 星期六
     * 7 星期日
     */
	public static int dayForWeek(Date time) throws Exception {
		Calendar c = Calendar.getInstance();
		c.setTime(time);
		int dayForWeek = 0;
		if(c.get(Calendar.DAY_OF_WEEK) == 1){
			dayForWeek = 7;
		}else{
			dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
		}
		return dayForWeek;
	}
	
	public static boolean handlerAppType(String appType){
		boolean result=true;
		if(appType.equals("cbt_ytc")){
			result=false;
		}else if(appType.equals("zyb_kbj") || appType.equals("zyb_kxy")){
			result=false;
		}else if(appType.equals("yxt_xqb") || appType.equals("yxt_cgt") || appType.equals("yxt_kk")){
			result=false;
		}else if(appType.equals("hz_amj") || appType.equals("hz_ss")){
			result=false;
		}else if(appType.equals("yd_xxb")){
			result=false;
		}
		return result;
	}
	/**
	 * 判断是否要充值送钱
	 */
	public static boolean isNotPastTime(double money,String startTime,String endTime) {		
		boolean bool=true;
		if (money!=0 && StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
			// 判断是否在有效期内
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String nowDate = sdf.format(new Date());
			int startDateInt = Integer.parseInt(startTime.replace("-", ""));
			int endDateInt = Integer.parseInt(endTime.replace("-", ""));
			int nowDateInt = Integer.parseInt(nowDate.replace("-", ""));
			if (startDateInt > nowDateInt || nowDateInt > endDateInt) {// 不在有效期内
				bool=false;
			}
		} else {
			bool=false;
		}
		return bool;
	}

    /**
     * 判断企业认证类型 1-企业认证 2-个人认证 0-没有认证
     *
     * @param mapList Map List
     * @param i 数值
     */
    public static void judgeAuth(List<Map<String, Object>> mapList, int i) {
        Object objEA = mapList.get(i).get("enterpriseAuth");
        int intEnterpriseAuth = null == objEA ? 0 : Integer.parseInt(objEA.toString());

        Object objPA = mapList.get(i).get("personalAuth");
        int intPersonalAuth = null == objPA ? 0 : Integer.parseInt(objPA.toString());

        if (intEnterpriseAuth > 0) {
            mapList.get(i).put("auth", 1); // 企业认证
        } else if (intPersonalAuth > 0) {
            mapList.get(i).put("auth", 2); // 个人认证
        } else {
            mapList.get(i).put("auth", 0); // 没有认证
        }
    }

    /**
     * 校验服务时间是否过期
     * @return
     * @throws Exception
     */
    public static boolean checkServiceTime(Map<String,Object> map) throws Exception {
        boolean flag = false;
        String overdue = StringUtil.null2Str(map.get("overdue"));
        if(overdue.equals("")){
        	flag = false;//默认不过期 
        }
        String nowDate="";
        String format="yyyy-MM-dd HH:mm";
        if(overdue.contains(" 00:00")){//说明是不带时分的服务时间
            format="yyyy-MM-dd";
        }
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        nowDate=sdf.format(new Date());
        //String serviceTime = "2016-04-01 17:00:00";
        if(!StringUtil.isNullStr(overdue)){
            long st = StrToDate(overdue,format).getTime();//逾期时间
            long nt = StrToDate(nowDate,format).getTime();
            if(nt>st){
                // 当前时间 大于逾期时间，过期
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 字符串转换成日期
     *
     * @param str
     * @return date
     */
    public static Date StrToDate(String str,String formatStr) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    
    /**
     * 获取订单发布后距离现在的时间
     * @param joinTime
     * @return
     */
    public static String getReleaseTimeByOrder(String joinTime){
    	String releaseToNowTime="";
    	long joinTimeMillis=DateUtil.getMillisByDate(joinTime,"yyyy-MM-dd HH:mm:ss");
        long nowTimeMillis=System.currentTimeMillis();
        long surplusMillis=nowTimeMillis-joinTimeMillis;
        
        if(surplusMillis<0){//服务器时间不一致，导致可能出现负数
        	return "0分钟前发布";
        }
        if( surplusMillis< 1000*60*60){
        	releaseToNowTime=(surplusMillis/(1000*60))+"分钟前发布";
        }else if(surplusMillis >= 1000*60*60 && surplusMillis <= 1000*60*60*24){
        	releaseToNowTime=(surplusMillis/(1000*60*60))+"小时前发布";
        }else if( surplusMillis > 1000*60*60*24){
        	releaseToNowTime=(surplusMillis/(1000*60*60*24))+"天前发布";
        }else{
        	releaseToNowTime="";
        }
        return releaseToNowTime;
    }

	public static Boolean readNetFile(String imageUrl,String targetPath){
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
	        //保存到文件
			File targetFile = new File(targetPath);
	         FileOutputStream out = new FileOutputStream(targetFile);
	         out.write(arr);
	         out.close();
			// 读图像
			/*Image image = null;
			ByteArrayInputStream in = new ByteArrayInputStream(buf.toByteArray());
			image = ImageIO.read(in);
			in.close();*/
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return true;
	}

	/**
	 * Deletes all files and subdirectories under "dir".
	 * @param dir Directory to be deleted
	 * @return boolean Returns "true" if all deletions were successful.
	 *                 If a deletion fails, the method stops attempting to
	 *                 delete and returns "false".
	 */
	public static boolean deleteDir(File dir) {

		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i=0; i<children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
				logger.info("delete file "+dir.getPath()+" "+children[i]);
			}
		}
		logger.info("delete dir "+dir.getPath());
		// The directory is now empty so now it can be smoked
		return dir.delete();
	}
}

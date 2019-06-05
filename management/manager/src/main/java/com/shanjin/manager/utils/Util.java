package com.shanjin.manager.utils;

import com.shanjin.manager.Bean.AppInfo;
import com.shanjin.sso.bean.SystemUserInfo;
import com.shanjin.manager.constant.Constant;
import com.shanjin.manager.job.MerchantOrderJob;
import com.shanjin.manager.time.StatisticalUtil;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.web.util.HtmlUtils;

public class Util
{
  public static String getFilter(Map<String, String[]> strFilter, Map<String, String[]> intFilter)
  {
    StringBuffer filter = new StringBuffer();
    for (Map.Entry entry : strFilter.entrySet()) {
      if ((entry.getValue() != null) && (((String[])entry.getValue()).length > 0) && (!((String[])entry.getValue())[0].equals("")) && (!((String[])entry.getValue())[0].equals("null"))) {
        filter.append((String)entry.getKey() + "='" + escapeCharacter(((String[])entry.getValue())[0].trim()) + "' and ");
      }
    }

    for (Map.Entry entry : intFilter.entrySet()) {
      if ((entry.getValue() != null) && (((String[])entry.getValue()).length > 0) && (!((String[])entry.getValue())[0].equals("")) && (!((String[])entry.getValue())[0].equals("null"))) {
        filter.append((String)entry.getKey() + "=" + Long.parseLong(((String[])entry.getValue())[0]) + " and ");
      }
    }
    filter.append(" 0=0 ");
    return filter.toString();
  }

  public static String escapeCharacter(String str) {
    String o1 = "'";
    return str.replace(o1, "\\'");
  }

  public static String escapeCharacterFliter(String str) {
    String o1 = "'";
    String o2 = "_";
    return str.replace(o1, "\\'").replace(o2, "\\_");
  }

  public static String getLikeFilter(Map<String, String[]> strFilter) {
    StringBuffer filter = new StringBuffer();
    for (Map.Entry entry : strFilter.entrySet()) {
      if ((entry.getValue() != null) && (((String[])entry.getValue()).length > 0) && (!((String[])entry.getValue())[0].equals(""))) {
        filter.append(" and " + (String)entry.getKey() + " like '%" + escapeCharacterFliter(((String[])entry.getValue())[0].trim()) + "%'");
      }
    }

    return filter.toString();
  }

  public static String getdateFilter(String key, String[] startTime, String[] offTime) {
    StringBuffer filter = new StringBuffer();
    if ((startTime != null) && (startTime.length > 0) && (!startTime[0].equals(""))) {
      filter.append(" and " + key + ">'" + startTime[0] + "'");
      if ((offTime != null) && (offTime.length > 0) && (!offTime[0].equals(""))) {
        filter.append(" and " + key + "<'" + offTime[0] + "'");
      }
    }
    else if ((offTime != null) && (offTime.length > 0) && (!offTime[0].equals(""))) {
      filter.append(" and " + key + "<'" + offTime[0] + "'");
    }

    return filter.toString();
  }

  public static String getStatisticFilter(Map<String, String[]> paramMap, String[] dimension)
  {
    Map strFilter = new HashMap();
    Map intFilter = new HashMap();
    StringBuffer sql = new StringBuffer();
    if (StringUtil.nullToBoolean(dimension).booleanValue()) {
      sql.append(" having ");
      for (String s : dimension) {
        String key = "m." + s;
        if ((s.equals("order_status")) || (s.equals("clientFlag"))) {
          intFilter.put(key, (String[])paramMap.get(s));
        }
        strFilter.put(key, (String[])paramMap.get(s));
      }
      sql.append(getFilter(strFilter, intFilter));
    }

    return sql.toString();
  }
  public static String getStatisticUserFilter(Map<String, String[]> paramMap, String[] dimension) {
    Map strFilter = new HashMap();
    Map intFilter = new HashMap();
    StringBuffer sql = new StringBuffer();
    if (StringUtil.nullToBoolean(dimension).booleanValue()) {
      sql.append(" having ");
      for (String s : dimension) {
        String key = "u." + s;
        if ((s.equals("clientType")) || (s.equals("clientFlag"))) {
          intFilter.put(key, (String[])paramMap.get(s));
        }
        strFilter.put(key, (String[])paramMap.get(s));
      }
      sql.append(getFilter(strFilter, intFilter));
    }

    return sql.toString();
  }
  public static void main(String[] args) {
    Map strfilter = new HashMap();
    Map intfilter = new HashMap();
    String[] str = { "1" };
    String[] in = { "ww" };
    intfilter.put("oi.order_id", str);
    intfilter.put("oi.user_id", str);
    intfilter.put("oi.order_status", str);
    intfilter.put("oi.order_status", str);
    strfilter.put("oi.app_type", in);
    strfilter.put("oi.join_time", in);
    System.out.println(getFilter(strfilter, intfilter));
  }

  public static Map<String, String> sortMap(String sort) {
    Map map = null;
    if ((sort != null) && (!"".equals(sort))) {
      map = new HashMap();
      try {
        Object json = new JSONTokener(sort).nextValue();
        if ((json instanceof JSONObject)) {
          JSONObject jsonObject = (JSONObject)json;
          map.put("property", jsonObject.getString("property"));
          map.put("direction", jsonObject.getString("direction"));
        } else if ((json instanceof JSONArray)) {
          JSONArray jsonArray = (JSONArray)json;
          if ((jsonArray != null) && (jsonArray.length() > 0)) {
            JSONObject jo = jsonArray.getJSONObject(0);
            map.put("property", jo.getString("property"));
            map.put("direction", jo.getString("direction"));
          }
        }
      }
      catch (JSONException e) {
        e.printStackTrace();
        return map;
      }
    }
    return map;
  }

  public static Map<String, String[]> getParams(Map<String, String[]> param, HttpSession session) {
    SystemUserInfo user = (SystemUserInfo)session.getAttribute("_user");
    if (user.getStr("userName").equals(Constant.ADMIN)) {
      return param;
    }
    Map filterParam = new HashMap();
    filterParam.put("userType", (String[])param.get("userType"));
    filterParam.put("realName", (String[])param.get("realName"));
    filterParam.put("phone", (String[])param.get("phone"));
    filterParam.put("start_time", (String[])param.get("start_time"));
    filterParam.put("off_time", (String[])param.get("off_time"));
    filterParam.put("province", (String[])param.get("province"));
    filterParam.put("city", (String[])param.get("city"));
    filterParam.put("appType", (String[])param.get("appType"));
    filterParam.put("id", (String[])param.get("id"));
    filterParam.put("start", (String[])param.get("start"));
    filterParam.put("limit", (String[])param.get("limit"));
    filterParam.put("page", (String[])param.get("page"));
    filterParam.put("sort", (String[])param.get("sort"));
    int userType = user.getInt("userType").intValue();
    if (userType == 2) {
      String[] province = new String[1];
      province[0] = user.getLong("province").toString();
      filterParam.remove("province");
      filterParam.put("province", province);
    } else if (userType == 3) {
      String[] province = new String[1];
      String[] city = new String[1];
      province[0] = user.getLong("province").toString();
      city[0] = user.getLong("city").toString();
      filterParam.remove("province");
      filterParam.remove("city");
      filterParam.put("province", province);
      filterParam.put("city", city);
    } else if (userType == 4) {
      String[] id = new String[1];
      id[0] = user.getLong("id").toString();
      filterParam.remove("id");
      filterParam.put("id", id);
    }
    return filterParam;
  }

  public static Map<String, String[]> getMerchantParams(Map<String, String[]> param, HttpSession session)
  {
    SystemUserInfo user = (SystemUserInfo)session.getAttribute("_user");
    if (user.getStr("userName").equals(Constant.ADMIN)) {
      return param;
    }
    Map filterParam = new HashMap();
    filterParam.put("start", (String[])param.get("start"));
    filterParam.put("limit", (String[])param.get("limit"));
    filterParam.put("page", (String[])param.get("page"));
    filterParam.put("name", (String[])param.get("name"));
    filterParam.put("start_time", (String[])param.get("start_time"));
    filterParam.put("off_time", (String[])param.get("off_time"));
    filterParam.put("telephone", (String[])param.get("telephone"));
    filterParam.put("app_type", (String[])param.get("app_type"));
    filterParam.put("province", (String[])param.get("province"));
    filterParam.put("city", (String[])param.get("city"));
    filterParam.put("agentId", (String[])param.get("agentId"));
    filterParam.put("auth_type", (String[])param.get("auth_type"));
    filterParam.put("auth_status", (String[])param.get("auth_status"));
    filterParam.put("invite_code", (String[])param.get("invite_code"));
    filterParam.put("sort", (String[])param.get("sort"));
    int userType = user.getInt("userType").intValue();
    if (userType == 2) {
      String[] province = new String[1];
      province[0] = user.getStr("provinceDesc");
      filterParam.remove("province");
      filterParam.put("province", province);
    } else if (userType == 3) {
      String[] province = new String[1];
      String[] city = new String[1];
      province[0] = user.getStr("provinceDesc");
      city[0] = user.getStr("cityDesc");
      filterParam.remove("province");
      filterParam.remove("city");
      filterParam.put("province", province);
      filterParam.put("city", city);
    } else if (userType == 4) {
      String[] province = new String[1];
      String[] city = new String[1];
      province[0] = user.getStr("provinceDesc");
      city[0] = user.getStr("cityDesc");
      filterParam.remove("province");
      filterParam.remove("city");
      filterParam.put("province", province);
      filterParam.put("city", city);
      String[] agentId = new String[1];
      agentId[0] = user.getLong("id").toString();
      filterParam.remove("agentId");
      filterParam.put("agentId", agentId);
    }
    return filterParam;
  }

  public static Map<String, String[]> getWithDrawParams(Map<String, String[]> param, HttpSession session)
  {
    SystemUserInfo user = (SystemUserInfo)session.getAttribute("_user");
    if (user.getStr("userName").equals(Constant.ADMIN)) {
      return param;
    }
    Map filterParam = new HashMap();
    filterParam.put("start", (String[])param.get("start"));
    filterParam.put("limit", (String[])param.get("limit"));
    filterParam.put("page", (String[])param.get("page"));
    filterParam.put("merchants_name", (String[])param.get("merchants_name"));
    filterParam.put("start_time", (String[])param.get("start_time"));
    filterParam.put("off_time", (String[])param.get("off_time"));
    filterParam.put("withdraw_status", (String[])param.get("withdraw_status"));
    filterParam.put("province", (String[])param.get("province"));
    filterParam.put("city", (String[])param.get("city"));
    filterParam.put("agentId", (String[])param.get("agentId"));
    filterParam.put("sort", (String[])param.get("sort"));
    int userType = user.getInt("userType").intValue();
    if (userType == 2) {
      String[] province = new String[1];
      province[0] = user.getStr("provinceDesc");
      filterParam.remove("province");
      filterParam.put("province", province);
    } else if (userType == 3) {
      String[] province = new String[1];
      String[] city = new String[1];
      province[0] = user.getStr("provinceDesc");
      city[0] = user.getStr("cityDesc");
      filterParam.remove("province");
      filterParam.remove("city");
      filterParam.put("province", province);
      filterParam.put("city", city);
    } else if (userType == 4) {
      String[] province = new String[1];
      String[] city = new String[1];
      province[0] = user.getStr("provinceDesc");
      city[0] = user.getStr("cityDesc");
      filterParam.remove("province");
      filterParam.remove("city");
      filterParam.put("province", province);
      filterParam.put("city", city);
      String[] agentId = new String[1];
      agentId[0] = user.getLong("id").toString();
      filterParam.remove("agentId");
      filterParam.put("agentId", agentId);
    }
    return filterParam;
  }

  public static Map<String, String[]> getOrderParams(Map<String, String[]> param, HttpSession session)
  {
    SystemUserInfo user = (SystemUserInfo)session.getAttribute("_user");
    if (user.getStr("userName").equals(Constant.ADMIN)) {
      return param;
    }
    Map filterParam = new HashMap();
    filterParam.put("start", (String[])param.get("start"));
    filterParam.put("limit", (String[])param.get("limit"));
    filterParam.put("page", (String[])param.get("page"));
    filterParam.put("order_id", (String[])param.get("order_id"));
    filterParam.put("phone", (String[])param.get("phone"));
    filterParam.put("merchant_phone", (String[])param.get("merchant_phone"));
    filterParam.put("merchant_name", (String[])param.get("merchant_name")); 
    filterParam.put("order_pay_type", (String[])param.get("order_pay_type"));  
    filterParam.put("app_type", (String[])param.get("app_type"));
    
    filterParam.put("purchase_status", (String[])param.get("purchase_status"));
    filterParam.put("start_time", (String[])param.get("start_time"));

    filterParam.put("off_time", (String[])param.get("off_time"));
    filterParam.put("service_type_name", (String[])param.get("service_type_name"));

    filterParam.put("province", (String[])param.get("province"));
    filterParam.put("city", (String[])param.get("city"));
    filterParam.put("agentId", (String[])param.get("agentId"));
    filterParam.put("sort", (String[])param.get("sort"));
    int userType = user.getInt("userType").intValue();
    if (userType == 2) {
      String[] province = new String[1];
      province[0] = user.getStr("provinceDesc");
      filterParam.remove("province");
      filterParam.put("province", province);
    } else if (userType == 3) {
      String[] province = new String[1];
      String[] city = new String[1];
      province[0] = user.getStr("provinceDesc");
      city[0] = user.getStr("cityDesc");
      filterParam.remove("province");
      filterParam.remove("city");
      filterParam.put("province", province);
      filterParam.put("city", city);
    } else if (userType == 4) {
      String[] province = new String[1];
      String[] city = new String[1];
      province[0] = user.getStr("provinceDesc");
      city[0] = user.getStr("cityDesc");
      filterParam.remove("province");
      filterParam.remove("city");
      filterParam.put("province", province);
      filterParam.put("city", city);
      String[] agentId = new String[1];
      agentId[0] = user.getLong("id").toString();
      filterParam.remove("agentId");
      filterParam.put("agentId", agentId);
    }
    return filterParam;
  }
  
  public static Map<String, String[]> getStaticsParams(Map<String, String[]> param, HttpSession session)
  {
    SystemUserInfo user = (SystemUserInfo)session.getAttribute("_user");
    if (user.getStr("userName").equals(Constant.ADMIN)) {
      return param;
    }
    Map filterParam = new HashMap();
    filterParam.put("start", (String[])param.get("start"));
    filterParam.put("limit", (String[])param.get("limit"));
    filterParam.put("page", (String[])param.get("page"));
   
    filterParam.put("start_time", (String[])param.get("start_time"));
    filterParam.put("off_time", (String[])param.get("off_time"));
    
    filterParam.put("order_no", (String[])param.get("order_no"));
    filterParam.put("province", (String[])param.get("province"));
    filterParam.put("city", (String[])param.get("city"));
    filterParam.put("sort", (String[])param.get("sort"));
    int userType = user.getInt("userType").intValue();
    if (userType == 2) {
      String[] province = new String[1];
      province[0] = user.getStr("provinceDesc");
      filterParam.remove("province");
      filterParam.put("province", province);
    } else if (userType == 3) {
      String[] province = new String[1];
      String[] city = new String[1];
      province[0] = user.getStr("provinceDesc");
      city[0] = user.getStr("cityDesc");
      filterParam.remove("province");
      filterParam.remove("city");
      filterParam.put("province", province);
      filterParam.put("city", city);
    } else if (userType == 4) {
      String[] province = new String[1];
      String[] city = new String[1];
      province[0] = user.getStr("provinceDesc");
      city[0] = user.getStr("cityDesc");
      filterParam.remove("province");
      filterParam.remove("city");
      filterParam.put("province", province);
      filterParam.put("city", city);
    }
    return filterParam;
  }
  
  public static Map<String, String[]> getStaticsInstallParams(Map<String, String[]> param, HttpSession session)
  {
    SystemUserInfo user = (SystemUserInfo)session.getAttribute("_user");
    if (user.getStr("userName").equals(Constant.ADMIN)) {
      return param;
    }
    Map filterParam = new HashMap();
    filterParam.put("start", (String[])param.get("start"));
    filterParam.put("limit", (String[])param.get("limit"));
    filterParam.put("page", (String[])param.get("page"));
    filterParam.put("invite_code", (String[])param.get("invite_code"));
    filterParam.put("start_time", (String[])param.get("start_time"));
    filterParam.put("off_time", (String[])param.get("off_time"));
    filterParam.put("e_start_time", (String[])param.get("e_start_time"));
    filterParam.put("e_off_time", (String[])param.get("e_off_time"));
    
    filterParam.put("province", (String[])param.get("province"));
    filterParam.put("city", (String[])param.get("city"));
    filterParam.put("sort", (String[])param.get("sort"));
    int userType = user.getInt("userType").intValue();
    if (userType == 2) {
      String[] province = new String[1];
      province[0] = String.valueOf(user.getLong("province"));
      filterParam.remove("province");
      filterParam.put("province", province);
    } else if (userType == 3) {
      String[] province = new String[1];
      String[] city = new String[1];
      province[0] = String.valueOf(user.getLong("province"));
      city[0] = String.valueOf(user.getLong("city"));
      filterParam.remove("province");
      filterParam.remove("city");
      filterParam.put("province", province);
      filterParam.put("city", city);
    } else if (userType == 4) {
      String[] province = new String[1];
      String[] city = new String[1];
      province[0] = String.valueOf(user.getLong("province"));
      city[0] = String.valueOf(user.getLong("city"));
      filterParam.remove("province");
      filterParam.remove("city");
      filterParam.put("province", province);
      filterParam.put("city", city);
    }
    return filterParam;
  }
  
  public static String getStr(String intStr) {
    StringBuffer ss = new StringBuffer();
    if (intStr.equals("")) {
      return "";
    }
    String[] s = intStr.split(",");

    for (int i = 0; i < s.length; i++) {
      ss.append("'");
      ss.append(s[i]);
      ss.append("',");
    }

    return ss.substring(0, ss.length() - 1).toString();
  }

  public static Map<String, String[]> getAgentCharge(Map<String, String[]> param, HttpSession session)
  {
    SystemUserInfo user = (SystemUserInfo)session.getAttribute("_user");
    if (user.getStr("userName").equals("admin")) {
      return param;
    }
    Map filterParam = new HashMap();
    filterParam.put("start", (String[])param.get("start"));
    filterParam.put("limit", (String[])param.get("limit"));
    filterParam.put("page", (String[])param.get("page"));
    filterParam.put("charge_type", (String[])param.get("charge_type"));
    filterParam.put("userType", (String[])param.get("userType"));
    filterParam.put("order_id", (String[])param.get("order_id"));
    filterParam.put("agent_name", (String[])param.get("agent_name"));
    filterParam.put("head_name", (String[])param.get("head_name"));

    filterParam.put("order_status", (String[])param.get("order_status"));
    filterParam.put("start_time", (String[])param.get("start_time"));
    filterParam.put("off_time", (String[])param.get("off_time"));
    filterParam.put("province", (String[])param.get("province"));
    filterParam.put("city", (String[])param.get("city"));
    filterParam.put("agentId", (String[])param.get("agentId"));
    filterParam.put("sort", (String[])param.get("sort"));
    int userType = user.getInt("userType").intValue();
    if (userType == 2) {
      String[] province = new String[1];
      province[0] = user.getStr("provinceDesc");
      filterParam.remove("province");
      filterParam.put("province", province);
    } else if (userType == 3) {
      String[] province = new String[1];
      String[] city = new String[1];
      province[0] = user.getStr("provinceDesc");
      city[0] = user.getStr("cityDesc");
      filterParam.remove("province");
      filterParam.remove("city");
      filterParam.put("province", province);
      filterParam.put("city", city);
    } else if (userType == 4) {
      String[] province = new String[1];
      String[] city = new String[1];
      province[0] = user.getStr("provinceDesc");
      city[0] = user.getStr("cityDesc");
      filterParam.remove("province");
      filterParam.remove("city");
      filterParam.put("province", province);
      filterParam.put("city", city);
      String[] agentId = new String[1];
      agentId[0] = user.getLong("id").toString();
      filterParam.remove("agentId");
      filterParam.put("agentId", agentId);
    }
    return filterParam;
  }

  public static Map<String, String[]> getStoreAuditParams(Map<String, String[]> param, HttpSession session)
  {
    SystemUserInfo user = (SystemUserInfo)session.getAttribute("_user");
    if (user.getStr("userName").equals("admin")) {
      return param;
    }
    Map filterParam = new HashMap();
    filterParam.put("start", (String[])param.get("start"));
    filterParam.put("limit", (String[])param.get("limit"));
    filterParam.put("page", (String[])param.get("page"));
    filterParam.put("name", (String[])param.get("name"));
    filterParam.put("auth_status", (String[])param.get("auth_status"));
    filterParam.put("auth_type", (String[])param.get("auth_type"));
    filterParam.put("telephone", (String[])param.get("telephone"));
    filterParam.put("app_type", (String[])param.get("app_type"));
    filterParam.put("start_time", (String[])param.get("start_time"));
    filterParam.put("invite_code", (String[])param.get("invite_code"));
    filterParam.put("off_time", (String[])param.get("off_time"));
    filterParam.put("province", (String[])param.get("province"));
    filterParam.put("city", (String[])param.get("city"));
    filterParam.put("agentId", (String[])param.get("agentId"));
    filterParam.put("sort", (String[])param.get("sort"));
    filterParam.put("remark", (String[])param.get("remark"));
    int userType = user.getInt("userType").intValue();
    if (userType == 2) {
      String[] province = new String[1];
      province[0] = user.getStr("provinceDesc");
      filterParam.remove("province");
      filterParam.put("province", province);
    } else if (userType == 3) {
      String[] province = new String[1];
      String[] city = new String[1];
      province[0] = user.getStr("provinceDesc");
      city[0] = user.getStr("cityDesc");
      filterParam.remove("province");
      filterParam.remove("city");
      filterParam.put("province", province);
      filterParam.put("city", city);
    } else if (userType == 4) {
      String[] province = new String[1];
      String[] city = new String[1];
      province[0] = user.getStr("provinceDesc");
      city[0] = user.getStr("cityDesc");
      filterParam.remove("province");
      filterParam.remove("city");
      filterParam.put("province", province);
      filterParam.put("city", city);
      String[] agentId = new String[1];
      agentId[0] = user.getLong("id").toString();
      filterParam.remove("agentId");
      filterParam.put("agentId", agentId);
    }
    return filterParam;
  }

  public static Map<String, String[]> getEmployeeParams(Map<String, String[]> param, HttpSession session)
  {
    SystemUserInfo user = (SystemUserInfo)session.getAttribute("_user");
    if (user.getStr("userName").equals(Constant.ADMIN)) {
      return param;
    }
    Map filterParam = new HashMap();
    filterParam.put("start", (String[])param.get("start"));
    filterParam.put("limit", (String[])param.get("limit"));
    filterParam.put("page", (String[])param.get("page"));
    filterParam.put("name", (String[])param.get("name"));
    filterParam.put("phone", (String[])param.get("phone"));
    filterParam.put("id", (String[])param.get("id"));
    filterParam.put("id", (String[])param.get("id"));
    filterParam.put("start_time", (String[])param.get("start_time"));
    filterParam.put("off_time", (String[])param.get("off_time"));
    filterParam.put("employees_type", (String[])param.get("employees_type"));
    filterParam.put("merchantsid", (String[])param.get("merchantsid"));
    filterParam.put("province", (String[])param.get("province"));
    filterParam.put("city", (String[])param.get("city"));
    filterParam.put("agentId", (String[])param.get("agentId"));
    filterParam.put("sort", (String[])param.get("sort"));
    int userType = user.getInt("userType").intValue();
    if (userType == 2) {
      String[] province = new String[1];
      province[0] = user.getStr("provinceDesc");
      filterParam.remove("province");
      filterParam.put("province", province);
    } else if (userType == 3) {
      String[] province = new String[1];
      String[] city = new String[1];
      province[0] = user.getStr("provinceDesc");
      city[0] = user.getStr("cityDesc");
      filterParam.remove("province");
      filterParam.remove("city");
      filterParam.put("province", province);
      filterParam.put("city", city);
    } else if (userType == 4) {
      String[] province = new String[1];
      String[] city = new String[1];
      province[0] = user.getStr("provinceDesc");
      city[0] = user.getStr("cityDesc");
      filterParam.remove("province");
      filterParam.remove("city");
      filterParam.put("province", province);
      filterParam.put("city", city);
      String[] agentId = new String[1];
      agentId[0] = user.getLong("id").toString();
      filterParam.remove("agentId");
      filterParam.put("agentId", agentId);
    }
    return filterParam;
  }

  public static String getChangeDate(String date) {
    if ((date == null) || (date.equals(""))) {
      return "";
    }
    String dateCH = date.substring(4, 24);
    String dateCsds = "";
    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat sdf2 = new SimpleDateFormat("MMM d yyyy HH:mm:ss", 
      Locale.ENGLISH);
    try {
      dateCsds = sdf1.format(sdf2.parse(dateCH));
      System.out.println("res:" + dateCsds);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return dateCsds;
  }

  public static String getChangeFensiDate(String date) {
    if ((date == null) || (date.equals(""))) {
      return "";
    }
    String dateCH = date.substring(4, 24);
    String dateCsds = "";
    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdf2 = new SimpleDateFormat("MMM d yyyy", 
      Locale.ENGLISH);
    try {
      dateCsds = sdf1.format(sdf2.parse(dateCH));
      System.out.println("res:" + dateCsds);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return dateCsds;
  }
  public static String getLastMonth() {
    Date date = new Date();
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    calendar.add(5, -7);
    date = calendar.getTime();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String dateString = formatter.format(date);
    return dateString;
  }

  public static String getLastDay() {
    Date date = new Date();
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    calendar.set(11, 0);
    calendar.set(12, 0);
    calendar.set(13, 0);
    calendar.set(14, 0);
    date = calendar.getTime();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String dateString = formatter.format(date);
    return dateString;
  }

  public static String getLastDayByDay() {
    Date date = new Date();
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    calendar.add(5, -1);
    date = calendar.getTime();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    String dateString = formatter.format(date);
    return dateString;
  }
  public static String getBeforLastDayByDay() {
    Date date = new Date();
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    calendar.add(5, -2);
    date = calendar.getTime();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    String dateString = formatter.format(date);
    return dateString;
  }

  public static String getLastWeekByDay() {
    Date date = new Date();
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    calendar.add(5, -7);
    date = calendar.getTime();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    String dateString = formatter.format(date);
    return dateString;
  }

  public static String getCurrentDate(Date date)
  {
    if (date == null) {
      return "";
    }
    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
    return sdf1.format(date);
  }

  public static String getChangeDayDate(String date) {
    if ((date == null) || (date.equals(""))) {
      return "";
    }
    String dateCH = date.substring(4, 24);
    String dateCsds = "";
    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdf2 = new SimpleDateFormat("MMM d yyyy", 
      Locale.ENGLISH);
    try {
      dateCsds = sdf1.format(sdf2.parse(dateCH));
      System.out.println("res:" + dateCsds);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return dateCsds;
  }
  public static Object getExportdateFilter(String key, String[] startTime, String[] offTime) {
    StringBuffer filter = new StringBuffer();
    if ((startTime != null) && (startTime.length > 0) && (!startTime[0].equals("")) && (!startTime[0].equals("null"))) {
      filter.append(" and " + key + ">'" + getChangeDate(startTime[0]) + "'");
      if ((offTime != null) && (offTime.length > 0) && (!offTime[0].equals("")) && (!offTime[0].equals("null"))) {
        filter.append(" and " + key + "<'" + getChangeDate(offTime[0]) + "'");
      }
    }
    else if ((offTime != null) && (offTime.length > 0) && (!offTime[0].equals("")) && (!offTime[0].equals("null"))) {
      filter.append(" and " + key + "<'" + getChangeDate(offTime[0]) + "'");
    }

    return filter.toString();
  }

  public static String getExportdateFilter(int flag) {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    if (flag == 1)
    {
      Calendar cal_1 = Calendar.getInstance();
      cal_1.add(2, -1);
      cal_1.set(5, 1);
      String firstDay = format.format(cal_1.getTime());
      return firstDay;
    }

    Calendar c = Calendar.getInstance();
    c.add(2, 0);
    c.set(5, 1);
    String first = format.format(c.getTime());
    return first;
  }

  public static Map<String, String[]> getAgentEmployeeParams(Map<String, String[]> param, HttpSession session)
  {
    SystemUserInfo user = (SystemUserInfo)session.getAttribute("_user");
    if (user.getStr("userName").equals("admin")) {
      return param;
    }
    Map filterParam = new HashMap();
    filterParam.put("start", (String[])param.get("start"));
    filterParam.put("limit", (String[])param.get("limit"));
    filterParam.put("page", (String[])param.get("page"));
    filterParam.put("name", (String[])param.get("name"));
    filterParam.put("phone", (String[])param.get("phone"));
    filterParam.put("invite_code", (String[])param.get("invite_code"));
    filterParam.put("start_time", (String[])param.get("start_time"));
    filterParam.put("off_time", (String[])param.get("off_time"));
    filterParam.put("province", (String[])param.get("province"));
    filterParam.put("city", (String[])param.get("city"));
    filterParam.put("provinceDesc", (String[])param.get("provinceDesc"));
    filterParam.put("cityDesc", (String[])param.get("cityDesc"));

    filterParam.put("sort", (String[])param.get("sort"));
    int userType = user.getInt("userType").intValue();
    if (userType == 2) {
      String[] province = new String[1];
      province[0] = StringUtil.null2Str(user.getLong("province"));
      filterParam.remove("province");
      filterParam.put("province", province);

      String[] provinceDesc = new String[1];
      provinceDesc[0] = user.getStr("provinceDesc");
      filterParam.remove("provinceDesc");
      filterParam.put("provinceDesc", provinceDesc);
    } else if (userType == 3) {
      String[] province = new String[1];
      String[] city = new String[1];
      province[0] = StringUtil.null2Str(user.getLong("province"));
      city[0] = StringUtil.null2Str(user.getLong("city"));
      filterParam.remove("province");
      filterParam.remove("city");
      filterParam.put("province", province);
      filterParam.put("city", city);
      String[] provinceDesc = new String[1];
      String[] cityDesc = new String[1];
      provinceDesc[0] = user.getStr("provinceDesc");
      filterParam.remove("provinceDesc");
      filterParam.put("provinceDesc", provinceDesc);
      cityDesc[0] = user.getStr("cityDesc");
      filterParam.remove("cityDesc");
      filterParam.put("cityDesc", cityDesc);
    } else if (userType == 4) {
      String[] province = new String[1];
      String[] city = new String[1];
      province[0] = StringUtil.null2Str(user.getLong("province"));
      city[0] = StringUtil.null2Str(user.getLong("city"));
      filterParam.remove("province");
      filterParam.remove("city");
      filterParam.put("province", province);
      filterParam.put("city", city);
      String[] provinceDesc = new String[1];
      String[] cityDesc = new String[1];
      provinceDesc[0] = user.getStr("provinceDesc");
      filterParam.remove("provinceDesc");
      filterParam.put("provinceDesc", provinceDesc);
      cityDesc[0] = user.getStr("cityDesc");
      filterParam.remove("cityDesc");
      filterParam.put("cityDesc", cityDesc);
      String[] agentId = new String[1];
      agentId[0] = user.getLong("id").toString();
      filterParam.remove("agentId");
      filterParam.put("agentId", agentId);
    }
    return filterParam;
  }

  public static String getMaxTime(String start_time1, String start_time) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date1 = sdf.parse(start_time1);
    Date date = sdf.parse(start_time);
    if (date1.after(date)) {
      return start_time1;
    }
    return start_time;
  }

  public static Map<String, String> getvipMemberApplyFilter(Map<String, String[]> param, HttpSession session)
  {
    Map paraMap = new HashMap();
    if (StringUtil.isNotNullMap(param, "merchantName")) {
      paraMap.put("merchantName", StringUtil.null2Str(((String[])param.get("merchantName"))[0]));
    }
    if (StringUtil.isNotNullMap(param, "telephone")) {
      paraMap.put("telephone", StringUtil.null2Str(((String[])param.get("telephone"))[0]));
    }
    if (StringUtil.isNotNullMap(param, "applyStatus")) {
      paraMap.put("applyStatus", StringUtil.null2Str(((String[])param.get("applyStatus"))[0]));
    }
    if (StringUtil.isNotNullMap(param, "clientType")) {
        paraMap.put("clientType", StringUtil.null2Str(((String[])param.get("clientType"))[0]));
      }
    if (StringUtil.isNotNullMap(param, "stime")) {
      paraMap.put("stime", StringUtil.null2Str(((String[])param.get("stime"))[0]));
    }
    if (StringUtil.isNotNullMap(param, "etime")) {
      paraMap.put("etime", StringUtil.null2Str(((String[])param.get("etime"))[0]));
    }
    String province = "";
    String city = "";
    String appType = "";
    String appTypeBoo = "true";
    if (StringUtil.isNotNullMap(param, "appType")) {
      appType = StringUtil.null2Str(((String[])param.get("appType"))[0]);
    }
    if (StringUtil.isNotNullMap(param, "sort")) {
      paraMap.put("sort", StringUtil.null2Str(((String[])param.get("sort"))[0]));
    }
    if (StringUtil.isNotNullMap(param, "start")) {
      paraMap.put("start", StringUtil.null2Str(((String[])param.get("start"))[0]));
    }
    if (StringUtil.isNotNullMap(param, "limit")) {
      paraMap.put("limit", StringUtil.null2Str(((String[])param.get("limit"))[0]));
    }

    SystemUserInfo user = (SystemUserInfo)session.getAttribute("_user");
    String userName = StringUtil.null2Str(session.getAttribute("_user_name"));
    paraMap.put("userType", StringUtil.null2Str(user.getInt("userType")));
    if (Constant.ADMIN.equals(userName))
    {
      if (StringUtil.isNotNullMap(param, "province")) {
        paraMap.put("province", StringUtil.null2Str(((String[])param.get("province"))[0]));
      }
      if (StringUtil.isNotNullMap(param, "city"))
        paraMap.put("city", StringUtil.null2Str(((String[])param.get("city"))[0]));
    }
    else {
      int userType = StringUtil.nullToInteger(user.getInt("userType")).intValue();

      if (userType == 1) {
        if (StringUtil.isNotNullMap(param, "province")) {
          paraMap.put("province", StringUtil.null2Str(((String[])param.get("province"))[0]));
        }
        if (StringUtil.isNotNullMap(param, "city"))
          paraMap.put("city", StringUtil.null2Str(((String[])param.get("city"))[0]));
      }
      else if (userType == 2) {
        province = user.getStr("provinceDesc");
        paraMap.put("province", province);
        if (StringUtil.isNotNullMap(param, "city"))
          paraMap.put("city", StringUtil.null2Str(((String[])param.get("city"))[0]));
      }
      else if (userType == 3) {
        province = user.getStr("provinceDesc");
        city = user.getStr("cityDesc");
        paraMap.put("province", province);
        paraMap.put("city", city);
      } else if (userType == 4) {
        province = user.getStr("provinceDesc");
        city = user.getStr("cityDesc");
        paraMap.put("province", province);
        paraMap.put("city", city);
        List appList = (List)session.getAttribute("_user_app");
        String appTypes = "";
        if ((appList != null) && (appList.size() > 0)) {
          for (int i = 0; i < appList.size(); i++) {
            if (StringUtil.isNullStr(appType))
            {
              if (i == 0)
                appTypes = ((AppInfo)appList.get(i)).getStr("app_type");
              else
                appTypes = appTypes + "," + ((AppInfo)appList.get(i)).getStr("app_type");
            }
            else
            {
              if (StringUtil.null2Str(((AppInfo)appList.get(i)).getStr("app_type")).equals(appType))
              {
                appTypes = appType;
                break;
              }

              appTypes = "";
            }
          }
        }

        appType = appTypes;
        if (StringUtil.isNullStr((String)paraMap.get("appType"))) {
          appTypeBoo = "false";
        }
      }
    }

    paraMap.put("appType", appType);
    paraMap.put("appTypeBoo", appTypeBoo);
    return paraMap;
  }

  public static boolean checkImagSize(File file, Map<String, String[]> param) throws IOException {
    Image src = ImageIO.read(file);
    int wideth = src.getWidth(null);
    int height = src.getHeight(null);
    System.out.println(wideth + "," + height);
    String type = ((String[])param.get("type"))[0];
    if (("1".equals(type)) && (wideth <= 144) && (height <= 168))
      return true;
    if (("2".equals(type)) && (wideth <= 140) && (height <= 88)) {
      return true;
    }
    return false;
  }

  public static Map<String, String[]> getDayMerPhotoParams(Map<String, String[]> param, HttpSession session)
  {
    SystemUserInfo user = (SystemUserInfo)session.getAttribute("_user");
    if (user.getStr("userName").equals(Constant.ADMIN)) {
      return param;
    }

    Map filterParam = new HashMap();
    filterParam.put("start", (String[])param.get("start"));
    filterParam.put("limit", (String[])param.get("limit"));
    filterParam.put("page", (String[])param.get("page"));
    filterParam.put("head_date", (String[])param.get("head_date"));
    filterParam.put("area", (String[])param.get("area"));
    filterParam.put("province", (String[])param.get("province"));
    filterParam.put("city", (String[])param.get("city"));

    int userType = user.getInt("userType").intValue();
    if (userType == 2) {
      String[] province = new String[1];
      province[0] = user.getStr("provinceDesc");
      filterParam.remove("province");
      filterParam.put("province", province);
    } else if (userType == 3) {
      String[] province = new String[1];
      String[] city = new String[1];
      province[0] = user.getStr("provinceDesc");
      if (StringUtil.matchProvince(user.getStr("provinceDesc")))
        city[0] = user.getStr("provinceDesc");
      else {
        city[0] = user.getStr("cityDesc");
      }
      filterParam.remove("province");
      filterParam.remove("city");
      filterParam.put("province", province);
      filterParam.put("city", city);
    }
    return filterParam;
  }

  public static Map<String, String[]> getOrderRewardAccountParams(Map<String, String[]> param, HttpSession session)
  {
    SystemUserInfo user = (SystemUserInfo)session.getAttribute("_user");
    if (user.getStr("userName").equals(Constant.ADMIN)) {
      return param;
    }

    Map filterParam = new HashMap();
    filterParam.put("start", (String[])param.get("start"));
    filterParam.put("limit", (String[])param.get("limit"));
    filterParam.put("page", (String[])param.get("page"));
    filterParam.put("order_no", (String[])param.get("order_no"));
    filterParam.put("activity_id", (String[])param.get("activity_id"));
    filterParam.put("merchant_name", (String[])param.get("merchant_name"));
    filterParam.put("start_time", (String[])param.get("start_time"));
    filterParam.put("off_time", (String[])param.get("off_time"));
    filterParam.put("is_transfer", (String[])param.get("is_transfer"));
    filterParam.put("app_type", (String[])param.get("app_type"));
    filterParam.put("service_id", (String[])param.get("service_id"));
    filterParam.put("province", (String[])param.get("province"));
    filterParam.put("city", (String[])param.get("city"));
    filterParam.put("userPhone", (String[])param.get("userPhone"));
    filterParam.put("merPhone", (String[])param.get("merPhone"));
    

    int userType = user.getInt("userType").intValue();
    if (userType == 2) {
      String[] province = new String[1];
      province[0] = user.getStr("provinceDesc");
      filterParam.remove("province");
      filterParam.put("province", province);
    } else if (userType == 3) {
      String[] province = new String[1];
      String[] city = new String[1];
      province[0] = user.getStr("provinceDesc");
      if (StringUtil.matchProvince(user.getStr("provinceDesc")))
        city[0] = user.getStr("provinceDesc");
      else {
        city[0] = user.getStr("cityDesc");
      }
      filterParam.remove("province");
      filterParam.remove("city");
      filterParam.put("province", province);
      filterParam.put("city", city);
    }
    return filterParam;
  }

  public static String getExportMerFileName(HttpSession session, Map<String, String[]> param)
  {
    StringBuffer fileName = new StringBuffer();
    fileName.append("商户_");
    SystemUserInfo user = (SystemUserInfo)session.getAttribute("_user");
    if (user.getStr("userName").equals(Constant.ADMIN)) {
      return "";
    }

    int userType = user.getInt("userType").intValue();
    if (userType == 2)
      fileName.append(user.getStr("provinceDesc")).append("_");
    else if (userType == 3) {
      fileName.append(user.getStr("provinceDesc")).append("_").append(user.getStr("cityDesc")).append("_");
    }
    else {
      return "";
    }
    if ((((String[])param.get("time"))[0] != null) && (!((String[])param.get("time"))[0].equals("")))
    {
    	if(DateUtil.compareTime(param.get("time")[0])){
    		return "1";
    	}
    	String date=DateUtil.getDateparse(((String[])param.get("time"))[0]);
    	
    	fileName.append(date);
    	
    }
    else {
      fileName.append(StatisticalUtil.getCurrentTime());
    }
    fileName.append(".xlsx");
    return fileName.toString();
  }

public static String getExportMerOrderFileName(Map<String, String[]> param) {

    StringBuffer fileName = new StringBuffer();
    fileName.append("订单_");
    if ((((String[])param.get("time"))[0] != null) && (!((String[])param.get("time"))[0].equals("")))
    {
    	if(DateUtil.compareMerOrderTime(param.get("time")[0])){
    		return "";
    	}
    	String date=DateUtil.getDateMerOrderparse(((String[])param.get("time"))[0]);
    	
    	fileName.append(date);
    	
    }
    else {
      fileName.append(MerchantOrderJob.getCurrentTime());
    }
    fileName.append(".xlsx");
    return fileName.toString();
  
}

public static Map<String, String[]> getOrderUserRewardAccountParams(Map<String, String[]> param, HttpSession session) {

    SystemUserInfo user = (SystemUserInfo)session.getAttribute("_user");
    if (user.getStr("userName").equals(Constant.ADMIN)) {
      return param;
    }

    Map filterParam = new HashMap();
    filterParam.put("start", (String[])param.get("start"));
    filterParam.put("limit", (String[])param.get("limit"));
    filterParam.put("page", (String[])param.get("page"));
    filterParam.put("order_no", (String[])param.get("order_no"));
    filterParam.put("activity_id", (String[])param.get("activity_id"));
    filterParam.put("merchant_name", (String[])param.get("merchant_name"));
    filterParam.put("start_time", (String[])param.get("start_time"));
    filterParam.put("off_time", (String[])param.get("off_time"));
    filterParam.put("is_transfer", (String[])param.get("is_transfer"));
    filterParam.put("app_type", (String[])param.get("app_type"));
    filterParam.put("service_id", (String[])param.get("service_id"));
    filterParam.put("province", (String[])param.get("province"));
    filterParam.put("city", (String[])param.get("city"));
    filterParam.put("userPhone", (String[])param.get("userPhone"));
    filterParam.put("merPhone", (String[])param.get("merPhone"));
    

    int userType = user.getInt("userType").intValue();
    if (userType == 2) {
      String[] province = new String[1];
      province[0] = user.getStr("provinceDesc");
      filterParam.remove("province");
      filterParam.put("province", province);
    } else if (userType == 3) {
      String[] province = new String[1];
      String[] city = new String[1];
      province[0] = user.getStr("provinceDesc");
      if (StringUtil.matchProvince(user.getStr("provinceDesc")))
        city[0] = user.getStr("provinceDesc");
      else {
        city[0] = user.getStr("cityDesc");
      }
      filterParam.remove("province");
      filterParam.remove("city");
      filterParam.put("province", province);
      filterParam.put("city", city);
    }
    return filterParam;
  
}

public static Map<String, String[]> getCommonEditParams(Map<String, String[]> param) {
	Map<String, String[]> map =new HashMap<String, String[]>();
	map.putAll(param);
	return map;
}

}
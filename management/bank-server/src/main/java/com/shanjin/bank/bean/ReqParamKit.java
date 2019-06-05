package com.shanjin.bank.bean;

import java.util.Map;

/**
 * Created by Administrator on 2016/7/6.
 */
public class ReqParamKit {
    private Map<String, String[]> paramsMap = null;

    private ReqParamKit() {
    }

    private ReqParamKit(Map<String, String[]> paramsMap) {
        this.paramsMap = paramsMap;
    }

    public static ReqParamKit getInstance(Map<String, String[]> paramsMap) {
        return new ReqParamKit(paramsMap);
    }

    public String getString(String key) {
        String[] val = this.paramsMap.get(key);
        return val == null ? null : val[0];
    }

    public Long getLong(String key) {
        String[] val = this.paramsMap.get(key);
        return val == null ? null : Long.parseLong(val[0]);
    }

    public Integer getInt(String key) {
        String[] val = this.paramsMap.get(key);
        return val == null ? null : Integer.parseInt(val[0]);
    }

    public Boolean getBoolean(String key) {
        String[] val = this.paramsMap.get(key);
        return val == null ? null : Boolean.parseBoolean(val[0]);
    }

    public String[] get(String key){
        return this.paramsMap.get(key);
    }

    /**
     * 页数
     * @return
     */
    public int getPageNumber() {
        return this.getInt("start") == null ? 0 : this.getInt("start");
    }

    /**
     * 分页条数
     * @return
     */
    public int getPageSize() {
        return this.getInt("limit") == null ? 20 : this.getInt("limit");
    }

    public int getPage(){
        return this.getInt("page") == null ? 1 : this.getInt("page");
    }

    public boolean has(String key){
        String val = this.getString(key);
        if(val != null && val.length()>0)
            return true;
        return false;
    }
}

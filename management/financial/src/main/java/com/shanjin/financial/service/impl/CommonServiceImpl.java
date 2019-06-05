package com.shanjin.financial.service.impl;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.financial.service.CommonService;
import com.shanjin.financial.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/6.
 */
public class CommonServiceImpl implements CommonService {
    @Override
    public List<Record> getArea(Long parentId) {
        String sql = "SELECT * FROM area t WHERE t.parent_id IS NULL ORDER BY t.index_str";
        if(parentId!=null){
            sql = "SELECT * FROM area t WHERE t.parent_id ="+parentId;
        }
        List<Record> areaList= Db.find(sql);
        return areaList;
    }


    @Override
    public List<Record> getServiveCity(Long parentId) {
        List<Record> areaList=null;
        String city=Db.queryStr("SELECT area FROM area t WHERE t.id =?",parentId);
        if(StringUtil.matchProvince(city)){
            areaList=new ArrayList<Record>();
            Record r=new Record();
            r.set("id", parentId).set("area", city);
            areaList.add(r);
        }else{
            String	sql = "SELECT * FROM area t WHERE t.parent_id ="+parentId;
            areaList=Db.find(sql);
        }
        return areaList;
    }

    public List<Record> getAppType() {
        List<Record> appType= new ArrayList<Record>();
        appType.add(new Record().set("app_type", "").set("app_name", "全部"));
        String sql="select app_type,app_name from merchant_app_info where is_del=0";
        List<Record> appType2 =Db.find(sql);
        appType.addAll(appType2);
        return appType;
    }
}

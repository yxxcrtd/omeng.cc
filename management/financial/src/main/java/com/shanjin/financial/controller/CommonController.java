package com.shanjin.financial.controller;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.financial.bean.EmptyResponse;
import com.shanjin.financial.bean.NormalResponse;
import com.shanjin.financial.service.CommonService;
import com.shanjin.financial.service.impl.CommonServiceImpl;
import com.shanjin.financial.util.StringUtil;

import java.util.List;

public class CommonController extends Controller {
    private CommonService commonService = new CommonServiceImpl();

    /**
     * 地区信息
     */
    public void showArea() {
        Long parentId = null;
        if (this.getPara("parentId") != null && !"".equals(this.getPara("parentId"))) {
            parentId = StringUtil.nullToLong(this.getPara("parentId"));
        }
        List<Record> areaList = commonService.getArea(parentId);
        if (areaList != null && areaList.size() > 0) {
            this.renderJson(new NormalResponse(areaList));
        } else {
            this.renderJson(new EmptyResponse());
        }
    }


    /**
     * 地区信息
     */
    public void showServiveCity() {
        Long parentId = null;
        if (this.getPara("parentId") != null && !"".equals(this.getPara("parentId"))) {
            parentId = StringUtil.nullToLong(this.getPara("parentId"));
        }
        List<Record> areaList = commonService.getServiveCity(parentId);
        if (areaList != null && areaList.size() > 0) {
            this.renderJson(new NormalResponse(areaList));
        } else {
            this.renderJson(new EmptyResponse());
        }
    }

    public void showAppType() {
        List<Record> appTypeList = commonService.getAppType();
        if (appTypeList != null && appTypeList.size() > 0) {
            this.renderJson(new NormalResponse(appTypeList));
        } else {
            this.renderJson(new EmptyResponse());
        }
    }

}

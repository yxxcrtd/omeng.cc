package com.shanjin.bank.service;


import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.bank.util.ExcelExportUtil;


public class ExportService {

    public static ExportService service = new ExportService();

    /**
     * 导出数据
     *
     * @param response
     * @param request
     * @param records  结果集
     */
    public  void export(HttpServletResponse response, HttpServletRequest request, List<JSONObject> records, List<ExcelExportUtil.Pair> titles, String fileName, int type) {


        ExcelExportUtil.exportByRecord(response, request, fileName, titles, records, type);
    }

    public void downLoad(HttpServletResponse response, HttpServletRequest request, String path) throws IOException {
        ExcelExportUtil.download(path, response, request);
    }
}

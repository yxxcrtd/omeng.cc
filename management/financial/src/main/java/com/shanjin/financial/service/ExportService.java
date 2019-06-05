package com.shanjin.financial.service;


import com.jfinal.plugin.activerecord.Model;
import com.shanjin.financial.util.ExcelExportUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


public class ExportService {

    public static ExportService service = new ExportService();

    /**
     * 导出数据
     *
     * @param response
     * @param request
     * @param records  结果集
     */
    public <M extends Model<M>> void export(HttpServletResponse response, HttpServletRequest request, List<M> records, List<ExcelExportUtil.Pair> titles, String fileName, int type) {


        ExcelExportUtil.exportByRecord(response, request, fileName, titles, records, type);
    }

    public void downLoad(HttpServletResponse response, HttpServletRequest request, String path) throws IOException {
        ExcelExportUtil.download(path, response, request);
    }
}

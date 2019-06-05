package com.shanjin.financial.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.financial.bean.MerchantApplyWithdrawRecord;
import com.shanjin.financial.bean.ReqParamKit;
import com.shanjin.financial.service.FinancialService;
import com.shanjin.financial.util.ExcelExportUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/6.
 */
public class FinancialServiceImpl implements FinancialService {

    @Override
    public List<ExcelExportUtil.Pair> getWithdrawalTitles() {
        List<ExcelExportUtil.Pair> titles = new ArrayList<>();

        titles.add(new ExcelExportUtil.Pair("id", "id"));
        titles.add(new ExcelExportUtil.Pair("name", "姓名"));
        titles.add(new ExcelExportUtil.Pair("telephone", "手机号"));
        titles.add(new ExcelExportUtil.Pair("withdraw_price", "提现金额"));
        titles.add(new ExcelExportUtil.Pair("withdraw_bank", "提现银行"));
        titles.add(new ExcelExportUtil.Pair("name", "银行账户名"));
        titles.add(new ExcelExportUtil.Pair("withdraw_no", "银行卡号"));
        titles.add(new ExcelExportUtil.Pair("withdraw_time", "申请时间"));
        titles.add(new ExcelExportUtil.Pair("withdraw_status", "状态"));
        titles.add(new ExcelExportUtil.Pair("complete_time", "完成时间"));
        titles.add(new ExcelExportUtil.Pair("audit_name", "经办人"));

        return titles;
    }
    @Override
    public List<Record> getWithdrawalData(Map<String, String[]> paramMap) {
        ReqParamKit paramKit = ReqParamKit.getInstance(paramMap);
        int pageNumber = paramKit.getInt("start");
        int pageSize = paramMap.get("limit")[0] == null ? 20
                : Integer.parseInt(paramMap.get("limit")[0]);

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT a.id,b.name,c.telephone, d.dict_value AS withdraw_bank,a.withdraw_no, a.withdraw_price, a.withdraw_time,a.withdraw_status,a.audit_name FROM \n" +
                "merchant_apply_withdraw_record AS a \n" +
                "left join merchant_info AS b on a.merchant_id = b.id \n" +
                "left join merchant_contact AS c on a.merchant_id = c.merchant_id \n" +
                "left join dictionary AS d on a.withdraw = d.id \n" +
                "WHERE a.is_del=0 and a.withdraw_status < 2");

        //按姓名模糊搜索
        String name = paramKit.getString("name");
        if (name != null && name.length() > 0) {
            sql.append(" and b.name like '%" + name + "%'");
        }

        //按手机号码模糊搜索
        String phone = paramKit.getString("phone");
        if (phone != null && phone.length() > 0)
            sql.append(" and c.telephone like '%" + phone + "%'");


        //按申请日期范围搜索
        String beginDate = paramKit.getString("beginDate");
        if (beginDate != null && beginDate.length() > 0)
            sql.append(" and a.withdraw_time >= '" + beginDate + "'");

        String endDate = paramKit.getString("endDate");
        if (endDate != null && endDate.length() > 0)
            sql.append(" and a.withdraw_time <= '" + endDate + "'");

        //追加查询条件和排序
        String sort = paramKit.getString("sort");
        if (sort != null) {
            JSONArray sortArray = JSON.parseArray(sort);
            sql.append(" order by " + sortArray.getJSONObject(0).get("property") + " " + sortArray.getJSONObject(0).get("direction"));
        } else {
            sql.append(" order by a.withdraw_time desc");
        }

        sql.append(" LIMIT " + pageNumber);
        sql.append(",");
        sql.append(pageSize);

        List<Record> list = Db.find(sql.toString());

        return list;
    }

    @Override
    public List<MerchantApplyWithdrawRecord> getAllWithdrawalData() {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT a.id,b.name,c.telephone, d.dict_value AS withdraw_bank,a.withdraw_no, a.withdraw_price, a.withdraw_time,a.withdraw_status,a.audit_name FROM \n" +
                "merchant_apply_withdraw_record AS a \n" +
                "left join merchant_info AS b on a.merchant_id = b.id \n" +
                "left join merchant_contact AS c on a.merchant_id = c.merchant_id \n" +
                "left join dictionary AS d on a.withdraw = d.id \n" +
                "WHERE a.is_del=0");
        List<MerchantApplyWithdrawRecord> list = MerchantApplyWithdrawRecord.dao.find(sql.toString());

        for (MerchantApplyWithdrawRecord mawr : list) {
            int status = mawr.getInt("withdraw_status");

            switch (status) {
                case 0:
                    mawr.set("withdraw_status", "失败");
                    break;
                case 1:
                    mawr.set("withdraw_status", "成功");
                    break;
                case 2:
                    mawr.set("withdraw_status", "提取中");
                    break;
            }
        }
        return list;
    }

    @Override
    public List<Record> getAuditData(Map<String, String[]> paramMap) {
        ReqParamKit paramKit = ReqParamKit.getInstance(paramMap);
        int pageNumber = paramKit.getInt("start");
        int pageSize = paramMap.get("limit")[0] == null ? 20
                : Integer.parseInt(paramMap.get("limit")[0]);

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT a.id,b.name,c.telephone, d.dict_value AS withdraw_bank,a.withdraw_no, a.withdraw_price, a.withdraw_time,a.withdraw_status,a.audit_name FROM \n" +
                "merchant_apply_withdraw_record AS a \n" +
                "left join merchant_info AS b on a.merchant_id = b.id \n" +
                "left join merchant_contact AS c on a.merchant_id = c.merchant_id \n" +
                "left join dictionary AS d on a.withdraw = d.id \n" +
                "WHERE a.is_del=0 and a.withdraw_status = 2");

        //按姓名模糊搜索
        String name = paramKit.getString("name");
        if (name != null && name.length() > 0) {
            sql.append(" and b.name like '%" + name + "%'");
        }

        //按手机号码模糊搜索
        String phone = paramKit.getString("phone");
        if (phone != null && phone.length() > 0)
            sql.append(" and c.telephone like '%" + phone + "%'");


        //按申请日期范围搜索
        String beginDate = paramKit.getString("beginDate");
        if (beginDate != null && beginDate.length() > 0)
            sql.append(" and a.withdraw_time >= '" + beginDate + "'");

        String endDate = paramKit.getString("endDate");
        if (endDate != null && endDate.length() > 0)
            sql.append(" and a.withdraw_time <= '" + endDate + "'");

        //追加查询条件和排序
        String sort = paramKit.getString("sort");
        if (sort != null) {
            JSONArray sortArray = JSON.parseArray(sort);
            sql.append(" order by " + sortArray.getJSONObject(0).get("property") + " " + sortArray.getJSONObject(0).get("direction"));
        } else {
            sql.append(" order by a.withdraw_time desc");
        }

        sql.append(" LIMIT " + pageNumber);
        sql.append(",");
        sql.append(pageSize);

        List<Record> list = Db.find(sql.toString());

        return list;
    }

    @Override
    public List<Record> getStatementWxData(ReqParamKit paramKit) {
        StringBuffer sql = new StringBuffer();
        sql.append("select * from bill_wx_day where 1=1");

        //按申请日期范围搜索
        String beginDate = paramKit.getString("beginDate");
        if (beginDate != null && beginDate.length() > 0)
            sql.append(" and bill_date >= '" + beginDate + "'");

        String endDate = paramKit.getString("endDate");
        if (endDate != null && endDate.length() > 0)
            sql.append(" and bill_date <= '" + endDate + "'");

        //追加查询条件和排序
        String sort = paramKit.getString("sort");
        if (sort != null) {
            JSONArray sortArray = JSON.parseArray(sort);
            sql.append(" order by " + sortArray.getJSONObject(0).get("property") + " " + sortArray.getJSONObject(0).get("direction"));
        } else {
            sql.append(" order by bill_date desc");
        }

        sql.append(" LIMIT " + paramKit.getPageNumber());
        sql.append(",");
        sql.append(paramKit.getPageSize());

        List<Record> list = Db.find(sql.toString());

        return list;
    }

    @Override
    public List<Record> getStatementAlipayData(ReqParamKit paramKit) {
        StringBuffer sql = new StringBuffer();
        sql.append("select * from bill_ali_day where 1=1");

        //按申请日期范围搜索
        String beginDate = paramKit.getString("beginDate");
        if (beginDate != null && beginDate.length() > 0)
            sql.append(" and bill_date >= '" + beginDate + "'");

        String endDate = paramKit.getString("endDate");
        if (endDate != null && endDate.length() > 0)
            sql.append(" and bill_date <= '" + endDate + "'");

        //追加查询条件和排序
        String sort = paramKit.getString("sort");
        if (sort != null) {
            JSONArray sortArray = JSON.parseArray(sort);
            sql.append(" order by " + sortArray.getJSONObject(0).get("property") + " " + sortArray.getJSONObject(0).get("direction"));
        } else {
            sql.append(" order by bill_date desc");
        }

        sql.append(" LIMIT " + paramKit.getPageNumber());
        sql.append(",");
        sql.append(paramKit.getPageSize());

        List<Record> list = Db.find(sql.toString());

        return list;
    }

    @Override
    public List<Record> getStatementWxDetailData(ReqParamKit paramKit) {
        String day = paramKit.getString("day");
        String sql = "select * from bill_wx_detail where bill_date='"+day+"'";

        List<Record> list = Db.find(sql);

        return list;
    }

    @Override
    public List<Record> getStatementAlipayDetailData(ReqParamKit paramKit) {
        String day = paramKit.getString("day");
        String sql = "select * from bill_ali_detail where bill_date='"+day+"'";

        List<Record> list = Db.find(sql);

        return list;
    }

    @Override
    public List<Record> getStatementWalletData(ReqParamKit paramKit) {
        StringBuffer sql = new StringBuffer();
        sql.append("select * from bill_wallet_day where 1=1");

        //按申请日期范围搜索
        String beginDate = paramKit.getString("beginDate");
        if (beginDate != null && beginDate.length() > 0)
            sql.append(" and bill_date >= '" + beginDate + "'");

        String endDate = paramKit.getString("endDate");
        if (endDate != null && endDate.length() > 0)
            sql.append(" and bill_date <= '" + endDate + "'");

        //追加查询条件和排序
        String sort = paramKit.getString("sort");
        if (sort != null) {
            JSONArray sortArray = JSON.parseArray(sort);
            sql.append(" order by " + sortArray.getJSONObject(0).get("property") + " " + sortArray.getJSONObject(0).get("direction"));
        } else {
            sql.append(" order by bill_date desc");
        }

        sql.append(" LIMIT " + paramKit.getPageNumber());
        sql.append(",");
        sql.append(paramKit.getPageSize());

        List<Record> list = Db.find(sql.toString());

        return list;
    }

    @Override
    public List<Record> getStatementWalletDetailData(ReqParamKit paramKit) {
        String day = paramKit.getString("day");
        String sql = "select * from bill_wallet_detail where bill_date='"+day+"'";

        List<Record> list = Db.find(sql);

        return list;
    }
}

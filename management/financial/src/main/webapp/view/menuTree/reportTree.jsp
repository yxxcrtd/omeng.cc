<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/cms.tld" prefix="cms" %>
<%
    response.setHeader("Pragma", "No-cache");
    response.setHeader("Cache-Control", "no-cache");
    response.setHeader("Expires", "0");

    request.setCharacterEncoding("UTF-8");
%>
{
"data":[
<cms:havePerm url='/financial/index'>
    {
    "id": "report_0",
    "text": "用户资产查询",
    "description": "用户资产查询",
    "father_id": "2",
    "leaf": 1,
    "type_id": "MENU",
    "iconCls": "Fuwushangtixianguanli",
    "url": "/report/userAccountReport"
    },
    {
    "id": "report_1",
    "text": "已完成订单明细",
    "description": "已完成订单明细",
    "father_id": "2",
    "leaf": 1,
    "type_id": "MENU",
    "iconCls": "Fuwushangtixianguanli",
    "url": "/report/orderComplete"
    },
    {
    "id": "report_2",
    "text": "千万粉丝活动明细",
    "description": "千万粉丝活动明细",
    "father_id": "2",
    "leaf": 1,
    "type_id": "MENU",
    "iconCls": "Fuwushangtixianguanli",
    "url": "/report/fansActivityDetail"
    },
    {
    "id": "report_3",
    "text": "增值服务明细",
    "description": "增值服务明细",
    "father_id": "2",
    "leaf": 1,
    "type_id": "MENU",
    "iconCls": "Fuwushangtixianguanli",
    "url": "/report/vatServiceDetail"
    },
    {
    "id": "report_4",
    "text": "剪彩红包明细",
    "description": "剪彩红包明细",
    "father_id": "2",
    "leaf": 1,
    "type_id": "MENU",
    "iconCls": "Fuwushangtixianguanli",
    "url": "/report/giftDetail"
    },
    {
    "id": "report_5",
    "text": "订单奖励明细",
    "description": "订单奖励明细",
    "father_id": "2",
    "leaf": 1,
    "type_id": "MENU",
    "iconCls": "Fuwushangtixianguanli",
    "url": "/report/orderRewardDetail"
    },
    {
    "id": "report_6",
    "text": "收/付款汇总日计表",
    "description": "收/付款汇总日计表",
    "father_id": "2",
    "leaf": 1,
    "type_id": "MENU",
    "iconCls": "Fuwushangtixianguanli",
    "url": "/report/dayPaymentsTotalReport"
    },
    {
    "id": "report_7",
    "text": "收/支汇总日计表",
    "description": "收/支汇总日计表",
    "father_id": "2",
    "leaf": 1,
    "type_id": "MENU",
    "iconCls": "Fuwushangtixianguanli",
    "url": "/report/dayBalanceTotalReport"
    },
    {
    "id": "report_8",
    "text": "客户备付金汇总日计表",
    "description": "客户备付金汇总日计表",
    "father_id": "2",
    "leaf": 1,
    "type_id": "MENU",
    "iconCls": "Fuwushangtixianguanli",
    "url": "/report/dayPettyCashTotalReport"
    },
    {
    "id": "report_9",
    "text": "收款统计表",
    "description": "收款统计表",
    "father_id": "2",
    "leaf": 1,
    "type_id": "MENU",
    "iconCls": "Fuwushangtixianguanli",
    "url": "/report/sumInReport"
    },
    {
    "id": "report_10",
    "text": "付款统计表",
    "description": "付款统计表",
    "father_id": "2",
    "leaf": 1,
    "type_id": "MENU",
    "iconCls": "Fuwushangtixianguanli",
    "url": "/report/sumPayReport"
    },
    {
    "id": "report_11",
    "text": "费用统计表",
    "description": "费用统计表",
    "father_id": "2",
    "leaf": 1,
    "type_id": "MENU",
    "iconCls": "Fuwushangtixianguanli",
    "url": "/report/sumCashReport"
    },
    {
    "id": "report_12",
    "text": "收入统计表",
    "description": "收入统计表",
    "father_id": "2",
    "leaf": 1,
    "type_id": "MENU",
    "iconCls": "Fuwushangtixianguanli",
    "url": "/report/sumReceiveReport"
    },
    {
    "id": "report_13",
    "text": "客户备付金统计表",
    "description": "客户备付金统计表",
    "father_id": "2",
    "leaf": 1,
    "type_id": "MENU",
    "iconCls": "Fuwushangtixianguanli",
    "url": "/report/sumPreparedReport"
    }
</cms:havePerm>
]
}
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
    "id": "financial_1",
    "text": "服务商提现记录",
    "description": "服务商提现记录",
    "father_id": "1",
    "leaf": 1,
    "type_id": "MENU",
    "iconCls": "Fuwushangtixianguanli",
    "url": "/financial/index"
    },
    {
    "id": "financial_2",
    "text": "服务商提现审核",
    "description": "服务商提现审核",
    "father_id": "1",
    "leaf": 1,
    "type_id": "MENU",
    "iconCls": "Fuwushangtixianguanli",
    "url": "/financial/audit"
    },{
    "id": "financial_2_1",
    "text": "银行处理结果查询",
    "description": "银行处理结果查询",
    "father_id": "1",
    "leaf": 1,
    "type_id": "MENU",
    "iconCls": "Fuwushangtixianguanli",
    "url": "/financial/bankQuery"
    },
    {
    "id": "financial_3",
    "text": "微信对账",
    "description": "微信对账",
    "father_id": "1",
    "leaf": 1,
    "type_id": "MENU",
    "iconCls": "Fuwushangtixianguanli",
    "url": "/financial/statementWx"
    },
    {
    "id": "financial_4",
    "text": "支付宝对账",
    "description": "支付宝对账",
    "father_id": "1",
    "leaf": 1,
    "type_id": "MENU",
    "iconCls": "Fuwushangtixianguanli",
    "url": "/financial/statementAlipay"
    },
    {
    "id": "financial_5",
    "text": "内部对账",
    "description": "内部对账",
    "father_id": "1",
    "leaf": 1,
    "type_id": "MENU",
    "iconCls": "Fuwushangtixianguanli",
    "url": "/financial/statementSelf"
    }
</cms:havePerm>
]
}
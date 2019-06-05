<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ include file="../common/common.jsp"%>
<%
	response.setHeader("Pragma","No-cache");
	response.setHeader("Cache-Control","no-cache");
	response.setHeader("Expires","0");

	request.setCharacterEncoding("UTF-8");	
	
	//String webRoot = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>服务商业务统计</title>
<style>
        .card {
            background: #fff;
            margin: 10px;
            position: relative;
            max-width: 300px;;
            border-radius: 5px;
            display: inline-block;
        }
        .card-content {
            box-shadow: 0 0 1px rgba(0, 0, 0, 0.9) inset;
            position: relative;
        }
        .card-content-inner {
            padding: 15px;
            position: relative;
            text-align: center;
        }
        .card-content-inner > p:first-child {
            margin-top: 0;
        }
        .card-content-inner > p:last-child {
            margin-bottom: 0;
        }
        .card-header {
            text-align:center;
            padding:8px 5px;
            border-radius: 0.1rem 0.1rem 0 0;
            color:#FFF;
            background:#2D2D2D;
        }
        .card-header.no-padding {
            padding: 0;
        }
        .btn{display:inline-block;cursor:pointer;padding:0 15px;;height:30px;line-height:30px;border-radius:15px;background:red;color:#FFF;margin:5px;}
    </style>
<script type="text/javascript">
//引入扩展组件
Ext.Loader.setConfig({enabled: true});

Ext.Loader.setPath('Ext.ux', '../ExtJS4.2/ux/');

Ext.require([
    'Ext.data.*',
    'Ext.grid.*',
    'Ext.util.*',
    'Ext.tip.QuickTipManager'
]);


	//下载所有商户运营信息
	function download() {
		definprompt();
			};
//-------------------start---------------------
			var definprompt=function(){
			 	var formPanel = new Ext.form.FormPanel({
			 		   autoWidth:true,
			 		   layout:"form",
			 		   frame:true,
			 		   labelWidth:75,
			 		   labelAlign:"right",
			 		   items:[{
			 		    xtype:"label",
			 		    height  : 20,
			 		    text :"请输入时间:"
			 		   },{
		 					xtype : 'datefield',
		 					name: 'head_date',
		 					format : 'Y-m-d',
		 					labelAlign:'left',				
		 					
		 				}],  
			 		   buttons : [{
			 		    text : '确定',
			 		    handler : function(){
			 		    
		                    var head_date=formPanel.form.findField('head_date').getValue();
			              
			              	Ext.Ajax.request({  
		                         url:"/analysis/downloadExcel",  
		                         method:'post',  
		                         params:{time:head_date},  
		                         success:function(o){
		                        	 win.close();
		                        	 var hrefadd=o.responseText;
		                        	  if(hrefadd==''){
		                        		 Ext.example.msg("提示","请选择小于当前时间，并且大于7月30号的时间");
		                        		 return;
		                        	 }else{
		                        		 window.location.href = o.responseText; 
		                        	 }
		                        	 
		                         },  
		                         failure:function(form,action){ 
		                          	 Ext.example.msg("提示","下载失败！");
		                         }  
		                     });    
			 		    }
			 		   }, {
			 		    text : '取消',
			 		    handler : function(){
			 		    formPanel.close();
			 		     win.close();
			 		    }
			 		   }]
			 		  });
			 		  var win = new Ext.Window({
			 		   title:"输入框",
			 		   modal:true,
			 		   width:250,
			 		   height:150,
			 		   collapsible:false,
			 		   resizable:false,
			 		   closeAction:'hide',
			 		   items:[formPanel]
			 		  });
			 		  
			 		    win.show();
			           };
			  	     
//*********************下载报表end*********************
	

</script>

</head>
<body>
<div class="card">
    <div class="card-header">服务商信息统计表</div>
    <div class="card-content">
        <div class="card-content-inner">
            <p>用于下载所选日期</p>
            <p>当天的服务商订单报表。</p>
            <span class="btn" onClick="download()">下载报表</span>
        </div>
    </div>
</div>


</body>
</html>
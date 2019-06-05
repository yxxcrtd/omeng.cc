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

Ext.onReady(function() {
	 <cms:havePerm url='/analysis/downloadExcel'>
	  Ext.create('Ext.Button', {
	    id:'download',
		 text: '下载商户订单',
	    iconCls:'Daochu',
	    renderTo: Ext.getBody(),
	    handler: function() {
	    	 download();
	    }
	}); 
	  Ext.getCmp('download').btnEl.setStyle('background-color',"white");
	</cms:havePerm>
	
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
		                        		 Ext.example.msg("提示","请选择小于当前时间，并且大于18号的时间");
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
 
	
	    
});
</script>

</head>
<body>
</body>
</html>
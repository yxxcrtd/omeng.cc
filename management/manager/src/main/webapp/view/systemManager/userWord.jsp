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
<title>用户关键词</title>
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
var store;
Ext.onReady(function() {
    var columns = [
                   {header:'序号',xtype: 'rownumberer',width:50},
                   {header:'关键词',dataIndex:'app_key_word'},
                   {header:'添加时间',dataIndex:'join_time'},
                   
               ];
	
    store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true,
        proxy:{  
            type:'ajax',  
            url:'/systemManager/userWordsList',
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'#'  
            }  
        },  
        fields:[  
           {name:'app_key_word'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'join_time'},
           {name:'is_audit'}
           
        ]  
    });
 
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	 var app_key_word=Ext.getCmp('keyword').getValue();
    	 var wordsNum=Ext.getCmp('wordsNum').getValue();
    	 var start_time=Ext.getCmp('start_time').getValue();
		 var off_time=Ext.getCmp('off_time').getValue();
         var new_params = {app_key_word:app_key_word,wordsNum:wordsNum,start_time : start_time,off_time : off_time};    
        Ext.apply(store.proxy.extraParams, new_params);    
    });
    var sm = Ext.create('Ext.selection.CheckboxModel');
    var grid = Ext.create("Ext.grid.Panel",{
    	region: 'center',
    	border: false,
    	store: store,
    	selModel: sm,
        columns: columns,
        region: 'center', //框架中显示位置，单独运行可去掉此段
        loadMask:true, //显示遮罩和提示功能,即加载Loading……  
        forceFit:true, //自动填满表格  
        columnLines:false, //列的边框
        rowLines:true, //设置为false则取消行的框线样式
        dockedItems: [{
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[
        	       '-',
        	       <cms:havePerm url='/systemManager/deletetUserWord'>
                   { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
        	    	   listeners: {
        	    		   click:function(){
        	    			   delUserAll();
        	    		   }
        	    	   }},</cms:havePerm>
        	    	   <cms:havePerm url='/systemManager/exportUserWord'>
        	 	       { xtype: 'button', text: '导出',iconCls:'Daochu',
        	     	    	   listeners: {
        	     	    		   click:function(){
        	     	    			   exportAll();
        	     	    		   }
        	     	    	   }
        	            }</cms:havePerm>
                 ],
        },{
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[ {xtype: 'textfield',id:'keyword',name: 'keyword',fieldLabel: '关键词',labelAlign:'left',labelWidth:60},'-',
 	       { xtype: 'numberfield',id:'wordsNum',name: 'wordsNum',fieldLabel: '字数',labelAlign:'left',labelWidth:60},'-',
        	       { xtype: 'datetimefield',id: 'start_time',name: 'start_time',format : 'Y-m-d',fieldLabel: '开始时间',labelAlign:'left',labelWidth:60},'-',
        	       { xtype: 'datetimefield',id: 'off_time',name: 'off_time',format : 'Y-m-d',fieldLabel: '结束时间',labelAlign:'left',labelWidth:60},'-',
        	       <cms:havePerm url='/systemManager/userWordsList'>
        	       { xtype: 'button',id:'select', text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			     var app_key_word=Ext.getCmp('keyword').getValue();
    	    		    	 var wordsNum=Ext.getCmp('wordsNum').getValue();
    	    		    	 var start_time=Ext.getCmp('start_time').getValue();
    	    				 var off_time=Ext.getCmp('off_time').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,start:0,app_key_word:app_key_word,wordsNum:wordsNum,start_time : start_time,off_time : off_time}}); 
    	    		   }
    	    		   }}</cms:havePerm>
                   ]
        }],
        bbar : Ext.create("Ext.ux.CustomSizePagingToolbar", {// 在表格底部 配置分页
            displayInfo : true,
            store : store
        })
    });
  //加载数据  
    store.load({params:{start:0,limit:20}}); 
  
    // 表格配置结束
    grid.on("itemcontextmenu",function(view,record,item,index,e){  
        e.preventDefault();  
        contextmenu.showAt(e.getXY());  
    });
    
    grid.on("itemdblclick",function(grid, row){
    	//Ext.Msg.alert("系统提示","你双击啦！ID为："+row.data.id);  
    });
    
	// 整体架构容器
	Ext.create("Ext.container.Viewport", {
		layout : 'border',
		autoHeight: true,
		border: false,
		items : [grid]
	});
	 //导出所有用户关键词
	function exportAll() {
		 var app_key_word=Ext.getCmp('keyword').getValue();
    	 var wordsNum=Ext.getCmp('wordsNum').getValue();
    	 var start_time=Ext.getCmp('start_time').getValue();
		 var off_time=Ext.getCmp('off_time').getValue();
         window.location.href = '/systemManager/exportUserWord?app_key_word='+app_key_word+'&wordsNum='+wordsNum+'&start_time='+start_time+'&off_time='+off_time;
	};
	//订单删除
	 function delUserAll()  
    {  
         //grid中复选框被选中的项  
           
         var rows = grid.getSelectionModel().getSelection();  
         //user_id：所有选中的用户Id的集合使用','隔开，初始化为空    
         var app_key_word = '';  
         for(var i = 0;i<rows.length;i++)  
         {  
            if(i>0)  
            {  
            	app_key_word = app_key_word+','+rows[i].get('app_key_word');  
            }else{  
            	app_key_word = app_key_word+rows[i].get('app_key_word');  
            }  
         }  
         //没有选择要执行操作的对象  
           
         if(app_key_word == "")  
         {  
        	Ext.example.msg("提示","请选择要删除的对象！");
            return ;  
         }else{  
            Ext.Msg.confirm("提示信息","请确定要执行删除操作吗?",function (btn){  
                if(btn == 'yes')  
                {  
                    Ext.Ajax.request({  
                        url:"/systemManager/deletetUserWord",  
                        params:{app_key_word:app_key_word},  
                        method:'post',  
                        success:function(o){  
                        	Ext.example.msg("提示","关键词删除成功！");
                            store.reload();  
                            return ;  
                        },  
                        failure:function(form,action){  
                        	Ext.example.msg("提示","关键词删除失败！");
                        }  
                    });    
                }  
            });  
         }  
    } ;
	    
});


//*********************服务商信息认证start*********************
var audit=function(app_key_word,join_time,remark,is_audit){

	if(is_audit==2){
	   // Ext.example.msg("拒绝原因",remark);
		Ext.Msg.alert("拒绝原因", remark);
	}else if(is_audit==0){
		certificate(app_key_word,join_time,remark,is_audit);
	}
};
var defineStore = new Ext.data.SimpleStore({
 	fields:['type','name'],
 	data:[['1','字数不符'],
 	      ['2','词语不文雅'],
 	      ['3','其他']
 	]
 });
 	   function  certificate(app_key_word,join_time,remark,is_audit){  
 		defineStore.load();
		var cert_winForm =  Ext.create('Ext.form.Panel', {  
	                frame: true,   //frame属性  
	                //title: 'Form Fields',  
	                width: 400,
	                height:300,
	                bodyPadding:5,  
	                //renderTo:"panel21",  
	                fieldDefaults: {  
	                    labelAlign: 'left',  
	                    labelWidth: 90,  
	                    anchor: '100%'  
	                },  
	                items: [{  
	                    //显示文本框，相当于label  
	                    xtype: 'displayfield',   
	                    name: 'displayfield1',  
//	                    fieldLabel: 'Display field',  
	                    value: '审核关键词'  
	                     
	                }, 
	                 {   
	                    xtype: 'textfield', 
	                    name: 'app_key_word',  
	                    fieldLabel: '关键词'
	                }, {
	    				xtype : 'datetimefield',
	    				name : 'join_time',
	    				format : 'Y-m-d',
	    				fieldLabel : '添加时间'
	    			},{   
	                    xtype: 'textfield', 
	                    name: 'remark',  
	                    fieldLabel: '备注',
	                    hidden:true
	                }
	               
	               ]  
	            });  
	    //创建window面板，表单面板是依托window面板显示的  
	    
	     cert_winForm.form.findField('app_key_word').setValue(app_key_word);
	     cert_winForm.form.findField('join_time').setValue(join_time);

	    var certwindow = Ext.create('Ext.window.Window',{  
	              title : "审核关键词",  
	              width: 450,
	              height:350,
	              autoScroll: true, 
	              //height : 120,  
	              //plain : true,  
	              iconCls : "addicon",  
	              // 不可以随意改变大小  
	              resizable : true,  
	              // 是否可以拖动  
	              draggable:true,  
	              collapsible : true, // 允许缩放条  
	              closeAction : 'close',  
	              closable : true,  
	              // 弹出模态窗体  
	              modal : 'true',  
	              buttonAlign : "center",  
	              bodyStyle : "padding:0 0 0 0",  
	              items : [cert_winForm],  
	              buttons : [{  
	                         text : "通过",  
	                         minWidth : 70,  
	                         handler : function() {  
	                            if (cert_winForm.getForm().isValid()) {
	                             Ext.Msg.confirm("提示信息","确定审核通过吗?",function (btn){  
	                              if(btn == 'yes')  
	                              {  
	  	                            	cert_winForm.getForm().submit({  
	  	                                          url :'/systemManager/AuditUserWord',  
	  	                                           //等待时显示 等待  
	  	                                          waitTitle: '请稍等...',  
	  	                                          waitMsg: '正在提交信息...',  
	  	                                            
	  	                                          success: function(fp, o) {  
	  	                                              if (o.result== true) { 
	  	                                     	      Ext.example.msg("提示","审核信息保存成功！");
	  	                                              store.reload();
	  	                                              certwindow.close(); //关闭窗口  
	  	                                              return ;  
	  	                                              }else {  
	  	                                        	      Ext.example.msg("提示","审核信息保存失败！");
	  	                                              }  
	  	                                          },  
	  	                                          failure: function() { 
	  	                                 	         Ext.example.msg("提示","审核信息保存失败！");
	  	                                          }  
	  	                                       });    
	                              }  
	                          });  

	                            }  
	                         }  
	                     }, {  
	                         text : "不通过",  
	                         minWidth : 70,  
	                         handler : function() {  
	                            if (cert_winForm.getForm().isValid()) {
	                            	
	                            	definprompt();
	                            	
	                            }  
	                         }  
	                     }]  
	           });  
	       certwindow.show();  
	    //自定义弹出框
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
	 		    text :"请输入不通过原因:"
	 		   },{
	   				xtype : 'combobox',
 					name : 'txt',		
 					valueField : 'type',
 					displayField : 'name',
 					editable:true,
 					store : defineStore,
 					hiddenName:'',
 					allowBlank	:false,
 					queryMode : 'local'
 				}],  
	 		   buttons : [{
	 		    text : '确定',
	 		    handler : function(){
	 		    
                    var txt=formPanel.form.findField('txt').getRawValue();
	              	cert_winForm.form.findField('remark').setValue(txt);
	              	  cert_winForm.getForm().submit({  
	                      url :'/systemManager/RefuseUserWord',  
	                       //等待时显示 等待  
	                      waitTitle: '请稍等...',  
	                      waitMsg: '正在提交信息...',  
	                        
	                      success: function(fp, o) {  

	                          if (o.result== true) { 
	                 	      Ext.example.msg("提示","审核信息保存成功！");
	                          store.reload();
	                          certwindow.close(); //关闭窗口  
	                          formPanel.close();
	                          win.close();
	                          return ;  
	                          }else {  
	                 	         Ext.example.msg("提示","审核信息保存失败！");
	                          }  
	                      },  
	                      failure: function() {  
	                         Ext.example.msg("提示","审核信息保存失败！");
	                      }  
	                   });     
	               
	         
	 		    }
	 		   }, {
	 		    text : '取消',
	 		    handler : function(){
	 		     formPanel.close();
	 		     win.hide();
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
	 		   listeners: {
	 			    close : function(){
	 			    	formPanel.close();
	 			    	
	 			    }
	 			},
	 		   items:[formPanel]
	 		  });
	 		    win.show();
	           }
	  	     };
</script>

</head>
<body>
</body>
</html>
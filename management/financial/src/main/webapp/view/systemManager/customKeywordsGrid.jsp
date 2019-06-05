<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ include file="../common/common.jsp"%>
<%
	response.setHeader("Pragma","No-cache");
	response.setHeader("Cache-Control","no-cache");
	response.setHeader("Expires","0");
	request.setCharacterEncoding("UTF-8");	
	
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>不拆分关键词</title>
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
    var columns = [
                   {header:'序号',xtype: 'rownumberer',width:50},
                   {header:'ID',dataIndex:'id',sortable:true,fixed:false,hidden:true},
                   {header:'关键词',dataIndex:'keyword',align:'center',sortable:true,fixed:false},
                   {header:'字数',dataIndex:'wordsNum',align:'center',sortable:true,fixed:false}
                 
               ];
    
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
        proxy:{  
            type:'ajax',  
            url:'/systemManager/customKeywordsList',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'#'  
            }  
        },  
        fields:[  
              //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
          {name:'id'},
          {name:'keyword'},
          {name:'wordsNum'}
        ]  
    });
    
	
	
    
    
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	 var keyword=Ext.getCmp('keyword').getValue();
     	 var wordsNum=Ext.getCmp('wordsNum').getValue();
		
         var new_params = {keyword:keyword,wordsNum:wordsNum};    
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
        dockedItems: [
    {
	xtype:'toolbar',
	dock:'top',
	displayInfo: true,
	items:[
	    	   <cms:havePerm url='/systemManager/addCustomKeywords'>
	           { xtype: 'button', id:'add', text: '添加',iconCls:'NewAdd',
		    	   listeners: {
		    		   click:function(){
		    			   showform();
		    		   }
		       }},'-',
		       </cms:havePerm>
		       <cms:havePerm url='/systemManager/deleteCustomKeywords'>
	           { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
		    	   listeners: {
		    		   click:function(){
		    			   deleteRecord();
		    		   }
		    	}},'-',
			    </cms:havePerm>
			    <cms:havePerm url='/systemManager/importCustomKeywords'>
		        { xtype: 'button', id:'import', text: '导入',iconCls:'daoru',
			    	   listeners: {
			    		   click:function(){
			    			   importForm();
			    		   }
			    }},'-',
		        </cms:havePerm>
		        <cms:havePerm url='/systemManager/exportCustomKeywords'>
     	       { xtype: 'button', text: '导出',iconCls:'Daochu',
         	    	   listeners: {
         	    		   click:function(){
         	    			   exportAll();
         	    		   }
         	    	   }
                }</cms:havePerm>
          ],
      },
      {
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{ xtype: 'textfield',id:'keyword',name: 'keyword',fieldLabel: '关键词',labelAlign:'left',labelWidth:60},'-',
        	       { xtype: 'numberfield',id:'wordsNum',name: 'wordsNum',fieldLabel: '字数',labelAlign:'left',labelWidth:60},'-',
					'-',
        	       <cms:havePerm url='/systemManager/customKeywordsList'>
        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var keyword=Ext.getCmp('keyword').getValue();
    	    			   var wordsNum=Ext.getCmp('wordsNum').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,keyword:keyword,wordsNum:wordsNum}}); 
    	    		   }
    	    		   }}
        	        </cms:havePerm>
        	       ]
        }],
        bbar : Ext.create("Ext.ux.CustomSizePagingToolbar", {// 在表格底部 配置分页
            displayInfo : true,
            store : store
        })
    });
  //加载数据  
    store.load({params:{start:0,limit:20}}); 
  
   
    
    grid.on("itemcontextmenu",function(view,record,item,index,e){  
        e.preventDefault();  
        contextmenu.showAt(e.getXY());  
    });
    

	// 整体架构容器
	Ext.create("Ext.container.Viewport", {
		layout : 'border',
		autoHeight: true,
		border: false,
		items : [grid]
	});
	
	
	 //导出所有关键词
	function exportAll() {
		 var keyword=Ext.getCmp('keyword').getValue();
     	 var wordsNum=Ext.getCmp('wordsNum').getValue();
		
        window.location.href = '/systemManager/exportCustomKeywords?keyword='+keyword+'&wordsNum='+wordsNum;
	};
	

	//删除字典值
	function deleteRecord()  
    {  
         //grid中复选框被选中的项  
         var records = grid.getSelectionModel().getSelection();  
       	 if(records.length <= 0){
       	     Ext.example.msg("提示","请选择要删除的对象！");
             return ;  
    	 }
         //ids：所有选中的用户Id的集合使用','隔开，初始化为空  
         var ids = '';  
         for(var i = 0;i<records.length;i++)  
         {  
            if(i>0){  
            	ids = ids+','+records[i].get('id');  
            }else{  
            	ids = ids+records[i].get('id');  
            }  
         }  
         Ext.Msg.confirm("提示信息","请确定要执行删除操作吗?",function (btn){  
             if(btn == 'yes')  
             {  
                 Ext.Ajax.request({  
                     url:"/systemManager/deleteCustomKeywords",  
                     params:{ids:ids},  
                     method:'post',  
                     success:function(o){   
                    	 Ext.example.msg("提示","删除成功！");
                         store.reload();  
                         return ;  
                     },  
                     failure:function(form,action){ 
                    	 Ext.example.msg("提示","删除失败！");
                     }  
                 });    
             }  
         });  
    } ;
      
    
  //form表单
	var showform = function() {
 		
		var add_winForm = Ext.create('Ext.form.Panel', {
			frame : true, //frame属性  
			//title: 'Form Fields',  
			width : 440,
			height : 400,
			bodyPadding : 5, 
			fieldDefaults : {
				labelAlign : 'left',
				labelWidth : 90,
				anchor : '100%'
			},
			items : [
{  
    //显示文本框，相当于label  
    xtype: 'displayfield',   
    name: 'displayfield1',  
    value: '新增关键词'  
     
},{ 
	xtype: 'textfield',
	name: 'keyword',
	fieldLabel: '关键词',
	allowBlank	:false 
}
			  
   	]

		});

	  
		
		//创建window面板，表单面板是依托window面板显示的  
		var syswin = Ext.create('Ext.window.Window', {
			title : '新增关键词',
			width : 450,
			height : 380,
			//height : 120,  
			//plain : true,  
			iconCls : "addicon",
			// 不可以随意改变大小  
			resizable : false,
			// 是否可以拖动  
			// draggable:false,  
			collapsible : true, // 允许缩放条  
			closeAction : 'close',
			closable : true,
			// 弹出模态窗体  
			modal : 'true',
			buttonAlign : "center",
			bodyStyle : "padding:0 0 0 0",
			items : [ add_winForm ],
			buttons : [ {
				text : "保存",
				minWidth : 70,
				handler : function() {
					if (add_winForm.getForm().isValid()) {
						
						//var st = add_winForm.form.findField('serviceType').getValue();
						//if(st==null||st==''){
						// 	Ext.example.msg("提示","请选择服务类型！");
						//	return;
						//}
						add_winForm.getForm().submit({
							url : '/systemManager/addCustomKeywords',
							//等待时显示 等待  
							waitTitle : '请稍等...',
							waitMsg : '正在提交信息...',
						    method : "POST",  
						    success : function(fp, o) {
                                if (o.result.data == 1) {  
                                    syswin.close(); //关闭窗口  
                                    Ext.example.msg("提示",o.result.message);
                            		add_winForm.form.reset(); 
                                    store.reload();  
                                }else {  
                              	    Ext.example.msg("提示",o.result.message); 
                                }  
							},
							failure : function() {
		                         syswin.close(); //关闭窗口  
                                 Ext.example.msg("提示","保存成功");
                         		 add_winForm.form.reset(); 
                                 store.reload();  
                                 return;
							}
						});
					}
				}
			}, {
				text : "关闭",
				minWidth : 70,
				handler : function() {
					add_winForm.form.reset(); 
					syswin.close();
				}
			} ]
		});
		syswin.show();
	};
    
	
	// =======导入======
	var importForm = function(d) {
		var import_winForm = Ext.create('Ext.form.Panel', {
			frame : true, //frame属性  
			//title: 'Form Fields',  
			width : 440,
			height : 300,
			bodyPadding : 5, 
			fieldDefaults : {
				labelAlign : 'left',
				labelWidth : 90,
				anchor : '100%'
			},
			items : [{  
                 //显示文本框，相当于label  
                 xtype: 'displayfield',   
                 name: 'displayfield1',  
                 value: '导入关键词库'  
                  
             },
    {
     layout:'column',
	 border:false,
     items:[{ 
         fieldLabel:'选择Excel文件',
         name:'upload',
         xtype:'fileuploadfield',  
         anchor:'80%',  
         emptyText:'请选择Excel文件', 
         regex:/(xls)|(xlsx)$/i,
         buttonText: '选择',
         selectOnFocus:true  
     },{xtype: 'displayfield',   
         name: 'displayfield',  
         value: ' 文件大小10M以内，格式xls、xlsx'
         }]
        }]

		});

	    var title = '导入关键词库';
		
		//创建window面板，表单面板是依托window面板显示的  
		var syswin = Ext.create('Ext.window.Window', {
			title : title,
			width : 450,
			height : 280,
			//height : 120,  
			//plain : true,  
			iconCls : "addicon",
			// 不可以随意改变大小  
			resizable : false,
			// 是否可以拖动  
			// draggable:false,  
			collapsible : true, // 允许缩放条  
			closeAction : 'close',
			closable : true,
			// 弹出模态窗体  
			modal : 'true',
			buttonAlign : "center",
			bodyStyle : "padding:0 0 0 0",
			items : [ import_winForm ],
			buttons : [ {
				text : "保存",
				minWidth : 70,
				handler : function() {
					if (import_winForm.getForm().isValid()) {
						import_winForm.getForm().submit({
							url : '/systemManager/importCustomKeywords',
							//等待时显示 等待  
							waitTitle : '请稍等...',
							waitMsg : '正在提交信息...',
							success : function(fp, o) {
								    Ext.example.msg("提示","导入成功！");
									syswin.close(); //关闭窗口  
									import_winForm.form.reset(); 
									store.reload();
							},
							failure : function() {
							    Ext.example.msg("提示","导入失败！");
							}
						});
					}
				}
			}, {
				text : "关闭",
				minWidth : 70,
				handler : function() {
					import_winForm.form.reset(); 
					syswin.close();
				}
			} ]
		});
		syswin.show();
	};
	
});



</script>
</head>
<body>
</body>
</html>
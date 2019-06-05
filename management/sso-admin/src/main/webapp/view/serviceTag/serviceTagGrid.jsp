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
<title>服务标签管理</title>
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
                   {header:'序号',xtype: 'rownumberer',width:50,align:'center'},
                   {header:'编号',dataIndex:'id',hidden:true,align:'center'},
                   {header:'标签',dataIndex:'tag',align:'center'},
                   {header:'推荐标签',dataIndex:'is_recommend',align:'center',renderer:function(value){  
                       if(value=='0'){  
                           return "<span style='color:red;font-weight:bold';>是</span>";  
                       } else{  
                           return "<span style='color:green;font-weight:bold';>否</span>";  
                       }
           		   }},
                   {header:'状态',dataIndex:'is_audit',align:'center',renderer:function(value){  
                       if(value=='0'){  
                           return "<span style='color:green;font-weight:bold';>启用</span>";  
                       } else{  
                           return "<span style='color:red;font-weight:bold';>禁用</span>";  
                       }
           		   }},
                   {header:'创建时间',dataIndex:'join_time',align:'center'}
               ];
	//创建store数据源
    
    //列表展示数据源
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true,
        proxy:{  
            type:'ajax',  
            url:'/serviceTag/serviceTagList',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'ext'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'tag'},  
           {name:'is_recommend'},
           {name:'is_audit'},
           {name:'join_time'}
        ]  
    });

    // 启用状态
    var auditComboStore = new Ext.data.SimpleStore({
    	fields:['name','value'],
    	data:[
    	      ['启用','0'],
    	      ['禁用','1']
    	]
    });

    // 是否推荐
    var recommendComboStore = new Ext.data.SimpleStore({
    	fields:['name','value'],
    	data:[
    	      ['是','0'],
    	      ['否','1']
    	]
    });
    //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
		var tag = Ext.getCmp('tag').getValue();
		var is_recommend=Ext.getCmp('is_recommend').getValue();
		var is_audit=Ext.getCmp('is_audit').getValue();
        var new_params = {tag:tag,is_recommend:is_recommend,is_audit:is_audit};    
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
        	       <cms:havePerm url='/serviceTag/addServiceTag'>
        	       { xtype: 'button',id:'add', text: '增加',iconCls:'NewAdd',
        	    	   listeners: {
        	    		   click:function(){
        	    			   showform(null);
        	    		   }
        	    	   }
        	       },'-',
        	       </cms:havePerm> 
        	       <cms:havePerm url='/serviceTag/deleteServiceTag'> 
                   { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
        	    	   listeners: {
        	    		   click:function(){
        	    			   delRecordAll();
        	    		   }
        	      }}
        	      </cms:havePerm> 

        	      ],
        },{
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{ xtype: 'textfield',id: 'tag',name: 'tag',fieldLabel: '标签',labelAlign:'left',labelWidth:60},'-',
	     	        {
				     	xtype:'combo',
						fieldLabel : '是否推荐',
						id: 'is_recommend',
					    store : recommendComboStore,
		    		    name:'is_recommend',
		   			    triggerAction: 'all',
		   			    displayField: 'name',
		   			    valueField: 'value',
		   			    value:'',
		   			    labelWidth:60,
					    mode:'local'
				   },'-',
     	           {
			     	xtype:'combo',
					fieldLabel : '启用状态',
					id: 'is_audit',
				    store : auditComboStore,
	    		    name:'is_audit',
	   			    triggerAction: 'all',
	   			    displayField: 'name',
	   			    valueField: 'value',
	   			    value:'',
	   			    labelWidth:60,
				    mode:'local'
			        },'-',
				   <cms:havePerm url='/serviceTag/serviceTagList'> 
				   { xtype: 'button', text: '查询',id:'select',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var tag=Ext.getCmp('tag').getValue();
    	    			   var is_recommend=Ext.getCmp('is_recommend').getValue();
    	    			   var is_audit=Ext.getCmp('is_audit').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,tag:tag,is_recommend:is_recommend,is_audit:is_audit}}); 
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
    <cms:havePerm url='/serviceTag/editServiceTag'> 
    grid.on("itemdblclick",function(grid, row){
    	showform(row.data);
    });
    </cms:havePerm> 

    
	// 整体架构容器
	Ext.create("Ext.container.Viewport", {
		layout : 'border',
		autoHeight: true,
		border: false,
		items : [grid]
	});
	
	
	
	//删除
	 function delRecordAll()  
     {  
          //grid中复选框被选中的项  
          var rows = grid.getSelectionModel().getSelection();  
          //user_id：所有选中的用户Id的集合使用','隔开，初始化为空    
          var id = '';  
          for(var i = 0;i<rows.length;i++)  
          {  
             if(i>0)  
             {  
                 id = id+','+rows[i].get('id');  
             }else{  
                 id = id+rows[i].get('id');  
             }  
          }  
          //没有选择要执行操作的对象  
            
          if(id == "")  
          {  
			 Ext.example.msg("提示","请选择要删除的对象！");
             return ;  
          }else{  
             Ext.Msg.confirm("提示信息","请确定要执行删除操作吗?",function (btn){  
                 if(btn == 'yes')  
                 {  
                     Ext.Ajax.request({  
                         url:"/serviceTag/deleteServiceTag",  
                         params:{ids:id},  
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
          }  
     } ;


   
 	var showform=function(d){

 	    var add_winForm =  Ext.create('Ext.form.Panel', {  
 	                frame: true,   //frame属性  
 	                //title: 'Form Fields',  
 	                width: 440, 
 	                height:140,
 	                // autoScroll: true, 
 	                bodyPadding:5,
 	                fileUpload:true,
 	                //renderTo:"panel21",  
 	                fieldDefaults: {  
 	                    labelAlign: 'left',  
 	                    labelWidth: 90,  
 	                    anchor: '90%'  
 	                },  
 	                items: [{
	    				xtype: 'hidden',
	    				name: "id"
	    			},{  
 	                    //输入启动页名称  
 	                    xtype: 'textfield', 
 	                    name: 'tag',  
 	                    fieldLabel: '标签',
 	                    allowBlank: false
 	                },{
 	                	xtype:'combo',
					    store : auditComboStore,
		    		    width:80,
		    		    name:'is_audit',
		   			    triggerAction: 'all',
		   			    displayField: 'name',
		   			    valueField: 'value',
		   			    hiddenName:'value',
		   			    value:'1',
					    mode:'local',
					    fieldLabel: '启用状态' 
				   },{
 	                	xtype:'combo',
					    store : recommendComboStore,
		    		    width:80,
		    		    name:'is_recommend',
		   			    triggerAction: 'all',
		   			    displayField: 'name',
		   			    valueField: 'value',
		   			    hiddenName:'value',
		   			    value:'0',
					    mode:'local',
					    fieldLabel: '是否推荐' 
				   }
 
 	               ]  
 	            });  
 	    
	    var title = '新增服务标签';
	    var reqName = 'addServiceTag';
	    //================判断是编辑还是新增===============
	    	if(d!=null&&d.id!=null&&d.id!=0){
	    		// 是编辑状态
	    	    add_winForm.form.findField('id').setValue(d.id);
	    	    add_winForm.form.findField('tag').setValue(d.tag);  
	    	    add_winForm.form.findField('is_audit').setValue(d.is_audit+'');
	    	    add_winForm.form.findField('is_recommend').setValue(d.is_recommend+'');
	        	title = '编辑服务标签';
	        	reqName = 'editServiceTag';
	    	}
	

        //================判断是编辑还是新增===============
 	    
 	    //创建window面板，表单面板是依托window面板显示的  
 	    var syswin = Ext.create('Ext.window.Window',{  
 	              title : title,  
 	              width : 470, 
 	              height: 220,
 	              //height : 120,  
 	              //plain : true,  
 	              iconCls : "addicon",  
 	              // 不可以随意改变大小  
 	              resizable : false,  
 	              //autoScroll: true,  
 	              // 是否可以拖动  
 	              // draggable:false,  
 	              collapsible : true, // 允许缩放条  
 	              closeAction : 'close',  
 	              closable : true,  
 	              // 弹出模态窗体  
 	              modal : 'true',  
 	              buttonAlign : "center",  
 	              bodyStyle : "padding:0 0 0 0",  
 	              items : [add_winForm],  
 	              buttons : [{  
 	                         text : "保存",  
 	                         minWidth : 70,  
 	                         handler : function() {  
 	                        	if (add_winForm.getForm().isValid()) {
 	                            add_winForm.getForm().submit({  
 	                                          url :'/serviceTag/'+reqName,  
 	                                           //等待时显示 等待  
 	                                          waitTitle: '请稍等...',  
 	                                          waitMsg: '正在提交信息...',  
 	                                          success: function(fp, o) {
 	                                        	 if (o.result.data == 1) {  
 	                                        		  syswin.close(); //关闭窗口  
	                                             	  Ext.example.msg("提示",o.result.message);
	                                                  store.reload();  
	                                              }else {  
	                                            	  Ext.example.msg("提示",o.result.message);
	                                              }  
 	                                          },  
 	                                          failure: function() {
 	                                          	 Ext.example.msg("提示",o.result.message);
 	                                          }  
 	                                       });  
 	                            } } 
 	                         }  
 	                    , {  
 	                         text : "关闭",  
 	                         minWidth : 70,  
 	                         handler : function() {  
 	                            syswin.close();  
 	                         }  
 	                     }]  
 	           });  
 	    syswin.show();  
 	    };
 	 
});



</script>

</head>
<body>
</body>
</html>
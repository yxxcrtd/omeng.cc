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
<title>第三方app管理</title>
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
                   {header:'名称',dataIndex:'name',align:'center'},
                   {header:'链接地址',dataIndex:'link',align:'center'},
                   {header:'状态',dataIndex:'status',align:'center',renderer:function(value){  
                       if(value=='0'){  
                           return "<span style='color:green;font-weight:bold';>启用</span>";  
                       } else if(value=='1'){  
                           return "<span style='color:red;font-weight:bold';>禁用</span>";  
                       }
           		   }},
                   {header:'排序',dataIndex:'rank',align:'center'}
                   
               ];
	//创建store数据源
    
    //列表展示数据源
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true,
        proxy:{  
            type:'ajax',  
            url:'/slider/getThirdApp',
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'ext'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'name'},  
           {name:'link'},
           {name:'status'},
           {name:'rank'}
        ]  
    });
 // 固定状态
    var StatusStore = new Ext.data.SimpleStore({
    	fields:['name','value'],
    	data:[['全部',''],
    	      ['启用','0'],
    	      ['禁用','1']
    	]
    });

    //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	var name=Ext.getCmp('name').getValue();
    	var status=Ext.getCmp('status').getValue();
        var new_params = { name:name,status:status};    
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
        	       <cms:havePerm url='/slider/editThirdApp'>
        	       { xtype: 'button',id:'add', text: '增加',iconCls:'NewAdd',
        	    	   listeners: {
        	    		   click:function(){
        	    			   showform(null);
        	    		   }
        	    	   }
        	       },</cms:havePerm>
        	       <cms:havePerm url='/slider/deleteThirdApp'>
                   { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
        	    	   listeners: {
        	    		   click:function(){
        	    			   delUserAll();
        	    		   }
        	    	   }},'-',</cms:havePerm>
        	    	  
        	      ],
        },{
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{ xtype: 'textfield',id: 'name',name: 'name',fieldLabel: '名称',labelAlign:'left',labelWidth:60},'-',
        	       {xtype:'combo',fieldLabel : '状态',id: 'status',store : StatusStore,name:'status', displayField: 'name',
   			    valueField: 'value',editable:false,labelWidth:60,mode:'local'},'-',
	             
   	                 <cms:havePerm url='/slider/getThirdApp'>
        	       { xtype: 'button', text: '查询',id:'select',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var name=Ext.getCmp('name').getValue();
    	    		       var status=Ext.getCmp('status').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,name:name,status:status}}); 
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
   
    
    grid.on("itemcontextmenu",function(view,record,item,index,e){  
        e.preventDefault();  
        contextmenu.showAt(e.getXY());  
    });
    
    <cms:havePerm url='/slider/editThirdApp'>
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
	
	//推荐位删除
	 function delUserAll()  
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
                         url:"/slider/deleteThirdApp",  
                         params:{id:id},  
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
 		var rows = grid.getSelectionModel().getSelection();
 	    var add_winForm =  Ext.create('Ext.form.Panel', {  
 	                frame: true,   //frame属性  
 	                //title: 'Form Fields',  
 	                width: 470, 
 	                height:300,
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
 	                    //显示文本框，相当于label  
 	                    xtype: 'displayfield',   
 	                    name: 'displayfield1',  
 	                    value: '填写推荐位相关信息'  
 	                     
 	                }, {
	    				xtype: 'hidden',
	    				name: "id"
	    			},{  
 	                    //输入名称
 	                    xtype: 'textfield', 
 	                    name: 'name',  
 	                    fieldLabel: '名称',
 	                    allowBlank	:false
 	                },{  
 	                    //输入链接地址
 	                    xtype: 'textfield', 
 	                    name: 'link',  
 	                    fieldLabel: '链接地址',
 	                    allowBlank	:false
 	                },{
 	             	   //下拉列表框  
 	                   xtype: 'combobox', //9  
 	                   fieldLabel: '状态',
 	                   displayField: 'value',
 	                   valueField : 'key',
 	                   name:'status',
 	                   editable:false,
 	                   store: Ext.create('Ext.data.Store', {  
 	                       fields: [  
 	                         {name:'key'},{ name: 'value'}  
 	                         ],  
 	                          data: [  
 	                         { 'key':'0','value': '启用' },  
 	                         { 'key':'1','value': '禁用' } 
 	                         ]  
 	                   }), width:220
 	   			    },{
 	   					// 输入排序
 	   					xtype : 'numberfield',
 	   					name : 'rank',
 	   					minValue: 0,
 	   				    maxValue: 100,
 	   					fieldLabel : '排序'
 	   				}
 	               ]  
 	            });  
 	    
	    var title = '新增第三方APP';
	    var reqName = 'editThirdApp';
	    //================判断是编辑还是新增===============
	    	if(d!=null&&d.id!=null&&d.id!=0){
	    		// 是编辑状态
	    	    add_winForm.form.findField('id').setValue(rows[0].get("id"));
	    	    add_winForm.form.findField('name').setValue(rows[0].get("name"));  
	    	    add_winForm.form.findField('link').setValue(rows[0].get("link"));
	    	    add_winForm.form.findField('status').setValue(rows[0].get("status")+'');
	    	    add_winForm.form.findField('rank').setValue(rows[0].get("rank")); 
	        	title = '编辑第三方APP';
	        	reqName = 'editThirdApp';
	    	}
	

        //================判断是编辑还是新增===============
 	    
 	    //创建window面板，表单面板是依托window面板显示的  
 	    var syswin = Ext.create('Ext.window.Window',{  
 	              title : title,  
 	              width : 500, 
 	              height: 300,
 	              //height : 120,  
 	              //plain : true,  
 	              iconCls : "addicon",  
 	              // 不可以随意改变大小  
 	              resizable : true,  
 	              autoScroll: true,  
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
 	                                          url :'/slider/'+reqName,  
 	                                           //等待时显示 等待  
 	                                          waitTitle: '请稍等...',  
 	                                          waitMsg: '正在提交信息...',  
 	                                            
 	                                          success: function(fp, o) {  
 	                                              if (o.result== true) { 
 	                                               	 Ext.example.msg("提示","保存成功！");
 	                                                 syswin.close(); //关闭窗口  
 	                                                 store.reload();  
 	                                              }else { 
 	                                               	 Ext.example.msg("提示","保存失败！");
 	                                              }  
 	                                          },  
 	                                          failure: function() {
 	                                          	 Ext.example.msg("提示","保存失败！");
 	                                          }  
 	                                       });  
 	                            }else{
 	                            	Ext.example.msg("提示","请填写完整信息！");}
 	                        	} 
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
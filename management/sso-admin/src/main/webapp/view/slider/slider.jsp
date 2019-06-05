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
<title>广告管理</title>
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
                   {header:'广告名称',dataIndex:'name',align:'center'},
                   {header:'开始时间',dataIndex:'join_time',align:'center'},
                   {header:'过期时间',dataIndex:'overdue_time',align:'center'},
                   {header:'启用状态',dataIndex:'slider_status',align:'center',renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:green;font-weight:bold';>启用</span>";  
                       } else if(value=='2'){  
                           return "<span style='color:red;font-weight:bold';>禁用</span>";  
                       }
           		   }},
                   {header:'广告类型',dataIndex:'slider_type',align:'center',renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:red;font-weight:bold';>外链</span>";  
                       } else if(value=='2'){  
                           return "<span style='color:green;font-weight:bold';>展示</span>";  
                       }else if(value=='3'){  
                           return "<span style='color:green;font-weight:bold';>内嵌</span>";  
                       }
           		   }},
           		   {header:'终端',dataIndex:'client',align:'center',renderer:function(value){  
                    if(value=='1'){  
                        return "<span style='color:red;font-weight:bold';>用户端</span>";  
                    } else if(value=='2'){  
                        return "<span style='color:green;font-weight:bold';>商户端</span>";  
                    }
        		   }},
                   {header:'排序',dataIndex:'rank',align:'center'},
                   {header:'链接地址',dataIndex:'link_url',align:'center'},
                   {header:'广告配图',dataIndex:'pics_path',hidden:true,align:'center',renderer:function(value){
                	   return "<span style='color:green;font-weight:bold';>下载</span>";
                   }},
                   {header:'内嵌别名',dataIndex:'tag',align:'center',hidden:true},
               ];
	//创建store数据源
    
    //列表展示数据源
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true,
        proxy:{  
            type:'ajax',  
            url:'/slider/getSliders',  
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
           {name:'join_time'},
           {name:'overdue_time'},
           {name:'slider_status'},
           {name:'slider_type'},
           {name:'client'},
           {name:'rank'},
           {name:'link_url'},
           {name:'pics_path'},
           {name:'tag'}
        ]  
    });
 
    // 时间状态
    var timeStatusComboStore = new Ext.data.SimpleStore({
    	fields:['name','value'],
    	data:[['全部','0'],
    	      ['使用中','1'],
    	      ['未开始','2'],
    	      ['已过期','3']
    	]
    });
    //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	var name=Ext.getCmp('name').getValue();
		var time_status=Ext.getCmp('time_status').getValue();
		var status_type=Ext.getCmp('status_type').getValue();  
        var new_params = { name:name,time_status:time_status,status_type:status_type};    
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
        	       <cms:havePerm url='/slider/addSlider'>
        	       { xtype: 'button',id:'add', text: '增加',iconCls:'NewAdd',
        	    	   listeners: {
        	    		   click:function(){
        	    			   showform(null);
        	    		   }
        	    	   }
        	       },</cms:havePerm>
        	       <cms:havePerm url='/slider/deleteSlider'>
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
        	items:[{ xtype: 'textfield',id: 'name',name: 'name',fieldLabel: '广告名称',labelAlign:'left',labelWidth:60},
     	           {
			     	xtype:'combo',
					fieldLabel : '时间状态',
					id: 'time_status',
				    store : timeStatusComboStore,
	    		    name:'time_status',
	   			    triggerAction: 'all',
	   			    displayField: 'name',
	   			    valueField: 'value',
	   			    value:'',
	   			    labelWidth:60,
				    mode:'local'
			        },
        	       { xtype: 'combobox',id:'status_type',name: 'status_type',displayField: 'status_name',valueField : 'status_type',
               		fieldLabel: '启用状态',store: Ext.create('Ext.data.Store', {  
                        fields: [  
   	                          { name: 'status_type'},{ name: 'status_name' }  
   	                          ],  
   	                          data: [  
   	                          { "status_type": "1","status_name": "启用 " },  
   	                          { "status_type": "2","status_name": "禁用" }
   	                          ]  
   	                    }),queryMode: 'local',labelAlign:'left',labelWidth:60},
   	                 <cms:havePerm url='/slider/getSliders'>
        	       { xtype: 'button', text: '查询',id:'select',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var name=Ext.getCmp('name').getValue();
    	    			   var time_status=Ext.getCmp('time_status').getValue();
    	    			    var status_type=Ext.getCmp('status_type').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,name:name,time_status:time_status,status_type:status_type}}); 
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
    <cms:havePerm url='/slider/editSlider'>
    grid.on("itemdblclick",function(grid, row){
    	showform(row.data);
    });
    </cms:havePerm>
    //grid.on("cellclick",function(table, td, cellIndex,record, tr, rowIndex,  e, eOpts){
   // 	if(cellIndex==6){
   // 		window.location.href = '/manager/slider/downLoad?path='+record.get('path');
  //  	}else{
   // 		return false;
   // 	}
   // 	});
    
	// 整体架构容器
	Ext.create("Ext.container.Viewport", {
		layout : 'border',
		autoHeight: true,
		border: false,
		items : [grid]
	});
	
	//广告删除
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
                         url:"/slider/deleteSlider",  
                         params:{id:id},  
                         method:'post',  
                         success:function(o){  
                        	 Ext.example.msg("提示","广告删除成功！");
                             store.reload();  
                             return ;  
                         },  
                         failure:function(form,action){ 
                        	 Ext.example.msg("提示","广告删除失败！");
                         }  
                     });    
                 }  
             });  
          }  
     } ;
    
   //form表单
   	var typeStore = new Ext.data.SimpleStore({
	    	fields:['name','value'],
	    	data:[['外链','1'],
	    	      ['展示','2'],
	    	      ['内嵌','3']
	    	      
	    	]
	    });
   	var statusStore = new Ext.data.SimpleStore({
    	fields:['name','value'],
    	data:[['启用','1'],
    	      ['禁用','2']
    	   
    	]
    });
   
 	var showform=function(d){
 		var pic = '';
 		if(d!=null&&d.id!=null&&d.id!=0){
 			pic = d.pics_path;
 		}
 	    var add_winForm =  Ext.create('Ext.form.Panel', {  
 	                frame: true,   //frame属性  
 	                //title: 'Form Fields',  
 	                width: 470, 
 	                height:470,
 	               // autoScroll: true, 
 	                bodyPadding:5,
 	                fileUpload:true,
 	               // resizable : true,  
  	                //autoScroll: true,  
  	              // 是否可以拖动  
  	                //draggable:true,  
  	               // collapsible : true, // 允许缩放条  
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
 	                    value: '填写广告相关信息'  
 	                     
 	                }, {
	    				xtype: 'hidden',
	    				name: "id"
	    			},{  
 	                    //输入广告名称  
 	                    xtype: 'textfield', 
 	                    name: 'name',  
 	                    fieldLabel: '广告名称',
 	                    allowBlank	:false
 	                },{
 	                	xtype:'combo',
					    store : statusStore,
		    		    width:80,
		    		    name:'slider_status',
		   			    triggerAction: 'all',
		   			    displayField: 'name',
		   			    valueField: 'value',
		   			    hiddenName:'value',
		   			    value:'1',
					    mode:'local',
					    fieldLabel: '启用状态' 
				   },{
 	                	xtype:'combo',
					    store : typeStore,
		    		    width:80,
		    		    name:'slider_type',
		   			    triggerAction: 'all',
		   			    displayField: 'name',
		   			    valueField: 'value',
		   			    hiddenName:'value',
		   			    value:'1',
					    mode:'local',
					    fieldLabel: '广告类型' 
				   },{  
   	                	xtype: 'textfield',   
		                name: 'tag',  
		                fieldLabel: '内嵌别名'
		                  
	                }, {  
 	   	                	xtype: 'textfield',   
  		                    name: 'link_url',  
  		                    fieldLabel: '链接地址'
  		                    //allowBlank	:false
  	                },{  
	                    //排序
	                    xtype: 'numberfield',  
	                    name: 'rank', //10  
	                    fieldLabel: '排序', 
	                    minValue: 1,  
	                    maxValue: 20  
	                }, { xtype: 'datetimefield',
                         name: 'join_time',
                         format : 'Y-m-d',
                         fieldLabel: '开始时间'
                    },
                    { xtype: 'datetimefield',
                        name: 'overdue_time',
                        format : 'Y-m-d',
                        allowBlank	:false,
                        fieldLabel: '过期时间'
                   },{
                 	   //下拉列表框  
                       xtype: 'combobox', //9  
                       fieldLabel: '终端',
                       displayField: 'value',
                       valueField : 'key',
                       name:'client',
                       store: Ext.create('Ext.data.Store', {  
                           fields: [  
                             {name:'key'},{ name: 'value'}  
                             ],  
                              data: [  
                             { 'key':'1','value': '用户端' },  
                             { 'key':'2','value': '商户端' } 
                             ]  
                       }), width:220
       			    },{
                	     layout:'column',
			       		  border:false,
			              items:[{  
		                  layout:'form',
		                  anchor: '70%',
		         		  border:false,
		                  columnWidth:.48,
		                  items:[{ 
		                	    
			                    fieldLabel:'上传图片',
			                    name:'upload',
			                    xtype:'fileuploadfield',  
			                    anchor:'80%',  
			                    emptyText:'请选择文件', 
			                    regex:/(png)|(jpg)$/i,
			                    buttonText: '选择',
			                    selectOnFocus:true  
			                }]
			              },{  
			                  layout:'form',
			                  anchor: '30%',
			         		  border:false,
			                  columnWidth:.48,
			                  items:[{xtype: 'displayfield',   
			 	                    name: 'displayfield',  
			 	                    value: ' 图片大小10M以内，格式jpg、png'
			 	                    }]
				              }]
                   } ,{
	    				xtype: 'hidden',
	    				name: "pics_path"
	    			},{  
                	    xtype: 'box', //或者xtype: 'component',  
                	    width: 300, //图片宽度  
                	    height: 200, //图片高度  
                	    autoEl: {  
                	        tag: 'img',    //指定为img标签  
                	        src: pic    //指定url路径  
                	    }  
                	} 
 	               ]  
 	            });  
 	    
	    var title = '新增广告';
	    var reqName = 'addSlider';
	    //================判断是编辑还是新增===============
	    	if(d!=null&&d.id!=null&&d.id!=0){
	    		// 是编辑状态
	    	    add_winForm.form.findField('id').setValue(d.id);
	    	    add_winForm.form.findField('name').setValue(d.name);  
	    	    add_winForm.form.findField('slider_status').setValue(d.slider_status+'');
	    	    add_winForm.form.findField('slider_type').setValue(d.slider_type+'');
	    	    add_winForm.form.findField('link_url').setValue(d.link_url); 
	    	    add_winForm.form.findField('rank').setValue(d.rank);
	    	    add_winForm.form.findField('client').setValue(d.client+'');
	    	    add_winForm.form.findField('tag').setValue(d.tag);
	    	    add_winForm.form.findField('join_time').setValue(d.join_time); 
	    	    add_winForm.form.findField('overdue_time').setValue(d.overdue_time); 
	    	    add_winForm.form.findField('pics_path').setValue(d.pics_path); 
	        	title = '编辑广告';
	        	reqName = 'editSlider';
	    	}
	

        //================判断是编辑还是新增===============
 	    
 	    //创建window面板，表单面板是依托window面板显示的  
 	    var syswin = Ext.create('Ext.window.Window',{  
 	              title : title,  
 	              width : 500, 
 	              height: 450,
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
 	                            } else{
 	                            	Ext.example.msg("提示","请填写完整信息！");
 	                            }
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
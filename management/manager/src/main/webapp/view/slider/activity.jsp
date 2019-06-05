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
<title>活动管理</title>
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
                   {header:'序号',xtype: 'rownumberer',width:50,align:'center'},
                   {header:'编号',dataIndex:'id',hidden:true,align:'center'},
                   {header:'标题',dataIndex:'title',align:'center'},
                   {header:'子标题',dataIndex:'subtitle',align:'center'},
                   {header:'描述',dataIndex:'description',align:'center'},
                   {header:'开始时间',dataIndex:'stime',align:'center'},
                   {header:'过期时间',dataIndex:'etime',align:'center'},
                   {header:'状态',dataIndex:'is_pub',align:'center',renderer:function(value,v,r){  
             			 if(value==1){  
                           return '<a href="javascript:StartOrStopAct(\''+r.data.id+'\','+value+')"><span style="color:green;font-weight:bold;">启用</span></a>';
                      } else if(value==0){  
                           return '<a href="javascript:StartOrStopAct(\''+r.data.id+'\','+value+')"><span style="color:red;font-weight:bold;">暂停</span></a>';
                         }
          		   }},
           		{header:'操作 ',dataIndex:'detail',align:'center',renderer:function(value,v,r){
           	    	return '<a href="javascript:showDetail(\''+r.data.id+'\')">入口</a>';
           	    	
          		}},
          		{header:'分享平台 ',dataIndex:'detail',align:'center',renderer:function(value,v,r){
           	    	return '<a href="javascript:showSharePlatForm(\''+r.data.id+'\')">平台</a>';
           	    	
          		}},
          		{header:'详情 ',dataIndex:'detail_table',align:'center',renderer:function(value,v,r){
          			if(value=='activity_requirement_detail'){  
           	    	return '<a href="javascript:showActivityDetail(\''+r.data.id+'\')">详情</a>';
          			}else if(value=='activity_order_reward'){  
           	    	return '<a href="javascript:showActivityOrderReward(\''+r.data.id+'\')">详情</a>';
          			}else if(value=='activity_user_order_reward'){  
           	    	return '<a href="javascript:showActivityUserOrderReward(\''+r.data.id+'\')">详情</a>';
          			}else {
          				
          			}
           	    	
          		}}
               ];
	//创建store数据源
    
    //列表展示数据源
   store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true,
        proxy:{  
            type:'ajax',  
            url:'/slider/getActivity',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'ext'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'title'},  
           {name:'subtitle'},
           {name:'description'},
           {name:'stime'},
           {name:'etime'},
           {name:'is_pub'},
           {name:'detail_table'}
        ]  
    });
 
 // 活动类型
	var store_ActivityType = Ext.create("Ext.data.Store", {
		pageSize : 20, // 每页显示几条数据
		proxy : {
			type : 'ajax',
			url : '/common/getActivityType',
			reader : {
				type : 'json',
				totalProperty : 'total',
				root : 'data'
			}
		},
		fields : [ {
			name : 'type'
		}, // mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置
		{
			name : 'name'
		} ]
	});
 
	// 入口类型
	var store_EntranceType = Ext.create("Ext.data.Store", {
		pageSize : 20, // 每页显示几条数据
		proxy : {
			type : 'ajax',
			url : '/common/getEntranceType',
			reader : {
				type : 'json',
				totalProperty : 'total',
				root : 'data'
			}
		},
		fields : [ {
			name : 'type'
		}, // mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置
		{
			name : 'name'
		} ]
	});
 
    //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	var title=Ext.getCmp('title').getValue();
		var is_pub=Ext.getCmp('is_pub').getValue();
		var entrance_id=Ext.getCmp('entrance_id').getValue();
        var new_params = { title:title,is_pub:is_pub,entrance_id:entrance_id};    
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
        	       <cms:havePerm url='/slider/addActivity'>
        	       { xtype: 'button',id:'add', text: '增加',iconCls:'NewAdd',
        	    	   listeners: {
        	    		   click:function(){
        	    			   showform(null);
        	    		   }
        	    	   }
        	       },</cms:havePerm>
        	       <cms:havePerm url='/slider/deleteActivity'>
                   { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
        	    	   listeners: {
        	    		   click:function(){
        	    			   delUserAll();
        	    		   }
        	    	   }},'-',</cms:havePerm>
        	    	   <cms:havePerm url='/common/flushActivityCache'>
        	           { xtype: 'button', id:'flush', text: '刷新',iconCls:'Refresh',
        		    	   listeners: {
        		    		   click:function(){
        		    			   flushParam();
        		    		   }
        		       }},'-', </cms:havePerm> 
        	      ],
        },{
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{ xtype: 'textfield',id: 'title',name: 'title',fieldLabel: '活动标题',labelAlign:'left',labelWidth:60},
     	           
        	       { xtype: 'combobox',id:'is_pub',name: 'is_pub',displayField: 'status_name',valueField : 'status_type',
               		fieldLabel: '状态',store: Ext.create('Ext.data.Store', {  
                        fields: [  
   	                          { name: 'status_type'},{ name: 'status_name' }  
   	                          ],  
   	                          data: [  
   	                          { "status_type": "1","status_name": "启用 " },  
   	                          { "status_type": "0","status_name": "暂停" }
   	                          ]  
   	                    }),queryMode: 'local',labelAlign:'left',labelWidth:60},
   	                 { xtype: 'combobox',id:'entrance_id',name: 'entrance_id',displayField: 'name',valueField : 'type',
   	                		fieldLabel: '入口',store: store_EntranceType,queryMode: 'local',labelAlign:'left',labelWidth:60},
   	                 <cms:havePerm url='/slider/getActivity'>
        	       { xtype: 'button', text: '查询',id:'select',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var title=Ext.getCmp('title').getValue();
    	    			   var is_pub=Ext.getCmp('is_pub').getValue();
    	    			   var entrance_id=Ext.getCmp('entrance_id').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,title:title,is_pub:is_pub,entrance_id:entrance_id}}); 
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
    store_ActivityType.load();
    store_EntranceType.load();
    grid.on("itemcontextmenu",function(view,record,item,index,e){  
        e.preventDefault();  
        contextmenu.showAt(e.getXY());  
    });
    <cms:havePerm url='/slider/editActivity'>
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
                         url:"/slider/deleteActivity",  
                         params:{id:id},  
                         method:'post',  
                         success:function(o){  
                        	 Ext.example.msg("提示","活动删除成功！");
                             store.reload();  
                             return ;  
                         },  
                         failure:function(form,action){ 
                        	 Ext.example.msg("提示","活动删除失败！");
                         }  
                     });    
                 }  
             });  
          }  
     } ;
    
     
   //刷新系统
 	function flushParam(){
         Ext.Ajax.request({  
             url:"/common/flushActivityCache",  
             method:'post',  
             success:function(o){   
  			    Ext.example.msg("提示","刷新成功！"); 
                 store.reload();  
                 return ;  
             },  
             failure:function(form,action){  
     		    Ext.example.msg("提示","刷新失败！"); 
             }  
         });
 	}  
 	
     
     
   	var statusStore = new Ext.data.SimpleStore({
    	fields:['name','value'],
    	data:[['启用','1'],
    	      ['暂停','0']
    	   
    	]
    });
   
 	var showform=function(d){
 		var sImage = '';
 		var bImage = '';
 		var shareImage = '';
 		var rows = grid.getSelectionModel().getSelection();
 		if(d!=null&&d.id!=null&&d.id!=0){
 			sImage = rows[0].raw.sImage;
 			bImage = rows[0].raw.bImage;
 			shareImage = rows[0].raw.shareImage;
 		}
 	    var add_winForm = Ext.create('Ext.form.Panel', {
            width: 550, 
            height:950,
			layout:"form",
		    frame:true,
		    labelWidth:80,
		    labelAlign : "right",
			items : [{
				// 字典类型
				xtype : 'textfield',
				name : 'id',
				hidden	:true
			},{//行1  
                layout:"column",//从左往右的布局   
                items:[{  
                    columnWidth:.5,//该列在整行中所占的百分比    
                    layout:"form",//从上往下的布局   
                    items:[{
        				//表名
        				xtype : 'textfield',
        				name : 'title',
        				allowBlank	:false,
        				fieldLabel : '标题',
                        width:220 
        			}]  
                },{  
                    columnWidth:.5,//该列在整行中所占的百分比    
                    layout:"form",//从上往下的布局   
                    style:'margin-left:20px',
                    items:[{
        				// 描述
        				xtype : 'textfield',
        				name : 'subtitle',
        				fieldLabel : '子标题', 
                        width:220
        			}]  
                }]  
    },{//行1  
        layout:"column",//从左往右的布局   
        items:[{  
            columnWidth:.5,//该列在整行中所占的百分比    
            layout:"form",//从上往下的布局   
            items:[{ xtype : 'combobox',
				 name : 'activity_type_id',
				 fieldLabel : '活动类型',
				 valueField : 'type',
				 displayField : 'name',
				 editable:false,
				 allowBlank	:false,
				 store : store_ActivityType,
				 hiddenName:'',
				 queryMode : 'local',
				 width:220
			}]  
        },{  
            columnWidth:.5,//该列在整行中所占的百分比    
            layout:"form",//从上往下的布局   
            style:'margin-left:20px',
            items:[{
            	  //下拉列表框  
                xtype: 'combobox', //9  
                fieldLabel: '详情',
                displayField: 'value',
                valueField : 'key',
                name:'detail_table',
                //editable:false,
                store: Ext.create('Ext.data.Store', {  
                    fields: [  
                      {name:'key'},{ name: 'value' }  
                      ],  
                      data: [  
                      { 'key':'activity_requirement_detail','value': '私人定制' },
                      { 'key':'activity_order_reward','value': '订单奖励' },
                      { 'key':'activity_user_order_reward','value': '用户补贴' }
                      ]  
                }),
             width:220
			}]  
        }]  
       },{//行1  
           layout:"column",//从左往右的布局   
           items:[{  
               columnWidth:.5,//该列在整行中所占的百分比    
               layout:"form",//从上往下的布局   
               items:[{  xtype: 'datetimefield',
                         name: 'stime',
                         format : 'Y-m-d',
                         fieldLabel: '开始时间',
                         allowBlank	:false,
                         width:220
                    }]  
           },{  
               columnWidth:.5,
               layout:"form", 
               style:'margin-left:20px',
               items:[{ xtype: 'datetimefield',
                         name: 'etime',
                         format : 'Y-m-d',
                         fieldLabel: '结束时间',
                         allowBlank	:false,
                         width:220
                    }]  
           }]  
          },{
              layout:"column",
              items:[{  
                  columnWidth:.5,
                  layout:"form",
                  items:[{
               	   //下拉列表框  
               	   xtype : 'textfield',
				name : 'url',
				fieldLabel : '链接',
                 width:220
               	   
      			}]  
              },{  
                  columnWidth:.5,
                  layout:"form",
                  style:'margin-left:20px',
                  items:[{
               	   //下拉列表框  
                	  xtype : 'textfield',
    				  name : 'shareTitle',
    				  fieldLabel : '分享标题',
                      width:220
      			}]  
              }]  
             },{
                 layout:"column",
                 items:[{  
                     columnWidth:.5,
                     layout:"form",
                     items:[{
                  	   //下拉列表框  
                  	   xtype : 'textfield',
   				       name : 'shareLink',
   				       fieldLabel : '分享链接',
                       width:220
                  	   
         			}]  
                 }]  
                },{
                 layout:"column",
                 items:[{  
                     columnWidth:.5,
                     layout:"form",
                     items:[{
         				// 描述
         				xtype : 'textarea',
         				name : 'description',
         				fieldLabel : '描述', 
                        width:220
         			}]  
                 },{  
                     columnWidth:.5,
                     layout:"form",
                     style:'margin-left:20px',
                     items:[{
                  	   //下拉列表框  
                    	xtype : 'textarea',
          				name : 'shareDesc',
          				fieldLabel : '分享描述', 
                        width:220
         			}]  
                 }]  
                },{
                	  layout:'column',
                	  border:false,
                       items:[{  
                       layout:'form',
                       anchor: '70%',
              		  border:false,
                       columnWidth:.48,
                       items:[{ 
                             fieldLabel:'分享图标',
                             name:'shareImage',
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
          			name: "shareImage_path"
          		},{  
            	    xtype: 'box', //或者xtype: 'component',  
            	    width: 300, //图片宽度  
            	    height: 200, //图片高度  
            	    autoEl: {  
            	        tag: 'img',  //指定为img标签  
            	        src: shareImage    //指定url路径  
            	    }  
            	},{
                      	  layout:'column',
                    	  border:false,
                           items:[{  
                           layout:'form',
                           anchor: '70%',
                  		  border:false,
                           columnWidth:.48,
                           items:[{ 
                                 fieldLabel:'小图',
                                 name:'sImage',
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
              			name: "sImage_path"
              		},{  
                	    xtype: 'box', //或者xtype: 'component',  
                	    width: 300, //图片宽度  
                	    height: 200, //图片高度  
                	    autoEl: {  
                	        tag: 'img',  //指定为img标签  
                	        src: sImage    //指定url路径  
                	    }  
                	},{
                      	  layout:'column',
                    	  border:false,
                           items:[{  
                           layout:'form',
                           anchor: '70%',
                  		  border:false,
                           columnWidth:.48,
                           items:[{ 
                                 fieldLabel:'大图',
                                 name:'bImage',
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
              			name: "bImage_path"
              		},{  
                	    xtype: 'box', //或者xtype: 'component',  
                	    width: 300, //图片宽度  
                	    height: 200, //图片高度  
                	    autoEl: {  
                	        tag: 'img',  //指定为img标签  
                	        src: bImage    //指定url路径  
                	    }  
                	}]

		});
 	    
	    var title = '新增活动';
	    var reqName = 'addActivity';
	    //================判断是编辑还是新增===============
	    	if(d!=null&&d.id!=null&&d.id!=0){
	    		// 是编辑状态
	    	    add_winForm.form.findField('id').setValue(rows[0].get("id"));
	    	    add_winForm.form.findField('title').setValue(rows[0].get("title"));  
	    	    add_winForm.form.findField('subtitle').setValue(rows[0].get("subtitle"));
	    	    add_winForm.form.findField('description').setValue(rows[0].get("description"));
	    	    add_winForm.form.findField('stime').setValue(rows[0].get("stime")); 
	    	    add_winForm.form.findField('etime').setValue(rows[0].get("etime"));
	    	    add_winForm.form.findField('activity_type_id').setValue(rows[0].raw.activity_type_id);
	    	    add_winForm.form.findField('shareTitle').setValue(rows[0].raw.shareTitle);
	    	    add_winForm.form.findField('shareDesc').setValue(rows[0].raw.shareDesc);
	    	    add_winForm.form.findField('shareLink').setValue(rows[0].raw.shareLink);
	    	    
	    	    add_winForm.form.findField('url').setValue(rows[0].raw.url); 
	    	    add_winForm.form.findField('detail_table').setValue(rows[0].get("detail_table")); 
	    	    add_winForm.form.findField('sImage_path').setValue(rows[0].raw.sImage); 
	    	    add_winForm.form.findField('bImage_path').setValue(rows[0].raw.bImage); 
	    	    add_winForm.form.findField('shareImage_path').setValue(rows[0].raw.shareImage); 
	        	title = '编辑活动';
	        	reqName = 'editActivity';
	    	}
	

        //================判断是编辑还是新增===============
 	    
 	    //创建window面板，表单面板是依托window面板显示的  
 	    var syswin = Ext.create('Ext.window.Window',{  
 	              title : title,  
 	              width : 600, 
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

var StartOrStopAct=function(id,status){

	var sucessmessage;
	var errormessage;
	var message;
	var edit_del;
	if(status==1){
		edit_del=0;
		message='请确定要执行暂停操作吗?'
	}else if(status==0){
		edit_del=1;
		message='请确定要执行启用操作吗?'
	}
	Ext.Msg.confirm("提示信息", message, function(btn) {
		if (btn == 'yes') {
			Ext.Ajax.request({
				url : "/slider/startOrstopActivity",
				params : {
					id : id,
					status : edit_del
				},
				method : 'post',
				success : function(o) {
				    Ext.example.msg("提示",o.responseText); 
					store.reload();
					return;
				},
				failure: function(o) {
                  	 Ext.example.msg("提示",o.responseText);
                  } 
			});
		}
	});
};

function showDetail(id){
	parent.removeTab("activity_model_");
	  parent.addTab("activity_model_","活动入口配置","Fuwushangyunyingxinxiguanli","/slider/activityEntranceIndex?activity_id="+id); 	 
}

function showActivityDetail(id){
	parent.removeTab("activity_detail_");
	parent.addTab("activity_detail_","活动详情配置","Fuwushangyunyingxinxiguanli","/slider/activityDetailIndex?activity_id="+id); 	 
}

function showSharePlatForm(id){
	parent.removeTab("activity_plat_form");
	parent.addTab("activity_plat_form","分享平台配置","Fuwushangyunyingxinxiguanli","/slider/activityPlatFormIndex?activity_id="+id); 	 
}

function showActivityOrderReward(id){
	parent.removeTab("activity_order_reward_");
	parent.addTab("activity_order_reward_","活动详情配置","Fuwushangyunyingxinxiguanli","/activity/orderRewardindex?activity_id="+id); 	 
}

function showActivityUserOrderReward(id){
	parent.removeTab("activity_user_order_reward_");
	parent.addTab("activity_user_order_reward_","用户补贴账单","Fuwushangyunyingxinxiguanli","/activity/orderUserRewardindex?activity_id="+id); 	 
}

</script>

</head>
<body>
</body>
</html>
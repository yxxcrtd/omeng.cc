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
<title>分享活动管理</title>
<script type="text/javascript">
//引入扩展组件desc
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
                   {header:'编号',dataIndex:'id',hidden:true,align:'center',width:100},
                   {header:'标题',dataIndex:'title',align:'center',width:200},
                   {header:'描述',dataIndex:'desc',align:'center',width:200},
                   {header:'分享图标',dataIndex:'image',align:'center',width:200},
                   {header:'分享地址',dataIndex:'clickUrl',align:'center',width:200},
                   {header:'活动地址',dataIndex:'webUrl',align:'center',width:200},
           		   {header:'状态',dataIndex:'status',align:'center',width:200,renderer:function(value,v,r){  
           			 if(value==0){  
                         return '<a href="javascript:StartOrStopAct(\''+r.data.id+'\','+value+')"><span style="color:green;font-weight:bold;">启用</span></a>';
                    } else if(value==1){  
                         return '<a href="javascript:StartOrStopAct(\''+r.data.id+'\','+value+')"><span style="color:red;font-weight:bold;">暂停</span></a>';
                       }
        		   }}
                   
               ];
	
    //列表展示数据源
    store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true,
        proxy:{  
            type:'ajax',  
            url:'/slider/getShareActivity',  
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
           {name:'desc'},
           {name:'image'},
           {name:'clickUrl'},
           {name:'webUrl'},
           {name:'status'}
        ]  
    });
 
  
    //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	var title=Ext.getCmp('title').getValue();
        var new_params = {title:title};    
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
        	       <cms:havePerm url='/slider/addShareActivity'>
        	       { xtype: 'button',id:'add', text: '增加',iconCls:'NewAdd',
        	    	   listeners: {
        	    		   click:function(){
        	    			   showform(null);
        	    		   }
        	    	   }
        	       },</cms:havePerm>
        	       <cms:havePerm url='/slider/deleteShareActivity'>
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
        	items:[{ xtype: 'textfield',id: 'title',name: 'title',fieldLabel: '标题',labelAlign:'left',labelWidth:60},
     	           <cms:havePerm url='/slider/getShareActivity'>
        	       { xtype: 'button', text: '查询',id:'select',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var title=Ext.getCmp('title').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,title:title}}); 
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
    <cms:havePerm url='/slider/editShareActivity'>
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
                         url:"/slider/deleteShareActivity",  
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
    
    
 	var showform=function(d){
 		var pic = '';
 		if(d!=null&&d.id!=null&&d.id!=0){
 			pic = d.image;
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
 	                    value: '填写活动相关信息'  
 	                     
 	                }, {
	    				xtype: 'hidden',
	    				name: "id"
	    			},{  
 	                    //输入广告名称  
 	                    xtype: 'textfield', 
 	                    name: 'title',  
 	                    fieldLabel: '标题',
 	                    allowBlank	:false
 	                },{  
 	                    //输入广告名称  
 	                    xtype: 'textfield', 
 	                    name: 'desc',  
 	                    fieldLabel: '描述',
 	                    allowBlank	:false
 	                },{  
 	                    //输入广告名称  
 	                    xtype: 'textfield', 
 	                    name: 'clickUrl',  
 	                    fieldLabel: '分享地址',
 	                    allowBlank	:false
 	                },{  
 	                    //输入广告名称  
 	                    xtype: 'textfield', 
 	                    name: 'webUrl',  
 	                    fieldLabel: '活动地址',
 	                    allowBlank	:false
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
 	    
	    var title = '新增活动';
	    var reqName = 'addShareActivity';
	    //================判断是编辑还是新增===============
	    	if(d!=null&&d.id!=null&&d.id!=0){
	    		// 是编辑状态
	    	    add_winForm.form.findField('id').setValue(d.id);
	    	    add_winForm.form.findField('title').setValue(d.title);  
	    	    add_winForm.form.findField('desc').setValue(d.desc);
	    	   
	    	    add_winForm.form.findField('clickUrl').setValue(d.clickUrl); 
	    	    add_winForm.form.findField('webUrl').setValue(d.webUrl);
	    	    title = '编辑广告';
	        	reqName = 'editShareActivity';
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

var StartOrStopAct=function(id,status){

	var sucessmessage;
	var errormessage;
	var message;
	var edit_del;
	if(status==0){
		edit_del=1;
		message='请确定要执行暂停操作吗?'
	}else if(status==1){
		edit_del=0;
		message='请确定要执行启用操作吗?'
	}
	Ext.Msg.confirm("提示信息", message, function(btn) {
		if (btn == 'yes') {
			Ext.Ajax.request({
				url : "/slider/startOrstopAct",
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
</script>

</head>
<body>
</body>
</html>
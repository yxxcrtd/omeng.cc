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
<title>报价方案表单详情</title>
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
                   {header:'ID',dataIndex:'id',sortable:true,fixed:false,align:'center'},
                   {header:'附件类型',dataIndex:'attachment_type',align:'center',renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:red;font-weight:bold';>图片</span>";  
                       } else if(value=='2'){  
                           return "<span style='color:green;font-weight:bold';>语音</span>";  
                       }
           	       }},
                   {header:'附件样式',dataIndex:'attachment_style',sortable:true,fixed:false,align:'center'},
                   {header:'加入时间',dataIndex:'join_time',sortable:true,fixed:false,align:'center'},
                   {header:'路径',dataIndex:'path',sortable:true,fixed:false,align:'center'}
               ];
    
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
        proxy:{  
            type:'ajax',  
            url:'/systemManager/getDictAttchList',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'#'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'attachment_type'},  
           {name:'attachment_style'},
           {name:'join_time'},
           {name:'path'}
        ]  
    });
    
 
	
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	 var dictionary_id=Ext.getCmp('dictionary_id').getValue();
		 var new_params = {dictionary_id:dictionary_id};    
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
           
	    	   <cms:havePerm url='/systemManager/addDictAttch'>
	           { xtype: 'button', id:'add', text: '添加',iconCls:'NewAdd',
		    	   listeners: {
		    		   click:function(){
		    			   addPicform(null);
		    		   }
		    	   }},'-',
		       </cms:havePerm>
		       <cms:havePerm url='/systemManager/deleteDictAttch'>
	           { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
		    	   listeners: {
		    		   click:function(){
		    			   deleteRecord();
		    		   }
		    	   }},'-',
			       </cms:havePerm>
          ],
      },
      {
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{ xtype: 'textfield',id:'dictionary_id',name: 'dictionary_id',fieldLabel: '字典ID',labelAlign:'left',labelWidth:60,value:'${dictionary_id}',hidden:true},'-',
        	       
        	       <cms:havePerm url='/systemManager/getDictAttchList'>
        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var dictionary_id=Ext.getCmp('dictionary_id').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,dictionary_id:dictionary_id}}); 
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
   
    
    <cms:havePerm url='/systemManager/editDictAttch'>
    grid.on("itemdblclick",function(grid, row){
    	addPicform(row.data); 
    });</cms:havePerm>
    
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
                     url:"/systemManager/deleteDictAttch",  
                     params:{id:ids},  
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
    var addPicform = function(d) {
    	var pic = '';
 		var rows = grid.getSelectionModel().getSelection();
 		if(d!=null&&d.id!=null&&d.id!=0){
 			pic =rows[0].get('path');
 		}
	  
    	var add_PicForm = Ext.create('Ext.form.Panel', {
    		frame : true, //frame属性  
    		//title: 'Form Fields',  
    		width : 440,
    		height : 350,
    		bodyPadding : 5, 
    		fieldDefaults : {
    			labelAlign : 'left',
    			labelWidth : 90,
    			anchor : '100%'
    		},
    		items : [{
    			// 字典id
    			xtype : 'textfield',
    			name : 'id',
    			hidden	:true
    		},{
    			// 字典id
    			xtype : 'textfield',
    			name : 'dictionary_id',
    			hidden	:true
    		},{
    			// 字典类型
    			xtype : 'textfield',
    			name : 'dict_type',
    			hidden	:true
    		},{
             	  //附件类型
                xtype: 'combobox', //9  
                fieldLabel: '附件类型',
                displayField: 'value',
                valueField : 'key',
                name:'attachment_type',
                store: Ext.create('Ext.data.Store', {  
                    fields: [  
                      {name:'key'},{ name: 'value' }  
                      ],  
                                      data: [  
                      { 'key':'1','value': '图片' },  
                      { 'key':'2','value': '语音' } 
                      ]  
                })
			},
    		{
    			// 附件类型
    			xtype : 'textfield',
    			name : 'attachment_style',
    			fieldLabel : '附件样式',
    			allowBlank	:false,
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
        	        tag: 'img',  //指定为img标签  
        	        src: pic    //指定url路径  
        	    }  
        	} ]

    	});
    	 var title = '新增附件';
		 var reqName = 'addDictAttch';
		    //================判断是编辑还是新增===============
		    	if(d!=null&&d.id!=null&&d.id!=0){
		    	 add_PicForm.form.findField('id').setValue(rows[0].get('id'));
		         add_PicForm.form.findField('attachment_type').setValue(rows[0].get('attachment_type')+'');
		         add_PicForm.form.findField('attachment_style').setValue(rows[0].get('attachment_style'));
		         add_PicForm.form.findField('pics_path').setValue(rows[0].get('path'));
    	         title = '编辑附件';
    	         reqName = 'editDictAttch';
		    	}
		    	add_PicForm.form.findField('dictionary_id').setValue('${dictionary_id}');
		    	add_PicForm.form.findField('dict_type').setValue('${dict_type}');
    	//创建window面板，表单面板是依托window面板显示的  
    	var addPicwin = Ext.create('Ext.window.Window', {
    		title : "添加图片",
    		width : 450,
    		height : 350,
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
    		items : [ add_PicForm ],
    		buttons : [ {
    			text : "保存",
    			minWidth : 70,
    			handler : function() {
    				if (add_PicForm.getForm().isValid()) {
    					add_PicForm.getForm().submit({
    						url : '/systemManager/'+reqName,
    						//等待时显示 等待  
    						waitTitle : '请稍等...',
    						waitMsg : '正在提交信息...',
    						success : function(fp, o) {
    							if (o.result== true) { 
                                   	 Ext.example.msg("提示","保存成功！");
                                   	 addPicwin.close(); 
                                     store.reload();  
                                  }else { 
                                   	 Ext.example.msg("提示","保存失败！");
                                  }  
    			              	    
    						},
    						failure : function(fp, o) {
    							addPicwin.close(); //关闭窗口    
    							Ext.example.msg("提示",o.response.responseText);                                                                                    
    							     
    						}
    					});
    				}
    			}
    		}, {
    			text : "关闭",
    			minWidth : 70,
    			handler : function() {
    				addPicwin.close();
    			}
    		} ]
    	});
    	addPicwin.show();
    };
	
});


</script>
</head>
<body>
</body>
</html>
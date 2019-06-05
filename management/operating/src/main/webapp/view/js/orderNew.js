var switchAppType = function(order_id, app_type, service_type,orderTypeName) {
	if (app_type == 'lxz') {
		lxz_detailform(order_id, app_type, service_type,orderTypeName);
	}else if (app_type == 'cbt_cbt'|| app_type == 'cbt_ytc') {
		cbt_detailform(order_id, app_type, service_type,orderTypeName);
	}else if (app_type == 'zyb_kbj'|| app_type == 'zyb_ajz'|| app_type == 'zyb_kxy'|| app_type == 'zyb_smx'|| app_type == 'zyb_acw'|| app_type == 'zyb_ysh') {
		zyb_detailform(order_id, app_type, service_type,orderTypeName);
	} else if (app_type == 'sxd') {
		sxd_detailform(order_id, app_type, service_type,orderTypeName);
	} else if (app_type == 'yxt_cgt'|| app_type == 'yxt_yxt'|| app_type == 'yxt_xqb'|| app_type == 'yxt_kk') {
		yxt_detailform(order_id, app_type, service_type,orderTypeName);
	} else if (app_type == 'fyb') {
		fyb_detailform(order_id, app_type, service_type,orderTypeName);
	} else if (app_type == 'jzx') {
		jzx_detailform(order_id, app_type, service_type,orderTypeName);
	} else if (app_type == 'hz_aml'||app_type == 'hz_amj'||app_type == 'hz_ss') {
		hz_detailform(order_id, app_type, service_type,orderTypeName);
	} else if (app_type == 'ydc') {
		ydc_detailform(order_id, app_type, service_type,orderTypeName);
	} else if (app_type == 'dgf') {
		dgf_detailform(order_id, app_type, service_type,orderTypeName);
	} else if (app_type == 'mst') {
		mst_detailform(order_id, app_type, service_type,orderTypeName);
	} else if (app_type == 'zsy') {
		zsy_detailform(order_id, app_type, service_type,orderTypeName);
	} else if (app_type == 'qpl') {
		qpl_detailform(order_id, app_type, service_type,orderTypeName);
	} else if (app_type == 'xhf') {
		xhf_detailform(order_id, app_type, service_type,orderTypeName);
	} else if (app_type == 'yd_yd'||app_type == 'yd_xxb') {
		yd_detailform(order_id, app_type, service_type,orderTypeName);
	} else if (app_type == 'syp') {
		syp_detailform(order_id, app_type, service_type,orderTypeName);
	} else if (app_type == 'ts') {
		ts_detailform(order_id, app_type, service_type,orderTypeName);
	} else if (app_type == 'jrj_zsb'||app_type == 'jrj_jrj') {
		jrj_detailform(order_id, app_type, service_type,orderTypeName);
	} else if (app_type == 'ams_ydb'||app_type == 'ams_wbt'||app_type == 'ams_yyt'||app_type == 'ams_afs'||app_type == 'ams_szx') {
		ams_detailform(order_id, app_type, service_type,orderTypeName);
	} else if (app_type == 'xlb_hdb'||app_type == 'xlb_xlb') {
		xlb_detailform(order_id, app_type, service_type,orderTypeName);
	} else if (app_type == 'qzy') {
		qzy_detailform(order_id, app_type, service_type,orderTypeName);
	} else if (app_type == 'zyd') {
		zyd_detailform(order_id, app_type, service_type,orderTypeName);
	} else if (app_type == 'hyt') {
		hyt_detailform(order_id, app_type, service_type,orderTypeName);
	} else if (app_type == 'ydh') {
		ydh_detailform(order_id, app_type, service_type,orderTypeName);
	} else if (app_type == 'yp') {
		yp_detailform(order_id, app_type, service_type,orderTypeName);
	}else if (app_type == 'swg') {
		swg_detailform(order_id, app_type, service_type,orderTypeName);
	}else if (app_type == 'zdf') {
		zdf_detailform(order_id, app_type, service_type,orderTypeName);
	}else if (app_type == 'gxfw') {
		zdf_detailform(order_id, app_type, service_type,orderTypeName);
	}
};


// 旅行者订单详情页
var lxz_detailform = function(order_id, app_type, service_type,orderTypeName) {
	var lxz_detail_winForm = Ext.create('Ext.form.Panel', {
		frame : true, // frame属性
		// title: 'Form Fields',
		width : 440,
		height : 350,
		bodyPadding : 5,
		// renderTo:"panel21",
		fieldDefaults : {
			labelAlign : 'left',
			labelWidth : 90,
			anchor : '90%'
		},
		items : [ {
			// 显示文本框，相当于label
			xtype : 'displayfield',
			name : 'displayfield1',
			// fieldLabel: 'Display field',
			value : '旅行者订单详情'

		},{
			// 服务类型
			xtype : 'textfield',
			name : 'orderTypeName',
			fieldLabel : '服务类型',
			readOnly : true
		}, {
			// 出行天数
			xtype : 'textfield',
			name : 'travel_days',
			fieldLabel : '出行天数',
			readOnly : true
		},{
			// 是否旅行社推荐
			xtype : 'combobox',
			name : 'is_recommend',
			fieldLabel : '旅行社推荐',
			valueField : 'key',
			displayField : 'value',
			editable : false,
			store : Ext.create('Ext.data.Store', {
				fields : [ {
					name : 'key'
				}, {
					name : 'value'
				} ],
				data : [ {
					"key" : "0",
					"value" : "否"
				}, {
					"key" : "1",
					"value" : "是"
				} ]
			}),
			queryMode : 'local',
			value : '',
			readOnly : true
		},{
			// 景点
			xtype : 'textfield',
			name : 'scenic_spot',
			fieldLabel : '景点',
			readOnly : true
		},{
			// 旅游人数
			xtype : 'textfield',
			name : 'tourists_number',
			fieldLabel : '旅游人数',
			readOnly : true
		},{
			// 预期消费
			xtype : 'textfield',
			name : 'consumption_budget',
			fieldLabel : '预期消费',
			readOnly : true
		},{
			// 出发时间
			xtype : 'datetimefield',
			name : 'travel_time',
			fieldLabel : '出发时间',
			format : 'Y-m-d',
			readOnly : true
		}, {
			// 旅游偏好
			xtype : 'textfield',
			name : 'travel_predilection',
			fieldLabel : '旅游偏好',
			readOnly : true
		}, {
			// 地址
			xtype : 'textfield',
			name : 'address',
			fieldLabel : '预约地址',
			readOnly : true
		}]
	});

	// 向后台获取订单详情数据
	Ext.Ajax.request({
		url : '/order/getOrderDetail',
		params : {
			order_id : order_id,
			app_type : app_type,
			service_type : service_type
		},
		success : function(response) {
			Ext.getBody().unmask();
			var json = Ext.JSON.decode(response.responseText);
			if (json.code == 'OK') {
				var data = json.data;
				lxz_detail_winForm.form.findField('orderTypeName').setValue(orderTypeName);
				lxz_detail_winForm.form.findField('travel_days').setValue(
						data[0].travel_days);
				lxz_detail_winForm.form.findField('address').setValue(
						data[0].address);
				lxz_detail_winForm.form.findField('travel_predilection').setValue(
						data[0].travel_predilection);
				lxz_detail_winForm.form.findField('scenic_spot').setValue(
						data[0].scenic_spot);
				lxz_detail_winForm.form.findField('is_recommend').setValue(
						data[0].is_recommend +'');		
				lxz_detail_winForm.form.findField('tourists_number').setValue(
						data[0].tourists_number);		
			    lxz_detail_winForm.form.findField('travel_time').setValue(
						data[0].travel_time);
				lxz_detail_winForm.form.findField('consumption_budget').setValue(
						data[0].consumption_budget);
				
				if (data[0].scenic_spot == null || data[0].scenic_spot +''=='') {
					lxz_detail_winForm.form.findField('scenic_spot')
									.hide();
					
				} 
				if (data[0].consumption_budget == null || data[0].consumption_budget +''=='') {
					lxz_detail_winForm.form.findField('consumption_budget')
									.hide();
					
				}
				if (data[0].travel_time == null || data[0].travel_time +''=='') {
					lxz_detail_winForm.form.findField('travel_time')
									.hide();
					
				}
				if (data[0].address == null || data[0].address +''=='') {
					if(data[0].detail_address == null || data[0].detail_address +''==''){
					 lxz_detail_winForm.form.findField('address')
									.hide();
					}else{
					lxz_detail_winForm.form.findField('address').setValue(
						data[0].detail_address);
					}
					
				 }else{
				  if(data[0].detail_address == null || data[0].detail_address +''==''){
					
					}else{
					lxz_detail_winForm.form.findField('address').setValue(
						data[0].address+data[0].detail_address);
					}
				}
				if (data[0].tourists_number == null || data[0].tourists_number +''=='') {
					lxz_detail_winForm.form.findField('tourists_number')
									.hide();
					
				}			
				if (data[0].travel_predilection == null || data[0].travel_predilection +''=='') {
					lxz_detail_winForm.form.findField('travel_predilection')
									.hide();
				} 
		 	} else {

			}
		},
		failure : function(request) {
			Ext.MessageBox.show({
				title : '操作提示',
				msg : "连接服务器失败",
				buttons : Ext.MessageBox.OK,
				icon : Ext.MessageBox.ERROR
			});
		},
		method : 'post'
	});
	var lxz_detailwindow = Ext.create('Ext.window.Window', {
		title : "订单详情",
		width : 450,
		height : 350,
		// height : 120,
		// plain : true,
		iconCls : "addicon",
		// 不可以随意改变大小
		resizable : true,
		// 是否可以拖动
		draggable : true,
		collapsible : true, // 允许缩放条
		closeAction : 'close',
		closable : true,
		// 弹出模态窗体
		modal : 'true',
		buttonAlign : "center",
		bodyStyle : "padding:0 0 0 0",
		items : [ lxz_detail_winForm ]
	});
	lxz_detailwindow.show();
};

// 左右帮订单详情页
var zyb_detailform = function(order_id, app_type, service_type,orderTypeName) {
	var zyb_detail_winForm = Ext.create('Ext.form.Panel', {
		frame : true, // frame属性
		// title: 'Form Fields',
		width : 440,
		height : 420,
		bodyPadding : 5,
		// renderTo:"panel21",
		fieldDefaults : {
			labelAlign : 'left',
			labelWidth : 90,
			anchor : '90%'
		},
		items : [ {
			// 显示文本框，相当于label
			xtype : 'displayfield',
			name : 'displayfield1',
			// fieldLabel: 'Display field',
			value : '左右帮订单详情'

		}, {
			//服务类型
			xtype : 'textfield',
			name : 'orderTypeName',
			fieldLabel : '服务类型',
			readOnly : true
		},{
			// 出发地
			xtype : 'textfield',
			name : 'start_site',
			fieldLabel : '出发地',
			readOnly : true
		}, {
			// 到达地
			xtype : 'textfield',
			name : 'end_site',
			fieldLabel : '到达地',
			readOnly : true
		},{
			// 出发地是否有电梯
			xtype : 'combobox',
			name : 'start_site_have_elevator',
			fieldLabel : '出发地电梯',
			valueField : 'key',
			displayField : 'value',
			editable : false,
			store : Ext.create('Ext.data.Store', {
				fields : [ {
					name : 'key'
				}, {
					name : 'value'
				} ],
				data : [ {
					"key" : "0",
					"value" : "无"
				}, {
					"key" : "1",
					"value" : "有"
				} ]
			}),
			queryMode : 'local',
			value : '',
			readOnly : true
		},{
			// 到发地是否有电梯
			xtype : 'combobox',
			name : 'end_site_have_elevator',
			fieldLabel : '到达地电梯',
			valueField : 'key',
			displayField : 'value',
			editable : false,
			store : Ext.create('Ext.data.Store', {
				fields : [ {
					name : 'key'
				}, {
					name : 'value'
				} ],
				data : [ {
					"key" : "0",
					"value" : "无"
				}, {
					"key" : "1",
					"value" : "有"
				} ]
			}),
			queryMode : 'local',
			value : '',
			readOnly : true
		},{
			// 出发地楼层
			xtype : 'textfield',
			name : 'start_site_floor',
			fieldLabel : '出发地楼层',
			readOnly : true
		},{
			// 到达地楼层
			xtype : 'textfield',
			name : 'end_site_floor',
			fieldLabel : '到达地楼层',
			readOnly : true
		}, {
			// 搬运物品
			xtype : 'textfield',
			name : 'move_article',
			fieldLabel : '搬运物品',
			readOnly : true
		}, {
			// 保洁期限
			xtype : 'textfield',
			name : 'time_limit',
			fieldLabel : '保洁期限',
			readOnly : true
		},  {
			// 服务项目
			xtype : 'textfield',
			name : 'service_item_value',
			fieldLabel : '服务项目',
			readOnly : true
		}, {
			// 新居面积
			xtype : 'textfield',
			name : 'clean_area',
			fieldLabel : '新居面积',
			readOnly : true
		},{
			// 有无宠物
			xtype : 'textfield',
			name : 'have_pet',
			fieldLabel : '有无宠物',
			readOnly : true
		},{
			// 保姆类型
			xtype : 'combobox',
			name : 'nanny_type',
			fieldLabel : '保姆类型',
			valueField : 'key',
			displayField : 'value',
			editable : false,
			store : Ext.create('Ext.data.Store', {
				fields : [ {
					name : 'key'
				}, {
					name : 'value'
				} ],
				data : [ {
					"key" : "1",
					"value" : "长期钟点工"
				}, {
					"key" : "2",
					"value" : "住家保姆"
				}, {
					"key" : "3",
					"value" : "月嫂"
				}, {
					"key" : "4",
					"value" : "育儿嫂"
				}, {
					"key" : "5",
					"value" : "看护护理"
				}, {
					"key" : "6",
					"value" : "管家"
				}, {
					"key" : "7",
					"value" : "催乳师"
				} ]
			}),
			queryMode : 'local',
			readOnly : true
		},{
			// 房屋面积
			xtype : 'textfield',
			name : 'house_area',
			fieldLabel : '房屋面积',
			readOnly : true
		},{
			// 保洁类型
			xtype : 'combobox',
			name : 'clean_type',
			fieldLabel : '保洁类型',
			valueField : 'key',
			displayField : 'value',
			editable : false,
			store : Ext.create('Ext.data.Store', {
				fields : [ {
					name : 'key'
				}, {
					name : 'value'
				} ],
				data : [ {
					"key" : "1",
					"value" : "家庭保洁"
				}, {
					"key" : "2",
					"value" : "新居开荒"
				}, {
					"key" : "3",
					"value" : "家居保养"
				}, {
					"key" : "4",
					"value" : "公司保洁"
				}, {
					"key" : "5",
					"value" : "外墙清洗"
				} ]
			}),
			queryMode : 'local',
			value : '',
			readOnly : true
		},
		{
			// 服务项目
			xtype : 'textfield',
			name : 'service_item',
			fieldLabel : '服务项目',
			readOnly : true
		},
		 {
			// 饮食偏好
			xtype : 'textfield',
			name : 'diet_predilection',
			fieldLabel : '饮食偏好',
			readOnly : true
		}, {
			// 预产期
			xtype : 'textfield',
			name : 'pre_production_period',
			fieldLabel : '预产期',
			readOnly : true
		}, {
			// 服务频率
			xtype : 'textfield',
			name : 'service_frequency',
			fieldLabel : '服务频率',
			readOnly : true
		},{
			// 宝宝性别
			xtype : 'textfield',
			name : 'baby_sex',
			fieldLabel : '宝宝性别',
			readOnly : true
		},{
			// 宝宝年龄
			xtype : 'textfield',
			name : 'baby_age',
			fieldLabel : '宝宝年龄',
			readOnly : true
		},{
			// 护理对象
			xtype : 'textfield',
			name : 'nurse_condition',
			fieldLabel : '护理对象',
			readOnly : true
		},{
			// 自理状况
			xtype : 'textfield',
			name : 'care_type',
			fieldLabel : '自理状况',
			readOnly : true
		},{
			// 是否住家
			xtype : 'textfield',
			name : 'reside_in',
			fieldLabel : '是否住家',
			readOnly : true
		},{
			// 维修内容
			xtype : 'textfield',
			name : 'repair_content',
			fieldLabel : '维修内容',
			readOnly : true
		},{
			// 上门服务
			xtype : 'textfield',
			name : 'visit_service',
			fieldLabel : '上门服务',
			readOnly : true
		},  {
			// 洗护类型
			xtype : 'textfield',
			name : 'wash_type',
			fieldLabel : '洗护类型',
			readOnly : true
		}, {
			// 件数
			xtype : 'textfield',
			name : 'wash_count',
			fieldLabel : '件数',
			readOnly : true
		} ,{
			// 宠物类型
			xtype : 'textfield',
			name : 'pet_type',
			fieldLabel : '宠物类型',
			readOnly : true
		} ,{
			// 宠物服务
			xtype : 'textfield',
			name : 'pet_service',
			fieldLabel : '宠物服务',
			readOnly : true
		} ,{
			// 宠物大小
			xtype : 'textfield',
			name : 'pet_size',
			fieldLabel : '宠物大小',
			readOnly : true
		} ,{
			//宠物品种
			xtype : 'textfield',
			name : 'pet_variety',
			fieldLabel : '宠物品种',
			readOnly : true
		} ,{
			//宠物性别
			xtype : 'textfield',
			name : 'pet_sex',
			fieldLabel : '宠物性别',
			readOnly : true
		} ,{
			//宠物年龄
			xtype : 'textfield',
			name : 'pet_age',
			fieldLabel : '宠物年龄',
			readOnly : true
		} ,{
			//心理价位
			xtype : 'textfield',
			name : 'speculative_price_level',
			fieldLabel : '心理价位',
			readOnly : true
		} ,{
			//回收类型
			xtype : 'textfield',
			name : 'recycle_type',
			fieldLabel : '回收类型',
			readOnly : true
		},{
			//服务时间
			xtype : 'textfield',
			name : 'service_time',
			fieldLabel : '服务时间',
			readOnly : true
		} ,{
			// 预约地址
			xtype : 'textfield',
			name : 'address',
			fieldLabel : '预约地址',
			readOnly : true
		} , {
			// 其他需求
			xtype : 'textfield',
			name : 'demand',
			fieldLabel : '其他需求',
			readOnly : true
		}]
	});
	// 创建window面板，表单面板是依托window面板显示的

	// 向后台获取订单详情数据
	Ext.Ajax
			.request({
				url : '/order/getOrderDetail',
				params : {
					order_id : order_id,
					app_type : app_type,
					service_type : service_type
				},
				success : function(response) {
					Ext.getBody().unmask();
					var json = Ext.JSON.decode(response.responseText);
					if (json.code == 'OK') {
						var data = json.data;
						zyb_detail_winForm.form.findField('orderTypeName')
								.setValue(orderTypeName);
						zyb_detail_winForm.form.findField('pet_type')
								.setValue(data[0].pet_type);
						zyb_detail_winForm.form.findField('pet_service')
								.setValue(data[0].pet_service);
						zyb_detail_winForm.form.findField('pet_size')
								.setValue(data[0].pet_size);
						zyb_detail_winForm.form.findField('pet_variety')
								.setValue(data[0].pet_variety);
						zyb_detail_winForm.form.findField('pet_sex')
								.setValue(data[0].pet_sex);
						zyb_detail_winForm.form.findField('pet_age')
								.setValue(data[0].pet_age);
						zyb_detail_winForm.form.findField('speculative_price_level')
								.setValue(data[0].speculative_price_level);
						zyb_detail_winForm.form.findField('recycle_type')
								.setValue(data[0].recycle_type);
						
						
						
						
						zyb_detail_winForm.form.findField('start_site')
								.setValue(data[0].start_site);
								
						zyb_detail_winForm.form.findField('end_site').setValue(
								data[0].end_site);
						zyb_detail_winForm.form.findField('start_site_have_elevator').setValue(
								data[0].start_site_have_elevator+'');
						zyb_detail_winForm.form.findField('end_site_have_elevator').setValue(
								data[0].end_site_have_elevator+'');
						zyb_detail_winForm.form.findField('start_site_floor').setValue(
								data[0].start_site_floor);
						zyb_detail_winForm.form.findField('end_site_floor').setValue(
								data[0].end_site_floor);
							
						zyb_detail_winForm.form.findField('move_article')
								.setValue(data[0].move_article);
						zyb_detail_winForm.form.findField('time_limit')
								.setValue(data[0].time_limit + '');
						zyb_detail_winForm.form.findField('service_item_value')
								.setValue(data[0].service_item_value);
						zyb_detail_winForm.form.findField('clean_area')
								.setValue(data[0].clean_area);
						zyb_detail_winForm.form.findField('house_area')
								.setValue(data[0].house_area);		
						zyb_detail_winForm.form.findField('clean_type')
								.setValue(data[0].clean_type+'');		
					
						zyb_detail_winForm.form.findField('have_pet').setValue(
								data[0].have_pet + '');
						zyb_detail_winForm.form.findField('service_time')
								.setValue(data[0].service_time);
						zyb_detail_winForm.form.findField('address').setValue(
								data[0].address);
					     zyb_detail_winForm.form.findField('demand').setValue(
								data[0].demand);			
								
						zyb_detail_winForm.form.findField('nanny_type')
								.setValue(data[0].nanny_type + '');
						zyb_detail_winForm.form.findField('service_item')
								.setValue(data[0].service_item + '');
						zyb_detail_winForm.form.findField('diet_predilection')
								.setValue(data[0].diet_predilection);
						zyb_detail_winForm.form.findField(
								'pre_production_period').setValue(
								data[0].pre_production_period);
						zyb_detail_winForm.form.findField('service_frequency')
								.setValue(data[0].service_frequency);
						zyb_detail_winForm.form.findField('baby_sex').setValue(
								data[0].baby_sex + '');
                       
						zyb_detail_winForm.form.findField('baby_age').setValue(
								data[0].baby_age+ '岁');
						zyb_detail_winForm.form.findField('nurse_condition')
								.setValue(data[0].nurse_condition + '');
						zyb_detail_winForm.form.findField('care_type')
								.setValue(data[0].care_type + '');
						
						zyb_detail_winForm.form.findField('reside_in')
								.setValue(data[0].reside_in + '');
						zyb_detail_winForm.form.findField('repair_content')
								.setValue(data[0].repair_content + '');
                        
						zyb_detail_winForm.form.findField('visit_service')
								.setValue(data[0].visit_service + '');
						zyb_detail_winForm.form.findField('wash_type')
								.setValue(data[0].wash_type + '');
						zyb_detail_winForm.form.findField('wash_count')
								.setValue(data[0].wash_count);
                       
                        if (data[0].pet_type == null || data[0].pet_type +''=='') {
							zyb_detail_winForm.form.findField('pet_type')
									.hide();
						}
						if (data[0].pet_service == null || data[0].pet_service +''=='') {
							zyb_detail_winForm.form.findField('pet_service')
									.hide();
						}
						if (data[0].pet_size == null || data[0].pet_size +''=='') {
							zyb_detail_winForm.form.findField('pet_size')
									.hide();
						}
						if (data[0].pet_variety == null || data[0].pet_variety +''=='') {
							zyb_detail_winForm.form.findField('pet_variety')
									.hide();
						}
						if (data[0].pet_sex == null || data[0].pet_sex +''=='') {
							zyb_detail_winForm.form.findField('pet_sex')
									.hide();
						}
						if (data[0].pet_age == null || data[0].pet_age +''=='') {
							zyb_detail_winForm.form.findField('pet_age')
									.hide();
						}
						
				    	if (data[0].recycle_type == null || data[0].recycle_type +''=='') {
							zyb_detail_winForm.form.findField('recycle_type')
									.hide();
						}
                       
                       
                        if (data[0].house_area == null || data[0].house_area +''=='') {
							zyb_detail_winForm.form.findField('house_area')
									.hide();
						}
						if (data[0].start_site_have_elevator == null || data[0].start_site_have_elevator +''=='') {
							zyb_detail_winForm.form.findField('start_site_have_elevator')
									.hide();
						}
						if (data[0].end_site_have_elevator == null || data[0].end_site_have_elevator +''=='') {
							zyb_detail_winForm.form.findField('end_site_have_elevator')
									.hide();
						}
						if (data[0].start_site_floor == null || data[0].start_site_floor +''=='') {
							zyb_detail_winForm.form.findField('start_site_floor')
									.hide();
						}
						if (data[0].end_site_floor == null || data[0].end_site_floor +''=='') {
							zyb_detail_winForm.form.findField('end_site_floor')
									.hide();
						}
						if (data[0].address == null || data[0].address +''=='') {
					      if(data[0].detail_address == null || data[0].detail_address +''==''){
					      zyb_detail_winForm.form.findField('address')
									.hide();
					    }else{
					        zyb_detail_winForm.form.findField('address').setValue(
						     data[0].detail_address);
					}
					
				     }else{
				     if(data[0].detail_address == null || data[0].detail_address +''==''){
					
					}else{
					zyb_detail_winForm.form.findField('address').setValue(
						data[0].address+data[0].detail_address);
					}
				}
						if (data[0].demand == null || data[0].demand +''=='') {
							zyb_detail_winForm.form.findField('demand')
									.hide();
						}
						if (data[0].clean_type == null || data[0].clean_type +''=='') {
							zyb_detail_winForm.form.findField('clean_type')
									.hide();
						}
						if (data[0].start_site == null || data[0].start_site +''=='') {
							zyb_detail_winForm.form.findField('start_site')
									.hide();
						}
						if (data[0].end_site == null || data[0].end_site +''=='') {
							zyb_detail_winForm.form.findField('end_site')
									.hide();
						}
						if (data[0].move_article == null || data[0].move_article +''=='') {
							zyb_detail_winForm.form.findField('move_article')
									.hide();
						}
						if (data[0].time_limit == null || data[0].time_limit +''=='') {
							zyb_detail_winForm.form.findField('time_limit')
									.hide();
						}
						if (data[0].service_item_value == null || data[0].service_item_value +''=='') {
							zyb_detail_winForm.form.findField(
									'service_item_value').hide();
						} 
						if (data[0].clean_area == null || data[0].clean_area +''=='') {
							zyb_detail_winForm.form.findField('clean_area')
									.hide();
						}
						if (data[0].have_pet == null || data[0].have_pet +''=='') {
							zyb_detail_winForm.form.findField('have_pet')
									.hide();
						}
						if (data[0].service_time == null || data[0].service_time +''=='') {
							zyb_detail_winForm.form.findField('service_time')
									.hide();
						}
						if (data[0].address == null || data[0].address +''=='') {
							zyb_detail_winForm.form.findField('address').hide();
						}
						if (data[0].nanny_type == null || data[0].nanny_type +''=='') {
							zyb_detail_winForm.form.findField('nanny_type')
									.hide();
						}
						
						if (data[0].service_item == null || data[0].service_item +''=='') {
							zyb_detail_winForm.form.findField('service_item')
									.hide();
						} 
						if (data[0].diet_predilection == null || data[0].diet_predilection +''=='') {
							zyb_detail_winForm.form.findField(
									'diet_predilection').hide();
						}
						if (data[0].pre_production_period == null || data[0].pre_production_period +''=='') {
							zyb_detail_winForm.form.findField(
									'pre_production_period').hide();
						}
						if (data[0].service_frequency == null || data[0].service_frequency +''=='') {
							zyb_detail_winForm.form.findField(
									'service_frequency').hide();
						} 
						if (data[0].baby_sex == null || data[0].baby_sex +''=='') {
							zyb_detail_winForm.form.findField('baby_sex')
									.hide();
						}
						if (data[0].baby_age == null || data[0].baby_age +''=='') {
							zyb_detail_winForm.form.findField('baby_age')
									.hide();
						}
						if (data[0].nurse_condition == null || data[0].nurse_condition +''=='') {
							zyb_detail_winForm.form
									.findField('nurse_condition').hide();
						}
						if (data[0].care_type == null || data[0].care_type +''=='') {
							zyb_detail_winForm.form.findField('care_type')
									.hide();
						}
						if (data[0].speculative_price_level == null || data[0].speculative_price_level +''=='') {
							zyb_detail_winForm.form.findField(
									'speculative_price_level').hide();
						}
						if (data[0].reside_in == null || data[0].reside_in +''=='') {
							zyb_detail_winForm.form.findField('reside_in')
									.hide();
						}
						if (data[0].repair_content == null || data[0].repair_content +''=='')
								{
							zyb_detail_winForm.form.findField('repair_content')
									.hide();
						}
						if (data[0].visit_service == null || data[0].visit_service +''=='') {
							zyb_detail_winForm.form.findField('visit_service')
									.hide();
						}
						if (data[0].wash_type == null || data[0].wash_type +''=='') {
							zyb_detail_winForm.form.findField('wash_type')
									.hide();
						}
						if (data[0].wash_count == null || data[0].wash_count +''=='') {
							zyb_detail_winForm.form.findField('wash_count')
									.hide();
						}

					} else {

					}
				},
				failure : function(request) {
					Ext.MessageBox.show({
						title : '操作提示',
						msg : "连接服务器失败",
						buttons : Ext.MessageBox.OK,
						icon : Ext.MessageBox.ERROR
					});
				},
				method : 'post'
			});
	var zyb_detailwindow = Ext.create('Ext.window.Window', {
		title : "订单详情",
		width : 450,
		height : 350,
		// height : 120,
		// plain : true,
		iconCls : "addicon",
		// 不可以随意改变大小
		resizable : true,
		//autoScroll : true,
		// 是否可以拖动
		draggable : true,
		collapsible : true, // 允许缩放条
		closeAction : 'close',
		closable : true,
		// 弹出模态窗体
		modal : 'true',
		buttonAlign : "center",
		bodyStyle : "padding:0 0 0 0",
		items : [ zyb_detail_winForm ]
	});
	zyb_detailwindow.show();
};

// 上下贷订单详情页
var sxd_detailform = function(order_id, app_type, service_type,orderTypeName) {
	var sxd_detail_winForm = Ext.create('Ext.form.Panel', {
		frame : true, // frame属性
		// title: 'Form Fields',
		width : 440,
		height : 450,
		bodyPadding : 5,
		// renderTo:"panel21",
		fieldDefaults : {
			labelAlign : 'left',
			labelWidth : 90,
			anchor : '90%'
		},
		items : [ {
			// 显示文本框，相当于label
			xtype : 'displayfield',
			name : 'displayfield1',
			// fieldLabel: 'Display field',
			value : '上下贷订单详情'

		},{
			// 服务类型
			xtype : 'textfield',
			name : 'orderTypeName',
			fieldLabel : '服务类型',
			readOnly : true
		},{
			// 贷款人身份
			xtype : 'textfield',
			name : 'identity',
			fieldLabel : '贷款人身份',
			readOnly : true
		}, {
			// 月收入
			xtype : 'textfield',
			name : 'monthly_income',
			fieldLabel : '月收入(K)',
			readOnly : true
		}, {
			// 申请贷款的城市
			xtype : 'textfield',
			name : 'apply_loan_city',
			fieldLabel : '贷款城市',
			readOnly : true
		},{
			// 交通情况
			xtype : 'textfield',
			name : 'traffic',
			fieldLabel : '交通情况',
			readOnly : true
		},{
			// 住房情况
			xtype : 'textfield',
			name : 'house',
			fieldLabel : '住房情况',
			readOnly : true
		},  {
			// 希望贷款数额
			xtype : 'textfield',
			name : 'hope_loan_amount',
			fieldLabel : '贷款数额(万)',
			readOnly : true
		}, {
			// 希望贷款期限 月份
			xtype : 'textfield',
			name : 'hope_loan_deadline',
			fieldLabel : '贷款期限(月)',
			readOnly : true
		},{
			// 物品抵押
			xtype : 'textfield',
			name : 'have_guarantee',
			fieldLabel : '物品抵押',
			readOnly : true
		},{
			// 贷款用途
			xtype : 'textfield',
			name : 'loan_purpose',
			fieldLabel : '贷款用途',
			readOnly : true
		}, {
			// 服务的时间
			xtype : 'datetimefield',
			name : 'service_time',
			fieldLabel : '服务的时间',
			format : 'Y-m-d',
			readOnly : true
		}, {
			//预约地址
			xtype : 'textfield',
			name : 'address',
			fieldLabel : '预约地址',
			readOnly : true
		} ]
	});
	// 创建window面板，表单面板是依托window面板显示的

	// 向后台获取订单详情数据
	Ext.Ajax.request({
		url : '/order/getOrderDetail',
		params : {
			order_id : order_id,
			app_type : app_type,
			service_type : service_type
		},
		success : function(response) {
			Ext.getBody().unmask();
			var json = Ext.JSON.decode(response.responseText);
			if (json.code == 'OK') {
				var data = json.data;
				sxd_detail_winForm.form.findField('orderTypeName').setValue(orderTypeName);
				sxd_detail_winForm.form.findField('identity').setValue(
						data[0].identity + '');
				sxd_detail_winForm.form.findField('monthly_income').setValue(
						data[0].monthly_income);
				sxd_detail_winForm.form.findField('apply_loan_city').setValue(
						data[0].apply_loan_city);
				sxd_detail_winForm.form.findField('traffic').setValue(
						data[0].traffic + '');
				sxd_detail_winForm.form.findField('house').setValue(
						data[0].house + '');
				sxd_detail_winForm.form.findField('hope_loan_amount').setValue(
						data[0].hope_loan_amount);
				sxd_detail_winForm.form.findField('hope_loan_deadline')
						.setValue(data[0].hope_loan_deadline);
				sxd_detail_winForm.form.findField('have_guarantee').setValue(
						data[0].have_guarantee + '');
				sxd_detail_winForm.form.findField('loan_purpose').setValue(
						data[0].loan_purpose + '');
				sxd_detail_winForm.form.findField('service_time').setValue(
						data[0].service_time);
				sxd_detail_winForm.form.findField('address').setValue(
						data[0].address);				
						
                if (data[0].identity == null || data[0].identity +''=='') {
							sxd_detail_winForm.form.findField('identity')
									.hide();
						}
				 if (data[0].monthly_income == null || data[0].monthly_income +''=='') {
							sxd_detail_winForm.form.findField('monthly_income')
									.hide();
						}
				 if (data[0].apply_loan_city == null || data[0].apply_loan_city +''=='') {
							sxd_detail_winForm.form.findField('apply_loan_city')
									.hide();
						}
				 if (data[0].traffic == null || data[0].traffic +''=='') {
							sxd_detail_winForm.form.findField('traffic')
									.hide();
						}
				 if (data[0].house == null || data[0].house +''=='') {
							sxd_detail_winForm.form.findField('house')
									.hide();
						}
				 if (data[0].hope_loan_amount == null || data[0].hope_loan_amount +''=='') {
							sxd_detail_winForm.form.findField('hope_loan_amount')
									.hide();
						}
						
			     if (data[0].hope_loan_deadline == null || data[0].hope_loan_deadline +''=='') {
							sxd_detail_winForm.form.findField('hope_loan_deadline')
									.hide();
						}
				 if (data[0].have_guarantee == null || data[0].have_guarantee +''=='') {
							sxd_detail_winForm.form.findField('have_guarantee')
									.hide();
						}
			     if (data[0].loan_purpose == null || data[0].loan_purpose +''=='') {
							sxd_detail_winForm.form.findField('loan_purpose')
									.hide();
						}
			    if (data[0].service_time == null || data[0].service_time +''=='') {
							sxd_detail_winForm.form.findField('service_time')
									.hide();
						}
			    if (data[0].address == null || data[0].address +''=='') {
					      if(data[0].detail_address == null || data[0].detail_address +''==''){
					      sxd_detail_winForm.form.findField('address')
									.hide();
					    }else{
					        sxd_detail_winForm.form.findField('address').setValue(
						     data[0].detail_address);
					}
					
				     }else{
				     if(data[0].detail_address == null || data[0].detail_address +''==''){
					
					}else{
					sxd_detail_winForm.form.findField('address').setValue(
						data[0].address+data[0].detail_address);
					}
				}
			} else {

			}
		},
		failure : function(request) {
			Ext.MessageBox.show({
				title : '操作提示',
				msg : "连接服务器失败",
				buttons : Ext.MessageBox.OK,
				icon : Ext.MessageBox.ERROR
			});
		},
		method : 'post'
	});
	var sxd_detailwindow = Ext.create('Ext.window.Window', {
		title : "订单详情",
		width : 450,
		height : 450,
		// height : 120,
		// plain : true,
		iconCls : "addicon",
		// 不可以随意改变大小
		resizable : true,
		//autoScroll : true,
		// 是否可以拖动
		draggable : true,
		collapsible : true, // 允许缩放条
		closeAction : 'close',
		closable : true,
		// 弹出模态窗体
		modal : 'true',
		buttonAlign : "center",
		bodyStyle : "padding:0 0 0 0",
		items : [ sxd_detail_winForm ]
	});
	sxd_detailwindow.show();
};

// 易学堂订单详情页
var yxt_detailform = function(order_id, app_type, service_type,orderTypeName) {
	var yxt_detail_winForm = Ext.create('Ext.form.Panel', {
		frame : true, // frame属性
		// title: 'Form Fields',
		width : 450,
		height : 470,
		bodyPadding : 5,
		// renderTo:"panel21",
		fieldDefaults : {
			labelAlign : 'left',
			labelWidth : 90,
			anchor : '90%'
		},
		items : [ {
			// 显示文本框，相当于label
			xtype : 'displayfield',
			name : 'displayfield1',
			// fieldLabel: 'Display field',
			value : '易学堂订单详情'

		}, {
			// 服务类型
			xtype : 'textfield',
			name : 'orderTypeName',
			fieldLabel : '服务类型',
			readOnly : true
		},{
			// 老师性别
			xtype : 'textfield',
			name : 'teacher_sex',
			fieldLabel : '老师性别',
			readOnly : true
		},{
			// 教师身份
			xtype : 'textfield',
			name : 'teacher_identity',
			fieldLabel : '教师身份',
			readOnly : true
		},{
			// 上课方式
			xtype : 'textfield',
			name : 'class_type',
			fieldLabel : '上课方式',
			readOnly : true
		},{
			// 上课时间
			xtype : 'textfield',
			name : 'class_date',
			fieldLabel : '上课时间',
			readOnly : true
		}, {
			// 服务项目
			xtype : 'textfield',
			name : 'service_item_show',
			fieldLabel : '服务项目',
			readOnly : true
		}, {
			// 服务类型
			xtype : 'textfield',
			name : 'service_type_show',
			fieldLabel : '服务类型',
			readOnly : true
		}, {
			// 服务小类
			xtype : 'textfield',
			name : 'service_class_show',
			fieldLabel : '服务小类',
			readOnly : true
		}, {
			// 预约地址
			xtype : 'textfield',
			name : 'address',
			fieldLabel : '预约地址',
			readOnly : true
		} ]
	});
	// 向后台获取订单详情数据
	Ext.Ajax
			.request({
				url : '/order/getOrderDetail',
				params : {
					order_id : order_id,
					app_type : app_type,
					service_type : service_type
				},
				success : function(response) {
					Ext.getBody().unmask();
					var json = Ext.JSON.decode(response.responseText);
					if (json.code == 'OK') {
						var data = json.data;
						yxt_detail_winForm.form.findField('orderTypeName')
								.setValue(orderTypeName);
						yxt_detail_winForm.form.findField('teacher_sex')
								.setValue(data[0].teacher_sex + '');
						yxt_detail_winForm.form.findField('teacher_identity')
								.setValue(data[0].teacher_identity + '');
						yxt_detail_winForm.form.findField('class_type')
								.setValue(data[0].class_type + '');
						yxt_detail_winForm.form.findField('class_date')
								.setValue(data[0].class_date);
						yxt_detail_winForm.form.findField('address').setValue(
								data[0].address);
						yxt_detail_winForm.form.findField('service_item_show')
								.setValue(data[0].service_item_show);
						yxt_detail_winForm.form.findField('service_type_show')
								.setValue(data[0].service_type_show);
						yxt_detail_winForm.form.findField('service_class_show')
								.setValue(data[0].service_class_show);
						
					   if (data[0].teacher_sex == null || data[0].teacher_sex +''=='') {
							yxt_detail_winForm.form.findField('teacher_sex')
									.hide();
						}
						 if (data[0].teacher_identity == null || data[0].teacher_identity +''=='') {
							yxt_detail_winForm.form.findField('teacher_identity')
									.hide();
						}
						 if (data[0].class_type == null || data[0].class_type +''=='') {
							yxt_detail_winForm.form.findField('class_type')
									.hide();
						}
						 if (data[0].class_date == null || data[0].class_date +''=='') {
							yxt_detail_winForm.form.findField('class_date')
									.hide();
						}
						   if (data[0].address == null || data[0].address +''=='') {
					      if(data[0].detail_address == null || data[0].detail_address +''==''){
					      yxt_detail_winForm.form.findField('address')
									.hide();
					    }else{
					        yxt_detail_winForm.form.findField('address').setValue(
						     data[0].detail_address);
					    }
					
				     }else{
				     if(data[0].detail_address == null || data[0].detail_address +''==''){
					
					}else{
					yxt_detail_winForm.form.findField('address').setValue(
						data[0].address+data[0].detail_address);
					}
				}
						if (data[0].service_item_show == null || data[0].service_item_show +''=='') {
							yxt_detail_winForm.form.findField('service_item_show')
									.hide();
						}
						 if (data[0].service_type_show == null || data[0].service_type_show +''=='') {
							yxt_detail_winForm.form.findField('service_type_show')
									.hide();
						}
						if (data[0].service_class_show == null || data[0].service_class_show +''=='') {
							yxt_detail_winForm.form.findField('service_class_show')
									.hide();
						}
					} else {

					}
				},
				failure : function(request) {
					Ext.MessageBox.show({
						title : '操作提示',
						msg : "连接服务器失败",
						buttons : Ext.MessageBox.OK,
						icon : Ext.MessageBox.ERROR
					});
				},
				method : 'post'
			});
	var yxt_detailwindow = Ext.create('Ext.window.Window', {
		title : "订单详情",
		width : 450,
		height : 350,
		// height : 120,
		// plain : true,
		iconCls : "addicon",
		// 不可以随意改变大小
		resizable : true,
		//autoScroll : true,
		// 是否可以拖动
		draggable : true,
		collapsible : true, // 允许缩放条
		closeAction : 'close',
		closable : true,
		// 弹出模态窗体
		modal : 'true',
		buttonAlign : "center",
		bodyStyle : "padding:0 0 0 0",
		items : [ yxt_detail_winForm ]
	});
	yxt_detailwindow.show();
};
// 车百通订单详情页
var cbt_detailform = function(order_id, app_type, service_type,orderTypeName) {
	var cbt_detail_winForm = Ext.create('Ext.form.Panel', {
		frame : true, // frame属性
		// title: 'Form Fields',
		width : 450,
		height : 470,
		bodyPadding : 5,

		// renderTo:"panel21",
		fieldDefaults : {
			labelAlign : 'center',
			labelWidth : 80,
			anchor : '95%'
		},
		items : [ {
			// 显示文本框，相当于label
			xtype : 'displayfield',
			name : 'displayfield1',
			value : '车百通订单详情'

		},{
			// 服务类型
			xtype : 'textfield',
			name : 'orderTypeName',
			fieldLabel : '服务类型',
			readOnly : true
		},{
			// 内饰清洗
			xtype : 'textfield',
			name : 'inside_wash',
			fieldLabel : '内饰清洗',
			readOnly : true
		},{
			// 代办类型
			xtype : 'textfield',
			name : 'agency_type',
			fieldLabel : '代办类型',
			readOnly : true
		},{
			//租车类型
			xtype : 'textfield',
			name : 'rental_type',
			fieldLabel : '租车类型',
			readOnly : true
		},{
			// 维修部位
			xtype : 'textfield',
			name : 'repair_place',
			fieldLabel : '维修部位',
			readOnly : true
		},{
			// 车辆类型
			xtype : 'textfield',
			name : 'rental_car_type',
			fieldLabel : '车辆类型',
			readOnly : true
		},{
			// 是否需要司机
			xtype : 'textfield',
			name : 'need_driver',
			fieldLabel : '司机',
			readOnly : true
		},{
			// 车辆数量
			xtype : 'textfield',
			name : 'car_num',
			fieldLabel : '车辆数量',
			readOnly : true
		},{
			// 练车时段
			xtype : 'textfield',
			name : 'practice_car_time',
			fieldLabel : '练车时段',
			readOnly : true
		},{
			// 报考车型
			xtype : 'textfield',
			name : 'car_type',
			fieldLabel : '报考车型',
			readOnly : true
		},{
			// 保养车辆
			xtype : 'textfield',
			name : 'upkeep_vehicle',
			fieldLabel : '保养车辆',
			readOnly : true
		},{
			// 保养内容
			xtype : 'textfield',
			name : 'upkeep_content',
			fieldLabel : '保养内容',
			readOnly : true
		},{
			// 美容服务项目
			xtype : 'textfield',
			name : 'beautify_service_items',
			fieldLabel : '服务项目',
			readOnly : true
		},{
			// 服务地点
			xtype : 'textfield',
			name : 'service_site',
			fieldLabel : '服务地点',
			readOnly : true
		},{
			// 车牌号
			xtype : 'textfield',
			name : 'car_no',
			fieldLabel : '车牌号',
			readOnly : true
		},
		{
			// 车辆停放预约地址
			xtype : 'textfield',
			name : 'car_park_address',
			fieldLabel : '停放地址',
			readOnly : true
		},
		{
			// 预约地址
			xtype : 'textfield',
			name : 'address',
			fieldLabel : '预约地址',
			readOnly : true
		},
		{
			// 服务的时间
			xtype : 'textfield',
			name : 'hope_service_time',
			fieldLabel : '服务时间',
			readOnly : true
		},
		{
			// 服务的时间
			xtype : 'datetimefield',
			name : 'service_time',
			fieldLabel : '服务时间',
			format : 'Y-m-d',
			readOnly : true
		},{
			// 需求
			xtype : 'textfield',
			name : 'demand',
			fieldLabel : '需求',
			readOnly : true
		}]
	});

	// 向后台获取订单详情数据
	Ext.Ajax
			.request({
				url : '/order/getOrderDetail',
				params : {
					order_id : order_id,
					app_type : app_type,
					service_type : service_type
				},
				success : function(response) {
					Ext.getBody().unmask();
					var json = Ext.JSON.decode(response.responseText);
					if (json.code == 'OK') {
						var data = json.data;
						cbt_detail_winForm.form.findField('orderTypeName')
								.setValue(orderTypeName);
						cbt_detail_winForm.form.findField('hope_service_time')
								.setValue(data[0].hope_service_time);
						cbt_detail_winForm.form.findField('service_time')
						        .setValue(data[0].service_time);
						cbt_detail_winForm.form.findField('car_no')
								.setValue(data[0].car_no);
						cbt_detail_winForm.form.findField('car_park_address')
								.setValue(data[0].car_park_address);
						cbt_detail_winForm.form.findField('address')
						.setValue(data[0].address);
						cbt_detail_winForm.form.findField('inside_wash')
								.setValue(data[0].inside_wash + '');
						
						cbt_detail_winForm.form.findField('demand')
						.setValue(data[0].demand);
						cbt_detail_winForm.form.findField('repair_place')
								.setValue(data[0].repair_place);
						cbt_detail_winForm.form.findField('agency_type')
								.setValue(data[0].agency_type+ '');
								
								
						cbt_detail_winForm.form.findField('car_type')
								.setValue(data[0].car_type);		
						cbt_detail_winForm.form.findField('practice_car_time')
								.setValue(data[0].practice_car_time);		
						cbt_detail_winForm.form.findField('rental_type')
								.setValue(data[0].rental_type+'');
						cbt_detail_winForm.form.findField('rental_car_type')
								.setValue(data[0].rental_car_type);		
						cbt_detail_winForm.form.findField('need_driver')
								.setValue(data[0].need_driver+'');				
						cbt_detail_winForm.form.findField('car_num')
								.setValue(data[0].car_num+'');
										
						
						cbt_detail_winForm.form.findField('upkeep_vehicle')
								.setValue(data[0].upkeep_vehicle);
						cbt_detail_winForm.form.findField('upkeep_content')
								.setValue(data[0].upkeep_content + '');
					
						
						cbt_detail_winForm.form.findField(
								'beautify_service_items').setValue(
								data[0].beautify_service_items);
						cbt_detail_winForm.form.findField('service_site')
								.setValue(data[0].service_site + '');
						

                      if (data[0].car_type == null || data[0].car_type +''=='') {
							cbt_detail_winForm.form.findField('car_type')
									.hide();
						}
                     if (data[0].practice_car_time == null || data[0].practice_car_time +''=='') {
							cbt_detail_winForm.form.findField('practice_car_time')
									.hide();
						}else{
				          var practice_car_time=getCommonName(data[0].practice_car_time,'cbtPracticeCarTime');
				          cbt_detail_winForm.form.findField('practice_car_time')
						         .setValue(practice_car_time);
				       }
                     if (data[0].rental_type == null || data[0].rental_type +''=='') {
							cbt_detail_winForm.form.findField('rental_type')
									.hide();
						}
                     if (data[0].rental_car_type == null || data[0].rental_car_type +''=='') {
							cbt_detail_winForm.form.findField('rental_car_type')
									.hide();
						}
                     if (data[0].need_driver == null || data[0].need_driver +''=='') {
							cbt_detail_winForm.form.findField('need_driver')
									.hide();
						}
                       if (data[0].car_num == null || data[0].car_num +''=='') {
							cbt_detail_winForm.form.findField('car_num')
									.hide();
						}
						if (data[0].hope_service_time == null || data[0].hope_service_time +''=='') {
							cbt_detail_winForm.form.findField(
									'hope_service_time').hide();
						}
						if (data[0].service_time == null || data[0].service_time +''=='') {
							cbt_detail_winForm.form.findField(
									'service_time').hide();
						}
						if (data[0].car_no == null || data[0].car_no +''=='') {
							cbt_detail_winForm.form.findField(
									'car_no').hide();
						}
						if (data[0].agency_type == null || data[0].agency_type +''=='') {
							cbt_detail_winForm.form.findField(
									'agency_type').hide();
						}
						if (data[0].car_park_address == null || data[0].car_park_address +''=='') {
							cbt_detail_winForm.form.findField(
									'car_park_address').hide();
						}
						 if (data[0].address == null || data[0].address +''=='') {
					      if(data[0].detail_address == null || data[0].detail_address +''==''){
					      cbt_detail_winForm.form.findField('address')
									.hide();
					    }else{
					        cbt_detail_winForm.form.findField('address').setValue(
						     data[0].detail_address);
					    }
					
				     }else{
				     if(data[0].detail_address == null || data[0].detail_address +''==''){
					
					}else{
					cbt_detail_winForm.form.findField('address').setValue(
						data[0].address+data[0].detail_address);
					}
				}
						if (data[0].inside_wash == null || data[0].inside_wash +''=='') {
							cbt_detail_winForm.form.findField('inside_wash')
									.hide();
						}
						if (data[0].repair_place == null || data[0].repair_place +''=='') {
							cbt_detail_winForm.form.findField('repair_place')
									.hide();
						} 
						if (data[0].demand == null || data[0].demand +''=='') {
							cbt_detail_winForm.form.findField('demand')
									.hide();
						}
						if (data[0].upkeep_vehicle == null || data[0].upkeep_vehicle +''=='') {
							cbt_detail_winForm.form.findField('upkeep_vehicle')
									.hide();
						}
						if (data[0].class_date == null || data[0].class_date +''=='') {
							cbt_detail_winForm.form.findField('upkeep_content')
									.hide();
						}
						
						
						if (data[0].beautify_service_items == null || data[0].beautify_service_items +''=='') {
							cbt_detail_winForm.form.findField(
									'beautify_service_items').hide();
						}
						if (data[0].service_site == null || data[0].service_site +''=='') {
							cbt_detail_winForm.form.findField('service_site')
									.hide();
						}
						
					} else {

					}
				},
				failure : function(request) {
					Ext.MessageBox.show({
						title : '操作提示',
						msg : "连接服务器失败",
						buttons : Ext.MessageBox.OK,
						icon : Ext.MessageBox.ERROR
					});
				},
				method : 'post'
			});
	var cbt_detailwindow = Ext.create('Ext.window.Window', {
		title : "订单详情",
		width : 450,
		height : 350,
		// height : 120,
		// plain : true,
		iconCls : "addicon",
		// 不可以随意改变大小
		resizable : true,
		//autoScroll : true,
		// 是否可以拖动
		draggable : true,
		collapsible : true, // 允许缩放条
		closeAction : 'close',
		closable : true,
		// 弹出模态窗体
		modal : 'true',
		buttonAlign : "center",
		bodyStyle : "padding:0 0 0 0",
		items : [ cbt_detail_winForm ]
	});
	cbt_detailwindow.show();
};

// 房源宝订单详情页
var fyb_detailform = function(order_id, app_type, service_type,orderTypeName) {

	var store_position_type = Ext.create("Ext.data.Store", {
		pageSize : 20, // 每页显示几条数据
		proxy : {
			type : 'ajax',
			url : '/common/getHouseType',
			reader : {
				type : 'json',
				totalProperty : 'total',
				root : 'data'
			}
		},
		fields : [ {
			name : 'dict_key'
		}, // mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置
		{
			name : 'dict_value'
		} ]
	});

	var fyb_detail_winForm = Ext.create('Ext.form.Panel', {
		frame : true, // frame属性
		// title: 'Form Fields',
		width : 440,
		height : 450,
		bodyPadding : 5,
		// renderTo:"panel21",
		fieldDefaults : {
			labelAlign : 'left',
			labelWidth : 90,
			anchor : '90%'
		},
		items : [ {
			// 显示文本框，相当于label
			xtype : 'displayfield',
			name : 'displayfield1',
			// fieldLabel: 'Display field',
			value : '房源宝订单详情'

		}, {
			// 服务类型
			xtype : 'textfield',
			name : 'orderTypeName',
			fieldLabel : '服务类型',
			readOnly : true
		},{
			// 房屋类型
			xtype : 'textfield',
			name : 'house_type',
			fieldLabel : '房屋类型',
			readOnly : true
		},{
			// 装修状况
			xtype : 'textfield',
			name : 'decoration_type',
			fieldLabel : '装修状况',
			readOnly : true
		},{
			// 房价
			xtype : 'textfield',
			name : 'house_price',
			fieldLabel : '房价',
			readOnly : true
		}, {
			// 房屋面积
			xtype : 'textfield',
			name : 'house_area',
			fieldLabel : '房屋面积',
			readOnly : true
		}, {
			// 房间数
			xtype : 'textfield',
			name : 'room_number',
			fieldLabel : '房间数',
			readOnly : true
		}, {
			// 客厅数
			xtype : 'textfield',
			name : 'parlour_number',
			fieldLabel : '客厅数',
			readOnly : true
		}, {
			// 卫生间数
			xtype : 'textfield',
			name : 'toilet_number',
			fieldLabel : '卫生间数',
			readOnly : true
		}, {
			// 楼层
			xtype : 'textfield',
			name : 'floor_number',
			fieldLabel : '楼层',
			readOnly : true
		}, {
			// 租金
			xtype : 'textfield',
			name : 'rent_money',
			fieldLabel : ' 租金',
			readOnly : true
		},{
			// 出租类型
			xtype : 'textfield',
			name : 'rent_type',
			fieldLabel : '出租类型',
			readOnly : true
		},{
			// 出租方式
			xtype : 'textfield',
			name : 'rent_way',
			fieldLabel : '出租方式',
			readOnly : true
		},{
			// 合租类型
			xtype : 'textfield',
			name : 'jointRent_type',
			fieldLabel : '合租类型',
			readOnly : true
		},{
			// 服务时间
			xtype : 'datetimefield',
			name : 'service_time',
			fieldLabel : '服务时间',
			format : 'Y-m-d',
			readOnly : true
		},{
			// 地址
			xtype : 'textfield',
			name : 'address',
			fieldLabel : '地址',
			readOnly : true
		} ]
	});

	// 向后台获取订单详情数据
	Ext.Ajax
			.request({
				url : '/order/getOrderDetail',
				params : {
					order_id : order_id,
					app_type : app_type,
					service_type : service_type
				},
				success : function(response) {
					Ext.getBody().unmask();
					var json = Ext.JSON.decode(response.responseText);
					if (json.code == 'OK') {
						var data = json.data;
						fyb_detail_winForm.form.findField('orderTypeName')
								.setValue(orderTypeName);
						fyb_detail_winForm.form.findField('house_type')
								.setValue(data[0].house_type + '');
						fyb_detail_winForm.form.findField('decoration_type')
								.setValue(data[0].decoration_type + '');
						fyb_detail_winForm.form.findField('house_price')
								.setValue(data[0].house_price + '');
						fyb_detail_winForm.form.findField('house_area')
								.setValue(data[0].house_area);
						fyb_detail_winForm.form.findField('room_number')
								.setValue(data[0].room_number + '室');
						fyb_detail_winForm.form.findField('rent_type')
								.setValue(data[0].rent_type + '');
						fyb_detail_winForm.form.findField('rent_way').setValue(
								data[0].rent_way + '');
						fyb_detail_winForm.form.findField('jointRent_type')
								.setValue(data[0].jointRent_type + '');
						fyb_detail_winForm.form.findField('parlour_number')
								.setValue(data[0].parlour_number+ '厅');
						fyb_detail_winForm.form.findField('toilet_number')
								.setValue(data[0].toilet_number+ '卫');
						fyb_detail_winForm.form.findField('floor_number')
								.setValue(data[0].floor_number);
						fyb_detail_winForm.form.findField('rent_money')
								.setValue(data[0].rent_money);
						fyb_detail_winForm.form.findField('service_time')
								.setValue(data[0].service_time);
                        fyb_detail_winForm.form.findField('address')
								.setValue(data[0].address);
								
						if (data[0].decoration_type == null || data[0].decoration_type +''=='') {
							fyb_detail_winForm.form
									.findField('decoration_type').hide();
						}
						if (data[0].house_price == null || data[0].house_price +''=='') {
							fyb_detail_winForm.form.findField('house_price')
									.hide();
						}
						if (data[0].house_area == null || data[0].house_area +''=='') {
							fyb_detail_winForm.form.findField('house_area')
									.hide();
						}

						if (data[0].room_number == null || data[0].room_number +''=='') {
							fyb_detail_winForm.form.findField('room_number')
									.hide();
						}
						if (data[0].rent_type == null || data[0].rent_type +''=='') {
							fyb_detail_winForm.form.findField('rent_type')
									.hide();
						}
						if (data[0].rent_way == null || data[0].rent_way +''=='') {
							fyb_detail_winForm.form.findField('rent_way')
									.hide();
						}
						if (data[0].jointRent_type == null || data[0].jointRent_type +''=='') {
							fyb_detail_winForm.form.findField('jointRent_type')
									.hide();
						}

						if (data[0].parlour_number == null || data[0].parlour_number +''=='') {
							fyb_detail_winForm.form.findField('parlour_number')
									.hide();
						}
						if (data[0].toilet_number == null || data[0].toilet_number +''=='') {
							fyb_detail_winForm.form.findField('toilet_number')
									.hide();
						}
						if (data[0].floor_number == null || data[0].floor_number +''=='') {
							fyb_detail_winForm.form.findField('floor_number')
									.hide();
						}
						if (data[0].service_time == null || data[0].service_time +''=='') {
							fyb_detail_winForm.form.findField('service_time')
									.hide();
						}
						if (data[0].address == null || data[0].address +''=='') {
					             if(data[0].detail_address == null || data[0].detail_address +''==''){
					                 fyb_detail_winForm.form.findField('address').hide();
					        }else{
					                 fyb_detail_winForm.form.findField('address').setValue( data[0].detail_address);
					         }
					        }else{
				                if(data[0].detail_address == null || data[0].detail_address +''==''){
					
					           }else{
					              fyb_detail_winForm.form.findField('address').setValue(data[0].address+data[0].detail_address);
					           }
				           }
						if (data[0].rent_money == null || data[0].rent_money +''=='') {
							fyb_detail_winForm.form.findField('rent_money')
									.hide();
						}

						if (data[0].house_type == null || data[0].house_type +''=='') {
							fyb_detail_winForm.form.findField('house_type')
									.hide();
						
						} 

					} else {

					}
				},
				failure : function(request) {
					Ext.MessageBox.show({
						title : '操作提示',
						msg : "连接服务器失败",
						buttons : Ext.MessageBox.OK,
						icon : Ext.MessageBox.ERROR
					});
				},
				method : 'post'
			});
	var fyb_detailwindow = Ext.create('Ext.window.Window', {
		title : "订单详情",
		width : 450,
		height : 450,
		// height : 120,
		// plain : true,
		iconCls : "addicon",
		//autoScroll : true,
		// 不可以随意改变大小
		resizable : true,
		// 是否可以拖动
		draggable : true,
		collapsible : true, // 允许缩放条
		closeAction : 'close',
		closable : true,
		// 弹出模态窗体
		modal : 'true',
		buttonAlign : "center",
		bodyStyle : "padding:0 0 0 0",
		items : [ fyb_detail_winForm ]
	});
	fyb_detailwindow.show();
};

// 货运通订单详情页
var hyt_detailform = function(order_id, app_type, service_type,orderTypeName) {
	var hyt_detail_winForm = Ext.create('Ext.form.Panel', {
		frame : true, // frame属性
		// title: 'Form Fields',
		width : 440,
		height : 350,
		bodyPadding : 5,
		// renderTo:"panel21",
		fieldDefaults : {
			labelAlign : 'left',
			labelWidth : 90,
			anchor : '90%'
		},
		items : [ {
			// 显示文本框，相当于label
			xtype : 'displayfield',
			name : 'displayfield1',
			// fieldLabel: 'Display field',
			value : '货运通订单详情'

		}, {
			// 服务类型
			xtype : 'textfield',
			name : 'orderTypeName',
			fieldLabel : '服务类型',
			readOnly : true
		},{
			// 货物类型
			xtype : 'textfield',
			name : 'commodity_type',
			fieldLabel : '货物类型',
			readOnly : true
		},{
			// 始发地
			xtype : 'textfield',
			name : 'start_address',
			fieldLabel : '始发地',
			readOnly : true
		},  {
			// 目的地
			xtype : 'textfield',
			name : 'destination',
			fieldLabel : '目的地',
			readOnly : true
		}, {
			// 距离
			xtype : 'textfield',
			name : 'distance',
			fieldLabel : '距离',
			readOnly : true
		},{
			// 重量
			xtype : 'textfield',
			name : 'weight',
			fieldLabel : '重量',
			readOnly : true
		},{
			// 体积
			xtype : 'textfield',
			name : 'volume',
			fieldLabel : '体积',
			readOnly : true
		}, {
			// 仓储月数
			xtype : 'textfield',
			name : 'months',
			fieldLabel : '仓储月数',
			readOnly : true
		},
		{
			// 服务时间
			xtype : 'textfield',
			name : 'service_time',
			fieldLabel : '服务时间',
			readOnly : true
		}, 
		{
			// 地址
			xtype : 'textfield',
			name : 'address',
			fieldLabel : '预约地址',
			readOnly : true
		}, 
		{
			// 其他需求
			xtype : 'textfield',
			name : 'demand',
			fieldLabel : '其他需求',
			readOnly : true
		}]
	});

	// 向后台获取订单详情数据
	Ext.Ajax.request({
		url : '/order/getOrderDetail',
		params : {
			order_id : order_id,
			app_type : app_type,
			service_type : service_type
		},
		success : function(response) {
			Ext.getBody().unmask();
			var json = Ext.JSON.decode(response.responseText);
			if (json.code == 'OK') {
				var data = json.data;
				hyt_detail_winForm.form.findField('orderTypeName').setValue(orderTypeName);
				hyt_detail_winForm.form.findField('commodity_type').setValue(
						data[0].commodity_type);
				hyt_detail_winForm.form.findField('start_address').setValue(
						data[0].start_address);
				hyt_detail_winForm.form.findField('destination')
						.setValue(data[0].destination + '');
				hyt_detail_winForm.form.findField('distance').setValue(
						data[0].distance);
				hyt_detail_winForm.form.findField('weight').setValue(
						data[0].weight + '');
				hyt_detail_winForm.form.findField('volume').setValue(
						data[0].volume + '');
				hyt_detail_winForm.form.findField('months').setValue(
						data[0].months);
				hyt_detail_winForm.form.findField('service_time').setValue(
						data[0].service_time);
				hyt_detail_winForm.form.findField('address')
						.setValue(data[0].address);
				hyt_detail_winForm.form.findField('demand').setValue(
						data[0].demand);
						
				if (data[0].commodity_type == null || data[0].commodity_type +''=='') {
							hyt_detail_winForm.form.findField(
									'commodity_type').hide();
					}
			   if (data[0].start_address == null || data[0].start_address +''=='') {
							hyt_detail_winForm.form.findField(
									'start_address').hide();
						}
			 if (data[0].destination == null || data[0].destination +''=='') {
							hyt_detail_winForm.form.findField(
									'destination').hide();
						}
			 if (data[0].distance == null || data[0].distance +''=='') {
							hyt_detail_winForm.form.findField(
									'distance').hide();
						}
			 if (data[0].weight == null || data[0].weight +''=='') {
							hyt_detail_winForm.form.findField(
									'weight').hide();
						}
			 if (data[0].volume == null || data[0].volume +''=='') {
							hyt_detail_winForm.form.findField(
									'volume').hide();
						}
			 if (data[0].months == null || data[0].months +''=='') {
							hyt_detail_winForm.form.findField(
									'months').hide();
						}
			 if (data[0].service_time == null || data[0].service_time +''=='') {
							hyt_detail_winForm.form.findField(
									'service_time').hide();
						}
			 if (data[0].address == null || data[0].address +''=='') {
					if(data[0].detail_address == null || data[0].detail_address +''==''){
					    hyt_detail_winForm.form.findField('address').hide();
				   }else{
					   hyt_detail_winForm.form.findField('address').setValue( data[0].detail_address);
					}
				}else{
				       if(data[0].detail_address == null || data[0].detail_address +''==''){
					
					 }else{
					   hyt_detail_winForm.form.findField('address').setValue(data[0].address+data[0].detail_address);
					           }
				           }
						
			if (data[0].demand == null || data[0].demand +''=='') {
							hyt_detail_winForm.form.findField(
									'demand').hide();
						}
			} else {

			}
		},
		failure : function(request) {
			Ext.MessageBox.show({
				title : '操作提示',
				msg : "连接服务器失败",
				buttons : Ext.MessageBox.OK,
				icon : Ext.MessageBox.ERROR
			});
		},
		method : 'post'
	});

	var hyt_detailwindow = Ext.create('Ext.window.Window', {
		title : "订单详情",
		width : 450,
		height : 350,
		// height : 120,
		// plain : true,
		iconCls : "addicon",
		// 不可以随意改变大小
		resizable : true,
		//autoScroll : true,
		// 是否可以拖动
		draggable : true,
		collapsible : true, // 允许缩放条
		closeAction : 'close',
		closable : true,
		// 弹出模态窗体
		modal : 'true',
		buttonAlign : "center",
		bodyStyle : "padding:0 0 0 0",
		items : [ hyt_detail_winForm ]
	});
	hyt_detailwindow.show();
};

// 金装修订单详情页
var jzx_detailform = function(order_id, app_type, service_type,orderTypeName) {
	var jzx_detail_winForm = Ext.create('Ext.form.Panel', {
		frame : true, // frame属性
		// title: 'Form Fields',
		width : 440,
		height : 350,
		bodyPadding : 5,
		// renderTo:"panel21",
		fieldDefaults : {
			labelAlign : 'left',
			labelWidth : 90,
			anchor : '90%'
		},
		items : [ {
			// 显示文本框，相当于label
			xtype : 'displayfield',
			name : 'displayfield1',
			// fieldLabel:
			// 'Display
			// field',
			value : '金装修订单详情'

		},{
			// 服务类型
			xtype : 'textfield',
			name : 'orderTypeName',
			fieldLabel : '服务类型',
			readOnly : true
		}, {
			// 装修风格
			xtype : 'textfield',
			name : 'decoration_style',
			fieldLabel : '装修风格',
			readOnly : true
		},{
			// 装修类型
			xtype : 'textfield',
			name : 'decoration_type',
			fieldLabel : '装修类型',
			readOnly : true
		},{
			// 装修面积
			xtype : 'textfield',
			name : 'decoration_area',
			fieldLabel : '装修面积',
			readOnly : true
		},{
			// 装修范围
			xtype : 'textfield',
			name : 'decoration_range',
			fieldLabel : '装修范围',
			readOnly : true
		}, {
			// 房型
			xtype : 'textfield',
			name : 'house_model',
			fieldLabel : '房型',
			readOnly : true
		}, {
			// 装修形式
			xtype : 'textfield',
			name : 'decoration_way',
			fieldLabel : '装修形式',
			readOnly : true
		},{
			// 装修档次
			xtype : 'textfield',
			name : 'decoration_level',
			fieldLabel : '装修档次',
			readOnly : true
		}, {
			// 装修预算
			xtype : 'textfield',
			name : 'decoration_budget_money',
			fieldLabel : '装修预算',
			readOnly : true
		},{
			// 送货上门
			xtype : 'textfield',
			name : 'service_to_home',
			fieldLabel : '送货上门',
			readOnly : true
		},{
			// 服务时间
			xtype : 'datetimefield',
			name : 'service_time',
			fieldLabel : '服务时间',
			format : 'Y-m-d',
			readOnly : true
		}, {
			// 服务地址
			xtype : 'textfield',
			name : 'address',
			fieldLabel : '预约地址',
			readOnly : true
		} ]
	});

	// 向后台获取订单详情数据
	Ext.Ajax.request({
		url : '/order/getOrderDetail',
		params : {
			order_id : order_id,
			app_type : app_type,
			service_type : service_type
		},
		success : function(response) {
			Ext.getBody().unmask();
			var json = Ext.JSON.decode(response.responseText);
			if (json.code == 'OK') {
				var data = json.data;
				jzx_detail_winForm.form.findField('orderTypeName').setValue(orderTypeName);
				jzx_detail_winForm.form.findField('decoration_style').setValue(
						data[0].decoration_style);
				jzx_detail_winForm.form.findField('decoration_type').setValue(
						data[0].decoration_type+'');
			    jzx_detail_winForm.form.findField('decoration_area').setValue(
						data[0].decoration_area);
				jzx_detail_winForm.form.findField('decoration_range').setValue(
						data[0].decoration_range);
				
				jzx_detail_winForm.form.findField('house_model').setValue(
						data[0].house_model);
				jzx_detail_winForm.form.findField('decoration_way').setValue(
						data[0].decoration_way + '');
				jzx_detail_winForm.form.findField('decoration_level').setValue(
						data[0].decoration_level);
				jzx_detail_winForm.form.findField('decoration_budget_money')
						.setValue(data[0].decoration_budget_money);
				jzx_detail_winForm.form.findField('service_to_home')
						.setValue(data[0].service_to_home + '');
				jzx_detail_winForm.form.findField('address').setValue(
						data[0].address);
				jzx_detail_winForm.form.findField('service_time').setValue(
						data[0].service_time);
				
				if (data[0].decoration_style == null || data[0].decoration_style +''=='') {
							jzx_detail_winForm.form.findField(
									'decoration_style').hide();
				}
			    if (data[0].decoration_type == null || data[0].decoration_type +''=='') {
							jzx_detail_winForm.form.findField(
									'decoration_type').hide();
				}		
				if (data[0].decoration_area == null || data[0].decoration_area +''=='') {
							jzx_detail_winForm.form.findField(
									'decoration_area').hide();
						}
			    if (data[0].decoration_range == null || data[0].decoration_range +''=='') {
							jzx_detail_winForm.form.findField(
									'decoration_range').hide();
				}
				if (data[0].house_model == null || data[0].house_model +''=='') {
							jzx_detail_winForm.form.findField(
									'house_model').hide();
						}		
		        if (data[0].decoration_way == null || data[0].decoration_way +''=='') {
							jzx_detail_winForm.form.findField(
									'decoration_way').hide();
						}
			    if (data[0].decoration_level == null || data[0].decoration_level +''=='') {
							jzx_detail_winForm.form.findField(
									'decoration_level').hide();
					}
				if (data[0].decoration_budget_money == null || data[0].decoration_budget_money +''=='') {
							jzx_detail_winForm.form.findField(
									'decoration_budget_money').hide();
						}
			    if (data[0].address == null || data[0].address +''=='') {
					             if(data[0].detail_address == null || data[0].detail_address +''==''){
					                 jzx_detail_winForm.form.findField('address').hide();
					        }else{
					                 jzx_detail_winForm.form.findField('address').setValue( data[0].detail_address);
					         }
					        }else{
				                if(data[0].detail_address == null || data[0].detail_address +''==''){
					
					           }else{
					              jzx_detail_winForm.form.findField('address').setValue(data[0].address+data[0].detail_address);
					           }
				           }
						
				if (data[0].service_to_home == null || data[0].service_to_home +''=='') {
							jzx_detail_winForm.form.findField(
									'service_to_home').hide();
						}	
			   if (data[0].service_time == null || data[0].service_time +''=='') {
							jzx_detail_winForm.form.findField(
									'service_time').hide();
						}			
			} else {

			}
		},
		failure : function(request) {
			Ext.MessageBox.show({
				title : '操作提示',
				msg : "连接服务器失败",
				buttons : Ext.MessageBox.OK,
				icon : Ext.MessageBox.ERROR
			});
		},
		method : 'post'
	});

	var jzx_detailwindow = Ext.create('Ext.window.Window', {
		title : "订单详情",
		width : 450,
		height : 400,
		// height : 120,
		// plain : true,
		iconCls : "addicon",
		// 不可以随意改变大小
		resizable : true,
		//autoScroll : true,
		// 是否可以拖动
		draggable : true,
		collapsible : true, // 允许缩放条
		closeAction : 'close',
		closable : true,
		// 弹出模态窗体
		modal : 'true',
		buttonAlign : "center",
		bodyStyle : "padding:0 0 0 0",
		items : [ jzx_detail_winForm ]
	});
	jzx_detailwindow.show();
};

// 亲子园订单详情页
var qzy_detailform = function(order_id, app_type, service_type,orderTypeName) {
	var qzy_detail_winForm = Ext.create('Ext.form.Panel', {
		frame : true, // frame属性
		// title: 'Form Fields',
		width : 440,
		height : 350,
		bodyPadding : 5,
		// renderTo:"panel21",
		fieldDefaults : {
			labelAlign : 'left',
			labelWidth : 90,
			anchor : '90%'
		},
		items : [ {
			// 显示文本框，相当于label
			xtype : 'displayfield',
			name : 'displayfield1',

			value : '亲子园订单详情'

		}, {
			// 服务类型
			xtype : 'textfield',
			name : 'orderTypeName',
			fieldLabel : '服务类型',
			readOnly : true
		},{
			// 摄影类型
			xtype : 'textfield',
			name : 'photograph_type',
			fieldLabel : '摄影类型',
			readOnly : true
		},{
			// 拍摄地点
			xtype : 'textfield',
			name : 'shooting_address',
			fieldLabel : '拍摄地点',
			readOnly : true
		},{
			// 预期消费
			xtype : 'textfield',
			name : 'expected_consumption',
			fieldLabel : '预期消费',
			readOnly : true
		},{
			// 宝宝性别
			xtype : 'textfield',
			name : 'baby_sex',
			fieldLabel : '宝宝性别',
			readOnly : true
		},{
			// 宝宝年龄
			xtype : 'textfield',
			name : 'baby_age',
			fieldLabel : '宝宝年龄',
			readOnly : true
		}, {
			// 课程类型
			xtype : 'textfield',
			name : 'class_type',
			fieldLabel : '课程类型',
			readOnly : true
		},{
			// 上课时间
			xtype : 'textfield',
			name : 'class_time',
			fieldLabel : '上课时间',
			readOnly : true
		},{
			// 教师性别
			xtype : 'textfield',
			name : 'teacher_sex',
			fieldLabel : '教师性别',
			readOnly : true
		},{
			// 游乐类型
			xtype : 'textfield',
			name : 'amusement_type',
			fieldLabel : '游乐类型',
			readOnly : true
		},{
			// 母音护理
			xtype : 'textfield',
			name : 'nurse_service_type',
			fieldLabel : '母音护理',
			readOnly : true
		},{
			// 时间
			xtype : 'textfield',
			name : 'service_time',
			fieldLabel : '时间',
			readOnly : true
		}, {
			// 地址
			xtype : 'textfield',
			name : 'address',
			fieldLabel : '预约地址',
			readOnly : true
		} ]
	});

	// 向后台获取订单详情数据
	Ext.Ajax.request({
		url : '/order/getOrderDetail',
		params : {
			order_id : order_id,
			app_type : app_type,
			service_type : service_type
		},
		success : function(response) {
			Ext.getBody().unmask();
			var json = Ext.JSON.decode(response.responseText);
			if (json.code == 'OK') {
				var data = json.data;
				qzy_detail_winForm.form.findField('orderTypeName').setValue(orderTypeName);
				qzy_detail_winForm.form.findField('photograph_type').setValue(
						data[0].photograph_type + '');
				qzy_detail_winForm.form.findField('shooting_address').setValue(
						data[0].shooting_address + '');
				qzy_detail_winForm.form.findField('expected_consumption')
						.setValue(data[0].expected_consumption);
				qzy_detail_winForm.form.findField('baby_sex').setValue(
						data[0].baby_sex + '');
				qzy_detail_winForm.form.findField('baby_age').setValue(
						data[0].baby_age);
				qzy_detail_winForm.form.findField('class_type').setValue(
						data[0].class_type + '');
				qzy_detail_winForm.form.findField('class_time').setValue(
						data[0].class_time);
				qzy_detail_winForm.form.findField('teacher_sex').setValue(
						data[0].teacher_sex + '');
				qzy_detail_winForm.form.findField('amusement_type').setValue(
						data[0].amusement_type + '');
				qzy_detail_winForm.form.findField('nurse_service_type').setValue(
						data[0].nurse_service_type + '');
				qzy_detail_winForm.form.findField('service_time')
						.setValue(data[0].service_time);
				qzy_detail_winForm.form.findField('address')
						.setValue(data[0].address);
				
				if (data[0].photograph_type == null || data[0].photograph_type +''=='') {
							qzy_detail_winForm.form.findField(
									'photograph_type').hide();
						}
				if (data[0].shooting_address == null || data[0].shooting_address +''=='') {
							qzy_detail_winForm.form.findField(
									'shooting_address').hide();
						}
				if (data[0].expected_consumption == null || data[0].expected_consumption +''=='') {
							qzy_detail_winForm.form.findField(
									'expected_consumption').hide();
						}
				if (data[0].baby_sex == null || data[0].baby_sex +''=='') {
							qzy_detail_winForm.form.findField(
									'baby_sex').hide();
						}		
				if (data[0].baby_age == null || data[0].baby_age +''=='') {
							qzy_detail_winForm.form.findField(
									'baby_age').hide();
						}
						
				if (data[0].class_type == null || data[0].class_type +''=='') {
							qzy_detail_winForm.form.findField(
									'class_type').hide();
						}
						 
			   if (data[0].class_time == null || data[0].class_time +''=='') {
							qzy_detail_winForm.form.findField(
									'class_time').hide();
						}
			   if (data[0].teacher_sex == null || data[0].teacher_sex +''=='') {
							qzy_detail_winForm.form.findField(
									'teacher_sex').hide();
						}			
				if (data[0].amusement_type == null || data[0].amusement_type +''=='') {
							qzy_detail_winForm.form.findField(
									'amusement_type').hide();
						}
				if (data[0].nurse_service_type == null || data[0].nurse_service_type +''=='') {
							qzy_detail_winForm.form.findField(
									'nurse_service_type').hide();
						}
				if (data[0].service_time == null || data[0].service_time +''=='') {
							qzy_detail_winForm.form.findField(
									'service_time').hide();
						}
			    if (data[0].address == null || data[0].address +''=='') {
					             if(data[0].detail_address == null || data[0].detail_address +''==''){
					                 qzy_detail_winForm.form.findField('address').hide();
					        }else{
					                 qzy_detail_winForm.form.findField('address').setValue( data[0].detail_address);
					         }
					        }else{
				                if(data[0].detail_address == null || data[0].detail_address +''==''){
					
					           }else{
					              qzy_detail_winForm.form.findField('address').setValue(data[0].address+data[0].detail_address);
					           }
				           }
			} else {

			}
		},
		failure : function(request) {
			Ext.MessageBox.show({
				title : '操作提示',
				msg : "连接服务器失败",
				buttons : Ext.MessageBox.OK,
				icon : Ext.MessageBox.ERROR
			});
		},
		method : 'post'
	});

	var qzy_detailwindow = Ext.create('Ext.window.Window', {
		title : "订单详情",
		width : 450,
		height : 350,
		// height : 120,
		// plain : true,
		iconCls : "addicon",
		// 不可以随意改变大小
		resizable : true,
		//autoScroll : true,
		// 是否可以拖动
		draggable : true,
		collapsible : true, // 允许缩放条
		closeAction : 'close',
		closable : true,
		// 弹出模态窗体
		modal : 'true',
		buttonAlign : "center",
		bodyStyle : "padding:0 0 0 0",
		items : [ qzy_detail_winForm ]
	});
	qzy_detailwindow.show();
};

// 洗乐吧订单详情页
var xlb_detailform = function(order_id, app_type, service_type,orderTypeName) {
	var xlb_detail_winForm = Ext.create('Ext.form.Panel', {
		frame : true, // frame属性
		// title: 'Form Fields',
		width : 440,
		height : 350,
		bodyPadding : 5,
		// renderTo:"panel21",
		fieldDefaults : {
			labelAlign : 'left',
			labelWidth : 90,
			anchor : '90%'
		},
		items : [ {
			// 显示文本框，相当于label
			xtype : 'displayfield',
			name : 'displayfield1',

			value : '洗乐吧订单详情'

		},{
			// 服务类型
			xtype : 'textfield',
			name : 'orderTypeName',
			fieldLabel : '服务类型',
			readOnly : true
		},{
			// 庆典或礼仪
			xtype : 'textfield',
			name : 'cele_or_model',
			fieldLabel : '庆典或礼仪',
			readOnly : true
		},{
			// 婚宴桌数
			xtype : 'textfield',
			name : 'table_number',
			fieldLabel : '婚宴桌数',
			readOnly : true
		},{
			// 预算金额
			xtype : 'textfield',
			name : 'budget',
			fieldLabel : '预算金额',
			readOnly : true
		},{
			// 婚纱类型
			xtype : 'textfield',
			name : 'wedding_dress_type',
			fieldLabel : '婚纱类型',
			readOnly : true
		},{
			// 庆典类型
			xtype : 'textfield',
			name : 'cele_type',
			fieldLabel : '庆典类型',
			readOnly : true
		},{
			// 庆典的节目内容
			xtype : 'textfield',
			name : 'cele_content',
			fieldLabel : '节目内容',
			readOnly : true
		},{
			// 模特类型
			xtype : 'textfield',
			name : 'model_type',
			fieldLabel : '模特类型',
			readOnly : true
		},{
			// 模特性别
			xtype : 'textfield',
			name : 'model_sex',
			fieldLabel : '模特性别',
			readOnly : true
		},{
			//模特年龄
			xtype : 'textfield',
			name : 'model_detail',
			fieldLabel : '模特年龄',
			readOnly : true
		},
		{
			//头车类型
			xtype : 'textfield',
			name : 'leader_car_type',
			fieldLabel : '头车类型',
			readOnly : true
		},{
			//是否需要头车司机
			xtype : 'textfield',
			name : 'leader_car_driver',
			fieldLabel : '头车司机',
			readOnly : true
		},{
			//车队车类型
			xtype : 'textfield',
			name : 'queue_car_type',
			fieldLabel : '车队车类型',
			readOnly : true
		},
		{
			//车队数量
			xtype : 'textfield',
			name : 'queue_car_number',
			fieldLabel : '车队数量',
			readOnly : true
		},{
			//租赁时间
			xtype : 'textfield',
			name : 'rent_time',
			fieldLabel : '租赁时间',
			readOnly : true
		},{
			//婚礼类型
			xtype : 'textfield',
			name : 'wedding_type',
			fieldLabel : '婚礼类型',
			readOnly : true
		},{
			//婚礼人员
			xtype : 'textfield',
			name : 'wedding_roles',
			fieldLabel : '婚礼人员',
			readOnly : true
		},{
			//主题色系
			xtype : 'textfield',
			name : 'theme',
			fieldLabel : '主题色系',
			readOnly : true
		},{
			//婚礼预期价格
			xtype : 'textfield',
			name : 'price_expire',
			fieldLabel : '婚礼预期价格',
			readOnly : true
		},{
			//服务时间
			xtype : 'textfield',
			name : 'service_time',
			fieldLabel : '服务时间',
			readOnly : true
		},
		{
			//地址
			xtype : 'textfield',
			name : 'address',
			fieldLabel : '预约地址',
			readOnly : true
		}]
	});

	// 向后台获取订单详情数据
	Ext.Ajax.request({
		url : '/order/getOrderDetail',
		params : {
			order_id : order_id,
			app_type : app_type,
			service_type : service_type
		},
		success : function(response) {
			Ext.getBody().unmask();
			var json = Ext.JSON.decode(response.responseText);
			if (json.code == 'OK') {
				var data = json.data;
				xlb_detail_winForm.form.findField('orderTypeName').setValue(orderTypeName);
				xlb_detail_winForm.form.findField('cele_or_model').setValue(
						data[0].cele_or_model );
				xlb_detail_winForm.form.findField('cele_type').setValue(
						data[0].cele_type);
						
			   xlb_detail_winForm.form.findField('wedding_dress_type').setValue(
						data[0].wedding_dress_type );
			   xlb_detail_winForm.form.findField('table_number').setValue(
						data[0].table_number);
				xlb_detail_winForm.form.findField('budget').setValue(
						data[0].budget);
						
				xlb_detail_winForm.form.findField('cele_content').setValue(
						data[0].cele_content + '');
				xlb_detail_winForm.form.findField('model_type').setValue(
						data[0].model_type + '');
				xlb_detail_winForm.form.findField('model_sex').setValue(
						data[0].model_sex + '');
				xlb_detail_winForm.form.findField('model_detail').setValue(
						data[0].model_detail + '');
				xlb_detail_winForm.form.findField('leader_car_type')
						.setValue(data[0].leader_car_type + '');
				xlb_detail_winForm.form.findField('leader_car_driver')
						.setValue(data[0].leader_car_driver + '');
				xlb_detail_winForm.form.findField('queue_car_type').setValue(
						data[0].queue_car_type + '');
				xlb_detail_winForm.form.findField('queue_car_number').setValue(
						data[0].queue_car_number + '');
				xlb_detail_winForm.form.findField('rent_time').setValue(
						data[0].rent_time+'');
				xlb_detail_winForm.form.findField('wedding_type').setValue(
						data[0].wedding_type + '');
				xlb_detail_winForm.form.findField('wedding_roles').setValue(
						data[0].wedding_roles + '');
				xlb_detail_winForm.form.findField('theme')
						.setValue(data[0].theme+ '');
				xlb_detail_winForm.form.findField('price_expire')
						.setValue(data[0].price_expire+ '');
				xlb_detail_winForm.form.findField('service_time')
						.setValue(data[0].service_time+ '');
				xlb_detail_winForm.form.findField('address')
						.setValue(data[0].address+ '');
				
				 if (data[0].wedding_type == null || data[0].wedding_type +''=='') {
							xlb_detail_winForm.form.findField(
									'wedding_type').hide();	}		
			   if (data[0].cele_or_model == null || data[0].cele_or_model +''=='') {
							xlb_detail_winForm.form.findField(
									'cele_or_model').hide();	}	
			   if (data[0].cele_type == null || data[0].cele_type +''=='') {
							xlb_detail_winForm.form.findField(
									'cele_type').hide();	}	
			   if (data[0].cele_content == null || data[0].cele_content +''=='') {
							xlb_detail_winForm.form.findField(
									'cele_content').hide();	
				}	
			   if (data[0].model_type == null || data[0].model_type +''=='') {
							xlb_detail_winForm.form.findField(
									'model_type').hide();	}	
			   if (data[0].model_sex == null || data[0].model_sex +''=='') {
							xlb_detail_winForm.form.findField(
									'model_sex').hide();	}	
			   if (data[0].model_detail == null || data[0].model_detail +''=='') {
							xlb_detail_winForm.form.findField(
									'model_detail').hide();	
			   }
			   
			   if (data[0].leader_car_type == null || data[0].leader_car_type +''=='') {
							xlb_detail_winForm.form.findField(
									'leader_car_type').hide();	
									}	
			   if (data[0].leader_car_driver == null || data[0].leader_car_driver +''=='') {
							xlb_detail_winForm.form.findField(
									'leader_car_driver').hide();	
									}	
				 if (data[0].queue_car_type == null || data[0].queue_car_type +''=='') {
							xlb_detail_winForm.form.findField(
									'queue_car_type').hide();	
									}	
				if (data[0].queue_car_number == null || data[0].queue_car_number +''=='') {
							xlb_detail_winForm.form.findField(
									'queue_car_number').hide();	}	
				 if (data[0].rent_time == null || data[0].rent_time +''=='') {
							xlb_detail_winForm.form.findField(
									'rent_time').hide();		}	
				if (data[0].wedding_roles == null || data[0].wedding_roles +''=='') {
							xlb_detail_winForm.form.findField(
									'wedding_roles').hide();	
				}
				if (data[0].theme == null || data[0].theme +''=='') {
							xlb_detail_winForm.form.findField(
									'theme').hide();		}	
				if (data[0].price_expire == null || data[0].price_expire +''=='') {
							xlb_detail_winForm.form.findField(
									'price_expire').hide();	}
				if (data[0].service_time == null || data[0].service_time +''=='') {
							xlb_detail_winForm.form.findField(
									'service_time').hide();		}
			   if (data[0].address == null || data[0].address +''=='') {
					             if(data[0].detail_address == null || data[0].detail_address +''==''){
					                 xlb_detail_winForm.form.findField('address').hide();
					        }else{
					                 xlb_detail_winForm.form.findField('address').setValue( data[0].detail_address);
					         }
					        }else{
				                if(data[0].detail_address == null || data[0].detail_address +''==''){
					
					           }else{
					              xlb_detail_winForm.form.findField('address').setValue(data[0].address+data[0].detail_address);
					           }
				           }	
				if (data[0].wedding_dress_type == null || data[0].wedding_dress_type +''=='') {
							xlb_detail_winForm.form.findField(
									'wedding_dress_type').hide();		}
			   if (data[0].table_number == null || data[0].table_number +''=='') {
							xlb_detail_winForm.form.findField(
									'table_number').hide();		}
			   if (data[0].budget == null || data[0].budget +''=='') {
							xlb_detail_winForm.form.findField(
									'budget').hide();		}
		       
			} else {

			}
		},
		failure : function(request) {
			Ext.MessageBox.show({
				title : '操作提示',
				msg : "连接服务器失败",
				buttons : Ext.MessageBox.OK,
				icon : Ext.MessageBox.ERROR
			});
		},
		method : 'post'
	});

	var xlb_detailwindow = Ext.create('Ext.window.Window', {
		title : "订单详情",
		width : 450,
		height : 350,
		// height : 120,
		// plain : true,
		iconCls : "addicon",
		// 不可以随意改变大小
		resizable : true,
		// 是否可以拖动
		draggable : true,
		//autoScroll : true,
		collapsible : true, // 允许缩放条
		closeAction : 'close',
		closable : true,
		// 弹出模态窗体
		modal : 'true',
		buttonAlign : "center",
		bodyStyle : "padding:0 0 0 0",
		items : [ xlb_detail_winForm ]
	});
	xlb_detailwindow.show();
};
// 易订餐订单详情页
var ydc_detailform = function(order_id, app_type, service_type,orderTypeName) {
	var ydc_detail_winForm = Ext.create('Ext.form.Panel', {
		frame : true, // frame属性
		// title: 'Form Fields',
		width : 440,
		height : 350,
		bodyPadding : 5,
		// renderTo:"panel21",
		fieldDefaults : {
			labelAlign : 'left',
			labelWidth : 90,
			anchor : '90%'
		},
		items : [ {
			// 显示文本框，相当于label
			xtype : 'displayfield',
			name : 'displayfield1',
			value : '易订餐订单详情'

		},{
			// 服务类型
			xtype : 'textfield',
			name : 'orderTypeName',
			fieldLabel : '服务类型',
			readOnly : true
		},{
			// 就餐类型
			xtype : 'textfield',
			name : 'eat_type',
			fieldLabel : '就餐类型',
			readOnly : true
		},{
			// 是否接受大厅
			xtype : 'textfield',
			name : 'is_receive_hall',
			fieldLabel : '大厅',
			readOnly : true
		},{
			// 就餐位置
			xtype : 'textfield',
			name : 'eat_address',
			fieldLabel : '就餐位置',
			readOnly : true
		},{
			// 人数
			xtype : 'textfield',
			name : 'people_num',
			fieldLabel : '人数',
			readOnly : true
		},{
			// 特殊要求
			xtype : 'textfield',
			name : 'special_requirements',
			fieldLabel : '特殊要求',
			readOnly : true
		},{
			// 服务时间
			xtype : 'textfield',
			name : 'service_time',
			fieldLabel : '服务时间',
			readOnly : true
		},{
			// 地址
			xtype : 'textfield',
			name : 'address',
			fieldLabel : '预约地址',
			readOnly : true
		}]
	});

	// 向后台获取订单详情数据
	Ext.Ajax.request({
		url : '/order/getOrderDetail',
		params : {
			order_id : order_id,
			app_type : app_type,
			service_type : service_type
		},
		success : function(response) {
			Ext.getBody().unmask();
			var json = Ext.JSON.decode(response.responseText);
			if (json.code == 'OK') {
				var data = json.data;
				ydc_detail_winForm.form.findField('orderTypeName').setValue(orderTypeName);
				ydc_detail_winForm.form.findField('eat_type').setValue(
						data[0].eat_type + '');
				ydc_detail_winForm.form.findField('eat_address').setValue(
						data[0].eat_address + '');
				ydc_detail_winForm.form.findField('special_requirements')
						.setValue(data[0].special_requirements);
				ydc_detail_winForm.form.findField('address').setValue(
						data[0].address);
				ydc_detail_winForm.form.findField('is_receive_hall').setValue(
						data[0].is_receive_hall + '');
				ydc_detail_winForm.form.findField('people_num').setValue(
						data[0].people_num);
				ydc_detail_winForm.form.findField('service_time').setValue(
						data[0].service_time);
						
              	if (data[0].eat_type == null || data[0].eat_type +''=='') {
							ydc_detail_winForm.form.findField(
									'eat_type').hide();	}	
				if (data[0].eat_address == null || data[0].eat_address +''=='') {
							ydc_detail_winForm.form.findField(
									'eat_address').hide();	}	
				if (data[0].special_requirements == null || data[0].special_requirements +''=='') {
							ydc_detail_winForm.form.findField(
									'special_requirements').hide();	
				}
				  if (data[0].address == null || data[0].address +''=='') {
					             if(data[0].detail_address == null || data[0].detail_address +''==''){
					                 ydc_detail_winForm.form.findField('address').hide();
					        }else{
					                 ydc_detail_winForm.form.findField('address').setValue( data[0].detail_address);
					         }
					        }else{
				                if(data[0].detail_address == null || data[0].detail_address +''==''){
					
					           }else{
					              ydc_detail_winForm.form.findField('address').setValue(data[0].address+data[0].detail_address);
					           }
				           }	
				
			   if (data[0].service_time == null || data[0].service_time +''=='') {
							ydc_detail_winForm.form.findField(
									'service_time').hide();			}	
		    	if (data[0].people_num == null || data[0].people_num +''=='') {
							ydc_detail_winForm.form.findField(
									'people_num').hide();			}
			   if (data[0].is_receive_hall == null || data[0].is_receive_hall +''=='') {
							ydc_detail_winForm.form.findField(
									'is_receive_hall').hide();			}																								
			} else {

			}
		},
		failure : function(request) {
			Ext.MessageBox.show({
				title : '操作提示',
				msg : "连接服务器失败",
				buttons : Ext.MessageBox.OK,
				icon : Ext.MessageBox.ERROR
			});
		},
		method : 'post'
	});

	var ydc_detailwindow = Ext.create('Ext.window.Window', {
		title : "订单详情",
		width : 450,
		height : 350,
		// height : 120,
		// plain : true,
		iconCls : "addicon",
		// 不可以随意改变大小
		resizable : true,
		//autoScroll : true,
		// 是否可以拖动
		draggable : true,
		collapsible : true, // 允许缩放条
		closeAction : 'close',
		closable : true,
		// 弹出模态窗体
		modal : 'true',
		buttonAlign : "center",
		bodyStyle : "padding:0 0 0 0",
		items : [ ydc_detail_winForm ]
	});
	ydc_detailwindow.show();
};

// 运动汇订单详情页
var ydh_detailform = function(order_id, app_type, service_type,orderTypeName) {
	var ydh_detail_winForm = Ext.create('Ext.form.Panel', {
		frame : true, // frame属性
		// title: 'Form Fields',
		width : 440,
		height : 350,
		bodyPadding : 5,
		// renderTo:"panel21",
		fieldDefaults : {
			labelAlign : 'left',
			labelWidth : 90,
			anchor : '90%'
		},
		items : [ {
			// 显示文本框，相当于label
			xtype : 'displayfield',
			name : 'displayfield1',

			value : '运动汇订单详情'

		},{
			// 服务类型
			xtype : 'textfield',
			name : 'orderTypeName',
			fieldLabel : '服务类型',
			readOnly : true
		},{
			// 健身方向
			xtype : 'textfield',
			name : 'gym_type',
			fieldLabel : '健身方向',
			readOnly : true
		},{
			// 健身方式
			xtype : 'textfield',
			name : 'gym_way',
			fieldLabel : '健身方式',
			readOnly : true
		},{
			// 兴趣方式
			xtype : 'textfield',
			name : 'interest_gym_way',
			fieldLabel : '兴趣方式',
			readOnly : true
		},{
			// 游泳馆类型
			xtype : 'textfield',
			name : 'natatoria_type',
			fieldLabel : '游泳馆类型',
			readOnly : true
		},{
			// 预约人数
			xtype : 'textfield',
			name : 'reservation_number',
			fieldLabel : '预约人数',
			readOnly : true
		},{
			//  球类类型
			xtype : 'textfield',
			name : 'ball_type',
			fieldLabel : '球类类型',
			readOnly : true
		},{
			// 消费时间段
			xtype : 'textfield',
			name : 'consumption_time_period',
			fieldLabel : '消费时间段',
			readOnly : true
		}, {
			// 溜冰场地类型
			xtype : 'textfield',
			name : 'skating_site_type',
			fieldLabel : '溜冰场地类型',
			readOnly : true
		},{
			// 预约时间
			xtype : 'textfield',
			name : 'reservation_time',
			fieldLabel : '预约时间',
			readOnly : true
		}, {
			// 地址
			xtype : 'textfield',
			name : 'address',
			fieldLabel : '预约地址',
			readOnly : true
		} ]
	});

	// 向后台获取订单详情数据
	Ext.Ajax.request({
		url : '/order/getOrderDetail',
		params : {
			order_id : order_id,
			app_type : app_type,
			service_type : service_type
		},
		success : function(response) {
			Ext.getBody().unmask();
			var json = Ext.JSON.decode(response.responseText);
			if (json.code == 'OK') {
				var data = json.data;
				ydh_detail_winForm.form.findField('orderTypeName').setValue(orderTypeName);
				ydh_detail_winForm.form.findField('gym_type').setValue(
						data[0].gym_type + '');
				ydh_detail_winForm.form.findField('gym_way').setValue(
						data[0].gym_way + '');
				ydh_detail_winForm.form.findField('interest_gym_way').setValue(
						data[0].interest_gym_way + '');
				ydh_detail_winForm.form.findField('natatoria_type').setValue(
						data[0].natatoria_type + '');
				ydh_detail_winForm.form.findField(
						'reservation_number').setValue(
						data[0].reservation_number);
				ydh_detail_winForm.form.findField('ball_type').setValue(
						data[0].ball_type + '');
				ydh_detail_winForm.form.findField('consumption_time_period')
						.setValue(data[0].consumption_time_period);
				ydh_detail_winForm.form.findField('skating_site_type')
						.setValue(data[0].skating_site_type + '');
				ydh_detail_winForm.form.findField('address')
						.setValue(data[0].address);
				
				ydh_detail_winForm.form.findField('reservation_time').setValue(
						data[0].reservation_time);
						
				if (data[0].reservation_number == null || data[0].reservation_number +''=='') {
							ydh_detail_winForm.form.findField(
									'reservation_number').hide();	}
			   if (data[0].gym_type == null || data[0].gym_type +''=='') {
							ydh_detail_winForm.form.findField(
									'gym_type').hide();	}	
				if (data[0].gym_way == null || data[0].gym_way +''=='') {
							ydh_detail_winForm.form.findField(
									'gym_way').hide();	}	
				if (data[0].interest_gym_way == null || data[0].interest_gym_way +''=='') {
							ydh_detail_winForm.form.findField(
									'interest_gym_way').hide();	
					}	
				if (data[0].natatoria_type == null || data[0].natatoria_type +''=='') {
							ydh_detail_winForm.form.findField(
									'natatoria_type').hide();	}	
				
				if (data[0].ball_type == null || data[0].ball_type +''=='') {
							ydh_detail_winForm.form.findField(
									'ball_type').hide();	}	
				if (data[0].consumption_time_period == null || data[0].consumption_time_period +''=='') {
							ydh_detail_winForm.form.findField(
									'consumption_time_period').hide();	}	
				if (data[0].skating_site_type == null || data[0].skating_site_type +''=='') {
							ydh_detail_winForm.form.findField(
									'skating_site_type').hide();
									}		
				
				if (data[0].reservation_time == null || data[0].reservation_time +''=='') {
							ydh_detail_winForm.form.findField(
									'reservation_time').hide();	
										}	
				if (data[0].address == null || data[0].address +''=='') {
					             if(data[0].detail_address == null || data[0].detail_address +''==''){
					                 ydh_detail_winForm.form.findField('address').hide();
					        }else{
					                 ydh_detail_winForm.form.findField('address').setValue( data[0].detail_address);
					         }
					        }else{
				                if(data[0].detail_address == null || data[0].detail_address +''==''){
					
					           }else{
					              ydh_detail_winForm.form.findField('address').setValue(data[0].address+data[0].detail_address);
					           }
				           }											
			} else {

			}
		},
		failure : function(request) {
			Ext.MessageBox.show({
				title : '操作提示',
				msg : "连接服务器失败",
				buttons : Ext.MessageBox.OK,
				icon : Ext.MessageBox.ERROR
			});
		},
		method : 'post'
	});

	var ydh_detailwindow = Ext.create('Ext.window.Window', {
		title : "订单详情",
		width : 450,
		height : 350,
		// height : 120,
		// plain : true,
		iconCls : "addicon",
		//autoScroll : true,
		// 不可以随意改变大小
		resizable : true,
		// 是否可以拖动
		draggable : true,
		collapsible : true, // 允许缩放条
		closeAction : 'close',
		closable : true,
		// 弹出模态窗体
		modal : 'true',
		buttonAlign : "center",
		bodyStyle : "padding:0 0 0 0",
		items : [ ydh_detail_winForm ]
	});
	ydh_detailwindow.show();
};

// 再云端订单详情页
var zyd_detailform = function(order_id, app_type, service_type,orderTypeName) {
	var zyd_detail_winForm = Ext.create('Ext.form.Panel', {
		frame : true, // frame属性
		// title: 'Form Fields',
		width : 440,
		height : 400,
		bodyPadding : 5,
		// renderTo:"panel21",
		fieldDefaults : {
			labelAlign : 'left',
			labelWidth : 90,
			anchor : '90%'
		},
		items : [ {
			// 显示文本框，相当于label
			xtype : 'displayfield',
			name : 'displayfield1',

			value : '再云端订单详情'

		},{
			// 服务类型
			xtype : 'textfield',
			name : 'orderTypeName',
			fieldLabel : '服务类型',
			readOnly : true
		},{
			//开发平台
			xtype : 'textfield',
			name : 'webSite_development_platform',
			fieldLabel : '开发平台',
			readOnly : true
		},{
			//移动开发平台
			xtype : 'textfield',
			name : 'mobile_development_platform',
			fieldLabel : '移动开发平台',
			readOnly : true
		},{
			//产品方向
			xtype : 'textfield',
			name : 'product_direction',
			fieldLabel : '产品方向',
			readOnly : true
		},{
			//开发预算
			xtype : 'textfield',
			name : 'development_budge',
			fieldLabel : '开发预算',
			readOnly : true
		}, {
			// 网站类型
			xtype : 'textfield',
			name : 'webSite_type',
			fieldLabel : '网站类型',
			readOnly : true
		},{
			// 制作类型
			xtype : 'textfield',
			name : 'make_type',
			fieldLabel : '制作类型',
			readOnly : true
		},{
			//省
			xtype : 'textfield',
			name : 'province',
			fieldLabel : '省',
			readOnly : true
		},{
			//市
			xtype : 'textfield',
			name : 'city',
			fieldLabel : '市',
			readOnly : true
		},{
			//完成时间
			xtype : 'textfield',
			name : 'finish_time',
			fieldLabel : '完成时间',
			readOnly : true
		},{
			// 服务时间
			xtype : 'datetimefield',
			name : 'service_time',
			fieldLabel : '服务时间',
			format : 'Y-m-d',
			readOnly : true
		},{
			//地址
			xtype : 'textfield',
			name : 'address',
			fieldLabel : '预约地址',
			readOnly : true
		},{
			//需求
			xtype : 'textfield',
			name : 'demand',
			fieldLabel : '需求',
			readOnly : true
		}  ]
	});

	// 向后台获取订单详情数据
	Ext.Ajax.request({
		url : '/order/getOrderDetail',
		params : {
			order_id : order_id,
			app_type : app_type,
			service_type : service_type
		},
		success : function(response) {
			Ext.getBody().unmask();
			var json = Ext.JSON.decode(response.responseText);
			if (json.code == 'OK') {
				var data = json.data;
				zyd_detail_winForm.form.findField('orderTypeName').setValue(orderTypeName);
				zyd_detail_winForm.form.findField('webSite_development_platform')
						.setValue(data[0].webSite_development_platform + '');
				zyd_detail_winForm.form.findField('mobile_development_platform')
						.setValue(data[0].mobile_development_platform);
						
				zyd_detail_winForm.form.findField('product_direction')
						.setValue(data[0].product_direction + '');
				
		    	zyd_detail_winForm.form.findField('demand')
						.setValue(data[0].demand + '');
				zyd_detail_winForm.form.findField('webSite_type').setValue(
						data[0].webSite_type + '');
				zyd_detail_winForm.form.findField('make_type').setValue(
						data[0].make_type + '');
				zyd_detail_winForm.form.findField('finish_time').setValue(
						data[0].finish_time);
				zyd_detail_winForm.form.findField('service_time').setValue(
						data[0].service_time);
				
				zyd_detail_winForm.form.findField('province')
						.setValue(data[0].province);
				zyd_detail_winForm.form.findField('city')
						.setValue(data[0].city);
				zyd_detail_winForm.form.findField('development_budge')
						.setValue(data[0].development_budge);
				zyd_detail_winForm.form.findField('address')
						.setValue(data[0].address);
						
			if (data[0].demand == null || data[0].demand +''=='') {
							zyd_detail_winForm.form.findField(
									'demand').hide();
						}			
			if (data[0].webSite_development_platform == null || data[0].webSite_development_platform +''=='') {
							zyd_detail_winForm.form.findField(
									'webSite_development_platform').hide();
						}
		    if (data[0].mobile_development_platform == null || data[0].mobile_development_platform +''=='') {
							zyd_detail_winForm.form.findField(
									'mobile_development_platform').hide();
			}		
			if (data[0].product_direction == null || data[0].product_direction +''=='') {
							zyd_detail_winForm.form.findField(
									'product_direction').hide();
						}
		  			
			if (data[0].development_budge == null || data[0].development_budge +''=='') {
							zyd_detail_winForm.form.findField(
									'development_budge').hide();
						}
			
		   			
			if (data[0].webSite_type == null || data[0].webSite_type +''=='') {
							zyd_detail_winForm.form.findField(
									'webSite_type').hide();
						}
		  if (data[0].province == null || data[0].province +''=='') {
							zyd_detail_winForm.form.findField(
									'province').hide();
						}
		if (data[0].city == null || data[0].city +''=='') {
							zyd_detail_winForm.form.findField(
									'city').hide();
						}  			
			if (data[0].make_type == null || data[0].make_type +''=='') {
							zyd_detail_winForm.form.findField(
									'make_type').hide();
						}			
		   if (data[0].finish_time == null || data[0].finish_time +''=='') {
							zyd_detail_winForm.form.findField(
									'finish_time').hide();
						}
		   if (data[0].service_time == null || data[0].service_time +''=='') {
							zyd_detail_winForm.form.findField(
									'service_time').hide();
						} 			
		 
		 			
		  if (data[0].address == null || data[0].address +''=='') {
					             if(data[0].detail_address == null || data[0].detail_address +''==''){
					                 zyd_detail_winForm.form.findField('address').hide();
					        }else{
					                 zyd_detail_winForm.form.findField('address').setValue( data[0].detail_address);
					         }
					        }else{
				                if(data[0].detail_address == null || data[0].detail_address +''==''){
					
					           }else{
					              zyd_detail_winForm.form.findField('address').setValue(data[0].address+data[0].detail_address);
					           }
				           }							
			} else {

			}
		},
		failure : function(request) {
			Ext.MessageBox.show({
				title : '操作提示',
				msg : "连接服务器失败",
				buttons : Ext.MessageBox.OK,
				icon : Ext.MessageBox.ERROR
			});
		},
		method : 'post'
	});

	var zyd_detailwindow = Ext.create('Ext.window.Window', {
		title : "订单详情",
		width : 450,
		height : 400,
		// height : 120,
		// plain : true,
		iconCls : "addicon",
		//autoScroll : true,
		// 不可以随意改变大小
		resizable : true,
		// 是否可以拖动
		draggable : true,
		collapsible : true, // 允许缩放条
		closeAction : 'close',
		closable : true,
		// 弹出模态窗体
		modal : 'true',
		buttonAlign : "center",
		bodyStyle : "padding:0 0 0 0",
		items : [ zyd_detail_winForm ]
	});
	zyd_detailwindow.show();
};

// 红妆订单详情页
var hz_detailform = function(order_id, app_type, service_type,orderTypeName) {
	var hz_detail_winForm = Ext.create('Ext.form.Panel', {
		frame : true, // frame属性
		// title: 'Form Fields',
		width : 440,
		height : 530,
		bodyPadding : 5,
		// renderTo:"panel21",
		fieldDefaults : {
			labelAlign : 'left',
			labelWidth : 90,
			anchor : '90%'
		},
		items : [ {
			// 显示文本框，相当于label
			xtype : 'displayfield',
			name : 'displayfield1',
			value : '红妆订单详情'

		},{
			// 服务类型
			xtype : 'textfield',
			name : 'orderTypeName',
			fieldLabel : '服务类型',
			readOnly : true
		},{
			// 美发类型
			xtype : 'textfield',
			name : 'hairdressing_type',
			fieldLabel : '美发类型',
			readOnly : true
		},{
			// 美容美体
			xtype : 'textfield',
			name : 'beautiful_face_body',
			fieldLabel : '美容美体',
			readOnly : true
		},{
			// 性别
			xtype : 'textfield',
			name : 'sex',
			fieldLabel : '性别',
			readOnly : true
		},{
			// 年龄
			xtype : 'textfield',
			name : 'age',
			fieldLabel : '年龄',
			readOnly : true
		} ,{
			//预算消费
			xtype : 'textfield',
			name : 'budget',
			fieldLabel : '预算消费',
			readOnly : true
		} ,{
			// 身高
			xtype : 'textfield',
			name : 'height',
			fieldLabel : '身高',
			readOnly : true
		} ,{
			// 上门服务
			xtype : 'textfield',
			name : 'service_way',
			fieldLabel : '上门服务',
			readOnly : true
		} ,
		
		{
			// 服务项目
			xtype : 'textfield',
			name : 'items',
			fieldLabel : '服务项目',
			readOnly : true
		},
		{
			// 服务时间
			xtype : 'textfield',
			name : 'service_time',
			fieldLabel : '服务时间',
			readOnly : true
		},{
			// 地址
			xtype : 'textfield',
			name : 'address',
			fieldLabel : '预约地址',
			readOnly : true
		} ]
	});

	// 向后台获取订单详情数据
	Ext.Ajax.request({
		url : '/order/getOrderDetail',
		params : {
			order_id : order_id,
			app_type : app_type,
			service_type : service_type
		},
		success : function(response) {
			Ext.getBody().unmask();
			var json = Ext.JSON.decode(response.responseText);
			if (json.code == 'OK') {
				var data = json.data;
				hz_detail_winForm.form.findField('orderTypeName').setValue(orderTypeName);
				hz_detail_winForm.form.findField('hairdressing_type').setValue(
						data[0].hairdressing_type + '');
				hz_detail_winForm.form.findField('beautiful_face_body').setValue(
						data[0].beautiful_face_body + '');
				hz_detail_winForm.form.findField('budget').setValue(
						data[0].budget);
						
				hz_detail_winForm.form.findField('sex').setValue(
						data[0].sex + '');
				hz_detail_winForm.form.findField('height').setValue(
						data[0].height);
				hz_detail_winForm.form.findField('age').setValue(
						data[0].age);
				hz_detail_winForm.form.findField('items').setValue(
						data[0].items + '');
				hz_detail_winForm.form.findField('service_time').setValue(
						data[0].service_time);
				hz_detail_winForm.form.findField('service_way').setValue(
						data[0].service_way + '');
				hz_detail_winForm.form.findField('address').setValue(
						data[0].address);
				if (data[0].hairdressing_type == null || data[0].hairdressing_type +''=='')
						 {
					hz_detail_winForm.form.findField('hairdressing_type')
							.hide();
				}
				if (data[0].beautiful_face_body == null || data[0].beautiful_face_body +''=='')
						 {
					hz_detail_winForm.form.findField('beautiful_face_body')
							.hide();
				}
				if (data[0].budget == null || data[0].budget +''=='')
						 {
					hz_detail_winForm.form.findField('budget')
							.hide();
				}
				if (data[0].sex == null || data[0].sex +''=='')
						 {
					hz_detail_winForm.form.findField('sex')
							.hide();
				}
				if (data[0].height == null || data[0].height +''=='')
						 {
					hz_detail_winForm.form.findField('height')
							.hide();
				}
				if (data[0].age == null || data[0].age +''=='')
						 {
					hz_detail_winForm.form.findField('age')
							.hide();
				}
				if (data[0].items == null || data[0].items +''=='')
						 {
					hz_detail_winForm.form.findField('items')
							.hide();
				}
				if (data[0].service_time == null || data[0].service_time +''=='')
						 {
					hz_detail_winForm.form.findField('service_time')
							.hide();
				}
				if (data[0].service_way == null || data[0].service_way +''=='')
						 {
					hz_detail_winForm.form.findField('service_way')
							.hide();
				}
				 if (data[0].address == null || data[0].address +''=='') {
					             if(data[0].detail_address == null || data[0].detail_address +''==''){
					                 hz_detail_winForm.form.findField('address').hide();
					        }else{
					                 hz_detail_winForm.form.findField('address').setValue( data[0].detail_address);
					         }
					        }else{
				                if(data[0].detail_address == null || data[0].detail_address +''==''){
					
					           }else{
					              hz_detail_winForm.form.findField('address').setValue(data[0].address+data[0].detail_address);
					           }
				           }		

			} else {

			}
		},
		failure : function(request) {
			Ext.MessageBox.show({
				title : '操作提示',
				msg : "连接服务器失败",
				buttons : Ext.MessageBox.OK,
				icon : Ext.MessageBox.ERROR
			});
		},
		method : 'post'
	});

	var hz_detailwindow = Ext.create('Ext.window.Window', {
		title : "订单详情",
		width : 445,
		height : 440,
		// height : 120,
		// plain : true,
		iconCls : "addicon",
		//autoScroll : true,
		//autoScroll : true,
		// 不可以随意改变大小
		resizable : true,
		// 是否可以拖动
		draggable : true,
		collapsible : true, // 允许缩放条
		closeAction : 'close',
		closable : true,
		// 弹出模态窗体
		modal : 'true',
		buttonAlign : "center",
		bodyStyle : "padding:0 0 0 0",
		items : [ hz_detail_winForm ]
	});
	hz_detailwindow.show();
};

// 艾秘书订单详情页
var ams_detailform = function(order_id, app_type, service_type,orderTypeName) {
	var ams_detail_winForm = Ext.create('Ext.form.Panel', {
		frame : true, // frame属性
		// title: 'Form Fields',
		width : 440,
		height : 350,
		bodyPadding : 5,
		// renderTo:"panel21",
		fieldDefaults : {
			labelAlign : 'left',
			labelWidth : 90,
			anchor : '90%'
		},
		items : [ {
			// 显示文本框，相当于label
			xtype : 'displayfield',
			name : 'displayfield1',

			value : '艾秘书订单详情'

		},{
			// 服务类型
			xtype : 'textfield',
			name : 'orderTypeName',
			fieldLabel : '服务类型',
			readOnly : true
		},{
			// 商标名
			xtype : 'textfield',
			name : 'brandName',
			fieldLabel : '商标名',
			readOnly : true
		},{
			// 代办时间
			xtype : 'datetimefield',
			name : 'agentTime',
			fieldLabel : '代办时间',
			format : 'Y-m-d',
			readOnly : true
		},{
			// 预约时间
			xtype : 'datetimefield',
			name : 'reserveTime',
			fieldLabel : '预约时间',
			format : 'Y-m-d',
			readOnly : true
		},{
			//代理项目
			xtype : 'textfield',
			name : 'serviceItem',
			fieldLabel : '代理项目',
			readOnly : true
		},{
			//签证国家
			xtype : 'textfield',
			name : 'signCountry',
			fieldLabel : '签证国家',
			readOnly : true
		},{
			//一级分类
			xtype : 'textfield',
			name : 'catalog',
			fieldLabel : '一级分类',
			readOnly : true
		},{
			//二级分类
			xtype : 'textfield',
			name : 'class',
			fieldLabel : '二级分类',
			readOnly : true
		},{
			//委托时长
			xtype : 'textfield',
			name : 'entrust_time',
			fieldLabel : '委托时长',
			readOnly : true
		} ,{
			//数量
			xtype : 'textfield',
			name : 'quality',
			fieldLabel : '数量',
			readOnly : true
		} ,{
			//上门服务
			xtype : 'textfield',
			name : 'service_to_home',
			fieldLabel : '上门服务',
			readOnly : true
		} , {

			// 地址
			xtype : 'textfield',
			name : 'address',
			fieldLabel : '预约地址',
			readOnly : true
		}, {

			// 补充说明
			xtype : 'textfield',
			name : 'demand',
			fieldLabel : '补充说明',
			readOnly : true
		} ]
	});

	// 向后台获取订单详情数据
	Ext.Ajax.request({
		url : '/order/getOrderDetail',
		params : {
			order_id : order_id,
			app_type : app_type,
			service_type : service_type
		},
		success : function(response) {
			Ext.getBody().unmask();
			var json = Ext.JSON.decode(response.responseText);
			if (json.code == 'OK') {
				var data = json.data;
				ams_detail_winForm.form.findField('orderTypeName').setValue(orderTypeName);
				ams_detail_winForm.form.findField('entrust_time')
						.setValue(data[0].entrust_time);
				ams_detail_winForm.form.findField('service_to_home')
						.setValue(data[0].service_to_home);
				ams_detail_winForm.form.findField('brandName')
						.setValue(data[0].brandName + '');
				ams_detail_winForm.form.findField('agentTime')
						.setValue(data[0].agentTime);
				ams_detail_winForm.form.findField('reserveTime').setValue(
						data[0].reserveTime);
				ams_detail_winForm.form.findField('serviceItem').setValue(
						data[0].serviceItem);
				ams_detail_winForm.form.findField('signCountry')
						.setValue(data[0].signCountry);
				ams_detail_winForm.form.findField('catalog').setValue(
						data[0].catalog);
				ams_detail_winForm.form.findField('class').setValue(
						data[0].class);
				ams_detail_winForm.form.findField('quality').setValue(
						data[0].quality);
				ams_detail_winForm.form.findField('address').setValue(
						data[0].address);
				ams_detail_winForm.form.findField('demand').setValue(
						data[0].demand);
				if (data[0].entrust_time == null || data[0].entrust_time +''=='')
						{
					ams_detail_winForm.form.findField('entrust_time')
							.hide();
				}
				if (data[0].service_to_home == null || data[0].service_to_home +''=='')
						{
					ams_detail_winForm.form.findField('service_to_home')
							.hide();
				}
				if (data[0].brandName == null || data[0].brandName +''=='')
						{
					ams_detail_winForm.form.findField('brandName')
							.hide();
				}
				if (data[0].agentTime == null || data[0].agentTime +''=='')
						{
					ams_detail_winForm.form.findField('agentTime')
							.hide();
				}
				if (data[0].reserveTime == null || data[0].reserveTime +''=='')
						 {
					ams_detail_winForm.form.findField('reserveTime')
							.hide();
				}
              if (data[0].serviceItem == null || data[0].serviceItem +''=='') {
							ams_detail_winForm.form.findField(
									'serviceItem').hide();	
				}
				if (data[0].signCountry == null || data[0].signCountry +''=='') {
							ams_detail_winForm.form.findField(
			 						'signCountry').hide();		
			   }
			   if (data[0].catalog == null || data[0].catalog +''=='') {
							ams_detail_winForm.form.findField(
									'catalog').hide();	
				}
			if (data[0].class == null || data[0].class +''=='') {
							ams_detail_winForm.form.findField(
									'class').hide();	
					}
			if (data[0].quality == null || data[0].quality +''=='') {
							ams_detail_winForm.form.findField(
									'quality').hide();	
				}
		  if (data[0].address == null || data[0].address +''=='') {
					             if(data[0].detail_address == null || data[0].detail_address +''==''){
					                 ams_detail_winForm.form.findField('address').hide();
					        }else{
					                 ams_detail_winForm.form.findField('address').setValue( data[0].detail_address);
					         }
					        }else{
				                if(data[0].detail_address == null || data[0].detail_address +''==''){
					
					           }else{
					              ams_detail_winForm.form.findField('address').setValue(data[0].address+data[0].detail_address);
					           }
				           }		
		if (data[0].demand == null || data[0].demand +''=='') {
							ams_detail_winForm.form.findField(
									'demand').hide();	
				}
			} else {

			}
		},
		failure : function(request) {
			Ext.MessageBox.show({
				title : '操作提示',
				msg : "连接服务器失败",
				buttons : Ext.MessageBox.OK,
				icon : Ext.MessageBox.ERROR
			});
		},
		method : 'post'
	});

	var ams_detailwindow = Ext.create('Ext.window.Window', {
		title : "订单详情",
		width : 450,
		height : 350,
		// height : 120,
		// plain : true,
		iconCls : "addicon",
		// 不可以随意改变大小
		resizable : true,
		//autoScroll : true,
		// 是否可以拖动
		draggable : true,
		collapsible : true, // 允许缩放条
		closeAction : 'close',
		closable : true,
		// 弹出模态窗体
		modal : 'true',
		buttonAlign : "center",
		bodyStyle : "padding:0 0 0 0",
		items : [ ams_detail_winForm ]
	});
	ams_detailwindow.show();
};

// 妙手堂订单详情页
var mst_detailform = function(order_id, app_type, service_type,orderTypeName) {
	var mst_detail_winForm = Ext.create('Ext.form.Panel', {
		frame : true, // frame属性
		// title: 'Form Fields',
		width : 440,
		height : 350,
		bodyPadding : 5,
		// renderTo:"panel21",
		fieldDefaults : {
			labelAlign : 'left',
			labelWidth : 90,
			anchor : '90%'
		},
		items : [ {
			// 显示文本框，相当于label
			xtype : 'displayfield',
			name : 'displayfield1',

			value : '妙手堂订单详情'

		},{
			// 服务类型
			xtype : 'textfield',
			name : 'orderTypeName',
			fieldLabel : '服务类型',
			readOnly : true
		},{
			// 服务地点
			xtype : 'textfield',
			name : 'service_site',
			fieldLabel : '服务地点',
			readOnly : true
		},{
			// 男性数量
			xtype : 'textfield',
			name : 'man_num',
			fieldLabel : '男性数量',
			readOnly : true
		},{
			// 女性数量
			xtype : 'textfield',
			name : 'woman_num',
			fieldLabel : '女性数量',
			readOnly : true
		},{
			// 服务项目
			xtype : 'textfield',
			name : 'service_item',
			fieldLabel : '服务项目',
			readOnly : true
		}, {
			// 预约时间
			xtype : 'textfield',
			name : 'service_time',
			fieldLabel : '预约时间',
			readOnly : true
		},{
			// 地址
			xtype : 'textfield',
			name : 'address',
			fieldLabel : '预约地址',
			readOnly : true
		}, {
			// 额外要求
			xtype : 'textfield',
			name : 'demand',
			fieldLabel : '额外要求',
			readOnly : true
		}  ]
	});

	// 向后台获取订单详情数据
	Ext.Ajax.request({
		url : '/order/getOrderDetail',
		params : {
			order_id : order_id,
			app_type : app_type,
			service_type : service_type
		},
		success : function(response) {
			Ext.getBody().unmask();
			var json = Ext.JSON.decode(response.responseText);
			if (json.code == 'OK') {
				var data = json.data;
				mst_detail_winForm.form.findField('orderTypeName').setValue(orderTypeName);
				mst_detail_winForm.form.findField('service_site').setValue(
						data[0].service_site + '');
				mst_detail_winForm.form.findField('man_num').setValue(
						data[0].man_num);
				mst_detail_winForm.form.findField('woman_num')
						.setValue(data[0].woman_num);
				mst_detail_winForm.form.findField('service_item').setValue(
						data[0].service_item + '');
				mst_detail_winForm.form.findField('service_item').setValue(
						data[0].service_item + '');
				mst_detail_winForm.form.findField('service_time').setValue(
						data[0].service_time);
				mst_detail_winForm.form.findField('address')
						.setValue(data[0].address);
				mst_detail_winForm.form.findField('demand').setValue(
						data[0].demand);
						
				if (data[0].service_site == null || data[0].service_site +''=='') {
							mst_detail_winForm.form.findField(
									'service_site').hide();
										}	
                if (data[0].man_num == null || data[0].man_num +''=='') {
							mst_detail_winForm.form.findField(
									'man_num').hide();
										}	
				if (data[0].woman_num == null || data[0].woman_num +''=='') {
							mst_detail_winForm.form.findField(
									'woman_num').hide();
										}	
                if (data[0].service_item == null || data[0].service_item +''=='') {
							mst_detail_winForm.form.findField(
									'service_item').hide();
					}
				
                if (data[0].service_time == null || data[0].service_time +''=='') {
							mst_detail_winForm.form.findField(
									'service_time').hide();	
										}	
				if (data[0].address == null || data[0].address +''=='') {
					             if(data[0].detail_address == null || data[0].detail_address +''==''){
					                 mst_detail_winForm.form.findField('address').hide();
					        }else{
					                 mst_detail_winForm.form.findField('address').setValue( data[0].detail_address);
					         }
					        }else{
				                if(data[0].detail_address == null || data[0].detail_address +''==''){
					
					           }else{
					              mst_detail_winForm.form.findField('address').setValue(data[0].address+data[0].detail_address);
					           }
				           }		
				if (data[0].demand == null || data[0].demand +''=='') {
							mst_detail_winForm.form.findField(
									'demand').hide();	
									}																		
			} else {

			}
		},
		failure : function(request) {
			Ext.MessageBox.show({
				title : '操作提示',
				msg : "连接服务器失败",
				buttons : Ext.MessageBox.OK,
				icon : Ext.MessageBox.ERROR
			});
		},
		method : 'post'
	});

	var mst_detailwindow = Ext.create('Ext.window.Window', {
		title : "订单详情",
		width : 450,
		height : 350,
		// height : 120,
		// plain : true,
		iconCls : "addicon",
		// 不可以随意改变大小
		resizable : true,
		//autoScroll : true,
		// 是否可以拖动
		draggable : true,
		collapsible : true, // 允许缩放条
		closeAction : 'close',
		closable : true,
		// 弹出模态窗体
		modal : 'true',
		buttonAlign : "center",
		bodyStyle : "padding:0 0 0 0",
		items : [ mst_detail_winForm ]
	});
	mst_detailwindow.show();
};
// 棋牌乐订单详情页
var qpl_detailform = function(order_id, app_type, service_type,orderTypeName) {
	var qpl_detail_winForm = Ext.create('Ext.form.Panel', {
		frame : true, // frame属性
		// title: 'Form Fields',
		width : 440,
		height : 350,
		bodyPadding : 5,
		// renderTo:"panel21",
		fieldDefaults : {
			labelAlign : 'left',
			labelWidth : 90,
			anchor : '90%'
		},
		items : [ {
			// 显示文本框，相当于label
			xtype : 'displayfield',
			name : 'displayfield1',

			value : '棋牌乐订单详情'

		},{
			// 服务类型
			xtype : 'textfield',
			name : 'orderTypeName',
			fieldLabel : '服务类型',
			readOnly : true
		}, {
			// 到店人数
			xtype : 'textfield',
			name : 'arrival_num_of_people',
			fieldLabel : '到店人数',
			readOnly : true
		},{
			// 扑克项目
			xtype : 'textfield',
			name : 'poker_item',
			fieldLabel : '扑克项目',
			readOnly : true
		},{
			// 棋类项目
			xtype : 'textfield',
			name : 'chess_item',
			fieldLabel : '棋类项目',
			readOnly : true
		},{
			// 预约时间
			xtype : 'textfield',
			name : 'reservation_time',
			fieldLabel : '预约时间',
			readOnly : true
		},{
			// 附加服务
			xtype : 'textfield',
			name : 'append_service',
			fieldLabel : '附加服务',
			readOnly : true
		}]
	});

	// 向后台获取订单详情数据
	Ext.Ajax.request({
		url : '/order/getOrderDetail',
		params : {
			order_id : order_id,
			app_type : app_type,
			service_type : service_type
		},
		success : function(response) {
			Ext.getBody().unmask();
			var json = Ext.JSON.decode(response.responseText);
			if (json.code == 'OK') {
				var data = json.data;
				qpl_detail_winForm.form.findField('orderTypeName').setValue(orderTypeName);
				qpl_detail_winForm.form.findField('arrival_num_of_people')
						.setValue(data[0].arrival_num_of_people);
				qpl_detail_winForm.form.findField('poker_item').setValue(
						data[0].poker_item + '');
				qpl_detail_winForm.form.findField('chess_item').setValue(
						data[0].chess_item+ '');
				qpl_detail_winForm.form.findField('reservation_time').setValue(
						data[0].reservation_time);
				qpl_detail_winForm.form.findField('append_service').setValue(
						data[0].append_service + '');
                if (data[0].poker_item == null || data[0].poker_item +''=='') {
							qpl_detail_winForm.form.findField(
									'poker_item').hide();	}
                if (data[0].arrival_num_of_people == null || data[0].arrival_num_of_people +''=='') {
							qpl_detail_winForm.form.findField(
									'arrival_num_of_people').hide();	}
				if (data[0].chess_item == null || data[0].chess_item +''=='') {
							qpl_detail_winForm.form.findField(
									'chess_item').hide();	}
				if (data[0].reservation_time == null || data[0].reservation_time +''=='') {
							qpl_detail_winForm.form.findField(
									'reservation_time').hide();	}
				if (data[0].append_service == null || data[0].append_service +''=='') {
							qpl_detail_winForm.form.findField(
									'append_service').hide();	}
			} else {

			}
		},
		failure : function(request) {
			Ext.MessageBox.show({
				title : '操作提示',
				msg : "连接服务器失败",
				buttons : Ext.MessageBox.OK,
				icon : Ext.MessageBox.ERROR
			});
		},
		method : 'post'
	});

	var qpl_detailwindow = Ext.create('Ext.window.Window', {
		title : "订单详情",
		width : 450,
		height : 350,
		// height : 120,
		// plain : true,
		iconCls : "addicon",
		// 不可以随意改变大小
		resizable : true,
		//autoScroll : true,
		// 是否可以拖动
		draggable : true,
		collapsible : true, // 允许缩放条
		closeAction : 'close',
		closable : true,
		// 弹出模态窗体
		modal : 'true',
		buttonAlign : "center",
		bodyStyle : "padding:0 0 0 0",
		items : [ qpl_detail_winForm ]
	});
	qpl_detailwindow.show();
};

// 摄影棚订单详情页
var syp_detailform = function(order_id, app_type, service_type,orderTypeName) {
	var syp_detail_winForm = Ext.create('Ext.form.Panel', {
		frame : true, // frame属性
		// title: 'Form Fields',
		width : 440,
		height : 350,
		bodyPadding : 5,
		// renderTo:"panel21",
		fieldDefaults : {
			labelAlign : 'left',
			labelWidth : 110,
			anchor : '90%'
		},
		items : [ {
			// 显示文本框，相当于label
			xtype : 'displayfield',
			name : 'displayfield1',
			value : '摄影棚订单详情'

		},{
			// 服务类型
			xtype : 'textfield',
			name : 'orderTypeName',
			fieldLabel : '服务类型',
			readOnly : true
		},{
			// 拍摄场景
			xtype : 'textfield',
			name : 'shoot_scene',
			fieldLabel : '拍摄场景',
			readOnly : true
		},{
			// 性别
			xtype : 'textfield',
			name : 'sex',
			fieldLabel : '性别',
			readOnly : true
		},
		
		{
			// 商业摄影类型
			xtype : 'textfield',
			name : 'shoot_type',
			fieldLabel : '商业摄影类型',
			readOnly : true
		},
		{
			// 宠物类型
			xtype : 'textfield',
			name : 'pet_type',
			fieldLabel : '宠物类型',
			readOnly : true
		},{
			// 照片尺寸
			xtype : 'textfield',
			name : 'photo_size',
			fieldLabel : '照片尺寸',
			readOnly : true
		},
		{
			// 价格预期
			xtype : 'textfield',
			name : 'budget',
			fieldLabel : '价格预期',
			readOnly : true
		},
		 {
			// 摄影师数量
			xtype : 'textfield',
			name : 'photographer_number',
			fieldLabel : '摄影师数量',
			readOnly : true
		}, {
			// 预计影片时长
			xtype : 'textfield',
			name : 'forecast_film_duration',
			fieldLabel : '预计影片时长',
			readOnly : true
		},
		{
			// 拍摄时间
			xtype : 'datetimefield',
			name : 'service_time',
			fieldLabel : '拍摄时间',
			format : 'Y-m-d',
			readOnly : true
		},{
			// 其他需求
			xtype : 'textfield',
			name : 'demand',
			fieldLabel : '其他需求',
			readOnly : true
		},{
			// 拍摄地址
			xtype : 'textfield',
			name : 'address',
			fieldLabel : '拍摄地址',
			readOnly : true
		}]
	});

	// 向后台获取订单详情数据
	Ext.Ajax.request({
		url : '/order/getOrderDetail',
		params : {
			order_id : order_id,
			app_type : app_type,
			service_type : service_type
		},
		success : function(response) {
			Ext.getBody().unmask();
			var json = Ext.JSON.decode(response.responseText);
			if (json.code == 'OK') {
				var data = json.data;
				syp_detail_winForm.form.findField('orderTypeName').setValue(orderTypeName);
				syp_detail_winForm.form.findField('shoot_scene').setValue(
						data[0].shoot_scene);
				syp_detail_winForm.form.findField('shoot_type').setValue(
						data[0].shoot_type);
				syp_detail_winForm.form.findField('pet_type').setValue(
						data[0].pet_type);
				syp_detail_winForm.form.findField('photo_size')
						.setValue(data[0].photo_size );
				syp_detail_winForm.form.findField('budget').setValue(
						data[0].budget );
				syp_detail_winForm.form.findField('photographer_number')
						.setValue(data[0].photographer_number);
				syp_detail_winForm.form.findField('forecast_film_duration')
						.setValue(data[0].forecast_film_duration);

				syp_detail_winForm.form.findField('service_time').setValue(
						data[0].service_time);
				syp_detail_winForm.form.findField('sex').setValue(
						data[0].sex);
				syp_detail_winForm.form.findField('demand').setValue(
						data[0].demand);
				syp_detail_winForm.form.findField('address').setValue(
						data[0].address);
								
				if (data[0].budget == null || data[0].budget +''=='')
				{
				syp_detail_winForm.form.findField('budget')
						.hide();
			    }
				if (data[0].sex == null || data[0].sex +''=='')
				{
				syp_detail_winForm.form.findField('sex')
						.hide();
			    }
				if (data[0].shoot_scene == null || data[0].shoot_scene +''=='')
					{
					syp_detail_winForm.form.findField('shoot_scene')
							.hide();
				}
				if (data[0].shoot_type == null || data[0].shoot_type +''=='')
				{
				syp_detail_winForm.form.findField('shoot_type')
						.hide();
			    } 
				if (data[0].pet_type == null || data[0].pet_type +''=='')
					 {
					syp_detail_winForm.form.findField('pet_type')
							.hide();
				}
				if (data[0].photo_size == null || data[0].photo_size +''=='')
					{
					syp_detail_winForm.form.findField('photo_size')
							.hide();
				}
				if (data[0].photographer_number == null || data[0].photographer_number +''=='')
				{
				syp_detail_winForm.form.findField('photographer_number')
						.hide();
			}
				if (data[0].forecast_film_duration == null || data[0].forecast_film_duration +''=='')
				{
				syp_detail_winForm.form.findField('forecast_film_duration')
						.hide();
			}
				if (data[0].photographer_number == null || data[0].photographer_number +''=='')
				{
				syp_detail_winForm.form.findField('photographer_number')
						.hide();
			}
			if (data[0].demand == null || data[0].demand +''=='')
				{
				syp_detail_winForm.form.findField('demand')
						.hide();
			}
			if (data[0].address == null || data[0].address +''=='') {
				if(data[0].detail_address == null || data[0].detail_address +''==''){
					 syp_detail_winForm.form.findField('address').hide();
				}else{
					 syp_detail_winForm.form.findField('address').setValue( data[0].detail_address);
				 }
				}else{
				     if(data[0].detail_address == null || data[0].detail_address +''==''){
					}else{
					  syp_detail_winForm.form.findField('address').setValue(data[0].address+data[0].detail_address);
				 }
			}	
			if (data[0].service_time == null || data[0].service_time +''=='')
				{
				syp_detail_winForm.form.findField('service_time')
						.hide();
			}
			} else {

			}
		},
		failure : function(request) {
			Ext.MessageBox.show({
				title : '操作提示',
				msg : "连接服务器失败",
				buttons : Ext.MessageBox.OK,
				icon : Ext.MessageBox.ERROR
			});
		},
		method : 'post'
	});

	var syp_detailwindow = Ext.create('Ext.window.Window', {
		title : "订单详情",
		width : 450,
		height : 350,
		// height : 120,
		// plain : true,
		iconCls : "addicon",
		//autoScroll : true,
		// 不可以随意改变大小
		resizable : true,
		// 是否可以拖动
		draggable : true,
		collapsible : true, // 允许缩放条
		closeAction : 'close',
		closable : true,
		// 弹出模态窗体
		modal : 'true',
		buttonAlign : "center",
		bodyStyle : "padding:0 0 0 0",
		items : [ syp_detail_winForm ]
	});
	syp_detailwindow.show();
};

// 推手订单详情页
var ts_detailform = function(order_id, app_type, service_type,orderTypeName) {
	var ts_detail_winForm = Ext.create('Ext.form.Panel', {
		frame : true, // frame属性
		// title: 'Form Fields',
		width : 440,
		height : 350,
		bodyPadding : 5,
		// renderTo:"panel21",
		fieldDefaults : {
			labelAlign : 'left',
			labelWidth : 90,
			anchor : '90%'
		},
		items : [ {
			// 显示文本框，相当于label
			xtype : 'displayfield',
			name : 'displayfield1',
			value : '推手订单详情'

		},{
			// 服务类型
			xtype : 'textfield',
			name : 'orderTypeName',
			fieldLabel : '服务类型',
			readOnly : true
		},{
			// 广告位类型
			xtype : 'textfield',
			name : 'ad_position_type',
			fieldLabel : '广告位类型',
			readOnly : true
		},{
			// 影视广告类型
			xtype : 'textfield',
			name : 'movie_ad_type',
			fieldLabel : '影视广告类型',
			readOnly : true
		},{
			// 网络营销类型
			xtype : 'textfield',
			name : 'net_marketing_type',
			fieldLabel : '网络营销类型',
			readOnly : true
		},{
			// 线下活动类型
			xtype : 'textfield',
			name : 'offline_activity_type',
			fieldLabel : '线下活动类型',
			readOnly : true
		}, {
			// 其他类型
			xtype : 'textfield',
			name : 'other_type',
			fieldLabel : '其他类型',
			readOnly : true
		},{
			// 推广时段
			xtype : 'textfield',
			name : 'extension_time',
			fieldLabel : '推广时段',
			readOnly : true
		}, {
			// 开始时间
			xtype : 'textfield',
			name : 'start_time',
			fieldLabel : '开始时间',
			readOnly : true
		},{
			// 结束时间
			xtype : 'textfield',
			name : 'end_time',
			fieldLabel : '结束时间',
			readOnly : true
		},{
			// 推广预算
			xtype : 'textfield',
			name : 'extension_budget',
			fieldLabel : '推广预算',
			readOnly : true
		},{
			// 预约时间
			xtype : 'textfield',
			name : 'reservation_time',
			fieldLabel : '预约时间',
			readOnly : true
		},{
			// 地址
			xtype : 'textfield',
			name : 'address',
			fieldLabel : '预约地址',
			readOnly : true
		},{
			// 备注
			xtype : 'textfield',
			name : 'demand',
			fieldLabel : '备注',
			readOnly : true
		}]
	});

	// 向后台获取订单详情数据
	Ext.Ajax.request({
		url : '/order/getOrderDetail',
		params : {
			order_id : order_id,
			app_type : app_type,
			service_type : service_type
		},
		success : function(response) {
			Ext.getBody().unmask();
			var json = Ext.JSON.decode(response.responseText);
			if (json.code == 'OK') {
				var data = json.data;
				ts_detail_winForm.form.findField('orderTypeName').setValue(orderTypeName);
				ts_detail_winForm.form.findField('ad_position_type').setValue(
						data[0].ad_position_type+ '');
				ts_detail_winForm.form.findField('movie_ad_type').setValue(
						data[0].movie_ad_type + '');
				ts_detail_winForm.form.findField('net_marketing_type').setValue(
						data[0].net_marketing_type + '');
				ts_detail_winForm.form.findField('offline_activity_type').setValue(
						data[0].offline_activity_type+ '');
				ts_detail_winForm.form.findField('other_type').setValue(
						data[0].other_type+ '');
				ts_detail_winForm.form.findField('extension_time').setValue(
						data[0].extension_time + '');
				ts_detail_winForm.form.findField('start_time').setValue(
						data[0].start_time);
				ts_detail_winForm.form.findField('end_time').setValue(
						data[0].end_time);
				ts_detail_winForm.form.findField('extension_budget').setValue(
						data[0].extension_budget + '');
				ts_detail_winForm.form.findField('reservation_time').setValue(
						data[0].reservation_time);
				ts_detail_winForm.form.findField('address').setValue(
						data[0].address );
				ts_detail_winForm.form.findField('demand').setValue(
						data[0].demand);
				if (data[0].ad_position_type == null || data[0].ad_position_type +''=='')
					{
					ts_detail_winForm.form.findField('ad_position_type')
							.hide();
				}
				if (data[0].movie_ad_type == null || data[0].movie_ad_type +''=='')
					{
					ts_detail_winForm.form.findField('movie_ad_type')
							.hide();
				}
				if (data[0].net_marketing_type == null || data[0].net_marketing_type +''=='')
					{
					ts_detail_winForm.form.findField('net_marketing_type')
							.hide();
				}
				if (data[0].offline_activity_type == null || data[0].offline_activity_type +''=='')
						 {
					ts_detail_winForm.form.findField('offline_activity_type')
							.hide();
				}
				if (data[0].other_type == null || data[0].other_type +''=='')
						 {
					ts_detail_winForm.form.findField('other_type')
							.hide();
				}
				if (data[0].extension_time == null || data[0].extension_time +''=='')
						 {
					ts_detail_winForm.form.findField('extension_time')
							.hide();
				}
				if (data[0].start_time == null || data[0].start_time +''=='')
						 {
					ts_detail_winForm.form.findField('start_time')
							.hide();
				}
				if (data[0].end_time == null || data[0].end_time +''=='')
						 {
					ts_detail_winForm.form.findField('end_time')
							.hide();
				}
				if (data[0].extension_budget == null || data[0].extension_budget +''=='')
						 {
					ts_detail_winForm.form.findField('extension_budget')
							.hide();
				}
				if (data[0].reservation_time == null || data[0].reservation_time +''=='')
						 {
					ts_detail_winForm.form.findField('reservation_time')
							.hide();
				}
				if (data[0].address == null || data[0].address +''=='') {
					             if(data[0].detail_address == null || data[0].detail_address +''==''){
					                 ts_detail_winForm.form.findField('address').hide();
					        }else{
					                 ts_detail_winForm.form.findField('address').setValue( data[0].detail_address);
					         }
					        }else{
				                if(data[0].detail_address == null || data[0].detail_address +''==''){
					
					           }else{
					              ts_detail_winForm.form.findField('address').setValue(data[0].address+data[0].detail_address);
					           }
				           }		
				if (data[0].demand == null || data[0].demand +''=='')
						 {
					ts_detail_winForm.form.findField('demand')
							.hide();
				}
				
			} else {

			}
		},
		failure : function(request) {
			Ext.MessageBox.show({
				title : '操作提示',
				msg : "连接服务器失败",
				buttons : Ext.MessageBox.OK,
				icon : Ext.MessageBox.ERROR
			});
		},
		method : 'post'
	});

	var ts_detailwindow = Ext.create('Ext.window.Window', {
		title : "订单详情",
		width : 450,
		height : 350,
		// height : 120,
		// plain : true,
		iconCls : "addicon",
		//autoScroll : true,
		// 不可以随意改变大小
		resizable : true,
		// 是否可以拖动
		draggable : true,
		collapsible : true, // 允许缩放条
		closeAction : 'close',
		closable : true,
		// 弹出模态窗体
		modal : 'true',
		buttonAlign : "center",
		bodyStyle : "padding:0 0 0 0",
		items : [ ts_detail_winForm ]
	});
	ts_detailwindow.show();
};

// 鲜花坊订单详情页
var xhf_detailform = function(order_id, app_type, service_type,orderTypeName) {
	var xhf_detail_winForm = Ext.create('Ext.form.Panel', {
		frame : true, // frame属性
		// title: 'Form Fields',
		width : 440,
		height : 400,
		bodyPadding : 5,
		// renderTo:"panel21",
		fieldDefaults : {
			labelAlign : 'left',
			labelWidth : 90,
			anchor : '90%'
		},
		items : [ {
			// 显示文本框，相当于label
			xtype : 'displayfield',
			name : 'displayfield1',
			value : '鲜花坊订单详情'

		},{
			// 服务类型
			xtype : 'textfield',
			name : 'orderTypeName',
			fieldLabel : '服务类型',
			readOnly : true
		},{
			// 花的样式
			xtype : 'textfield',
			name : 'flower_style',
			fieldLabel : '花的样式',
			readOnly : true
		},{
			// 鲜花种类
			xtype : 'textfield',
			name : 'flower_type',
			fieldLabel : '鲜花种类',
			readOnly : true
		},{
			// 花盒类型
			xtype : 'textfield',
			name : 'box_type',
			fieldLabel : '花盒类型',
			readOnly : true
		},{
			// 花篮类型
			xtype : 'textfield',
			name : 'basket_type',
			fieldLabel : '花篮类型',
			readOnly : true
		}, {
			// 花的数量
			xtype : 'textfield',
			name : 'quantity',
			fieldLabel : '花的数量',
			readOnly : true
		},{
			// 祝福对象
			xtype : 'textfield',
			name : 'wish_people',
			fieldLabel : '祝福对象',
			readOnly : true
		}, {
			// 祝福卡片内容
			xtype : 'textarea',
			name : 'blessings',
			fieldLabel : '祝福卡片内容',
			readOnly : true
		},{
			// 是否上门服务
			xtype : 'textarea',
			name : 'is_visit',
			fieldLabel : '上门服务',
			readOnly : true
		},{
			// 配送时间
			xtype : 'datetimefield',
			name : 'service_time',
			fieldLabel : '配送时间',
			format : 'Y-m-d',
			readOnly : true
		},{
			// 配送地点
			xtype : 'textfield',
			name : 'delivery_site',
			fieldLabel : '配送地点',
			readOnly : true
		},{
			// 其他需求
			xtype : 'textfield',
			name : 'demand',
			fieldLabel : '其他需求',
			readOnly : true
		}]
	});

	// 向后台获取订单详情数据
	Ext.Ajax.request({
		url : '/order/getOrderDetail',
		params : {
			order_id : order_id,
			app_type : app_type,
			service_type : service_type
		},
		success : function(response) {
			Ext.getBody().unmask();
			var json = Ext.JSON.decode(response.responseText);
			if (json.code == 'OK') {
				var data = json.data;
				xhf_detail_winForm.form.findField('orderTypeName').setValue(orderTypeName);
				xhf_detail_winForm.form.findField('flower_style')
						.setValue(data[0].flower_style + '');
				xhf_detail_winForm.form.findField('flower_type').setValue(
						data[0].flower_type + '');
				xhf_detail_winForm.form.findField('box_type').setValue(
						data[0].box_type + '');
				xhf_detail_winForm.form.findField('basket_type').setValue(
						data[0].basket_type + '');
						
				xhf_detail_winForm.form.findField('quantity').setValue(
						data[0].quantity);
				xhf_detail_winForm.form.findField('wish_people').setValue(
						data[0].wish_people);
				xhf_detail_winForm.form.findField('blessings').setValue(
						data[0].blessings);
				xhf_detail_winForm.form.findField('is_visit').setValue(
						data[0].is_visit + '');
				xhf_detail_winForm.form.findField('service_time').setValue(
						data[0].service_time);
				xhf_detail_winForm.form.findField('delivery_site')
						.setValue(data[0].delivery_site);
				xhf_detail_winForm.form.findField('demand')
						.setValue(data[0].demand);
              
              if (data[0].flower_style == null || data[0].flower_style +''=='')
					 {
					xhf_detail_winForm.form.findField('flower_style')
							.hide();
				   	}	
		      if (data[0].flower_type == null || data[0].flower_type +''=='')
						 {
					xhf_detail_winForm.form.findField('flower_type')
							.hide();
					}
			  if (data[0].box_type == null || data[0].box_type +''=='')
						 {
					xhf_detail_winForm.form.findField('box_type')
							.hide();
			 		}
			  if (data[0].basket_type == null || data[0].basket_type +''=='')
						 {
					xhf_detail_winForm.form.findField('basket_type')
							.hide();
					}	
			 if (data[0].quantity == null || data[0].quantity +''=='')
				 {
					xhf_detail_winForm.form.findField('quantity')
							.hide();}
			if (data[0].wish_people == null || data[0].wish_people +''=='')
				 {
					xhf_detail_winForm.form.findField('wish_people')
							.hide();}
			 if (data[0].blessings == null || data[0].blessings +''=='')
					 {
					xhf_detail_winForm.form.findField('blessings')
							.hide();
								}
			 if (data[0].is_visit == null || data[0].is_visit+ ''+''=='')
					 {
					xhf_detail_winForm.form.findField('is_visit')
							.hide();
								}	
		     if (data[0].service_time == null || data[0].service_time +''=='')
					 {
					xhf_detail_winForm.form.findField('service_time')
							.hide();
								}	
			 if (data[0].delivery_site == null || data[0].delivery_site +''=='')
					{
					xhf_detail_winForm.form.findField('delivery_site')
							.hide();
								}
			 if (data[0].demand == null || data[0].demand +''=='')
					{
					xhf_detail_winForm.form.findField('demand')
							.hide();
								}	
			} else {
             xhf_detail_winForm.form.hide();
			}
		},
		failure : function(request) {
			Ext.MessageBox.show({
				title : '操作提示',
				msg : "连接服务器失败",
				buttons : Ext.MessageBox.OK,
				icon : Ext.MessageBox.ERROR
			});
		},
		method : 'post'
	});

	var xhf_detailwindow = Ext.create('Ext.window.Window', {
		title : "订单详情",
		width : 450,
		height : 400,
		// height : 120,
		// plain : true,
		iconCls : "addicon",
		// 不可以随意改变大小
		resizable : true,
		//autoScroll : true,
		// 是否可以拖动
		draggable : true,
		collapsible : true, // 允许缩放条
		closeAction : 'close',
		closable : true,
		// 弹出模态窗体
		modal : 'true',
		buttonAlign : "center",
		bodyStyle : "padding:0 0 0 0",
		items : [ xhf_detail_winForm ]
	});
	xhf_detailwindow.show();
};

// 夜店坊订单详情页
var yd_detailform = function(order_id, app_type, service_type,orderTypeName) {
	var yd_detail_winForm = Ext.create('Ext.form.Panel', {
		frame : true, // frame属性
		// title: 'Form Fields',
		width : 440,
		height : 350,
		bodyPadding : 5,
		// renderTo:"panel21",
		fieldDefaults : {
			labelAlign : 'left',
			labelWidth : 90,
			anchor : '90%'
		},
		items : [ {
			// 显示文本框，相当于label
			xtype : 'displayfield',
			name : 'displayfield1',
			value : '夜店订单详情'

		},{
			// 服务类型
			xtype : 'textfield',
			name : 'orderTypeName',
			fieldLabel : '服务类型',
			readOnly : true
		},{
			// 酒吧类型
			xtype : 'textfield',
			name : 'bar_type',
			fieldLabel : '酒吧类型',
			readOnly : true
		},{
			// 主题酒吧类型
			xtype : 'textfield',
			name : 'theme_type',
			fieldLabel : '主题酒吧类型',
			readOnly : true
		},{
			// 座位偏好
			xtype : 'textfield',
			name : 'seat_preference',
			fieldLabel : '座位偏好',
			readOnly : true
		},{
			// 按摩方式
			xtype : 'textfield',
			name : 'cheirapsis_way',
			fieldLabel : '按摩方式',
			readOnly : true
		}, {
			// 按摩部位
			xtype : 'textfield',
			name : 'cheirapsis_part',
			fieldLabel : '按摩部位',
			readOnly : true
		}, {
			// 男性数量
			xtype : 'textfield',
			name : 'man_num',
			fieldLabel : '男性数量',
			readOnly : true
		},{
			// 女性数量
			xtype : 'textfield',
			name : 'woman_num',
			fieldLabel : '女性数量',
			readOnly : true
		},{
			// 预算
			xtype : 'textfield',
			name : 'budget',
			fieldLabel : '预算',
			readOnly : true
		},{
			// 是否代驾
			xtype : 'textfield',
			name : 'driving_service',
			fieldLabel : '是否代驾',
			readOnly : true
		},{
			// 桑拿类型
			xtype : 'textfield',
			name : 'sauna_type',
			fieldLabel : '桑拿类型',
			readOnly : true
		},{
			// 额外要求
			xtype : 'textfield',
			name : 'extra_service',
			fieldLabel : '额外要求',
			readOnly : true
		},{
			// 预约人数
			xtype : 'textfield',
			name : 'people_num',
			fieldLabel : '预约人数',
			readOnly : true
		},{
			// 会所类型
			xtype : 'textfield',
			name : 'club_type',
			fieldLabel : '会所类型',
			readOnly : true
		},{
			// 公主类型
			xtype : 'textfield',
			name : 'girl_type',
			fieldLabel : '公主类型',
			readOnly : true
		},{
			// 表演类型
			xtype : 'textfield',
			name : 'show_type',
			fieldLabel : '表演类型',
			readOnly : true
		},{
			// 洗浴类型
			xtype : 'textfield',
			name : 'massage_type',
			fieldLabel : '洗浴类型',
			readOnly : true
		},{
			// 足浴项目
			xtype : 'textfield',
			name : 'foot_item',
			fieldLabel : '足浴项目',
			readOnly : true
		},{
			// 技师性别
			xtype : 'textfield',
			name : 'technician_gender',
			fieldLabel : '技师性别',
			readOnly : true
		},{
			// 包厢大小
			xtype : 'textfield',
			name : 'box_size',
			fieldLabel : '包厢大小',
			readOnly : true
		},{
			// 点歌男人数
			xtype : 'textfield',
			name : 'song_man_num',
			fieldLabel : '点歌男人数',
			readOnly : true
		},{
			// 点歌女人数
			xtype : 'textfield',
			name : 'song_woman_num',
			fieldLabel : '点歌女人数',
			readOnly : true
		},{
			// SPA类型
			xtype : 'textfield',
			name : 'spa_type',
			fieldLabel : 'SPA类型',
			readOnly : true
		},{
			// 服务项目
			xtype : 'textfield',
			name : 'service_item',
			fieldLabel : '服务项目',
			readOnly : true
		},{
			// 预约时间
			xtype : 'datetimefield',
			name : 'service_time',
			fieldLabel : '预约时间',
			format : 'Y-m-d',
			readOnly : true
		}, {
			// 预约地址
			xtype : 'textfield',
			name : 'address',
			fieldLabel : '预约地址',
			readOnly : true
		}]
	});

	// 向后台获取订单详情数据
	Ext.Ajax.request({
		url : '/order/getOrderDetail',
		params : {
			order_id : order_id,
			app_type : app_type,
			service_type : service_type
		},
		success : function(response) {
			Ext.getBody().unmask();
			var json = Ext.JSON.decode(response.responseText);
			if (json.code == 'OK') {
				var data = json.data;
				yd_detail_winForm.form.findField('orderTypeName').setValue(orderTypeName);
				yd_detail_winForm.form.findField('cheirapsis_way').setValue(
						data[0].cheirapsis_way + '');
				yd_detail_winForm.form.findField('cheirapsis_part').setValue(
						data[0].cheirapsis_part + '');
				yd_detail_winForm.form.findField('bar_type').setValue(
						data[0].bar_type + '');
				yd_detail_winForm.form.findField('theme_type')
						.setValue(data[0].theme_type+ '');
				yd_detail_winForm.form.findField('seat_preference')
						.setValue(data[0].seat_preference + '');
				yd_detail_winForm.form.findField('man_num').setValue(
						data[0].man_num);
				yd_detail_winForm.form.findField('woman_num').setValue(
						data[0].woman_num);
				yd_detail_winForm.form.findField('budget').setValue(
						data[0].budget);
				yd_detail_winForm.form.findField('driving_service').setValue(
						data[0].driving_service + '');
				yd_detail_winForm.form.findField('sauna_type').setValue(
						data[0].sauna_type + '');
				yd_detail_winForm.form.findField('extra_service').setValue(
						data[0].extra_service);
				yd_detail_winForm.form.findField('people_num').setValue(
						data[0].people_num);
				yd_detail_winForm.form.findField('club_type').setValue(
						data[0].club_type + '');
				yd_detail_winForm.form.findField('girl_type').setValue(
						data[0].girl_type);
				yd_detail_winForm.form.findField('show_type').setValue(
						data[0].show_type + '');
						
				yd_detail_winForm.form.findField('massage_type').setValue(
						data[0].massage_type + '');
				yd_detail_winForm.form.findField('service_item').setValue(
						data[0].service_item + '');
				yd_detail_winForm.form.findField('foot_item').setValue(
						data[0].foot_item);		
				yd_detail_winForm.form.findField('technician_gender').setValue(
						data[0].technician_gender + '');
				yd_detail_winForm.form.findField('box_size').setValue(
						data[0].box_size + '');	
				yd_detail_winForm.form.findField('song_man_num').setValue(
						data[0].song_man_num);
				yd_detail_winForm.form.findField('song_woman_num').setValue(
						data[0].song_woman_num);
				yd_detail_winForm.form.findField('spa_type').setValue(
						data[0].spa_type + '');
					
				yd_detail_winForm.form.findField('service_time').setValue(
						data[0].service_time);
				yd_detail_winForm.form.findField('address').setValue(
						data[0].address);
							
			    if (data[0].bar_type == null || data[0].bar_type +''=='')
						{
					yd_detail_winForm.form.findField('bar_type')
							.hide();
								}	
			   if (data[0].theme_type == null || data[0].theme_type +''=='')
						{
					yd_detail_winForm.form.findField('theme_type')
							.hide();	}	
			 if (data[0].seat_preference == null || data[0].seat_preference +''=='')
						{
					yd_detail_winForm.form.findField('seat_preference')
							.hide();	}	
			   if (data[0].man_num == null || data[0].man_num +''=='')
						{
					yd_detail_winForm.form.findField('man_num')
							.hide();	}	
			if (data[0].woman_num == null || data[0].woman_num +''=='')
						{
					yd_detail_winForm.form.findField('woman_num')
							.hide();	}	
			   if (data[0].budget == null || data[0].budget +''=='')
						 {
					yd_detail_winForm.form.findField('budget')
							.hide();	}	
			  if (data[0].driving_service == null || data[0].driving_service +''=='')
					 {
					yd_detail_winForm.form.findField('driving_service')
							.hide();	}	
			   if (data[0].sauna_type == null || data[0].sauna_type +''=='')
					 {
					yd_detail_winForm.form.findField('sauna_type')
							.hide();	}	
			 if (data[0].extra_service == null || data[0].extra_service +''=='')
				 {
					yd_detail_winForm.form.findField('extra_service')
							.hide();	
			 }
			if (data[0].cheirapsis_way == null || data[0].cheirapsis_way +''=='')
						{
					yd_detail_winForm.form.findField('cheirapsis_way')
							.hide();
				}
			if (data[0].cheirapsis_part == null || data[0].cheirapsis_part +''=='')
						{
					yd_detail_winForm.form.findField('cheirapsis_part')
							.hide();
				}
			 if (data[0].people_num == null || data[0].people_num +''=='')
				 {
					yd_detail_winForm.form.findField('people_num')
							.hide();	}	
			 if (data[0].club_type == null || data[0].club_type +''=='')
				 {
					yd_detail_winForm.form.findField('club_type')
							.hide();	}	
			 if (data[0].girl_type == null || data[0].girl_type +''=='')
				 {
					yd_detail_winForm.form.findField('girl_type')
							.hide();	
				}	
			 if (data[0].show_type == null || data[0].show_type +''=='')
				 {
					yd_detail_winForm.form.findField('show_type')
							.hide();	}
							
			if (data[0].massage_type == null || data[0].massage_type +''=='')
				 {
					yd_detail_winForm.form.findField('massage_type')
							.hide();	}	
			 if (data[0].service_item == null || data[0].service_item +''=='')
				 {
					yd_detail_winForm.form.findField('service_item')
							.hide();	
			}
			 if (data[0].foot_item == null || data[0].foot_item +''=='')
				 {
					yd_detail_winForm.form.findField('foot_item')
							.hide();	
				}	
			 if (data[0].technician_gender == null || data[0].technician_gender +''=='')
				 {
					yd_detail_winForm.form.findField('technician_gender')
							.hide();	}
							
			if (data[0].box_size == null || data[0].box_size +''=='')
				 {
					yd_detail_winForm.form.findField('box_size')
							.hide();	}	
			 if (data[0].song_man_num == null || data[0].song_man_num +''=='')
				 {
					yd_detail_winForm.form.findField('song_man_num')
							.hide();	}	
			 if (data[0].song_woman_num == null || data[0].song_woman_num +''=='')
				 {
					yd_detail_winForm.form.findField('song_woman_num')
							.hide();	}	
			 if (data[0].spa_type == null || data[0].spa_type +''=='')
				 {
					yd_detail_winForm.form.findField('spa_type')
							.hide();	}
							
							
			if (data[0].service_item == null || data[0].service_item +''=='')
				 {
					yd_detail_winForm.form.findField('service_item')
							.hide();	}	
			 if (data[0].service_time == null || data[0].service_time +''=='')
				 {
					yd_detail_winForm.form.findField('service_time')
							.hide();	}	
			 if (data[0].address == null || data[0].address +''=='') {
					             if(data[0].detail_address == null || data[0].detail_address +''==''){
					                 yd_detail_winForm.form.findField('address').hide();
					        }else{
					                 yd_detail_winForm.form.findField('address').setValue( data[0].detail_address);
					         }
					        }else{
				                if(data[0].detail_address == null || data[0].detail_address +''==''){
					
					           }else{
					              yd_detail_winForm.form.findField('address').setValue(data[0].address+data[0].detail_address);
					           }
				           }		
				
				
			} else {
             
			}
		},
		failure : function(request) {
			Ext.MessageBox.show({
				title : '操作提示',
				msg : "连接服务器失败",
				buttons : Ext.MessageBox.OK,
				icon : Ext.MessageBox.ERROR
			});
		},
		method : 'post'
	});

	var yd_detailwindow = Ext.create('Ext.window.Window', {
		title : "订单详情",
		width : 450,
		height : 350,
		// height : 120,
		// plain : true,
		iconCls : "addicon",
		//autoScroll : true,
		// 不可以随意改变大小
		resizable : true,
		// 是否可以拖动
		draggable : true,
		collapsible : true, // 允许缩放条
		closeAction : 'close',
		closable : true,
		// 弹出模态窗体
		modal : 'true',
		buttonAlign : "center",
		bodyStyle : "padding:0 0 0 0",
		items : [ yd_detail_winForm ]
	});
	yd_detailwindow.show();
};
// 状师爷订单详情页
var zsy_detailform = function(order_id, app_type, service_type,orderTypeName) {

	var zsy_detail_winForm = Ext.create('Ext.form.Panel', {
		frame : true, // frame属性
		// title: 'Form Fields',
		width : 450,
		height : 550,
		bodyPadding : 5,
		// renderTo:"panel21",
		fieldDefaults : {
			labelAlign : 'left',
			labelWidth : 110,
			anchor : '90%'
		},
		items : [
				{
					// 显示文本框，相当于label
					xtype : 'displayfield',
					name : 'displayfield1',
					value : '状师爷订单详情'

				},{
			// 服务类型
			xtype : 'textfield',
			name : 'orderTypeName',
			fieldLabel : '服务类型',
			readOnly : true
		},
				{
					// 诉讼服务
					xtype : 'textfield',
					name : 'litigation_service',
					fieldLabel : '诉讼服务',
					readOnly : true
				} ,
				{
					// 民事诉讼
					xtype : 'textfield',
					name : 'civil_litigation',
					fieldLabel : '民事诉讼',
					readOnly : true
				} ,
				{
					// 民事诉讼分类1
					xtype : 'textfield',
					name : 'civil_litigation_item1',
					fieldLabel : '民事诉讼分类1',
					readOnly : true
				} ,
				{
					// 民事诉讼分类2
					xtype : 'textfield',
					name : 'civil_litigation_item2',
					fieldLabel : '民事诉讼',
					readOnly : true
				} ,
				{
					// 知识产权
					xtype : 'textfield',
					name : 'intellectual_property',
					fieldLabel : '知识产权',
					readOnly : true
				} ,{
					// 代表方
					xtype : 'textfield',
					name : 'representative_side',
					fieldLabel : '代表方',
					readOnly : true
				} ,
				{
					// 诉讼结果
					xtype : 'textfield',
					name : 'litigation_outcome',
					fieldLabel : '诉讼结果',
					readOnly : true
				} ,{
					// 法院判决
					xtype : 'textfield',
					name : 'court_decision',
					fieldLabel : '法院判决',
					readOnly : true
				} ,{
					// 诉讼仲裁
					xtype : 'textfield',
					name : 'litigation_arbitration',
					fieldLabel : '诉讼仲裁',
					readOnly : true
				} ,
				{
					// 预约时间
					xtype : 'datetimefield',
					name : 'court_decision_time',
					fieldLabel : '预约时间',
					format : 'Y-m-d',
					readOnly : true
				},{
					// 是否有证据
					xtype : 'datetimefield',
					name : 'is_have_evidence',
					fieldLabel : '证据',
					format : 'Y-m-d',
					readOnly : true
				},{
					// 是否法院通知
					xtype : 'datetimefield',
					name : 'is_court_notification',
					fieldLabel : '法院通知',
					format : 'Y-m-d',
					readOnly : true
				},
				{
					// 通知时间
					xtype : 'datetimefield',
					name : 'notification_time',
					fieldLabel : '通知时间',
					format : 'Y-m-d',
					readOnly : true
				} ,{
					// 非讼服务
					xtype : 'textfield',
					name : 'nolitigatio_service',
					fieldLabel : '非讼服务',
					readOnly : true
				} ,{
					// 分类
					xtype : 'textfield',
					name : 'nolitigatio_service_item',
					fieldLabel : '分类',
					readOnly : true
				} ,{
					// 男人数量
					xtype : 'textfield',
					name : 'man_number',
					fieldLabel : '男人数量',
					readOnly : true
				} ,{
					// 女人数量
					xtype : 'textfield',
					name : 'woman_number',
					fieldLabel : '女人数量',
					readOnly : true
				} ,{
					// 性别
					xtype : 'textfield',
					name : 'sex',
					fieldLabel : '性别',
					readOnly : true
				} ,{
					// 律师工作年限
					xtype : 'textfield',
					name : 'lawyers_working_age',
					fieldLabel : '律师工作年限',
					readOnly : true
				},
				{
					// 预约时间
					xtype : 'datetimefield',
					name : 'service_time',
					fieldLabel : '预约时间',
					format : 'Y-m-d',
					readOnly : true
				},
				{
					// 预约地址
					xtype : 'textfield',
					name : 'address',
					fieldLabel : '预约地址',
					readOnly : true
				}
				]
	});

	// 向后台获取订单详情数据
	Ext.Ajax.request({
		url : '/order/getOrderDetail',
		params : {
			order_id : order_id,
			app_type : app_type,
			service_type : service_type
		},
		success : function(response) {
			Ext.getBody().unmask();
			var json = Ext.JSON.decode(response.responseText);
			if (json.code == 'OK') {
				var data = json.data;
				zsy_detail_winForm.form.findField('orderTypeName').setValue(orderTypeName);
				zsy_detail_winForm.form.findField('litigation_service')
						.setValue(data[0].litigation_service);
				zsy_detail_winForm.form.findField('civil_litigation')
						.setValue(data[0].civil_litigation);
				zsy_detail_winForm.form.findField('civil_litigation_item1')
						.setValue(data[0].civil_litigation_item1);
				zsy_detail_winForm.form.findField('civil_litigation_item2')
						.setValue(data[0].civil_litigation_item2);

				zsy_detail_winForm.form.findField('intellectual_property')
						.setValue(data[0].intellectual_property);
				zsy_detail_winForm.form.findField('representative_side')
						.setValue(data[0].representative_side + '');
				zsy_detail_winForm.form.findField('litigation_outcome')
						.setValue(data[0].litigation_outcome + '');
				zsy_detail_winForm.form.findField('court_decision').setValue(
						data[0].court_decision + '');
				zsy_detail_winForm.form.findField('litigation_arbitration')
						.setValue(data[0].litigation_arbitration + '');
				zsy_detail_winForm.form.findField('court_decision_time').setValue(
						data[0].court_decision_time);
			    zsy_detail_winForm.form.findField('is_have_evidence').setValue(
						data[0].is_have_evidence + '');
			    zsy_detail_winForm.form.findField('is_court_notification').setValue(
						data[0].is_court_notification + '');
                zsy_detail_winForm.form.findField('notification_time').setValue(
						data[0].notification_time);
			    zsy_detail_winForm.form.findField('service_time').setValue(
						data[0].service_time);
				zsy_detail_winForm.form.findField('address').setValue(
						data[0].address);
				
				zsy_detail_winForm.form.findField('nolitigatio_service')
						.setValue(data[0].nolitigatio_service);
				zsy_detail_winForm.form.findField('nolitigatio_service_item')
						.setValue(data[0].nolitigatio_service_item);
				
				 zsy_detail_winForm.form.findField('man_number').setValue(
						data[0].man_number);
				 zsy_detail_winForm.form.findField('woman_number').setValue(
						data[0].woman_number);
				 zsy_detail_winForm.form.findField('sex').setValue(
						data[0].sex + '');		
				 zsy_detail_winForm.form.findField('lawyers_working_age').setValue(
						data[0].lawyers_working_age);
				
				
				
				
				if (data[0].litigation_service == null || data[0].litigation_service +''=='')
						{
					zsy_detail_winForm.form.findField('litigation_service')
							.hide();
				}
				if (data[0].civil_litigation == null || data[0].civil_litigation +''=='')
						 {
					zsy_detail_winForm.form.findField('civil_litigation')
							.hide();
				}
				if (data[0].civil_litigation_item1 == null || data[0].civil_litigation_item1 +''=='')
						 {
				zsy_detail_winForm.form.findField('civil_litigation_item1')
							.hide();
				}
				if (data[0].civil_litigation_item2 == null || data[0].civil_litigation_item2 +''=='') {
					zsy_detail_winForm.form.findField('civil_litigation_item2')
							.hide();
				}
				if (data[0].intellectual_property == null || data[0].intellectual_property +''==''){
					zsy_detail_winForm.form.findField('intellectual_property')
							.hide();
				}
				if (data[0].representative_side == null || data[0].representative_side +''=='') {
					zsy_detail_winForm.form.findField('representative_side')
							.hide();
				}
				if (data[0].litigation_outcome == null || data[0].litigation_outcome +''=='') {
					zsy_detail_winForm.form.findField('litigation_outcome')
							.hide();
				}
				
				if (data[0].court_decision == null || data[0].court_decision +''=='') {
					zsy_detail_winForm.form.findField('court_decision')
							.hide();
				}
				if (data[0].litigation_arbitration == null || data[0].litigation_arbitration +''=='') {
					zsy_detail_winForm.form.findField('litigation_arbitration')
							.hide();
				}
				if (data[0].court_decision_time == null || data[0].court_decision_time +''=='') {
					zsy_detail_winForm.form.findField('court_decision_time')
							.hide();
				}
				if (data[0].is_have_evidence == null || data[0].is_have_evidence +''=='') {
					zsy_detail_winForm.form.findField('is_have_evidence')
							.hide();
				}
				
				if (data[0].is_court_notification == null || data[0].is_court_notification +''=='') {
					zsy_detail_winForm.form.findField('is_court_notification')
							.hide();
				}
				if (data[0].notification_time == null || data[0].notification_time +''=='') {
					zsy_detail_winForm.form.findField('notification_time')
							.hide();
				}
				if (data[0].service_time == null || data[0].service_time +''=='') {
					zsy_detail_winForm.form.findField('service_time')
							.hide();
				}
				
				if (data[0].address == null || data[0].address +''=='') {
					             if(data[0].detail_address == null || data[0].detail_address +''==''){
					                 zsy_detail_winForm.form.findField('address').hide();
					        }else{
					                 zsy_detail_winForm.form.findField('address').setValue( data[0].detail_address);
					         }
					        }else{
				                if(data[0].detail_address == null || data[0].detail_address +''==''){
					
					           }else{
					              zsy_detail_winForm.form.findField('address').setValue(data[0].address+data[0].detail_address);
					           }
				           }	
				if (data[0].man_number == null || data[0].man_number +''=='') {
					zsy_detail_winForm.form.findField('man_number')
							.hide();
				}
				if (data[0].woman_number == null || data[0].woman_number +''=='') {
					zsy_detail_winForm.form.findField('woman_number')
							.hide();
				}
				if (data[0].sex == null || data[0].sex +''=='') {
					zsy_detail_winForm.form.findField('sex')
							.hide();
				}
				if (data[0].lawyers_working_age == null || data[0].lawyers_working_age +''=='') {
					zsy_detail_winForm.form.findField('lawyers_working_age')
							.hide();
				}
				
				if (data[0].nolitigatio_service == null || data[0].nolitigatio_service +''=='')
						{
					zsy_detail_winForm.form.findField('nolitigatio_service')
							.hide();
				}
				
				if (data[0].nolitigatio_service_item == null || data[0].nolitigatio_service_item +''=='')
						{
					zsy_detail_winForm.form.findField('nolitigatio_service_item')
							.hide();
				}
			} else {

			}
		},
		failure : function(request) {
			Ext.MessageBox.show({
				title : '操作提示',
				msg : "连接服务器失败",
				buttons : Ext.MessageBox.OK,
				icon : Ext.MessageBox.ERROR
			});
		},
		method : 'post'
	});

	var zsy_detailwindow = Ext.create('Ext.window.Window', {
		title : "订单详情",
		width : 450,
		height : 400,
		// height : 120,
		// plain : true,
		iconCls : "addicon",
		// 不可以随意改变大小
		resizable : true,
		//autoScroll : true,
		// 是否可以拖动
		draggable : true,
		collapsible : true, // 允许缩放条
		closeAction : 'close',
		closable : true,
		// 弹出模态窗体
		modal : 'true',
		buttonAlign : "center",
		bodyStyle : "padding:0 0 0 0",
		items : [ zsy_detail_winForm ]
	});
	zsy_detailwindow.show();
};

// 蛋糕房订单详情页
var dgf_detailform = function(order_id, app_type, service_type,orderTypeName) {

	var dgf_detail_winForm = Ext.create('Ext.form.Panel', {
		frame : true, // frame属性
		// title: 'Form Fields',
		width : 450,
		height : 550,
		bodyPadding : 5,
		// renderTo:"panel21",
		fieldDefaults : {
			labelAlign : 'left',
			labelWidth : 110,
			anchor : '90%'
		},
		items : [
				{
					// 显示文本框，相当于label
					xtype : 'displayfield',
					name : 'displayfield1',
					value : '蛋糕房订单详情'

				},{
			// 服务类型
			xtype : 'textfield',
			name : 'orderTypeName',
			fieldLabel : '服务类型',
			readOnly : true
		},{
			// 蛋糕口味
			xtype : 'textfield',
			name : 'cake_taste',
			fieldLabel : '蛋糕口味',
			readOnly : true
		},{
			// 蛋糕形状
			xtype : 'textfield',
			name : 'cake_shape',
			fieldLabel : '蛋糕形状',
			readOnly : true
		},
		{
					// 蛋糕尺寸
					xtype : 'textfield',
					name : 'cake_size',
					fieldLabel : '蛋糕尺寸',
					readOnly : true
				} ,
				{
					// 祝福对象
					xtype : 'textfield',
					name : 'wish_people',
					fieldLabel : '祝福对象',
					readOnly : true
				} ,
				{
					// 祝福语
					xtype : 'textfield',
					name : 'blessings',
					fieldLabel : '祝福语',
					readOnly : true
				} ,
				{
					// 糕点类型
					xtype : 'textfield',
					name : 'pastry_type',
					fieldLabel : '糕点类型',
					readOnly : true
				} ,
				
				{
					// 糕点重量
					xtype : 'textfield',
					name : 'pastry_weight',
					fieldLabel : '糕点重量',
					readOnly : true
				} ,{
					// 面包类型
					xtype : 'textfield',
					name : 'bread_type',
					fieldLabel : '面包类型',
					readOnly : true
				} ,
				
				{
					// 面包份数
					xtype : 'textfield',
					name : 'bread_unit',
					fieldLabel : '面包份数',
					readOnly : true
				} ,
				
				{
					// 配送地点
					xtype : 'textfield',
					name : 'address',
					fieldLabel : '配送地点',
					readOnly : true
				} ,{
					// 是否送货上门
					xtype : 'textfield',
					name : 'service_to_home',
					fieldLabel : '送货上门',
					readOnly : true
				} , {
					// 配送时间
					xtype : 'textfield',
					name : 'delivery_time',
					fieldLabel : '配送时间',
					readOnly : true
				},{
					// 其他需求
					xtype : 'textfield',
					name : 'demand',
					fieldLabel : '其他需求',
					readOnly : true
				} , ]
	});

	// 向后台获取订单详情数据
	Ext.Ajax.request({
		url : '/order/getOrderDetail',
		params : {
			order_id : order_id,
			app_type : app_type,
			service_type : service_type
		},
		success : function(response) {
			Ext.getBody().unmask();
			var json = Ext.JSON.decode(response.responseText);
			if (json.code == 'OK') {
				var data = json.data;
				dgf_detail_winForm.form.findField('orderTypeName').setValue(orderTypeName);
				dgf_detail_winForm.form.findField('cake_taste')
						.setValue(data[0].cake_taste);
				dgf_detail_winForm.form.findField('cake_shape')
						.setValue(data[0].cake_shape);
				dgf_detail_winForm.form.findField('cake_size')
						.setValue(data[0].cake_size);
				dgf_detail_winForm.form.findField('wish_people')
						.setValue(data[0].wish_people);

				dgf_detail_winForm.form.findField('blessings')
						.setValue(data[0].blessings);
				dgf_detail_winForm.form.findField('pastry_type')
						.setValue(data[0].pastry_type);
				dgf_detail_winForm.form.findField('pastry_weight')
						.setValue(data[0].pastry_weight);
				dgf_detail_winForm.form.findField('bread_type').setValue(
						data[0].bread_type);
				dgf_detail_winForm.form.findField('bread_unit')
						.setValue(data[0].bread_unit);
				dgf_detail_winForm.form.findField('delivery_time').setValue(
						data[0].delivery_time);


                dgf_detail_winForm.form.findField('service_to_home')
						.setValue(data[0].service_to_home);
				dgf_detail_winForm.form.findField('address').setValue(
						data[0].address);
                dgf_detail_winForm.form.findField('demand').setValue(
						data[0].demand);

				if (data[0].cake_taste == null || data[0].cake_taste +''=='')
						{
					dgf_detail_winForm.form.findField('cake_taste')
							.hide();
				}
				if (data[0].cake_shape == null || data[0].cake_shape +''=='')
						 {
					dgf_detail_winForm.form.findField('cake_shape')
							.hide();
				}
				if (data[0].cake_size == null || data[0].cake_size +''=='')
						 {
					dgf_detail_winForm.form.findField('cake_size')
							.hide();
				}
				if (data[0].wish_people == null || data[0].wish_people +''=='') {
					dgf_detail_winForm.form.findField('wish_people')
							.hide();
				}
				if (data[0].blessings == null || data[0].blessings +''==''){
					dgf_detail_winForm.form.findField('blessings')
							.hide();
				}
				if (data[0].pastry_type == null || data[0].pastry_type +''=='') {
					dgf_detail_winForm.form.findField('pastry_type')
							.hide();
				}
				if (data[0].pastry_weight == null || data[0].pastry_weight +''=='') {
					dgf_detail_winForm.form.findField('pastry_weight')
							.hide();
				}
				
				if (data[0].bread_type == null || data[0].bread_type +''=='') {
					dgf_detail_winForm.form.findField('bread_type')
							.hide();
				}
				if (data[0].bread_unit == null || data[0].bread_unit +''=='') {
					dgf_detail_winForm.form.findField('bread_unit')
							.hide();
				}
				if (data[0].delivery_time == null || data[0].delivery_time +''=='') {
					dgf_detail_winForm.form.findField('delivery_time')
							.hide();
				}
				if (data[0].demand == null || data[0].demand +''=='') {
					dgf_detail_winForm.form.findField('demand')
							.hide();
				}
				if (data[0].service_to_home == null || data[0].service_to_home +''=='') {
					dgf_detail_winForm.form.findField('service_to_home')
							.hide();
				}
				if (data[0].address == null || data[0].address +''=='') {
					             if(data[0].detail_address == null || data[0].detail_address +''==''){
					                 dgf_detail_winForm.form.findField('address').hide();
					        }else{
					                 dgf_detail_winForm.form.findField('address').setValue( data[0].detail_address);
					         }
					        }else{
				                if(data[0].detail_address == null || data[0].detail_address +''==''){
					
					           }else{
					              dgf_detail_winForm.form.findField('address').setValue(data[0].address+data[0].detail_address);
					           }
				           }	
			} else {

			}
		},
		failure : function(request) {
			Ext.MessageBox.show({
				title : '操作提示',
				msg : "连接服务器失败",
				buttons : Ext.MessageBox.OK,
				icon : Ext.MessageBox.ERROR
			});
		},
		method : 'post'
	});

	var dgf_detailwindow = Ext.create('Ext.window.Window', {
		title : "订单详情",
		width : 450,
		height : 400,
		// height : 120,
		// plain : true,
		iconCls : "addicon",
		// 不可以随意改变大小
		resizable : true,
		//autoScroll : true,
		// 是否可以拖动
		draggable : true,
		collapsible : true, // 允许缩放条
		closeAction : 'close',
		closable : true,
		// 弹出模态窗体
		modal : 'true',
		buttonAlign : "center",
		bodyStyle : "padding:0 0 0 0",
		items : [ dgf_detail_winForm ]
	});
	dgf_detailwindow.show();
};

//商务馆订单详情页
var swg_detailform = function(order_id, app_type, service_type,orderTypeName) {

	var swg_detail_winForm = Ext.create('Ext.form.Panel', {
		frame : true, // frame属性
		// title: 'Form Fields',
		width : 450,
		height : 550,
		bodyPadding : 5,
		// renderTo:"panel21",
		fieldDefaults : {
			labelAlign : 'left',
			labelWidth : 110,
			anchor : '90%'
		},
		items : [
				{
					// 显示文本框，相当于label
					xtype : 'displayfield',
					name : 'displayfield1',
					value : '商务馆订单详情'

				},{
			// 服务类型
			xtype : 'textfield',
			name : 'orderTypeName',
			fieldLabel : '服务类型',
			readOnly : true
		},{
			// 麻将区
			xtype : 'textfield',
			name : 'mahJong',
			fieldLabel : '麻将区',
			readOnly : true
		},{
			// 扑克区
			xtype : 'textfield',
			name : 'poker',
			fieldLabel : '扑克区',
			readOnly : true
		},{
			// 棋类区
			xtype : 'textfield',
			name : 'chess',
			fieldLabel : '棋类区',
			readOnly : true
		},{
			// 娱乐类型
			xtype : 'textfield',
			name : 'recreation_type',
			fieldLabel : '娱乐类型',
			readOnly : true
		},{
			// 娱乐项目
			xtype : 'textfield',
			name : 'recreation_item',
			fieldLabel : '娱乐项目',
			readOnly : true
		},{
			// 预约人数
			xtype : 'textfield',
			name : 'reservation_people_count',
			fieldLabel : '预约人数',
			readOnly : true
			} ,{
		// 预约时间
		    xtype : 'datetimefield',
			name : 'service_time',
			fieldLabel : '预约时间',
			format : 'Y-m-d',
			readOnly : true
		}, {
		// 就座区域
			xtype : 'textfield',
			name : 'seating_area',
			fieldLabel : '就座区域',
			readOnly : true
				} ,
				{
					// 附加服务
					xtype : 'textfield',
					name : 'extra_service',
					fieldLabel : '附加服务',
					readOnly : true
				},
				{
					//地址
					xtype : 'textfield',
					name : 'address',
					fieldLabel : '预约地址',
					readOnly : true
				} ,
				{
					// 其他需求
					xtype : 'textfield',
					name : 'demand',
					fieldLabel : '其他需求',
					readOnly : true
				}  ]
	});

	// 向后台获取订单详情数据
	Ext.Ajax.request({
		url : '/order/getOrderDetail',
		params : {
			order_id : order_id,
			app_type : app_type,
			service_type : service_type
		},
		success : function(response) {
			Ext.getBody().unmask();
			var json = Ext.JSON.decode(response.responseText);
			if (json.code == 'OK') {
				var data = json.data;
				swg_detail_winForm.form.findField('orderTypeName').setValue(orderTypeName);
				swg_detail_winForm.form.findField('mahJong')
						.setValue(data[0].mahJong +'');
				swg_detail_winForm.form.findField('poker')
						.setValue(data[0].poker +'');
				swg_detail_winForm.form.findField('chess')
						.setValue(data[0].chess +'');
				swg_detail_winForm.form.findField('reservation_people_count')
						.setValue(data[0].reservation_people_count);
				swg_detail_winForm.form.findField('service_time')
						.setValue(data[0].service_time);
				swg_detail_winForm.form.findField('seating_area')
						.setValue(data[0].seating_area+'');
				swg_detail_winForm.form.findField('extra_service')
				.setValue(data[0].extra_service);
				swg_detail_winForm.form.findField('address')
						.setValue(data[0].address);
				swg_detail_winForm.form.findField('demand')
						.setValue(data[0].demand);
				swg_detail_winForm.form.findField('recreation_type')
				.setValue(data[0].recreation_type);
				swg_detail_winForm.form.findField('recreation_item')
				.setValue(data[0].recreation_item);

                if (data[0].recreation_type == null || data[0].recreation_type +''=='')
						{
					swg_detail_winForm.form.findField('recreation_type')
							.hide();
				}
				if (data[0].recreation_item == null || data[0].recreation_item +''=='')
						{
					swg_detail_winForm.form.findField('recreation_item')
							.hide();
				}
				if (data[0].mahJong == null || data[0].mahJong +''=='')
						{
					swg_detail_winForm.form.findField('mahJong')
							.hide();
				}
				if (data[0].poker == null || data[0].poker +''=='')
						{
					swg_detail_winForm.form.findField('poker')
							.hide();
				}
				if (data[0].chess == null || data[0].chess +''=='')
						{
					swg_detail_winForm.form.findField('chess')
							.hide();
				}
				if (data[0].reservation_people_count == null || data[0].reservation_people_count +''=='')
						{
					swg_detail_winForm.form.findField('reservation_people_count')
							.hide();
				}
				if (data[0].service_time == null || data[0].service_time +''=='')
						 {
					swg_detail_winForm.form.findField('service_time')
							.hide();
				}
				if (data[0].seating_area == null || data[0].seating_area +''=='')
						 {
					swg_detail_winForm.form.findField('seating_area')
							.hide();
				}
				
				if (data[0].extra_service == null || data[0].extra_service +''=='') {
					swg_detail_winForm.form.findField('extra_service')
							.hide();
				}
				if (data[0].address == null || data[0].address +''=='') {
					             if(data[0].detail_address == null || data[0].detail_address +''==''){
					                 swg_detail_winForm.form.findField('address').hide();
					        }else{
					                 swg_detail_winForm.form.findField('address').setValue( data[0].detail_address);
					         }
					        }else{
				                if(data[0].detail_address == null || data[0].detail_address +''==''){
					
					           }else{
					              swg_detail_winForm.form.findField('address').setValue(data[0].address+data[0].detail_address);
					           }
				           }
				if (data[0].demand == null || data[0].demand +''=='') {
					swg_detail_winForm.form.findField('demand')
							.hide();
				}
				
				
			} else {

			}
		},
		failure : function(request) {
			Ext.MessageBox.show({
				title : '操作提示',
				msg : "连接服务器失败",
				buttons : Ext.MessageBox.OK,
				icon : Ext.MessageBox.ERROR
			});
		},
		method : 'post'
	});

	var swg_detailwindow = Ext.create('Ext.window.Window', {
		title : "订单详情",
		width : 450,
		height : 400,
		// height : 120,
		// plain : true,
		iconCls : "addicon",
		// 不可以随意改变大小
		resizable : true,
		//autoScroll : true,
		// 是否可以拖动
		draggable : true,
		collapsible : true, // 允许缩放条
		closeAction : 'close',
		closable : true,
		// 弹出模态窗体
		modal : 'true',
		buttonAlign : "center",
		bodyStyle : "padding:0 0 0 0",
		items : [ swg_detail_winForm ]
	});
	swg_detailwindow.show();
};
//金融街订单详情页
var jrj_detailform = function(order_id, app_type, service_type,orderTypeName) {

	var jrj_detail_winForm = Ext.create('Ext.form.Panel', {
		frame : true, // frame属性
		// title: 'Form Fields',
		width : 450,
		height : 550,
		bodyPadding : 5,
		// renderTo:"panel21",
		fieldDefaults : {
			labelAlign : 'left',
			labelWidth : 110,
			anchor : '90%'
		},
		items : [
				{
					// 显示文本框，相当于label
					xtype : 'displayfield',
					name : 'displayfield1',
					value : '金融街订单详情'

				},{
			   // 服务类型
			     xtype : 'textfield',
			     name : 'orderTypeName',
			     fieldLabel : '服务类型',
			     readOnly : true
		         },{
					//服务项目
					xtype : 'textfield',
					name : 'service_items',
					fieldLabel : '服务项目',
					readOnly : true
				} ,{
					//缴费期限
					xtype : 'textfield',
					name : 'insHeadline',
					fieldLabel : '缴费期限',
					readOnly : true
				} ,{
					//保险金额
					xtype : 'textfield',
					name : 'insAmount',
					fieldLabel : '保险金额',
					readOnly : true
				} ,{
					//数量
					xtype : 'textfield',
					name : 'amount',
					fieldLabel : '数量',
					readOnly : true
				} ,{
					//房产
					xtype : 'textfield',
					name : 'creditHouse',
					fieldLabel : '房产',
					readOnly : true
				},{
					//汽车
					xtype : 'textfield',
					name : 'creditCar',
					fieldLabel : '汽车',
					readOnly : true
				 },{
					// 预约时间
					xtype : 'datetimefield',
					name : 'service_time',
					fieldLabel : '预约时间',
					format : 'Y-m-d',
					readOnly : true
				}, 
				{
					//地址
					xtype : 'textfield',
					name : 'address',
					fieldLabel : '预约地址',
					readOnly : true
				} ]
	});

	// 向后台获取订单详情数据
	Ext.Ajax.request({
		url : '/order/getOrderDetail',
		params : {
			order_id : order_id,
			app_type : app_type,
			service_type : service_type
		},
		success : function(response) {
			Ext.getBody().unmask();
			var json = Ext.JSON.decode(response.responseText);
			if (json.code == 'OK') {
				var data = json.data;
				jrj_detail_winForm.form.findField('orderTypeName').setValue(orderTypeName);
				jrj_detail_winForm.form.findField('service_items')
						.setValue(data[0].service_items);
				jrj_detail_winForm.form.findField('insHeadline')
						.setValue(data[0].insHeadline+'年');
				jrj_detail_winForm.form.findField('insAmount')
						.setValue(data[0].insAmount);
				jrj_detail_winForm.form.findField('amount')
						.setValue(data[0].amount);
				jrj_detail_winForm.form.findField('creditHouse')
						.setValue(data[0].creditHouse+'');
				jrj_detail_winForm.form.findField('creditCar')
						.setValue(data[0].creditCar+'');
				jrj_detail_winForm.form.findField('address')
						.setValue(data[0].address);
				jrj_detail_winForm.form.findField('service_time')
						.setValue(data[0].service_time);
				

                if (data[0].service_items == null || data[0].service_items +''=='')
						{
					jrj_detail_winForm.form.findField('service_items')
							.hide();
				}
				if (data[0].insHeadline == null || data[0].insHeadline +''=='')
						{
					jrj_detail_winForm.form.findField('insHeadline')
							.hide();
				}
				if (data[0].insAmount == null || data[0].insAmount +''=='')
						{
					jrj_detail_winForm.form.findField('insAmount')
							.hide();
				}
				if (data[0].amount == null || data[0].amount +''=='')
						{
					jrj_detail_winForm.form.findField('amount')
							.hide();
				}
				if (data[0].creditHouse == null || data[0].creditHouse +''=='')
						 {
					jrj_detail_winForm.form.findField('creditHouse')
							.hide();
				}
				if (data[0].creditCar == null || data[0].creditCar +''=='')
						 {
					jrj_detail_winForm.form.findField('creditCar')
							.hide();
				}
				
				if (data[0].address == null || data[0].address +''=='') {
					             if(data[0].detail_address == null || data[0].detail_address +''==''){
					                 jrj_detail_winForm.form.findField('address').hide();
					        }else{
					                 jrj_detail_winForm.form.findField('address').setValue( data[0].detail_address);
					         }
					        }else{
				                if(data[0].detail_address == null || data[0].detail_address +''==''){
					
					           }else{
					              jrj_detail_winForm.form.findField('address').setValue(data[0].address+data[0].detail_address);
					           }
				           }
				if (data[0].service_time == null || data[0].service_time +''=='') {
					jrj_detail_winForm.form.findField('service_time')
							.hide();
				}
				
				
			} else {

			}
		},
		failure : function(request) {
			Ext.MessageBox.show({
				title : '操作提示',
				msg : "连接服务器失败",
				buttons : Ext.MessageBox.OK,
				icon : Ext.MessageBox.ERROR
			});
		},
		method : 'post'
	});

	var jrj_detailwindow = Ext.create('Ext.window.Window', {
		title : "订单详情",
		width : 450,
		height : 400,
		// height : 120,
		// plain : true,
		iconCls : "addicon",
		// 不可以随意改变大小
		resizable : true,
		//autoScroll : true,
		// 是否可以拖动
		draggable : true,
		collapsible : true, // 允许缩放条
		closeAction : 'close',
		closable : true,
		// 弹出模态窗体
		modal : 'true',
		buttonAlign : "center",
		bodyStyle : "padding:0 0 0 0",
		items : [ jrj_detail_winForm ]
	});
	jrj_detailwindow.show();
};

//金融街订单详情页
var zdf_detailform = function(order_id, app_type, service_type,orderTypeName) {

	var zdf_detail_winForm = Ext.create('Ext.form.Panel', {
		frame : true, // frame属性
		// title: 'Form Fields',
		width : 450,
		height : 550,
		bodyPadding : 5,
		// renderTo:"panel21",
		fieldDefaults : {
			labelAlign : 'left',
			labelWidth : 110,
			anchor : '90%'
		},
		items : [
				{
					// 显示文本框，相当于label
					xtype : 'displayfield',
					name : 'displayfield1',
					value : '钟点房订单详情'

				},{
			// 服务类型
			xtype : 'textfield',
			name : 'orderTypeName',
			fieldLabel : '服务类型',
			readOnly : true
		},{
					//房间类型
					xtype : 'textfield',
					name : 'roomType',
					fieldLabel : '房间类型',
					readOnly : true
				} ,{
					//房间数量
					xtype : 'textfield',
					name : 'quantity',
					fieldLabel : '房间数量',
					readOnly : true
				} ,{
					//住房时长
					xtype : 'textfield',
					name : 'reserveTime',
					fieldLabel : '住房时长',
					readOnly : true
				} ,{
					//费用预算
					xtype : 'textfield',
					name : 'budget',
					fieldLabel : '费用预算',
					readOnly : true
				} ,{
					// 入住时间
					xtype : 'datetimefield',
					name : 'checkInTime',
					fieldLabel : '入住时间',
					format : 'Y-m-d',
					readOnly : true
				}, 
				{
					//地址
					xtype : 'textfield',
					name : 'address',
					fieldLabel : '预约地址',
					readOnly : true
				}, 
				{
					//其他需求
					xtype : 'textfield',
					name : 'demand',
					fieldLabel : '其他需求',
					readOnly : true
				}  ]
	});

	// 向后台获取订单详情数据
	Ext.Ajax.request({
		url : '/order/getOrderDetail',
		params : {
			order_id : order_id,
			app_type : app_type,
			service_type : service_type
		},
		success : function(response) {
			Ext.getBody().unmask();
			var json = Ext.JSON.decode(response.responseText);
			if (json.code == 'OK') {
				var data = json.data;
				zdf_detail_winForm.form.findField('orderTypeName').setValue(orderTypeName);
				zdf_detail_winForm.form.findField('roomType')
						.setValue(data[0].roomType);
				zdf_detail_winForm.form.findField('quantity')
						.setValue(data[0].quantity+'间');
				zdf_detail_winForm.form.findField('reserveTime')
						.setValue(data[0].reserveTime+'小时');
				zdf_detail_winForm.form.findField('budget')
						.setValue(data[0].budget);
				zdf_detail_winForm.form.findField('checkInTime')
						.setValue(data[0].checkInTime+'');
				zdf_detail_winForm.form.findField('demand')
						.setValue(data[0].demand+'');
				zdf_detail_winForm.form.findField('address')
						.setValue(data[0].address);
				

                if (data[0].roomType == null || data[0].roomType +''=='')
						{
					zdf_detail_winForm.form.findField('roomType')
							.hide();
				}
				if (data[0].quantity == null || data[0].quantity +''=='')
						{
					zdf_detail_winForm.form.findField('quantity')
							.hide();
				}
				if (data[0].reserveTime == null || data[0].reserveTime +''=='')
						{
					zdf_detail_winForm.form.findField('reserveTime')
							.hide();
				}
				if (data[0].budget == null || data[0].budget +''=='')
						{
					zdf_detail_winForm.form.findField('budget')
							.hide();
				}
				if (data[0].checkInTime == null || data[0].checkInTime +''=='')
						 {
					zdf_detail_winForm.form.findField('checkInTime')
							.hide();
				}
				if (data[0].address == null || data[0].address +''=='') {
					             if(data[0].detail_address == null || data[0].detail_address +''==''){
					                 zdf_detail_winForm.form.findField('address').hide();
					        }else{
					                 zdf_detail_winForm.form.findField('address').setValue( data[0].detail_address);
					         }
					        }else{
				                if(data[0].detail_address == null || data[0].detail_address +''==''){
					
					           }else{
					              zdf_detail_winForm.form.findField('address').setValue(data[0].address+data[0].detail_address);
					           }
				           }
				if (data[0].demand == null || data[0].demand +''=='')
						 {
					zdf_detail_winForm.form.findField('demand')
							.hide();
				}
				
				
				
			} else {

			}
		},
		failure : function(request) {
			Ext.MessageBox.show({
				title : '操作提示',
				msg : "连接服务器失败",
				buttons : Ext.MessageBox.OK,
				icon : Ext.MessageBox.ERROR
			});
		},
		method : 'post'
	});

	var zdf_detailwindow = Ext.create('Ext.window.Window', {
		title : "订单详情",
		width : 450,
		height : 400,
		// height : 120,
		// plain : true,
		iconCls : "addicon",
		// 不可以随意改变大小
		resizable : true,
		//autoScroll : true,
		// 是否可以拖动
		draggable : true,
		collapsible : true, // 允许缩放条
		closeAction : 'close',
		closable : true,
		// 弹出模态窗体
		modal : 'true',
		buttonAlign : "center",
		bodyStyle : "padding:0 0 0 0",
		items : [ zdf_detail_winForm ]
	});
	zdf_detailwindow.show();
};

//个性服务订单详情页
var gxfw_detailform = function(order_id, app_type, service_type,orderTypeName) {

	var gxfw_detail_winForm = Ext.create('Ext.form.Panel', {
		frame : true, // frame属性
		// title: 'Form Fields',
		width : 450,
		height : 550,
		bodyPadding : 5,
		// renderTo:"panel21",
		fieldDefaults : {
			labelAlign : 'left',
			labelWidth : 110,
			anchor : '90%'
		},
		items : [
				{
					// 显示文本框，相当于label
					xtype : 'displayfield',
					name : 'displayfield1',
					value : '个性服务订单详情'

				},{
					//需求
					xtype : 'textfield',
					name : 'title',
					fieldLabel : '需求',
					readOnly : true
				} ,{
					// 时间
					xtype : 'datetimefield',
					name : 'service_time',
					fieldLabel : '预约时间',
					format : 'Y-m-d',
					readOnly : true
				}, 
				{
					//地址
					xtype : 'textfield',
					name : 'address',
					fieldLabel : '预约地址',
					readOnly : true
				}, 
				{
					//其他需求
					xtype : 'textfield',
					name : 'demand',
					fieldLabel : '其他需求',
					readOnly : true
				}]
	});

	// 向后台获取订单详情数据
	Ext.Ajax.request({
		url : '/order/getOrderDetail',
		params : {
			order_id : order_id,
			app_type : app_type,
			service_type : service_type
		},
		success : function(response) {
			Ext.getBody().unmask();
			var json = Ext.JSON.decode(response.responseText);
			if (json.code == 'OK') {
				var data = json.data;
				gxfw_detail_winForm.form.findField('orderTypeName').setValue(orderTypeName);
				gxfw_detail_winForm.form.findField('title')
						.setValue(data[0].title);
				gxfw_detail_winForm.form.findField('service_time')
						.setValue(data[0].service_time);
				gxfw_detail_winForm.form.findField('address')
						.setValue(data[0].address);
				gxfw_detail_winForm.form.findField('demand')
						.setValue(data[0].demand);
				

                if (data[0].title == null || data[0].title +''=='')
						{
					gxfw_detail_winForm.form.findField('title')
							.hide();
				}
				if (data[0].service_time == null || data[0].service_time +''=='')
						{
					gxfw_detail_winForm.form.findField('service_time')
							.hide();
				}
				if (data[0].address == null || data[0].address +''=='') {
					             if(data[0].detail_address == null || data[0].detail_address +''==''){
					                 gxfw_detail_winForm.form.findField('address').hide();
					        }else{
					                 gxfw_detail_winForm.form.findField('address').setValue( data[0].detail_address);
					         }
					        }else{
				                if(data[0].detail_address == null || data[0].detail_address +''==''){
					
					           }else{
					              gxfw_detail_winForm.form.findField('address').setValue(data[0].address+data[0].detail_address);
					           }
				           }
				if (data[0].demand == null || data[0].demand +''=='')
						{
					gxfw_detail_winForm.form.findField('demand')
							.hide();
				}
				
				
				
				
			} else {

			}
		},
		failure : function(request) {
			Ext.MessageBox.show({
				title : '操作提示',
				msg : "连接服务器失败",
				buttons : Ext.MessageBox.OK,
				icon : Ext.MessageBox.ERROR
			});
		},
		method : 'post'
	});

	var gxfw_detailwindow = Ext.create('Ext.window.Window', {
		title : "订单详情",
		width : 450,
		height : 400,
		// height : 120,
		// plain : true,
		iconCls : "addicon",
		// 不可以随意改变大小
		resizable : true,
		//autoScroll : true,
		// 是否可以拖动
		draggable : true,
		collapsible : true, // 允许缩放条
		closeAction : 'close',
		closable : true,
		// 弹出模态窗体
		modal : 'true',
		buttonAlign : "center",
		bodyStyle : "padding:0 0 0 0",
		items : [ gxfw_detail_winForm ]
	});
	gxfw_detailwindow.show();
};
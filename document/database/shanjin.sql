/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50623
Source Host           : localhost:3306
Source Database       : shanjin20150828

Target Server Type    : MYSQL
Target Server Version : 50623
File Encoding         : 65001

Date: 2015-08-28 19:57:41
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for ams_order_info
-- ----------------------------
DROP TABLE IF EXISTS `ams_order_info`;
CREATE TABLE `ams_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '记录在平台中的唯一标识符',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `gsdb_service_type` smallint(2) DEFAULT '0' COMMENT '工商代办服务类型',
  `cwdb_financial_type` smallint(2) DEFAULT '0' COMMENT '财务代办服务类型',
  `visa_type` smallint(2) DEFAULT '0' COMMENT '签证类型',
  `visa_country` varchar(10) DEFAULT NULL COMMENT '签证国家',
  `visa_service_demand` varchar(20) DEFAULT NULL COMMENT '签证服务需求 1,2,3的存储形式',
  `reservation_time` datetime DEFAULT NULL COMMENT '预约时间',
  `demand` varchar(200) DEFAULT NULL COMMENT '其他需求',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='艾秘书订单信息表';

-- ----------------------------
-- Table structure for android_app_push_config
-- ----------------------------
DROP TABLE IF EXISTS `android_app_push_config`;
CREATE TABLE `android_app_push_config` (
  `app_type` varchar(20) NOT NULL,
  `app_id` varchar(30) DEFAULT NULL,
  `app_key` varchar(30) DEFAULT NULL,
  `app_secret` varchar(30) DEFAULT NULL,
  `master_secret` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`app_type`),
  UNIQUE KEY `app_type` (`app_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='安卓应用推送设置';

-- ----------------------------
-- Table structure for app_auth_info
-- ----------------------------
DROP TABLE IF EXISTS `app_auth_info`;
CREATE TABLE `app_auth_info` (
  `app_type` varchar(30) DEFAULT NULL COMMENT '应用名称',
  `action_type` varchar(10) DEFAULT NULL COMMENT '判断是用户版还是商户版',
  `device_id` varchar(40) DEFAULT NULL COMMENT '设备唯一标示',
  `join_time` datetime DEFAULT NULL COMMENT '添加时间',
  `is_disable` int(1) DEFAULT '0' COMMENT '是否禁用'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设备验证信息表';

-- ----------------------------
-- Table structure for app_config
-- ----------------------------
DROP TABLE IF EXISTS `app_config`;
CREATE TABLE `app_config` (
  `config_type` varchar(10) DEFAULT NULL COMMENT '配置项',
  `config_value` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='应用配置表';

-- ----------------------------
-- Table structure for app_info
-- ----------------------------
DROP TABLE IF EXISTS `app_info`;
CREATE TABLE `app_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT,
  `app_name` varchar(10) DEFAULT NULL COMMENT '应用名称',
  `app_type` varchar(30) DEFAULT NULL COMMENT '应用类型',
  `app_remark` varchar(100) DEFAULT NULL COMMENT '应用备注',
  `icon_path` varchar(200) DEFAULT NULL COMMENT '图标路径',
  `is_del` int(1) DEFAULT '0' COMMENT '是否被删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for app_update
-- ----------------------------
DROP TABLE IF EXISTS `app_update`;
CREATE TABLE `app_update` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `package_name` varchar(40) NOT NULL COMMENT '应用包名',
  `package_type` int(1) NOT NULL DEFAULT '1' COMMENT '1:商户版；2：用户版',
  `update_type` int(1) NOT NULL DEFAULT '1' COMMENT '1:提示升级；2：强制升级',
  `app_type` varchar(30) NOT NULL,
  `version` varchar(10) NOT NULL COMMENT '版本号',
  `download_url` varchar(200) NOT NULL COMMENT '下载路径',
  `channel` varchar(10) DEFAULT NULL COMMENT '渠道（待用）',
  `is_del` int(1) DEFAULT '0' COMMENT '0:未删除；1：删除',
  `publish_status` int(1) DEFAULT '0' COMMENT '0:撤销；1：发布',
  `detail` varchar(200) DEFAULT NULL COMMENT '升级描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for area
-- ----------------------------
DROP TABLE IF EXISTS `area`;
CREATE TABLE `area` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `area` varchar(64) NOT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `index_str` varchar(2) DEFAULT NULL,
  `is_leaves` int(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `city` (`area`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for authority_group_info
-- ----------------------------
DROP TABLE IF EXISTS `authority_group_info`;
CREATE TABLE `authority_group_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT,
  `groupName` varchar(100) NOT NULL,
  `createTime` datetime DEFAULT NULL,
  `createName` varchar(100) DEFAULT NULL,
  `updateTime` datetime DEFAULT NULL,
  `updateName` varchar(100) DEFAULT NULL,
  `disabled` int(1) DEFAULT '0',
  `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for authority_group_role
-- ----------------------------
DROP TABLE IF EXISTS `authority_group_role`;
CREATE TABLE `authority_group_role` (
  `groupId` bigint(18) NOT NULL,
  `roleId` bigint(18) NOT NULL,
  PRIMARY KEY (`groupId`,`roleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for authority_resource_info
-- ----------------------------
DROP TABLE IF EXISTS `authority_resource_info`;
CREATE TABLE `authority_resource_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT,
  `resName` varchar(500) NOT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `disabled` int(1) DEFAULT '0',
  `linkPath` varchar(100) DEFAULT NULL,
  `type` int(1) unsigned zerofill DEFAULT '0' COMMENT '0：展示，1:查询，2：新增，3：更新，4：删除，5：上传，6：下载，7：导出',
  `fatherId` bigint(18) DEFAULT NULL COMMENT '父资源ID（0或null即为根节点）',
  `rank` int(2) DEFAULT '1' COMMENT '同级资源排序字段',
  `isLeaf` int(1) DEFAULT '0' COMMENT '是否是叶子节点（0：不是，1：是），默认不是',
  `isCommon` int(1) DEFAULT '0' COMMENT '是否公共资源（0：否，1：是）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for authority_role_info
-- ----------------------------
DROP TABLE IF EXISTS `authority_role_info`;
CREATE TABLE `authority_role_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT,
  `roleName` varchar(100) NOT NULL,
  `createTime` datetime DEFAULT NULL,
  `createName` varchar(100) DEFAULT NULL,
  `updateTime` datetime DEFAULT NULL,
  `updateName` varchar(100) DEFAULT NULL,
  `disabled` int(1) DEFAULT '0',
  `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for authority_role_resource
-- ----------------------------
DROP TABLE IF EXISTS `authority_role_resource`;
CREATE TABLE `authority_role_resource` (
  `roleId` bigint(18) NOT NULL,
  `resId` bigint(18) NOT NULL,
  PRIMARY KEY (`roleId`,`resId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for authority_user_app
-- ----------------------------
DROP TABLE IF EXISTS `authority_user_app`;
CREATE TABLE `authority_user_app` (
  `userId` bigint(18) NOT NULL,
  `appId` bigint(18) NOT NULL,
  PRIMARY KEY (`userId`,`appId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for authority_user_group
-- ----------------------------
DROP TABLE IF EXISTS `authority_user_group`;
CREATE TABLE `authority_user_group` (
  `userId` bigint(18) NOT NULL,
  `groupId` bigint(18) NOT NULL,
  PRIMARY KEY (`userId`,`groupId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for authority_user_info
-- ----------------------------
DROP TABLE IF EXISTS `authority_user_info`;
CREATE TABLE `authority_user_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT,
  `userName` varchar(100) NOT NULL,
  `realName` varchar(100) DEFAULT NULL,
  `psw` varchar(200) DEFAULT NULL,
  `pswHints` varchar(200) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `address` varchar(100) DEFAULT NULL COMMENT '联系地址',
  `createTime` datetime DEFAULT NULL,
  `createName` varchar(100) DEFAULT NULL,
  `updateTime` datetime DEFAULT NULL,
  `updateName` varchar(100) DEFAULT NULL,
  `disabled` int(1) DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `isAdmin` int(1) unsigned zerofill DEFAULT '0',
  `userType` int(1) DEFAULT '1' COMMENT '1.公司员工；2.省代理；3.市代理；4.项目代理',
  `province` bigint(20) DEFAULT NULL,
  `city` bigint(20) DEFAULT NULL,
  `balance` int(20) DEFAULT '0' COMMENT '余额',
  `accountName` varchar(100) DEFAULT NULL,
  `accountBank` varchar(100) DEFAULT NULL,
  `accountNumber` varchar(100) DEFAULT NULL,
  `isDel` int(1) DEFAULT '0',
  `provinceDesc` varchar(50) DEFAULT NULL,
  `cityDesc` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for authority_user_role
-- ----------------------------
DROP TABLE IF EXISTS `authority_user_role`;
CREATE TABLE `authority_user_role` (
  `userId` bigint(18) NOT NULL,
  `roleId` bigint(18) NOT NULL,
  PRIMARY KEY (`userId`,`roleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for cbt_beautify_service_info
-- ----------------------------
DROP TABLE IF EXISTS `cbt_beautify_service_info`;
CREATE TABLE `cbt_beautify_service_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `car_park_address` varchar(100) DEFAULT NULL COMMENT '车辆停放地址',
  `hope_service_time` datetime DEFAULT NULL COMMENT '希望服务的时间',
  `beautify_service_items` varchar(100) DEFAULT NULL COMMENT '美容服务项目 11,12,13的形式存储 关联车百通美容服务项目字典表的id',
  `service_site` smallint(1) DEFAULT '2' COMMENT '服务地点 1-上门美容 2-到店美容',
  `beautify_demand` varchar(200) DEFAULT NULL COMMENT '美容需求',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='车百通美容服务内容表';

-- ----------------------------
-- Table structure for cbt_park_service_info
-- ----------------------------
DROP TABLE IF EXISTS `cbt_park_service_info`;
CREATE TABLE `cbt_park_service_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `car_no` varchar(10) DEFAULT NULL COMMENT '车牌号',
  `car_park_address` varchar(100) DEFAULT NULL COMMENT '车辆停放地址',
  `hope_service_time` datetime DEFAULT NULL COMMENT '希望服务的时间',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='车百通停车服务内容表';

-- ----------------------------
-- Table structure for cbt_repair_service_info
-- ----------------------------
DROP TABLE IF EXISTS `cbt_repair_service_info`;
CREATE TABLE `cbt_repair_service_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `longitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `repair_place` varchar(100) DEFAULT NULL COMMENT '维修部位 1,2,3的形式存储 关联汽车维修服务项目字典表的id',
  `hope_service_time` datetime DEFAULT NULL COMMENT '希望服务的时间',
  `repair_demand` varchar(200) DEFAULT NULL COMMENT '维修需求',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='车百通维修服务内容表';

-- ----------------------------
-- Table structure for cbt_upkeep_service_info
-- ----------------------------
DROP TABLE IF EXISTS `cbt_upkeep_service_info`;
CREATE TABLE `cbt_upkeep_service_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `upkeep_vehicle` varchar(20) DEFAULT NULL COMMENT '保养车辆',
  `upkeep_content` smallint(2) DEFAULT '2' COMMENT '保养内容 1-小保养 2-大保养',
  `hope_service_time` datetime DEFAULT NULL COMMENT '希望服务的时间',
  `upkeep_demand` varchar(200) DEFAULT NULL COMMENT '保养需求',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='车百通保养服务内容表';

-- ----------------------------
-- Table structure for cbt_wash_service_info
-- ----------------------------
DROP TABLE IF EXISTS `cbt_wash_service_info`;
CREATE TABLE `cbt_wash_service_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `car_park_address` varchar(100) DEFAULT NULL COMMENT '车辆停放地址',
  `hope_service_time` datetime DEFAULT NULL COMMENT '希望服务的时间',
  `service_site` smallint(1) DEFAULT '2' COMMENT '服务地点 1-上门洗车 2-到店洗车',
  `inside_wash` smallint(1) DEFAULT '0' COMMENT '内饰清洗 0-不需要 1-需要',
  `wash_demand` varchar(200) DEFAULT NULL COMMENT '清洗需求',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='车百通清洗服务内容表';

-- ----------------------------
-- Table structure for data_version
-- ----------------------------
DROP TABLE IF EXISTS `data_version`;
CREATE TABLE `data_version` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT,
  `data_type` varchar(100) NOT NULL,
  `version` int(8) DEFAULT NULL,
  `is_del` smallint(1) DEFAULT '0',
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='数据版本表';

-- ----------------------------
-- Table structure for dgf_order_info
-- ----------------------------
DROP TABLE IF EXISTS `dgf_order_info`;
CREATE TABLE `dgf_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `cake_taste` varchar(200) DEFAULT '0' COMMENT '蛋糕口味',
  `cake_shape` varchar(200) DEFAULT '0' COMMENT '蛋糕形状',
  `cake_size` varchar(20) DEFAULT NULL COMMENT '蛋糕尺寸',
  `wish_people` varchar(50) DEFAULT NULL COMMENT '祝福对象',
  `blessings` varchar(200) DEFAULT NULL COMMENT '祝福语',
  `pastry_type` varchar(40) DEFAULT '0' COMMENT '糕点类型',
  `pastry_weight` varchar(20) DEFAULT NULL COMMENT '糕点重量',
  `bread_type` varchar(40) DEFAULT '0' COMMENT '面包类型',
  `bread_unit` int(5) DEFAULT NULL COMMENT '面包份数',
  `delivery_time` varchar(50) DEFAULT NULL COMMENT '配送时间',
  `is_visit` smallint(1) DEFAULT NULL COMMENT '是否送货上门',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `address` varchar(100) DEFAULT NULL COMMENT '配送地点',
  `demand` varchar(200) DEFAULT NULL COMMENT '其他需求',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='蛋糕房订单信息表';

-- ----------------------------
-- Table structure for dictionary
-- ----------------------------
DROP TABLE IF EXISTS `dictionary`;
CREATE TABLE `dictionary` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `dict_type` varchar(80) NOT NULL COMMENT '字典类型，用于区分记录',
  `dict_key` varchar(80) NOT NULL COMMENT '字典记录主键',
  `dict_value` varchar(80) NOT NULL COMMENT '字典记录值',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `parent_dict_id` int(10) DEFAULT NULL COMMENT '父级字典ID',
  `is_del` smallint(1) NOT NULL DEFAULT '0' COMMENT '是否被删除 0-未删除 1-已删除',
  `dict_level` varchar(20) DEFAULT NULL COMMENT '字典层级 app:应用级别；service:服务级别',
  `dict_domain` varchar(20) DEFAULT NULL COMMENT '字典域，表述字典项属于那个域：如service_type值，或appType值',
  `is_leaves` smallint(1) DEFAULT '1' COMMENT '是否叶子节点 0-否 1-是',
  `sort` int(5) DEFAULT NULL COMMENT '排序字段',
  PRIMARY KEY (`id`),
  UNIQUE KEY `字典类型，字典key` (`dict_type`,`dict_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='字典表';

-- ----------------------------
-- Table structure for dictionary_attachment
-- ----------------------------
DROP TABLE IF EXISTS `dictionary_attachment`;
CREATE TABLE `dictionary_attachment` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `dictionary_id` bigint(18) unsigned NOT NULL COMMENT '字典表主键ID',
  `attachment_type` smallint(2) NOT NULL DEFAULT '1' COMMENT '附件类型 1-图片 2-语音',
  `attachment_style` varchar(20) NOT NULL DEFAULT 'image' COMMENT '附件样式 image-默认（一个字典对应的附件只有一种样式的时候） selected-选中 unSelected-未选中',
  `path` varchar(200) NOT NULL COMMENT '附件路径',
  `join_time` datetime NOT NULL COMMENT '附件添加时间',
  `is_del` smallint(1) NOT NULL DEFAULT '0' COMMENT '是否被删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `字典表主键ID` (`dictionary_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='字典附件表';

-- ----------------------------
-- Table structure for evaluation
-- ----------------------------
DROP TABLE IF EXISTS `evaluation`;
CREATE TABLE `evaluation` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `user_id` bigint(18) NOT NULL COMMENT '用户ID',
  `merchant_id` bigint(18) NOT NULL COMMENT '商户ID',
  `service_type_id` int(10) NOT NULL COMMENT '服务类型ID',
  `attitude_evaluation` smallint(1) DEFAULT '0' COMMENT '服务态度评价 范围1-5',
  `quality_evaluation` smallint(1) DEFAULT '0' COMMENT '服务质量评价 范围1-5',
  `speed_evaluation` smallint(1) DEFAULT '0' COMMENT '服务速度评价 范围1-5',
  `text_evaluation` varchar(255) DEFAULT NULL COMMENT '文字评价',
  `join_time` datetime NOT NULL COMMENT '创建时间',
  `app_type` varchar(20) NOT NULL COMMENT '应用类型',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE,
  KEY `用户ID` (`user_id`) USING BTREE,
  KEY `商户ID` (`merchant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单评价表';

-- ----------------------------
-- Table structure for feedback
-- ----------------------------
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(18) NOT NULL COMMENT '反馈的用户ID',
  `feedback_time` datetime NOT NULL COMMENT '反馈时间',
  `content` varchar(600) NOT NULL COMMENT '字典记录值',
  `is_del` smallint(1) NOT NULL DEFAULT '0' COMMENT '是否被删除 0-未删除 1-已删除',
  `app_type` varchar(20) NOT NULL COMMENT '应用程序类型',
  PRIMARY KEY (`id`),
  KEY `用户ID` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='反馈信息表';

-- ----------------------------
-- Table structure for fyb_buyhouse_order_info
-- ----------------------------
DROP TABLE IF EXISTS `fyb_buyhouse_order_info`;
CREATE TABLE `fyb_buyhouse_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `house_type` smallint(2) DEFAULT '0' COMMENT '房屋类型。1新房，2二手房。',
  `decoration_type` smallint(2) DEFAULT '0' COMMENT '装修状况。1毛坯，2简装，3中装，4精装',
  `house_price` varchar(20) DEFAULT '0.00' COMMENT '房价',
  `house_area` varchar(20) DEFAULT '0.00' COMMENT '房屋面积',
  `room_number` smallint(3) DEFAULT '0' COMMENT '房间数',
  `parlour_number` smallint(3) DEFAULT NULL COMMENT '客厅数',
  `toilet_number` smallint(3) DEFAULT NULL COMMENT '卫生间数',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `service_time` datetime DEFAULT NULL COMMENT '服务时间',
  `address` varchar(200) DEFAULT NULL COMMENT '地址',
  `demand` varchar(200) DEFAULT NULL COMMENT '补充说明',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='房源宝买房订单信息表';

-- ----------------------------
-- Table structure for fyb_renthouse_order_info
-- ----------------------------
DROP TABLE IF EXISTS `fyb_renthouse_order_info`;
CREATE TABLE `fyb_renthouse_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `rent_type` smallint(2) DEFAULT '0' COMMENT '出租类型。1出租房，2商业楼，3旺铺商铺，4厂房仓库',
  `rent_way` smallint(2) DEFAULT '0' COMMENT '出租方式。1整租，2合租',
  `house_type` smallint(2) DEFAULT '0' COMMENT '房屋类型。1住宅，2公寓，3别墅，4四合院',
  `jointRent_type` smallint(2) DEFAULT '0' COMMENT '合租类型。1单间，2床位',
  `decoration_type` smallint(2) DEFAULT '0' COMMENT '装修状况。1毛坯，2简装，3中装，4精装',
  `room_number` smallint(2) DEFAULT '0' COMMENT '房间数',
  `parlour_number` smallint(2) DEFAULT '0' COMMENT '客厅数',
  `toilet_number` smallint(2) DEFAULT '0' COMMENT '卫生间数',
  `floor_number` varchar(100) DEFAULT '0' COMMENT '楼层',
  `house_area` varchar(15) DEFAULT '0.00' COMMENT '房屋面积',
  `rent_money` varchar(30) DEFAULT '0.00' COMMENT ' 租金',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `service_time` datetime DEFAULT NULL COMMENT '服务时间',
  `address` varchar(200) DEFAULT NULL COMMENT '地址',
  `demand` varchar(255) DEFAULT NULL COMMENT '需求',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='房源宝租房信息表';

-- ----------------------------
-- Table structure for hyt_order_info
-- ----------------------------
DROP TABLE IF EXISTS `hyt_order_info`;
CREATE TABLE `hyt_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `commodity_type` varchar(50) DEFAULT NULL COMMENT '货物类型，支持多选',
  `start_address` varchar(255) DEFAULT NULL COMMENT '始发地，应用于物流服务类型',
  `destination` varchar(255) DEFAULT NULL COMMENT '目的地，应用于物流服务类型',
  `distance` double(13,2) DEFAULT NULL COMMENT '距离',
  `weight` varchar(40) DEFAULT NULL COMMENT '重量范围值',
  `volume` varchar(40) DEFAULT NULL COMMENT '体积范围值',
  `months` varchar(100) DEFAULT NULL COMMENT '仓储月数',
  `service_time` varchar(40) DEFAULT NULL COMMENT '服务时间',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `address` varchar(200) DEFAULT '' COMMENT '地址',
  `demand` varchar(255) DEFAULT NULL COMMENT '特殊要求',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='货运通订单信息表';

-- ----------------------------
-- Table structure for hz_order_info
-- ----------------------------
DROP TABLE IF EXISTS `hz_order_info`;
CREATE TABLE `hz_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `service_time` datetime DEFAULT NULL COMMENT '服务时间',
  `service_item` varchar(40) DEFAULT NULL COMMENT '服务项目 1,2,3的存储形式',
  `service_way` smallint(2) DEFAULT '0' COMMENT '服务方式。1上门服务。2不上门服务',
  `address` varchar(200) DEFAULT '' COMMENT '地址',
  `demand` varchar(200) DEFAULT '' COMMENT '需求说明',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `sex` smallint(1) DEFAULT NULL COMMENT '性别（0保密  1男  2女）',
  `height` smallint(3) DEFAULT NULL COMMENT '身高',
  `age` smallint(3) DEFAULT NULL COMMENT '年龄',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='红妆订单信息表';

-- ----------------------------
-- Table structure for innodb_monitor
-- ----------------------------
DROP TABLE IF EXISTS `innodb_monitor`;
CREATE TABLE `innodb_monitor` (
  `a` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for jrj_order_info
-- ----------------------------
DROP TABLE IF EXISTS `jrj_order_info`;
CREATE TABLE `jrj_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '记录在平台中的唯一标识符',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `insurance_type` smallint(2) DEFAULT '0' COMMENT '保险类型',
  `financial_type` smallint(2) DEFAULT '0' COMMENT '理财类型',
  `pawn_type` smallint(2) DEFAULT '0' COMMENT '典当类型',
  `expect_money` smallint(2) DEFAULT '0' COMMENT '预期金额',
  `expect_duration` smallint(2) DEFAULT NULL COMMENT '预期时间',
  `demand` varchar(200) DEFAULT NULL COMMENT '其他需求',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='金融街订单信息表';

-- ----------------------------
-- Table structure for jzx_order_info
-- ----------------------------
DROP TABLE IF EXISTS `jzx_order_info`;
CREATE TABLE `jzx_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `decoration_style` varchar(50) DEFAULT NULL COMMENT '装修风格',
  `decoration_type` smallint(2) DEFAULT NULL COMMENT '装修类型。1家装，2工装，3局部',
  `decoration_area` varchar(10) DEFAULT '0.00' COMMENT '装修面积',
  `decoration_range` varchar(20) DEFAULT '' COMMENT '装修范围（可多选）',
  `house_model` varchar(15) DEFAULT '0' COMMENT '房型（1,2,3）',
  `decoration_way` varchar(15) DEFAULT '0' COMMENT '装修形式。1简装，2精装。',
  `decoration_level` varchar(15) DEFAULT '0' COMMENT '装修档次',
  `decoration_budget_money` varchar(15) DEFAULT NULL COMMENT '装修预算',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `address` varchar(200) DEFAULT '' COMMENT '服务地址',
  `service_time` datetime DEFAULT NULL COMMENT '服务时间',
  `demand` varchar(200) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='金装修订单信息表';

-- ----------------------------
-- Table structure for lxz_order_info
-- ----------------------------
DROP TABLE IF EXISTS `lxz_order_info`;
CREATE TABLE `lxz_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `travel_days` smallint(4) DEFAULT NULL COMMENT '出行天数',
  `travel_predilection` varchar(40) DEFAULT NULL COMMENT '旅游偏好，关联旅行者字典表Id，1,2,3的存储形式',
  `demand` varchar(200) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='旅行者订单信息表';

-- ----------------------------
-- Table structure for manager_agent_charge
-- ----------------------------
DROP TABLE IF EXISTS `manager_agent_charge`;
CREATE TABLE `manager_agent_charge` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录在平台中的唯一标识符',
  `agent_id` varchar(80) NOT NULL COMMENT '代理商',
  `charge_money` int(20) NOT NULL DEFAULT '0' COMMENT '充值或扣费金额',
  `charge_type` int(1) NOT NULL COMMENT '消费类型（1充值，2扣费）',
  `charge_time` datetime NOT NULL COMMENT '充值时间',
  `head_name` varchar(80) NOT NULL COMMENT '负责人',
  `order_status` int(1) NOT NULL COMMENT '订单状态 0-待审核，1-已完成',
  `is_del` int(1) DEFAULT '0' COMMENT '是否被删除 0-未删除 1-已删除',
  `remark` varchar(80) DEFAULT NULL COMMENT '备注',
  `charge_reason` int(1) DEFAULT NULL COMMENT '充值扣费原因（1：服务商vip开通；2：订单推送开通；3：顾问号开通）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='代理商充值表';

-- ----------------------------
-- Table structure for manager_grab_fee
-- ----------------------------
DROP TABLE IF EXISTS `manager_grab_fee`;
CREATE TABLE `manager_grab_fee` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `province` varchar(20) DEFAULT NULL COMMENT '所在省份',
  `city` varchar(20) DEFAULT NULL COMMENT '所在城市',
  `app_type` varchar(30) DEFAULT NULL COMMENT '应用类型',
  `grab_fee` decimal(10,0) DEFAULT '1' COMMENT '抢单费用',
  `status` int(1) DEFAULT '0' COMMENT '执行状态（0未执行 1执行）',
  `time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_del` smallint(1) NOT NULL DEFAULT '0' COMMENT '是否被删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for manager_issue_vouchers
-- ----------------------------
DROP TABLE IF EXISTS `manager_issue_vouchers`;
CREATE TABLE `manager_issue_vouchers` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录在平台中的唯一标识符',
  `coupons_name` varchar(80) NOT NULL COMMENT '代金券类型名',
  `app_name` varchar(80) NOT NULL COMMENT '应用程序类型名',
  `issue_total` int(20) DEFAULT NULL COMMENT '发放代金券数量',
  `issue_time` datetime DEFAULT NULL COMMENT '发放代金券时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='用户发放代金券表';

-- ----------------------------
-- Table structure for manager_operate_log
-- ----------------------------
DROP TABLE IF EXISTS `manager_operate_log`;
CREATE TABLE `manager_operate_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `operate_type` varchar(10) DEFAULT NULL,
  `source_url` varchar(100) DEFAULT NULL,
  `source_name` varchar(100) DEFAULT NULL,
  `operate_user` varchar(100) DEFAULT NULL,
  `operate_time` datetime DEFAULT NULL,
  `operate_ip` varchar(100) DEFAULT NULL,
  `is_del` int(1) unsigned zerofill DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for manager_slider_info
-- ----------------------------
DROP TABLE IF EXISTS `manager_slider_info`;
CREATE TABLE `manager_slider_info` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '记录在平台中的唯一标识符',
  `name` varchar(80) DEFAULT NULL COMMENT '广告名称',
  `slider_type` int(10) DEFAULT '0' COMMENT '广告类型',
  `join_time` datetime DEFAULT NULL COMMENT '开始时间',
  `overdue_time` datetime DEFAULT NULL COMMENT '结束时间',
  `slider_status` smallint(1) DEFAULT '1' COMMENT '广告状态（1：启用，0：禁用）',
  `pics_path` varchar(80) DEFAULT NULL COMMENT '图片地址',
  `link_url` varchar(80) DEFAULT NULL COMMENT '链接地址',
  `app_type` varchar(80) DEFAULT NULL COMMENT 'app类型',
  `sort` int(5) DEFAULT NULL COMMENT '排序',
  `is_del` smallint(1) DEFAULT '0' COMMENT '是否被删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='广告表';

-- ----------------------------
-- Table structure for merchant_album
-- ----------------------------
DROP TABLE IF EXISTS `merchant_album`;
CREATE TABLE `merchant_album` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` bigint(18) NOT NULL COMMENT '商户ID',
  `album_name` varchar(20) NOT NULL COMMENT '相册名称',
  `join_time` datetime NOT NULL COMMENT '相册创建时间',
  `is_del` smallint(1) NOT NULL DEFAULT '0' COMMENT '是否被删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `商户ID` (`merchant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户相册表';

-- ----------------------------
-- Table structure for merchant_apply_withdraw_record
-- ----------------------------
DROP TABLE IF EXISTS `merchant_apply_withdraw_record`;
CREATE TABLE `merchant_apply_withdraw_record` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` bigint(18) NOT NULL COMMENT '商户ID',
  `withdraw` bigint(18) NOT NULL COMMENT '银行卡类型或者支付宝、微信方式(关联字典表的ID主键)',
  `withdraw_no` varchar(50) NOT NULL COMMENT '银行卡或者支付宝、微信账号',
  `withdraw_price` decimal(10,2) NOT NULL COMMENT '提现金额',
  `withdraw_time` datetime NOT NULL COMMENT '提现时间',
  `withdraw_status` smallint(1) NOT NULL COMMENT '0-失败 1-成功 2-提取中',
  `remark` varchar(100) NOT NULL COMMENT '备注',
  `is_del` smallint(1) NOT NULL DEFAULT '0' COMMENT '是否被删除 0-未删除 1-已删除',
  `audit_name` varchar(20) DEFAULT NULL COMMENT '审核人姓名',
  `app_type` varchar(30) NOT NULL COMMENT '应用类型',
  PRIMARY KEY (`id`),
  KEY `商户ID` (`merchant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户申请提现记录表';

-- ----------------------------
-- Table structure for merchant_app_info
-- ----------------------------
DROP TABLE IF EXISTS `merchant_app_info`;
CREATE TABLE `merchant_app_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT,
  `app_name` varchar(10) DEFAULT NULL COMMENT '应用名称',
  `app_type` varchar(30) DEFAULT NULL COMMENT '应用类型',
  `app_remark` varchar(100) DEFAULT NULL COMMENT '应用备注',
  `icon_path` varchar(200) DEFAULT NULL COMMENT '图标路径',
  `is_del` int(1) DEFAULT '0' COMMENT '是否被删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for merchant_attachment
-- ----------------------------
DROP TABLE IF EXISTS `merchant_attachment`;
CREATE TABLE `merchant_attachment` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` bigint(18) NOT NULL COMMENT '商户ID',
  `attachment_type` smallint(2) NOT NULL COMMENT '附件类型 1-图片 后期可能加入语言、或者其他资源',
  `attachment_use` smallint(2) NOT NULL COMMENT '附件用途 比如：11 用户头像 12 用户车辆图片 格式为AB，A为类型，B为用途',
  `path` varchar(200) NOT NULL COMMENT '附件路径',
  `join_time` datetime NOT NULL COMMENT '附件创建时间',
  `is_del` smallint(1) NOT NULL DEFAULT '0' COMMENT '是否被删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `商户ID` (`merchant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户附件表';

-- ----------------------------
-- Table structure for merchant_auth
-- ----------------------------
DROP TABLE IF EXISTS `merchant_auth`;
CREATE TABLE `merchant_auth` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` bigint(18) NOT NULL COMMENT '商户ID',
  `auth_type` smallint(1) NOT NULL COMMENT '认证类型 1-企业认证 2-个人认证',
  `auth_status` smallint(1) NOT NULL DEFAULT '0' COMMENT '认证状态 0-未通过 1-已通过 2-验证中',
  `path` varchar(100) NOT NULL COMMENT '认证图片的路径 ',
  `join_time` datetime NOT NULL COMMENT '认证申请时间',
  `auth_time` datetime DEFAULT NULL COMMENT '认证审核时间',
  PRIMARY KEY (`id`),
  KEY `商户ID` (`merchant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户认证信息表';

-- ----------------------------
-- Table structure for merchant_contact
-- ----------------------------
DROP TABLE IF EXISTS `merchant_contact`;
CREATE TABLE `merchant_contact` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` bigint(18) NOT NULL COMMENT '商户ID',
  `telephone` varchar(20) NOT NULL COMMENT '联系电话',
  PRIMARY KEY (`id`),
  KEY `商户ID` (`merchant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户联系方式表';

-- ----------------------------
-- Table structure for merchant_employees
-- ----------------------------
DROP TABLE IF EXISTS `merchant_employees`;
CREATE TABLE `merchant_employees` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` bigint(18) NOT NULL COMMENT '商户ID',
  `name` varchar(20) DEFAULT NULL COMMENT '员工姓名',
  `phone` varchar(15) NOT NULL COMMENT '员工手机号',
  `employees_type` smallint(1) NOT NULL DEFAULT '2' COMMENT '员工类型 1-老板 2-普通员工 3-财务',
  `join_time` datetime NOT NULL COMMENT '加入时间',
  `password` varchar(10) DEFAULT NULL COMMENT '员工密码，暂时保留字段',
  `verification_code` varchar(6) DEFAULT NULL COMMENT '验证码',
  `verification_time` datetime DEFAULT NULL COMMENT '验证时间',
  `verification_status` smallint(1) DEFAULT NULL COMMENT '验证状态 0-未通过 1-已通过 2-验证中',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登陆时间',
  `app_type` varchar(30) DEFAULT NULL COMMENT '应用类型',
  `is_del` smallint(1) DEFAULT '0' COMMENT '是否删除',
  `employee_key` varchar(60) DEFAULT NULL COMMENT '商户员工密钥',
  PRIMARY KEY (`id`),
  KEY `商户ID` (`merchant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户员工信息表';

-- ----------------------------
-- Table structure for merchant_employees_num_apply
-- ----------------------------
DROP TABLE IF EXISTS `merchant_employees_num_apply`;
CREATE TABLE `merchant_employees_num_apply` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` bigint(18) NOT NULL COMMENT '商户ID',
  `apply_increase_employee_num` smallint(6) NOT NULL COMMENT '申请增加的员工数',
  `apply_time` datetime NOT NULL COMMENT '申请时间',
  `apply_status` smallint(1) NOT NULL DEFAULT '0' COMMENT '申请状态 0-待确认 1-已确认 2-已开通 3-无效（代理审核为无效）4-已过期（开通一年到期）',
  `confirm_time` datetime DEFAULT NULL COMMENT '确认时间',
  `open_time` datetime DEFAULT NULL COMMENT '开通时间',
  `failure_time` datetime DEFAULT NULL COMMENT '失效时间',
  `app_type` varchar(30) NOT NULL COMMENT '应用程序类型',
  `is_del` smallint(1) NOT NULL DEFAULT '0' COMMENT '是否被删除 0未删除，1已删除',
  `confirm_user` varchar(50) DEFAULT NULL COMMENT '确认用户名（一般是项目代理）',
  `open_user` varchar(50) DEFAULT NULL COMMENT '开通用户名（一般是公司运营）',
  `confirm_user_id` bigint(20) DEFAULT NULL,
  `open_user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `商户ID` (`merchant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户员工数增加申请表';

-- ----------------------------
-- Table structure for merchant_info
-- ----------------------------
DROP TABLE IF EXISTS `merchant_info`;
CREATE TABLE `merchant_info` (
  `id` bigint(18) NOT NULL COMMENT '主键ID',
  `name` varchar(60) DEFAULT NULL COMMENT '商户名称',
  `detail` varchar(500) DEFAULT NULL COMMENT '商户详情介绍',
  `short_name` varchar(20) DEFAULT NULL COMMENT '商户简称',
  `address` varchar(60) DEFAULT NULL COMMENT '商户地址',
  `province` varchar(20) DEFAULT NULL COMMENT '所在省份',
  `city` varchar(20) DEFAULT NULL COMMENT '所在城市',
  `location_address` varchar(100) DEFAULT NULL COMMENT '定位得到的地址信息',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `join_time` datetime NOT NULL COMMENT '创建时间',
  `money_password` varchar(10) DEFAULT NULL COMMENT '提现密码',
  `money_real_name` varchar(10) DEFAULT NULL COMMENT '提现真实姓名',
  `money_id_no` varchar(20) DEFAULT NULL COMMENT '提现真实身份证号',
  `ip` varchar(20) DEFAULT NULL COMMENT 'ip地址',
  `micro_website_url` varchar(100) DEFAULT NULL COMMENT '微官网url',
  `max_employee_num` smallint(4) NOT NULL DEFAULT '1' COMMENT '最大员工数',
  `vip_level` smallint(2) DEFAULT '0' COMMENT '会员级别 0-非会员 1-1级会员 2-2级会员',
  `order_price` decimal(5,2) DEFAULT '1.00' COMMENT '抢单费用',
  `app_type` varchar(30) NOT NULL COMMENT '应用类型',
  `is_del` smallint(1) NOT NULL DEFAULT '0' COMMENT '是否被删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `商户名称` (`name`),
  KEY `经纬度` (`longitude`,`latitude`),
  KEY `主键ID` (`id`) USING BTREE,
  KEY `是否删除` (`is_del`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户信息表';

-- ----------------------------
-- Table structure for merchant_order_abandon
-- ----------------------------
DROP TABLE IF EXISTS `merchant_order_abandon`;
CREATE TABLE `merchant_order_abandon` (
  `merchant_id` bigint(18) NOT NULL COMMENT '商户ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  KEY `商户ID` (`merchant_id`) USING BTREE,
  KEY `商户ID，订单ID` (`merchant_id`,`order_id`) USING BTREE,
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户订单抛弃表';

-- ----------------------------
-- Table structure for merchant_order_payment_details
-- ----------------------------
DROP TABLE IF EXISTS `merchant_order_payment_details`;
CREATE TABLE `merchant_order_payment_details` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT,
  `merchant_id` bigint(18) NOT NULL DEFAULT '0' COMMENT '商户ID',
  `pay_type` smallint(1) NOT NULL DEFAULT '0' COMMENT '支付类型。1充值成功。2抢单成功。',
  `pay_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '支付金额',
  `pay_time` datetime NOT NULL COMMENT '支付时间',
  `service_type_id` bigint(18) DEFAULT '0' COMMENT '服务类型。',
  `app_type` varchar(30) NOT NULL COMMENT '应用类型',
  `is_del` smallint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `商户ID` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for merchant_payment_details
-- ----------------------------
DROP TABLE IF EXISTS `merchant_payment_details`;
CREATE TABLE `merchant_payment_details` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` bigint(18) NOT NULL COMMENT '商户ID',
  `payment_type` smallint(1) NOT NULL COMMENT '收支类型 0-收入 1-支出',
  `business_id` bigint(18) NOT NULL COMMENT '订单ID或者提现ID',
  `payment_price` decimal(10,2) NOT NULL COMMENT '业务处理金额',
  `payment_time` datetime NOT NULL COMMENT '业务处理时间',
  `is_del` smallint(1) NOT NULL DEFAULT '0' COMMENT '是否被删除 0-未删除 1-已删除',
  `app_type` varchar(30) NOT NULL COMMENT '应用类型',
  PRIMARY KEY (`id`),
  KEY `商户ID` (`merchant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户收支明细记录表';

-- ----------------------------
-- Table structure for merchant_photo
-- ----------------------------
DROP TABLE IF EXISTS `merchant_photo`;
CREATE TABLE `merchant_photo` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `album_id` bigint(18) NOT NULL COMMENT '相册ID',
  `path` varchar(100) NOT NULL COMMENT '相片路径',
  `join_time` datetime NOT NULL COMMENT '相片创建时间',
  `is_del` smallint(1) NOT NULL DEFAULT '0' COMMENT '是否被删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `相册ID` (`album_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户相片表';

-- ----------------------------
-- Table structure for merchant_plan
-- ----------------------------
DROP TABLE IF EXISTS `merchant_plan`;
CREATE TABLE `merchant_plan` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` bigint(18) NOT NULL COMMENT '商户ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `receive_employees_id` bigint(18) DEFAULT NULL COMMENT '接单人ID',
  `price` decimal(10,2) NOT NULL COMMENT '方案价格',
  `content` varchar(200) DEFAULT NULL COMMENT '方案内容',
  `plan_type` bigint(18) NOT NULL COMMENT '关联服务类型表服务类型ID',
  `join_time` datetime NOT NULL COMMENT '附件添加时间',
  `is_del` smallint(1) NOT NULL DEFAULT '0' COMMENT '是否被删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `商户ID` (`merchant_id`) USING BTREE,
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户方案表';

-- ----------------------------
-- Table structure for merchant_plan_attachment
-- ----------------------------
DROP TABLE IF EXISTS `merchant_plan_attachment`;
CREATE TABLE `merchant_plan_attachment` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_plan_id` bigint(18) NOT NULL COMMENT '商户方案ID',
  `attachment_type` smallint(2) NOT NULL COMMENT '附件类型 1-图片 2-语音',
  `attachment_use` smallint(2) NOT NULL COMMENT '附件用途 比如：11 方案图片 格式为AB，A为类型，B为用途',
  `path` varchar(200) NOT NULL COMMENT '附件路径',
  `join_time` datetime NOT NULL COMMENT '附件添加时间',
  `is_del` smallint(1) NOT NULL DEFAULT '0' COMMENT '是否被删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `商户方案ID` (`merchant_plan_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户方案附件表';

-- ----------------------------
-- Table structure for merchant_push
-- ----------------------------
DROP TABLE IF EXISTS `merchant_push`;
CREATE TABLE `merchant_push` (
  `merchant_id` bigint(18) NOT NULL COMMENT '商户ID',
  `phone` varchar(15) DEFAULT NULL COMMENT '手机号',
  `client_id` varchar(100) NOT NULL COMMENT '商户客户端ID',
  `app_type` varchar(30) NOT NULL COMMENT '应用类型',
  `client_type` int(1) DEFAULT NULL COMMENT '1：安卓，2：ios',
  `device_id` varchar(100) DEFAULT NULL COMMENT '唯一设备ID',
  KEY `商户ID` (`merchant_id`) USING BTREE,
  KEY `商户CLIENT_ID` (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户推送信息表';

-- ----------------------------
-- Table structure for merchant_service_type
-- ----------------------------
DROP TABLE IF EXISTS `merchant_service_type`;
CREATE TABLE `merchant_service_type` (
  `merchant_id` bigint(18) NOT NULL COMMENT '商户ID',
  `service_type_id` bigint(18) NOT NULL COMMENT '服务类型ID',
  `app_type` varchar(30) NOT NULL COMMENT '应用类型',
  KEY `商户ID` (`merchant_id`) USING BTREE,
  KEY `商户ID，appType` (`merchant_id`,`app_type`) USING BTREE,
  KEY `appType` (`app_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户服务类型表';

-- ----------------------------
-- Table structure for merchant_statistics
-- ----------------------------
DROP TABLE IF EXISTS `merchant_statistics`;
CREATE TABLE `merchant_statistics` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` bigint(18) NOT NULL COMMENT '商户ID',
  `grab_frequency` int(10) NOT NULL DEFAULT '0' COMMENT '总抢单次数',
  `service_frequency` int(10) NOT NULL DEFAULT '0' COMMENT '总服务次数',
  `total_income_price` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '总收入金额',
  `total_withdraw_price` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '总提取金额',
  `surplus_price` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '余额',
  `order_surplus_price` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '订单余额',
  `total_attitude_evaluation` int(10) DEFAULT '0' COMMENT '总服务态度评价',
  `total_quality_evaluation` int(10) DEFAULT '0' COMMENT '总服务质量评价',
  `total_speed_evaluation` int(10) DEFAULT '0' COMMENT '总服务速度评价',
  `total_count_evaluation` int(10) DEFAULT '0' COMMENT '总评价次数',
  `app_type` varchar(30) NOT NULL COMMENT '应用类型',
  PRIMARY KEY (`id`),
  KEY `商户ID` (`merchant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户统计信息表';

-- ----------------------------
-- Table structure for merchant_topup_apply
-- ----------------------------
DROP TABLE IF EXISTS `merchant_topup_apply`;
CREATE TABLE `merchant_topup_apply` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT,
  `merchant_id` bigint(18) NOT NULL DEFAULT '0' COMMENT '商户ID',
  `topup_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '充值金额',
  `apply_time` datetime NOT NULL COMMENT '申请时间',
  `apply_status` smallint(1) NOT NULL DEFAULT '0' COMMENT '申请状态 0-待确认 1-已确认 2-已开通 3-无效（代理审核为无效）4-已过期（开通一年到期）',
  `confirm_time` datetime DEFAULT NULL COMMENT '审核时间',
  `app_type` varchar(30) NOT NULL COMMENT '应用类型',
  `is_del` smallint(1) NOT NULL DEFAULT '0' COMMENT '删除标记。0未删除，1已删除',
  `open_time` datetime DEFAULT NULL,
  `confirm_user` varchar(50) DEFAULT NULL,
  `open_user` varchar(50) DEFAULT NULL,
  `confirm_user_id` bigint(20) DEFAULT NULL,
  `open_user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `商户ID` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户充值申请表';

-- ----------------------------
-- Table structure for merchant_users
-- ----------------------------
DROP TABLE IF EXISTS `merchant_users`;
CREATE TABLE `merchant_users` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` bigint(18) NOT NULL COMMENT '商户ID',
  `user_id` bigint(18) unsigned NOT NULL COMMENT '商户客户ID',
  `service_frequency` int(10) NOT NULL DEFAULT '0' COMMENT '服务次数',
  `app_type` varchar(30) NOT NULL COMMENT '应用类型',
  PRIMARY KEY (`id`),
  KEY `商户ID` (`merchant_id`),
  KEY `用户ID` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户客户信息表';

-- ----------------------------
-- Table structure for merchant_vip_apply
-- ----------------------------
DROP TABLE IF EXISTS `merchant_vip_apply`;
CREATE TABLE `merchant_vip_apply` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` bigint(18) NOT NULL COMMENT '关联的商户ID',
  `apply_vip_level` smallint(2) NOT NULL COMMENT '申请会员等级',
  `apply_time` datetime NOT NULL COMMENT '申请时间',
  `apply_status` smallint(1) NOT NULL DEFAULT '0' COMMENT '申请状态 0-待确认 1-已确认 2-已开通 3-无效（代理审核为无效）4-已过期（开通一年到期）',
  `confirm_time` datetime DEFAULT NULL COMMENT '确认收款时间',
  `open_time` datetime DEFAULT NULL COMMENT '开通时间',
  `failure_time` datetime DEFAULT NULL COMMENT '失效时间',
  `app_type` varchar(30) NOT NULL COMMENT '应用程序类型',
  `is_del` smallint(1) NOT NULL DEFAULT '0' COMMENT '是否被删除 0未删除，1已删除',
  `confirm_user` varchar(50) DEFAULT NULL COMMENT '确认用户名（一般是项目代理）',
  `open_user` varchar(50) DEFAULT NULL COMMENT '开通用户名（一般是公司运营）',
  `confirm_user_id` bigint(20) DEFAULT NULL,
  `open_user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `商户ID` (`merchant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户会员申请表';

-- ----------------------------
-- Table structure for merchant_vouchers_permissions
-- ----------------------------
DROP TABLE IF EXISTS `merchant_vouchers_permissions`;
CREATE TABLE `merchant_vouchers_permissions` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` bigint(18) DEFAULT NULL COMMENT '商户ID',
  `vouchers_id` bigint(18) NOT NULL COMMENT '代金券类型，关联服务类型表ID，例如1-洗车 2-维修 3-保养',
  `need_price` decimal(10,2) DEFAULT NULL COMMENT '使用代金券需满足的金额；为空则为不限制',
  `cutoff_time` datetime DEFAULT NULL COMMENT '截止时间',
  `join_time` datetime NOT NULL COMMENT '创建时间',
  `count` int(6) NOT NULL DEFAULT '0' COMMENT '数量',
  `is_del` smallint(1) NOT NULL DEFAULT '0' COMMENT '是否被删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `商户ID` (`merchant_id`) USING BTREE,
  KEY `代金券ID` (`vouchers_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户代金券权限表';

-- ----------------------------
-- Table structure for merchant_withdraw
-- ----------------------------
DROP TABLE IF EXISTS `merchant_withdraw`;
CREATE TABLE `merchant_withdraw` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` bigint(18) NOT NULL COMMENT '商户ID',
  `real_name` varchar(20) NOT NULL DEFAULT '' COMMENT '真实姓名',
  `ID_No` varchar(20) NOT NULL DEFAULT '' COMMENT '身份证号',
  `withdraw` bigint(18) NOT NULL COMMENT '银行卡类型或者支付宝、微信方式(关联字典表的ID主键)',
  `withdraw_no` varchar(50) NOT NULL COMMENT '银行卡或者支付宝、微信账号',
  `app_type` varchar(30) NOT NULL COMMENT '应用类型',
  `is_del` smallint(1) NOT NULL DEFAULT '0' COMMENT '是否解绑',
  PRIMARY KEY (`id`),
  KEY `商户ID` (`merchant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户提现信息表 （记录银行卡等信息）';

-- ----------------------------
-- Table structure for mst_order_info
-- ----------------------------
DROP TABLE IF EXISTS `mst_order_info`;
CREATE TABLE `mst_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '记录在平台中的唯一标识符',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `service_manner` smallint(2) DEFAULT '0' COMMENT '服务方式',
  `service_site` varchar(100) DEFAULT NULL COMMENT '服务地点',
  `service_num_of_people` smallint(3) DEFAULT '1' COMMENT '服务人数',
  `technician_sex` smallint(1) DEFAULT '0' COMMENT '技师性别 0-不限 1-男 2-女',
  `service_item` smallint(2) DEFAULT '0' COMMENT '服务项目',
  `reservation_time` datetime DEFAULT NULL COMMENT '预约时间',
  `service_demand` smallint(2) DEFAULT '0' COMMENT '服务要求',
  `extra_demand` varchar(20) DEFAULT '0' COMMENT '额外需求',
  `demand` varchar(200) DEFAULT NULL COMMENT '其他需求',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='妙手堂订单信息表';

-- ----------------------------
-- Table structure for order_attachment
-- ----------------------------
DROP TABLE IF EXISTS `order_attachment`;
CREATE TABLE `order_attachment` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `attachment_type` smallint(2) NOT NULL COMMENT '附件类型 1-图片 后期可能加入语言、或者其他资源',
  `attachment_use` smallint(2) NOT NULL COMMENT '附件用途 比如：11 用户头像 12 用户车辆图片 格式为AB，A为类型，B为用途',
  `path` varchar(200) NOT NULL COMMENT '附件路径',
  `join_time` datetime NOT NULL COMMENT '附件添加时间',
  `is_del` smallint(1) NOT NULL DEFAULT '0' COMMENT '是否被删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单附件表';

-- ----------------------------
-- Table structure for order_info
-- ----------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(20) DEFAULT NULL COMMENT '订单编号',
  `user_id` bigint(18) NOT NULL COMMENT '订单的用户ID',
  `service_type_id` int(10) NOT NULL COMMENT '订单类型，关联服务类型表服务类型ID',
  `merchant_id` bigint(18) DEFAULT NULL COMMENT '所选的商户ID',
  `receive_employees_id` bigint(18) DEFAULT NULL COMMENT '接单员工ID',
  `merchant_plan_id` bigint(18) DEFAULT NULL COMMENT '所选商家方案ID',
  `order_status` smallint(1) NOT NULL COMMENT '用户端订单的状态 1-新预约 2-待选择 3-已确认 4-已完成 5-支付完成 6-订单已过期 7-无效订单',
  `order_price` decimal(10,2) DEFAULT NULL COMMENT '订单金额',
  `order_actual_price` decimal(10,2) DEFAULT NULL COMMENT '订单实际金额',
  `order_pay_type` smallint(1) DEFAULT NULL COMMENT '支付方式 1-支付宝支付 2-微信支付 3-现金支付 （关联字典表）',
  `vouchers_id` bigint(18) DEFAULT NULL COMMENT '代金券ID',
  `join_time` datetime DEFAULT NULL COMMENT '加入的时间',
  `confirm_time` datetime DEFAULT NULL COMMENT '确认时间',
  `deal_time` datetime DEFAULT NULL COMMENT '成交时间',
  `is_del` smallint(1) NOT NULL DEFAULT '0' COMMENT '是否被删除 0-未删除 1-已删除',
  `app_type` varchar(30) NOT NULL COMMENT '应用类型',
  `ip` varchar(20) DEFAULT NULL COMMENT 'IP地址',
  `province` varchar(20) DEFAULT NULL COMMENT '所在省份',
  `city` varchar(20) DEFAULT NULL COMMENT '所在城市',
  PRIMARY KEY (`id`),
  KEY `用户ID` (`user_id`) USING BTREE,
  KEY `商户ID` (`merchant_id`) USING BTREE,
  KEY `商户方案ID` (`merchant_plan_id`) USING BTREE,
  KEY `是否删除` (`is_del`) USING BTREE,
  KEY `appType，是否删除` (`app_type`,`is_del`) USING BTREE,
  KEY `接单人ID` (`receive_employees_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单表';

-- ----------------------------
-- Table structure for phone_information
-- ----------------------------
DROP TABLE IF EXISTS `phone_information`;
CREATE TABLE `phone_information` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(18) NOT NULL DEFAULT '0' COMMENT '用户ID',
  `handset_makers` varchar(200) DEFAULT NULL COMMENT '设备厂商',
  `mobile_version` varchar(200) DEFAULT NULL COMMENT '设备版本',
  `mobile_number` varchar(200) DEFAULT NULL COMMENT '设备编号',
  `registere_time` datetime DEFAULT NULL COMMENT '注册时间',
  `user_type` int(1) DEFAULT '0' COMMENT '用户类型（1-用户，2-商户）',
  PRIMARY KEY (`id`),
  KEY `用户ID` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for push_merchant_order
-- ----------------------------
DROP TABLE IF EXISTS `push_merchant_order`;
CREATE TABLE `push_merchant_order` (
  `order_id` bigint(18) DEFAULT NULL COMMENT '订单ID',
  `merchant_id` bigint(18) DEFAULT NULL COMMENT '商户ID',
  `client_id` varchar(100) DEFAULT NULL COMMENT '设备唯一编号',
  `client_type` int(1) DEFAULT NULL COMMENT '设备类型',
  `join_time` datetime DEFAULT NULL COMMENT '添加时间',
  KEY `订单ID` (`order_id`) USING BTREE,
  KEY `商户ID` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='推送商户订单记录表';

-- ----------------------------
-- Table structure for qpl_order_info
-- ----------------------------
DROP TABLE IF EXISTS `qpl_order_info`;
CREATE TABLE `qpl_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `mahJong` varchar(255) DEFAULT NULL,
  `poker` smallint(2) DEFAULT '0' COMMENT '扑克项目',
  `chess` smallint(2) DEFAULT '0' COMMENT '棋类项目',
  `reservation_people_count` varchar(10) DEFAULT '0' COMMENT '预约人数',
  `reservation_time` datetime DEFAULT NULL COMMENT '预约时间',
  `append_service` varchar(20) DEFAULT '0' COMMENT '附加服务',
  `demand` varchar(200) DEFAULT NULL COMMENT '其他需求',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='棋牌乐订单信息表';

-- ----------------------------
-- Table structure for qzy_order_info
-- ----------------------------
DROP TABLE IF EXISTS `qzy_order_info`;
CREATE TABLE `qzy_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `photograph_type` smallint(2) DEFAULT NULL COMMENT '摄影类型',
  `shooting_address` smallint(2) DEFAULT NULL COMMENT '拍摄地点',
  `expected_consumption` varchar(20) DEFAULT '0' COMMENT '预期消费。1 500以上，2 300-500，3 100-300，4 100以下',
  `baby_sex` smallint(1) DEFAULT NULL,
  `baby_age` varchar(10) DEFAULT '0' COMMENT '宝宝年龄。1 4岁以上，2 2-4岁，3 0-2岁',
  `class_type` smallint(2) DEFAULT '0' COMMENT '课程类型。1舞蹈戏剧课，2球类运动课，3健身课，4音乐课，5艺术课，6烹饪课，7语言课，8其他',
  `class_time` varchar(20) DEFAULT '0' COMMENT '上课时间。周一到周日',
  `teacher_sex` smallint(2) DEFAULT NULL,
  `amusement_type` smallint(2) DEFAULT '0' COMMENT '游乐类型。1儿童乐园，2主题游乐场，3婴儿游泳馆，4创意手工坊',
  `nurse_service_type` smallint(2) DEFAULT NULL COMMENT '母音护理',
  `service_time` datetime DEFAULT NULL COMMENT '时间',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `address` varchar(200) DEFAULT '' COMMENT '地址',
  `demand` varchar(200) DEFAULT '' COMMENT '需求说明',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='亲子园订单信息表';

-- ----------------------------
-- Table structure for service_type
-- ----------------------------
DROP TABLE IF EXISTS `service_type`;
CREATE TABLE `service_type` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `service_type_id` int(10) NOT NULL COMMENT '服务类型ID',
  `service_type_name` varchar(50) NOT NULL COMMENT '服务类型名称',
  `app_name` varchar(10) DEFAULT NULL COMMENT '应用名称',
  `app_type` varchar(30) NOT NULL COMMENT '应用类型',
  `is_del` smallint(1) NOT NULL DEFAULT '0' COMMENT '是否被删除 0-未删除 1-已删除',
  `parent_id` bigint(18) DEFAULT NULL COMMENT '父id',
  `is_leaves` smallint(1) DEFAULT '1' COMMENT '是否叶子节点 0-否 1-是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='服务类型表';

-- ----------------------------
-- Table structure for service_type_attachment
-- ----------------------------
DROP TABLE IF EXISTS `service_type_attachment`;
CREATE TABLE `service_type_attachment` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `service_type_id` bigint(18) NOT NULL COMMENT '服务类型表主键ID',
  `attachment_type` smallint(2) NOT NULL DEFAULT '1' COMMENT '附件类型 1-图片 2-语音',
  `attachment_style` varchar(20) NOT NULL DEFAULT 'orderIcon' COMMENT '附件样式 orderIcon-订单图标 showIcon-业务图标',
  `path` varchar(200) NOT NULL COMMENT '附件路径',
  `join_time` datetime NOT NULL COMMENT '附件添加时间',
  `is_del` smallint(1) NOT NULL DEFAULT '0' COMMENT '是否被删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `服务类型ID` (`service_type_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='服务类型附件表';

-- ----------------------------
-- Table structure for swg_order_info
-- ----------------------------
DROP TABLE IF EXISTS `swg_order_info`;
CREATE TABLE `swg_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `reservation_people_count` smallint(2) DEFAULT '0' COMMENT '预约人数',
  `reservation_time` datetime DEFAULT NULL COMMENT '预约时间',
  `seating_area` smallint(2) DEFAULT '0' COMMENT '就座区域',
  `append_service` varchar(20) DEFAULT '0' COMMENT '附加服务',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `address` varchar(200) DEFAULT NULL COMMENT '地址',
  `demand` varchar(200) DEFAULT NULL COMMENT '其他需求',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商务馆订单信息表';

-- ----------------------------
-- Table structure for sxd_loan_info
-- ----------------------------
DROP TABLE IF EXISTS `sxd_loan_info`;
CREATE TABLE `sxd_loan_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '用户在平台中的唯一标识符',
  `order_id` bigint(18) NOT NULL COMMENT '关联的订单ID',
  `identity` smallint(1) DEFAULT NULL COMMENT '贷款人身份 0-保密 1-企业家 2-上班族',
  `monthly_income` int(10) DEFAULT NULL COMMENT '月收入',
  `apply_loan_city` varchar(6) DEFAULT NULL COMMENT '申请贷款的城市',
  `traffic` smallint(1) DEFAULT NULL COMMENT '交通情况 1-有车一族 2-公共通',
  `house` smallint(1) DEFAULT NULL COMMENT '住房情况 1-已有住房 2-租房一族',
  `hope_loan_amount` int(8) DEFAULT NULL COMMENT '希望贷款数额',
  `demand` varchar(200) DEFAULT NULL COMMENT '备注',
  `hope_loan_deadline` varchar(2) DEFAULT NULL COMMENT '希望贷款期限 月份',
  `have_guarantee` smallint(1) DEFAULT NULL COMMENT '是否有物品抵押 1-有 2-无',
  `loan_purpose` smallint(1) DEFAULT NULL COMMENT '贷款用途 1-经营周转 2-个人消费 3-贷款买车 4-按揭买房',
  PRIMARY KEY (`id`),
  KEY `商户ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='贷款人信息表';

-- ----------------------------
-- Table structure for syp_order_info
-- ----------------------------
DROP TABLE IF EXISTS `syp_order_info`;
CREATE TABLE `syp_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '记录在平台中的唯一标识符',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `shoot_scene` varchar(40) DEFAULT NULL COMMENT '拍摄场景 1,2存储形式',
  `hssy_shoot_style` varchar(40) DEFAULT NULL COMMENT '婚纱摄影拍摄风格',
  `grxz_shoot_style` varchar(40) DEFAULT NULL COMMENT '个人写真拍摄风格',
  `zhsy_service_type` varchar(40) DEFAULT NULL COMMENT '展会摄影服务类型',
  `wdy_service_type` varchar(40) DEFAULT NULL COMMENT '微电影服务类型',
  `photographer_number` varchar(10) DEFAULT NULL COMMENT '摄影师数量',
  `forecast_film_duration` varchar(20) DEFAULT NULL COMMENT '预计影片时长',
  `price_expect` varchar(20) DEFAULT '0' COMMENT '价格预期',
  `shoot_time` datetime DEFAULT NULL COMMENT '拍摄时间',
  `demand` varchar(200) DEFAULT NULL COMMENT '其他需求',
  `sex` int(1) DEFAULT NULL COMMENT '性别（1男2女）',
  `address` varchar(200) DEFAULT NULL COMMENT '拍摄地址',
  `etsy_service_type` varchar(2) DEFAULT NULL COMMENT '儿童摄影类型',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='摄影棚订单信息表';

-- ----------------------------
-- Table structure for ts_order_info
-- ----------------------------
DROP TABLE IF EXISTS `ts_order_info`;
CREATE TABLE `ts_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '记录在平台中的唯一标识符',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `outdoors_ad_type` smallint(2) DEFAULT '0' COMMENT '户外广告类型',
  `movie_ad_type` smallint(2) DEFAULT '0' COMMENT '影视广告类型',
  `net_ad_type` smallint(2) DEFAULT '0' COMMENT '网络广告类型',
  `marketing_ad_type` smallint(2) DEFAULT '0' COMMENT '网络广告类型',
  `ad_place_num` smallint(2) DEFAULT NULL COMMENT '广告位数量',
  `ad_duration` smallint(2) DEFAULT NULL COMMENT '广告时长',
  `put_time` smallint(2) DEFAULT NULL COMMENT '投放时间',
  `put_duration` smallint(2) DEFAULT NULL COMMENT '投放时段',
  `price_manner` smallint(2) DEFAULT NULL COMMENT '计费方式',
  `activity_time` datetime DEFAULT NULL COMMENT '活动时间',
  `demand` varchar(200) DEFAULT NULL COMMENT '其他需求',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='推手订单信息表';

-- ----------------------------
-- Table structure for user_address
-- ----------------------------
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '记录在平台中的唯一标识符',
  `user_id` bigint(18) NOT NULL COMMENT '用户ID',
  `address_type` smallint(1) NOT NULL COMMENT '性别 0-家地址 1-公司地址 2-常用地址',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `address_info` varchar(60) NOT NULL COMMENT '地址信息',
  `app_type` varchar(30) NOT NULL COMMENT '应用类型',
  PRIMARY KEY (`id`),
  KEY `用户ID` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户地址信息表';

-- ----------------------------
-- Table structure for user_attachment
-- ----------------------------
DROP TABLE IF EXISTS `user_attachment`;
CREATE TABLE `user_attachment` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '记录在平台中的唯一标识符',
  `user_id` bigint(18) NOT NULL COMMENT '附件关联的用户ID',
  `attachment_type` smallint(2) NOT NULL COMMENT '附件类型 1-图片 后期可能加入语言、或者其他资源',
  `attachment_use` smallint(2) NOT NULL COMMENT '附件用途 比如：11 用户头像 12 用户车辆图片 格式为AB，A为类型，B为用途',
  `path` varchar(200) NOT NULL COMMENT '附件路径',
  `join_time` datetime NOT NULL COMMENT '附件添加时间',
  `is_del` smallint(1) NOT NULL DEFAULT '0' COMMENT '是否被删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `用户ID` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户附件表';

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `id` bigint(18) NOT NULL COMMENT '主键ID',
  `name` varchar(10) DEFAULT NULL COMMENT '姓名',
  `sex` smallint(1) DEFAULT '0' COMMENT '性别 0-保密 1-男 2-女',
  `phone` varchar(15) NOT NULL COMMENT '手机号',
  `join_time` datetime NOT NULL COMMENT '加入时间',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登陆时间',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `verification_code` varchar(6) DEFAULT NULL COMMENT '验证码',
  `verification_time` datetime DEFAULT NULL COMMENT '验证时间',
  `is_verification` smallint(1) NOT NULL DEFAULT '0' COMMENT '是否被验证 0-未验证 1-已验证',
  `is_del` smallint(1) NOT NULL DEFAULT '0' COMMENT '是否被删除 0-未删除 1-已删除',
  `user_key` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `手机号` (`phone`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户信息表';

-- ----------------------------
-- Table structure for user_merchant_collection
-- ----------------------------
DROP TABLE IF EXISTS `user_merchant_collection`;
CREATE TABLE `user_merchant_collection` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(18) NOT NULL COMMENT '用户ID',
  `merchant_id` bigint(18) NOT NULL COMMENT '商户ID',
  `join_time` datetime NOT NULL COMMENT '添加时间',
  `app_type` varchar(30) DEFAULT NULL COMMENT '应用类型',
  PRIMARY KEY (`id`),
  KEY `用户ID` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户收藏商户表';

-- ----------------------------
-- Table structure for user_push
-- ----------------------------
DROP TABLE IF EXISTS `user_push`;
CREATE TABLE `user_push` (
  `user_id` bigint(18) NOT NULL COMMENT '用户ID',
  `client_id` varchar(100) NOT NULL COMMENT '用户客户端ID',
  `app_type` varchar(30) NOT NULL COMMENT '应用类型',
  `client_type` int(1) DEFAULT '1' COMMENT '1：安卓，2：ios',
  `device_id` varchar(100) DEFAULT NULL COMMENT 'APP生成的设备ID',
  KEY `用户ID` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户推送信息表';

-- ----------------------------
-- Table structure for user_statistics
-- ----------------------------
DROP TABLE IF EXISTS `user_statistics`;
CREATE TABLE `user_statistics` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '记录在平台中的唯一标识符',
  `user_id` bigint(18) NOT NULL COMMENT '用户ID',
  `bespeak_frequency` int(10) NOT NULL DEFAULT '0' COMMENT '总预约次数',
  `service_frequency` int(10) NOT NULL DEFAULT '0' COMMENT '总服务次数',
  `total_pay_price` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '总支出金额',
  `total_actual_price` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '实际总支出金额',
  `app_type` varchar(30) NOT NULL COMMENT '应用类型',
  PRIMARY KEY (`id`),
  KEY `用户ID` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户统计信息表';

-- ----------------------------
-- Table structure for user_vouchers_info
-- ----------------------------
DROP TABLE IF EXISTS `user_vouchers_info`;
CREATE TABLE `user_vouchers_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(18) NOT NULL COMMENT '用户ID',
  `vouchers_id` bigint(18) NOT NULL COMMENT '代金券ID',
  `get_time` datetime NOT NULL COMMENT '获取代金券时间',
  `is_use` smallint(1) NOT NULL DEFAULT '0' COMMENT '是否被使用 0-未使用 1-已使用',
  `is_del` smallint(1) DEFAULT '0' COMMENT '是否删除 0-没有 1-删除',
  `is_del_merchant` smallint(1) DEFAULT '0' COMMENT '是否被删除商户用 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `用户ID` (`user_id`) USING BTREE,
  KEY `代金券ID` (`vouchers_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户代金券表';

-- ----------------------------
-- Table structure for vouchers_info
-- ----------------------------
DROP TABLE IF EXISTS `vouchers_info`;
CREATE TABLE `vouchers_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `service_type_id` int(10) NOT NULL COMMENT '代金券类型，关联服务类型表服务类型ID',
  `cutoff_time` datetime DEFAULT NULL COMMENT '截止时间',
  `price` decimal(10,2) NOT NULL COMMENT '金额',
  `count` int(6) NOT NULL DEFAULT '0' COMMENT '数量',
  `icon_path` varchar(255) DEFAULT NULL COMMENT '图片路径',
  `app_type` varchar(30) NOT NULL COMMENT '应用类型',
  `is_del` smallint(1) NOT NULL DEFAULT '0' COMMENT '是否被删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='代金券表';

-- ----------------------------
-- Table structure for xhf_order_info
-- ----------------------------
DROP TABLE IF EXISTS `xhf_order_info`;
CREATE TABLE `xhf_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `flower_style` smallint(2) DEFAULT '0' COMMENT '花的样式',
  `flower_type` smallint(2) DEFAULT '0' COMMENT '鲜花种类',
  `box_type` smallint(2) DEFAULT NULL COMMENT '花盒类型',
  `basket_type` smallint(2) DEFAULT NULL COMMENT '花篮类型',
  `quantity` int(5) DEFAULT NULL COMMENT '花的数量',
  `blessings` varchar(200) DEFAULT NULL COMMENT '祝福卡片内容',
  `delivery_time` date DEFAULT NULL COMMENT '配送时间',
  `delivery_site` varchar(100) DEFAULT NULL COMMENT '配送地点',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `demand` varchar(200) DEFAULT NULL COMMENT '其他需求',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='鲜花坊订单信息表';

-- ----------------------------
-- Table structure for xhf_order_item
-- ----------------------------
DROP TABLE IF EXISTS `xhf_order_item`;
CREATE TABLE `xhf_order_item` (
  `id` int(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` int(18) NOT NULL COMMENT '订单id',
  `dict_type` varchar(20) DEFAULT NULL COMMENT '字典类型',
  `dict_key` varchar(80) DEFAULT NULL COMMENT '字典key',
  `quantity` int(5) DEFAULT NULL COMMENT '数量',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='鲜花坊订单项目表';

-- ----------------------------
-- Table structure for xlb_cele_model_order_info
-- ----------------------------
DROP TABLE IF EXISTS `xlb_cele_model_order_info`;
CREATE TABLE `xlb_cele_model_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `cele_or_model` smallint(2) DEFAULT '0' COMMENT '庆典或礼仪',
  `cele_type` smallint(2) DEFAULT '0' COMMENT '庆典类型：发布会、奠基仪式、企业年会、生日寿宴',
  `cele_content` varchar(100) DEFAULT NULL COMMENT '庆典的节目内容',
  `model_type` smallint(2) DEFAULT '0' COMMENT '模特类型',
  `model_sex` smallint(1) DEFAULT NULL COMMENT '模特性别',
  `model_detail` varchar(100) DEFAULT NULL COMMENT '模特年龄',
  `service_time` varchar(100) DEFAULT NULL COMMENT '服务时间',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `demand` varchar(255) DEFAULT NULL COMMENT '特殊要求',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='喜乐吧庆典订单信息表';

-- ----------------------------
-- Table structure for xlb_wedding_car_order_info
-- ----------------------------
DROP TABLE IF EXISTS `xlb_wedding_car_order_info`;
CREATE TABLE `xlb_wedding_car_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `leader_car_type` varchar(50) DEFAULT NULL COMMENT '头车类型',
  `leader_car_driver` smallint(1) DEFAULT '0' COMMENT '是否需要头车司机',
  `queue_car_type` varchar(50) DEFAULT NULL COMMENT '车队车类型',
  `queue_car_number` smallint(3) DEFAULT '0' COMMENT '车队数量',
  `rent_time` smallint(1) DEFAULT '0' COMMENT '租赁时间：半天、全天',
  `service_time` varchar(100) DEFAULT NULL COMMENT '婚礼时间',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `demand` varchar(255) DEFAULT NULL COMMENT '特殊需求',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='喜乐吧婚车租赁订单信息表';

-- ----------------------------
-- Table structure for xlb_wedding_order_info
-- ----------------------------
DROP TABLE IF EXISTS `xlb_wedding_order_info`;
CREATE TABLE `xlb_wedding_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(18) NOT NULL,
  `wedding_type` smallint(2) DEFAULT NULL COMMENT '婚礼类型',
  `wedding_roles` varchar(100) DEFAULT NULL COMMENT '婚礼人员',
  `theme` smallint(2) DEFAULT NULL COMMENT '主题色系',
  `price_expire` varchar(100) DEFAULT NULL COMMENT '婚礼预期价格',
  `service_time` varchar(100) DEFAULT NULL COMMENT '婚礼日期',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `demand` varchar(255) DEFAULT NULL COMMENT '特殊需求',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='喜乐吧婚礼策划订单信息表';

-- ----------------------------
-- Table structure for xlb_wedding_photo_order_info
-- ----------------------------
DROP TABLE IF EXISTS `xlb_wedding_photo_order_info`;
CREATE TABLE `xlb_wedding_photo_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `photography_type` varchar(100) DEFAULT NULL COMMENT '摄影风格',
  `photography_scene` varchar(100) DEFAULT NULL COMMENT '场景选择',
  `price_expire` varchar(100) DEFAULT NULL COMMENT '期望价格',
  `service_time` varchar(100) DEFAULT NULL COMMENT '服务时间',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `demand` varchar(255) DEFAULT NULL COMMENT '特殊要求',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='喜乐吧婚纱摄影订单信息表';

-- ----------------------------
-- Table structure for ydc_order_info
-- ----------------------------
DROP TABLE IF EXISTS `ydc_order_info`;
CREATE TABLE `ydc_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `eat_type` varchar(100) DEFAULT '0' COMMENT '就餐类型：情侣、朋友、商务等',
  `eat_address` varchar(100) DEFAULT '0' COMMENT '就餐位置。1包厢，2大厅',
  `special_requirements` varchar(100) DEFAULT '0' COMMENT '特殊要求。1刷卡，2发票，3宝宝椅，4停车场，5露天，6湖边，7无线上网，8其他',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `address` varchar(100) DEFAULT '' COMMENT '地址',
  `order_type` int(10) DEFAULT NULL COMMENT '服务类型',
  `people_num` int(5) DEFAULT '1' COMMENT '人数',
  `service_time` varchar(20) DEFAULT NULL COMMENT '服务时间',
  `demand` varchar(200) DEFAULT '' COMMENT '需求说明',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for ydh_order_info
-- ----------------------------
DROP TABLE IF EXISTS `ydh_order_info`;
CREATE TABLE `ydh_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `ball_type` smallint(2) DEFAULT '0' COMMENT '球类类型。1室内篮球馆，2室外篮球馆，3羽毛球馆，4足球场，5网球场，6高尔夫球场，7乒乓球馆',
  `gym_type` smallint(2) DEFAULT '0' COMMENT '健身类型。1瑜伽塑形，2运动减脂，3器材健身',
  `gym_way` smallint(2) DEFAULT '0' COMMENT '锻炼方式。1个人锻炼，2私人教练',
  `interest_gym_way` varchar(20) DEFAULT '0' COMMENT '感兴趣的健身方式。1组合哑铃，2力量训练器，3动感单车，4健身操，5划船机，6沙袋，7瑜伽，8游泳，9跑步机，10其他，（可多选）',
  `natatoria_type` smallint(2) DEFAULT '0' COMMENT '游泳馆类型。1室外游泳馆，2水上乐园，3室内游泳馆',
  `skating_site_type` smallint(2) DEFAULT '0' COMMENT '溜冰场地类型。1旱冰溜冰场，2冰刀溜冰场',
  `reservation_number` varchar(10) DEFAULT '0' COMMENT '预约人数',
  `consumption_time_period` varchar(20) DEFAULT '0' COMMENT '消费时间段。1 1-2小时，2 2-4小时，3 1小时，4 全天',
  `reservation_time` datetime DEFAULT NULL COMMENT '预约时间',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `address` varchar(200) DEFAULT '' COMMENT '地址',
  `demand` varchar(200) DEFAULT '' COMMENT '需求说明',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='运动汇订单信息表';

-- ----------------------------
-- Table structure for yd_bar_order_info
-- ----------------------------
DROP TABLE IF EXISTS `yd_bar_order_info`;
CREATE TABLE `yd_bar_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `service_type` smallint(2) DEFAULT '0' COMMENT '每个业务类型下面的服务类型',
  `people_num` smallint(4) DEFAULT '1' COMMENT '聚会人数',
  `wine_type` smallint(2) DEFAULT '0' COMMENT '酒类型',
  `seat_preference` smallint(2) DEFAULT '0' COMMENT '座位偏好',
  `bar_type` smallint(2) DEFAULT '0' COMMENT '酒吧类型',
  `service_time` datetime DEFAULT NULL COMMENT '预约时间',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `address` varchar(100) DEFAULT '' COMMENT '地址',
  `demand` varchar(200) DEFAULT '' COMMENT '需求说明',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='夜店酒吧订单信息表';

-- ----------------------------
-- Table structure for yd_bath_sauna_order_info
-- ----------------------------
DROP TABLE IF EXISTS `yd_bath_sauna_order_info`;
CREATE TABLE `yd_bath_sauna_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `service_type` smallint(2) DEFAULT '0' COMMENT '每个业务类型下面的服务类型',
  `extra_service` varchar(20) DEFAULT NULL COMMENT '额外要求',
  `service_time` datetime DEFAULT NULL COMMENT '预约时间',
  `people_num` smallint(4) DEFAULT '1' COMMENT '预约人数',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `address` varchar(100) DEFAULT '' COMMENT '地址',
  `demand` varchar(200) DEFAULT '' COMMENT '需求说明',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='夜店洗浴桑拿订单信息表';

-- ----------------------------
-- Table structure for yd_club_order_info
-- ----------------------------
DROP TABLE IF EXISTS `yd_club_order_info`;
CREATE TABLE `yd_club_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `service_type` smallint(2) DEFAULT '0' COMMENT '每个业务类型下面的服务类型',
  `box_size` smallint(2) DEFAULT '0' COMMENT '包厢大小',
  `extra_service` varchar(20) DEFAULT NULL COMMENT '额外服务',
  `beauty_type` varchar(20) DEFAULT NULL COMMENT '偏好美女类型',
  `service_time` datetime DEFAULT NULL COMMENT '预约时间',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '维度',
  `address` varchar(100) DEFAULT NULL COMMENT '地址',
  `demand` varchar(200) DEFAULT NULL COMMENT '需求说明',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='夜店会所订单信息表';

-- ----------------------------
-- Table structure for yd_coffee_bar_order_info
-- ----------------------------
DROP TABLE IF EXISTS `yd_coffee_bar_order_info`;
CREATE TABLE `yd_coffee_bar_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `service_type` smallint(2) DEFAULT '0' COMMENT '每个业务类型下面的服务类型',
  `service_time` datetime DEFAULT NULL COMMENT '预约时间',
  `people_num` smallint(4) DEFAULT '1' COMMENT '预约人数',
  `seating_area` smallint(2) DEFAULT '0' COMMENT '就座区域',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `address` varchar(100) DEFAULT NULL COMMENT '地址',
  `demand` varchar(200) DEFAULT NULL COMMENT '需求说明',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='夜店咖啡水吧订单信息表';

-- ----------------------------
-- Table structure for yd_evening_show_order_info
-- ----------------------------
DROP TABLE IF EXISTS `yd_evening_show_order_info`;
CREATE TABLE `yd_evening_show_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `service_type` smallint(2) DEFAULT '0' COMMENT '每个业务类型下面的服务类型',
  `service_time` datetime DEFAULT NULL COMMENT '预约时间',
  `people_num` smallint(4) DEFAULT '1' COMMENT '预约人数',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `address` varchar(100) DEFAULT NULL COMMENT '地址',
  `demand` varchar(200) DEFAULT NULL COMMENT '需求说明',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='夜店夜场表演订单信息表';

-- ----------------------------
-- Table structure for yd_foot_massage_order_info
-- ----------------------------
DROP TABLE IF EXISTS `yd_foot_massage_order_info`;
CREATE TABLE `yd_foot_massage_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `service_type` smallint(2) DEFAULT '0' COMMENT '每个业务类型下面的服务类型',
  `service_item` smallint(2) DEFAULT '0' COMMENT '服务项目',
  `foot_item` varchar(20) DEFAULT NULL COMMENT '足浴项目',
  `service_time` datetime DEFAULT NULL COMMENT '预约时间',
  `people_num` smallint(4) DEFAULT '1' COMMENT '预约人数',
  `technician_gender` smallint(1) DEFAULT NULL COMMENT '技师性别',
  `visit_service` smallint(1) DEFAULT NULL COMMENT '上门服务 0-不需要 1-需要',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `address` varchar(100) DEFAULT NULL COMMENT '地址',
  `demand` varchar(200) DEFAULT NULL COMMENT '需求说明',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='夜店足疗按摩订单信息表';

-- ----------------------------
-- Table structure for yd_ktv_order_info
-- ----------------------------
DROP TABLE IF EXISTS `yd_ktv_order_info`;
CREATE TABLE `yd_ktv_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `service_type` smallint(2) DEFAULT '0' COMMENT '每个业务类型下面的服务类型',
  `box_size` smallint(2) DEFAULT '0' COMMENT '包厢大小',
  `end_time` datetime DEFAULT NULL COMMENT '欢唱结束时间',
  `service_time` datetime DEFAULT NULL COMMENT '预约时间',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '维度',
  `address` varchar(100) DEFAULT NULL COMMENT '地址',
  `demand` varchar(200) DEFAULT NULL COMMENT '需求说明',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='夜店KTV订单信息表';

-- ----------------------------
-- Table structure for yd_spa_order_info
-- ----------------------------
DROP TABLE IF EXISTS `yd_spa_order_info`;
CREATE TABLE `yd_spa_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `service_type` smallint(2) DEFAULT '0' COMMENT '每个业务类型下面的服务类型',
  `service_item` varchar(20) DEFAULT NULL COMMENT '服务项目',
  `service_time` datetime DEFAULT NULL COMMENT '预约时间',
  `people_num` smallint(4) DEFAULT '1' COMMENT '预约人数',
  `extra_service` varchar(20) DEFAULT NULL COMMENT '额外服务',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '维度',
  `address` varchar(100) DEFAULT NULL COMMENT '地址',
  `demand` varchar(200) DEFAULT NULL COMMENT '需求说明',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='夜店SPA订单信息表';

-- ----------------------------
-- Table structure for yp_job_seekers_info
-- ----------------------------
DROP TABLE IF EXISTS `yp_job_seekers_info`;
CREATE TABLE `yp_job_seekers_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '关联的用户ID',
  `educational_level` smallint(2) DEFAULT '0' COMMENT '教育水平',
  `on_job_status` smallint(2) DEFAULT '0' COMMENT '在职状态',
  `expect_income` smallint(2) DEFAULT '0' COMMENT '期望薪资',
  `personal_intro` varchar(500) DEFAULT '' COMMENT '个人介绍',
  PRIMARY KEY (`id`),
  KEY `用户ID` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='赢聘求职者信息表';

-- ----------------------------
-- Table structure for yp_order_info
-- ----------------------------
DROP TABLE IF EXISTS `yp_order_info`;
CREATE TABLE `yp_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '记录在平台中的唯一标识符',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `apply_job_type` smallint(2) DEFAULT '0' COMMENT '求职类型',
  `apply_job_direction` smallint(2) DEFAULT '1' COMMENT '求职方向',
  `position_type` smallint(2) DEFAULT '0' COMMENT '职位类型',
  `apply_job_post` varchar(100) DEFAULT NULL COMMENT '求职岗位',
  `demand` varchar(200) DEFAULT NULL COMMENT '其他需求',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='赢聘订单信息表';

-- ----------------------------
-- Table structure for yxt_order_info
-- ----------------------------
DROP TABLE IF EXISTS `yxt_order_info`;
CREATE TABLE `yxt_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `ico_type` varchar(10) DEFAULT NULL COMMENT '订单图标类型',
  `teacher_sex` smallint(1) DEFAULT NULL COMMENT '老师性别： 1-男 2-女',
  `teacher_identity` smallint(2) DEFAULT NULL COMMENT '教师身份 1-在职老师 2-在校学生 3-机构老师 -4其他',
  `class_type` smallint(1) DEFAULT NULL COMMENT '上课方式 1-老师上门 2-学生上门 3-在线授课 4-协商地点',
  `class_date` varchar(20) DEFAULT NULL COMMENT '上课时间，根据逗号分隔',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `demand` varchar(200) DEFAULT NULL COMMENT '补充说明',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL,
  `address` varchar(200) DEFAULT NULL COMMENT '地址',
  `service_item_show` varchar(20) DEFAULT NULL COMMENT '服务项目',
  `service_type_show` varchar(20) DEFAULT NULL COMMENT '服务类型',
  `service_class_show` varchar(20) DEFAULT NULL COMMENT '服务小类',
  `app_type` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='易学堂订单信息表';

-- ----------------------------
-- Table structure for zsy_order_info
-- ----------------------------
DROP TABLE IF EXISTS `zsy_order_info`;
CREATE TABLE `zsy_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `flzx_consult_manner` smallint(2) DEFAULT '0' COMMENT '法律咨询咨询方式',
  `flzx_consult_direction` smallint(2) DEFAULT '0' COMMENT '法律咨询咨询方向',
  `flzx_detail_content` smallint(2) DEFAULT '0' COMMENT '法律咨询详细内容',
  `sbzl_service_item` smallint(2) DEFAULT '0' COMMENT '商标专利服务项目',
  `htbs_service_item` smallint(2) DEFAULT '0' COMMENT '合同编审服务项目',
  `ssfw_service_item` smallint(2) DEFAULT '0' COMMENT '诉讼服务服务项目',
  `pxgw_service_item` smallint(2) DEFAULT '0' COMMENT '培训顾问服务项目',
  `pxgw_item_detail` smallint(2) DEFAULT '0' COMMENT '培训顾问项目详细',
  `lawyer_operation_duration` smallint(2) DEFAULT '1' COMMENT '律师执业年限',
  `reservation_time` datetime DEFAULT NULL COMMENT '预约时间',
  `demand` varchar(200) DEFAULT NULL COMMENT '其他需求',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='状师爷订单信息表';

-- ----------------------------
-- Table structure for zyb_clean_order_info
-- ----------------------------
DROP TABLE IF EXISTS `zyb_clean_order_info`;
CREATE TABLE `zyb_clean_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '记录在平台中的唯一标识符',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `clean_type` smallint(1) DEFAULT NULL COMMENT '保洁类型 1-家庭保洁 2-新居开荒 3-家居保养',
  `time_limit` smallint(1) DEFAULT NULL COMMENT '保洁期限 1-临时保洁 2-长期保洁',
  `service_item_value` varchar(20) DEFAULT NULL COMMENT '保存项目类型值',
  `clean_area` varchar(20) DEFAULT NULL COMMENT '保洁面积',
  `have_pet` smallint(1) DEFAULT NULL COMMENT '是否有宠物 0-无 1-狗狗 2-猫咪 3-其他',
  `service_time` varchar(30) DEFAULT NULL COMMENT '服务时间',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `address` varchar(100) DEFAULT NULL COMMENT '地址',
  `demand` varchar(200) DEFAULT NULL COMMENT '其他需求',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='左右帮保洁订单信息表';

-- ----------------------------
-- Table structure for zyb_nanny_order_info
-- ----------------------------
DROP TABLE IF EXISTS `zyb_nanny_order_info`;
CREATE TABLE `zyb_nanny_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '记录在平台中的唯一标识符',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `nanny_type` smallint(1) DEFAULT NULL COMMENT '保姆类型 1-长期钟点工 2-住家保姆 3-月嫂 4-育儿嫂 5-看护护理',
  `house_area` varchar(10) DEFAULT NULL COMMENT '房屋面积',
  `service_item` varchar(40) DEFAULT NULL COMMENT '服务项目 1-保洁 2-做饭',
  `diet_predilection` varchar(40) DEFAULT NULL COMMENT '饮食偏好',
  `pre_production_period` varchar(20) DEFAULT NULL COMMENT '预产期',
  `service_frequency` varchar(40) DEFAULT NULL COMMENT '服务频率',
  `baby_sex` smallint(1) DEFAULT NULL COMMENT '宝宝性别 1-男宝宝 2-女宝宝',
  `baby_age` varchar(10) DEFAULT NULL COMMENT '宝宝年龄 1-1-6个月 2-7-12个月',
  `nurse_condition` smallint(1) DEFAULT NULL COMMENT '护理对象 1-男老人 2-女老人 3-男病人 4-女病人  1,2,3的形式存储',
  `care_type` smallint(1) DEFAULT NULL COMMENT '自理状况 1-自理 2-半自理 3-不能自理',
  `have_pet` smallint(2) DEFAULT NULL COMMENT '是否有宠物 0-无 1-狗狗 2-猫咪 3-其他',
  `speculative_price_level` varchar(10) DEFAULT NULL COMMENT '心理价位',
  `service_time` varchar(30) DEFAULT NULL COMMENT '服务时间',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `address` varchar(100) DEFAULT NULL COMMENT '地址',
  `reside_in` smallint(1) DEFAULT NULL COMMENT '是否住家',
  `demand` varchar(200) DEFAULT NULL COMMENT '其他需求',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='左右帮保姆订单信息表';

-- ----------------------------
-- Table structure for zyb_remove_order_info
-- ----------------------------
DROP TABLE IF EXISTS `zyb_remove_order_info`;
CREATE TABLE `zyb_remove_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '记录在平台中的唯一标识符',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `start_site_longitude` double(13,10) DEFAULT NULL COMMENT '出发地经度',
  `start_site_latitude` double(13,10) DEFAULT NULL COMMENT '出发地纬度',
  `start_site` varchar(50) DEFAULT NULL COMMENT '出发地',
  `end_site_longitude` double(13,10) DEFAULT NULL COMMENT '到达地经度',
  `end_site_latitude` double(13,10) DEFAULT NULL COMMENT '到达地纬度',
  `end_site` varchar(50) DEFAULT NULL COMMENT '到达地',
  `start_site_have_elevator` smallint(1) DEFAULT NULL COMMENT '出发地是否有电梯 0-无 1-有',
  `end_site_have_elevator` smallint(1) DEFAULT NULL COMMENT '到发地是否有电梯 0-无 1-有',
  `start_site_floor` varchar(10) DEFAULT NULL COMMENT '出发地楼层',
  `end_site_floor` varchar(10) DEFAULT NULL COMMENT '到达地楼层',
  `service_time` varchar(30) DEFAULT NULL COMMENT '服务时间',
  `move_article` varchar(100) DEFAULT NULL COMMENT '搬运物品',
  `demand` varchar(200) DEFAULT NULL COMMENT '其他需求',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='左右帮搬家订单信息表';

-- ----------------------------
-- Table structure for zyb_repair_order_info
-- ----------------------------
DROP TABLE IF EXISTS `zyb_repair_order_info`;
CREATE TABLE `zyb_repair_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '记录在平台中的唯一标识符',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `repair_content` smallint(2) DEFAULT NULL COMMENT '维修内容 1-手机维修 2-电脑维修',
  `service_time` varchar(30) DEFAULT NULL COMMENT '维修时间',
  `visit_service` smallint(1) DEFAULT NULL COMMENT '上门服务 0-不需要 1-需要',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL,
  `address` varchar(100) DEFAULT NULL COMMENT '地址',
  `demand` varchar(200) DEFAULT NULL COMMENT '其他需求',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='左右帮维修订单信息表';

-- ----------------------------
-- Table structure for zyb_wash_order_info
-- ----------------------------
DROP TABLE IF EXISTS `zyb_wash_order_info`;
CREATE TABLE `zyb_wash_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '记录在平台中的唯一标识符',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `wash_type` varchar(40) DEFAULT NULL COMMENT '洗护类型 1-日常洗衣 2-鞋靴皮革 3-家纺家居 4-奢护保养',
  `service_time` varchar(30) DEFAULT NULL COMMENT '洗衣时间',
  `wash_count` int(5) DEFAULT NULL COMMENT '件数',
  `visit_service` smallint(1) DEFAULT NULL COMMENT '上门服务 0-不需要 1-需要',
  `demand` varchar(200) DEFAULT NULL COMMENT '详细描述',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `address` varchar(100) DEFAULT NULL COMMENT '地址',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='左右帮洗衣订单信息表';

-- ----------------------------
-- Table structure for zyd_order_info
-- ----------------------------
DROP TABLE IF EXISTS `zyd_order_info`;
CREATE TABLE `zyd_order_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(18) NOT NULL COMMENT '订单ID',
  `webSite_development_platform` smallint(2) DEFAULT '0' COMMENT '开发平台。1PC网站，2WAP网站',
  `webSite_type` smallint(2) DEFAULT '0' COMMENT '网站类型。1电商网站，2企业政府官网，3社交网站，4其他',
  `make_type` smallint(2) DEFAULT '0' COMMENT '制作类型。1前端切图，2响应布局，3网页特效设计，4前端交互设计，5web前端，6前端优化，7其他',
  `soft_type` smallint(2) DEFAULT '0' COMMENT '软件类型。1 企业软件ERP，2办公自动化OA系统，3客户关系管理CRM，4CMS系统开发',
  `mobile_development_platform` varchar(20) DEFAULT '0' COMMENT '移动端开发平台。1安卓，2IOS，3微信开发',
  `product_direction` smallint(2) DEFAULT '0' COMMENT '产品方向。1游戏类，2电商类，3社交类，4生活类，5工具类，6影音类，7其他',
  `development_budge` varchar(10) DEFAULT NULL COMMENT '开发预算',
  `finish_time` varchar(10) DEFAULT NULL COMMENT '完成时间。1 1周内完成，2 1-2周，3 2-4周，4 4周以上',
  `service_time` datetime DEFAULT NULL COMMENT '服务时间',
  `longitude` double(13,10) DEFAULT NULL COMMENT '经度',
  `latitude` double(13,10) DEFAULT NULL COMMENT '纬度',
  `address` varchar(200) DEFAULT NULL COMMENT '地址',
  `demand` varchar(200) DEFAULT NULL COMMENT '需求补充',
  PRIMARY KEY (`id`),
  KEY `订单ID` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='再云端订单信息表';

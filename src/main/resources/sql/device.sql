CREATE TABLE `device` (
`dev_id` varchar(64) NOT NULL COMMENT '设备编码',
`openid` varchar(64) NOT NULL COMMENT '微信用户openid',
`dev_name` varchar(64) NOT NULL COMMENT '设备名称',
`type` tinyint(1) NOT NULL COMMENT '设备类型 1：太阳能款 2:：插电款',
`install_height` int NOT NULL COMMENT '安装高度 单位：厘米 范围0.1~2.9米之间',
`distance` int NOT NULL COMMENT '传感器满液位距离 单位：厘米 范围范围最低0.1米',
`upper_limit` int NOT NULL COMMENT '上限设置 单位：百分比值，10%，存储10 范围10～100',
`lower_limit` int NOT NULL default 0 COMMENT '下限设置 范围0～90',
`low_energy_switch` tinyint(1) NOT NULL default 1 COMMENT '低能耗开关 默认开启，用户不可设置',
`drainage_model` tinyint(1) NOT NULL default 1 COMMENT '排水模式',
`deleted` tinyint(1) NOT NULL default 0 COMMENT '删除状态 0：未删除  1：已删除',
`create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
`modify_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改日期',
PRIMARY KEY (`dev_id`),
INDEX idx_openid(`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci;

alter table device add COLUMN drainage_model tinyint(1) NOT NULL default 1 COMMENT '排水模式';

CREATE TABLE `device_record` (
`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
`dev_id` varchar(64) NOT NULL COMMENT '设备编码',
`liquid_height` int DEFAULT NULL COMMENT '液位高度',
`liquid_percent` int DEFAULT NULL COMMENT '液位百分比',
`liquid_status` tinyint(1) NOT NULL COMMENT '液位状态 0 正常， 1，低液位报警， 2，高液位报警',
`create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
PRIMARY KEY (`id`),
INDEX idx_dev_id_create_time(`dev_id`,`create_time`)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci;


CREATE TABLE `user` (
`openid` varchar(64) NOT NULL COMMENT '微信用户openid',
`user_name` varchar(64) DEFAULT NULL COMMENT '微信用户名称',
`language` varchar(32) DEFAULT NULL COMMENT '语言',
`remind_switch` tinyint(1) NOT NULL default 1 COMMENT '公众号提醒开关 0:关   1:开',
`create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
`modify_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改日期',
PRIMARY KEY (`openid`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE `notify_record` (
`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
`openid` varchar(64) NOT NULL COMMENT '微信用户openid',
`dev_id` varchar(64) NOT NULL COMMENT '设备编码',
`notify_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '通知时间',
PRIMARY KEY (`id`),
INDEX idx_openid_dev_id(`openid`,`dev_id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci;


-- 事件调度器
-- 首先确保事件调度器已启用
SET GLOBAL event_scheduler = ON;

-- 创建每天凌晨1点删除5天前数据的事件
DELIMITER //
CREATE EVENT clear_device_record
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_TIMESTAMP + INTERVAL 1 DAY + INTERVAL 3 HOUR
DO
DELETE FROM device_record WHERE create_time < DATE_SUB(NOW(), INTERVAL 5 DAY);
//
DELIMITER ;

-- 查看已有事件
SHOW EVENTS;
-- 查看事件详情
SHOW create EVENT clear_device_record;
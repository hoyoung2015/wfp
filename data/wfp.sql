/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50626
Source Host           : localhost:3306
Source Database       : wfp

Target Server Type    : MYSQL
Target Server Version : 50626
File Encoding         : 65001

Date: 2015-11-10 17:20:45
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for baobiao
-- ----------------------------
DROP TABLE IF EXISTS `baobiao`;
CREATE TABLE `baobiao` (
  `stock_code` varchar(10) NOT NULL,
  `name` varchar(125) DEFAULT NULL,
  `sname` varchar(125) DEFAULT NULL,
  `area` varchar(125) DEFAULT NULL,
  `industry` varchar(125) DEFAULT NULL,
  `travel_100` bigint(21) DEFAULT NULL,
  `travel_90` bigint(21) DEFAULT NULL,
  `travel_80` bigint(21) DEFAULT NULL,
  `travel_70` bigint(21) DEFAULT NULL,
  `travel_60` bigint(21) DEFAULT NULL,
  `travel_50` bigint(21) DEFAULT NULL,
  `travel_40` bigint(21) DEFAULT NULL,
  `travel_30` bigint(21) DEFAULT NULL,
  `travel_20` bigint(21) DEFAULT NULL,
  `travel_10` bigint(21) DEFAULT NULL,
  `travel_5` bigint(21) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for company_info
-- ----------------------------
DROP TABLE IF EXISTS `company_info`;
CREATE TABLE `company_info` (
  `stock_code` varchar(10) NOT NULL,
  `sname` varchar(125) DEFAULT NULL,
  `area` varchar(125) DEFAULT NULL,
  `pos_x` float(9,6) DEFAULT NULL,
  `pos_y` float(9,6) DEFAULT NULL,
  `industry` varchar(125) DEFAULT NULL,
  `institutional` decimal(12,2) DEFAULT NULL,
  `lootchips` decimal(12,2) DEFAULT NULL,
  `pricelimit` decimal(12,2) DEFAULT NULL,
  `shareholders` decimal(12,2) DEFAULT NULL,
  `name` varchar(125) DEFAULT NULL,
  `ename` varchar(125) DEFAULT NULL,
  `register_date` date DEFAULT NULL,
  `addr_reg` varchar(255) DEFAULT NULL,
  `addr_work` varchar(255) DEFAULT NULL,
  `market` varchar(125) DEFAULT NULL,
  `listing_date` date DEFAULT NULL,
  `offer_date` date DEFAULT NULL,
  `stock_type` varchar(45) DEFAULT NULL,
  `id` int(11) NOT NULL,
  `addr` varchar(255) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `web_site` varchar(255) DEFAULT NULL,
  `reg_capital` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`stock_code`),
  KEY `ix_addr` (`addr`),
  KEY `ix_industry` (`industry`),
  KEY `ix_name` (`name`),
  KEY `ix_sname` (`sname`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for company_patents
-- ----------------------------
DROP TABLE IF EXISTS `company_patents`;
CREATE TABLE `company_patents` (
  `patentsId` int(11) NOT NULL AUTO_INCREMENT,
  `comName` varchar(255) DEFAULT NULL,
  `patCode` varchar(255) DEFAULT NULL,
  `patInfo` text,
  `patMainStdmode` varchar(255) DEFAULT NULL,
  `patName` varchar(255) DEFAULT NULL,
  `patStdmode` varchar(255) DEFAULT NULL,
  `source` varchar(255) DEFAULT NULL,
  `green` tinyint(1) DEFAULT '0',
  `remarks` varchar(255) DEFAULT NULL,
  `stockCode` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`patentsId`)
) ENGINE=InnoDB AUTO_INCREMENT=10939 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for com_hporg
-- ----------------------------
DROP TABLE IF EXISTS `com_hporg`;
CREATE TABLE `com_hporg` (
  `stock_code` varchar(10) NOT NULL,
  `hporg_id` int(11) NOT NULL,
  `distance1` float DEFAULT NULL,
  `distance2` float DEFAULT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=98562 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for com_org
-- ----------------------------
DROP TABLE IF EXISTS `com_org`;
CREATE TABLE `com_org` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `stock_code` varchar(45) DEFAULT NULL,
  `green_org_id` int(11) DEFAULT NULL,
  `distance1` int(11) DEFAULT '0',
  `distance2` int(11) DEFAULT '0',
  `distance3` int(11) DEFAULT '0',
  `distance4` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=217545 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for com_org_dis
-- ----------------------------
DROP TABLE IF EXISTS `com_org_dis`;
CREATE TABLE `com_org_dis` (
  `stock_code` varchar(10) NOT NULL,
  `id` int(11) DEFAULT NULL,
  `dis` float DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for green_org
-- ----------------------------
DROP TABLE IF EXISTS `green_org`;
CREATE TABLE `green_org` (
  `id` int(11) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `lng` float(9,6) DEFAULT NULL,
  `lat` float(9,6) DEFAULT NULL,
  `province` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `area` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for hporg
-- ----------------------------
DROP TABLE IF EXISTS `hporg`;
CREATE TABLE `hporg` (
  `id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `province` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `area` varchar(255) DEFAULT NULL,
  `pos_x` float(9,6) DEFAULT NULL,
  `pos_y` float(9,6) DEFAULT NULL,
  `addr` varchar(255) DEFAULT NULL,
  `cert_level` varchar(255) DEFAULT NULL,
  `cert_no` varchar(255) DEFAULT NULL,
  `eval_range` varchar(255) DEFAULT NULL,
  `tel` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `project_num` int(11) DEFAULT NULL,
  `permit_num` int(11) DEFAULT NULL,
  `engineer_num` int(11) DEFAULT NULL,
  `record_num` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for new_item
-- ----------------------------
DROP TABLE IF EXISTS `new_item`;
CREATE TABLE `new_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `create_date` datetime DEFAULT NULL,
  `publish_date` datetime DEFAULT NULL,
  `query` varchar(255) DEFAULT NULL,
  `sourceName` varchar(255) DEFAULT NULL,
  `stock_code` varchar(255) DEFAULT NULL,
  `summary` text,
  `target_html` longtext,
  `target_url` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_penfdx47m3j9l98cppcxw6va1` (`target_url`),
  KEY `ix_target_url` (`target_url`),
  KEY `ix_title` (`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for social_report
-- ----------------------------
DROP TABLE IF EXISTS `social_report`;
CREATE TABLE `social_report` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `create_date` datetime DEFAULT NULL,
  `emp_resp` text,
  `equity_resp` text,
  `gd_resp` text,
  `hj_resp` text,
  `publish_date` varchar(255) DEFAULT NULL,
  `social_resp` text,
  `stock_code` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_i44ohgos65sg5wo823x206vr1` (`stock_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for social_report_syn
-- ----------------------------
DROP TABLE IF EXISTS `social_report_syn`;
CREATE TABLE `social_report_syn` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `create_date` datetime DEFAULT NULL,
  `emp_score` float DEFAULT NULL,
  `equity_score` float DEFAULT NULL,
  `gd_score` float DEFAULT NULL,
  `hj_score` float DEFAULT NULL,
  `level` varchar(255) DEFAULT NULL,
  `publish_date` varchar(255) DEFAULT NULL,
  `report_file_url` varchar(255) DEFAULT NULL,
  `social_score` float DEFAULT NULL,
  `stock_code` varchar(255) DEFAULT NULL,
  `total_score` float DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_86uxdde0kdu1sq2bbx4nvrgh5` (`stock_code`)
) ENGINE=InnoDB AUTO_INCREMENT=2813 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tag_meta
-- ----------------------------
DROP TABLE IF EXISTS `tag_meta`;
CREATE TABLE `tag_meta` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `create_date` datetime DEFAULT NULL,
  `tag` varchar(255) DEFAULT NULL,
  `tag_value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Function structure for cal_dis
-- ----------------------------
DROP FUNCTION IF EXISTS `cal_dis`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `cal_dis`(lon1 FLOAT,lat1 FLOAT,lon2 FLOAT,lat2 FLOAT) RETURNS float
BEGIN
DECLARE ew1,ns1,ew2,ns2,dx,dy,dew,distance DOUBLE;
DECLARE DEF_PI180 DOUBLE DEFAULT 0.01745329252;
DECLARE DEF_PI DOUBLE DEFAULT 3.14159265359;
DECLARE DEF_2PI DOUBLE DEFAULT 6.28318530712;
DECLARE DEF_R DOUBLE DEFAULT 6370693.5;
SET ew1 = lon1 * DEF_PI180;
SET ns1 = lat1 * DEF_PI180;
SET ew2 = lon2 * DEF_PI180;
SET ns2 = lat2 * DEF_PI180;
SET dew = ew1 - ew2;
IF dew > DEF_PI THEN 
	SET dew = DEF_2PI - dew;
ELSEIF dew < -DEF_PI THEN 
	SET dew = DEF_2PI + dew;
END IF;
SET dx = DEF_R * COS(ns1) * dew; -- 东西方向长度(在纬度圈上的投影长度)
SET dy = DEF_R * (ns1 - ns2); -- 南北方向长度(在经度圈上的投影长度)
SET distance = SQRT(dx * dx + dy * dy);
RETURN distance;
END
;;
DELIMITER ;

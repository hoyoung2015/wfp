/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50626
Source Host           : localhost:3306
Source Database       : wfp

Target Server Type    : MYSQL
Target Server Version : 50626
File Encoding         : 65001

Date: 2015-11-29 20:52:02
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for company_info
-- ----------------------------
DROP TABLE IF EXISTS `company_info`;
CREATE TABLE `company_info` (
  `stock_code` varchar(10) NOT NULL,
  `sname` varchar(125) DEFAULT NULL,
  `area` varchar(125) DEFAULT NULL,
  `pos_x` float DEFAULT NULL,
  `pos_y` float DEFAULT NULL,
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
  `addr` varchar(255) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `web_site` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`stock_code`)
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
) ENGINE=MyISAM AUTO_INCREMENT=113014 DEFAULT CHARSET=utf8;

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
-- Table structure for com_org_dis_avg
-- ----------------------------
DROP TABLE IF EXISTS `com_org_dis_avg`;
CREATE TABLE `com_org_dis_avg` (
  `stock_code` varchar(10) NOT NULL DEFAULT '',
  `avg_dis` int(11) DEFAULT NULL,
  PRIMARY KEY (`stock_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for green_org
-- ----------------------------
DROP TABLE IF EXISTS `green_org`;
CREATE TABLE `green_org` (
  `id` int(11) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `lng` float DEFAULT NULL,
  `lat` float DEFAULT NULL,
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
  `stock_code` varchar(255) NOT NULL,
  `emp_score` float DEFAULT NULL,
  `equity_score` float DEFAULT NULL,
  `gd_score` float DEFAULT NULL,
  `hj_score` float DEFAULT NULL,
  `level` varchar(255) DEFAULT NULL,
  `publish_date` varchar(255) NOT NULL,
  `report_file_url` varchar(255) DEFAULT NULL,
  `social_score` float DEFAULT NULL,
  `total_score` float DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  PRIMARY KEY (`stock_code`,`publish_date`),
  KEY `ix_publish_date` (`publish_date`),
  KEY `UK_86uxdde0kdu1sq2bbx4nvrgh5` (`stock_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
CREATE DEFINER=`root`@`localhost` FUNCTION `cal_dis`(lng2 FLOAT,lat2 FLOAT,lng1 FLOAT,lat1 FLOAT) RETURNS float
BEGIN
DECLARE rc,rj INT;
DECLARE m_RadLo2,m_RadLa2,m_RadLo1,m_RadLa1,dLo,dLa,dx,dy,ec,ed,p DOUBLE;
DECLARE distance FLOAT;
SET rc = 6378137;
SET rj = 6356725;
SET p = 3.141592653589793;
SET m_RadLa2 = p*lat2/180.0;
SET m_RadLo2 = p*lng2/180.0;
SET m_RadLa1 = p*lat1/180.0;
SET m_RadLo1 = p*lng1/180.0;
SET ec = rj+(rc-rj)*(90.0-lat1)/90.0;
SET ed = ec*COS(m_RadLa1);
SET dx = (m_RadLo2-m_RadLo1)*ed;
SET dy = (m_RadLa2-m_RadLa1)*ec;
SET distance = SQRT(POW(dx,2)+POW(dy,2));
RETURN distance;
END
;;
DELIMITER ;

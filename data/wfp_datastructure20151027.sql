-- MySQL dump 10.13  Distrib 5.6.24, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: wfp
-- ------------------------------------------------------
-- Server version	5.6.26

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `com_org`
--

DROP TABLE IF EXISTS `com_org`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `company_info`
--

DROP TABLE IF EXISTS `company_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  PRIMARY KEY (`stock_code`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `green_org`
--

DROP TABLE IF EXISTS `green_org`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `green_org` (
  `id` int(11) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `lng` float DEFAULT NULL,
  `lat` float DEFAULT NULL,
  `province` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `area` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `new_item`
--

DROP TABLE IF EXISTS `new_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `social_report`
--

DROP TABLE IF EXISTS `social_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `social_report_syn`
--

DROP TABLE IF EXISTS `social_report_syn`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tag_meta`
--

DROP TABLE IF EXISTS `tag_meta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tag_meta` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `create_date` datetime DEFAULT NULL,
  `tag` varchar(255) DEFAULT NULL,
  `tag_value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping events for database 'wfp'
--

--
-- Dumping routines for database 'wfp'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-10-27 17:22:03

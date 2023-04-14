-- MySQL dump 10.13  Distrib 5.7.12, for Win64 (x86_64)
--
-- Host: localhost    Database: lithium_translate
-- ------------------------------------------------------
-- Server version	5.7.13-log

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
-- Table structure for table `language`
--

-- DROP TABLE IF EXISTS `language`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `language` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `enabled` bit(1) NOT NULL,
  `locale2` varchar(255) DEFAULT NULL,
  `locale3` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_language_enabled` (`enabled`),
  KEY `idx_language_locale2` (`locale2`,`enabled`),
  KEY `idx_language_locale3` (`locale3`,`enabled`)
) ENGINE=InnoDB AUTO_INCREMENT=186 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `latest_change_set`
--

-- DROP TABLE IF EXISTS `latest_change_set`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `latest_change_set` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `apply_date` datetime NOT NULL,
  `change_number` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  `language_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_lcs_uniq` (`name`,`language_id`),
  KEY `FK9m7oyg58dyre4q7f7v3r1mva3` (`language_id`),
  CONSTRAINT `FK9m7oyg58dyre4q7f7v3r1mva3` FOREIGN KEY (`language_id`) REFERENCES `language` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `namespace`
--

-- DROP TABLE IF EXISTS `namespace`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `namespace` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_ns_uniq` (`parent_id`,`code`),
  CONSTRAINT `FKiewe75hd2d5jbn94c10kx5gff` FOREIGN KEY (`parent_id`) REFERENCES `namespace` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=183 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `translation_key`
--

-- DROP TABLE IF EXISTS `translation_key`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `translation_key` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `key_code` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `namespace_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_trans_key` (`namespace_id`,`key_code`),
  CONSTRAINT `FK1mfb2u61daqmm8yp8r038ghbr` FOREIGN KEY (`namespace_id`) REFERENCES `namespace` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=412 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `translation_value`
--

-- DROP TABLE IF EXISTS `translation_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `translation_value` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` int(11) NOT NULL,
  `current_id` bigint(20) DEFAULT NULL,
  `default_value_id` bigint(20) DEFAULT NULL,
  `key_id` bigint(20) DEFAULT NULL,
  `language_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_tv_unique` (`key_id`,`language_id`),
  KEY `FK6ut3l2ip9ddfctfyk4300rdu7` (`current_id`),
  KEY `FKb14ducgq4s5vghwky14lchtas` (`default_value_id`),
  KEY `FKamoxtmus46b41fix6hup1qqic` (`language_id`),
  CONSTRAINT `FK23tq3l9seqnawyqtx4gwmweku` FOREIGN KEY (`key_id`) REFERENCES `translation_key` (`id`),
  CONSTRAINT `FK6ut3l2ip9ddfctfyk4300rdu7` FOREIGN KEY (`current_id`) REFERENCES `translation_value_revision` (`id`),
  CONSTRAINT `FKamoxtmus46b41fix6hup1qqic` FOREIGN KEY (`language_id`) REFERENCES `language` (`id`),
  CONSTRAINT `FKb14ducgq4s5vghwky14lchtas` FOREIGN KEY (`default_value_id`) REFERENCES `translation_value_default` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=412 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `translation_value_default`
--

-- DROP TABLE IF EXISTS `translation_value_default`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `translation_value_default` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NOT NULL,
  `translation_value_id` bigint(20) DEFAULT NULL,
  `value` longtext NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=412 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `translation_value_revision`
--

-- DROP TABLE IF EXISTS `translation_value_revision`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `translation_value_revision` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `author` varchar(255) NOT NULL,
  `created_date` datetime NOT NULL,
  `translation_value_id` bigint(20) DEFAULT NULL,
  `value` longtext NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-11-03 16:58:27

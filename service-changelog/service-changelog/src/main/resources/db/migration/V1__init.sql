-- MySQL dump 10.13  Distrib 5.7.12, for Win64 (x86_64)
--
-- Host: localhost    Database: lithium_changelog
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
-- Table structure for table `change_log`
--

-- DROP TABLE IF EXISTS `change_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `change_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `additional_info` varchar(255) DEFAULT NULL,
  `change_date` datetime NOT NULL,
  `comments` varchar(255) DEFAULT NULL,
  `complete` bit(1) NOT NULL,
  `entity_record_id` bigint(20) NOT NULL,
  `translate_field_changes` int(11) DEFAULT NULL,
  `author_user_id` bigint(20) NOT NULL,
  `change_log_entity_id` bigint(20) NOT NULL,
  `change_log_type_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_changelog_targetbydate` (`change_log_entity_id`,`entity_record_id`,`change_date`),
  KEY `idx_changelog_authorbydate` (`change_date`,`author_user_id`),
  KEY `idx_changelog_entityrecordid` (`entity_record_id`),
  KEY `FKhbi7dwtthjymxx6xru9k6bome` (`author_user_id`),
  KEY `FKbsu5ttxt9693n80fasamgp9sw` (`change_log_type_id`),
  CONSTRAINT `FKbsu5ttxt9693n80fasamgp9sw` FOREIGN KEY (`change_log_type_id`) REFERENCES `change_log_type` (`id`),
  CONSTRAINT `FKbyi7he3qoy2s1qs5x831of6ec` FOREIGN KEY (`change_log_entity_id`) REFERENCES `change_log_entity` (`id`),
  CONSTRAINT `FKhbi7dwtthjymxx6xru9k6bome` FOREIGN KEY (`author_user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `change_log_entity`
--

-- DROP TABLE IF EXISTS `change_log_entity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `change_log_entity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_9o69jifat5vme9bu9kc823fma` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `change_log_field_change`
--

-- DROP TABLE IF EXISTS `change_log_field_change`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `change_log_field_change` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `field` varchar(255) NOT NULL,
  `from_value` longtext,
  `to_value` longtext,
  `change_log_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKlow3uiqba2jflb0vwdep1ptur` (`change_log_id`),
  CONSTRAINT `FKlow3uiqba2jflb0vwdep1ptur` FOREIGN KEY (`change_log_id`) REFERENCES `change_log` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `change_log_type`
--

-- DROP TABLE IF EXISTS `change_log_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `change_log_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ng5uer9qc1a0sfj1jqglwe98f` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `domain`
--

-- DROP TABLE IF EXISTS `domain`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domain` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_domain_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

-- DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `guid` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_guid` (`guid`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-11-03 17:26:43

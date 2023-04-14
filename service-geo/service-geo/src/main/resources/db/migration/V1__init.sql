-- MySQL dump 10.16  Distrib 10.1.13-MariaDB, for osx10.11 (x86_64)
--
-- Host: localhost    Database: lithium_geo
-- ------------------------------------------------------
-- Server version	10.1.13-MariaDB

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
-- Table structure for table `admin_level1`
--

DROP TABLE IF EXISTS `admin_level1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `admin_level1` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) DEFAULT NULL,
  `enabled` bit(1) DEFAULT NULL,
  `manual` bit(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `country_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_code` (`code`),
  KEY `idx_name` (`name`),
  KEY `FKcp9kdqlree1bsklkes6y4vs1f` (`country_id`),
  CONSTRAINT `FKcp9kdqlree1bsklkes6y4vs1f` FOREIGN KEY (`country_id`) REFERENCES `country` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_level1`
--

LOCK TABLES `admin_level1` WRITE;
/*!40000 ALTER TABLE `admin_level1` DISABLE KEYS */;
/*!40000 ALTER TABLE `admin_level1` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_level2`
--

DROP TABLE IF EXISTS `admin_level2`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `admin_level2` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) DEFAULT NULL,
  `enabled` bit(1) DEFAULT NULL,
  `manual` bit(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `level1_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_code` (`code`),
  KEY `idx_name` (`name`),
  KEY `FKf5o0hxgkeo14m9eydhspt9h7c` (`level1_id`),
  CONSTRAINT `FKf5o0hxgkeo14m9eydhspt9h7c` FOREIGN KEY (`level1_id`) REFERENCES `admin_level1` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_level2`
--

LOCK TABLES `admin_level2` WRITE;
/*!40000 ALTER TABLE `admin_level2` DISABLE KEYS */;
/*!40000 ALTER TABLE `admin_level2` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `city`
--

DROP TABLE IF EXISTS `city`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `city` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `enabled` bit(1) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `manual` bit(1) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `population` bigint(20) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `country_id` bigint(20) DEFAULT NULL,
  `level1_id` bigint(20) DEFAULT NULL,
  `level2_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_code` (`code`),
  UNIQUE KEY `idx_all` (`code`,`country_id`),
  KEY `idx_name` (`name`),
  KEY `idx_lat` (`latitude`),
  KEY `idx_lon` (`longitude`),
  KEY `FKrpd7j1p7yxr784adkx4pyepba` (`country_id`),
  KEY `FKg8854shux4aaqh433hjgyoakx` (`level1_id`),
  KEY `FKrvcpjds8pdrjp7picxeqy0009` (`level2_id`),
  CONSTRAINT `FKg8854shux4aaqh433hjgyoakx` FOREIGN KEY (`level1_id`) REFERENCES `admin_level1` (`id`),
  CONSTRAINT `FKrpd7j1p7yxr784adkx4pyepba` FOREIGN KEY (`country_id`) REFERENCES `country` (`id`),
  CONSTRAINT `FKrvcpjds8pdrjp7picxeqy0009` FOREIGN KEY (`level2_id`) REFERENCES `admin_level2` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `city`
--

LOCK TABLES `city` WRITE;
/*!40000 ALTER TABLE `city` DISABLE KEYS */;
/*!40000 ALTER TABLE `city` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `country`
--

DROP TABLE IF EXISTS `country`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `country` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `capital` varchar(255) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `continent` varchar(255) DEFAULT NULL,
  `currency_code` varchar(255) DEFAULT NULL,
  `currency_name` varchar(255) DEFAULT NULL,
  `enabled` bit(1) DEFAULT NULL,
  `equivalent_fips` varchar(255) DEFAULT NULL,
  `fips` varchar(255) DEFAULT NULL,
  `iso3` varchar(255) DEFAULT NULL,
  `iso_nr` int(11) DEFAULT NULL,
  `languages` varchar(255) DEFAULT NULL,
  `manual` bit(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `neighbours` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `population` bigint(20) DEFAULT NULL,
  `postal_code_format` varchar(255) DEFAULT NULL,
  `postal_code_regex` varchar(255) DEFAULT NULL,
  `sqkm` bigint(20) DEFAULT NULL,
  `top_level_domain` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_code` (`code`),
  UNIQUE KEY `idx_iso3` (`iso3`),
  UNIQUE KEY `idx_fips` (`fips`),
  UNIQUE KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `country`
--

LOCK TABLES `country` WRITE;
/*!40000 ALTER TABLE `country` DISABLE KEYS */;
/*!40000 ALTER TABLE `country` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `file_update`
--

DROP TABLE IF EXISTS `file_update`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `file_update` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_date` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `file_update`
--

LOCK TABLES `file_update` WRITE;
/*!40000 ALTER TABLE `file_update` DISABLE KEYS */;
/*!40000 ALTER TABLE `file_update` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-10-01 10:56:05

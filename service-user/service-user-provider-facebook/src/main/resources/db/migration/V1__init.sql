-- MySQL dump 10.13  Distrib 5.7.21, for osx10.13 (x86_64)
--
-- Host: localhost    Database: lithium_user_vipps
-- ------------------------------------------------------
-- Server version	5.7.21

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
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `address_line1` varchar(255) DEFAULT NULL,
  `address_line2` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `zip_code` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `auth_attempt`
--
DROP TABLE IF EXISTS `auth_attempt`;
CREATE TABLE `auth_attempt` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`created` datetime DEFAULT NULL,
`expires_on` datetime DEFAULT NULL,
`provided_access_token` varchar(2083) DEFAULT NULL,
`x_request_id` varchar(255) DEFAULT NULL,
`url` varchar(2083) DEFAULT NULL,
`callback_request_id` bigint(20) DEFAULT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `idx_uat_requestid` (`x_request_id`),
KEY `FK2r2fvp454a7hw6qr583ewwnsg` (`callback_request_id`),
CONSTRAINT `FK2r2fvp454a7hw6qr583ewwnsg` FOREIGN KEY (`callback_request_id`) REFERENCES `callback_request` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `callback_request`
--

DROP TABLE IF EXISTS `callback_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `callback_request` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `request_id` varchar(255) NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `error_info_id` bigint(20) DEFAULT NULL,
  `user_details_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_uat_requestid` (`request_id`),
  KEY `FK9d97ro7dnje4absp82akebmu4` (`error_info_id`),
  KEY `FKc1tct38dc544f9gn7fx7d2n0i` (`user_details_id`),
  CONSTRAINT `FK9d97ro7dnje4absp82akebmu4` FOREIGN KEY (`error_info_id`) REFERENCES `error_info` (`id`),
  CONSTRAINT `FKc1tct38dc544f9gn7fx7d2n0i` FOREIGN KEY (`user_details_id`) REFERENCES `user_details` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `callback_request`
--

LOCK TABLES `callback_request` WRITE;
/*!40000 ALTER TABLE `callback_request` DISABLE KEYS */;
/*!40000 ALTER TABLE `callback_request` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `error_info`
--

DROP TABLE IF EXISTS `error_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `error_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `error_code` varchar(255) DEFAULT NULL,
  `error_group` varchar(255) DEFAULT NULL,
  `error_message` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `error_info`
--

LOCK TABLES `error_info` WRITE;
/*!40000 ALTER TABLE `error_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `error_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`created` datetime DEFAULT NULL,
`deleted` bit(1) NOT NULL,
`domain_name` varchar(255) DEFAULT NULL,
`user_id` varchar(255) DEFAULT NULL,
`current_auth_attempt_id` bigint(20) DEFAULT NULL,
`current_user_details_id` bigint(20) DEFAULT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `idx_u_all` (`domain_name`,`user_id`),
KEY `FK7g3r0wwoj9lo3y2hpeoegh39q` (`current_auth_attempt_id`),
KEY `FKf8j0t432ob1ud5ruk5mxv2n4q` (`current_user_details_id`),
CONSTRAINT `FK7g3r0wwoj9lo3y2hpeoegh39q` FOREIGN KEY (`current_auth_attempt_id`) REFERENCES `auth_attempt` (`id`),
CONSTRAINT `FKf8j0t432ob1ud5ruk5mxv2n4q` FOREIGN KEY (`current_user_details_id`) REFERENCES `user_details` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `user_details`
--

DROP TABLE IF EXISTS `user_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bank_id_verified` varchar(255) DEFAULT NULL,
  `date_of_birth` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `mobile_number` varchar(255) DEFAULT NULL,
  `ssn` varchar(255) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `address_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2j3d435pe9j2ajtoxfgpcj4i` (`address_id`),
  CONSTRAINT `FK2j3d435pe9j2ajtoxfgpcj4i` FOREIGN KEY (`address_id`) REFERENCES `address` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_details`
--

LOCK TABLES `user_details` WRITE;
/*!40000 ALTER TABLE `user_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'lithium_user_vipps'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-05-10  8:42:23

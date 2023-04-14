-- MySQL dump 10.13  Distrib 5.7.12, for Win64 (x86_64)
--
-- Host: localhost    Database: lithium_user
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
-- Table structure for table `address`
--

-- DROP TABLE IF EXISTS `address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `address_line1` varchar(255) NOT NULL,
  `address_line2` varchar(255) DEFAULT NULL,
  `address_line3` varchar(255) DEFAULT NULL,
  `admin_level1` varchar(255) DEFAULT NULL,
  `admin_level1code` varchar(10) DEFAULT NULL,
  `city` varchar(255) NOT NULL,
  `city_code` varchar(10) DEFAULT NULL,
  `country` varchar(255) NOT NULL,
  `country_code` varchar(10) DEFAULT NULL,
  `postal_code` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_city` (`city`),
  KEY `idx_cityCode` (`city_code`),
  KEY `idx_adminLevel1` (`admin_level1`),
  KEY `idx_adminLevel1Code` (`admin_level1code`),
  KEY `idx_country` (`country`),
  KEY `idx_countryCode` (`country_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `category`
--

-- DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_category_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
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
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_domain_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `grd`
--

-- DROP TABLE IF EXISTS `grd`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `grd` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `descending` bit(1) DEFAULT NULL,
  `self_applied` bit(1) DEFAULT NULL,
  `domain_id` bigint(20) DEFAULT NULL,
  `group_id` bigint(20) DEFAULT NULL,
  `role_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8vsf8qs32s3j1st9n63p1i05i` (`domain_id`),
  KEY `FKm4atd1dtvxqx4ix3abmmawxek` (`group_id`),
  KEY `FKq66uoyhhfarl7vtb2t2idoefx` (`role_id`),
  CONSTRAINT `FK8vsf8qs32s3j1st9n63p1i05i` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`),
  CONSTRAINT `FKm4atd1dtvxqx4ix3abmmawxek` FOREIGN KEY (`group_id`) REFERENCES `group_table` (`id`),
  CONSTRAINT `FKq66uoyhhfarl7vtb2t2idoefx` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `group_table`
--

-- DROP TABLE IF EXISTS `group_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `group_table` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deleted` bit(1) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `enabled` bit(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `domain_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_group_name_domain` (`name`,`domain_id`),
  KEY `FKcn68tp7op2akwqiyy5rj6isd4` (`domain_id`),
  CONSTRAINT `FKcn68tp7op2akwqiyy5rj6isd4` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `label`
--

-- DROP TABLE IF EXISTS `label`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `label` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `label_eng` varchar(255) NOT NULL,
  `label_key` varchar(255) NOT NULL,
  `system_key` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_label_key` (`label_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role`
--

-- DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `category_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_role_role` (`role`),
  KEY `FKhp4rxponpav7lrbt3h8lshin2` (`category_id`),
  CONSTRAINT `FKhp4rxponpav7lrbt3h8lshin2` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `status`
--

-- DROP TABLE IF EXISTS `status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `status` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deleted` bit(1) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(35) NOT NULL,
  `user_enabled` bit(1) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_reccgx9nr0a8dwv201t44l6pd` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `transaction_type_account`
--

-- DROP TABLE IF EXISTS `transaction_type_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction_type_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_type_code` varchar(255) NOT NULL,
  `credit` bit(1) NOT NULL,
  `debit` bit(1) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_tta_all` (`account_type_code`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

-- DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cellphone_number` varchar(255) DEFAULT NULL,
  `comments` longtext,
  `country_code` varchar(255) DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `deleted` bit(1) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `first_name` varchar(35) NOT NULL,
  `last_name` varchar(35) NOT NULL,
  `password` varchar(255) NOT NULL,
  `password_updated` datetime DEFAULT NULL,
  `password_updated_by` varchar(255) DEFAULT NULL,
  `telephone_number` varchar(255) DEFAULT NULL,
  `updated_date` datetime NOT NULL,
  `username` varchar(35) NOT NULL,
  `domain_id` bigint(20) DEFAULT NULL,
  `postal_address_id` bigint(20) DEFAULT NULL,
  `residential_address_id` bigint(20) DEFAULT NULL,
  `status_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_u_all` (`domain_id`,`username`),
  KEY `FKmq4pww6c9he4t5f79uhlts0fq` (`postal_address_id`),
  KEY `FKfth4nd1bgw3mr692q5iy827gu` (`residential_address_id`),
  KEY `FKr62indkt0r2anb0m8hy5ldfpd` (`status_id`),
  CONSTRAINT `FKfth4nd1bgw3mr692q5iy827gu` FOREIGN KEY (`residential_address_id`) REFERENCES `address` (`id`),
  CONSTRAINT `FKk1hsftp46a7obygffmevl2g3s` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`),
  CONSTRAINT `FKmq4pww6c9he4t5f79uhlts0fq` FOREIGN KEY (`postal_address_id`) REFERENCES `address` (`id`),
  CONSTRAINT `FKr62indkt0r2anb0m8hy5ldfpd` FOREIGN KEY (`status_id`) REFERENCES `status` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_groups`
--

-- DROP TABLE IF EXISTS `user_groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_groups` (
  `user_id` bigint(20) NOT NULL,
  `group_id` bigint(20) NOT NULL,
  UNIQUE KEY `idx_urr_id` (`user_id`,`group_id`),
  KEY `FKjci186t9bje7c7n5um32v6cmy` (`group_id`),
  CONSTRAINT `FKjci186t9bje7c7n5um32v6cmy` FOREIGN KEY (`group_id`) REFERENCES `group_table` (`id`),
  CONSTRAINT `FKxgk67l5yp8458l39rog6nppe` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_labels`
--

-- DROP TABLE IF EXISTS `user_labels`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_labels` (
  `user_id` bigint(20) NOT NULL,
  `label_id` bigint(20) NOT NULL,
  KEY `idx_user_id` (`user_id`),
  KEY `idx_label_id` (`label_id`),
  CONSTRAINT `FKr9qq2egms33qe9rs4fig9d8or` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKrvl0t6tbm12ueejyhm5d3853m` FOREIGN KEY (`label_id`) REFERENCES `label` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_password_token`
--

-- DROP TABLE IF EXISTS `user_password_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_password_token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_on` datetime NOT NULL,
  `token` varchar(255) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_nm566khtoltheyq9vxr6j2ynt` (`user_id`),
  KEY `idx_upt_createdon` (`created_on`),
  CONSTRAINT `FKkmuk29ig6bm6cdehgstitir9j` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
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

-- Dump completed on 2016-11-04 14:22:14

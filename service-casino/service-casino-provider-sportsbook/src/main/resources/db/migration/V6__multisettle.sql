-- MariaDB dump 10.17  Distrib 10.4.12-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: lithium_casino_sportsbook
-- ------------------------------------------------------
-- Server version	10.4.12-MariaDB-1:10.4.12+maria~bionic

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `bet`
--

DROP TABLE IF EXISTS `bet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bet` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `accounting_transaction_id` bigint(20) NOT NULL,
  `amount` double NOT NULL,
  `balance_after` double NOT NULL,
  `bet_id` varchar(255) NOT NULL,
  `created_date` bigint(20) NOT NULL,
  `modified_date` bigint(20) NOT NULL,
  `purchase_id` varchar(255) NOT NULL,
  `request_id` bigint(20) NOT NULL,
  `timestamp` datetime DEFAULT NULL,
  `version` int(11) NOT NULL,
  `reservation_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_bet_id` (`bet_id`),
  UNIQUE KEY `idx_request_id` (`request_id`),
  KEY `idx_purchase_id` (`purchase_id`),
  KEY `FKnf04sov8qmnptgudao7k1mh54` (`reservation_id`),
  KEY `FK5lxp96uxbvhn0q54fttc8jc2d` (`user_id`),
  KEY `idx_timestamp` (`timestamp`),
  CONSTRAINT `FK5lxp96uxbvhn0q54fttc8jc2d` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKnf04sov8qmnptgudao7k1mh54` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `currency`
--

DROP TABLE IF EXISTS `currency`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `currency` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_cur_code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `domain`
--

DROP TABLE IF EXISTS `domain`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domain` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `currency_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_domain_name` (`name`),
  KEY `FK8hg9eix2ub5wn1be6q55x98ov` (`currency_id`),
  CONSTRAINT `FK8hg9eix2ub5wn1be6q55x98ov` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reservation`
--

DROP TABLE IF EXISTS `reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reservation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `accounting_transaction_id` bigint(20) NOT NULL,
  `amount` double NOT NULL,
  `balance_after` double NOT NULL,
  `bonus_used_amount` double NOT NULL,
  `created_date` bigint(20) NOT NULL,
  `modified_date` bigint(20) NOT NULL,
  `reserve_id` bigint(20) NOT NULL,
  `timestamp` datetime DEFAULT NULL,
  `total_bet_amount` double NOT NULL,
  `version` int(11) NOT NULL,
  `currency_id` bigint(20) NOT NULL,
  `reservation_cancel_id` bigint(20) DEFAULT NULL,
  `reservation_commit_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  `session_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_reserve_id` (`reserve_id`),
  UNIQUE KEY `idx_accounting_id` (`accounting_transaction_id`),
  UNIQUE KEY `idx_res_cancel` (`reservation_cancel_id`),
  UNIQUE KEY `idx_res_commit` (`reservation_commit_id`),
  KEY `idx_timestamp` (`timestamp`),
  KEY `FKpogyppirvgp7kbvnc2mrhwnl8` (`currency_id`),
  KEY `FKm4oimk0l1757o9pwavorj6ljg` (`user_id`),
  KEY `idx_session_id` (`session_id`),
  CONSTRAINT `FKf5wskqnx29w2jsff5o556rgmd` FOREIGN KEY (`reservation_cancel_id`) REFERENCES `reservation_cancel` (`id`),
  CONSTRAINT `FKky9yie2twghg8x2uw1kgkwl7w` FOREIGN KEY (`reservation_commit_id`) REFERENCES `reservation_commit` (`id`),
  CONSTRAINT `FKm4oimk0l1757o9pwavorj6ljg` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKpogyppirvgp7kbvnc2mrhwnl8` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reservation_cancel`
--

DROP TABLE IF EXISTS `reservation_cancel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reservation_cancel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `accounting_transaction_id` bigint(20) NOT NULL,
  `balance_after` double NOT NULL,
  `created_date` bigint(20) NOT NULL,
  `modified_date` bigint(20) NOT NULL,
  `timestamp` datetime DEFAULT NULL,
  `version` int(11) NOT NULL,
  `reservation_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_reservation` (`reservation_id`),
  UNIQUE KEY `idx_accounting_id` (`accounting_transaction_id`),
  KEY `idx_timestamp` (`timestamp`),
  CONSTRAINT `FKhpx3691topk6gxsqpv3pfh0v9` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reservation_commit`
--

DROP TABLE IF EXISTS `reservation_commit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reservation_commit` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `accounting_transaction_id` bigint(20) NOT NULL,
  `balance_after` double NOT NULL,
  `created_date` bigint(20) NOT NULL,
  `modified_date` bigint(20) NOT NULL,
  `timestamp` datetime DEFAULT NULL,
  `version` int(11) NOT NULL,
  `reservation_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_reservation` (`reservation_id`),
  UNIQUE KEY `idx_accounting_id` (`accounting_transaction_id`),
  KEY `idx_timestamp` (`timestamp`),
  CONSTRAINT `FKa71od5rhtuql08dbuw7h981t3` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `settlement`
--

DROP TABLE IF EXISTS `settlement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `settlement` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `balance_after` double NOT NULL,
  `created_date` bigint(20) NOT NULL,
  `modified_date` bigint(20) NOT NULL,
  `request_id` bigint(20) NOT NULL,
  `timestamp` datetime DEFAULT NULL,
  `version` int(11) NOT NULL,
  `currency_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_request_id` (`request_id`),
  KEY `idx_timestamp` (`timestamp`),
  KEY `FK9brprtqtn0d8j7w8bgacih3b0` (`currency_id`),
  CONSTRAINT `FK9brprtqtn0d8j7w8bgacih3b0` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `settlement_credit`
--

DROP TABLE IF EXISTS `settlement_credit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `settlement_credit` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `accounting_transaction_id` bigint(20) NOT NULL,
  `balance_after` double NOT NULL,
  `created_date` bigint(20) NOT NULL,
  `modified_date` bigint(20) NOT NULL,
  `request_id` bigint(20) NOT NULL,
  `timestamp` datetime DEFAULT NULL,
  `version` int(11) NOT NULL,
  `bet_id` bigint(20) NOT NULL,
  `amount` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_accounting_id` (`accounting_transaction_id`),
  UNIQUE KEY `idx_request_id_bet_id` (`request_id`,`bet_id`),
  KEY `idx_timestamp` (`timestamp`),
  KEY `FKoh6n5qbx96tlynm1cv9uxaim1` (`bet_id`),
  CONSTRAINT `FKoh6n5qbx96tlynm1cv9uxaim1` FOREIGN KEY (`bet_id`) REFERENCES `bet` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `settlement_debit`
--

DROP TABLE IF EXISTS `settlement_debit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `settlement_debit` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `accounting_transaction_id` bigint(20) NOT NULL,
  `balance_after` double NOT NULL,
  `created_date` bigint(20) NOT NULL,
  `modified_date` bigint(20) NOT NULL,
  `request_id` bigint(20) NOT NULL,
  `timestamp` datetime DEFAULT NULL,
  `version` int(11) NOT NULL,
  `currency_id` bigint(20) NOT NULL,
  `amount` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_request_id` (`request_id`),
  UNIQUE KEY `idx_accounting_id` (`accounting_transaction_id`),
  KEY `idx_timestamp` (`timestamp`),
  KEY `FKd0w7umddihkemf1mkb1p0guas` (`currency_id`),
  CONSTRAINT `FKd0w7umddihkemf1mkb1p0guas` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `settlement_entry`
--

DROP TABLE IF EXISTS `settlement_entry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `settlement_entry` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `accounting_transaction_id` bigint(20) NOT NULL,
  `amount` double NOT NULL,
  `request_index` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  `version` int(11) NOT NULL,
  `bet_id` bigint(20) NOT NULL,
  `settlement_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_se_accounting_id` (`accounting_transaction_id`),
  KEY `FK1g1l78en3i9hmprp4icka1m7v` (`bet_id`),
  KEY `FK7mhktq204j0ifm8bry06dvpmm` (`settlement_id`),
  CONSTRAINT `FK1g1l78en3i9hmprp4icka1m7v` FOREIGN KEY (`bet_id`) REFERENCES `bet` (`id`),
  CONSTRAINT `FK7mhktq204j0ifm8bry06dvpmm` FOREIGN KEY (`settlement_id`) REFERENCES `settlement` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `guid` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  `domain_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_guid` (`guid`),
  KEY `FKk1hsftp46a7obygffmevl2g3s` (`domain_id`),
  CONSTRAINT `FKk1hsftp46a7obygffmevl2g3s` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

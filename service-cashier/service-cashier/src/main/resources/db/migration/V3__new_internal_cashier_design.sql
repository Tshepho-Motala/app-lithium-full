-- MySQL dump 10.13  Distrib 5.7.19, for osx10.12 (x86_64)
--
-- Host: localhost    Database: lithium_cashier
-- ------------------------------------------------------
-- Server version	5.7.19

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
-- Table structure for table `domain`
--

DROP TABLE IF EXISTS `domain`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domain` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_domain_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `domain_method`
--

DROP TABLE IF EXISTS `domain_method`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domain_method` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `access_rule` varchar(255) DEFAULT NULL,
  `deleted` bit(1) NOT NULL,
  `deposit` bit(1) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `priority` int(11) NOT NULL,
  `version` int(11) NOT NULL,
  `domain_id` bigint(20) NOT NULL,
  `image_id` bigint(20) DEFAULT NULL,
  `method_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6315weatw34aqtfr5ucuwh68u` (`domain_id`),
  KEY `FKgl9t3e0kduj67xmh9pc79aw9o` (`image_id`),
  KEY `FKnb5h8putj4p4bndyvr3ecggxy` (`method_id`),
  CONSTRAINT `FK6315weatw34aqtfr5ucuwh68u` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`),
  CONSTRAINT `FKgl9t3e0kduj67xmh9pc79aw9o` FOREIGN KEY (`image_id`) REFERENCES `image` (`id`),
  CONSTRAINT `FKnb5h8putj4p4bndyvr3ecggxy` FOREIGN KEY (`method_id`) REFERENCES `method` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `domain_method_processor`
--

DROP TABLE IF EXISTS `domain_method_processor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domain_method_processor` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `access_rule` varchar(255) DEFAULT NULL,
  `deleted` bit(1) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `enabled` bit(1) NOT NULL,
  `version` int(11) NOT NULL,
  `weight` double DEFAULT NULL,
  `domain_method_id` bigint(20) NOT NULL,
  `fees_id` bigint(20) DEFAULT NULL,
  `limits_id` bigint(20) DEFAULT NULL,
  `processor_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKe4qoutqxwmx72dskdxxqv8knw` (`domain_method_id`),
  KEY `FKmsbhrl91rgmuq6g18ue1fde4h` (`fees_id`),
  KEY `FKtb08xd2bbanst5xh31lnymbua` (`limits_id`),
  KEY `FKmw8cf55tihmdn9pvyjmerhesn` (`processor_id`),
  CONSTRAINT `FKe4qoutqxwmx72dskdxxqv8knw` FOREIGN KEY (`domain_method_id`) REFERENCES `domain_method` (`id`),
  CONSTRAINT `FKmsbhrl91rgmuq6g18ue1fde4h` FOREIGN KEY (`fees_id`) REFERENCES `fees` (`id`),
  CONSTRAINT `FKmw8cf55tihmdn9pvyjmerhesn` FOREIGN KEY (`processor_id`) REFERENCES `processor` (`id`),
  CONSTRAINT `FKtb08xd2bbanst5xh31lnymbua` FOREIGN KEY (`limits_id`) REFERENCES `limits` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `domain_method_processor_profile`
--

DROP TABLE IF EXISTS `domain_method_processor_profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domain_method_processor_profile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `enabled` bit(1) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `weight` double DEFAULT NULL,
  `domain_method_processor_id` bigint(20) NOT NULL,
  `fees_id` bigint(20) DEFAULT NULL,
  `limits_id` bigint(20) DEFAULT NULL,
  `profile_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_pt_domain_method_processor_id_profile_id` (`domain_method_processor_id`,`profile_id`),
  KEY `FK5gmondmsduktfkk606uy34oia` (`fees_id`),
  KEY `FKm4vp0kphq0y8pje4hp9cq4i22` (`limits_id`),
  KEY `FKbbuggvguw9nlarlrpx1ok5nml` (`profile_id`),
  CONSTRAINT `FK54tq6bm5ahqagc0oaflbcbe22` FOREIGN KEY (`domain_method_processor_id`) REFERENCES `domain_method_processor` (`id`),
  CONSTRAINT `FK5gmondmsduktfkk606uy34oia` FOREIGN KEY (`fees_id`) REFERENCES `fees` (`id`),
  CONSTRAINT `FKbbuggvguw9nlarlrpx1ok5nml` FOREIGN KEY (`profile_id`) REFERENCES `profile` (`id`),
  CONSTRAINT `FKm4vp0kphq0y8pje4hp9cq4i22` FOREIGN KEY (`limits_id`) REFERENCES `limits` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `domain_method_processor_property`
--

DROP TABLE IF EXISTS `domain_method_processor_property`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domain_method_processor_property` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `value` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `domain_method_processor_id` bigint(20) NOT NULL,
  `processor_property_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4wbr73s1h9w9ufehvf32ribgr` (`domain_method_processor_id`),
  KEY `FK240qgo373or3yt2ad829xdlnh` (`processor_property_id`),
  CONSTRAINT `FK240qgo373or3yt2ad829xdlnh` FOREIGN KEY (`processor_property_id`) REFERENCES `processor_property` (`id`),
  CONSTRAINT `FK4wbr73s1h9w9ufehvf32ribgr` FOREIGN KEY (`domain_method_processor_id`) REFERENCES `domain_method_processor` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `domain_method_processor_user`
--

DROP TABLE IF EXISTS `domain_method_processor_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domain_method_processor_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `enabled` bit(1) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `weight` double DEFAULT NULL,
  `domain_method_processor_id` bigint(20) NOT NULL,
  `fees_id` bigint(20) DEFAULT NULL,
  `limits_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_pt_domain_method_processor_id_user_id` (`domain_method_processor_id`,`user_id`),
  KEY `FKb2avo135ob9i6c9hfg4e56xk4` (`fees_id`),
  KEY `FKad5ct7oiae0mvc13880gtham6` (`limits_id`),
  KEY `FK6rbeqbkgm4wevecmx7rd56q4w` (`user_id`),
  CONSTRAINT `FK6rbeqbkgm4wevecmx7rd56q4w` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKad5ct7oiae0mvc13880gtham6` FOREIGN KEY (`limits_id`) REFERENCES `limits` (`id`),
  CONSTRAINT `FKb2avo135ob9i6c9hfg4e56xk4` FOREIGN KEY (`fees_id`) REFERENCES `fees` (`id`),
  CONSTRAINT `FKmhmdg94anvqcurin2qaih11ip` FOREIGN KEY (`domain_method_processor_id`) REFERENCES `domain_method_processor` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `domain_method_profile`
--

DROP TABLE IF EXISTS `domain_method_profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domain_method_profile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `enabled` bit(1) DEFAULT NULL,
  `priority` int(11) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `domain_method_id` bigint(20) NOT NULL,
  `profile_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_pt_domain_method_id_profile_id` (`domain_method_id`,`profile_id`),
  KEY `FKmslvodul47o447810y014maq4` (`profile_id`),
  CONSTRAINT `FK5s6hqh7iiqprdlywd8s9ktymo` FOREIGN KEY (`domain_method_id`) REFERENCES `domain_method` (`id`),
  CONSTRAINT `FKmslvodul47o447810y014maq4` FOREIGN KEY (`profile_id`) REFERENCES `profile` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `domain_method_user`
--

DROP TABLE IF EXISTS `domain_method_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domain_method_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `enabled` bit(1) DEFAULT NULL,
  `priority` int(11) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `domain_method_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_pt_domain_method_id_user_id` (`domain_method_id`,`user_id`),
  KEY `FK83ce97awt6pv4xn58ktkhwv10` (`user_id`),
  CONSTRAINT `FK83ce97awt6pv4xn58ktkhwv10` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKn8vp6u45calccirjfxohrpj5y` FOREIGN KEY (`domain_method_id`) REFERENCES `domain_method` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fees`
--

DROP TABLE IF EXISTS `fees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fees` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `flat` bigint(20) DEFAULT NULL,
  `minimum` bigint(20) DEFAULT NULL,
  `percentage` decimal(19,2) DEFAULT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `image`
--

DROP TABLE IF EXISTS `image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `image` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `base64` longblob,
  `filename` varchar(255) NOT NULL,
  `filesize` bigint(20) DEFAULT NULL,
  `filetype` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `limits`
--

DROP TABLE IF EXISTS `limits`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `limits` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `max_amount` bigint(20) DEFAULT NULL,
  `max_amount_day` bigint(20) DEFAULT NULL,
  `max_amount_month` bigint(20) DEFAULT NULL,
  `max_amount_week` bigint(20) DEFAULT NULL,
  `max_transactions_day` bigint(20) DEFAULT NULL,
  `max_transactions_month` bigint(20) DEFAULT NULL,
  `max_transactions_week` bigint(20) DEFAULT NULL,
  `min_amount` bigint(20) DEFAULT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `method`
--

DROP TABLE IF EXISTS `method`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `method` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  `image_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_code` (`code`),
  KEY `FK2o05msl1sqbpxrmppkjv0cp7d` (`image_id`),
  CONSTRAINT `FK2o05msl1sqbpxrmppkjv0cp7d` FOREIGN KEY (`image_id`) REFERENCES `image` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `method_stage`
--

DROP TABLE IF EXISTS `method_stage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `method_stage` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deposit` bit(1) NOT NULL,
  `description` longtext,
  `number` int(11) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `method_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_method_number_deposit` (`method_id`,`number`,`deposit`),
  CONSTRAINT `FKbc5j9q4iqcgfbema5v0h5wk1e` FOREIGN KEY (`method_id`) REFERENCES `method` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `method_stage_field`
--

DROP TABLE IF EXISTS `method_stage_field`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `method_stage_field` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `description` longtext,
  `display_order` int(11) DEFAULT NULL,
  `input` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  `size_md` int(11) DEFAULT NULL,
  `size_xs` int(11) DEFAULT NULL,
  `type` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  `stage_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrwm579tby7xdeeccreyscrg04` (`stage_id`),
  CONSTRAINT `FKrwm579tby7xdeeccreyscrg04` FOREIGN KEY (`stage_id`) REFERENCES `method_stage` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `processor`
--

DROP TABLE IF EXISTS `processor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `processor` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `deposit` bit(1) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `url` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  `withdraw` bit(1) NOT NULL,
  `fees_id` bigint(20) DEFAULT NULL,
  `limits_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_code` (`code`),
  KEY `FKf6qndb04uxh62iu9g8iy92wc3` (`fees_id`),
  KEY `FKesesgmn8vxugr4rflnuc5puny` (`limits_id`),
  CONSTRAINT `FKesesgmn8vxugr4rflnuc5puny` FOREIGN KEY (`limits_id`) REFERENCES `limits` (`id`),
  CONSTRAINT `FKf6qndb04uxh62iu9g8iy92wc3` FOREIGN KEY (`fees_id`) REFERENCES `fees` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `processor_method`
--

DROP TABLE IF EXISTS `processor_method`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `processor_method` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` int(11) NOT NULL,
  `method_id` bigint(20) NOT NULL,
  `processor_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_processor_method` (`processor_id`,`method_id`),
  KEY `FK8xq4j6icx2xr9cprevfleameq` (`method_id`),
  CONSTRAINT `FK20849q6fjusuwpgfrb5mt51d2` FOREIGN KEY (`processor_id`) REFERENCES `processor` (`id`),
  CONSTRAINT `FK8xq4j6icx2xr9cprevfleameq` FOREIGN KEY (`method_id`) REFERENCES `method` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `processor_property`
--

DROP TABLE IF EXISTS `processor_property`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `processor_property` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `default_value` varchar(255) DEFAULT NULL,
  `description` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `type` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  `processor_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKh3ni9rvo6wjmfb14ca64xnvix` (`processor_id`),
  CONSTRAINT `FKh3ni9rvo6wjmfb14ca64xnvix` FOREIGN KEY (`processor_id`) REFERENCES `processor` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `profile`
--

DROP TABLE IF EXISTS `profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `profile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `domain_id` bigint(20) NOT NULL,
  `profile_requirements_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKodo47gjfaf284ojju7tq017ac` (`domain_id`),
  KEY `FK1jjcps4vrv7ipuy2iqlyt75nf` (`profile_requirements_id`),
  CONSTRAINT `FK1jjcps4vrv7ipuy2iqlyt75nf` FOREIGN KEY (`profile_requirements_id`) REFERENCES `profile_requirements` (`id`),
  CONSTRAINT `FKodo47gjfaf284ojju7tq017ac` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `profile_requirements`
--

DROP TABLE IF EXISTS `profile_requirements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `profile_requirements` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_active_days` int(11) DEFAULT NULL,
  `number_deposits` int(11) DEFAULT NULL,
  `number_payouts` int(11) DEFAULT NULL,
  `total_deposits` bigint(20) DEFAULT NULL,
  `total_payouts` bigint(20) DEFAULT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `transaction`
--

DROP TABLE IF EXISTS `transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `amount_cents` bigint(20) DEFAULT NULL,
  `created_on` datetime NOT NULL,
  `currency_code` varchar(255) NOT NULL,
  `processor_reference` bigint(20) DEFAULT NULL,
  `transaction_type` int(11) DEFAULT NULL,
  `current_id` bigint(20) DEFAULT NULL,
  `domain_method_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_createdOn` (`created_on`),
  KEY `idx_procRef` (`processor_reference`),
  KEY `idx_tranType` (`transaction_type`),
  KEY `FKafgnfxunh57alamsa2cvbnwn9` (`current_id`),
  KEY `FK8a6w1grm09k5uhke0rbswv0xx` (`domain_method_id`),
  KEY `FKsg7jp0aj6qipr50856wf6vbw1` (`user_id`),
  CONSTRAINT `FK8a6w1grm09k5uhke0rbswv0xx` FOREIGN KEY (`domain_method_id`) REFERENCES `domain_method` (`id`),
  CONSTRAINT `FKafgnfxunh57alamsa2cvbnwn9` FOREIGN KEY (`current_id`) REFERENCES `transaction_workflow_history` (`id`),
  CONSTRAINT `FKsg7jp0aj6qipr50856wf6vbw1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `transaction_comment`
--

DROP TABLE IF EXISTS `transaction_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `comment` varchar(255) DEFAULT NULL,
  `timestamp` datetime NOT NULL,
  `author_id` bigint(20) NOT NULL,
  `transaction_id` bigint(20) NOT NULL,
  `workflow_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKn8fxiqd5gbv05pqv3xyopctli` (`author_id`),
  KEY `FKp3pq6y3hugkltb26x0lcbcem3` (`transaction_id`),
  KEY `FK5lp2rujorr7xvf0oq6ex9usmi` (`workflow_id`),
  CONSTRAINT `FK5lp2rujorr7xvf0oq6ex9usmi` FOREIGN KEY (`workflow_id`) REFERENCES `transaction_workflow_history` (`id`),
  CONSTRAINT `FKn8fxiqd5gbv05pqv3xyopctli` FOREIGN KEY (`author_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKp3pq6y3hugkltb26x0lcbcem3` FOREIGN KEY (`transaction_id`) REFERENCES `transaction` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `transaction_data`
--

DROP TABLE IF EXISTS `transaction_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction_data` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `field` varchar(255) DEFAULT NULL,
  `output` bit(1) NOT NULL,
  `stage` int(11) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  `transaction_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_tran_field` (`transaction_id`,`field`),
  CONSTRAINT `FK2fsg2ip18u3lanvkxr8bui6sb` FOREIGN KEY (`transaction_id`) REFERENCES `transaction` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `transaction_processing_attempt`
--

DROP TABLE IF EXISTS `transaction_processing_attempt`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction_processing_attempt` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `processor_messages` longtext,
  `processor_raw_request` longtext,
  `processor_raw_response` longtext,
  `processor_reference` varchar(255) DEFAULT NULL,
  `success` bit(1) DEFAULT NULL,
  `timestamp` datetime NOT NULL,
  `transaction_id` bigint(20) NOT NULL,
  `workflow_from_id` bigint(20) NOT NULL,
  `workflow_to_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKc21ev1vpxbx8l0nxnrs4emmft` (`transaction_id`),
  KEY `FKoniaj6f7wvinyru2dx26hdgaj` (`workflow_from_id`),
  KEY `FKfqmhi70tewi5714gg40w2ytqy` (`workflow_to_id`),
  CONSTRAINT `FKc21ev1vpxbx8l0nxnrs4emmft` FOREIGN KEY (`transaction_id`) REFERENCES `transaction` (`id`),
  CONSTRAINT `FKfqmhi70tewi5714gg40w2ytqy` FOREIGN KEY (`workflow_to_id`) REFERENCES `transaction_workflow_history` (`id`),
  CONSTRAINT `FKoniaj6f7wvinyru2dx26hdgaj` FOREIGN KEY (`workflow_from_id`) REFERENCES `transaction_workflow_history` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `transaction_status`
--

DROP TABLE IF EXISTS `transaction_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction_status` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `code` varchar(35) NOT NULL,
  `deleted` bit(1) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_4njbdf40n0ot97uo82umng8b5` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `transaction_workflow_history`
--

DROP TABLE IF EXISTS `transaction_workflow_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction_workflow_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `accounting_reference` bigint(20) DEFAULT NULL,
  `stage` int(11) NOT NULL,
  `timestamp` datetime NOT NULL,
  `assigned_to_id` bigint(20) DEFAULT NULL,
  `author_id` bigint(20) DEFAULT NULL,
  `processor_id` bigint(20) DEFAULT NULL,
  `status_id` bigint(20) NOT NULL,
  `transaction_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKswntheqq3gg5b4hfcgyqd2phg` (`assigned_to_id`),
  KEY `FKb2ek2exqnmv7v6w52hrp4dmn8` (`author_id`),
  KEY `FKt7kgic4e8if8jy2f7k6c226vx` (`processor_id`),
  KEY `FKsr0n81k72flqkwf5gbkovoib4` (`status_id`),
  KEY `FKjmmgouavo5kgud3gdc15kxsu5` (`transaction_id`),
  CONSTRAINT `FKb2ek2exqnmv7v6w52hrp4dmn8` FOREIGN KEY (`author_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKjmmgouavo5kgud3gdc15kxsu5` FOREIGN KEY (`transaction_id`) REFERENCES `transaction` (`id`),
  CONSTRAINT `FKsr0n81k72flqkwf5gbkovoib4` FOREIGN KEY (`status_id`) REFERENCES `transaction_status` (`id`),
  CONSTRAINT `FKswntheqq3gg5b4hfcgyqd2phg` FOREIGN KEY (`assigned_to_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKt7kgic4e8if8jy2f7k6c226vx` FOREIGN KEY (`processor_id`) REFERENCES `domain_method_processor` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;
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
  `limits_id` bigint(20) DEFAULT NULL,
  `profile_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_guid` (`guid`),
  KEY `FKmxp206n04ksmrofviqhd7q13c` (`limits_id`),
  KEY `FKof44u64o1d7scaukghm9veo23` (`profile_id`),
  CONSTRAINT `FKmxp206n04ksmrofviqhd7q13c` FOREIGN KEY (`limits_id`) REFERENCES `limits` (`id`),
  CONSTRAINT `FKof44u64o1d7scaukghm9veo23` FOREIGN KEY (`profile_id`) REFERENCES `profile` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping routines for database 'lithium_cashier'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-08-24 14:37:43

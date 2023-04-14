-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: lithium_game_mock
-- ------------------------------------------------------
-- Server version	5.5.5-10.2.13-MariaDB

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
-- Table structure for table `mock_activity`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mock_activity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `request` longtext DEFAULT NULL,
  `round_id` varchar(255) DEFAULT NULL,
  `mock_session_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmm7v2tn85e8649j8n00hfa8v8` (`mock_session_id`),
  CONSTRAINT `FKmm7v2tn85e8649j8n00hfa8v8` FOREIGN KEY (`mock_session_id`) REFERENCES `mock_session` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mock_activity_execution`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mock_activity_execution` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `emulated_lithium_response` longtext DEFAULT NULL,
  `emulated_provider_request` longtext DEFAULT NULL,
  `execution_duration_ms` bigint(20) DEFAULT NULL,
  `lithium_transaction_id` bigint(20) DEFAULT NULL,
  `response` longtext DEFAULT NULL,
  `transaction_id` varchar(255) DEFAULT NULL,
  `mock_activity_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKe3l9vpiamguri23fpinfix7vi` (`mock_activity_id`),
  CONSTRAINT `FKe3l9vpiamguri23fpinfix7vi` FOREIGN KEY (`mock_activity_id`) REFERENCES `mock_activity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mock_session`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mock_session` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `auth_token` longtext DEFAULT NULL,
  `currency` varchar(255) DEFAULT NULL,
  `game_start_url` varchar(255) DEFAULT NULL,
  `provider_game_id` varchar(255) DEFAULT NULL,
  `provider_guid` varchar(255) DEFAULT NULL,
  `user_guid` varchar(255) DEFAULT NULL,
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

-- Dump completed on 2019-05-14 10:22:24

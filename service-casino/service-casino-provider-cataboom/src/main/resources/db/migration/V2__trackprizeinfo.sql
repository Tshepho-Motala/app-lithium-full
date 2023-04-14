-- MySQL dump 10.17  Distrib 10.3.17-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: lithium_provider_cataboom_campaign
-- ------------------------------------------------------
-- Server version	10.3.17-MariaDB-1:10.3.17+maria~bionic-log

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
-- Table structure for table `user`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `player_guid` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `player_guid` (`player_guid`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `initial_link`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `initial_link` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `campaignid` varchar(255) NOT NULL,
  `link` varchar(255) NOT NULL,
  `playid` varchar(255) DEFAULT NULL,
  `timestamp` datetime NOT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_playid` (`playid`),
  KEY `FKa75jn43ttqwu7qdyayavy6297` (`user_id`),
  CONSTRAINT `FKa75jn43ttqwu7qdyayavy6297` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `prize_fullfilment`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `prize_fullfilment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `campaignid` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `playcode` varchar(255) NOT NULL,
  `playerid` varchar(255) NOT NULL,
  `prizecode` varchar(255) DEFAULT NULL,
  `prizelink` varchar(255) DEFAULT NULL,
  `prizepin` varchar(255) DEFAULT NULL,
  `timestamp` datetime NOT NULL,
  `token` varchar(255) NOT NULL,
  `winlevel` varchar(255) NOT NULL,
  `initial_link_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_collection` (`user_id`,`playcode`),
  KEY `FKld5x2rro6utmkcw05cqeg2gxf` (`initial_link_id`),
  CONSTRAINT `FKld5x2rro6utmkcw05cqeg2gxf` FOREIGN KEY (`initial_link_id`) REFERENCES `initial_link` (`id`),
  CONSTRAINT `FKsgm7lflduypq8csp7sc0mf8ta` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
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

-- Dump completed on 2019-08-15 12:19:52

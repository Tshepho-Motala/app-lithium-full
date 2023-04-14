-- CREATE DATABASE  IF NOT EXISTS `lithium_games` /*!40100 DEFAULT CHARACTER SET utf8 */;
-- USE `lithium_games`;
-- MySQL dump 10.13  Distrib 5.7.12, for Win64 (x86_64)
--
-- Host: localhost    Database: lithium_games
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
-- Table structure for table `game`
--

-- DROP TABLE IF EXISTS `game`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `domain_name` varchar(255) DEFAULT NULL,
  `enabled` bit(1) NOT NULL,
  `guid` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `provider_game_id` varchar(255) DEFAULT NULL,
  `provider_guid` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `visible` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_gm_guid_domain` (`guid`,`domain_name`),
  KEY `idx_gm_domain` (`domain_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `game_graphic`
--

-- DROP TABLE IF EXISTS `game_graphic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_graphic` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deleted` bit(1) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `game_id` bigint(20) NOT NULL,
  `version` int(11) NOT NULL,
  `graphic_id` bigint(20) NOT NULL,
  `graphic_function_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_gg_gameid` (`game_id`),
  KEY `idx_gg_gameid_function` (`game_id`,`graphic_function_id`),
  KEY `FK8o2fq0u424b61apyqbefuft6` (`graphic_id`),
  KEY `FKik5bxjx9n4oxyvudtdi3wjtvc` (`graphic_function_id`),
  CONSTRAINT `FK8o2fq0u424b61apyqbefuft6` FOREIGN KEY (`graphic_id`) REFERENCES `graphic` (`id`),
  CONSTRAINT `FKik5bxjx9n4oxyvudtdi3wjtvc` FOREIGN KEY (`graphic_function_id`) REFERENCES `graphic_function` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `game_label_value`
--

-- DROP TABLE IF EXISTS `game_label_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_label_value` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deleted` bit(1) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `game_id` bigint(20) NOT NULL,
  `version` int(11) NOT NULL,
  `label_value_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_glv_gameid_labelvalue` (`game_id`,`label_value_id`,`deleted`),
  KEY `idx_glv_gameid` (`game_id`),
  KEY `FKpt26o0gm0t72sq1hur6md3kum` (`label_value_id`),
  CONSTRAINT `FKpt26o0gm0t72sq1hur6md3kum` FOREIGN KEY (`label_value_id`) REFERENCES `label_value` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `graphic`
--

-- DROP TABLE IF EXISTS `graphic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `graphic` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deleted` bit(1) NOT NULL,
  `image` longblob,
  `md5hash` varchar(255) DEFAULT NULL,
  `size` bigint(20) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_gr_size_md5` (`size`,`md5hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `graphic_function`
--

-- DROP TABLE IF EXISTS `graphic_function`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `graphic_function` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `enabled` bit(1) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_7axgj3tiyqoqc2ch0asvt1b2o` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `label`
--

-- DROP TABLE IF EXISTS `label`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `label` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_defh0r2wr6e5g7vu7vanv4pxa` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `label_value`
--

-- DROP TABLE IF EXISTS `label_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `label_value` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `value` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `label_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_label_value` (`label_id`,`value`),
  CONSTRAINT `FKre71r2qpe0al31ks5ys0mf3fj` FOREIGN KEY (`label_id`) REFERENCES `label` (`id`)
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

-- Dump completed on 2016-11-03 12:03:07

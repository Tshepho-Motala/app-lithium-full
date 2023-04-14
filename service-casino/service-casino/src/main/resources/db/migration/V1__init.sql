-- MySQL dump 10.13  Distrib 5.7.12, for Win64 (x86_64)
--
-- Host: localhost    Database: lithium_casino
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
-- Table structure for table `bonus`
--

-- DROP TABLE IF EXISTS `bonus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bonus` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `edit_user` varchar(255) DEFAULT NULL,
  `current_id` bigint(20) DEFAULT NULL,
  `edit_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKhqcfbrniy1b01o1rrgax4k33q` (`current_id`),
  KEY `FKar2q531fpev0px9qb7lwat54u` (`edit_id`),
  CONSTRAINT `FKar2q531fpev0px9qb7lwat54u` FOREIGN KEY (`edit_id`) REFERENCES `bonus_revision` (`id`),
  CONSTRAINT `FKhqcfbrniy1b01o1rrgax4k33q` FOREIGN KEY (`current_id`) REFERENCES `bonus_revision` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bonus_requirements_deposit`
--

-- DROP TABLE IF EXISTS `bonus_requirements_deposit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bonus_requirements_deposit` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bonus_percentage` int(11) DEFAULT NULL,
  `max_deposit` bigint(20) DEFAULT NULL,
  `min_deposit` bigint(20) DEFAULT NULL,
  `wager_requirements` int(11) DEFAULT NULL,
  `bonus_revision_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK89wv622pvgcbx75mf4lpkc7nx` (`bonus_revision_id`),
  CONSTRAINT `FK89wv622pvgcbx75mf4lpkc7nx` FOREIGN KEY (`bonus_revision_id`) REFERENCES `bonus_revision` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bonus_requirements_signup`
--

-- DROP TABLE IF EXISTS `bonus_requirements_signup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bonus_requirements_signup` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bonus_percentage` int(11) DEFAULT NULL,
  `wager_requirements` int(11) DEFAULT NULL,
  `bonus_revision_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6ikjt1xnch7uuv58drubsuhdg` (`bonus_revision_id`),
  CONSTRAINT `FK6ikjt1xnch7uuv58drubsuhdg` FOREIGN KEY (`bonus_revision_id`) REFERENCES `bonus_revision` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bonus_revision`
--

-- DROP TABLE IF EXISTS `bonus_revision`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bonus_revision` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bonus_code` varchar(255) DEFAULT NULL,
  `bonus_name` varchar(255) DEFAULT NULL,
  `enabled` bit(1) NOT NULL,
  `expiration_date` datetime DEFAULT NULL,
  `expiration_date_timezone` varchar(255) DEFAULT NULL,
  `for_deposit_number` int(11) DEFAULT NULL,
  `max_payout` bigint(20) DEFAULT NULL,
  `max_redeemable` int(11) DEFAULT NULL,
  `starting_date` datetime DEFAULT NULL,
  `starting_date_timezone` varchar(255) DEFAULT NULL,
  `valid_days` int(11) DEFAULT NULL,
  `domain_id` bigint(20) NOT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK94mmohwo2vwot1mg1qeyi4d0n` (`domain_id`),
  KEY `FKccuyj5kt47rrisiwqkeox79ep` (`parent_id`),
  CONSTRAINT `FK94mmohwo2vwot1mg1qeyi4d0n` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`),
  CONSTRAINT `FKccuyj5kt47rrisiwqkeox79ep` FOREIGN KEY (`parent_id`) REFERENCES `bonus_revision` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bonus_rules_freespins`
--

-- DROP TABLE IF EXISTS `bonus_rules_freespins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bonus_rules_freespins` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `freespins` int(11) DEFAULT NULL,
  `game_guid` varchar(255) DEFAULT NULL,
  `bonus_revision_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9cvfnasc3379q23tf9s5y2kql` (`bonus_revision_id`),
  CONSTRAINT `FK9cvfnasc3379q23tf9s5y2kql` FOREIGN KEY (`bonus_revision_id`) REFERENCES `bonus_revision` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bonus_rules_games`
--

-- DROP TABLE IF EXISTS `bonus_rules_games`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bonus_rules_games` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `game_category` varchar(255) DEFAULT NULL,
  `game_guid` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bonus_rules_games_percentages`
--

-- DROP TABLE IF EXISTS `bonus_rules_games_percentages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bonus_rules_games_percentages` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `game_category` varchar(255) DEFAULT NULL,
  `game_guid` varchar(255) DEFAULT NULL,
  `percentage` int(11) DEFAULT NULL,
  `bonus_revision_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK25gtkp6gt7vk5u4p536qjg03a` (`bonus_revision_id`),
  CONSTRAINT `FK25gtkp6gt7vk5u4p536qjg03a` FOREIGN KEY (`bonus_revision_id`) REFERENCES `bonus_revision` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `player_bonus`
--

-- DROP TABLE IF EXISTS `player_bonus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `player_bonus` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `player_guid` varchar(255) DEFAULT NULL,
  `current_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmvpdrnmvhlm55pi3xahu1w6yk` (`current_id`),
  CONSTRAINT `FKmvpdrnmvhlm55pi3xahu1w6yk` FOREIGN KEY (`current_id`) REFERENCES `player_bonus_history` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `player_bonus_history`
--

-- DROP TABLE IF EXISTS `player_bonus_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `player_bonus_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bonus_amount` bigint(20) DEFAULT NULL,
  `bonus_percentage` int(11) DEFAULT NULL,
  `cancelled` bit(1) DEFAULT NULL,
  `completed` bit(1) DEFAULT NULL,
  `expired` bit(1) DEFAULT NULL,
  `play_through_cents` bigint(20) DEFAULT NULL,
  `play_through_required_cents` bigint(20) DEFAULT NULL,
  `started_date` datetime NOT NULL,
  `trigger_amount` bigint(20) DEFAULT NULL,
  `bonus_id` bigint(20) DEFAULT NULL,
  `player_bonus_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKlwaljevn6i4d2xmr57sy82w2k` (`bonus_id`),
  KEY `FK27t3vgltnfmrxbcm3we2kpsji` (`player_bonus_id`),
  CONSTRAINT `FK27t3vgltnfmrxbcm3we2kpsji` FOREIGN KEY (`player_bonus_id`) REFERENCES `player_bonus` (`id`),
  CONSTRAINT `FKlwaljevn6i4d2xmr57sy82w2k` FOREIGN KEY (`bonus_id`) REFERENCES `bonus_revision` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `wagering_requirements`
--

-- DROP TABLE IF EXISTS `wagering_requirements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `wagering_requirements` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `maximum` int(11) DEFAULT NULL,
  `minimum` int(11) DEFAULT NULL,
  `wager` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-11-30 21:36:09

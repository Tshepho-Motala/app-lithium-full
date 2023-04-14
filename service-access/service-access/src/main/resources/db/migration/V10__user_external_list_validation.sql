ALTER TABLE `external_list`
ADD COLUMN `validate_once` BIT(1) NOT NULL DEFAULT 0;

-- MariaDB dump 10.17  Distrib 10.4.9-MariaDB, for osx10.14 (x86_64)
--
-- Host: 127.0.0.1    Database: lithium_access
-- ------------------------------------------------------
-- Server version	10.4.9-MariaDB

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
-- Table structure for table `user_external_list_validation`
--

-- DROP TABLE IF EXISTS `user_external_list_validation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_external_list_validation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `error_message` varchar(255) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `passed` bit(1) NOT NULL,
  `updated_on` datetime NOT NULL,
  `validated_on` datetime NOT NULL,
  `external_list_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_external_list` (`user_id`,`external_list_id`),
  KEY `FK6jy4i5td5vct9j9su31heu019` (`external_list_id`),
  CONSTRAINT `FK6jy4i5td5vct9j9su31heu019` FOREIGN KEY (`external_list_id`) REFERENCES `external_list` (`id`),
  CONSTRAINT `FKe2vxgmwprhw6pvvqjc9ywy2yh` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
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

-- Dump completed on 2020-04-20 10:20:27

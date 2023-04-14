-- MySQL dump 10.13  Distrib 8.0.21, for macos10.15 (x86_64)
--
-- Host: 127.0.0.1    Database: lithium_casino_cms
-- ------------------------------------------------------
-- Server version	5.7.31

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `domain`
--

-- DROP TABLE IF EXISTS `domain`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `domain` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `version` int(11) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lobby`
--

-- DROP TABLE IF EXISTS `lobby`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lobby` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `version` int(11) NOT NULL,
    `current_id` bigint(20) DEFAULT NULL,
    `domain_id` bigint(20) NOT NULL,
    `edit_id` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FKght9ce3djoiy82t18rixc2xyh` (`current_id`),
    KEY `FK8x145drsp3xlw6yqbav5vx9l2` (`domain_id`),
    KEY `FK3shsh1worx43uei93o7uqpjei` (`edit_id`),
    CONSTRAINT `FK3shsh1worx43uei93o7uqpjei` FOREIGN KEY (`edit_id`) REFERENCES `lobby_revision` (`id`),
    CONSTRAINT `FK8x145drsp3xlw6yqbav5vx9l2` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`),
    CONSTRAINT `FKght9ce3djoiy82t18rixc2xyh` FOREIGN KEY (`current_id`) REFERENCES `lobby_revision` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lobby_revision`
--

-- DROP TABLE IF EXISTS `lobby_revision`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lobby_revision` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_date` datetime NOT NULL,
    `description` longtext NOT NULL,
    `json` longtext NOT NULL,
    `modified_date` datetime DEFAULT NULL,
    `version` int(11) NOT NULL,
    `created_by_id` bigint(20) NOT NULL,
    `lobby_id` bigint(20) NOT NULL,
    `modified_by_id` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FKjebere6piijtf5jc3mdp9ihnw` (`created_by_id`),
    KEY `FK57dijrx10moh87g3jnj87u2ak` (`lobby_id`),
    KEY `FKm6p5d664g6150nrfuantimvcc` (`modified_by_id`),
    CONSTRAINT `FK57dijrx10moh87g3jnj87u2ak` FOREIGN KEY (`lobby_id`) REFERENCES `lobby` (`id`),
    CONSTRAINT `FKjebere6piijtf5jc3mdp9ihnw` FOREIGN KEY (`created_by_id`) REFERENCES `user` (`id`),
    CONSTRAINT `FKm6p5d664g6150nrfuantimvcc` FOREIGN KEY (`modified_by_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

-- DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `guid` varchar(255) NOT NULL,
    `version` int(11) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_user_guid` (`guid`)
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

-- Dump completed on 2021-02-07 16:17:23

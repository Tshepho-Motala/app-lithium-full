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
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;

CREATE TABLE `currency` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_cur_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;

CREATE TABLE `domain` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_domain_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;

CREATE TABLE `game` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `guid` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_guid` (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;

CREATE TABLE `game_play` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `balance_after` bigint(20) DEFAULT NULL,
  `created_date` bigint(20) NOT NULL,
  `guid` varchar(255) NOT NULL,
  `modified_date` bigint(20) NOT NULL,
  `roxor_status` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  `game_id` bigint(20) NOT NULL,
  `platform_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_game_play_guid` (`guid`),
  KEY `idx_game_play_roxor_status` (`roxor_status`),
  KEY `FKkksijqedk2d3lo8n091055p9r` (`game_id`),
  KEY `FKf9itjw5ffxj3snpem4cwpb7r5` (`platform_id`),
  KEY `FKcrf6e1xiu4u363u522mtk7p88` (`user_id`),
  CONSTRAINT `FKcrf6e1xiu4u363u522mtk7p88` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKf9itjw5ffxj3snpem4cwpb7r5` FOREIGN KEY (`platform_id`) REFERENCES `platform` (`id`),
  CONSTRAINT `FKkksijqedk2d3lo8n091055p9r` FOREIGN KEY (`game_id`) REFERENCES `game` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;

CREATE TABLE `game_play_request` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `balance_after` bigint(20) DEFAULT NULL,
  `header_game_play_id` varchar(255) NOT NULL,
  `header_session_key` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL,
  `status_reason` longtext,
  `version` int(11) NOT NULL,
  `game_play_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_game_play_request_header_session_key` (`header_session_key`),
  KEY `idx_game_play_request_header_game_play_id` (`header_game_play_id`),
  KEY `idx_game_play_request_status` (`status`),
  KEY `FKk21orf8nsrhl0noexx6c6isv3` (`game_play_id`),
  CONSTRAINT `FKk21orf8nsrhl0noexx6c6isv3` FOREIGN KEY (`game_play_id`) REFERENCES `game_play` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `operation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `accrual_id` varchar(255) DEFAULT NULL,
  `amount_cents` bigint(20) DEFAULT NULL,
  `created_date` bigint(20) NOT NULL,
  `lithium_accounting_id` bigint(20) DEFAULT NULL,
  `modified_date` bigint(20) NOT NULL,
  `pool_id` varchar(255) DEFAULT NULL,
  `reference` varchar(255) DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  `transfer_id` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `associated_operation_id` bigint(20) DEFAULT NULL,
  `currency_id` bigint(20) DEFAULT NULL,
  `game_play_id` bigint(20) NOT NULL,
  `game_play_request_id` bigint(20) NOT NULL,
  `operation_type_id` bigint(20) DEFAULT NULL,
  `source_id` bigint(20) DEFAULT NULL,
  `type_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1396gm3neqlspgcpbq9y032eg` (`associated_operation_id`),
  KEY `FK1fp5uypqa7fuoy18qix1l2ls3` (`currency_id`),
  KEY `FK43lh9uin4dvt64v3i34ri13j1` (`game_play_id`),
  KEY `FKrxsm06d3k2wypb5rxwmhcnfyn` (`game_play_request_id`),
  KEY `FKbyh0oxs3gvdcgv55kqh0u9iw` (`operation_type_id`),
  KEY `FKi0jq4i3y91e97fqwa8yfdakjc` (`source_id`),
  KEY `FKnq2tdxrpjatqan2qfy4fat0so` (`type_id`),
  CONSTRAINT `FK1396gm3neqlspgcpbq9y032eg` FOREIGN KEY (`associated_operation_id`) REFERENCES `operation` (`id`),
  CONSTRAINT `FK1fp5uypqa7fuoy18qix1l2ls3` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`),
  CONSTRAINT `FK43lh9uin4dvt64v3i34ri13j1` FOREIGN KEY (`game_play_id`) REFERENCES `game_play` (`id`),
  CONSTRAINT `FKbyh0oxs3gvdcgv55kqh0u9iw` FOREIGN KEY (`operation_type_id`) REFERENCES `operation_type` (`id`),
  CONSTRAINT `FKi0jq4i3y91e97fqwa8yfdakjc` FOREIGN KEY (`source_id`) REFERENCES `source` (`id`),
  CONSTRAINT `FKnq2tdxrpjatqan2qfy4fat0so` FOREIGN KEY (`type_id`) REFERENCES `type` (`id`),
  CONSTRAINT `FKrxsm06d3k2wypb5rxwmhcnfyn` FOREIGN KEY (`game_play_request_id`) REFERENCES `game_play_request` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;

CREATE TABLE `operation_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;

CREATE TABLE `platform` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_platform_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;

CREATE TABLE `source` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `guid` varchar(255) NOT NULL,
  `source_type` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_source_guid` (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;

CREATE TABLE `type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET character_set_client = @saved_cs_client */;
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

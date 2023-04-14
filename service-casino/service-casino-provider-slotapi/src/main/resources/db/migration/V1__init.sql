-- Johans-MacBook-Pro:~ johantheitguy$ docker exec -it 122ee089fe94892ea6e4c43ac9bc52533969708fcf985351e6dcdbf090d0e7ce /bin/sh; exit
-- mysqldump -h 127.0.0.1 -u root -p lithium_casino_slotapi --skip-add-drop-table --skip-comments --no-data | sed 's/ AUTO_INCREMENT=[0-9]*//g'

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
CREATE TABLE `bet` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `amount` double NOT NULL,
  `balance_after` double NOT NULL,
  `bet_transaction_id` varchar(255) NOT NULL,
  `created_date` bigint(20) NOT NULL,
  `lithium_accounting_id` bigint(20) DEFAULT NULL,
  `modified_date` bigint(20) NOT NULL,
  `transaction_timestamp` datetime DEFAULT NULL,
  `version` int(11) NOT NULL,
  `bet_round_id` bigint(20) NOT NULL,
  `currency_id` bigint(20) NOT NULL,
  `kind_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_tran_id` (`bet_transaction_id`),
  KEY `idx_tran_timestamp` (`transaction_timestamp`),
  KEY `idx_lithium_accounting_id` (`lithium_accounting_id`),
  KEY `FKqdnmenj11ojaxvk6opxr4nw95` (`bet_round_id`),
  KEY `FKnidt225v3j3gtl208p82i50i1` (`currency_id`),
  KEY `FKgse8lq9ljxauqr1ect2a299oh` (`kind_id`),
  CONSTRAINT `FKgse8lq9ljxauqr1ect2a299oh` FOREIGN KEY (`kind_id`) REFERENCES `bet_request_kind` (`id`),
  CONSTRAINT `FKnidt225v3j3gtl208p82i50i1` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`),
  CONSTRAINT `FKqdnmenj11ojaxvk6opxr4nw95` FOREIGN KEY (`bet_round_id`) REFERENCES `bet_round` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bet_request_kind` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bet_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `balance_after` double NOT NULL,
  `bet_result_transaction_id` varchar(255) NOT NULL,
  `created_date` bigint(20) NOT NULL,
  `lithium_accounting_id` bigint(20) DEFAULT NULL,
  `modified_date` bigint(20) NOT NULL,
  `returns` double NOT NULL,
  `round_complete` bit(1) NOT NULL,
  `transaction_timestamp` datetime DEFAULT NULL,
  `version` int(11) NOT NULL,
  `bet_result_kind_id` bigint(20) NOT NULL,
  `bet_round_id` bigint(20) NOT NULL,
  `currency_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_tran_id` (`bet_result_transaction_id`),
  KEY `idx_tran_timestamp` (`transaction_timestamp`),
  KEY `idx_lithium_accounting_id` (`lithium_accounting_id`),
  KEY `FKjscd3nf2m1nwx5r3njxhdps7q` (`bet_result_kind_id`),
  KEY `FK9w628r4v0wq2ye5x21wx7awtk` (`bet_round_id`),
  KEY `FK45igs4hmd4v9g10trbcpiy5n4` (`currency_id`),
  CONSTRAINT `FK45igs4hmd4v9g10trbcpiy5n4` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`),
  CONSTRAINT `FK9w628r4v0wq2ye5x21wx7awtk` FOREIGN KEY (`bet_round_id`) REFERENCES `bet_round` (`id`),
  CONSTRAINT `FKjscd3nf2m1nwx5r3njxhdps7q` FOREIGN KEY (`bet_result_kind_id`) REFERENCES `bet_result_kind` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bet_result_kind` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bet_round` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `complete` bit(1) NOT NULL,
  `created_date` bigint(20) NOT NULL,
  `guid` varchar(255) NOT NULL,
  `modified_date` bigint(20) NOT NULL,
  `sequence_number` int(11) NOT NULL,
  `version` int(11) NOT NULL,
  `game_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_guid` (`guid`),
  KEY `FK9s5dxcs97tvp2n3prs49ydhun` (`game_id`),
  KEY `FKbrpnq6ep9l85hfyn9m7vp1l74` (`user_id`),
  CONSTRAINT `FK9s5dxcs97tvp2n3prs49ydhun` FOREIGN KEY (`game_id`) REFERENCES `game` (`id`),
  CONSTRAINT `FKbrpnq6ep9l85hfyn9m7vp1l74` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
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
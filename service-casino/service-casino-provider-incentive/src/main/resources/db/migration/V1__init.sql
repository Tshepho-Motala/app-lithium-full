-- Created with
-- mysqldump -u root -p lithium_casino_incentive --skip-add-drop-table --skip-comments --no-data | sed 's/ AUTO_INCREMENT=[0-9]*//g'

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
  `bet_transaction_id` varchar(255) NOT NULL,
  `created_date` bigint(20) NOT NULL,
  `error_code` int(11) DEFAULT NULL,
  `error_message` varchar(255) DEFAULT NULL,
  `lithium_accounting_id` bigint(20) DEFAULT NULL,
  `max_potential_win` double NOT NULL,
  `modified_date` bigint(20) NOT NULL,
  `total_odds` double NOT NULL,
  `total_stake` double NOT NULL,
  `transaction_timestamp` datetime DEFAULT NULL,
  `version` int(11) NOT NULL,
  `placement_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_tran_id` (`bet_transaction_id`),
  KEY `FK190yt0a942grhau7168ypn3w` (`placement_id`),
  CONSTRAINT `FK190yt0a942grhau7168ypn3w` FOREIGN KEY (`placement_id`) REFERENCES `placement` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bet_selection` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `price` double NOT NULL,
  `version` int(11) NOT NULL,
  `bet_id` bigint(20) NOT NULL,
  `event_id` bigint(20) NOT NULL,
  `market_id` bigint(20) NOT NULL,
  `selection_id` bigint(20) NOT NULL,
  `selection_type_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKluao96by32uhcqd79m7r2lglf` (`bet_id`),
  KEY `FKjdwlmrlcsgckoaqi0sng8mxrn` (`event_id`),
  KEY `FKgdy9dyi68r1kiar9qhemmuj4l` (`market_id`),
  KEY `FKkc8ucddubwq26ypj5h02j9b3r` (`selection_id`),
  KEY `FK1t3w18kx93b5jgv2civqxfl57` (`selection_type_id`),
  CONSTRAINT `FK1t3w18kx93b5jgv2civqxfl57` FOREIGN KEY (`selection_type_id`) REFERENCES `selection_type` (`id`),
  CONSTRAINT `FKgdy9dyi68r1kiar9qhemmuj4l` FOREIGN KEY (`market_id`) REFERENCES `market` (`id`),
  CONSTRAINT `FKjdwlmrlcsgckoaqi0sng8mxrn` FOREIGN KEY (`event_id`) REFERENCES `event` (`id`),
  CONSTRAINT `FKkc8ucddubwq26ypj5h02j9b3r` FOREIGN KEY (`selection_id`) REFERENCES `selection` (`id`),
  CONSTRAINT `FKluao96by32uhcqd79m7r2lglf` FOREIGN KEY (`bet_id`) REFERENCES `bet` (`id`)
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
CREATE TABLE `event` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `start_timestamp` datetime DEFAULT NULL,
  `version` int(11) NOT NULL,
  `event_name_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_event_unique` (`start_timestamp`,`event_name_id`),
  KEY `FKf2eqvah8xtnqkak1vt6slvvtb` (`event_name_id`),
  CONSTRAINT `FKf2eqvah8xtnqkak1vt6slvvtb` FOREIGN KEY (`event_name_id`) REFERENCES `event_name` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event_name` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_event_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `incentive_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `guid` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_guid` (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `market` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_market_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `placement` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `extra_data` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `currency_id` bigint(20) NOT NULL,
  `domain_id` bigint(20) NOT NULL,
  `incentive_user_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKg93miw2rv8ppl8ceqx4dhan9t` (`currency_id`),
  KEY `FKakgketxi6du2r5dtt1kf5v55n` (`domain_id`),
  KEY `FKg2mjw66098ougt76vcs45x9d0` (`incentive_user_id`),
  KEY `FK81j5l2p7k43vvxq8hsuko7q6` (`user_id`),
  CONSTRAINT `FK81j5l2p7k43vvxq8hsuko7q6` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKakgketxi6du2r5dtt1kf5v55n` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`),
  CONSTRAINT `FKg2mjw66098ougt76vcs45x9d0` FOREIGN KEY (`incentive_user_id`) REFERENCES `incentive_user` (`id`),
  CONSTRAINT `FKg93miw2rv8ppl8ceqx4dhan9t` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `selection` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `guid` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_guid` (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `selection_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `selection_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `settlement` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `error_code` int(11) DEFAULT NULL,
  `error_message` varchar(255) DEFAULT NULL,
  `lithium_accounting_id` bigint(20) DEFAULT NULL,
  `returns` double NOT NULL,
  `settlement_transaction_id` varchar(255) NOT NULL,
  `transaction_timestamp` datetime DEFAULT NULL,
  `version` int(11) NOT NULL,
  `bet_id` bigint(20) NOT NULL,
  `currency_id` bigint(20) NOT NULL,
  `settlement_result_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_tran_id` (`settlement_transaction_id`),
  KEY `FKdlx09ltf97c3ao3wmr28hegdc` (`bet_id`),
  KEY `FK9brprtqtn0d8j7w8bgacih3b0` (`currency_id`),
  KEY `FKqxcm5yo1u0hq8oj0cqcixhbfn` (`settlement_result_id`),
  CONSTRAINT `FK9brprtqtn0d8j7w8bgacih3b0` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`),
  CONSTRAINT `FKdlx09ltf97c3ao3wmr28hegdc` FOREIGN KEY (`bet_id`) REFERENCES `bet` (`id`),
  CONSTRAINT `FKqxcm5yo1u0hq8oj0cqcixhbfn` FOREIGN KEY (`settlement_result_id`) REFERENCES `settlement_result` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `settlement_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `settlement_selection` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` int(11) NOT NULL,
  `bet_selection_id` bigint(20) NOT NULL,
  `selection_result_id` bigint(20) NOT NULL,
  `settlement_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1o3252phbsayq4edviua53is2` (`bet_selection_id`),
  KEY `FKg9eu8wugeplm4hmy0gv9hskg1` (`selection_result_id`),
  KEY `FK1b8os0nmtf8laidide839o4mn` (`settlement_id`),
  CONSTRAINT `FK1b8os0nmtf8laidide839o4mn` FOREIGN KEY (`settlement_id`) REFERENCES `settlement` (`id`),
  CONSTRAINT `FK1o3252phbsayq4edviua53is2` FOREIGN KEY (`bet_selection_id`) REFERENCES `bet_selection` (`id`),
  CONSTRAINT `FKg9eu8wugeplm4hmy0gv9hskg1` FOREIGN KEY (`selection_result_id`) REFERENCES `selection_result` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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


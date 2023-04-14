-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: lithium_affiliate_ia
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

--
-- Table structure for table `report`
--

-- DROP TABLE IF EXISTS `report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `report` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) NOT NULL,
  `created_date` datetime NOT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `domain_name` varchar(255) NOT NULL,
  `editor` varchar(255) DEFAULT NULL,
  `enabled` bit(1) DEFAULT NULL,
  `run_retries_count` bigint(20) DEFAULT NULL,
  `scheduled_date` datetime DEFAULT NULL,
  `version` int(11) NOT NULL,
  `current_id` bigint(20) DEFAULT NULL,
  `edit_id` bigint(20) DEFAULT NULL,
  `last_completed_id` bigint(20) DEFAULT NULL,
  `last_failed_id` bigint(20) DEFAULT NULL,
  `running_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_domain` (`domain_name`),
  KEY `idx_enabled` (`enabled`),
  KEY `idx_deleted` (`deleted`),
  KEY `idx_created_by` (`created_by`),
  KEY `FKse03itlwjhftjwjvv719nkojm` (`current_id`),
  KEY `FKnnsgxbdjan9fgnbhg3us8wogl` (`edit_id`),
  KEY `FKq7e62pa471hv4jxn9b1t6mtw5` (`last_completed_id`),
  KEY `FK1tpam4pgfapuv8lm3o14xt68n` (`last_failed_id`),
  KEY `FKahusui6frrwef2jq0vjlfrm0b` (`running_id`),
  CONSTRAINT `FK1tpam4pgfapuv8lm3o14xt68n` FOREIGN KEY (`last_failed_id`) REFERENCES `report_run` (`id`),
  CONSTRAINT `FKahusui6frrwef2jq0vjlfrm0b` FOREIGN KEY (`running_id`) REFERENCES `report_run` (`id`),
  CONSTRAINT `FKnnsgxbdjan9fgnbhg3us8wogl` FOREIGN KEY (`edit_id`) REFERENCES `report_revision` (`id`),
  CONSTRAINT `FKq7e62pa471hv4jxn9b1t6mtw5` FOREIGN KEY (`last_completed_id`) REFERENCES `report_run` (`id`),
  CONSTRAINT `FKse03itlwjhftjwjvv719nkojm` FOREIGN KEY (`current_id`) REFERENCES `report_revision` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `report_action`
--

-- DROP TABLE IF EXISTS `report_action`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `report_action` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `action_type` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  `report_revision_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKd3h8eaxlxpuhpbl6ypvj1qir3` (`report_revision_id`),
  CONSTRAINT `FKd3h8eaxlxpuhpbl6ypvj1qir3` FOREIGN KEY (`report_revision_id`) REFERENCES `report_revision` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `report_action_label_value`
--

-- DROP TABLE IF EXISTS `report_action_label_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `report_action_label_value` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` int(11) NOT NULL,
  `label_value_id` bigint(20) NOT NULL,
  `report_action_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_report_action_id` (`report_action_id`),
  KEY `idx_label_value_id` (`label_value_id`),
  CONSTRAINT `FKgst55rfmj6gjrgqxaaedxpsa4` FOREIGN KEY (`label_value_id`) REFERENCES `label_value` (`id`),
  CONSTRAINT `FKpaa0uag1nb8nk8h9g337e0o49` FOREIGN KEY (`report_action_id`) REFERENCES `report_action` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `report_filter`
--

-- DROP TABLE IF EXISTS `report_filter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `report_filter` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `field` varchar(255) NOT NULL,
  `operator` varchar(255) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `report_revision_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1jhdwimc0k4h4ro1qtwmro8eu` (`report_revision_id`),
  CONSTRAINT `FK1jhdwimc0k4h4ro1qtwmro8eu` FOREIGN KEY (`report_revision_id`) REFERENCES `report_revision` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `report_revision`
--

-- DROP TABLE IF EXISTS `report_revision`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `report_revision` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `all_filters_applicable` bit(1) DEFAULT NULL,
  `chosen_date_string` varchar(255) DEFAULT NULL,
  `chosen_time_string` varchar(255) DEFAULT NULL,
  `cron` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `ggr_percentage_deduction` varchar(255) DEFAULT NULL,
  `granularity` int(11) NOT NULL,
  `granularity_offset` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `notify_email` varchar(255) DEFAULT NULL,
  `update_by` varchar(255) NOT NULL,
  `update_date` datetime NOT NULL,
  `version` int(11) NOT NULL,
  `report_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`),
  KEY `idx_description` (`description`),
  KEY `FK6mtsk57fjk9651ui2ltqtioe7` (`report_id`),
  CONSTRAINT `FK6mtsk57fjk9651ui2ltqtioe7` FOREIGN KEY (`report_id`) REFERENCES `report` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `report_run`
--

-- DROP TABLE IF EXISTS `report_run`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `report_run` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `access_key` varchar(255) NOT NULL,
  `actions_performed` bigint(20) DEFAULT NULL,
  `completed` bit(1) DEFAULT NULL,
  `completed_on` datetime DEFAULT NULL,
  `fail_reason` longtext DEFAULT NULL,
  `failed` bit(1) DEFAULT NULL,
  `filtered_records` bigint(20) DEFAULT NULL,
  `last_update` datetime DEFAULT NULL,
  `processed_records` bigint(20) DEFAULT NULL,
  `started_by` varchar(255) NOT NULL,
  `started_on` datetime NOT NULL,
  `total_records` bigint(20) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `report_id` bigint(20) NOT NULL,
  `report_revision_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_accesskey` (`access_key`),
  KEY `idx_started` (`started_on`),
  KEY `idx_completed` (`completed_on`),
  KEY `FKnapb0o8v7nu9pbmjbovm40yfw` (`report_id`),
  KEY `FK6le0bq7q5vonlyd9u7tc140ua` (`report_revision_id`),
  CONSTRAINT `FK6le0bq7q5vonlyd9u7tc140ua` FOREIGN KEY (`report_revision_id`) REFERENCES `report_revision` (`id`),
  CONSTRAINT `FKnapb0o8v7nu9pbmjbovm40yfw` FOREIGN KEY (`report_id`) REFERENCES `report` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `report_run_results`
--

-- DROP TABLE IF EXISTS `report_run_results`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `report_run_results` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `balance_adjust_amount_cents` bigint(20) DEFAULT NULL,
  `balance_adjust_count` bigint(20) DEFAULT NULL,
  `call_opt_out` bit(1) DEFAULT NULL,
  `casino_bet_amount_cents` bigint(20) DEFAULT NULL,
  `casino_bet_count` bigint(20) DEFAULT NULL,
  `casino_bonus_activate_amount_cents` bigint(20) DEFAULT NULL,
  `casino_bonus_bet_amount_cents` bigint(20) DEFAULT NULL,
  `casino_bonus_bet_count` bigint(20) DEFAULT NULL,
  `casino_bonus_cancel_amount_cents` bigint(20) DEFAULT NULL,
  `casino_bonus_expire_amount_cents` bigint(20) DEFAULT NULL,
  `casino_bonus_max_payout_excess_amount_cents` bigint(20) DEFAULT NULL,
  `casino_bonus_net_amount_cents` bigint(20) DEFAULT NULL,
  `casino_bonus_pending_amount_cents` bigint(20) DEFAULT NULL,
  `casino_bonus_pending_cancel_amount_cents` bigint(20) DEFAULT NULL,
  `casino_bonus_pending_count` bigint(20) DEFAULT NULL,
  `casino_bonus_transfer_from_bonus_amount_cents` bigint(20) DEFAULT NULL,
  `casino_bonus_transfer_from_bonus_pending_amount_cents` bigint(20) DEFAULT NULL,
  `casino_bonus_transfer_to_bonus_amount_cents` bigint(20) DEFAULT NULL,
  `casino_bonus_transfer_to_bonus_pending_amount_cents` bigint(20) DEFAULT NULL,
  `casino_bonus_win_amount_cents` bigint(20) DEFAULT NULL,
  `casino_bonus_win_count` bigint(20) DEFAULT NULL,
  `casino_net_amount_cents` bigint(20) DEFAULT NULL,
  `casino_win_amount_cents` bigint(20) DEFAULT NULL,
  `casino_win_count` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `current_balance_casino_bonus_cents` bigint(20) DEFAULT NULL,
  `current_balance_casino_bonus_pending_cents` bigint(20) DEFAULT NULL,
  `current_balance_cents` bigint(20) DEFAULT NULL,
  `date_of_birth` datetime DEFAULT NULL,
  `date_of_birth_day` int(11) DEFAULT NULL,
  `date_of_birth_month` int(11) DEFAULT NULL,
  `date_of_birth_year` int(11) DEFAULT NULL,
  `deposit_amount_cents` bigint(20) DEFAULT NULL,
  `deposit_count` bigint(20) DEFAULT NULL,
  `deposit_fee_cents` bigint(20) DEFAULT NULL,
  `email_opt_out` bit(1) DEFAULT NULL,
  `email_validated` bit(1) DEFAULT NULL,
  `enabled` bit(1) DEFAULT NULL,
  `ngr_amount` bigint(20) DEFAULT NULL,
  `payout_amount_cents` bigint(20) DEFAULT NULL,
  `payout_count` bigint(20) DEFAULT NULL,
  `period_closing_balance_casino_bonus_cents` bigint(20) DEFAULT NULL,
  `period_closing_balance_casino_bonus_pending_cents` bigint(20) DEFAULT NULL,
  `period_closing_balance_cents` bigint(20) DEFAULT NULL,
  `period_opening_balance_casino_bonus_cents` bigint(20) DEFAULT NULL,
  `period_opening_balance_casino_bonus_pending_cents` bigint(20) DEFAULT NULL,
  `period_opening_balance_cents` bigint(20) DEFAULT NULL,
  `sms_opt_out` bit(1) DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `affiliate_guid_id` bigint(20) DEFAULT NULL,
  `banner_guid_id` bigint(20) DEFAULT NULL,
  `campaign_guid_id` bigint(20) DEFAULT NULL,
  `cellphone_number_id` bigint(20) DEFAULT NULL,
  `email_id` bigint(20) DEFAULT NULL,
  `first_name_id` bigint(20) DEFAULT NULL,
  `last_name_id` bigint(20) DEFAULT NULL,
  `postal_address_admin_level1_id` bigint(20) DEFAULT NULL,
  `postal_address_city_id` bigint(20) DEFAULT NULL,
  `postal_address_country_id` bigint(20) DEFAULT NULL,
  `postal_address_line1_id` bigint(20) DEFAULT NULL,
  `postal_address_line2_id` bigint(20) DEFAULT NULL,
  `postal_address_line3_id` bigint(20) DEFAULT NULL,
  `postal_address_postal_code_id` bigint(20) DEFAULT NULL,
  `report_run_id` bigint(20) DEFAULT NULL,
  `residential_address_admin_level1_id` bigint(20) DEFAULT NULL,
  `residential_address_city_id` bigint(20) DEFAULT NULL,
  `residential_address_country_id` bigint(20) DEFAULT NULL,
  `residential_address_line1_id` bigint(20) DEFAULT NULL,
  `residential_address_line2_id` bigint(20) DEFAULT NULL,
  `residential_address_line3_id` bigint(20) DEFAULT NULL,
  `residential_address_postal_code_id` bigint(20) DEFAULT NULL,
  `signup_bonus_code_id` bigint(20) DEFAULT NULL,
  `status_id` bigint(20) DEFAULT NULL,
  `telephone_number_id` bigint(20) DEFAULT NULL,
  `username_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_created` (`created_date`),
  KEY `idx_dob` (`date_of_birth`),
  KEY `idx_dob_day` (`date_of_birth_day`),
  KEY `idx_dob_month` (`date_of_birth_month`),
  KEY `idx_dob_year` (`date_of_birth_year`),
  KEY `FKoircx07vnkjdovxxgd9fxuulr` (`affiliate_guid_id`),
  KEY `FK5kc3w17ptpq0539s2dklbahhq` (`banner_guid_id`),
  KEY `FKlrd2pmpefiuw6m8jit7gq30s6` (`campaign_guid_id`),
  KEY `FKe3hnj9h5np1mvk4de6afbmuf3` (`cellphone_number_id`),
  KEY `FKll5p7lspx56r8tk7avixvfrfo` (`email_id`),
  KEY `FK4o3v4uejnt16t7aw77kgoqo5g` (`first_name_id`),
  KEY `FKsr2980b0imcpxy221au88k7on` (`last_name_id`),
  KEY `FKcopusqnp5r9j6dc1cnv8k6bkv` (`postal_address_admin_level1_id`),
  KEY `FKqwfb5fm1ocga15nvse0s5ckwe` (`postal_address_city_id`),
  KEY `FKdyk563ai3o0hon6y10phcrk36` (`postal_address_country_id`),
  KEY `FKl980e16x21b8pghbfa7ulwo9g` (`postal_address_line1_id`),
  KEY `FKoykgvvxc9b5kbpjemuxehqso7` (`postal_address_line2_id`),
  KEY `FKqk3dg9w18c8qru9hihwuikcbs` (`postal_address_line3_id`),
  KEY `FKfx5s2p4tt9uc5ey5ej5d51jiq` (`postal_address_postal_code_id`),
  KEY `FKonp4ebvgtk1carn6e0f69mvuw` (`report_run_id`),
  KEY `FK9t6f9qvv67byt3hbksyqirgbm` (`residential_address_admin_level1_id`),
  KEY `FK4cc9ffrg6aqsyho1h4rh2qnwj` (`residential_address_city_id`),
  KEY `FKfdekgb5tah38qtv5v4x5ruhi7` (`residential_address_country_id`),
  KEY `FKms5kes52i593nj7u8qdqk3jrp` (`residential_address_line1_id`),
  KEY `FKsytxp1pxvmoxwpohbpp1lg3io` (`residential_address_line2_id`),
  KEY `FKm253umq7e8h5ueqvuk9m4blhc` (`residential_address_line3_id`),
  KEY `FKba9pocik01uwig0fa64jluft1` (`residential_address_postal_code_id`),
  KEY `FKbrweifxhvay69080nyxn2yhkn` (`signup_bonus_code_id`),
  KEY `FKnouhuax8xidoem89h54abgkvb` (`status_id`),
  KEY `FKe4t8al2i90f3l302xdjgarcas` (`telephone_number_id`),
  KEY `FKq15m2qwqybib20m4mytinghli` (`username_id`),
  CONSTRAINT `FK4cc9ffrg6aqsyho1h4rh2qnwj` FOREIGN KEY (`residential_address_city_id`) REFERENCES `string_value` (`id`),
  CONSTRAINT `FK4o3v4uejnt16t7aw77kgoqo5g` FOREIGN KEY (`first_name_id`) REFERENCES `string_value` (`id`),
  CONSTRAINT `FK5kc3w17ptpq0539s2dklbahhq` FOREIGN KEY (`banner_guid_id`) REFERENCES `string_value` (`id`),
  CONSTRAINT `FK9t6f9qvv67byt3hbksyqirgbm` FOREIGN KEY (`residential_address_admin_level1_id`) REFERENCES `string_value` (`id`),
  CONSTRAINT `FKba9pocik01uwig0fa64jluft1` FOREIGN KEY (`residential_address_postal_code_id`) REFERENCES `string_value` (`id`),
  CONSTRAINT `FKbrweifxhvay69080nyxn2yhkn` FOREIGN KEY (`signup_bonus_code_id`) REFERENCES `string_value` (`id`),
  CONSTRAINT `FKcopusqnp5r9j6dc1cnv8k6bkv` FOREIGN KEY (`postal_address_admin_level1_id`) REFERENCES `string_value` (`id`),
  CONSTRAINT `FKdyk563ai3o0hon6y10phcrk36` FOREIGN KEY (`postal_address_country_id`) REFERENCES `string_value` (`id`),
  CONSTRAINT `FKe3hnj9h5np1mvk4de6afbmuf3` FOREIGN KEY (`cellphone_number_id`) REFERENCES `string_value` (`id`),
  CONSTRAINT `FKe4t8al2i90f3l302xdjgarcas` FOREIGN KEY (`telephone_number_id`) REFERENCES `string_value` (`id`),
  CONSTRAINT `FKfdekgb5tah38qtv5v4x5ruhi7` FOREIGN KEY (`residential_address_country_id`) REFERENCES `string_value` (`id`),
  CONSTRAINT `FKfx5s2p4tt9uc5ey5ej5d51jiq` FOREIGN KEY (`postal_address_postal_code_id`) REFERENCES `string_value` (`id`),
  CONSTRAINT `FKl980e16x21b8pghbfa7ulwo9g` FOREIGN KEY (`postal_address_line1_id`) REFERENCES `string_value` (`id`),
  CONSTRAINT `FKll5p7lspx56r8tk7avixvfrfo` FOREIGN KEY (`email_id`) REFERENCES `string_value` (`id`),
  CONSTRAINT `FKlrd2pmpefiuw6m8jit7gq30s6` FOREIGN KEY (`campaign_guid_id`) REFERENCES `string_value` (`id`),
  CONSTRAINT `FKm253umq7e8h5ueqvuk9m4blhc` FOREIGN KEY (`residential_address_line3_id`) REFERENCES `string_value` (`id`),
  CONSTRAINT `FKms5kes52i593nj7u8qdqk3jrp` FOREIGN KEY (`residential_address_line1_id`) REFERENCES `string_value` (`id`),
  CONSTRAINT `FKnouhuax8xidoem89h54abgkvb` FOREIGN KEY (`status_id`) REFERENCES `string_value` (`id`),
  CONSTRAINT `FKoircx07vnkjdovxxgd9fxuulr` FOREIGN KEY (`affiliate_guid_id`) REFERENCES `string_value` (`id`),
  CONSTRAINT `FKonp4ebvgtk1carn6e0f69mvuw` FOREIGN KEY (`report_run_id`) REFERENCES `report_run` (`id`),
  CONSTRAINT `FKoykgvvxc9b5kbpjemuxehqso7` FOREIGN KEY (`postal_address_line2_id`) REFERENCES `string_value` (`id`),
  CONSTRAINT `FKq15m2qwqybib20m4mytinghli` FOREIGN KEY (`username_id`) REFERENCES `string_value` (`id`),
  CONSTRAINT `FKqk3dg9w18c8qru9hihwuikcbs` FOREIGN KEY (`postal_address_line3_id`) REFERENCES `string_value` (`id`),
  CONSTRAINT `FKqwfb5fm1ocga15nvse0s5ckwe` FOREIGN KEY (`postal_address_city_id`) REFERENCES `string_value` (`id`),
  CONSTRAINT `FKsr2980b0imcpxy221au88k7on` FOREIGN KEY (`last_name_id`) REFERENCES `string_value` (`id`),
  CONSTRAINT `FKsytxp1pxvmoxwpohbpp1lg3io` FOREIGN KEY (`residential_address_line2_id`) REFERENCES `string_value` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `string_value`
--

-- DROP TABLE IF EXISTS `string_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `string_value` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `users` bigint(20) NOT NULL,
  `value` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_value` (`value`)
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

-- Dump completed on 2019-07-01 13:34:46

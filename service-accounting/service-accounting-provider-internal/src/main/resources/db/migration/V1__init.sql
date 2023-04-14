-- MySQL dump 10.13  Distrib 5.7.12, for Win64 (x86_64)
--
-- Host: localhost    Database: lithium_accounting_internal
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
-- Table structure for table `account`
--

-- DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `balance_cents` bigint(20) NOT NULL,
  `version` int(11) NOT NULL,
  `account_code_id` bigint(20) NOT NULL,
  `account_type_id` bigint(20) NOT NULL,
  `currency_id` bigint(20) NOT NULL,
  `domain_id` bigint(20) NOT NULL,
  `owner_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_acc_all` (`currency_id`,`owner_id`,`domain_id`,`account_type_id`,`account_code_id`),
  KEY `idx_acc_balance` (`balance_cents`),
  KEY `FKsxu06qvphxc16hvonla1r0g7f` (`account_code_id`),
  KEY `FKgw84mgpacw9htdxcs2j1p7u6j` (`account_type_id`),
  KEY `FKpy61tgfe9h2a1r5pn6i6fy5j5` (`domain_id`),
  KEY `FKlijilgu3y8bx1rb3oirmqlw5k` (`owner_id`),
  CONSTRAINT `FK316pn109iutn6yqoxrqp09cpc` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`),
  CONSTRAINT `FKgw84mgpacw9htdxcs2j1p7u6j` FOREIGN KEY (`account_type_id`) REFERENCES `account_type` (`id`),
  CONSTRAINT `FKlijilgu3y8bx1rb3oirmqlw5k` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKpy61tgfe9h2a1r5pn6i6fy5j5` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`),
  CONSTRAINT `FKsxu06qvphxc16hvonla1r0g7f` FOREIGN KEY (`account_code_id`) REFERENCES `account_code` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `account_code`
--

-- DROP TABLE IF EXISTS `account_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_code` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_ac_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `account_label_value_constraint`
--

-- DROP TABLE IF EXISTS `account_label_value_constraint`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_label_value_constraint` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` int(11) NOT NULL,
  `account_id` bigint(20) NOT NULL,
  `label_value_id` bigint(20) NOT NULL,
  `transaction_entry_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_alvc_all` (`account_id`,`label_value_id`),
  KEY `FKb4kpxvxqq75ej6c9ed3nfkfja` (`label_value_id`),
  KEY `FKqjfk2vdw1sf0a1wcg2ew5a3k2` (`transaction_entry_id`),
  CONSTRAINT `FKb4kpxvxqq75ej6c9ed3nfkfja` FOREIGN KEY (`label_value_id`) REFERENCES `label_value` (`id`),
  CONSTRAINT `FKo7ku7bph4n0iiqxnmkdbfcvb6` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`),
  CONSTRAINT `FKqjfk2vdw1sf0a1wcg2ew5a3k2` FOREIGN KEY (`transaction_entry_id`) REFERENCES `transaction_entry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `account_type`
--

-- DROP TABLE IF EXISTS `account_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_at_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `currency`
--

-- DROP TABLE IF EXISTS `currency`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `currency` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_cur_code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=225 DEFAULT CHARSET=utf8;
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
  UNIQUE KEY `idx_label_name` (`name`)
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
-- Table structure for table `period`
--

-- DROP TABLE IF EXISTS `period`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `period` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date_end` datetime NOT NULL,
  `date_start` datetime NOT NULL,
  `day` int(11) NOT NULL,
  `granularity` int(11) NOT NULL,
  `month` int(11) NOT NULL,
  `open` bit(1) NOT NULL,
  `version` int(11) NOT NULL,
  `week` int(11) NOT NULL,
  `year` int(11) NOT NULL,
  `domain_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_pd_all` (`year`,`month`,`week`,`day`,`domain_id`),
  UNIQUE KEY `idx_pd_dates` (`date_start`,`date_end`),
  KEY `idx_pd_datestart` (`date_start`),
  KEY `idx_pd_dateend` (`date_end`),
  KEY `idx_pd_open` (`open`),
  KEY `idx_pd_granularity` (`granularity`),
  KEY `FKk1oj7pptmme05t9qyaay1d178` (`domain_id`),
  CONSTRAINT `FKk1oj7pptmme05t9qyaay1d178` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `summary_account`
--

-- DROP TABLE IF EXISTS `summary_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `summary_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `closing_balance_cents` bigint(20) NOT NULL,
  `credit_cents` bigint(20) NOT NULL,
  `damaged` bit(1) NOT NULL,
  `debit_cents` bigint(20) NOT NULL,
  `opening_balance_cents` bigint(20) NOT NULL,
  `tran_count` bigint(20) NOT NULL,
  `version` int(11) NOT NULL,
  `account_id` bigint(20) NOT NULL,
  `period_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_pd_all` (`period_id`,`account_id`),
  KEY `idx_pd_damaged` (`damaged`),
  KEY `FKqcjeskhuvgnagk22tracc2lm1` (`account_id`),
  CONSTRAINT `FKe0juxlee0cjej3ootv2oh0x24` FOREIGN KEY (`period_id`) REFERENCES `period` (`id`),
  CONSTRAINT `FKqcjeskhuvgnagk22tracc2lm1` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `summary_account_label_value`
--

-- DROP TABLE IF EXISTS `summary_account_label_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `summary_account_label_value` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `credit_cents` bigint(20) NOT NULL,
  `damaged` bit(1) NOT NULL,
  `debit_cents` bigint(20) NOT NULL,
  `tran_count` bigint(20) NOT NULL,
  `version` int(11) NOT NULL,
  `account_id` bigint(20) NOT NULL,
  `label_value_id` bigint(20) NOT NULL,
  `period_id` bigint(20) NOT NULL,
  `transaction_type_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_salv_all` (`period_id`,`transaction_type_id`,`account_id`,`label_value_id`),
  KEY `idx_salv_damaged` (`damaged`),
  KEY `FKtr16gg54qfwymfxjhwtfabjn1` (`account_id`),
  KEY `FK2i4ulopfctxumfb77yvc69fup` (`label_value_id`),
  KEY `FKg6fa9c4hpd0k99qx68c2tpot3` (`transaction_type_id`),
  CONSTRAINT `FK2i4ulopfctxumfb77yvc69fup` FOREIGN KEY (`label_value_id`) REFERENCES `label_value` (`id`),
  CONSTRAINT `FKg6fa9c4hpd0k99qx68c2tpot3` FOREIGN KEY (`transaction_type_id`) REFERENCES `transaction_type` (`id`),
  CONSTRAINT `FKib8ecp4m4iwe5ro878v4gerx1` FOREIGN KEY (`period_id`) REFERENCES `period` (`id`),
  CONSTRAINT `FKtr16gg54qfwymfxjhwtfabjn1` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `summary_account_transaction_type`
--

-- DROP TABLE IF EXISTS `summary_account_transaction_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `summary_account_transaction_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `credit_cents` bigint(20) NOT NULL,
  `damaged` bit(1) NOT NULL,
  `debit_cents` bigint(20) NOT NULL,
  `tran_count` bigint(20) NOT NULL,
  `version` int(11) NOT NULL,
  `account_id` bigint(20) NOT NULL,
  `period_id` bigint(20) NOT NULL,
  `transaction_type_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_sat_all` (`period_id`,`transaction_type_id`,`account_id`),
  KEY `idx_sat_damaged` (`damaged`),
  KEY `FKrll65krcphar6okf2guqfvmk2` (`account_id`),
  KEY `FKji7aat8spo6vrjgavg9yx233b` (`transaction_type_id`),
  CONSTRAINT `FK6pargy3vvm3g3736fcvudt1m1` FOREIGN KEY (`period_id`) REFERENCES `period` (`id`),
  CONSTRAINT `FKji7aat8spo6vrjgavg9yx233b` FOREIGN KEY (`transaction_type_id`) REFERENCES `transaction_type` (`id`),
  CONSTRAINT `FKrll65krcphar6okf2guqfvmk2` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `summary_domain`
--

-- DROP TABLE IF EXISTS `summary_domain`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `summary_domain` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `closing_balance_cents` bigint(20) NOT NULL,
  `credit_cents` bigint(20) NOT NULL,
  `debit_cents` bigint(20) NOT NULL,
  `opening_balance_cents` bigint(20) NOT NULL,
  `tag` int(11) NOT NULL,
  `tran_count` bigint(20) NOT NULL,
  `version` int(11) NOT NULL,
  `account_code_id` bigint(20) NOT NULL,
  `currency_id` bigint(20) NOT NULL,
  `period_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_pd_all` (`period_id`,`account_code_id`,`currency_id`),
  KEY `FKc1vkbsbo9pcr7mf1au38xdle` (`account_code_id`),
  KEY `FKc82f8e65j0fsocs15yofw15l4` (`currency_id`),
  CONSTRAINT `FKc1vkbsbo9pcr7mf1au38xdle` FOREIGN KEY (`account_code_id`) REFERENCES `account_code` (`id`),
  CONSTRAINT `FKc82f8e65j0fsocs15yofw15l4` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`),
  CONSTRAINT `FKsqyfd71klxkw8mxpdn0r6ovf6` FOREIGN KEY (`period_id`) REFERENCES `period` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `summary_domain_label_value`
--

-- DROP TABLE IF EXISTS `summary_domain_label_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `summary_domain_label_value` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `credit_cents` bigint(20) NOT NULL,
  `debit_cents` bigint(20) NOT NULL,
  `tag` int(11) NOT NULL,
  `tran_count` bigint(20) NOT NULL,
  `version` int(11) NOT NULL,
  `account_code_id` bigint(20) NOT NULL,
  `currency_id` bigint(20) NOT NULL,
  `label_value_id` bigint(20) NOT NULL,
  `period_id` bigint(20) NOT NULL,
  `transaction_type_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_pd_all` (`period_id`,`transaction_type_id`,`account_code_id`,`label_value_id`,`currency_id`),
  KEY `FKifkss328b2pamahrcdn1mhok` (`account_code_id`),
  KEY `FKalcf1fu6aai0ad7rvhulev57d` (`currency_id`),
  KEY `FKdgpulqquv5y8esxtr20mvjhpo` (`label_value_id`),
  KEY `FKiisngy330ue3hhg29wma83c7g` (`transaction_type_id`),
  CONSTRAINT `FKalcf1fu6aai0ad7rvhulev57d` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`),
  CONSTRAINT `FKaw3meq0ojnqrinrerubqaqqbs` FOREIGN KEY (`period_id`) REFERENCES `period` (`id`),
  CONSTRAINT `FKdgpulqquv5y8esxtr20mvjhpo` FOREIGN KEY (`label_value_id`) REFERENCES `label_value` (`id`),
  CONSTRAINT `FKifkss328b2pamahrcdn1mhok` FOREIGN KEY (`account_code_id`) REFERENCES `account_code` (`id`),
  CONSTRAINT `FKiisngy330ue3hhg29wma83c7g` FOREIGN KEY (`transaction_type_id`) REFERENCES `transaction_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `summary_domain_transaction_type`
--

-- DROP TABLE IF EXISTS `summary_domain_transaction_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `summary_domain_transaction_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `credit_cents` bigint(20) NOT NULL,
  `debit_cents` bigint(20) NOT NULL,
  `tag` int(11) NOT NULL,
  `tran_count` bigint(20) NOT NULL,
  `version` int(11) NOT NULL,
  `account_code_id` bigint(20) NOT NULL,
  `currency_id` bigint(20) NOT NULL,
  `period_id` bigint(20) NOT NULL,
  `transaction_type_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_pd_all` (`period_id`,`transaction_type_id`,`account_code_id`,`currency_id`),
  KEY `FKcx6155q751905kuhs0y9x4rb` (`account_code_id`),
  KEY `FKadl1ldg2cw6im6la2lg8rind5` (`currency_id`),
  KEY `FK7b7kojcvjd1ftw2f9rjsf6b88` (`transaction_type_id`),
  CONSTRAINT `FK7b7kojcvjd1ftw2f9rjsf6b88` FOREIGN KEY (`transaction_type_id`) REFERENCES `transaction_type` (`id`),
  CONSTRAINT `FKadl1ldg2cw6im6la2lg8rind5` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`),
  CONSTRAINT `FKcx6155q751905kuhs0y9x4rb` FOREIGN KEY (`account_code_id`) REFERENCES `account_code` (`id`),
  CONSTRAINT `FKke4lmps2en53osh2w30g5kevy` FOREIGN KEY (`period_id`) REFERENCES `period` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `transaction`
--

-- DROP TABLE IF EXISTS `transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cancelled` bit(1) NOT NULL,
  `closed_on` datetime DEFAULT NULL,
  `created_on` datetime NOT NULL,
  `open` bit(1) NOT NULL,
  `version` int(11) NOT NULL,
  `author_id` bigint(20) NOT NULL,
  `transaction_type_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_trx_created` (`created_on`),
  KEY `idx_trx_closed` (`closed_on`),
  KEY `idx_trx_open` (`open`),
  KEY `idx_trx_cancelled` (`cancelled`),
  KEY `FKa04er1mr2maie94v3wg4mx6mh` (`author_id`),
  KEY `FKnl0vpl01y6vu03hkpi4xupugo` (`transaction_type_id`),
  CONSTRAINT `FKa04er1mr2maie94v3wg4mx6mh` FOREIGN KEY (`author_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKnl0vpl01y6vu03hkpi4xupugo` FOREIGN KEY (`transaction_type_id`) REFERENCES `transaction_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `transaction_comment`
--

-- DROP TABLE IF EXISTS `transaction_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `comment` varchar(255) NOT NULL,
  `transaction_id` bigint(20) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_tc_tranid` (`transaction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `transaction_entry`
--

-- DROP TABLE IF EXISTS `transaction_entry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction_entry` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `amount_cents` bigint(20) NOT NULL,
  `date` datetime NOT NULL,
  `transaction_id` bigint(20) NOT NULL,
  `version` int(11) NOT NULL,
  `account_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_te_tranid` (`transaction_id`),
  KEY `idx_tx_date` (`date`),
  KEY `idx_tx_amount` (`amount_cents`),
  KEY `FKstv5ybbishi98km2or3r8du5n` (`account_id`),
  CONSTRAINT `FKstv5ybbishi98km2or3r8du5n` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `transaction_label_value`
--

-- DROP TABLE IF EXISTS `transaction_label_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction_label_value` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `transaction_id` bigint(20) NOT NULL,
  `version` int(11) NOT NULL,
  `label_value_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_tlv_tranid` (`transaction_id`),
  KEY `FKt81n7vo653e3ofrkpu1ocm4ab` (`label_value_id`),
  CONSTRAINT `FKt81n7vo653e3ofrkpu1ocm4ab` FOREIGN KEY (`label_value_id`) REFERENCES `label_value` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `transaction_type`
--

-- DROP TABLE IF EXISTS `transaction_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_tt_code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `transaction_type_account`
--

-- DROP TABLE IF EXISTS `transaction_type_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction_type_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_type_code` varchar(255) NOT NULL,
  `credit` bit(1) NOT NULL,
  `debit` bit(1) NOT NULL,
  `version` int(11) NOT NULL,
  `transaction_type_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_tta_all` (`account_type_code`,`transaction_type_id`),
  KEY `FKb2ipffw9mvs19u2r4qog9svuf` (`transaction_type_id`),
  CONSTRAINT `FKb2ipffw9mvs19u2r4qog9svuf` FOREIGN KEY (`transaction_type_id`) REFERENCES `transaction_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `transaction_type_label`
--

-- DROP TABLE IF EXISTS `transaction_type_label`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction_type_label` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_type_code` varchar(255) DEFAULT NULL,
  `label` varchar(255) NOT NULL,
  `summarize` bit(1) NOT NULL,
  `version` int(11) NOT NULL,
  `transaction_type_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_ttl_all` (`label`,`transaction_type_id`),
  KEY `FK1nb7nby9b1akmr5ayxpcc5hvs` (`transaction_type_id`),
  CONSTRAINT `FK1nb7nby9b1akmr5ayxpcc5hvs` FOREIGN KEY (`transaction_type_id`) REFERENCES `transaction_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

-- DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `guid` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_guid` (`guid`)
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

-- Dump completed on 2016-11-17 11:28:25

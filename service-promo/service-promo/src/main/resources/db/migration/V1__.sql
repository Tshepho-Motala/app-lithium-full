-- MySQL dump 10.13  Distrib 5.7.34, for Linux (x86_64)
--
-- Host: localhost    Database: lithium_promotions
-- ------------------------------------------------------
-- Server version	5.7.34

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
-- Table structure for table `activity`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `activity` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT,
                            `name` varchar(255) NOT NULL,
                            `promo_provider_id` bigint(20) NOT NULL,
                            `requires_value` bit(1) DEFAULT b'1',
                            `version` int(11) DEFAULT 0,
                            PRIMARY KEY (`id`),
                            KEY `idx_provider_activity` (`promo_provider_id`,`name`),
                            CONSTRAINT `fk_activity_promotion_id` FOREIGN KEY (`promo_provider_id`) REFERENCES `promo_provider` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `activity_extra_field`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `activity_extra_field` (
                                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                        `activity_id` bigint(20) NOT NULL,
                                        `name` varchar(100) NOT NULL,
                                        `description` varchar(2000),
                                        `data_type` varchar(50) DEFAULT NULL,
                                        `field_type` varchar(50) DEFAULT NULL,
                                        `version` int(11) DEFAULT 0,
                                        `fetch_external_data` bit(1) DEFAULT b'0',
                                        PRIMARY KEY (`id`),
                                        UNIQUE KEY `idx_activity_extra_field_name` (`activity_id`,`name`),
                                        KEY `idx_activity_extra_field_activity` (`activity_id`,`name`),
                                        CONSTRAINT `fk_activity_extra_field_activity_id` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `activity_extra_field_rule_value`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `activity_extra_field_rule_value` (
                                                   `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                                   `activity_extra_field_id` bigint(20) NOT NULL,
                                                   `rule_id` bigint(20) NOT NULL,
                                                   `value` varchar(255) NOT NULL,
                                                   `deleted` BIT(1) DEFAULT 0,
                                                   `version` int(11) DEFAULT 0,
                                                   PRIMARY KEY (`id`),
                                                   KEY `activity_extra_field_value_rule_id` (`rule_id`),
                                                   KEY `idx_activity_extra_field_rule` (`activity_extra_field_id`,`rule_id`),
                                                   CONSTRAINT `activity_extra_field_value_promo_provider_extra_field_id` FOREIGN KEY (`activity_extra_field_id`) REFERENCES `activity_extra_field` (`id`),
                                                   CONSTRAINT `activity_extra_field_value_rule_id` FOREIGN KEY (`rule_id`) REFERENCES `rule` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `challenge`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `challenge` (
                             `id` bigint(20) NOT NULL AUTO_INCREMENT,
                             `description` varchar(255) DEFAULT NULL,
                             `version` int(11) NOT NULL,
                             `icon_id` bigint(20) DEFAULT NULL,
                             `reward_id` bigint(20) DEFAULT NULL,
                             `challenge_group_id` bigint(20) DEFAULT NULL,
                             `sequenceNumber` int(11) DEFAULT NULL,
                             `deleted` bit(1) DEFAULT b'0',
                             `sequence_number` int(11) DEFAULT NULL,
                             PRIMARY KEY (`id`),
                             KEY `FKmyada1rqdh4eaksa5evxvqpfn` (`icon_id`),
                             KEY `FKdd7u96dq39v8u43fa77ixxsak` (`reward_id`),
                             KEY `fk_challenge_challenge_group_id` (`challenge_group_id`),
                             CONSTRAINT `FKdd7u96dq39v8u43fa77ixxsak` FOREIGN KEY (`reward_id`) REFERENCES `reward` (`id`),
                             CONSTRAINT `FKmyada1rqdh4eaksa5evxvqpfn` FOREIGN KEY (`icon_id`) REFERENCES `graphic` (`id`),
                             CONSTRAINT `fk_challenge_challenge_group_id` FOREIGN KEY (`challenge_group_id`) REFERENCES `challenge_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `challenge_group`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `challenge_group` (
                                   `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                   `promotion_revision_id` bigint(20) NOT NULL,
                                   `sequenced` bit(1) DEFAULT b'0',
                                   `version` int(11) DEFAULT 0,
                                   PRIMARY KEY (`id`),
                                   KEY `idx_promotion_id` (`promotion_revision_id`),
                                   `deleted` bit(1) DEFAULT b'0',
                                   CONSTRAINT `fk_challenge_group_promotion_id` FOREIGN KEY (`promotion_revision_id`) REFERENCES `promotion_revision` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `domain`
--

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
-- Table structure for table `graphic`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `graphic` (
                           `id` bigint(20) NOT NULL AUTO_INCREMENT,
                           `image` longblob NOT NULL,
                           `name` varchar(255) NOT NULL,
                           `size` bigint(20) DEFAULT NULL,
                           `type` varchar(255) NOT NULL,
                           `version` int(11) NOT NULL,
                           `deleted` BIT(1) DEFAULT 0,
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `label`
--

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
-- Table structure for table `promotion`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `promotion` (
                           `id` bigint(20) NOT NULL AUTO_INCREMENT,
                           `version` int(11) NOT NULL,
                           `current_id` bigint(20) DEFAULT NULL,
                           `edit_id` bigint(20) DEFAULT NULL,
                           `editor_id` bigint(20) DEFAULT NULL,
                           `deleted` bit(1) DEFAULT 0,
                           PRIMARY KEY (`id`),
                           KEY `FKsjmca40c6i15rps0nd0bs0mqx` (`current_id`),
                           KEY `FKjgrk7kfdlvyx3dem98i7m6p4t` (`edit_id`),
                           KEY `FKpneeg416ml8pqt7cymb1vwvph` (`editor_id`),
                           CONSTRAINT `FKjgrk7kfdlvyx3dem98i7m6p4t` FOREIGN KEY (`edit_id`) REFERENCES `promotion_revision` (`id`),
                           CONSTRAINT `FKpneeg416ml8pqt7cymb1vwvph` FOREIGN KEY (`editor_id`) REFERENCES `user` (`id`),
                           CONSTRAINT `FKsjmca40c6i15rps0nd0bs0mqx` FOREIGN KEY (`current_id`) REFERENCES `promotion_revision` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `promotion_revision`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `promotion_revision` (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                    `description` varchar(255) DEFAULT NULL,
                                    `end_date` datetime DEFAULT NULL,
                                    `name` varchar(255) NOT NULL,
                                    `start_date` datetime DEFAULT NULL,
                                    `version` int(11) NOT NULL,
                                    `domain_id` bigint(20) NOT NULL,
                                    `promotion_id` bigint(20) NOT NULL,
                                    `reward_id` bigint(20) DEFAULT NULL,
                                    `xp_level` int(11) DEFAULT NULL,
                                    `recurrence_pattern` varchar(255) DEFAULT NULL,
                                    `redeemable_in_total` int(11) DEFAULT NULL,
                                    `redeemable_in_event` int(11) DEFAULT NULL,
                                    `event_duration` int(11) DEFAULT NULL,
                                    `event_duration_granularity` int(11) DEFAULT NULL,
                                    `depends_on_promotion_id` bigint(20) DEFAULT NULL,
                                    `exclusive` bit(1) NOT NULL DEFAULT 0,
                                    `deleted` BIT(1) DEFAULT 0,
                                    PRIMARY KEY (`id`),
                                    KEY `idx_domain` (`domain_id`),
                                    KEY `FKf1f2s3yeiiyxpnojdbhm21k36` (`promotion_id`),
                                    KEY `FKe1fvstw46r67ic85ash6ed5lc` (`reward_id`),
                                    KEY `idx_domain_start_end_date` (`domain_id`,`start_date`,`end_date`,`xp_level`),
                                    KEY `idx_domain_sequence_number` (`domain_id`,`xp_level`),
                                    KEY `fk_promotion_revision_depends_on_promotion_id` (`depends_on_promotion_id`),
                                    CONSTRAINT `FKe1fvstw46r67ic85ash6ed5lc` FOREIGN KEY (`reward_id`) REFERENCES `reward` (`id`),
                                    CONSTRAINT `FKf1f2s3yeiiyxpnojdbhm21k36` FOREIGN KEY (`promotion_id`) REFERENCES `promotion` (`id`),
                                    CONSTRAINT `FKjc0sjkbv2cymd28p0rhgyxxgf` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`),
                                    CONSTRAINT `fk_promotion_revision_depends_on_promotion_id` FOREIGN KEY (`depends_on_promotion_id`) REFERENCES `promotion` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `promotion_stat`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `promotion_stat` (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                `action` varchar(255) NOT NULL,
                                `name` varchar(255) NOT NULL,
                                `type` varchar(255) NOT NULL,
                                `version` int(11) NOT NULL,
                                `owner_id` bigint(20) NOT NULL,
                                `deleted` BIT(1) DEFAULT 0,
                                PRIMARY KEY (`id`),
                                KEY `FKlkfumo6v2fh7hqxn2tbq6wsl8` (`owner_id`),
                                KEY `idx_name` (`name`,`owner_id`),
                                CONSTRAINT `FKlkfumo6v2fh7hqxn2tbq6wsl8` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `promotion_stat_entry`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `promotion_stat_entry` (
                                      `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                      `entry_date` datetime DEFAULT NULL,
                                      `value` bigint(20) NOT NULL,
                                      `version` int(11) NOT NULL,
                                      `promotion_stat_id` bigint(20) NOT NULL,
                                      PRIMARY KEY (`id`),
                                      KEY `FK5rj6ewgjeambl7gor1wowam0i` (`promotion_stat_id`),
                                      CONSTRAINT `FK5rj6ewgjeambl7gor1wowam0i` FOREIGN KEY (`promotion_stat_id`) REFERENCES `promotion_stat` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `promotion_stat_summary`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `promotion_stat_summary` (
                                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                        `version` int(11) NOT NULL,
                                        `promotion_stat_id` bigint(20) NOT NULL,
                                        `owner_id` bigint(20) NOT NULL,
                                        `period_id` bigint(20) NOT NULL,
                                        `value` bigint(20) NOT NULL,
                                        PRIMARY KEY (`id`),
                                        KEY `FK4g8542tycd1h0en9pu97sp5om` (`promotion_stat_id`),
                                        KEY `FKo16efnha1acf775tcfvkddnhe` (`owner_id`),
                                        KEY `FKhmsjptn7t88b52x9b6vif12wq` (`period_id`),
                                        CONSTRAINT `FK4g8542tycd1h0en9pu97sp5om` FOREIGN KEY (`promotion_stat_id`) REFERENCES `promotion_stat` (`id`),
                                        CONSTRAINT `FKhmsjptn7t88b52x9b6vif12wq` FOREIGN KEY (`period_id`) REFERENCES `period` (`id`),
                                        CONSTRAINT `FKo16efnha1acf775tcfvkddnhe` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `period`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `period` (
                          `id` bigint(20) NOT NULL AUTO_INCREMENT,
                          `date_end` datetime NOT NULL,
                          `date_start` datetime NOT NULL,
                          `day` int(11) NOT NULL,
                          `granularity` int(11) NOT NULL,
                          `hour` int(11) NOT NULL,
                          `month` int(11) NOT NULL,
                          `version` int(11) NOT NULL,
                          `week` int(11) NOT NULL,
                          `year` int(11) NOT NULL,
                          `domain_id` bigint(20) NOT NULL,
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `idx_pd_all` (`year`,`month`,`week`,`day`,`hour`,`domain_id`),
                          UNIQUE KEY `idx_pd_dates` (`date_start`,`date_end`,`domain_id`),
                          KEY `idx_pd_datestart` (`date_start`),
                          KEY `idx_pd_dateend` (`date_end`),
                          KEY `idx_pd_granularity` (`granularity`),
                          KEY `FKk1oj7pptmme05t9qyaay1d178` (`domain_id`),
                          CONSTRAINT `FKk1oj7pptmme05t9qyaay1d178` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `promo_provider`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `promo_provider` (
                                  `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                  `name` varchar(100) DEFAULT NULL,
                                  `url` varchar(255) NOT NULL,
                                  `category` varchar(50) NOT NULL,
                                  PRIMARY KEY (`id`),
                                  KEY `idx_promo_provider_url_category` (`url`,`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `promo_revision_exclusive_players`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `promo_revision_exclusive_players` (
                                                    `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                                    `promotion_revision_id` bigint(20) NOT NULL,
                                                    `player_id` bigint(20) NOT NULL,
                                                    PRIMARY KEY (`id`),
                                                    UNIQUE KEY `UK_ev1tgbjoobw1n4gvx4w821byx` (`player_id`),
                                                    KEY `FKcxbf0ukwrmduolu0q2tfcqlwp` (`promotion_revision_id`),
                                                    CONSTRAINT `FKctcfslfroi3gsbsl70hr3uk9e` FOREIGN KEY (`player_id`) REFERENCES `user` (`id`),
                                                    CONSTRAINT `FKcxbf0ukwrmduolu0q2tfcqlwp` FOREIGN KEY (`promotion_revision_id`) REFERENCES `promotion_revision` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reward`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reward` (
                          `id` bigint(20) NOT NULL AUTO_INCREMENT,
                          `version` int(11) NOT NULL,
                          `challenge_id` bigint(20) DEFAULT NULL,
                          `promotion_revision_id` bigint(20) DEFAULT NULL,
                          `reward_id` bigint(20) DEFAULT NULL,
                          PRIMARY KEY (`id`),
                          KEY `FK8m01x5eev9h271yjp4q58sh2h` (`challenge_id`),
                          KEY `FK12id7q7b5psomklcj9p1kv0jo` (`promotion_revision_id`),
                          CONSTRAINT `FK12id7q7b5psomklcj9p1kv0jo` FOREIGN KEY (`promotion_revision_id`) REFERENCES `promotion` (`id`),
                          CONSTRAINT `FK8m01x5eev9h271yjp4q58sh2h` FOREIGN KEY (`challenge_id`) REFERENCES `challenge` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rule`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rule` (
                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                        `value` bigint(20) NOT NULL,
                        `version` int(11) NOT NULL,
                        `challenge_id` bigint(20) DEFAULT NULL,
                        `promo_provider_id` bigint(20) DEFAULT NULL,
                        `operation` varchar(50) DEFAULT NULL,
                        `activity_id` bigint(20) DEFAULT NULL,
                        `deleted` bit(1) DEFAULT 0,
                        PRIMARY KEY (`id`),
                        KEY `FKtk1mvk5ufhqiib95243o7smd5` (`challenge_id`),
                        KEY `fk_rule_activity_id` (`activity_id`),
                        KEY `FKgm3l9ovk59o9oo49m15srcx6e` (`promo_provider_id`),
                        CONSTRAINT `FKgm3l9ovk59o9oo49m15srcx6e` FOREIGN KEY (`promo_provider_id`) REFERENCES `promo_provider` (`id`),
                        CONSTRAINT `FKtk1mvk5ufhqiib95243o7smd5` FOREIGN KEY (`challenge_id`) REFERENCES `challenge` (`id`),
                        CONSTRAINT `fk_rule_activity_id` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                        `guid` varchar(255) NOT NULL,
                        `timezone` varchar(255) DEFAULT NULL,
                        `version` int(11) NOT NULL,
                        `test_account` bit(1) DEFAULT 0,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `idx_guid` (`guid`),
                        KEY `idx_user_test` (`test_account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_category`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_category` (
                                 `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                 `promotion_revision_id` bigint(20) NOT NULL,
                                 `user_category_id` bigint(20) NOT NULL,
                                 `category_type` varchar(20) NOT NULL,
                                 PRIMARY KEY (`id`),
                                 KEY `idx_unique_promotion_category` (`promotion_revision_id`,`user_category_id`),
                                 CONSTRAINT `user_category_ibfk_1` FOREIGN KEY (`promotion_revision_id`) REFERENCES `promotion_revision` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_promotion`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_promotion` (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                `active` bit(1) DEFAULT NULL,
                                `completed` datetime DEFAULT NULL,
                                `expired` bit(1) DEFAULT NULL,
                                `promotion_complete` bit(1) DEFAULT NULL,
                                `percentage` decimal(19,2) DEFAULT NULL,
                                `started` datetime DEFAULT NULL,
                                `timezone` varchar(255) DEFAULT NULL,
                                `version` int(11) NOT NULL,
                                `promotion_revision_id` bigint(20) NOT NULL,
                                `period_id` bigint(20) NOT NULL,
                                `user_id` bigint(20) NOT NULL,
                                PRIMARY KEY (`id`),
                                KEY `FKcsj9drmwt8g7tlq4xxj0qltgw` (`promotion_revision_id`),
                                KEY `FKhwecdosjajnkonnuxvwonrp42` (`period_id`),
                                KEY `idx_user_promotion` (`user_id`,`promotion_revision_id`,`period_id`),
                                CONSTRAINT `FKcsj9drmwt8g7tlq4xxj0qltgw` FOREIGN KEY (`promotion_revision_id`) REFERENCES `promotion_revision` (`id`),
                                CONSTRAINT `FKfsmo8ipks83kcuqtky39hxyvc` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
                                CONSTRAINT `FKhwecdosjajnkonnuxvwonrp42` FOREIGN KEY (`period_id`) REFERENCES `period` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_promotion_challenge`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_promotion_challenge` (
                                          `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                          `challenge_complete` bit(1) DEFAULT NULL,
                                          `completed` datetime DEFAULT NULL,
                                          `percentage` decimal(19,2) DEFAULT NULL,
                                          `started` datetime DEFAULT NULL,
                                          `version` int(11) NOT NULL,
                                          `challenge_id` bigint(20) NOT NULL,
                                          `user_promotion_id` bigint(20) NOT NULL,
                                          `user_promotion_challenge_group_id` bigint(20) DEFAULT NULL,
                                          PRIMARY KEY (`id`),
                                          KEY `FK68kxn2ees8ku5kpelsq4i54qr` (`challenge_id`),
                                          KEY `FKq6ooklhgklneu7tqn6cbiwwkh` (`user_promotion_id`),
                                          KEY `fk_user_promotion_challenge_user_promotion_challenge_group_id` (`user_promotion_challenge_group_id`),
                                          CONSTRAINT `FK68kxn2ees8ku5kpelsq4i54qr` FOREIGN KEY (`challenge_id`) REFERENCES `challenge` (`id`),
                                          CONSTRAINT `FKq6ooklhgklneu7tqn6cbiwwkh` FOREIGN KEY (`user_promotion_id`) REFERENCES `user_promotion` (`id`),
                                          CONSTRAINT `fk_user_promotion_challenge_user_promotion_challenge_group_id` FOREIGN KEY (`user_promotion_challenge_group_id`) REFERENCES `user_promotion_challenge_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_promotion_challenge_rule`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_promotion_challenge_rule` (
                                               `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                               `completed` datetime DEFAULT NULL,
                                               `percentage` decimal(19,2) DEFAULT NULL,
                                               `rule_complete` bit(1) DEFAULT NULL,
                                               `started` datetime DEFAULT NULL,
                                               `version` int(11) NOT NULL,
                                               `promotion_stat_id` bigint(20) DEFAULT NULL,
                                               `rule_id` bigint(20) NOT NULL,
                                               `user_promotion_challenge_id` bigint(20) NOT NULL,
                                               PRIMARY KEY (`id`),
                                               KEY `FKkrsoyx86mbkbkqi53q9i0re9p` (`promotion_stat_id`),
                                               KEY `FK89otqnto1pjpa76bentf4x0tf` (`rule_id`),
                                               KEY `FKalr5k0t0qqlcnq3hhfr3l6vfj` (`user_promotion_challenge_id`),
                                               CONSTRAINT `FK89otqnto1pjpa76bentf4x0tf` FOREIGN KEY (`rule_id`) REFERENCES `rule` (`id`),
                                               CONSTRAINT `FKalr5k0t0qqlcnq3hhfr3l6vfj` FOREIGN KEY (`user_promotion_challenge_id`) REFERENCES `user_promotion_challenge` (`id`),
                                               CONSTRAINT `FKkrsoyx86mbkbkqi53q9i0re9p` FOREIGN KEY (`promotion_stat_id`) REFERENCES `promotion_stat` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_promotion_challenge_group`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_promotion_challenge_group` (
                                                  `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                                  `user_promotion_id` bigint(20) NOT NULL,
                                                  `challenge_group_id` bigint(20) NOT NULL,
                                                  `completed` datetime DEFAULT NULL,
                                                  `percentage` decimal(19,2) DEFAULT NULL,
                                                  `version` int(11) DEFAULT 0,
                                                  PRIMARY KEY (`id`),
                                                  KEY `fk_challenge_group_challenge_group_id` (`challenge_group_id`),
                                                  KEY `FK7sf9ti0r7tiypv3o6mtt3okb8` (`user_promotion_id`),
                                                  CONSTRAINT `FK7sf9ti0r7tiypv3o6mtt3okb8` FOREIGN KEY (`user_promotion_id`) REFERENCES `user_promotion` (`id`),
                                                  CONSTRAINT `fk_challenge_group_challenge_group_id` FOREIGN KEY (`challenge_group_id`) REFERENCES `challenge_group` (`id`)
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

-- Dump completed on 2022-09-06 17:22:03

-- MySQL dump 10.13  Distrib 5.7.12, for Win64 (x86_64)
--
-- Host: localhost    Database: lithium_user_search
-- ------------------------------------------------------
-- Server version	5.7.13-log

--
-- Table structure for table `user`
--

-- DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `guid` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_guid` (`guid`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

--
-- Table structure for table `current_account_balance`
--

-- DROP TABLE IF EXISTS `current_account_balance`;
CREATE TABLE `current_account_balance` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `current_account_balance` bigint(20) NOT NULL,
    `user_id` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `FK_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE INDEX `idx_current_account_balance` ON `current_account_balance` (`current_account_balance`) ALGORITHM INPLACE LOCK NONE;

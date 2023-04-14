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
-- Table structure for table `card`
--
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hexopay_customer` (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                    `email` varchar(255) DEFAULT NULL,
                                    `ip` varchar(255) DEFAULT NULL,
                                    `birth_date` varchar(255) DEFAULT NULL,
                                    `version` int(11) NOT NULL,
                                    UNIQUE KEY `email_idx` (`email`),
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hexopay_card` (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                `holder` varchar(255) NOT NULL,
                                `stamp` varchar(255) NOT NULL,
                                `token` varchar(255) NOT NULL,
                                `brand` varchar(255) NOT NULL,
                                `last_4` varchar(10) NOT NULL,
                                `first_1` varchar(10) NOT NULL,
                                `exp_month` int NOT NULL,
                                `exp_year` int NOT NULL,
                                `cvv` varchar(10) NOT NULL,
                                `version` int(11) NOT NULL,
                                `customer_id` bigint(20) NOT NULL,
                                `number` varchar(255) NOT NULL,
                                `secured` bit(1) DEFAULT 1,
                                PRIMARY KEY (`id`),
                                UNIQUE KEY `stamp_customer_idx` (`stamp`, `customer_id`),
                                UNIQUE KEY `token_idx` (`token`),
                                CONSTRAINT `FK_hexopay_customer_id` FOREIGN KEY (`customer_id`) REFERENCES `hexopay_customer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hexopay_transaction` (
                                       `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                       `uid` varchar(255) DEFAULT NULL,
                                       `tracking_id` varchar(255) DEFAULT NULL,
                                       `transaction_token_id` bigint(20) DEFAULT NULL,
                                       `amount` bigint(20) NOT NULL,
                                       `currency` varchar(255) NOT NULL,
                                       `created_at` datetime DEFAULT NULL,
                                       `type` varchar(255) DEFAULT NULL,
                                       `version` int(11) NOT NULL,
                                       `status` varchar(255) NOT NULL,
                                       `card_id` bigint(20) DEFAULT NULL,
                                       `message` varchar(255) DEFAULT NULL,
                                       `return_url` varchar(255) DEFAULT NULL,
                                       `notification_url` varchar(255) DEFAULT NULL,
                                       `threed_secure` bit(1) DEFAULT 1,
                                       `scenario` int DEFAULT 0,
                                       `ttl` bigint(20) DEFAULT -1,
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `uid_idx` (`uid`),
                                       CONSTRAINT `FK_hexopay_card_id` FOREIGN KEY (`card_id`) REFERENCES `hexopay_card` (`id`),
                                       CONSTRAINT `FK_hexopay_tran_token_id` FOREIGN KEY (`transaction_token_id`) REFERENCES `hexopay_transaction_token` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hexopay_transaction_token` (
                                       `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                       `tracking_id` varchar(255) DEFAULT NULL,
                                       `token` varchar(255) DEFAULT NULL,
                                       `amount` bigint(20) NOT NULL,
                                       `currency` varchar(255) NOT NULL,
                                       `created_at` datetime DEFAULT NULL,
                                       `type` varchar(255) DEFAULT NULL,
                                       `version` int(11) NOT NULL,
                                       `status` varchar(255) NOT NULL,
                                       `customer_id` bigint(20) DEFAULT NULL,
                                       `transaction_id` bigint(20) DEFAULT NULL,
                                       `return_url` varchar(255) DEFAULT NULL,
                                       `notification_url` varchar(255) DEFAULT NULL,
                                       `ttl` bigint(20) DEFAULT -1,
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `token_idx` (`token`),
                                       CONSTRAINT `FK_hexopay_token_customer_id` FOREIGN KEY (`customer_id`) REFERENCES `hexopay_customer` (`id`),
                                       CONSTRAINT `FK_hexopay_token_transaction_id` FOREIGN KEY (`transaction_id`) REFERENCES `hexopay_transaction` (`id`)
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

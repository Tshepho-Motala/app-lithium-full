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
-- Table structure for table `customer`
--
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `smartcash_customer` (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                `msisdn` varchar(255) NOT NULL,
                                `wallet_id` varchar(255) NOT NULL,
                                `last_name` varchar(255) NOT NULL,
                                `first_name` varchar(255) NOT NULL,
                                `middle_name` varchar(255),
                                `version` int(11) NOT NULL,
                                PRIMARY KEY (`id`),
                                UNIQUE KEY `msisdn_idx` (`msisdn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `smartcash_transaction` (
                                       `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                       `reference` varchar(255) DEFAULT NULL,
                                       `amount` varchar(255) NOT NULL,
                                       `currency` varchar(255) NOT NULL,
                                       `country` varchar(255) NOT NULL,
                                       `created_at` datetime DEFAULT NULL,
                                       `type` varchar(255) DEFAULT NULL,
                                       `version` int(11) NOT NULL,
                                       `status` varchar(255) NOT NULL,
                                       `customer_id` bigint(20) DEFAULT NULL,
                                       `message` varchar(255) DEFAULT NULL,
                                       `scenario` int DEFAULT 0,
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `reference_idx` (`reference`),
                                       CONSTRAINT `FK_smartcash_customer_id` FOREIGN KEY (`customer_id`) REFERENCES `smartcash_customer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO `smartcash_customer`(`msisdn`,
                                `wallet_id`,
                                `last_name`,
                                `first_name`,
                                `middle_name`,
                                `version`)
VALUES  ('8022220282','4932375217','Oyemade','Temitope','', 0),
        ('8022221041','3021847334','Echendu','Victor','', 0),
        ('8022221042','3021847335','Denzel','Washington','', 0),
        ('8022221043','3021847336','Tom','Hanks','', 0),
        ('8022221044','3021847337','Marilyn','Monroe','', 0),
        ('8022221045','3021847338','Robert','De Niro','', 0),
        ('8022221046','3021847339','Bette','Davis','', 0),
        ('8022221047','3021847340','Natalie','Portman','', 0),
        ('8022221048','3021847341','Gene','Hackman','', 0),
        ('8022221049','3021847342','Heath','Ledger','', 0),
        ('8022221050','3021847343','Katharine','Hepburn','', 0),
        ('8022221051','3021847344','Tom','Cruise','', 0);

/*!40101 SET character_set_client = @saved_cs_client */;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

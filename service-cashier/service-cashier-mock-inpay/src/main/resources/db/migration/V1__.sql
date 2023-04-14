/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS = @@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION = @@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE = @@TIME_ZONE */;
/*!40103 SET TIME_ZONE = '+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;

/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;

CREATE TABLE `inpay_debtor_account`
(
    `id`                bigint(20)   NOT NULL AUTO_INCREMENT,
    `version`           int(11)      NOT NULL,
    `debtor_account_id` bigint(20)   NOT NULL,
    `scheme_name`       varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;

CREATE TABLE `inpay_reason`
(
    `id`          bigint(20)   NOT NULL AUTO_INCREMENT,
    `version`     int(11)      NOT NULL,
    `kind`        varchar(255) NOT NULL,
    `category`    varchar(255) NOT NULL,
    `code`        varchar(255) NOT NULL,
    `message`     varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;

CREATE TABLE `inpay_transaction`
(
    `id`                     bigint(20)   NOT NULL AUTO_INCREMENT,
    `version`                int(11)      NOT NULL,
    `debtor_account_id`      bigint(20)   NOT NULL,
    `end_to_end_id`          varchar(255) NOT NULL,
    `inpay_unique_reference` varchar(255) NOT NULL,
    `amount`                 bigint(20)   NOT NULL,
    `currency`               varchar(255) NOT NULL,
    `timestamp`              datetime     NOT NULL,
    `state`                  varchar(255) NOT NULL,
    `x_request_id`           varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `FK_inpay_debtor_account_id` FOREIGN KEY (`debtor_account_id`) REFERENCES `inpay_debtor_account` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;

CREATE TABLE `inpay_transaction_reason`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT,
    `transaction_id` bigint(20) NOT NULL,
    `reason_id`      bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `FK_inpay_transaction_id` FOREIGN KEY (`transaction_id`) references `inpay_transaction` (`id`),
    CONSTRAINT `FK_inpay_reson_id` FOREIGN KEY (`reason_id`) references `inpay_reason` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;

INSERT INTO `inpay_reason`
VALUES (1, 0, 'payment_request_rejected', 'schema_validation_error', 'invalid_creditor_account', 'Rejected - Invalid creditor account'),
       (2, 0, 'refund_returned', 'beneficiary_request', 'beneficiary_request_returned_payment_as_per_beneficiary_request', 'Returned - Returned payment as per beneficiary request'),
       (3, 0, 'pending', 'pending', 'waiting_future_execution', 'Payment will be executed on YYYY-MM-DD');


/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;

/*!40103 SET TIME_ZONE = @OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE = @OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES = @OLD_SQL_NOTES */;

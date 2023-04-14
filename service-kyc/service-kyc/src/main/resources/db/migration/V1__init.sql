--
-- Table structure for table `domain`
--
-- DROP TABLE IF EXISTS `domain`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domain`
(
    `id`      bigint(20)   NOT NULL AUTO_INCREMENT,
    `name`    varchar(255) NOT NULL,
    `version` int(11)      NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_domain_name` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

-- DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user`
(
    `id`      bigint(20)   NOT NULL AUTO_INCREMENT,
    `guid`    varchar(255) NOT NULL,
    `version` int(11)      NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_user_guid` (`guid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `provider`
--

-- DROP TABLE IF EXISTS `provider`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `provider`
(
    `id`      bigint(20)   NOT NULL AUTO_INCREMENT,
    `guid`    varchar(255) NOT NULL,
    `version` int(11)      NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_provider_guid` (`guid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `method_type`
--

-- DROP TABLE IF EXISTS `method_type`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `method_type`
(
    `id`      bigint(20)   NOT NULL AUTO_INCREMENT,
    `name`    varchar(255) NOT NULL,
    `version` int(11)      NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_method_type_name` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `result_message`
--

-- DROP TABLE IF EXISTS `result_message`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `result_message`
(
    `id`          bigint(20)   NOT NULL AUTO_INCREMENT,
    `description` varchar(255) NOT NULL,
    `version`     int(11)      NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `kyc_document`
--

-- DROP TABLE IF EXISTS `kyc_document`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kyc_document`
(
    `id`   bigint(20) NOT NULL AUTO_INCREMENT,
    `type` int(11)    NOT NULL,
    `body` LONGBLOB   NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `verification_result`
--

-- DROP TABLE IF EXISTS `verification_result`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `verification_result`
(
    `id`                  bigint(20) NOT NULL AUTO_INCREMENT,
    `provider_request_id` varchar(255) DEFAULT NULL,
    `created_on`          datetime   NOT NULL,
    `provider_id`         bigint(20) NOT NULL,
    `user_id`             bigint(20) NOT NULL,
    `domain_id`           bigint(20) NOT NULL,
    `method_type_id`      bigint(20) NOT NULL,
    `legal_last_name`     varchar(255) DEFAULT NULL,
    `date_of_birth`       varchar(255) DEFAULT NULL,
    `address`             varchar(255) DEFAULT NULL,
    `country_of_birth`    varchar(255) DEFAULT NULL,
    `phone_number`        varchar(255) DEFAULT NULL,
    `nationality`         varchar(255) DEFAULT NULL,
    `method_type_uid`     varchar(255) DEFAULT NULL,
    `document_id`         bigint(20)   DEFAULT NULL,
    `result_message_id`   bigint(20)   DEFAULT NULL,
    `success`             bit(1)     NOT NULL,
    `manual`              bit(1)       DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_trx_created` (`created_on`),
    KEY `FKa04er1mr3maie94v3wg4mx6mh` (`user_id`),
    KEY `FKa05er3gr3maie37v4wg4mx5mx` (`domain_id`),
    KEY `FKnl0vpl01y6vu13hkpi4xupugo` (`provider_id`),
    KEY `FKtl0vkl71y7ju34hgyi6tipugf` (`method_type_id`),
    KEY `FKsir34lbndfaf67sdfv234sdcv` (`result_message_id`),
    KEY `FKgi68klbndfaf45kwdv234sdcv` (`document_id`),
    CONSTRAINT `FKsir34lbndfaf67sdfv234sdcv` FOREIGN KEY (`result_message_id`) REFERENCES `result_message` (`id`),
    CONSTRAINT `FKtl0vkl71y7ju34hgyi6tipugf` FOREIGN KEY (`method_type_id`) REFERENCES `method_type` (`id`),
    CONSTRAINT `FKa04er1mr3maie94v3wg4mx6mh` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    CONSTRAINT `FKa05er3gr3maie37v4wg4mx5mx` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`),
    CONSTRAINT `FKnl0vpl01y6vu13hkpi4xupugo` FOREIGN KEY (`provider_id`) REFERENCES `provider` (`id`),
    CONSTRAINT `FKgi68klbndfaf45kwdv234sdcv` FOREIGN KEY (`document_id`) REFERENCES `kyc_document` (`id`)

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
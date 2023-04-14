-- DROP TABLE IF EXISTS `domain_limit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domain_limit` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `amount` bigint(20) NOT NULL,
  `domain_name` varchar(255) NOT NULL,
  `granularity` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_dl_domain_gran_type` (`domain_name`,`granularity`,`type`),
  KEY `idx_dl_domain` (`domain_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `player_exclusion`
--

-- DROP TABLE IF EXISTS `player_exclusion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `player_exclusion` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NOT NULL,
  `expiry_date` datetime DEFAULT NULL,
  `permanent` bit(1) NOT NULL,
  `player_guid` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_pe_player` (`player_guid`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `player_limit`
--

-- DROP TABLE IF EXISTS `player_limit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `player_limit` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `amount` bigint(20) NOT NULL,
  `domain_name` varchar(255) NOT NULL,
  `granularity` int(11) NOT NULL,
  `player_guid` varchar(255) NOT NULL,
  `type` int(11) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_pl_player_gran_type` (`player_guid`,`granularity`,`type`),
  KEY `idx_pl_player` (`player_guid`),
  KEY `idx_pl_domain` (`domain_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

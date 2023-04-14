

--
-- Table structure for table `player_transaction_query_criteria`
--

-- DROP TABLE IF EXISTS `player_transaction_query_criteria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `player_transaction_query_criteria` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `completed_date` datetime DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `data_purged` bit(1) NOT NULL,
  `end_date` datetime DEFAULT NULL,
  `hash` varchar(255) DEFAULT NULL,
  `start_date` datetime DEFAULT NULL,
  `user_guid` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_tq_hash` (`hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `player_transaction_request`
--

-- DROP TABLE IF EXISTS `player_transaction_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `player_transaction_request` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `author` varchar(255) DEFAULT NULL,
  `request_date` datetime DEFAULT NULL,
  `version` int(11) NOT NULL,
  `query_criteria_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKo5xmdgrfvvnge4pw6tmmscw8n` (`query_criteria_id`),
  CONSTRAINT `FKo5xmdgrfvvnge4pw6tmmscw8n` FOREIGN KEY (`query_criteria_id`) REFERENCES `player_transaction_query_criteria` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `player_transaction`
--
CREATE TABLE `player_transaction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bonus_code` varchar(255) DEFAULT NULL,
  `bonus_name` varchar(255) DEFAULT NULL,
  `bonus_revision_id` bigint(20) NOT NULL,
  `external_tran_id` varchar(255) DEFAULT NULL,
  `game_guid` varchar(255) DEFAULT NULL,
  `game_name` varchar(255) DEFAULT NULL,
  `player_bonus_history_id` bigint(20) NOT NULL,
  `processing_method` varchar(255) DEFAULT NULL,
  `provider_guid` varchar(255) DEFAULT NULL,
  `tran_entry_account_balance` bigint(20) DEFAULT NULL,
  `tran_entry_account_code` varchar(255) DEFAULT NULL,
  `tran_entry_account_type` varchar(255) DEFAULT NULL,
  `tran_entry_amount` bigint(20) NOT NULL,
  `tran_entry_date` datetime DEFAULT NULL,
  `tran_entry_id` bigint(20) NOT NULL,
  `tran_id` bigint(20) NOT NULL,
  `user_guid` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `query_criteria_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_pt_tran_entry_id_crit_id` (`query_criteria_id`,`tran_entry_id`),
  KEY `idx_pt_crit_id` (`query_criteria_id`),
  KEY `idx_pt_tran_id_crit_id` (`query_criteria_id`,`tran_id`),
  KEY `idx_pt_tran_entry_date_crit_id` (`query_criteria_id`,`tran_entry_date`),
  KEY `idx_pt_user_guid_crit_id` (`query_criteria_id`,`user_guid`),
  KEY `idx_pt_external_tran_id_crit_id` (`query_criteria_id`,`external_tran_id`),
  KEY `idx_pt_tran_entry_amount_crit_id` (`query_criteria_id`,`tran_entry_amount`),
  KEY `idx_pt_account_type_crit_id` (`query_criteria_id`,`tran_entry_account_type`),
  KEY `idx_pt_account_code_crit_id` (`query_criteria_id`,`tran_entry_account_code`),
  KEY `idx_pt_provider_guid_crit_id` (`query_criteria_id`,`provider_guid`),
  KEY `idx_pt_game_name_crit_id` (`query_criteria_id`,`game_name`),
  KEY `idx_pt_bonus_code_crit_id` (`query_criteria_id`,`bonus_code`),
  KEY `idx_pt_bonus_name_crit_id` (`query_criteria_id`,`bonus_name`),
  CONSTRAINT `FK1gpbogmqqnw3n8dah3bgk8vvu` FOREIGN KEY (`query_criteria_id`) REFERENCES `player_transaction_query_criteria` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2009 DEFAULT CHARSET=utf8;

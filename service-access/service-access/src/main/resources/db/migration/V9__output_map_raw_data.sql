CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `guid` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_guid` (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `access_rule_result_status_options` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `outcome` bit(1) NOT NULL,
  `output` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

CREATE TABLE `access_rule_transaction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `browser` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `claimed_city` varchar(255) DEFAULT NULL,
  `claimed_country` varchar(255) DEFAULT NULL,
  `claimed_state` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `creation_date` datetime DEFAULT NULL,
  `device_id` longtext DEFAULT NULL,
  `ip_address` varchar(255) DEFAULT NULL,
  `os` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK79w0yfb4a6yv6a4ck0u8clh8` (`user_id`),
  CONSTRAINT `FK79w0yfb4a6yv6a4ck0u8clh8` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `access_control_list_rule_status_option_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` int(11) NOT NULL,
  `accesscontrollist_id` bigint(20) DEFAULT NULL,
  `outcome_id` bigint(20) DEFAULT NULL,
  `output_id` bigint(20) DEFAULT NULL,
  `access_control_list_rule_status_option_config_list_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKkjkfqdwaxsu0v4qlxbki1vc8` (`accesscontrollist_id`),
  KEY `FK31jcf86728fx7oqh7llwha6xd` (`outcome_id`),
  KEY `FK9kchggq5y86y8etiaqt9x57yh` (`output_id`),
  KEY `FKh9n150xoi2uh2q9vn9kve1si5` (`access_control_list_rule_status_option_config_list_id`),
  CONSTRAINT `FK31jcf86728fx7oqh7llwha6xd` FOREIGN KEY (`outcome_id`) REFERENCES `access_rule_result_status_options` (`id`),
  CONSTRAINT `FK9kchggq5y86y8etiaqt9x57yh` FOREIGN KEY (`output_id`) REFERENCES `access_rule_result_status_options` (`id`),
  CONSTRAINT `FKh9n150xoi2uh2q9vn9kve1si5` FOREIGN KEY (`access_control_list_rule_status_option_config_list_id`) REFERENCES `access_control_list` (`id`),
  CONSTRAINT `FKkjkfqdwaxsu0v4qlxbki1vc8` FOREIGN KEY (`accesscontrollist_id`) REFERENCES `access_control_list` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `access_control_list_transaction_data` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `access_control_list_id` bigint(20) DEFAULT NULL,
  `access_rule_transaction_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKk7pnjaee5b4rpuaoc9uxbd88p` (`access_control_list_id`),
  KEY `FK3gi19ivrydkpi1lsk0ra8200u` (`access_rule_transaction_id`),
  CONSTRAINT `FK3gi19ivrydkpi1lsk0ra8200u` FOREIGN KEY (`access_rule_transaction_id`) REFERENCES `access_rule_transaction` (`id`),
  CONSTRAINT `FKk7pnjaee5b4rpuaoc9uxbd88p` FOREIGN KEY (`access_control_list_id`) REFERENCES `access_control_list` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `external_list_rule_status_option_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` int(11) NOT NULL,
  `external_list_id` bigint(20) DEFAULT NULL,
  `outcome_id` bigint(20) DEFAULT NULL,
  `output_id` bigint(20) DEFAULT NULL,
  `external_list_rule_status_option_config_list_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKm65x5qufr1l002w4cmq0o03u4` (`external_list_id`),
  KEY `FKrtgymaarq5rv05l0k00hphlng` (`outcome_id`),
  KEY `FKfx7m3rjehg70fsf2ivm5yrnli` (`output_id`),
  KEY `FK8cgetm1rhgnnqa0ls56adckct` (`external_list_rule_status_option_config_list_id`),
  CONSTRAINT `FK8cgetm1rhgnnqa0ls56adckct` FOREIGN KEY (`external_list_rule_status_option_config_list_id`) REFERENCES `external_list` (`id`),
  CONSTRAINT `FKfx7m3rjehg70fsf2ivm5yrnli` FOREIGN KEY (`output_id`) REFERENCES `access_rule_result_status_options` (`id`),
  CONSTRAINT `FKm65x5qufr1l002w4cmq0o03u4` FOREIGN KEY (`external_list_id`) REFERENCES `external_list` (`id`),
  CONSTRAINT `FKrtgymaarq5rv05l0k00hphlng` FOREIGN KEY (`outcome_id`) REFERENCES `access_rule_result_status_options` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `external_list_transaction_data` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `access_rule_transaction_id` bigint(20) NOT NULL,
  `external_list_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8v6jpmbaqgowtr78swyq8pjmt` (`access_rule_transaction_id`),
  KEY `FKf88jqt8v01sc5wypompfbp8m8` (`external_list_id`),
  CONSTRAINT `FK8v6jpmbaqgowtr78swyq8pjmt` FOREIGN KEY (`access_rule_transaction_id`) REFERENCES `access_rule_transaction` (`id`),
  CONSTRAINT `FKf88jqt8v01sc5wypompfbp8m8` FOREIGN KEY (`external_list_id`) REFERENCES `external_list` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `raw_transaction_data` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime DEFAULT NULL,
  `raw_request_data` longtext DEFAULT NULL,
  `raw_response_data` longtext DEFAULT NULL,
  `access_control_list_transaction_data_id` bigint(20) DEFAULT NULL,
  `external_list_transaction_data_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKj4n23968jxcjuaiwtttq9c18y` (`access_control_list_transaction_data_id`),
  KEY `FK2hy2m9084sh1j4kt31k1esjk1` (`external_list_transaction_data_id`),
  CONSTRAINT `FK2hy2m9084sh1j4kt31k1esjk1` FOREIGN KEY (`external_list_transaction_data_id`) REFERENCES `external_list_transaction_data` (`id`),
  CONSTRAINT `FKj4n23968jxcjuaiwtttq9c18y` FOREIGN KEY (`access_control_list_transaction_data_id`) REFERENCES `access_control_list_transaction_data` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `player_cool_off_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NOT NULL,
  `expiry_date` datetime NOT NULL,
  `modify_author_guid` varchar(255) NOT NULL,
  `modify_timestamp` datetime NOT NULL,
  `modify_type` int(11) NOT NULL,
  `player_guid` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_coh_player` (`player_guid`),
  KEY `idx_coh_expiry_date` (`expiry_date`),
  KEY `idx_coh_modify_timestamp` (`modify_timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `player_exclusion_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `advisor` varchar(255) DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `exclusion_source` varchar(255) DEFAULT NULL,
  `expiry_date` datetime NOT NULL,
  `modify_author_guid` varchar(255) NOT NULL,
  `modify_timestamp` datetime NOT NULL,
  `modify_type` int(11) NOT NULL,
  `permanent` bit(1) NOT NULL,
  `player_guid` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_pe_player` (`player_guid`),
  KEY `idx_pe_expiry_date` (`expiry_date`),
  KEY `idx_pe_modify_timestamp` (`modify_timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `player_limit_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `amount` bigint(20) NOT NULL,
  `granularity` int(11) NOT NULL,
  `modify_author_guid` varchar(255) NOT NULL,
  `modify_timestamp` datetime NOT NULL,
  `modify_type` int(11) NOT NULL,
  `player_guid` varchar(255) NOT NULL,
  `type` int(11) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_pl_player_gran_type` (`player_guid`,`granularity`,`type`),
  KEY `idx_pl_player` (`player_guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
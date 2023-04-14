CREATE TABLE `domain` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_domain_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `authentication` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `access_token` longtext NOT NULL,
  `expiration_date` datetime NOT NULL,
  `version` int(11) NOT NULL,
  `domain_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_domain` (`domain_id`),
  CONSTRAINT `FKh8beg2hbv9fog77fersm8ar15` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `guid` varchar(255) DEFAULT NULL,
    `version` int(11) NOT NULL,
    `domain_id` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_guid` (`guid`),
    KEY `FKk1hsftp46a7obygffmevl2g3s` (`domain_id`),
    CONSTRAINT `FKk1hsftp46a7obygffmevl2g3s` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `failed_attempt` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `last_attempted_at` datetime DEFAULT NULL,
  `last_failure_message` varchar(255) NOT NULL,
  `last_failure_stacktrace` longtext NOT NULL,
  `total_attempts` bigint(20) NOT NULL,
  `version` int(11) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user` (`user_id`),
  KEY `idx_last_attempted_at` (`last_attempted_at`),
  CONSTRAINT `FKsq4a263qiykj8lmomjyomeds1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


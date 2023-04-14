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


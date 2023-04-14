-- lithium_domain.ecosystem definition

CREATE TABLE `ecosystem` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deleted` bit(1) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `display_name` varchar(65) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `name` varchar(35) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_swfpo8iopw9yibnw56jow3bf6` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- lithium_domain.ecosystem_relationship_type definition

CREATE TABLE `ecosystem_relationship_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `deleted` bit(1) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `enabled` bit(1) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_y2r3gv2f0luj0nw2hpsx5ps4` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- lithium_domain.ecosystem_domain_relationship definition

CREATE TABLE `ecosystem_domain_relationship` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deleted` bit(1) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `version` int(11) NOT NULL,
  `domain_id` bigint(20) DEFAULT NULL,
  `ecosystem_id` bigint(20) DEFAULT NULL,
  `relationship_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_edr_all` (`ecosystem_id`,`domain_id`,`relationship_id`),
  KEY `FKpe0bn4u7tvvg60g3v8wbjvhid` (`domain_id`),
  KEY `FKjr6m0bubrbkr3ecx99o76fpcr` (`relationship_id`),
  CONSTRAINT `FKjr6m0bubrbkr3ecx99o76fpcr` FOREIGN KEY (`relationship_id`) REFERENCES `ecosystem_relationship_type` (`id`),
  CONSTRAINT `FKkf2xcome9x2ko70dw49282art` FOREIGN KEY (`ecosystem_id`) REFERENCES `ecosystem` (`id`),
  CONSTRAINT `FKpe0bn4u7tvvg60g3v8wbjvhid` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

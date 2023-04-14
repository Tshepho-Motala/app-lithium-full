CREATE TABLE `provider_auth_client` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`code` varchar(255) DEFAULT NULL,
`creation_date` datetime DEFAULT NULL,
`description` varchar(255) DEFAULT NULL,
`guid` varchar(255) DEFAULT NULL,
`password` varchar(255) DEFAULT NULL,
`version` int(11) NOT NULL,
`domain_id` bigint(20) DEFAULT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `idx_domain_code` (`domain_id`,`code`),
UNIQUE KEY `idx_domain_guid` (`guid`),
KEY `idx_domain_id` (`domain_id`),
CONSTRAINT `FKjaqemrvusoax1332ul9r1hh2x` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

INSERT INTO `provider_auth_client` (code, description, guid, password, version, domain_id) VALUES('acme', 'Default auth client.', 'default/acme', 'acmesecret', 0, 1);
INSERT INTO `provider_auth_client` (code, description, guid, password, version, domain_id) VALUES('una', 'Default auth client for ui-network-admin', 'default/una', 'uNa@h4sANEWp4sswd', 0, 1);

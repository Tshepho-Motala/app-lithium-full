CREATE TABLE `external_list` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `enabled` bit(1) NOT NULL,
  `list_name` varchar(255) NOT NULL,
  `provider_url` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  `access_rule_id` bigint(20) NOT NULL,
  `priority` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrkoquilcbnc452ynp5as3ahi7` (`access_rule_id`),
  CONSTRAINT `FKrkoquilcbnc452ynp5as3ahi7` FOREIGN KEY (`access_rule_id`) REFERENCES `access_rule` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
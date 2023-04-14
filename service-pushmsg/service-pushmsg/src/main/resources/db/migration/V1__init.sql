CREATE TABLE `domain` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`name` varchar(255) NOT NULL,
`version` int(11) NOT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `idx_domain_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `provider` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`code` varchar(255) NOT NULL,
`enabled` bit(1) NOT NULL,
`name` varchar(255) DEFAULT NULL,
`url` varchar(255) NOT NULL,
`version` int(11) NOT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `idx_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `provider_property` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`default_value` varchar(255) DEFAULT NULL,
`description` longtext NOT NULL,
`name` varchar(255) NOT NULL,
`type` varchar(255) NOT NULL,
`version` int(11) NOT NULL,
`provider_id` bigint(20) NOT NULL,
PRIMARY KEY (`id`),
KEY `FK6r0fnmw3ab3my2ndft5iijoxt` (`provider_id`),
CONSTRAINT `FK6r0fnmw3ab3my2ndft5iijoxt` FOREIGN KEY (`provider_id`) REFERENCES `provider` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `domain_provider` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`access_rule` varchar(255) DEFAULT NULL,
`deleted` bit(1) NOT NULL,
`description` varchar(255) DEFAULT NULL,
`enabled` bit(1) NOT NULL,
`priority` int(11) NOT NULL,
`version` int(11) NOT NULL,
`domain_id` bigint(20) NOT NULL,
`provider_id` bigint(20) NOT NULL,
PRIMARY KEY (`id`),
KEY `FKfsmx96ypx3erced2ymwos2bvs` (`domain_id`),
KEY `FKokxh7s6dpdqcnml98p7vll18f` (`provider_id`),
CONSTRAINT `FKfsmx96ypx3erced2ymwos2bvs` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`),
CONSTRAINT `FKokxh7s6dpdqcnml98p7vll18f` FOREIGN KEY (`provider_id`) REFERENCES `provider` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `domain_provider_property` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`value` varchar(255) DEFAULT NULL,
`version` int(11) NOT NULL,
`domain_provider_id` bigint(20) NOT NULL,
`provider_property_id` bigint(20) NOT NULL,
PRIMARY KEY (`id`),
KEY `FKlht60da0ehjlf89dkpjstib8x` (`domain_provider_id`),
KEY `FK1lc12jd9uxl3moi5gor6871bt` (`provider_property_id`),
CONSTRAINT `FK1lc12jd9uxl3moi5gor6871bt` FOREIGN KEY (`provider_property_id`) REFERENCES `provider_property` (`id`),
CONSTRAINT `FKlht60da0ehjlf89dkpjstib8x` FOREIGN KEY (`domain_provider_id`) REFERENCES `domain_provider` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`guid` varchar(255) NOT NULL,
`opt_out` bit(1) DEFAULT NULL,
`version` int(11) NOT NULL,
`domain_id` bigint(20) NOT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `idx_user_guid` (`guid`),
KEY `FKk1hsftp46a7obygffmevl2g3s` (`domain_id`),
CONSTRAINT `FKk1hsftp46a7obygffmevl2g3s` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `external_user` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`created_at` datetime DEFAULT NULL,
`device_model` varchar(255) DEFAULT NULL,
`device_os` varchar(255) DEFAULT NULL,
`device_type` int(11) DEFAULT NULL,
`ip` varchar(255) DEFAULT NULL,
`language` tinyblob,
`last_active` datetime DEFAULT NULL,
`session_count` bigint(20) DEFAULT NULL,
`uuid` varchar(255) NOT NULL,
`version` int(11) NOT NULL,
`user_id` bigint(20) NOT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `idx_externaluser_uuid` (`uuid`,`user_id`),
KEY `FKpboxf8gx6847bq0m6qic7tbny` (`user_id`),
CONSTRAINT `FKpboxf8gx6847bq0m6qic7tbny` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `language` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`code` varchar(255) NOT NULL,
`name` varchar(255) NOT NULL,
`version` int(11) NOT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `idx_language_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `push_msg` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`created_date` datetime NOT NULL,
`delayed_option` varchar(255) DEFAULT NULL,
`delivery_time_of_day` varchar(255) DEFAULT NULL,
`failed` bit(1) NOT NULL,
`is_adm` bit(1) DEFAULT NULL,
`is_android` bit(1) DEFAULT NULL,
`is_any_web` bit(1) DEFAULT NULL,
`is_chrome` bit(1) DEFAULT NULL,
`is_chrome_web` bit(1) DEFAULT NULL,
`is_firefox` bit(1) DEFAULT NULL,
`is_ios` bit(1) DEFAULT NULL,
`is_safari` bit(1) DEFAULT NULL,
`iswp_wns` bit(1) DEFAULT NULL,
`latest_error_reason` longtext,
`priority` int(11) NOT NULL,
`provider_reference` varchar(255) DEFAULT NULL,
`send_after` varchar(255) DEFAULT NULL,
`sent_date` datetime DEFAULT NULL,
`template_id` varchar(255) DEFAULT NULL,
`ttl` varchar(255) DEFAULT NULL,
`version` int(11) NOT NULL,
`domain_id` bigint(20) NOT NULL,
`domain_provider_id` bigint(20) DEFAULT NULL,
PRIMARY KEY (`id`),
KEY `idx_pushmsg_created_date_priority` (`created_date`,`priority`),
KEY `idx_pushmsg_sent_date_priority` (`sent_date`,`priority`),
KEY `FKtbe6d3ebpw02q7aomt5a6klw1` (`domain_id`),
KEY `FKhqhocq97j3x57acxbdwp6sk0a` (`domain_provider_id`),
CONSTRAINT `FKhqhocq97j3x57acxbdwp6sk0a` FOREIGN KEY (`domain_provider_id`) REFERENCES `domain_provider` (`id`),
CONSTRAINT `FKtbe6d3ebpw02q7aomt5a6klw1` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `push_msg_content` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`content` longtext,
`version` int(11) NOT NULL,
`language_id` bigint(20) NOT NULL,
`push_msg_contents_id` bigint(20) DEFAULT NULL,
PRIMARY KEY (`id`),
KEY `FKku1k6cbkx6f0e8krkjtybhjeh` (`language_id`),
KEY `FKh7o5tdv3653h2ejlil2hpvo3t` (`push_msg_contents_id`),
CONSTRAINT `FKh7o5tdv3653h2ejlil2hpvo3t` FOREIGN KEY (`push_msg_contents_id`) REFERENCES `push_msg` (`id`),
CONSTRAINT `FKku1k6cbkx6f0e8krkjtybhjeh` FOREIGN KEY (`language_id`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `push_msg_heading` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`heading` varchar(255) DEFAULT NULL,
`version` int(11) NOT NULL,
`language_id` bigint(20) NOT NULL,
`push_msg_headings_id` bigint(20) DEFAULT NULL,
PRIMARY KEY (`id`),
KEY `FK9aexn4xg5a5lok6kugkvk1n6h` (`language_id`),
KEY `FKkfmdy4l83jqsy6c6axpal1uwl` (`push_msg_headings_id`),
CONSTRAINT `FK9aexn4xg5a5lok6kugkvk1n6h` FOREIGN KEY (`language_id`) REFERENCES `language` (`id`),
CONSTRAINT `FKkfmdy4l83jqsy6c6axpal1uwl` FOREIGN KEY (`push_msg_headings_id`) REFERENCES `push_msg` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `push_msg_template_revision` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`description` varchar(255) DEFAULT NULL,
`provider_template_id` varchar(255) DEFAULT NULL,
`push_msg_template_id` bigint(20) NOT NULL,
PRIMARY KEY (`id`),
KEY `FKku5yktcq69g6tiee8fkxnss1d` (`push_msg_template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `push_msg_template` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`edit_started_on` datetime DEFAULT NULL,
`enabled` bit(1) NOT NULL,
`name` varchar(255) NOT NULL,
`version` int(11) NOT NULL,
`current_id` bigint(20) DEFAULT NULL,
`domain_id` bigint(20) NOT NULL,
`edit_id` bigint(20) DEFAULT NULL,
`edit_by_id` bigint(20) DEFAULT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `idx_tpl_all` (`name`,`domain_id`),
KEY `FKnhlrk139mia8f6miklfkq5k66` (`current_id`),
KEY `FKop09a71cofx9y67hkm7k9x3gk` (`domain_id`),
KEY `FKfokge2kbtvfrlclq2yl2l10ir` (`edit_id`),
KEY `FKk7y19xms9dgeclab8lryq5tue` (`edit_by_id`),
CONSTRAINT `FKfokge2kbtvfrlclq2yl2l10ir` FOREIGN KEY (`edit_id`) REFERENCES `push_msg_template_revision` (`id`),
CONSTRAINT `FKk7y19xms9dgeclab8lryq5tue` FOREIGN KEY (`edit_by_id`) REFERENCES `user` (`id`),
CONSTRAINT `FKnhlrk139mia8f6miklfkq5k66` FOREIGN KEY (`current_id`) REFERENCES `push_msg_template_revision` (`id`),
CONSTRAINT `FKop09a71cofx9y67hkm7k9x3gk` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `push_msg_template_revision`
ADD CONSTRAINT `FKku5yktcq69g6tiee8fkxnss1d` FOREIGN KEY (`push_msg_template_id`) REFERENCES `push_msg_template`(`id`);

CREATE TABLE `push_msg_users` (
`pushmsg_id` bigint(20) NOT NULL,
`user_id` bigint(20) NOT NULL,
UNIQUE KEY `idx_urr_id` (`pushmsg_id`,`user_id`),
KEY `FKsn945jeydkamufcxe2esgy5j8` (`user_id`),
CONSTRAINT `FKqj3cdr06fuocr8stjcb5pmsqw` FOREIGN KEY (`pushmsg_id`) REFERENCES `push_msg` (`id`),
CONSTRAINT `FKsn945jeydkamufcxe2esgy5j8` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `segment` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`name` varchar(255) DEFAULT NULL,
`version` int(11) NOT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `included_segments` (
`pushmsg_id` bigint(20) NOT NULL,
`segment_id` bigint(20) NOT NULL,
UNIQUE KEY `idx_urr_id` (`pushmsg_id`,`segment_id`),
KEY `FKd4poshqhxungocbydvs5vv1d3` (`segment_id`),
CONSTRAINT `FKd4poshqhxungocbydvs5vv1d3` FOREIGN KEY (`segment_id`) REFERENCES `segment` (`id`),
CONSTRAINT `FKpg5icm5o9j1wkwm78mf0m2bjn` FOREIGN KEY (`pushmsg_id`) REFERENCES `push_msg` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `excluded_segments` (
`pushmsg_id` bigint(20) NOT NULL,
`segment_id` bigint(20) NOT NULL,
UNIQUE KEY `idx_urr_id` (`pushmsg_id`,`segment_id`),
KEY `FKjrqlk0jl4o8kskna8pk0kokro` (`segment_id`),
CONSTRAINT `FKimbvmr6o2klsm1pb01lfbnckn` FOREIGN KEY (`pushmsg_id`) REFERENCES `push_msg` (`id`),
CONSTRAINT `FKjrqlk0jl4o8kskna8pk0kokro` FOREIGN KEY (`segment_id`) REFERENCES `segment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


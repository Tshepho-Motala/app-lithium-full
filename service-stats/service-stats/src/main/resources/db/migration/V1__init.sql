CREATE TABLE `domain` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`name` varchar(255) NOT NULL,
`version` int(11) NOT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `idx_domain_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `user` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`guid` varchar(255) NOT NULL,
`version` int(11) NOT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `idx_user_guid` (`guid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `label` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`name` varchar(255) NOT NULL,
`version` int(11) NOT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `idx_label_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `label_value` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`value` varchar(255) DEFAULT NULL,
`version` int(11) NOT NULL,
`label_id` bigint(20) NOT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `idx_label_value` (`label_id`,`value`),
CONSTRAINT `FKre71r2qpe0al31ks5ys0mf3fj` FOREIGN KEY (`label_id`) REFERENCES `label` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `period` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`date_end` datetime NOT NULL,
`date_start` datetime NOT NULL,
`day` int(11) NOT NULL,
`granularity` int(11) NOT NULL,
`hour` int(11) NOT NULL,
`month` int(11) NOT NULL,
`version` int(11) NOT NULL,
`week` int(11) NOT NULL,
`year` int(11) NOT NULL,
`domain_id` bigint(20) NOT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `idx_pd_all` (`year`,`month`,`week`,`day`,`hour`,`domain_id`),
UNIQUE KEY `idx_pd_dates` (`date_start`,`date_end`,`domain_id`),
KEY `idx_pd_datestart` (`date_start`),
KEY `idx_pd_dateend` (`date_end`),
KEY `idx_pd_granularity` (`granularity`),
KEY `FKk1oj7pptmme05t9qyaay1d178` (`domain_id`),
CONSTRAINT `FKk1oj7pptmme05t9qyaay1d178` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `stat` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`name` varchar(255) NOT NULL,
`version` int(11) NOT NULL,
`domain_id` bigint(20) NOT NULL,
`owner_id` bigint(20) NOT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `idx_name` (`name`),
KEY `FKp3549noq1h9q0aetfcyq59mx6` (`domain_id`),
KEY `FKehrykdkf4j7tci3flqp9ckbkt` (`owner_id`),
CONSTRAINT `FKehrykdkf4j7tci3flqp9ckbkt` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`),
CONSTRAINT `FKp3549noq1h9q0aetfcyq59mx6` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `stat_entry` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`entry_date` datetime DEFAULT NULL,
`ip_address` varchar(255) DEFAULT NULL,
`user_agent` varchar(255) DEFAULT NULL,
`version` int(11) NOT NULL,
`owner_id` bigint(20) NOT NULL,
`stat_id` bigint(20) NOT NULL,
PRIMARY KEY (`id`),
KEY `FKc6hihytqxi5o20drvw5wpf3ha` (`owner_id`),
KEY `FKohd6i0jwv3yh0fpk5s0eio08n` (`stat_id`),
CONSTRAINT `FKc6hihytqxi5o20drvw5wpf3ha` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`),
CONSTRAINT `FKohd6i0jwv3yh0fpk5s0eio08n` FOREIGN KEY (`stat_id`) REFERENCES `stat` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `stat_entry_label_value` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`version` int(11) NOT NULL,
`label_value_id` bigint(20) NOT NULL,
`stat_id` bigint(20) NOT NULL,
PRIMARY KEY (`id`),
KEY `FKjyuptf7tmba3c4q3kqewxqoqi` (`label_value_id`),
KEY `FKojgjyq4a67dixpyfn475dc5h` (`stat_id`),
CONSTRAINT `FKjyuptf7tmba3c4q3kqewxqoqi` FOREIGN KEY (`label_value_id`) REFERENCES `label_value` (`id`),
CONSTRAINT `FKojgjyq4a67dixpyfn475dc5h` FOREIGN KEY (`stat_id`) REFERENCES `stat` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `stat_summary` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`count` bigint(20) DEFAULT NULL,
`version` int(11) NOT NULL,
`period_id` bigint(20) NOT NULL,
`stat_id` bigint(20) NOT NULL,
PRIMARY KEY (`id`),
KEY `FKa6f6sf0dg0luf6dtgtwoi8in0` (`period_id`),
KEY `FKa3i0kxojvxo9kmgxrlo2v34xw` (`stat_id`),
CONSTRAINT `FKa3i0kxojvxo9kmgxrlo2v34xw` FOREIGN KEY (`stat_id`) REFERENCES `stat` (`id`),
CONSTRAINT `FKa6f6sf0dg0luf6dtgtwoi8in0` FOREIGN KEY (`period_id`) REFERENCES `period` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `account_code` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `code` varchar(255) NOT NULL,
    `version` int(11) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_ac_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `account_type` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `code` varchar(255) NOT NULL,
    `version` int(11) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_at_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `currency` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `code` varchar(255) NOT NULL,
    `name` varchar(255) NOT NULL,
    `version` int(11) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_cur_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `domain` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `version` int(11) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_domain_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `label` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `version` int(11) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_label_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `label_value` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `value` varchar(255) DEFAULT NULL,
    `version` int(11) NOT NULL,
    `label_id` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_label_value` (`label_id`,`value`),
    CONSTRAINT `FKre71r2qpe0al31ks5ys0mf3fj` FOREIGN KEY (`label_id`) REFERENCES `label` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `period` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `date_end` datetime NOT NULL,
    `date_start` datetime NOT NULL,
    `day` int(11) NOT NULL,
    `granularity` int(11) NOT NULL,
    `month` int(11) NOT NULL,
    `open` bit(1) NOT NULL,
    `version` int(11) NOT NULL,
    `week` int(11) NOT NULL,
    `year` int(11) NOT NULL,
    `domain_id` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_pd_all` (`year`,`month`,`week`,`day`,`domain_id`),
    UNIQUE KEY `idx_pd_dates` (`date_start`,`date_end`,`domain_id`),
    KEY `idx_pd_datestart` (`date_start`),
    KEY `idx_pd_dateend` (`date_end`),
    KEY `idx_pd_open` (`open`),
    KEY `idx_pd_granularity` (`granularity`),
    KEY `FKk1oj7pptmme05t9qyaay1d178` (`domain_id`),
    CONSTRAINT `FKk1oj7pptmme05t9qyaay1d178` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `transaction_type` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `code` varchar(255) NOT NULL,
    `version` int(11) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_tt_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `summary_domain` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `closing_balance_cents` bigint(20) NOT NULL,
    `credit_cents` bigint(20) NOT NULL,
    `debit_cents` bigint(20) NOT NULL,
    `shard` varchar(255) NOT NULL,
    `tran_count` bigint(20) NOT NULL,
    `version` int(11) NOT NULL,
    `account_code_id` bigint(20) NOT NULL,
    `currency_id` bigint(20) NOT NULL,
    `period_id` bigint(20) NOT NULL,
    `test_users` BIT(1),
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_summary_domain_shard` (`period_id`,`account_code_id`,`currency_id`,`shard`),
    KEY `idx_summary_domain_all` (`period_id`,`account_code_id`,`currency_id`),
    KEY `FKc1vkbsbo9pcr7mf1au38xdle` (`account_code_id`),
    KEY `FKc82f8e65j0fsocs15yofw15l4` (`currency_id`),
    CONSTRAINT `FKc1vkbsbo9pcr7mf1au38xdle` FOREIGN KEY (`account_code_id`) REFERENCES `account_code` (`id`),
    CONSTRAINT `FKc82f8e65j0fsocs15yofw15l4` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`),
    CONSTRAINT `FKsqyfd71klxkw8mxpdn0r6ovf6` FOREIGN KEY (`period_id`) REFERENCES `period` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `summary_domain_label_value` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `credit_cents` bigint(20) NOT NULL,
    `debit_cents` bigint(20) NOT NULL,
    `shard` varchar(255) NOT NULL,
    `tran_count` bigint(20) NOT NULL,
    `version` int(11) NOT NULL,
    `account_code_id` bigint(20) NOT NULL,
    `currency_id` bigint(20) NOT NULL,
    `label_value_id` bigint(20) NOT NULL,
    `period_id` bigint(20) NOT NULL,
    `transaction_type_id` bigint(20) NOT NULL,
    `test_users` BIT(1),
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_summary_domain_label_value_shard` (`period_id`,`transaction_type_id`,`account_code_id`,`label_value_id`,`currency_id`,`shard`),
    KEY `idx_summary_domain_label_value_all` (`period_id`,`transaction_type_id`,`account_code_id`,`label_value_id`,`currency_id`),
    KEY `FKifkss328b2pamahrcdn1mhok` (`account_code_id`),
    KEY `FKalcf1fu6aai0ad7rvhulev57d` (`currency_id`),
    KEY `FKdgpulqquv5y8esxtr20mvjhpo` (`label_value_id`),
    KEY `FKiisngy330ue3hhg29wma83c7g` (`transaction_type_id`),
    CONSTRAINT `FKalcf1fu6aai0ad7rvhulev57d` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`),
    CONSTRAINT `FKaw3meq0ojnqrinrerubqaqqbs` FOREIGN KEY (`period_id`) REFERENCES `period` (`id`),
    CONSTRAINT `FKdgpulqquv5y8esxtr20mvjhpo` FOREIGN KEY (`label_value_id`) REFERENCES `label_value` (`id`),
    CONSTRAINT `FKifkss328b2pamahrcdn1mhok` FOREIGN KEY (`account_code_id`) REFERENCES `account_code` (`id`),
    CONSTRAINT `FKiisngy330ue3hhg29wma83c7g` FOREIGN KEY (`transaction_type_id`) REFERENCES `transaction_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `summary_domain_transaction_type` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `credit_cents` bigint(20) NOT NULL,
    `debit_cents` bigint(20) NOT NULL,
    `shard` varchar(255) NOT NULL,
    `tran_count` bigint(20) NOT NULL,
    `version` int(11) NOT NULL,
    `account_code_id` bigint(20) NOT NULL,
    `currency_id` bigint(20) NOT NULL,
    `period_id` bigint(20) NOT NULL,
    `transaction_type_id` bigint(20) NOT NULL,
    `test_users` BIT(1),
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_summary_domain_transaction_type_shard` (`period_id`,`transaction_type_id`,`account_code_id`,`currency_id`,`shard`),
    KEY `idx_summary_domain_transaction_type_all` (`period_id`,`transaction_type_id`,`account_code_id`,`currency_id`),
    KEY `FKcx6155q751905kuhs0y9x4rb` (`account_code_id`),
    KEY `FKadl1ldg2cw6im6la2lg8rind5` (`currency_id`),
    KEY `FK7b7kojcvjd1ftw2f9rjsf6b88` (`transaction_type_id`),
    CONSTRAINT `FK7b7kojcvjd1ftw2f9rjsf6b88` FOREIGN KEY (`transaction_type_id`) REFERENCES `transaction_type` (`id`),
    CONSTRAINT `FKadl1ldg2cw6im6la2lg8rind5` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`),
    CONSTRAINT `FKcx6155q751905kuhs0y9x4rb` FOREIGN KEY (`account_code_id`) REFERENCES `account_code` (`id`),
    CONSTRAINT `FKke4lmps2en53osh2w30g5kevy` FOREIGN KEY (`period_id`) REFERENCES `period` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

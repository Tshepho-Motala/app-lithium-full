CREATE TABLE `balance_limit` (
                                 `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                 `balance_cents` bigint(20) NOT NULL,
                                 `version` int(11) NOT NULL,
                                 `account_id` bigint(20) NOT NULL,
                                 `contra_account_id` bigint(20) NOT NULL,
                                 `transaction_type_to_id` bigint(20) NOT NULL,
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `idx_bl_all` (`account_id`, `contra_account_id`, `transaction_type_to_id`),
                                 KEY `idx_bl_balance` (`balance_cents`),
                                 KEY `FK_bl_account_id` (`account_id`),
                                 KEY `FK_bl_contra_account_id` (`contra_account_id`),
                                 KEY `FK_bl_transaction_type_to_id` (`transaction_type_to_id`),
                                 CONSTRAINT `FK_bl_account_id` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`),
                                 CONSTRAINT `FK_bl_contra_account_id` FOREIGN KEY (`contra_account_id`) REFERENCES `account` (`id`),
                                 CONSTRAINT `FK_bl_transaction_type_to_id` FOREIGN KEY (`transaction_type_to_id`) REFERENCES `transaction_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

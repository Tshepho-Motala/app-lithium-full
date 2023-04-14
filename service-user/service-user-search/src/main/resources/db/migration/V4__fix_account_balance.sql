DROP TABLE `current_account_balance`;

CREATE TABLE `current_account_balance` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   `current_account_balance` bigint(20) NOT NULL,
   `user_id` bigint(20) NOT NULL,
   `timestamp` datetime(3) DEFAULT NULL,
   PRIMARY KEY (`id`),
   UNIQUE KEY `user_id_unique` (`user_id`),
   KEY `idx_current_account_balance` (`current_account_balance`),
   CONSTRAINT `fk_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

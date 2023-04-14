CREATE TABLE `user_data_entry` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `data_key` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `data_value` varchar(2048) COLLATE utf8_bin DEFAULT NULL,
  `version` int(11) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_datakey` (`user_id`,`data_key`),
  CONSTRAINT `FKn7vwqmp7lmgo46favaesxj2vo` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

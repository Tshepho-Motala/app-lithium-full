CREATE TABLE `processor_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `processor_user_id` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `domain_method_processor_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_pu_unq` (`user_id`,`domain_method_processor_id`),
  KEY `FK94867slgxqh1e8ck6i4qlagdg` (`domain_method_processor_id`),
  CONSTRAINT `FK8j8bm5c1lgqiqadlq8ukaekib` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK94867slgxqh1e8ck6i4qlagdg` FOREIGN KEY (`domain_method_processor_id`) REFERENCES `domain_method_processor` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

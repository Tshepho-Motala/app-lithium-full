CREATE TABLE `user_password_hash_algorithm` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `hash_algorithm` int(11) NOT NULL,
  `salt` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKgbo9s0v9fw3hnl1s1ne00lope` (`user_id`),
  CONSTRAINT `FKgbo9s0v9fw3hnl1s1ne00lope` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

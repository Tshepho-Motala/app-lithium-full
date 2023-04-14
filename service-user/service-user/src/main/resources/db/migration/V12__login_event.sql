CREATE TABLE `login_event` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `comment` varchar(255) DEFAULT NULL,
  `date` datetime NOT NULL,
  `ip_address` varchar(255) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_loginevent_date_ip_address` (`date`,`ip_address`),
  KEY `FKsv6b3neqe86qfrd3as22uns6i` (`user_id`),
  CONSTRAINT `FKsv6b3neqe86qfrd3as22uns6i` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
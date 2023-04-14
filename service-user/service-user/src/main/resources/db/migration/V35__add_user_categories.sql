CREATE TABLE `user_category` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`description` varchar(255) DEFAULT NULL,
`name` varchar(255) DEFAULT NULL,
`domain_id` bigint(20) DEFAULT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `idx_category_name` (`name`,`domain_id`),
KEY `FKcfg3ddx5xldbq4jet7yyck0mq` (`domain_id`),
CONSTRAINT `FKcfg3ddx5xldbq4jet7yyck0mq` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

CREATE TABLE `user_categories` (
`user_id` bigint(20) NOT NULL,
`user_category_id` bigint(20) NOT NULL,
UNIQUE KEY `idx_urr_id` (`user_id`,`user_category_id`),
KEY `FKkbpbx9lixdlp01wrma854s99d` (`user_category_id`),
CONSTRAINT `FKkbpbx9lixdlp01wrma854s99d` FOREIGN KEY (`user_category_id`) REFERENCES `user_category` (`id`),
CONSTRAINT `FKqhdol0ia96a31f8ir2g928ems` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

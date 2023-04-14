DROP TABLE IF EXISTS `user_labels`;

DROP TABLE IF EXISTS `label`;

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

CREATE TABLE `user_label_value` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` int(11) NOT NULL,
  `label_value_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_label_value_id` (`label_value_id`),
  CONSTRAINT `FKcohol59x9ilkprre1y8bkulyt` FOREIGN KEY (`label_value_id`) REFERENCES `label_value` (`id`),
  CONSTRAINT `FKkft5ygnfw03ojtf4jvlsox17r` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

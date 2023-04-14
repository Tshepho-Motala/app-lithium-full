
DROP TABLE IF EXISTS `user_label_value`;

CREATE TABLE `user_revision` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime DEFAULT NULL,
  `version` int(11) NOT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `FKjcc8s113f74qmc8kcknrghxvd` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `user`
  ADD COLUMN `current_id` bigint(20) DEFAULT NULL,
  ADD KEY `FKcyw608hdrg3u8u0oe3vvvtstj` (`current_id`),
  ADD CONSTRAINT `FKcyw608hdrg3u8u0oe3vvvtstj` FOREIGN KEY (`current_id`) REFERENCES `user_revision` (`id`);

CREATE TABLE `user_revision_label_value` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` int(11) NOT NULL,
  `label_value_id` bigint(20) NOT NULL,
  `user_revision_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_revision_id` (`user_revision_id`),
  KEY `idx_label_value_id` (`label_value_id`),
  CONSTRAINT `FK15n9i72o6uddfmf2ssiof3nwn` FOREIGN KEY (`label_value_id`) REFERENCES `label_value` (`id`),
  CONSTRAINT `FK7flaxv3j118n5hy0v00kyu51s` FOREIGN KEY (`user_revision_id`) REFERENCES `user_revision` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;



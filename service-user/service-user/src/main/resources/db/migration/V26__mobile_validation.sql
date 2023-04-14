CREATE TABLE `mobile_validation_token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_on` datetime NOT NULL,
  `token` varchar(255) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_9286wnnprsdfuiowugwmn8i5dx` (`user_id`),
  KEY `idx_upt_createdon` (`created_on`),
  CONSTRAINT `FKpm63l87g8yg87ihl24mog0bayw` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
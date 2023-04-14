ALTER TABLE `user` ADD COLUMN `last_name_prefix` VARCHAR(15) DEFAULT NULL, ALGORITHM INPLACE, LOCK NONE;
ALTER TABLE `user` ADD COLUMN `country_code_of_birth` VARCHAR(10) DEFAULT NULL, ALGORITHM INPLACE, LOCK NONE;

CREATE TABLE `incomplete_user_label_value` (
 `id` bigint(20) NOT NULL AUTO_INCREMENT,
 `version` int(11) NOT NULL,
 `label_value_id` bigint(20) NOT NULL,
 `incomplete_user_id` bigint(20) NOT NULL,
 PRIMARY KEY (`id`),
 KEY `idx_incomplete_user_id` (`incomplete_user_id`),
 KEY `idx_label_value_id` (`label_value_id`),
 CONSTRAINT `FKoiuwefouwefoihweojnweoiwj` FOREIGN KEY (`label_value_id`) REFERENCES `label_value` (`id`),
 CONSTRAINT `FKoijwefpiojwepijwepwijefks` FOREIGN KEY (`incomplete_user_id`) REFERENCES `incomplete_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

ALTER TABLE `incomplete_user` ADD COLUMN `last_name_prefix` VARCHAR(15) DEFAULT NULL, ALGORITHM INPLACE, LOCK NONE;
ALTER TABLE `incomplete_user` ADD COLUMN `country_code_of_birth` VARCHAR(10) DEFAULT NULL, ALGORITHM INPLACE, LOCK NONE;

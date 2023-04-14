CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `guid` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_guid` (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `bonus_file_upload` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `complete` bit(1) NOT NULL,
  `file` longblob DEFAULT NULL,
  `file_type` varchar(255) DEFAULT NULL,
  `had_some_errors` bit(1) NOT NULL,
  `size` bigint(20) NOT NULL,
  `version` int(11) NOT NULL,
  `author_id` bigint(20) NOT NULL,
  `bonus_revision_id` bigint(20) NOT NULL,
  `creation_date` datetime DEFAULT NULL,
  `completion_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4jg6byoq9ko4tw6mqkjl2xkrj` (`author_id`),
  KEY `FKovxkrmgt2gdc5xdablgsytw9l` (`bonus_revision_id`),
  CONSTRAINT `FK4jg6byoq9ko4tw6mqkjl2xkrj` FOREIGN KEY (`author_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKovxkrmgt2gdc5xdablgsytw9l` FOREIGN KEY (`bonus_revision_id`) REFERENCES `bonus_revision` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `bonus_file_run_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `error_message` longtext DEFAULT NULL,
  `line_data` varchar(255) DEFAULT NULL,
  `success` bit(1) NOT NULL,
  `version` int(11) NOT NULL,
  `bonus_file_upload_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3u7o5fldp9dh6tuijdcao1de1` (`bonus_file_upload_id`),
  CONSTRAINT `FK3u7o5fldp9dh6tuijdcao1de1` FOREIGN KEY (`bonus_file_upload_id`) REFERENCES `bonus_file_upload` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

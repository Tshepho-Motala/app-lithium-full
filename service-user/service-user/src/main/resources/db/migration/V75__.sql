ALTER TABLE `user`
ADD COLUMN `comms_opt_in_complete` BIT(1);

-- lithium_user.user_link_type definition

CREATE TABLE `user_link_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `deleted` bit(1) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `enabled` bit(1) NOT NULL,
  `link_direction_sensitive` bit(1) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_syp29uldkcv0uubj6ib2hmmsl` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- lithium_user.user_link definition

CREATE TABLE `user_link` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deleted` bit(1) NOT NULL,
  `link_note` longtext,
  `version` int(11) NOT NULL,
  `primary_user_id` bigint(20) DEFAULT NULL,
  `secondary_user_id` bigint(20) DEFAULT NULL,
  `user_link_type_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbcf8ljmffmrj8gb79h4td5vpk` (`primary_user_id`),
  KEY `FKatsama5mly15a9tuumqnt0txi` (`secondary_user_id`),
  KEY `FK67cdel2idfmgyf9l49nceldpe` (`user_link_type_id`),
  CONSTRAINT `FK67cdel2idfmgyf9l49nceldpe` FOREIGN KEY (`user_link_type_id`) REFERENCES `user_link_type` (`id`),
  CONSTRAINT `FKatsama5mly15a9tuumqnt0txi` FOREIGN KEY (`secondary_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKbcf8ljmffmrj8gb79h4td5vpk` FOREIGN KEY (`primary_user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

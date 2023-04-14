CREATE TABLE `collection_data_revision`
(
  `id`  bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `creation_date` datetime NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY(`id`)
) ENGINE = InnoDB
DEFAULT CHARSET = utf8;

CREATE TABLE `collection_data`
(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `collection_name` varchar(255) NOT NULL,
  `data_key` varchar(255) NOT NULL,
  `data_value` varchar(512) NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY(`id`),
  UNIQUE KEY `FKsmakwfd7sbsfcnny3gnzva6f7p` (`collection_name`, `data_key`, `data_value`)
) ENGINE = InnoDB
DEFAULT CHARSET = utf8;

CREATE TABLE `collection_data_revision_entry`
(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `collection_revision_id` bigint(20)  NOT NULL,
  `collection_data_id` bigint(20) NOT NULL,
  `last_updated_revision_id` bigint(20) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `FKsmakwfd7sbsfcnny2gnzva6f7p` FOREIGN KEY (`collection_revision_id`) REFERENCES `collection_data_revision` (`id`),
  CONSTRAINT `FKsmakwfd7sbsfcnny1gnzva6f7p` FOREIGN KEY (`collection_data_id`) REFERENCES `collection_data` (`id`)
) ENGINE = InnoDB
DEFAULT CHARSET = utf8;

ALTER TABLE `user` ADD COLUMN `current_collection_data_rev_id` bigint(20) DEFAULT NULL, ALGORITHM INPLACE, LOCK NONE;

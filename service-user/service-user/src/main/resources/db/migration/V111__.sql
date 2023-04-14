CREATE TABLE `granularity`
(
  `id`   bigint(20)   NOT NULL AUTO_INCREMENT,
  `type` varchar(255) NOT NULL,
  UNIQUE KEY `idx_type` (`type`),
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `player_playtime_limit_v1_data_migration_progress`
(
  `id`   bigint(20)   NOT NULL AUTO_INCREMENT,
  `last_id_processed`   bigint(20)   NOT NULL,
  `running` bit(1) NOT NULL,
  `version` int(11)    NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `player_playtime_limit_v2_config_revision`
(
  `id`                bigint(20) NOT NULL AUTO_INCREMENT,
  `granularity_id`    bigint(20) DEFAULT NULL,
  `created_by_id`     bigint(20) DEFAULT NULL,
  `created_date`      datetime   NOT NULL,
  `effective_from`    datetime   NOT NULL,
  `user_id`           bigint(20) NOT NULL,
  `seconds_allocated` bigint(20) DEFAULT 0,
  `version`           int(11)    NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FKhp4rxponpav7lrbt3h8lshin1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKhp4rxponpav7lrbt3h8lshin0` FOREIGN KEY (`created_by_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKhp4rxponpav7lrbt3h8lshin4` FOREIGN KEY (`granularity_id`) REFERENCES `granularity` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `player_playtime_limit_v2_config`
(
  `id`                         bigint(20) NOT NULL AUTO_INCREMENT,
  `current_config_revision_id` bigint(20) DEFAULT NULL,
  `pending_config_revision_id` bigint(20) DEFAULT NULL,
  `user_id`                    bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user` (`user_id`),
  CONSTRAINT `FK9qxsgal6aq8bc38s5mrvlt053` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK9qxsgal6aq8bc38s5mrvlt051` FOREIGN KEY (`current_config_revision_id`) REFERENCES `player_playtime_limit_v2_config_revision` (`id`),
  CONSTRAINT `FK9qxsgal6aq8bc38s5mrvlt052` FOREIGN KEY (`pending_config_revision_id`) REFERENCES `player_playtime_limit_v2_config_revision` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `period`
(
  `id`             bigint(20) NOT NULL AUTO_INCREMENT,
  `granularity_id` bigint(20) DEFAULT NULL,
  `domain_id`      bigint(20) DEFAULT NULL,
  `date_start`     datetime   NOT NULL,
  `date_end`       datetime   NOT NULL,
  `version`        int(11)    NOT NULL,
  `day`            int(11)    NOT NULL,
  `week`           int(11)    NOT NULL,
  `month`          int(11)    NOT NULL,
  `year`           int(11)    NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FKhp4rxponpav7lrbt3h8lphin0` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`),
  CONSTRAINT `FKhp4rxponpav7lrbt3h8lzhin2` FOREIGN KEY (`granularity_id`) REFERENCES `granularity` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `player_playtime_limit_v2_entry`
(
  `id`                  bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id`             bigint(20) DEFAULT NULL,
  `period_id`             bigint(20) DEFAULT NULL,
  `limit_reached_at`    datetime DEFAULT NULL,
  `seconds_accumulated` bigint(20) NOT NULL,
  `version`             int(11)    NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FKhp4rxponpav7lrbt3lphin0` FOREIGN KEY (`period_id`) REFERENCES `period` (`id`),
  CONSTRAINT `FKhp4rxponpav7lrbt3lzhin2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


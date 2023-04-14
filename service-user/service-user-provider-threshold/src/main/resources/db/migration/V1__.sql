CREATE TABLE `domain`
(
  `id`      BIGINT AUTO_INCREMENT,
  `name`    VARCHAR(255) NULL,
  `version` INT          NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_domain` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user`
(
  `id`            BIGINT AUTO_INCREMENT,
  `dob_day`       INT          NULL,
  `dob_month`     INT          NULL,
  `dob_year`      INT          NULL,
  `guid`          VARCHAR(255) NULL,
  `notifications` BIT          DEFAULT 1,
  `test_account`  BIT          NULL,
  `version`       INT          NULL,
  `domain_id`     BIGINT       NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_guid` (`guid`),
  KEY `FKk1fhgjygffmevl2g3s` (`domain_id`),
  CONSTRAINT `FKk1hsfghfjjgpois` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `type`
(
  `id`      BIGINT AUTO_INCREMENT,
  `name`    VARCHAR(255) NULL,
  `version` INT          NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_type` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `threshold_revision`
(
  `id`             BIGINT AUTO_INCREMENT,
  `amount`         DECIMAL(19, 2) NULL,
  `created_by_id`  BIGINT         NULL,
  `created_date`   DATETIME       NULL,
  `granularity`    INT            NULL,
  `modified_by_id` BIGINT         NULL,
  `modified_date`  DATETIME       NULL,
  `percentage`     DECIMAL(19, 2) NULL,
  `version`        INT            NULL,
  `domain_id`      BIGINT         NULL,
  `type_id`        BIGINT         NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK3mqthvd0dq76kgg7oepmmvvtjb`
    FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`),
  CONSTRAINT `FKqbyricvebdou44asmhav0dcig1`
    FOREIGN KEY (`type_id`) REFERENCES `type` (`id`),
  CONSTRAINT `FKrfetja8sd4d7uh2tsre2bmmg`
    FOREIGN KEY (`created_by_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKldbltl72d04gvt3tyxshn06d6r`
    FOREIGN KEY (`modified_by_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `threshold`
(
  `id`         BIGINT AUTO_INCREMENT,
  `current_id` BIGINT NULL,
  `edit_id`    BIGINT NULL,
  `active`     BIT    DEFAULT 1,
  `version`    INT    NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK82fn86eieh77skel8dh3cso6me`
    FOREIGN KEY (`current_id`) REFERENCES `threshold_revision` (`id`),
  CONSTRAINT `FKeia0rd0wxhlxd1f8jdsnkhm235`
    FOREIGN KEY (`edit_id`) REFERENCES `threshold_revision` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `player_threshold_history`
(
  `id`                    BIGINT AUTO_INCREMENT,
  `amount`                DECIMAL(19, 2) NULL,
  `daily_limit`           DECIMAL(19, 2) NULL,
  `daily_limit_used`      DECIMAL(19, 2) NULL,
  `monthly_limit`         DECIMAL(19, 2) NULL,
  `monthly_limit_used`    DECIMAL(19, 2) NULL,
  `threshold_hit_date`    DATETIME       NULL,
  `version`               INT            NULL,
  `weekly_limit`          DECIMAL(19, 2) NULL,
  `weekly_limit_used`     DECIMAL(19, 2) NULL,
  `threshold_revision_id` BIGINT         NULL,
  `user_id`               BIGINT         NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FKakegehvd8c7upjkwrdwc6xni6e`
    FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKl9lg4yxd1q2ydf5x03rh38hec8`
    FOREIGN KEY (`threshold_revision_id`) REFERENCES `threshold_revision` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `threshold_age_group`
(
  `id`                    BIGINT AUTO_INCREMENT,
  `age_max`               INT    NULL,
  `age_min`               INT    NULL,
  `version`               INT    NULL,
  `threshold_revision_id` BIGINT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FKo4wkmnhdjxpibyjyl668emkysx`
    FOREIGN KEY (`threshold_revision_id`) REFERENCES `threshold_revision` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

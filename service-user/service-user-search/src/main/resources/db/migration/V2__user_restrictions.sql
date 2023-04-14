CREATE TABLE `domain_restrictions`
(
  `id`          bigint(20)   NOT NULL,
  `name`        varchar(255) NOT NULL,
  `domain_name` varchar(35)  NOT NULL,
  `enabled`     tinyint(1)   NOT NULL DEFAULT '0',
  `deleted`     tinyint(1)   NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_domain_set_id` (`id`),
  KEY `idx_domain_name` (`domain_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `user_restrictions`
(
  `id`          bigint(20)   NOT NULL AUTO_INCREMENT,
  `set_id`      bigint(20)   NOT NULL,
  `user_id`     bigint(20)   NOT NULL,
  `active_from` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `active_to`   timestamp    NULL     DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_restriction_set_id` (`set_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `FK_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_restriction_set` FOREIGN KEY (`set_id`) REFERENCES `domain_restrictions` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

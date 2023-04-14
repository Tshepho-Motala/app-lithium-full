CREATE TABLE `closure_reason`
(
    `id`          bigint(20)    NOT NULL AUTO_INCREMENT,
    `description` varchar(255) DEFAULT NULL,
    `domain_id`   bigint(20)    NULL,
    `text`        varchar(1000) NOT NULL,
    `version`     int(11)       NOT NULL,
    `deleted`     bit(1)        NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_domain_id` (`domain_id`),
    KEY `idx_deleted` (`deleted`),
    CONSTRAINT `idx_domain_id` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

INSERT INTO `status` (deleted, description, name, user_enabled, version)
VALUES (0, 'Account is closed by user', 'DISABLED_ACCOUNT_CLOSURE', 0, 0);

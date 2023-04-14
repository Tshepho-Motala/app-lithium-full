CREATE TABLE `domain` (
    `id`        bigint(20) NOT NULL AUTO_INCREMENT,
    `name`      varchar(255) DEFAULT NULL,
    `version`   int(11) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_domain_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `authentication` (
    `id`                bigint(20) NOT NULL AUTO_INCREMENT,
    `access_token`      longtext NOT NULL,
    `expiration_date`   datetime NOT NULL,
    `version`           int(11) NOT NULL,
    `domain_id`         bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_domain` (`domain_id`),
    CONSTRAINT `FKh8beg2hbv9fog77fersm8ar15` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user` (
    `id`        bigint(20) NOT NULL AUTO_INCREMENT,
    `guid`      varchar(255) DEFAULT NULL,
    `version`   int(11) NOT NULL,
    `domain_id` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_guid` (`guid`),
    KEY `FKk1hsftp46a7obygffmevl2g3s` (`domain_id`),
    CONSTRAINT `FKk1hsftp46a7obygffmevl2g3s` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `idin_request` (
    `id`                     bigint(20) NOT NULL AUTO_INCREMENT,
    `lithium_request_id`     varchar(255) DEFAULT NULL,
    `sphonic_transaction_id` varchar(255) DEFAULT NULL,
    `idin_applicant_hash`    varchar(255) NOT NULL,
    `bluem_transaction_id`   varchar(255) DEFAULT NULL,
    `return_url`             varchar(255) DEFAULT NULL,
    `verification_url`       varchar(255) DEFAULT NULL,
    `player_ip_address`      varchar(255) DEFAULT NULL,
    `version`                int(11) NOT NULL,
    `domain_id`              bigint(20) NOT NULL,
    `user_id`                bigint(20) NOT NULL,
    `created_date`           bigint(20) NOT NULL,
    `last_modified_date`     bigint(20) UNSIGNED,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_idin_request` (`domain_id`, `user_id`),
    KEY `FKk1hsftph6a7bydfffge32g46` (`domain_id`),
    KEY `FKk1hsffdsowp7bydffe2ge32g46` (`user_id`),
    CONSTRAINT `FKk1hsftph6a7bydfffge32g46` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`),
    CONSTRAINT `FKk1hsffdsowp7bydffe2ge32g46` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

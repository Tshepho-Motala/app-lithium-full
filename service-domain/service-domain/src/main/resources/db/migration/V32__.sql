CREATE TABLE `asset_template`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT,
    `deleted` bit(1) NOT NULL,
    `name`          varchar(255) NOT NULL,
    `description`   varchar(255) NULL,
    `lang`          varchar(255) NOT NULL,
    `version`       int(11) NOT NULL,
    `domain_id`     bigint(20) NOT NULL,
    `created_by_id` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY             `FK1626177008158` (`domain_id`),
    KEY             `FK1626177051589` (`created_by_id`),
    CONSTRAINT `FK1626177201204` FOREIGN KEY (`created_by_id`) REFERENCES `user` (`id`),
    CONSTRAINT `FK1626177243726` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

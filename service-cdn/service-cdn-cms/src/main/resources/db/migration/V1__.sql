CREATE TABLE `domain` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_domain_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

CREATE TABLE `cms_asset`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT,
    `deleted` bit(1) NOT NULL,
    `name`          varchar(255) NOT NULL,
    `url`           varchar(255) NOT NULL,
    `type`          varchar(50) NOT NULL,
    `size`          varchar(255) NOT NULL,
    `version`       int(11) NOT NULL,
    `domain_id`     bigint(20) NOT NULL,
    `uploaded_date` datetime NOT NULL,
    PRIMARY KEY (`id`),
    KEY             `FK1629894660490` (`domain_id`),
    CONSTRAINT `F1629894755850` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

CREATE TABLE `restriction_outcome_place_action` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `set_id` bigint(20) NOT NULL,
    `code` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_code` (`set_id`,`code`),
    CONSTRAINT `fk_domain_restriction_set_place` FOREIGN KEY (`set_id`) REFERENCES `domain_restriction_set` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `restriction_outcome_lift_action` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `set_id` bigint(20) NOT NULL,
    `code` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_code` (`set_id`,`code`),
    CONSTRAINT `fk_domain_restriction_set_lift` FOREIGN KEY (`set_id`) REFERENCES `domain_restriction_set` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
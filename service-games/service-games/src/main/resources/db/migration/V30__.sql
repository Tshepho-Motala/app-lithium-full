CREATE TABLE `game_studio` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `deleted` bit(1) NOT NULL,
    `name` varchar(255) NOT NULL,
    `version` int(11) NOT NULL,
    `domain_id` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_domain_name` (`domain_id`,`name`),
    KEY `idx_all` (`domain_id`,`name`,`deleted`),
    CONSTRAINT `fk_domain` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `game`
    ADD COLUMN `game_studio_id` BIGINT(20) DEFAULT NULL,
    ADD FOREIGN KEY `fk_game_studio` (`game_studio_id`) REFERENCES `game_studio` (`id`);
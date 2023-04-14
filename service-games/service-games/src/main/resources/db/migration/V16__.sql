CREATE TABLE `game_type`
(
    `id`        bigint(20)   NOT NULL AUTO_INCREMENT,
    `deleted`   bit(1)       NOT NULL,
    `name`      varchar(255) NOT NULL,
    `version`   int(11)      NOT NULL,
    `domain_id` bigint(20)   NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_domain_name` (`domain_id`, `name`),
    KEY `idx_all` (`domain_id`, `name`, `deleted`),
    CONSTRAINT `FKiutmplnrrefcvs9qvbdng2mla` FOREIGN KEY (`domain_id`) REFERENCES `lithium_games`.`domain` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

ALTER TABLE `lithium_games`.`game`
    ADD COLUMN `game_type_id` BIGINT(20) DEFAULT NULL,
    ADD FOREIGN KEY `fk_game_type` (`game_type_id`) REFERENCES `game_type` (`id`);
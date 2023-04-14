CREATE TABLE `player_time_slot_limit`
(
    `id`               bigint(20)   NOT NULL AUTO_INCREMENT,
    `player_guid`      varchar(255) NOT NULL,
    `domain_name`      varchar(255) NOT NULL,
    `limit_from_utc`   bigint(20)      NOT NULL,
    `limit_to_utc`     bigint(20)      NOT NULL,
    `create_timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `modify_timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `version`          int(11)      NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_plt_player_time` (`player_guid`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `player_time_slot_limit_history`
(
    `id`               bigint(20)   NOT NULL AUTO_INCREMENT,
    `player_guid`      varchar(255) NOT NULL,
    `domain_name`      varchar(255) NOT NULL,
    `limit_from_utc`   bigint(20)      NOT NULL,
    `limit_to_utc`     bigint(20)      NOT NULL,
     `modify_author_guid` varchar(255) NOT NULL,
    `create_timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `modify_timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `modify_type`      int(11) NOT NULL,
    `version`          int(11)      NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_pe_player` (`player_guid`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8;


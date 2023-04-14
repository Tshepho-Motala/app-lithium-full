CREATE TABLE `channel` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `version` int(11) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_game_channel_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE `game_channel` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `channel_id` bigint(20) NOT NULL,
    `game_id` bigint(20) NOT NULL,
    `version` int(11) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_game_channel` (`game_id`,`channel_id`),
    KEY `FK91qqemie6kdxsngi2nvw7ea4v` (`channel_id`),
    CONSTRAINT `FK91qqemie6kdxsngi2nvw7ea4v` FOREIGN KEY (`channel_id`) REFERENCES `channel` (`id`),
    CONSTRAINT `FKj21asjq7p394edmbeeql96mnv` FOREIGN KEY (`game_id`) REFERENCES `game` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE `channel_migration` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   `version` int(11) NOT NULL,
   `running` bit(1) NOT NULL,
   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

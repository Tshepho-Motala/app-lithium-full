CREATE TABLE `domain_game` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `domain_name` varchar(255) NOT NULL,
    `game_key` varchar(255) NOT NULL,
    `version` int(11) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX domain_game_domain_name_game_key_uindex (`domain_name`,`game_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
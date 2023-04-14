CREATE TABLE `game_category` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`casino_category` varchar(255) DEFAULT NULL,
`display_name` varchar(255) DEFAULT NULL,
`game_categories` varchar(255) DEFAULT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

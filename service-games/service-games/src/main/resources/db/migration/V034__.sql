CREATE TABLE `module` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   `name` varchar(255) NOT NULL,
   `version` int(11) NOT NULL,
   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE `progressive_jackpot_feed` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `version` int(11) NOT NULL,
    `enabled` bit(1) NOT NULL,
    `registered_on` datetime NOT NULL,
    `last_updated_on` datetime NOT NULL,
    `module_id` bigint(20) NOT NULL,
    `game_supplier_id` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_progressive_jackpot_feed_module` (`module_id`),
    KEY `fk_progressive_jackpot_feed_game_supplier` (`game_supplier_id`),
    CONSTRAINT `fk_progressive_jackpot_feed_module` FOREIGN KEY (`module_id`) REFERENCES `module` (`id`),
    CONSTRAINT `fk_progressive_jackpot_feed_game_supplier` FOREIGN KEY (`game_supplier_id`) REFERENCES `game_supplier` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE `currency` (
                          `id` bigint(20) NOT NULL AUTO_INCREMENT,
                          `code` varchar(10) NOT NULL,
                          `version` int(11) NOT NULL,
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE `progressive_jackpot_balance` (
                                               `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                               `version` int(11) NOT NULL,
                                               `progressive_id` varchar(255) NOT NULL,
                                               `amount` decimal(19,2) DEFAULT NULL,
                                               `won_by_amount` decimal(19,2) DEFAULT NULL,
                                               `game_supplier_id` bigint(20) NOT NULL,
                                               `currency_id` bigint(20) NOT NULL,
                                               PRIMARY KEY (`id`),
                                               CONSTRAINT `fk_progressive_jackpot_balance_currency` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`),
                                               CONSTRAINT `fk_progressive_jackpot_balance_supplier` FOREIGN KEY (`game_supplier_id`) REFERENCES `game_supplier` (`id`)
)ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE `progressive_jackpot_game_balance` (
                                               `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                               `version` int(11) NOT NULL,
                                               `progressive_id` varchar(255) NOT NULL,
                                               `amount` decimal(19,2) DEFAULT NULL,
                                               `won_by_amount` decimal(19,2) DEFAULT NULL,
                                               `game_id` bigint(20) NOT NULL,
                                               `currency_id` bigint(20) NOT NULL,
                                               PRIMARY KEY (`id`),
                                               CONSTRAINT `fk_progressive_jackpot_game_balance_game` FOREIGN KEY (`game_id`) REFERENCES `game` (`id`),
                                               CONSTRAINT `fk_progressive_jackpot_game_balance_currency` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`)
)ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

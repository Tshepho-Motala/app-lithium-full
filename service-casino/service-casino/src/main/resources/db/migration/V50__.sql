CREATE TABLE `bonus_rules_casino_chip` (
                                                       `casino_chip_value` bigint(20) NOT NULL,
                                                       `bonus_revision_id` bigint(20) NOT NULL,
                                                       `provider` varchar(255) DEFAULT NULL,
                                                       `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                                       PRIMARY KEY (`id`),
                                                       KEY `FKbni3dg64n934oc8m7lbdcsmcp` (`bonus_revision_id`),
                                                       CONSTRAINT `FKbni3dg64n934oc8m7lbdcsmcp` FOREIGN KEY (`bonus_revision_id`) REFERENCES `bonus_revision` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `bonus_rules_casino_chip_games` (
                                                             `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                                             `game_id` varchar(255) DEFAULT NULL,
                                                             `bonus_rules_casino_chip_id` bigint(20) NOT NULL,
                                                             PRIMARY KEY (`id`),
                                                             KEY `FK8hllkl4gagr22xflijo77qh67` (`bonus_rules_casino_chip_id`),
                                                             CONSTRAINT `FK8hllkl4gagr22xflijo77qh67` FOREIGN KEY (`bonus_rules_casino_chip_id`) REFERENCES `bonus_rules_casino_chip` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `bonus_rules_instant_reward` (
                                              `number_of_units` int(11) DEFAULT NULL,
                                              `bonus_revision_id` bigint(20) NOT NULL,
                                              `volatility` varchar(255) DEFAULT NULL,
                                              `provider` varchar(255) DEFAULT NULL,
                                              `instant_reward_unit_value` bigint(20) DEFAULT NULL,
                                              `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                              PRIMARY KEY (`id`),
                                              KEY `FKbnu3dg63n934oc8m7kbccsmcn` (`bonus_revision_id`),
                                              CONSTRAINT `FKbnu3dg63n934oc8m7kbccsmcn` FOREIGN KEY (`bonus_revision_id`) REFERENCES `bonus_revision` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `bonus_rules_instant_reward_games` (
                                                    `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                                    `game_id` varchar(255) DEFAULT NULL,
                                                    `bonus_rules_instant_reward_id` bigint(20) NOT NULL,
                                                    PRIMARY KEY (`id`),
                                                    KEY `FK8hllkl4gahr21xflijo77qh66` (`bonus_rules_instant_reward_id`),
                                                    CONSTRAINT `FK8hllkl4gahr21xflijo77qh66` FOREIGN KEY (`bonus_rules_instant_reward_id`) REFERENCES `bonus_rules_instant_reward` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;




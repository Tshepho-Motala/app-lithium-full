CREATE TABLE `bonus_unlock_games` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `game_guid` varchar(255) DEFAULT NULL,
  `game_id` varchar(255) DEFAULT NULL,
  `bonus_revision_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbgub68ie7hjw18yj5fu5tlwhe` (`bonus_revision_id`),
  CONSTRAINT `FKbgub68ie7hjw18yj5fu5tlwhe` FOREIGN KEY (`bonus_revision_id`) REFERENCES `bonus_revision` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

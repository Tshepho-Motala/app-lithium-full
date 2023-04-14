CREATE TABLE `player_bonus_pending` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bonus_amount` bigint(20) DEFAULT NULL,
  `bonus_percentage` int(11) DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `play_through_required_cents` bigint(20) DEFAULT NULL,
  `player_guid` varchar(255) DEFAULT NULL,
  `trigger_amount` bigint(20) DEFAULT NULL,
  `bonus_revision_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_pbp_player_guid` (`player_guid`),
  KEY `FK131ctgixbblt2115alxc59tyu` (`bonus_revision_id`),
  CONSTRAINT `FK131ctgixbblt2115alxc59tyu` FOREIGN KEY (`bonus_revision_id`) REFERENCES `bonus_revision` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
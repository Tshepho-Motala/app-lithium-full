ALTER TABLE `player_play_time_limit`
  ADD COLUMN `last_reset` datetime NOT NULL DEFAULT '1970-01-01 00:00:00';

CREATE TABLE `player_play_time_limit_ad_hoc_reset` (
 `id` bigint(20) NOT NULL,
 `running` bit(1) NOT NULL,
 `version` int(11) DEFAULT NULL,
 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

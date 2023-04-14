CREATE INDEX `idx_domain_name_granularity_type_time_in_minutes_used`
  ON `player_play_time_limit` (`domain_name`,`granularity`,`type`,`time_in_minutes_used`) ALGORITHM INPLACE LOCK NONE;

CREATE TABLE `user_active_sessions_metadata` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   `active_session_count` int(11) NOT NULL,
   `playtime_limit_last_updated` datetime DEFAULT NULL,
   `user_id` bigint(20) DEFAULT NULL,
   PRIMARY KEY (`id`),
   UNIQUE KEY `idx_user` (`user_id`),
   CONSTRAINT `FK9qxsgal6aq8bc38s5mrvlt050` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

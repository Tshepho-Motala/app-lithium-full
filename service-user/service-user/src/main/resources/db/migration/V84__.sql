CREATE TABLE `player_play_time_limit` (
                                          `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                          `version` int(11) NOT NULL,
                                          `user_id` bigint(20) NOT NULL,
                                          `domain_name` varchar(255) NOT NULL,
                                          `granularity` int(11) NOT NULL,
                                          `type` int(11) NOT NULL,
                                          `time_in_minutes` bigint(20) NOT NULL,
                                          `time_in_minutes_used` bigint(20) NOT NULL,
                                          `created_date` bigint(20) NOT NULL,
                                          `modified_date` bigint(20) NOT NULL,
                                          PRIMARY KEY (`id`),
                                          UNIQUE KEY `idx_pl_player_gran_type` (`user_id`, `granularity`, `type`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

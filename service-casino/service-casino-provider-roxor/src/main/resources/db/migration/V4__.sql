CREATE TABLE `reward_bonus_map` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `lithium_ext_bonus_id` bigint(20) NOT NULL,
  `roxor_reward_id` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_reward_bonus_map_lithiumExtBonusId` (`lithium_ext_bonus_id`),
  UNIQUE KEY `idx_reward_bonus_map_roxorRewardId` (`roxor_reward_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

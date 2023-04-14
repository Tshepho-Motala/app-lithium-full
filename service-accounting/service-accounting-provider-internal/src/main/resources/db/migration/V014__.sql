CREATE TABLE `summary_account_label_value_replay_job` (
  `id` bigint(20) NOT NULL,
  `current_id` bigint(20) DEFAULT NULL,
  `processing` bit(1) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
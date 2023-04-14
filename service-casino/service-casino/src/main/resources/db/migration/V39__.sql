CREATE TABLE `slot_api_data_migration` (
    `id` bigint(20) NOT NULL,
    `current_id` bigint(20) DEFAULT NULL,
    `processing` bit(1),
    `version` int(11) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
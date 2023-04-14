ALTER TABLE `game_graphic`
    MODIFY COLUMN `graphic_id` bigint(20) DEFAULT NULL,
    ADD COLUMN `url` varchar(255) DEFAULT NULL;
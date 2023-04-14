ALTER TABLE `game_type`
    ADD COLUMN type VARCHAR(255) NOT NULL,
    MODIFY COLUMN `name` VARCHAR(255) NOT NULL,
    DROP INDEX `idx_domain_name`,
    DROP INDEX idx_all,
    ADD CONSTRAINT `idx_domain_name_type` UNIQUE KEY (`domain_id`, `name`, `type`),
    ADD KEY `idx_all` (`domain_id`, `name`, `type`, deleted);

UPDATE `game_type` SET `type`='primary' WHERE name IS NOT NULL;

ALTER TABLE `game_type` MODIFY `type` VARCHAR(255) NOT NULL;

ALTER TABLE `game`
    ADD COLUMN `secondary_game_type_id` BIGINT(20) DEFAULT NULL,
    ADD CONSTRAINT fk_secondary_game_type FOREIGN KEY (`secondary_game_type_id`) REFERENCES `game_type` (`id`);
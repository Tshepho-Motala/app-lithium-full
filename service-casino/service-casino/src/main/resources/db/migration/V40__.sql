ALTER TABLE `provider`
    ADD COLUMN `domain_id` BIGINT(20) DEFAULT NULL;

UPDATE `provider`
    SET `domain_id` = (SELECT `id`
                       FROM `domain`
                       WHERE `name` = SUBSTRING_INDEX(`guid`, '/', 1));

ALTER TABLE `provider`
    ADD FOREIGN KEY `fk_domain` (`domain_id`) REFERENCES `domain` (`id`);
-- FIXME: Is there a better way to do this w/o cross db statements in a flyway script?...
INSERT IGNORE INTO `lithium_casino`.`domain` (`name`)
SELECT `name` FROM `lithium_domain`.`domain`;

ALTER TABLE `user` ADD COLUMN `domain_id` BIGINT(20) DEFAULT NULL;

UPDATE `user`
SET `user`.`domain_id` = (SELECT `id` FROM `domain` WHERE `name` = SUBSTRING_INDEX(`user`.`guid`, '/', 1));

ALTER TABLE `user` MODIFY COLUMN `domain_id` BIGINT(20) NOT NULL;
ALTER TABLE `user` ADD FOREIGN KEY `fk_domain` (`domain_id`) REFERENCES `domain` (`id`);

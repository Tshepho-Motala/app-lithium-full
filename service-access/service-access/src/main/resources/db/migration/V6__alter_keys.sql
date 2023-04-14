ALTER TABLE `list` DROP INDEX `UK_1g8ymw4pgef726ke3tyx5sbn6`;
ALTER TABLE `list` ADD CONSTRAINT UNIQUE `idx_domain_name` (`domain_id`,`name`);
ALTER TABLE `access_rule` DROP INDEX `idx_name`;
ALTER TABLE `access_rule` ADD CONSTRAINT UNIQUE `idx_domain_name` (`domain_id`, `name`);
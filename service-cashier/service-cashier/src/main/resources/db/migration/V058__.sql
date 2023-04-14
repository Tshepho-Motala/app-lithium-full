ALTER TABLE `processor_property` ADD COLUMN `client_available` BIT(1) NOT NULL DEFAULT 0;
ALTER TABLE `transaction` ADD COLUMN `error_code` INT DEFAULT NULL;
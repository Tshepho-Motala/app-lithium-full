UPDATE `domain_currency`
SET `is_default` = false
WHERE `is_default` IS NULL;

UPDATE `domain_currency`
INNER JOIN `currency` ON `domain_currency`.`currency_id` = `currency`.`id`
SET `domain_currency`.`name` = `currency`.`name`
WHERE `domain_currency`.`name` IS NULL;

ALTER TABLE `domain_currency` MODIFY COLUMN `is_default` BIT(1) NOT NULL DEFAULT 0;
ALTER TABLE `domain_currency` MODIFY COLUMN `name` VARCHAR(255) NOT NULL DEFAULT 'USD';
ALTER TABLE `domain_currency` ADD COLUMN `symbol` VARCHAR(255) NOT NULL DEFAULT '$';
ALTER TABLE `domain_currency` ADD COLUMN `divisor` int(11) NOT NULL DEFAULT 100;

UPDATE `domain_currency`
INNER JOIN `currency` ON `domain_currency`.`currency_id` = `currency`.`id`
SET `domain_currency`.`symbol` = `currency`.`code`
WHERE `symbol` IS NULL;
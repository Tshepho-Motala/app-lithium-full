ALTER TABLE `bet` ADD COLUMN `settlement_id` bigint(20) DEFAULT NULL;
ALTER TABLE `bet` ADD CONSTRAINT `fk_settlement_id` FOREIGN KEY (`settlement_id`) REFERENCES `settlement` (`id`);

UPDATE `bet`
INNER JOIN `settlement` ON `settlement`.`bet_id` = `bet`.`id`
SET `bet`.`settlement_id` = `settlement`.`id`;
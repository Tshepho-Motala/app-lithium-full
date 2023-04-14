ALTER TABLE `bet_selection` ADD COLUMN `settlement_selection_id` bigint(20) DEFAULT NULL;
ALTER TABLE `bet_selection` ADD CONSTRAINT `fk_settlement_selection_id` FOREIGN KEY (`settlement_selection_id`) REFERENCES `settlement_selection` (`id`);

UPDATE `bet_selection`
INNER JOIN `settlement_selection` ON `settlement_selection`.`bet_selection_id` = `bet_selection`.`id`
SET `bet_selection`.`settlement_selection_id` = `settlement_selection`.`id`;
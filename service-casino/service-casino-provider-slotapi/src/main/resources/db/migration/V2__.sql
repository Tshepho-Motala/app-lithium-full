ALTER TABLE `bet_round` ADD COLUMN `bet_result_id` bigint(20) DEFAULT NULL;
ALTER TABLE `bet_round` ADD CONSTRAINT `fk_bet_result_id` FOREIGN KEY (`bet_result_id`) REFERENCES `bet_result` (`id`);

UPDATE `bet_round`
INNER JOIN `bet_result` ON `bet_result`.`bet_round_id` = `bet_round`.`id`
SET `bet_round`.`bet_result_id` = `bet_result`.`id`;
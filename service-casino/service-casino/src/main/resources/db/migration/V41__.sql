ALTER TABLE `bet_round`
    ADD COLUMN `round_returns_total` DOUBLE DEFAULT NULL;

UPDATE `bet_round` br
    SET `round_returns_total` = (SELECT SUM(`returns`)
                                 FROM `bet_result`
                                 WHERE `bet_round_id` = br.id);

UPDATE `bet_round` br
    SET `round_returns_total` = 0
    WHERE `round_returns_total` IS NULL;

ALTER TABLE `bet_round` MODIFY COLUMN `round_returns_total` DOUBLE NOT NULL;

CREATE INDEX `idx_round_returns_total` ON `bet_round` (`round_returns_total`) ALGORITHM INPLACE LOCK NONE;

ALTER TABLE `bet_round` DROP FOREIGN KEY `FK5q9qokv9oujv9fbhyhw8batte`;
ALTER TABLE `bet_round` CHANGE `bet_result_id` `last_bet_result_id` BIGINT(20) DEFAULT NULL;
ALTER TABLE `bet_round` ADD FOREIGN KEY `fk_last_bet_result_id` (`last_bet_result_id`) REFERENCES `bet_result` (`id`);
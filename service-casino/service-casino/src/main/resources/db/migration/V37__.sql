ALTER TABLE `bet`
    DROP INDEX `idx_lithium_accounting_id`,
    DROP INDEX `idx_tran_id`,
    ADD COLUMN `provider_id` BIGINT(20) NOT NULL,
    ADD FOREIGN KEY `fk_provider` (`provider_id`) REFERENCES `provider` (`id`);
CREATE UNIQUE INDEX `idx_lithium_accounting_id` ON `bet` (`lithium_accounting_id`) ALGORITHM INPLACE LOCK NONE;
CREATE UNIQUE INDEX `idx_provider_tran_id` ON `bet` (`provider_id`, `bet_transaction_id`) ALGORITHM INPLACE LOCK NONE;

ALTER TABLE `bet_round` DROP INDEX `idx_guid`;
CREATE UNIQUE INDEX `idx_provider_guid` ON `bet_round` (`provider_id`, `guid`) ALGORITHM INPLACE LOCK NONE;

ALTER TABLE `bet_result`
DROP INDEX `idx_lithium_accounting_id`,
    DROP INDEX `idx_tran_id`,
    ADD COLUMN `provider_id` BIGINT(20) NOT NULL,
    ADD FOREIGN KEY `fk_provider` (`provider_id`) REFERENCES `provider` (`id`);
CREATE UNIQUE INDEX `idx_lithium_accounting_id` ON `bet_result` (`lithium_accounting_id`) ALGORITHM INPLACE LOCK NONE;
CREATE UNIQUE INDEX `idx_provider_tran_id` ON `bet_result` (`provider_id`, `bet_result_transaction_id`) ALGORITHM INPLACE LOCK NONE;


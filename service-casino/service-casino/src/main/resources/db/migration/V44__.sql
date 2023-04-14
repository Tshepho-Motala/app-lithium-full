SET FOREIGN_KEY_CHECKS = 0;

ALTER TABLE bet DROP FOREIGN KEY bet_ibfk_1;
ALTER TABLE bet DROP INDEX idx_provider_tran_id;
CREATE INDEX idx_provider_tran_id_kind_id ON `bet` (provider_id, bet_transaction_id, kind_id) ALGORITHM INPLACE LOCK NONE;
ALTER TABLE `bet` ADD FOREIGN KEY `fk_provider` (`provider_id`) REFERENCES `provider` (`id`), ALGORITHM INPLACE, LOCK NONE;

SET FOREIGN_KEY_CHECKS = 1;
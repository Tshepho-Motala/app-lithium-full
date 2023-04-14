SET FOREIGN_KEY_CHECKS = 0;

ALTER TABLE `reservation` ADD COLUMN `accounting_last_rechecked` DATETIME DEFAULT NULL, ALGORITHM INPLACE, LOCK NONE;
CREATE INDEX `idx_reservation_status_id_acc_last_rechecked` ON `reservation` (`reservation_status_id`, `accounting_last_rechecked`) ALGORITHM INPLACE LOCK NONE;

SET FOREIGN_KEY_CHECKS = 1;
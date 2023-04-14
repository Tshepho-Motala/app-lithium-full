ALTER TABLE `report_run_results`
ADD COLUMN `virtual_bet_amount_cents` bigint(20) DEFAULT NULL,
ADD COLUMN `virtual_bet_count` bigint(20) DEFAULT NULL,
ADD COLUMN `virtual_win_amount_cents` bigint(20) DEFAULT NULL,
ADD COLUMN `virtual_win_count` bigint(20) DEFAULT NULL,
ADD COLUMN `virtual_bet_void_amount_cents` bigint(20) DEFAULT NULL,
ADD COLUMN `virtual_bet_void_count` bigint(20) DEFAULT NULL,
ADD COLUMN `virtual_loss_amount_cents` bigint(20) DEFAULT NULL,
ADD COLUMN `virtual_loss_count` bigint(20) DEFAULT NULL;
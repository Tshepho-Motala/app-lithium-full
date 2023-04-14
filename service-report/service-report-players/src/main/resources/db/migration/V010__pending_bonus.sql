ALTER TABLE `report_run_results`
ADD COLUMN `current_balance_casino_bonus_pending_cents` BIGINT(20) DEFAULT NULL,
ADD COLUMN `period_opening_balance_casino_bonus_pending_cents` BIGINT(20) DEFAULT NULL,
ADD COLUMN `period_closing_balance_casino_bonus_pending_cents` BIGINT(20) DEFAULT NULL,
ADD COLUMN `casino_bonus_pending_amount_cents` BIGINT(20) DEFAULT NULL,
ADD COLUMN `casino_bonus_transfer_to_bonus_pending_amount_cents` BIGINT(20) DEFAULT NULL,
ADD COLUMN `casino_bonus_transfer_from_bonus_pending_amount_cents` BIGINT(20) DEFAULT NULL,
ADD COLUMN `casino_bonus_pending_cancel_amount_cents` BIGINT(20) DEFAULT NULL,
ADD COLUMN `casino_bonus_pending_count` BIGINT(20) DEFAULT NULL;
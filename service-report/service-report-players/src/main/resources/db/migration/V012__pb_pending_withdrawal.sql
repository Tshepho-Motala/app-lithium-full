ALTER TABLE `report_run_results`
ADD COLUMN `current_balance_pending_withdrawal_cents` bigint(20) DEFAULT NULL,
ADD COLUMN `period_opening_balance_pending_withdrawal_cents` bigint(20) DEFAULT NULL,
ADD COLUMN `period_closing_balance_pending_withdrawal_cents` bigint(20) DEFAULT NULL;

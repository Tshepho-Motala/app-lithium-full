ALTER TABLE `report_run_results`
ADD COLUMN `transfer_to_player_balance_pending_withdrawal_amount_cents` bigint(20) DEFAULT NULL,
ADD COLUMN `transfer_from_player_balance_pending_withdrawal_amount_cents` bigint(20) DEFAULT NULL;

-- account
DROP INDEX `idx_acc_balance` ON `account` ALGORITHM INPLACE LOCK NONE;

-- summary_account
DROP INDEX `idx_pd_damaged` ON `summary_account` ALGORITHM INPLACE LOCK NONE;

-- summary_account_label_value
DROP INDEX `idx_salv_damaged` ON `summary_account_label_value` ALGORITHM INPLACE LOCK NONE;

-- summary_account_transaction_type
DROP INDEX `idx_sat_damaged` ON `summary_account_transaction_type` ALGORITHM INPLACE LOCK NONE;

-- transaction
-- DROP INDEX `idx_trx_created` ON `transaction` ALGORITHM INPLACE LOCK NONE; Potentially useful for diagnostic queries
DROP INDEX `idx_trx_closed` ON `transaction` ALGORITHM INPLACE LOCK NONE;
DROP INDEX `idx_trx_open` ON `transaction` ALGORITHM INPLACE LOCK NONE;
DROP INDEX `idx_trx_cancelled` ON `transaction` ALGORITHM INPLACE LOCK NONE;

-- transaction_entry
-- DROP INDEX `idx_tx_date` ON `transaction_entry` ALGORITHM INPLACE LOCK NONE; Potentially useful for diagnostic queries
DROP INDEX `idx_tx_amount` ON `transaction_entry` ALGORITHM INPLACE LOCK NONE;
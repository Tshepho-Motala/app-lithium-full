set autocommit=0;
DROP INDEX `account_idx_account_id_currency_id` ON `account` ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_label_value_tran_type` ON `summary_account_label_value` (`label_value_id` ASC,`transaction_type_id` ASC) ALGORITHM INPLACE LOCK NONE;
commit

set autocommit=0;
DROP INDEX `idx_summary_domain_shard` ON `summary_domain` ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_summary_domain_shard` ON `summary_domain` (`period_id`,`account_code_id`, `currency_id`, `shard`, `test_users` ) ALGORITHM INPLACE LOCK NONE;
DROP INDEX `idx_summary_domain_all` ON `summary_domain` ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_summary_domain_all` ON `summary_domain` (`period_id`,`account_code_id`, `currency_id`, `test_users` ) ALGORITHM INPLACE LOCK NONE;
DROP INDEX `idx_summary_domain_transaction_type_shard` ON `summary_domain_transaction_type` ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_summary_domain_transaction_type_shard` ON `summary_domain_transaction_type` (`period_id`, `transaction_type_id`, `account_code_id`, `currency_id`, `shard`, `test_users` ) ALGORITHM INPLACE LOCK NONE;
DROP INDEX `idx_summary_domain_transaction_type_all` ON `summary_domain_transaction_type` ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_summary_domain_transaction_type_all` ON `summary_domain_transaction_type` (`period_id`, `transaction_type_id`, `account_code_id`, `currency_id`, `test_users` ) ALGORITHM INPLACE LOCK NONE;
DROP INDEX `idx_summary_domain_label_value_shard` ON `summary_domain_label_value` ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_summary_domain_label_value_shard` ON `summary_domain_label_value` (`period_id`, `transaction_type_id`, `account_code_id`,`label_value_id`, `currency_id`,`shard`, `test_users` ) ALGORITHM INPLACE LOCK NONE;
DROP INDEX `idx_summary_domain_label_value_all` ON `summary_domain_label_value` ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_summary_domain_label_value_all` ON `summary_domain_label_value` (`period_id`, `transaction_type_id`, `account_code_id`,`label_value_id`, `currency_id`, `test_users` ) ALGORITHM INPLACE LOCK NONE;
commit

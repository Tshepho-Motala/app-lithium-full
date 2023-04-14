ALTER TABLE `transaction_entry` 
ADD COLUMN `post_entry_account_balance_cents` BIGINT(20) NULL AFTER `account_id`,
ALGORITHM INPLACE,
LOCK NONE;

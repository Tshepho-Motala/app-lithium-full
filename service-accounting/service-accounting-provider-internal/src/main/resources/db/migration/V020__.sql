set autocommit=0;
DROP INDEX `account_idx_account_id_currency_id` ON `account` ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `account_idx_account_id_currency_id` ON `account` (`account_code_id` ASC,`currency_id` ASC,`id` ASC) ALGORITHM INPLACE LOCK NONE;
commit
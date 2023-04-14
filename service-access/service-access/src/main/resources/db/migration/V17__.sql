set autocommit=0;
CREATE INDEX `idx_creation_date` ON `raw_transaction_data` (`creation_date` ASC) ALGORITHM INPLACE LOCK NONE;
COMMIT

set autocommit=0;
CREATE INDEX `idx_timestamp` ON `transaction_processing_attempt` (`timestamp` ASC) ALGORITHM INPLACE LOCK NONE;
COMMIT

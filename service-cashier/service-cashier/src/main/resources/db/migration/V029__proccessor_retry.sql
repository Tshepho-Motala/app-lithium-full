ALTER TABLE `transaction` ADD retry_processing BIT DEFAULT false NOT NULL;

CREATE INDEX idx_tranRetryProcessing USING HASH ON `transaction`(retry_processing) ALGORITHM INPLACE LOCK NONE;

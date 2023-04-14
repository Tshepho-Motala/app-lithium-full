ALTER TABLE `transaction` ADD COLUMN `additional_reference` VARCHAR(255) DEFAULT NULL;
CREATE INDEX idx_additionalRef ON `transaction`(additional_reference) ALGORITHM INPLACE LOCK NONE;
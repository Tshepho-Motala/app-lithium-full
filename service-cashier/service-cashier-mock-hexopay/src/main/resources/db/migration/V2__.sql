ALTER TABLE `hexopay_customer` ADD COLUMN `first_name` VARCHAR(50) DEFAULT NULL;
ALTER TABLE `hexopay_customer` ADD COLUMN `last_name` VARCHAR(50) DEFAULT NULL;
ALTER TABLE `hexopay_customer` ADD COLUMN `address` VARCHAR(100) DEFAULT NULL;
ALTER TABLE `hexopay_customer` ADD COLUMN `city` VARCHAR(50) DEFAULT NULL;
ALTER TABLE `hexopay_customer` ADD COLUMN `state` VARCHAR(50) DEFAULT NULL;
ALTER TABLE `hexopay_customer` ADD COLUMN `zip` VARCHAR(50) DEFAULT NULL;
ALTER TABLE `hexopay_customer` ADD COLUMN `phone` VARCHAR(50) DEFAULT NULL;
ALTER TABLE `hexopay_customer` ADD COLUMN `country` VARCHAR(10) DEFAULT NULL;

ALTER TABLE `hexopay_transaction` ADD COLUMN `avs_reject` CHAR DEFAULT '1';
ALTER TABLE `hexopay_transaction` ADD COLUMN `cvc_reject` CHAR DEFAULT '1';
ALTER TABLE `hexopay_transaction` ADD COLUMN `avs_cvc_status` BIT(1) DEFAULT 1;

ALTER TABLE `hexopay_transaction_token` ADD COLUMN `avs_reject_codes` VARCHAR(50) DEFAULT NULL;
ALTER TABLE `hexopay_transaction_token` ADD COLUMN `cvc_reject_codes` VARCHAR(50) DEFAULT NULL;

ALTER TABLE `processor_user_card` MODIFY COLUMN `last_four_digits` varchar(255) DEFAULT NULL;
ALTER TABLE `processor_user_card` ADD COLUMN `hide_in_deposit` BIT(1) DEFAULT 0;
ALTER TABLE `processor_account_transaction` ADD COLUMN `redirect_url` VARCHAR(255) NULL;


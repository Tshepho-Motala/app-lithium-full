ALTER TABLE `user` ADD COLUMN `test_account` BIT(1) DEFAULT 0;
ALTER TABLE `user` ADD INDEX `idx_user_test` (`test_account` ASC);
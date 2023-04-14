ALTER TABLE `transaction` ADD INDEX `idx_domain_method_created_on`(`domain_method_id`, `created_on`), ALGORITHM=INPLACE, LOCK=NONE;
ALTER TABLE `transaction` DROP INDEX `idx_createdOn`, ALGORITHM=INPLACE, LOCK=NONE;

CREATE TABLE `processor_account_verification_type` (
                                          `id` INT NOT NULL AUTO_INCREMENT,
                                          `name` VARCHAR(255) NOT NULL,
                                          `version` INT(11) NOT NULL,
                                          PRIMARY KEY (`id`),
                                          UNIQUE KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `processor_user_card` ADD COLUMN `failed_verification_id` INT default NULL;
ALTER TABLE `processor_user_card` ADD CONSTRAINT `FK_payment_account_failed_verification_id` FOREIGN KEY (`failed_verification_id`) REFERENCES `processor_account_verification_type`(`id`);


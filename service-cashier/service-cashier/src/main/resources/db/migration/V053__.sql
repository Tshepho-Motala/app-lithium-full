CREATE TABLE `processor_account_type` (
                     `id` INT NOT NULL AUTO_INCREMENT,
                     `name` VARCHAR(255) NOT NULL,
                     `version` INT(11) NOT NULL,
                     PRIMARY KEY (`id`),
                     UNIQUE KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `processor_account_type` VALUES (1,'CARD', 0);

ALTER TABLE `processor_user_card` ADD COLUMN `type_id` INT default NULL;
ALTER TABLE `processor_user_card` ADD CONSTRAINT `FK_payment_account_type_id` FOREIGN KEY (`type_id`) REFERENCES `processor_account_type`(`id`);

UPDATE `processor_user_card` SET `type_id` = 1;

CREATE TABLE `processor_account_data` (
                     `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
                     `processor_account_id` bigint(20) NOT NULL,
                     `data` VARCHAR(8192) NULL,
                     PRIMARY KEY (`id`),
                     CONSTRAINT `FK_processor_account` FOREIGN KEY (`processor_account_id`) REFERENCES `processor_user_card` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `processor_account_transaction_state` (
                      `id` INT NOT NULL AUTO_INCREMENT,
                      `name` VARCHAR(255) NOT NULL,
                      `version` INT(11) NOT NULL,
                      PRIMARY KEY (`id`),
                      UNIQUE KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `processor_account_transaction` (
                    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
                    `version` INT(11),
                    `created_on` DATETIME(3),
                    `processor_reference` VARCHAR(255) DEFAULT NULL,
                    `user_id` BIGINT(20) NOT NULL,
                    `state_id` INT DEFAULT NULL,
                    `processor_account_id` BIGINT(20) DEFAULT NULL,
                    `domain_method_processor_id` BIGINT(20) NOT NULL,
                    `error_code` VARCHAR(255) DEFAULT NULL,
                    `error_message` VARCHAR(255) DEFAULT NULL,
                    PRIMARY KEY (`id`),
                    CONSTRAINT `FK_patx_processor_account` FOREIGN KEY (`processor_account_id`) REFERENCES `processor_user_card` (`id`),
                    CONSTRAINT `FK_patx_domain_method_processor_id` FOREIGN KEY (`domain_method_processor_id`) REFERENCES `domain_method_processor` (`id`),
                    CONSTRAINT `FK_patx_processor_account_transaction_state_id` FOREIGN KEY (`state_id`) REFERENCES `processor_account_transaction_state` (`id`),
                    CONSTRAINT `FK_patx_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


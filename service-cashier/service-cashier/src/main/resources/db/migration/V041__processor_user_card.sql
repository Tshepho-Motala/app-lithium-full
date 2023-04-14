CREATE TABLE `processor_user_card` (
   `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
   `domain_method_processor_id` bigint(20) NOT NULL,
   `user_id` bigint(20) NOT NULL,
   `reference` VARCHAR(255) NOT NULL,
   `card_type` VARCHAR(16) NULL,
   `last_four_digits` VARCHAR(16) NULL,
   `bin` VARCHAR(6) NULL,
   `expiry_date` VARCHAR(16) NULL,
   `is_active` BIT(1) NOT NULL DEFAULT 0,
   `is_default` BIT(1) NOT NULL DEFAULT 0,
   PRIMARY KEY (`id`),
   UNIQUE KEY `reference_idx` (`reference`),
   CONSTRAINT `FK_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
   CONSTRAINT `FK_domain_method_processor` FOREIGN KEY (`domain_method_processor_id`) REFERENCES `domain_method_processor` (`id`),
   INDEX `user_domain_method_processor_idx` (`user_id`, `domain_method_processor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `payment_method_status` (
                                 `id` int NOT NULL AUTO_INCREMENT,
                                 `name` varchar(255) NOT NULL,
                                 `description` varchar(255) NOT NULL,
                                 `version` int(11) NOT NULL,
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `payment_method_status` VALUES (1,'ACTIVE','No restrictions on that payment method', 0);

ALTER TABLE `processor_user_card` ADD COLUMN `status_id` int DEFAULT 1;
ALTER TABLE `processor_user_card` ADD CONSTRAINT `FK_payment_method_status_id` FOREIGN KEY (`status_id`) REFERENCES `payment_method_status` (`id`);

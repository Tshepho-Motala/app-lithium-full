CREATE TABLE `transaction_remark_type` (
                     `id` INT NOT NULL AUTO_INCREMENT,
                     `name` VARCHAR(255) NOT NULL,
                     `version` INT(11) NOT NULL,
                     PRIMARY KEY (`id`),
                     UNIQUE KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `transaction_remark` ADD COLUMN `remark_type_id` INT DEFAULT NULL;
ALTER TABLE `transaction_remark` ADD CONSTRAINT `FK_transaction_remark_type` FOREIGN KEY (`remark_type_id`) REFERENCES `transaction_remark_type` (`id`)


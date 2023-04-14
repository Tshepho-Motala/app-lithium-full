ALTER TABLE `lithium_user`.`user`
ADD COLUMN `external_username` varchar(35) DEFAULT NULL AFTER `username`,
ADD COLUMN `deleted_email` varchar(255) DEFAULT NULL AFTER `email`,
ADD COLUMN `deleted_telephone_number` varchar(255) DEFAULT NULL AFTER `telephone_number`,
ADD COLUMN `deleted_cellphone_number` varchar(255) DEFAULT NULL AFTER `cellphone_number`;

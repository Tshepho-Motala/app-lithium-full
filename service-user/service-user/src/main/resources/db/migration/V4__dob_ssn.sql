ALTER TABLE `lithium_user`.`user` 
ADD COLUMN `date_of_birth` DATETIME NULL AFTER `status_id`,
ADD COLUMN `social_security_number` VARCHAR(15) NULL AFTER `date_of_birth`;
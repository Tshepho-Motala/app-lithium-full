ALTER TABLE `lithium_user`.`user`
ADD COLUMN `password_hash` varchar(255) DEFAULT NULL AFTER `password`;

ALTER TABLE `lithium_user`.`user`
CHANGE COLUMN `password` `password_plaintext` VARCHAR(255) NULL ;

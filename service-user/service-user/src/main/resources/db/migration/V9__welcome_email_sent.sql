ALTER TABLE `lithium_user`.`user`
ADD COLUMN `welcome_email_sent` bit(1) DEFAULT 0;

UPDATE `lithium_user`.`user`
SET `welcome_email_sent` = TRUE;
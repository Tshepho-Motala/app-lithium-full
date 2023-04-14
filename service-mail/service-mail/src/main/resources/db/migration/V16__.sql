ALTER TABLE `email_template`
    ADD COLUMN `updated_on` DATETIME NULL DEFAULT NULL AFTER `enabled`;

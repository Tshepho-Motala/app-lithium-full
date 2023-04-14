ALTER TABLE `smstemplate`
    ADD COLUMN `updated_on` DATETIME NULL DEFAULT NULL AFTER `edit_by_id`;
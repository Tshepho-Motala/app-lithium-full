ALTER TABLE `sms`
ADD COLUMN `processing_started` DATETIME NULL,
ADD COLUMN `failed` BIT(1) NOT NULL DEFAULT 0;

UPDATE `sms` SET `processing` = false, `failed` = true WHERE processing = true;
ALTER TABLE `email`
ADD COLUMN `processing_started` DATETIME NULL,
ADD COLUMN `failed` BIT(1) NOT NULL DEFAULT 0;

UPDATE `email` SET `processing` = false, `failed` = true WHERE processing = true;
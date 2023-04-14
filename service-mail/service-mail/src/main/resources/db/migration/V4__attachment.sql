ALTER TABLE `email` 
ADD COLUMN `attachment_name` VARCHAR(255),
ADD COLUMN `attachment_data` LONGBLOB;
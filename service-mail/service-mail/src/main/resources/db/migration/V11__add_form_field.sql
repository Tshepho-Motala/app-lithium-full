ALTER TABLE `email_template_revision`
ADD COLUMN `email_from` VARCHAR(255) NULL DEFAULT NULL AFTER `email_template_id`;
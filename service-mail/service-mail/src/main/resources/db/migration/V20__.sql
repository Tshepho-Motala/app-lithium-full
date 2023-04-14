ALTER TABLE `email`
    ADD COLUMN `template_id` bigint(20),
    ADD CONSTRAINT `FK_template_id` FOREIGN KEY (`template_id`) REFERENCES `email_template` (id);

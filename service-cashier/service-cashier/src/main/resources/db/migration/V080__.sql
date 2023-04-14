LOCK TABLES `tag_type` WRITE , `transaction_tag` WRITE;

ALTER TABLE `transaction_tag`
    DROP FOREIGN KEY `FK1J6eker5j2rtkghjtk3pfj89l`;

ALTER TABLE `tag_type` DROP COLUMN `version`,
    CHANGE `id` `id` INT NOT NULL;

ALTER TABLE `transaction_tag`
    ADD CONSTRAINT `FK1J6eker5j2rtkghjtk3pfj89l` FOREIGN KEY (`type_id`) REFERENCES `tag_type` (`id`);

UNLOCK TABLES;
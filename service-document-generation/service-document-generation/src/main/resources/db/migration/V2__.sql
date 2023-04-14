ALTER TABLE `document_generation` MODIFY COLUMN `user_guid` VARCHAR(255);
CREATE TABLE `request_parameters` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `parameter_key` VARCHAR(255) NOT NULL,
    `parameter_value` VARCHAR(255) NULL DEFAULT NULL,
    `generation_id` BIGINT(20) NOT NULL,
PRIMARY KEY (`id`),
    INDEX `fk_doc_generation_id_idx` (`generation_id` ASC),
    INDEX `doc_generation_idx` (`generation_id` ASC),
    CONSTRAINT `fk_doc_generation_id`
FOREIGN KEY (`generation_id`)
    REFERENCES `document_generation` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION) ENGINE=InnoDB DEFAULT CHARSET=utf8;;
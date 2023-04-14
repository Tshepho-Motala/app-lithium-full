ALTER TABLE `document_v2` ADD COLUMN `file_name` varchar(255) DEFAULT NULL;
UPDATE `document_v2` dv2
    INNER JOIN document_file df on dv2.document_file_id = df.id
    INNER JOIN file f on df.file_id = f.id
SET dv2.file_name = f.name;

CREATE TABLE `document_type_mapping_name`
(
    `id`               bigint(20) NOT NULL AUTO_INCREMENT,
    `name`             varchar(64) NOT NULL,
    `document_type_id` BIGINT(20) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY `fk_document_type_id` (`document_type_id`) REFERENCES `document_type` (`id`),
    UNIQUE KEY `idx_document_type_name_unique` (`document_type_id`, `name`),
    KEY                `idx_name` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

INSERT INTO `document_type_mapping_name` (`name`, `document_type_id`)
SELECT document_type.type, document_type.id
FROM `document_type` AS document_type;

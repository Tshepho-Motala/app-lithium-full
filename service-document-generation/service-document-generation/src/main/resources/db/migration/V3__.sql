ALTER TABLE `document_generation` CHANGE COLUMN `user_guid` `user_guid` VARCHAR(255) NULL DEFAULT NULL ;

CREATE TABLE `document_file`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT,
    `reference`    varchar(255) NOT NULL,
    `provider`     varchar(255) NOT NULL,
    `data`         longblob,
    `created_date` datetime   NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `idx_doc_reference` (`reference` ASC),
    INDEX `idx_doc_created_date` (`created_date` ASC)
) ENGINE = InnoDB
  AUTO_INCREMENT = 48
  DEFAULT CHARSET = utf8;

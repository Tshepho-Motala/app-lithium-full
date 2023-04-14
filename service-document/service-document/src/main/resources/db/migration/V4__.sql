CREATE TABLE `domain`
(
    `id`      bigint(20)   NOT NULL AUTO_INCREMENT,
    `name`    varchar(255) NOT NULL,
    `version` int(11)      NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_domain_name` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `review_status`
(
    `id`      bigint(20)   NOT NULL AUTO_INCREMENT,
    `name`    varchar(128) NOT NULL,
    `version` int(11)      NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_name` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `review_reason`
(
    `id`        bigint(20)   NOT NULL AUTO_INCREMENT,
    `name`      varchar(128) NOT NULL,
    `domain_id` BIGINT(20)   NOT NULL,
    `enabled`   bit(1)       NOT NULL,
    `version`   int(11)      NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY `fk_domain` (`domain_id`) REFERENCES `domain` (`id`),
    UNIQUE KEY `idx_name_domain` (`name`, `domain_id`),
    KEY `idx_enabled` (`enabled`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

# update document type
DROP TABLE document_type;

CREATE TABLE `document_type`
(
    `id`            bigint(20)  NOT NULL AUTO_INCREMENT,
    `purpose`       int(11)     NOT NULL,
    `type`          varchar(64) NOT NULL,
    `domain_id`     BIGINT(20)  NOT NULL,
    `icon_base64`   longblob     DEFAULT NULL,
    `icon_name`     varchar(255) DEFAULT NULL,
    `icon_type`     varchar(255) DEFAULT NULL,
    `icon_size`     bigint(20)   DEFAULT NULL,
    `enabled`       bit(1)      NOT NULL,
    `modified_date` bigint(20)  NOT NULL,
    `version`       int(11)     NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY `fk_domain` (`domain_id`) REFERENCES `domain` (`id`),
    UNIQUE KEY `idx_domain_purpose_type` (`domain_id`, `purpose`, `type`),
    KEY `idx_enabled` (`enabled`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `document_v2`
(
    `id`                    bigint(20) NOT NULL AUTO_INCREMENT,
    `domain_id`             BIGINT(20) NOT NULL,
    `owner_id`              bigint(20) NOT NULL,
    `document_type_id`      bigint(20) NOT NULL,
    `review_status_id`      bigint(20) NOT NULL,
    `review_reason_id`      bigint(20) DEFAULT NULL,
    `document_file_id`      bigint(20) NOT NULL,
    `sensitive`             BIT(1)     NOT NULL DEFAULT 0,
    `deleted`               bit(1)     NOT NULL,
    `version`               int(11)    NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_owner_document_file` (`owner_id`, `document_file_id`),
    FOREIGN KEY `fk_domain` (`domain_id`) REFERENCES `domain` (`id`),
    FOREIGN KEY `fk_owner` (`owner_id`) REFERENCES `owner` (`id`),
    FOREIGN KEY `fk_document_type` (`document_type_id`) REFERENCES `document_type` (`id`),
    FOREIGN KEY `fk_review_status` (`review_status_id`) REFERENCES `review_status` (`id`),
    FOREIGN KEY `fk_review_reason` (`review_reason_id`) REFERENCES `review_reason` (`id`),
    FOREIGN KEY `fk_document_file` (`document_file_id`) REFERENCES `document_file` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

ALTER TABLE document_file MODIFY COLUMN `document_id` bigint(20) DEFAULT NULL;

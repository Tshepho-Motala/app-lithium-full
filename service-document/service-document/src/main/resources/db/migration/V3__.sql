CREATE TABLE `document_type`
(
    `id`            bigint(20)   NOT NULL AUTO_INCREMENT,
    `purpose`       int(11)      NOT NULL,
    `type`          varchar(64)  NOT NULL,
    `domain_name`   varchar(255) NOT NULL,
    `icon_base64`    longblob     DEFAULT NULL,
    `icon_name`      varchar(255) DEFAULT NULL,
    `icon_type`      varchar(255) DEFAULT NULL,
    `icon_size`      bigint(20)   DEFAULT NULL,
    `enabled`       bit(1)       NOT NULL,
    `modified_date` bigint(20)   NOT NULL,
    `version`       int(11)      NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_domainname_purpose_type` (`domain_name`, `purpose`, `type`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

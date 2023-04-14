CREATE TABLE `vendor_data`
(
    `id`      bigint(20)   NOT NULL AUTO_INCREMENT,
    `version` int(11)      NOT NULL,
    `result_id` bigint(20) NOT NULL,
    `vendor`    varchar(255) NOT NULL,
    `name`   varchar(255) NOT NULL,
    `value`   varchar(512),
    PRIMARY KEY (`id`),
    CONSTRAINT `FK_vendor_data_verification_result` FOREIGN KEY (`result_id`) REFERENCES `verification_result` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


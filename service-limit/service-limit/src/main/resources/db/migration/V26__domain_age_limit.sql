CREATE TABLE `domain_age_limit`
(
    `id`               bigint(20)   NOT NULL AUTO_INCREMENT,
    `amount`           bigint(20)   NOT NULL,
    `domain_name`      varchar(255) NOT NULL,
    `granularity`      int(11)      NOT NULL,
    `age_max`          int(11)      NOT NULL,
    `age_min`          int(11)      NOT NULL,
    `created_date`     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `modified_date`    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `created_by_guid`  varchar(255) NOT NULL,
    `modified_by_guid` varchar(255) NOT NULL,
    `type`             int(11)      NOT NULL,
    `version`          int(11)      NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET=utf8;
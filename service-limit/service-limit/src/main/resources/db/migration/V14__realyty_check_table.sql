CREATE TABLE `reality_check_set`
(
    `id`         bigint(20) NOT NULL AUTO_INCREMENT,
    `guid`       varchar(255)    NOT NULL,
    `timer_time` bigint(20) NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_pe_user` (`guid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `reality_check_track_data`
(
    `id`     bigint(20) NOT NULL AUTO_INCREMENT,
    `guid`   varchar(255) NOT NULL,
    `action` varchar(255) NOT NULL,
    `date`   datetime NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
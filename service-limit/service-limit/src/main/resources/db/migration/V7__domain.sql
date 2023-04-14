CREATE TABLE `domain`
(
    `id`      bigint(20)   NOT NULL AUTO_INCREMENT,
    `name`    varchar(255) NOT NULL,
    `version` int(11)      NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_domain_name` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
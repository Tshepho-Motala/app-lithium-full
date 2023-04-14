CREATE TABLE `banner`
(
    `id`                 BIGINT(20)   NOT NULL AUTO_INCREMENT,
    `version`            INT(11)      NOT NULL,
    `name`               VARCHAR(255) NOT NULL,
    `date`               DATE    NOT NULL,
    `time_from`          TIME,
    `time_to`            TIME         NULL,
    `link`               VARCHAR(255),
    `image_url`          VARCHAR(255),
    `display_text`       VARCHAR(255),
    `terms_url`          VARCHAR(255),
    `length_in_days`     INT(11),
    `single_day`         BIT(1),
    `recurrence_pattern` VARCHAR(255) NOT NULL,
    `logged_in`          BIT(1),
    `domain_id`          BIGINT(20),
    `deleted`            BIT(1) NOT NULL DEFAULT false,
    PRIMARY KEY (`id`),
    KEY `FKhkas57HmpaeJyas1thsm45sae` (`domain_id`),
    index `idx_time_from` (`time_from`),
    index `idx_time_to` (`time_to`),
    CONSTRAINT `FKhkas57HmpaeJyas1thsm45sae` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `page_banner`
(
    `id`                 bigint(20) NOT NULL AUTO_INCREMENT,
    `version`            int(11)    NOT NULL,
    `primary_nav_code`   varchar(255),
    `secondary_nav_code` varchar(255),
    `channel`            varchar(255),
    `position`           TINYINT,
    `lobby_id`           bigint(20) NOT NULL,
    `banner_id`          bigint(20) NOT NULL,
    `deleted`            BIT(1) NOT NULL DEFAULT false,
    PRIMARY KEY (`id`),
    KEY `FKhkas17HmpaeJyas2rgmk11saw` (`lobby_id`),
    KEY `FKhkas13HbWhksd6hjbkjas99hq` (`banner_id`),
    index `idx_lobby_page_banner` (`primary_nav_code`, `secondary_nav_code`, `channel`, `lobby_id`, `deleted`),
    CONSTRAINT `FKhkas17HmpaeJyas2rgmk11saw` FOREIGN KEY (`lobby_id`) REFERENCES `lobby` (`id`),
    CONSTRAINT `FKhkas13HbWhksd6hjbkjas99hq` FOREIGN KEY (`banner_id`) REFERENCES `banner` (`id`)
);

CREATE TABLE `tag`
(
    `id`        BIGINT(20)   NOT NULL AUTO_INCREMENT,
    `version`   INT(11)      NOT NULL,
    `name`      VARCHAR(255) NOT NULL,
    `domain_id` BIGINT(20)   NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FKhenj84hjhaeJyas1thsm26cre` (`domain_id`),
    CONSTRAINT `FKhenj84hjhaeJyas1thsm26cre` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`)
);

CREATE TABLE `banner_tag`
(
    `id`        BIGINT(20) NOT NULL AUTO_INCREMENT,
    `version`   INT(11)    NOT NULL,
    `enabled`      bit(1)     NOT NULL,
    `banner_id` BIGINT(20) NOT NULL,
    `tag_id`    BIGINT(20) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FKhenj8vRWOsDbl15DB1olVlcbS` (`banner_id`),
    KEY `FKhenj8ApqiiajNaeEsJgKywzNk` (`tag_id`),
    CONSTRAINT `FKhenj8vRWOsDbl15DB1olVlcbS` FOREIGN KEY (`banner_id`) REFERENCES `banner` (`id`),
    CONSTRAINT `FKhenj8ApqiiajNaeEsJgKywzNk` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`)
);

CREATE TABLE `banner_schedule`
(
    `id`        BIGINT(20) NOT NULL AUTO_INCREMENT,
    `version`   INT(11)    NOT NULL,
    `start_date`  DATE       NOT NULL,
    `end_date`    DATE       NOT NULL,
    `closed`      bit(1)     NOT NULL,
    `banner_id` BIGINT(20) NOT NULL,
    `deleted`            BIT(1) NOT NULL DEFAULT false,
    PRIMARY KEY (`id`),
    index `idx_start_date` (`start_date`),
    index `idx_end_date` (`end_date`),
    KEY `FKhydj4vRWOsDbl15DB1guJwBl0` (`banner_id`),
    CONSTRAINT `FKhydj4vRWOsDbl15DB1guJwBl0` FOREIGN KEY (`banner_id`) REFERENCES `banner` (`id`)
);

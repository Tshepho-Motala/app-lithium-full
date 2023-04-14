CREATE TABLE `paynl_iban`
(
    `id`      bigint(20) NOT NULL AUTO_INCREMENT,
    `version` int(11) NOT NULL,
    `number`  varchar(255) NOT NULL UNIQUE,
    `bic`     varchar(255),
    `holder`  varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `paynl_payment`
(
    `id`      bigint(20) NOT NULL AUTO_INCREMENT,
    `version` int(11) NOT NULL,
    `method`  varchar(255) NOT NULL,
    `iban_id` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `FK_iban_id` FOREIGN KEY (`iban_id`) REFERENCES `paynl_iban` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `paynl_customer`
(
    `id`         bigint(20) NOT NULL AUTO_INCREMENT,
    `version`    int(11) NOT NULL,
    `first_name` varchar(255),
    `last_name`  varchar(255),
    `ip_address` varchar(255),
    `birth_date` varchar(255),
    `gender`     varchar(255),
    `phone`      varchar(255),
    `email`      varchar(255),
    `language`   varchar(255),
    `trust`      int(11),
    `reference`  varchar(255),
    `payment_id`  bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `FK_payment_id` FOREIGN KEY (`payment_id`) REFERENCES `paynl_payment` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `paynl_order`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT,
    `version`       int(11)    NOT NULL,
    `order_id`      varchar(255),
    `country_code`  varchar(255),
    `delivery_date` varchar(255),
    `invoice_date`  varchar(255),
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `paynl_stats`
(
    `id`        bigint(20) NOT NULL AUTO_INCREMENT,
    `version`   int(11) NOT NULL,
    `info`      varchar(255),
    `tool`      varchar(255),
    `extra1`    varchar(255),
    `extra2`    varchar(255),
    `extra3`    varchar(255),
    `domain_id` varchar(255),
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `paynl_amount`
(
    `id`       bigint(20) NOT NULL AUTO_INCREMENT,
    `version`  int(11) NOT NULL,
    `value`    bigint(20) NOT NULL,
    `currency` varchar(255),
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE `paynl_transaction`
(
    `system_id`    bigint(20)   NOT NULL AUTO_INCREMENT,
    `version`      int(11)      NOT NULL,
    `id`           varchar(255) NOT NULL,
    `service_id`   varchar(255) NOT NULL,
    `description`  varchar(255),
    `reference`    varchar(255),
    `amount_id`    bigint(20)   NOT NULL,
    `created`      varchar(255),
    `modified`     varchar(255),
    `exchange_url` varchar(255),
    `status`       varchar(255),
    `type`         varchar(255),
    `refund_id`    varchar(255),
    `customer_id`  bigint(20) DEFAULT NULL,
    `stats_id`     bigint(20) DEFAULT NULL,
    `order_id`     bigint(20) DEFAULT NULL,
    PRIMARY KEY (`system_id`),
    CONSTRAINT `FK_amount_id` FOREIGN KEY (`amount_id`) REFERENCES `paynl_amount` (`id`),
    CONSTRAINT `FK_customer_id` FOREIGN KEY (`customer_id`) REFERENCES `paynl_customer` (`id`),
    CONSTRAINT `FK_stats_id` FOREIGN KEY (`stats_id`) REFERENCES `paynl_stats` (`id`),
    CONSTRAINT `FK_order_id` FOREIGN KEY (`order_id`) REFERENCES `paynl_order` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

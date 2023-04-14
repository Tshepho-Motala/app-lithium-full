CREATE TABLE `flutterwave_transaction`
(
    `id`         bigint(20) NOT NULL AUTO_INCREMENT,
    `tx_ref`     varchar(255) NOT NULL,
    `flw_ref`    varchar(255) NOT NULL,
    `amount`     bigint(20) NOT NULL,
    `currency`   varchar(255) NOT NULL,
    `created_at` datetime     NOT NULL,
    `status`     varchar(255) NOT NULL,
    `finalized`  tinyint(1) NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;



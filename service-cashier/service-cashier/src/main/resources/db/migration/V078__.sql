CREATE TABLE `tag_type`
(
    `id` INT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    `version` INT(11) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_name` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `transaction_tag`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT,
    `transaction_id` bigint(20) NOT NULL,
    `type_id`            int(11)    NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_transaction_and_tag_type` (`transaction_id`, `type_id`),
    CONSTRAINT `FK1td3irjstso8rygmc56pgl87k` FOREIGN KEY (`transaction_id`) REFERENCES `transaction` (`id`),
    CONSTRAINT `FK1J6eker5j2rtkghjtk3pfj89l` FOREIGN KEY (`type_id`) REFERENCES `tag_type` (`id`)

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

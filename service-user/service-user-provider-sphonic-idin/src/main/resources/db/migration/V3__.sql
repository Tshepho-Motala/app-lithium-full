CREATE TABLE `idin_response`
(
    `id`                bigint(20) NOT NULL AUTO_INCREMENT,
    `idin_request_id`   bigint NOT NULL,
    `stage`             int(2) NOT NULL,
    `raw_response_data` longtext DEFAULT NULL,
    `version`           int(11) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `FKsmakwfd7sbsfcnny0gnzva6f7p` FOREIGN KEY (`idin_request_id`) REFERENCES `idin_request` (`id`)
) ENGINE = InnoDB
DEFAULT CHARSET = utf8;

CREATE INDEX `idx_idin_res_request_id` ON `idin_response` (`idin_request_id`) ALGORITHM INPLACE LOCK NONE;
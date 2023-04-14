SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE `reservation_status` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `reservation`
    MODIFY COLUMN `accounting_transaction_id` BIGINT(20) DEFAULT NULL,
    ADD COLUMN `reservation_status_id` BIGINT(20) DEFAULT NULL,
    ALGORITHM INPLACE,
    LOCK NONE;

INSERT INTO `reservation_status` (`name`, `version`)
VALUES ('COMPLETED', 0);

UPDATE `reservation`
SET `reservation_status_id` = (SELECT `id`
                               FROM `reservation_status`
                               WHERE `name` = 'COMPLETED');

ALTER TABLE `reservation`
    MODIFY COLUMN `reservation_status_id` BIGINT(20) NOT NULL,
    ADD FOREIGN KEY `fk_reservation_status` (`reservation_status_id`) REFERENCES `reservation_status` (`id`),
    ALGORITHM INPLACE,
    LOCK NONE;

SET FOREIGN_KEY_CHECKS = 1;
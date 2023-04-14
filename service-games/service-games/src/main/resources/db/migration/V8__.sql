CREATE TABLE `domain` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_domain_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- FIXME: Is there a better way to do this w/o cross db statements in a flyway script?...
INSERT IGNORE INTO `lithium_games`.`domain` (`name`)
SELECT `name` FROM `lithium_domain`.`domain`;

ALTER TABLE `game` ADD COLUMN `domain_id` BIGINT(20) DEFAULT NULL;

UPDATE `game`
SET `game`.`domain_id` = (SELECT `id` FROM `domain` WHERE `name` = `game`.`domain_name`);

ALTER TABLE `game`
    MODIFY COLUMN `domain_id` BIGINT(20) NOT NULL,
    ADD FOREIGN KEY `fk_domain` (`domain_id`) REFERENCES `domain` (`id`);

ALTER TABLE `game`
    DROP INDEX `idx_gm_guid_domain`,
    DROP INDEX `idx_gm_domain`,
    DROP COLUMN `domain_name`;

CREATE UNIQUE INDEX `idx_gm_guid_domain` ON `game` (`guid`,`domain_id`) ALGORITHM INPLACE LOCK NONE;


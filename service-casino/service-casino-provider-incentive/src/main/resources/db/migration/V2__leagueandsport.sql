
/*!40101 SET NAMES utf8 */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sport` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_sport_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `competition` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `name` varchar(255),
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_condition_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `bet_selection` ADD COLUMN `sport_id` bigint(20) DEFAULT NULL;
CREATE INDEX `idx_sport_id` ON `bet_selection` (`sport_id`) ALGORITHM INPLACE LOCK NONE;
ALTER TABLE `bet_selection` ADD CONSTRAINT `ctx_sport_id` FOREIGN KEY (`sport_id`) REFERENCES `sport` (`id`);

ALTER TABLE `bet_selection` ADD COLUMN `competition_id` bigint(20) DEFAULT NULL;
CREATE INDEX `idx_competition_id` ON `bet_selection` (`competition_id`) ALGORITHM INPLACE LOCK NONE;
ALTER TABLE `bet_selection` ADD CONSTRAINT `ctx_competition_id` FOREIGN KEY (`competition_id`) REFERENCES `competition` (`id`);

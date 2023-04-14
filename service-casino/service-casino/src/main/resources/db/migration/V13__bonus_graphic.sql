CREATE TABLE `graphic` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deleted` bit(1) NOT NULL,
  `image` longblob,
  `md5hash` varchar(255) DEFAULT NULL,
  `size` bigint(20) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_gr_size_md5` (`size`,`md5hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE `bonus_revision` 
ADD COLUMN `bonus_description` varchar(999) DEFAULT NULL;


ALTER TABLE `bonus_revision` 
ADD COLUMN `graphic_id` bigint(20) DEFAULT NULL;

ALTER TABLE `bonus_revision` 
ADD CONSTRAINT `FKes80e2ef2utgtcdrdb7yunxt5` FOREIGN KEY (`graphic_id`) REFERENCES `graphic` (`id`);
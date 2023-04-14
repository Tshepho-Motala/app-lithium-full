CREATE TABLE `graphic` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deleted` bit(1) NOT NULL,
  `image` longblob DEFAULT NULL,
  `md5hash` varchar(255) DEFAULT NULL,
  `size` bigint(20) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_gr_size_md5` (`size`,`md5hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `graphic_function` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `enabled` bit(1) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_7axgj3tiyqoqc2ch0asvt1b2o` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `product_graphic` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deleted` bit(1) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `version` int(11) NOT NULL,
  `graphic_id` bigint(20) NOT NULL,
  `graphic_function_id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_pg_product` (`product_id`),
  KEY `idx_pg_product_function` (`product_id`,`graphic_function_id`),
  KEY `FK4c8v0y325iko08n9i8bpbnbwp` (`graphic_id`),
  KEY `FKe3twe9pmt7jmu2gaicw0uxixo` (`graphic_function_id`),
  CONSTRAINT `FK4c8v0y325iko08n9i8bpbnbwp` FOREIGN KEY (`graphic_id`) REFERENCES `graphic` (`id`),
  CONSTRAINT `FK6hp3vodje5gp1dnmid900mmac` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `FKe3twe9pmt7jmu2gaicw0uxixo` FOREIGN KEY (`graphic_function_id`) REFERENCES `graphic_function` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
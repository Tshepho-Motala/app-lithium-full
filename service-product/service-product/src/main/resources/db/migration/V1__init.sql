CREATE TABLE `domain` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_domain_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `enabled` bit(1) DEFAULT NULL,
  `guid` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `notification` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `domain_id` bigint(20) NOT NULL,
  `currency_amount` decimal(19,2) DEFAULT NULL,
  `currency_code` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_guid_domain` (`guid`,`domain_id`),
  KEY `FKb7vux51kft7gq81fh2wy2fq0d` (`domain_id`),
  CONSTRAINT `FKb7vux51kft7gq81fh2wy2fq0d` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `local_currency` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `country_code` varchar(255) DEFAULT NULL,
  `currency_amount` decimal(19,2) DEFAULT NULL,
  `currency_code` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_curr_product` (`country_code`,`currency_code`,`product_id`),
  KEY `FKsx8vb9j2k8sgtqrpaed51mv9v` (`product_id`),
  CONSTRAINT `FKsx8vb9j2k8sgtqrpaed51mv9v` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `payout` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bonus_code` varchar(255) DEFAULT NULL,
  `currency_amount` decimal(19,2) DEFAULT NULL,
  `currency_code` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_curr_product` (`bonus_code`,`currency_code`,`product_id`),
  KEY `FKcoyynn3ivcer5qlh1t9x2ybfd` (`product_id`),
  CONSTRAINT `FKcoyynn3ivcer5qlh1t9x2ybfd` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `guid` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_guid` (`guid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `transaction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `amount` decimal(19,2) DEFAULT NULL,
  `cashier_transaction_id` bigint(20) DEFAULT NULL,
  `created_on` datetime NOT NULL,
  `currency_code` varchar(255) DEFAULT NULL,
  `domain_method_id` bigint(20) DEFAULT NULL,
  `domain_method_name` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `domain_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1bkeyn2xhcnk7jnxpj1xvjd5m` (`product_id`),
  KEY `FKsg7jp0aj6qipr50856wf6vbw1` (`user_id`),
  KEY `FKkptx4uhrx2857oy5s5rdhwdoh` (`domain_id`),
  CONSTRAINT `FK1bkeyn2xhcnk7jnxpj1xvjd5m` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `FKkptx4uhrx2857oy5s5rdhwdoh` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`),
  CONSTRAINT `FKsg7jp0aj6qipr50856wf6vbw1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

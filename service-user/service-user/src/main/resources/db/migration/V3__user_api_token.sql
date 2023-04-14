CREATE TABLE `user_api_token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `guid` varchar(255) NOT NULL,
  `token` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_mlqj2tk2e92jx3rea5qdk9sw1` (`guid`),
  UNIQUE KEY `UK_hfi3k9usnhx5qva2a5cyyjue5` (`token`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

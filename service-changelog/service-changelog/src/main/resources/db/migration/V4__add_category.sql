CREATE TABLE `category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `sub_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

INSERT INTO `category` VALUES
(1,'Account'),
(2,'Support'),
(3,'Retention'),
(4,'Bonuses'),
(5,'Sales'),
(6,'Responsible Gaming');

ALTER TABLE `change_log` ADD COLUMN `deleted` BIT(1) DEFAULT 0;

ALTER TABLE `change_log`
  ADD COLUMN `category` bigint(20) DEFAULT NULL,
  ADD KEY `FKcyw608hqwe65cj5oe3vvvtstj` (`category`),
  ADD CONSTRAINT `FKcyw608hqwe65cj5oe3vvvtstj` FOREIGN KEY (`category`) REFERENCES `category` (`id`);

ALTER TABLE `change_log`
  ADD COLUMN `sub_category` bigint(20) DEFAULT NULL,
  ADD KEY `FKcyw608hqwe65cj5o65qwe8stj` (`sub_category`),
  ADD CONSTRAINT `FKcyw608hqwe65cj5o65qwe8stj` FOREIGN KEY (`sub_category`) REFERENCES `sub_category` (`id`);
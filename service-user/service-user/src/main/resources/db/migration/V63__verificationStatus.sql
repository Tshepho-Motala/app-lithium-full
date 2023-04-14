CREATE TABLE `verification_status` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) DEFAULT NULL,
  `level` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

INSERT INTO `verification_status` VALUES
(1,'UNVERIFIED LEVEL',0),
(2,'MANUALLY_VERIFIED',1),
(3,'EXTERNALLY_VERIFIED',1),
(4,'SOF_VERIFIED',2);

ALTER TABLE `user`
  ADD COLUMN `verification_status` bigint(20) DEFAULT NULL,
  ADD KEY `FKcyw608hdrg3dsa5oe3vvvtstj` (`verification_status`),
  ADD CONSTRAINT `FKcyw608hdrg3dsa5oe3vvvtstj` FOREIGN KEY (`verification_status`) REFERENCES `verification_status` (`id`);

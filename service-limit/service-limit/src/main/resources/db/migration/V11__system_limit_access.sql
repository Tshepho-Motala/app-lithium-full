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

CREATE TABLE `limit_system_access` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `domain_name` varchar(35) DEFAULT NULL,
  `verification_status_id` BIGINT(20) NULL,
  `login` bit(1) DEFAULT NULL,
  `deposit` bit(1) DEFAULT NULL,
  `withdraw` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_domain_limit_id` (`domain_name`,`verification_status_id`),
  INDEX `access_key_idx` (`verification_status_id` ASC),
  INDEX `domain_key_idx` (`domain_name` ASC),
  CONSTRAINT `access_key`
    FOREIGN KEY (`verification_status_id`)
        REFERENCES `verification_status` (`id`)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

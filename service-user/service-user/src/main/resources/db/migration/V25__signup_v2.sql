ALTER TABLE `user`
DROP COLUMN `sms_validated`;

ALTER TABLE `user`
ADD COLUMN `cellphone_validated` BIT(1) DEFAULT 0;

ALTER TABLE `user`
ADD COLUMN `welcome_sms_sent` BIT(1) DEFAULT 0;

CREATE TABLE `incomplete_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `affiliate_guid` varchar(255) DEFAULT NULL,
  `affiliate_secondary_guid1` varchar(255) DEFAULT NULL,
  `affiliate_secondary_guid2` varchar(255) DEFAULT NULL,
  `affiliate_secondary_guid3` varchar(255) DEFAULT NULL,
  `cellphone_number` varchar(255) DEFAULT NULL,
  `country_code` varchar(255) DEFAULT NULL,
  `domain_name` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `creation_date` datetime,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

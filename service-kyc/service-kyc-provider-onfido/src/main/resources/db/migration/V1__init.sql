CREATE TABLE `user_applicant` (
  `applicant_id` varchar(255) COLLATE utf8_bin NOT NULL,
  `user_guid` varchar(255) COLLATE utf8_bin NOT NULL,
  `domain_name` varchar(255) NOT NULL,
  PRIMARY KEY (`applicant_id`),
  UNIQUE KEY `idx_user_guid` (`user_guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `onfido_check` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `applicant_id` varchar(255) COLLATE utf8_bin NOT NULL,
    `check_id` varchar(255) NOT NULL,
    `status` varchar(255) NOT NULL,
    `kyc_verification_result_id` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_check_id` (`check_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
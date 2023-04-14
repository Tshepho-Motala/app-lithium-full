CREATE TABLE `document_generation` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `reference` VARCHAR(255) NOT NULL,
    `size` INT,
    `page` INT,
    `provider` VARCHAR(100) NOT NULL,
    `content_type` VARCHAR(255),
    `status` INT(10),
    `user_guid` VARCHAR(255) NOT NULL,
    `created_date`  datetime NOT NULL,
    `completed_date` datetime NULL DEFAULT NULL,
    CONSTRAINT `idx_reference` UNIQUE KEY (`reference`),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
-- Dumping data for table `city`
--

CREATE TABLE `domain` (
                          `id` bigint(20) NOT NULL AUTO_INCREMENT,
                          `name` varchar(255) NOT NULL,
                          `version` int(11) NOT NULL,
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `idx_domain_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

CREATE TABLE `user` (
                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                        `guid` varchar(255) NOT NULL,
                        `version` int(11) NOT NULL,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `idx_user_guid` (`guid`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

CREATE TABLE `email` (
                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                         `user_id` bigint(20) DEFAULT NULL,
                         `created_date` datetime NOT NULL,
                         `sent_date` datetime NULL,
                         `to` varchar(255) NOT NULL,
                         `from` varchar(255) NOT NULL,
                         `subject` varchar(255) NULL,
                         `bcc` varchar(255) NULL,
                         `attachment_name` varchar(255) NULL,
                         `failed` bit NOT NULL DEFAULT 0,
                         `processing` bit NOT NULL DEFAULT 0,
                         `priority` int(11) NOT NULL,
                         `error_count` int(11) NOT NULL,
                         `version` int(11) NOT NULL,
                         `attachment_data` LONGBLOB,
                         PRIMARY KEY (`id`),
                         CONSTRAINT `FK4jg6byweruikth4jksr2dgkjl2xkrj` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
                         index `idx_created_date` (`created_date`),
                         index `idx_sent_date` (`sent_date`),
                         index `idx_failed` (`failed`),
                         UNIQUE KEY `idx_pro_ec_pr` (`processing`, `error_count`, `priority`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
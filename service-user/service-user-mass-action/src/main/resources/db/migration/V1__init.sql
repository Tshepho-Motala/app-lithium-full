-- Database: lithium_user_mass_action
-- ------------------------------------------------------

--
-- Table structure for table `file_upload_meta`
--
#-- DROP TABLE IF EXISTS `file_upload_meta`;
CREATE TABLE `file_upload_meta` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `upload_status` varchar(20) NOT NULL,
    `upload_date` datetime NOT NULL,
    `upload_type` varchar(50) NOT NULL,
    `author_guid` varchar(20) NOT NULL,
    `domain_name` varchar(20) NOT NULL,
    `records_found` INTEGER DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

--
-- Table structure for table `mass_action_meta`
--
# -- DROP TABLE IF EXISTS `mass_action_meta`;
CREATE TABLE `mass_action_meta` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `bonus_code` varchar(255) DEFAULT NULL,
    `default_bonus_amount` double DEFAULT NULL,
    `bonus_description` varchar(200) DEFAULT NULL,
    `allow_duplicates` bit(1) DEFAULT NULL,
    `file_upload_meta_id` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_mam_meta_id` (`file_upload_meta_id`),
    CONSTRAINT `fk_mam_meta_id` FOREIGN KEY (`file_upload_meta_id`) REFERENCES `file_upload_meta` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

--
-- Table structure for table `file_upload_data`
--
-- DROP TABLE IF EXISTS `file_upload_data`;
CREATE TABLE `file_upload_data` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `row_number` bigint(20) NOT NULL,
    `player_id` bigint(20) NOT NULL,
    `player_guid` varchar(255) DEFAULT NULL,
    `amount` double DEFAULT NULL,
    `duplicate` bit(1) DEFAULT NULL,
    `user_status` varchar(20) DEFAULT NULL,
    `user_status_reason` varchar(50) DEFAULT NULL,
    `data_error` varchar(50) DEFAULT NULL,
    `file_upload_meta_id` bigint(20) DEFAULT NULL,
    `upload_status` varchar(20) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_fud_meta_id` (`file_upload_meta_id`),
    CONSTRAINT `fk_fud_meta_id` FOREIGN KEY (`file_upload_meta_id`) REFERENCES `file_upload_meta` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

--
-- Table structure for table `file`
--
-- DROP TABLE IF EXISTS `file`;
CREATE TABLE `file` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `file_name` varchar(100) NOT NULL,
    `file_type` varchar(50) NOT NULL,
    `data` longblob NOT NULL,
    `file_upload_meta_id` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_file_meta_id` (`file_upload_meta_id`),
    CONSTRAINT `fk_file_meta_id` FOREIGN KEY (`file_upload_meta_id`) REFERENCES `file_upload_meta` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

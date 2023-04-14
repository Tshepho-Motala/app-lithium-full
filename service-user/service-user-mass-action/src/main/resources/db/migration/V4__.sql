CREATE TABLE `domain` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_domain_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `domain` (name, version)
SELECT DISTINCT `domain_name`, 0
FROM `file_upload_meta`;

ALTER TABLE `file_upload_meta` ADD COLUMN `domain_id` bigint(20) DEFAULT NULL, ALGORITHM = COPY, LOCK = SHARED;

UPDATE file_upload_meta
    INNER JOIN domain ON file_upload_meta.domain_name = domain.name
SET file_upload_meta.domain_id = domain.id
WHERE file_upload_meta.domain_name = domain.name;

ALTER TABLE file_upload_meta
    DROP COLUMN domain_name;

ALTER TABLE `file_upload_meta`
    MODIFY COLUMN `domain_id` bigint(20) NOT NULL,
    ALGORITHM = COPY, LOCK = SHARED;

ALTER TABLE `file_upload_meta`
    ADD INDEX `FK_domain_id_idx` (`domain_id` ASC),
    ALGORITHM = COPY, LOCK = SHARED;

ALTER TABLE `file_upload_meta`
    ADD CONSTRAINT `FK_domain_id` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`),
    ALGORITHM = COPY, LOCK = SHARED;

CREATE TABLE `action` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `mass_action_meta_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_action_name` (`name`, `mass_action_meta_id`),
  KEY `fk_actions_id` (`mass_action_meta_id`),
  CONSTRAINT `fk_actions_id` FOREIGN KEY (`mass_action_meta_id`) REFERENCES `mass_action_meta` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `mass_action_meta`
    ADD COLUMN `status` VARCHAR(35) DEFAULT NULL,
    ADD COLUMN `status_reason` VARCHAR(255) DEFAULT NULL,
    ADD COLUMN `status_comment` VARCHAR(255) DEFAULT NULL,
    ADD COLUMN `verification_status_id` BIGINT(20) DEFAULT NULL,
    ADD COLUMN `verification_status_comment` VARCHAR(255) DEFAULT NULL,
    ADD COLUMN `test_player` BIT(1) DEFAULT NULL,
    ADD COLUMN `add_tags` VARCHAR(255) DEFAULT NULL,
    ADD COLUMN `replace_tag_from` BIGINT(20) DEFAULT NULL,
    ADD COLUMN `replace_tag_to` BIGINT(20) DEFAULT NULL,
    ADD COLUMN `remove_tags` VARCHAR(255) DEFAULT NULL,
    ADD COLUMN `note_category` VARCHAR(255) DEFAULT NULL,
    ADD COLUMN `note_sub_category` VARCHAR(255) DEFAULT NULL,
    ADD COLUMN `note_priority` INT DEFAULT NULL,
    ADD COLUMN `note_comment` TEXT DEFAULT NULL,
    ADD COLUMN `adjustment_amount_cents` BIGINT(20) DEFAULT NULL,
    ADD COLUMN `adjustment_transaction_type_code` VARCHAR(45) DEFAULT NULL,
    ADD COLUMN `adjustment_comment` VARCHAR(45) DEFAULT NULL,
    ALGORITHM = COPY, LOCK = SHARED;

INSERT INTO action (name, mass_action_meta_id)
SELECT "GRANT_BONUS" as name, m.id
FROM mass_action_meta m
         LEFT OUTER JOIN action a ON m.id = a.mass_action_meta_id
         INNER JOIN file_upload_meta fu ON fu.id = m.file_upload_meta_id
WHERE fu.upload_type not like "PLAYER%";

CREATE TABLE `user` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `guid` varchar(255) NOT NULL,
    `version` int(11) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_user_guid` (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `user` (guid, version)
SELECT DISTINCT `author_guid`, 0
FROM `file_upload_meta`;

ALTER TABLE `file_upload_meta`
    ADD COLUMN `author_id` bigint(20) DEFAULT NULL,
    ALGORITHM = COPY, LOCK = SHARED;

UPDATE file_upload_meta
    INNER JOIN user ON file_upload_meta.author_guid = user.guid
SET file_upload_meta.author_id = user.id
WHERE file_upload_meta.author_guid = user.guid;

ALTER TABLE file_upload_meta
    DROP COLUMN author_guid;

INSERT INTO `user` (guid, version)
SELECT DISTINCT `player_guid`, 0
FROM `file_upload_data`
WHERE `player_guid` is not null;

ALTER TABLE `file_upload_data`
    ADD COLUMN `uploaded_player_id` bigint(20) DEFAULT NULL,
    ALGORITHM = COPY, LOCK = SHARED;

UPDATE file_upload_data f1
    INNER JOIN file_upload_data f2 ON f1.player_id = f2.player_id
SET f1.uploaded_player_id = f2.player_id
WHERE f1.player_id = f2.player_id;

UPDATE file_upload_data f
    INNER JOIN user u ON u.guid = f.player_guid
SET f.player_id = u.id
WHERE f.player_guid = u.guid;

ALTER TABLE `file_upload_data`
    MODIFY COLUMN `player_id` bigint(20) DEFAULT NULL,
    MODIFY COLUMN `user_status` varchar(35) DEFAULT NULL,
    MODIFY COLUMN `user_status_reason` varchar(255) DEFAULT NULL,
    ALGORITHM = COPY, LOCK = SHARED;

UPDATE file_upload_data
SET player_id = null
WHERE player_guid is null;

ALTER TABLE `file_upload_data`
    ADD CONSTRAINT `FK_player_id` FOREIGN KEY (`player_id`) REFERENCES `user` (`id`),
    ALGORITHM = COPY, LOCK = SHARED;

ALTER TABLE `file_upload_meta`
    ADD CONSTRAINT `FK_author_id` FOREIGN KEY (`author_id`) REFERENCES `user` (`id`),
    ALGORITHM = COPY, LOCK = SHARED;

ALTER TABLE file_upload_data
    DROP COLUMN player_guid;

UPDATE file_upload_data fd
SET fd.data_error = 'USER_NOT_FOUND',
    fd.user_status = null,
    fd.user_status_reason = null
WHERE fd.user_status = 'UNKNOWN';

UPDATE file_upload_meta m
SET m.upload_status = 'FAILED_STAGE_1'
WHERE m.upload_status = 'FAILED';

UPDATE file_upload_data d
SET d.upload_status = 'FAILED_STAGE_1'
WHERE d.upload_status = 'FAILED';
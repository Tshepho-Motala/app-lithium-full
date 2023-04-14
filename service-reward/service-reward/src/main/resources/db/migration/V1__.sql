CREATE TABLE `domain` (
      `id` BIGINT NOT NULL AUTO_INCREMENT,
      `name` VARCHAR(255) NOT NULL,
      `version` INT NOT NULL DEFAULT 0,
      PRIMARY KEY (`id`),
      UNIQUE KEY idx_domain_name (`name`)

);

CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `guid` VARCHAR(255) NOT NULL,
    `api_token` VARCHAR(255),
    `original_id` VARCHAR(255),
    `test_account` BIT(1) DEFAULT 0,
    `version` INT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY idx_user_guid (`guid`)
);

use lithium_reward;

CREATE TABLE `reward` (
      `id` BIGINT NOT NULL AUTO_INCREMENT,
      `edit_user_id` BIGINT NOT NULL,
      `edit_id` BIGINT,
      `current_id` BIGINT,
      `domain_id` BIGINT NOT NULL,
      PRIMARY KEY (`id`),
      CONSTRAINT `fk_reward_edit_user_id` FOREIGN KEY (`edit_user_id`) REFERENCES `user`(`id`),
      CONSTRAINT `fk_reward_domain_id` FOREIGN KEY (`domain_id`) REFERENCES `domain`(`id`)
);

CREATE TABLE `reward_revision` (
       `id` BIGINT NOT NULL AUTO_INCREMENT,
       `name` VARCHAR(255) NOT NULL,
       `code` VARCHAR(255) NOT NULL,
       `description` VARCHAR(1024),
       `valid_for` INT NOT NULL,
       `valid_for_granularity` VARCHAR(255) NOT NULL,
        `enabled` BIT(1) DEFAULT 0,
       `reward_id` BIGINT NOT NULL,
       PRIMARY KEY (`id`),
       CONSTRAINT `fk_reward_revision_reward_id` FOREIGN KEY (`reward_id`) REFERENCES `reward`(`id`)
);

CREATE TABLE `reward_type` (
       `id` BIGINT NOT NULL AUTO_INCREMENT,
       `name` VARCHAR(255) NOT NULL,
       `code` VARCHAR(255),
       `url` VARCHAR(255) NOT NULL,
       `display_games` BIT(1) DEFAULT 1,
       PRIMARY KEY (`id`),
       INDEX idx_reward_type_url_name (`url`, `name`)
);

CREATE TABLE `reward_type_field` (
     `id` BIGINT NOT NULL AUTO_INCREMENT,
     `name` VARCHAR(255) NOT NULL,
     `description` VARCHAR(1024),
     `data_type` VARCHAR(200),
     `reward_type_id` BIGINT NOT NULL,
     PRIMARY KEY (`id`),
     CONSTRAINT `fk_reward_type_field_reward_id` FOREIGN KEY (`reward_type_id`) REFERENCES `reward_type`(`id`),
     INDEX idx_reward_type_name (`reward_type_id`, `name`)
);

CREATE TABLE `reward_revision_type` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `reward_type_id` BIGINT NOT NULL,
    `reward_revision_id` BIGINT NOT NULL,
    `instant` BIT(1) DEFAULT 0,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_reward__revision_type_reward_type_id` FOREIGN KEY (`reward_type_id`) REFERENCES `reward_type`(`id`),
    CONSTRAINT `fk_reward_type_reward_revision_id` FOREIGN KEY (`reward_revision_id`) REFERENCES `reward_revision`(`id`),
    INDEX idx_revision_reward_type (`reward_type_id`, `reward_revision_id`)
);

CREATE TABLE `reward_revision_type_value` (
      `id` BIGINT NOT NULL AUTO_INCREMENT,
      `value` VARCHAR(255) NOT NULL,
      `reward_type_field_id` BIGINT NOT NULL,
      `reward_revision_type_id` BIGINT NOT NULL,
      `version` INT NOT NULL DEFAULT 0,
      PRIMARY KEY (`id`),
      CONSTRAINT `fk_reward_revision_type_value_reward_type_field_id` FOREIGN KEY (`reward_type_field_id`) REFERENCES `reward_type_field`(`id`),
      CONSTRAINT `fk_reward_revision_type_value_reward_revision_type_id` FOREIGN KEY (`reward_revision_type_id`) REFERENCES `reward_revision_type`(`id`),
      INDEX idx_revision_type_value (`reward_revision_type_id`, `reward_type_field_id`)
);

CREATE TABLE `reward_revision_type_game` (
         `id` BIGINT NOT NULL AUTO_INCREMENT,
         `guid` VARCHAR(255) NOT NULL,
         `reward_revision_type_id` BIGINT NOT NULL,
         PRIMARY KEY (`id`),
         CONSTRAINT `fk_reward_revision_type_game_reward_revision_type_id` FOREIGN KEY (`reward_revision_type_id`) REFERENCES `reward_revision_type`(`id`)
);



CREATE TABLE `player_reward_history` (
         `id` BIGINT NOT NULL AUTO_INCREMENT,
         `created_date` DATETIME,
         `awarded_date` DATETIME,
         `redeemed_date` DATETIME,
         `expiry_date` DATETIME,
         `status` VARCHAR(50) NOT NULL,
         `version` INT NOT NULL DEFAULT 0,
         `reward_revision_id` BIGINT NOT NULL,
         `reward_source` VARCHAR(100),
         `player_id` BIGINT NOT NULL,
         PRIMARY KEY (`id`),
         CONSTRAINT `fk_player_reward_history_reward_revision_id` FOREIGN KEY (`reward_revision_id`) REFERENCES `reward_revision`(`id`),
         CONSTRAINT `fk_player_reward_history_user_id` FOREIGN KEY (`player_id`) REFERENCES `user`(`id`),
         INDEX idx_player_reward_revision_status (`player_id`, `reward_revision_id`, `status`),
         INDEX idx_player_reward_revision (`player_id`, `reward_revision_id`)
);

CREATE TABLE `player_reward_type_history` (
      `id` BIGINT NOT NULL AUTO_INCREMENT,
      `created_date` DATETIME,
      `awarded_date` DATETIME,
      `type_counter` DECIMAL(19,2),
      `reference_id` VARCHAR(255),
      `status` VARCHAR(50) NOT NULL,
      `version` INT NOT NULL DEFAULT 0,
      `player_reward_history_id` BIGINT NOT NULL,
      `reward_revision_type_id` BIGINT NOT NULL,
      PRIMARY KEY (`id`),
      CONSTRAINT `fk_player_reward_type_history_player_reward_history_id` FOREIGN KEY (`player_reward_history_id`) REFERENCES `player_reward_history`(`id`),
      CONSTRAINT `fk_player_reward_type_history_reward_revision_type_id` FOREIGN KEY (`reward_revision_type_id`) REFERENCES `reward_revision_type`(`id`),
      INDEX idx_history_revision_type (`player_reward_history_id`, `reward_revision_type_id`)
);


CREATE TABLE `player_reward_type_history_value` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `value` VARCHAR(255),
    `reward_type_field_id` BIGINT NOT NULL,
    `player_reward_type_history_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_player_reward_history_type_value_reward_type_field_id` FOREIGN KEY (`reward_type_field_id`) REFERENCES `reward_type_field`(`id`),
    CONSTRAINT  `fk_fk_player_reward_history_type_value` FOREIGN KEY (`player_reward_type_history_id`) REFERENCES `player_reward_type_history`(`id`),
    INDEX `idx_player_reward_history_type_value_field` (`player_reward_type_history_id`, `reward_type_field_id`),
    INDEX `idx_player_reward_type_history_value_history_id` (`player_reward_type_history_id`)
);


ALTER TABLE `reward`
    ADD CONSTRAINT fk_reward_current_id FOREIGN KEY (`current_id`) REFERENCES `reward_revision`(`id`),
    ADD CONSTRAINT fk_reward_edit_id FOREIGN KEY (`edit_id`) REFERENCES `reward_revision`(`id`)


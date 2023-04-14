CREATE TABLE `supplier_game_meta_display`
(
    `id`   BIGINT(20)   NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `supplier_game_meta_vertical`
(
    `id`   BIGINT(20)   NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `supplier_game_meta_hours`
(
    `id`         BIGINT(20) NOT NULL AUTO_INCREMENT,
    `type`       VARCHAR(100),
    `start_time` VARCHAR(100),
    `end_time`   VARCHAR(100),
    PRIMARY KEY (`id`)
);

CREATE TABLE `supplier_game_meta_dealer`
(
    `id`        BIGINT(20) NOT NULL AUTO_INCREMENT,
    `dealer_id` VARCHAR(255),
    `name`      VARCHAR(255),
    PRIMARY KEY (`id`)
);

CREATE TABLE `supplier_game_meta_data`
(
    `id`                 BIGINT(20)   NOT NULL AUTO_INCREMENT,
    `supplier_game_guid` VARCHAR(255),
    `game_vertical_id`   BIGINT(20),
    `game_type`          VARCHAR(255),
    `game_sub_type`      VARCHAR(255),
    `name`               VARCHAR(255) NOT NULL,
    `description_id`     BIGINT(20),
    `display_id`         BIGINT(20),
    `open`               BIT(1),
    `dealer_id`          BIGINT(20),
    `operation_hours_id` BIGINT(20),
    `players`            INT(10),
    `seats`              INT(10),
    `bet_behind`         BIT(1),
    `seats_taken`        VARCHAR(255),
    `dealer_hand`        VARCHAR(255),
    `history`            VARCHAR(255),
    PRIMARY KEY (`id`),
    FOREIGN KEY (`operation_hours_id`) REFERENCES supplier_game_meta_hours (`id`),
    FOREIGN KEY (`game_vertical_id`) REFERENCES supplier_game_meta_vertical (`id`),
    FOREIGN KEY (`display_id`) REFERENCES supplier_game_meta_display (`id`),
    FOREIGN KEY (dealer_id) REFERENCES supplier_game_meta_dealer (`id`)
);

CREATE TABLE `supplier_game_meta_description`
(
    `id`                         bigint(20)   NOT NULL AUTO_INCREMENT,
    `description`                VARCHAR(255) NOT NULL,
    `language`                   VARCHAR(3)   NOT NULL,
    `supplier_game_meta_data_id` BIGINT(20),
    PRIMARY KEY (`id`),
    FOREIGN KEY (`supplier_game_meta_data_id`) REFERENCES `supplier_game_meta_data` (`id`)
);

CREATE TABLE `supplier_game_meta_results`
(
    `id`                         BIGINT(20) NOT NULL AUTO_INCREMENT,
    `payout_level`               VARCHAR(255),
    `shield`                     BIT(1),
    `value`                      VARCHAR(255),
    `multiplier`                 INT(10),
    `location`                   VARCHAR(255),
    `color`                      VARCHAR(255),
    `score`                      VARCHAR(255),
    `ties`                       VARCHAR(255),
    `player_pair`                BIT(1),
    `banker_pair`                BIT(1),
    `natural`                    BIT(1),
    `supplier_game_meta_data_id` BIGINT(20),
    PRIMARY KEY (`id`),
    FOREIGN KEY (`supplier_game_meta_data_id`) REFERENCES `supplier_game_meta_data` (`id`)
);

CREATE TABLE `supplier_game_meta_links`
(
    `id`                         BIGINT(20) NOT NULL AUTO_INCREMENT,
    `type`                       VARCHAR(255),
    `size`                       VARCHAR(100),
    `url`                        VARCHAR(255),
    `supplier_game_meta_data_id` BIGINT(20),
    PRIMARY KEY (`id`),
    FOREIGN KEY (`supplier_game_meta_data_id`) REFERENCES `supplier_game_meta_data` (`id`)
);

CREATE TABLE `supplier_game_meta_bet_limits`
(
    `id`                         BIGINT(20) NOT NULL AUTO_INCREMENT,
    `currency_code`              VARCHAR(3),
    `currency_symbol`            VARCHAR(50) CHARACTER SET utf8,
    `minimum`                    DECIMAL(20, 2),
    `maximum`                    DECIMAL(20, 2),
    `supplier_game_meta_data_id` BIGINT(20),
    PRIMARY KEY (`id`),
    FOREIGN KEY (`supplier_game_meta_data_id`) REFERENCES `supplier_game_meta_data` (`id`)
);

ALTER TABLE `game`
    ADD COLUMN `supplier_game_meta_data_id` BIGINT(20),
    ADD COLUMN `supplier_game_guid`         VARCHAR(255),
    ADD FOREIGN KEY (`supplier_game_meta_data_id`) REFERENCES `supplier_game_meta_data` (`id`);

CREATE INDEX `idx_supplier_game_guid` ON `game` (`supplier_game_guid`) ALGORITHM INPLACE LOCK NONE;

ALTER TABLE game_supplier
    ADD COLUMN players_online INT(10);
CREATE TABLE `label` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL,
    `version` INT NOT NULL,

    PRIMARY KEY (`id`),
    UNIQUE KEY idx_label_name (`name`)
);

CREATE TABLE `label_value` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `value` VARCHAR(255),
    `version` INT NOT NULL,
    `label_id` BIGINT NOT NULL,

    PRIMARY  KEY (`id`),
    CONSTRAINT fk_label_value_label_id FOREIGN KEY (`label_id`) REFERENCES `label`(`id`) ON DELETE CASCADE,
    UNIQUE KEY idx_label_value (`label_id`, `value`)
);

CREATE TABLE `inbox_label_value` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `label_id` BIGINT NOT NULL,
    `inbox_id` BIGINT NOT NULL,
    `label_value_id` BIGINT NULL,
    `version` INT NOT NULL,

    PRIMARY KEY (`id`),
    UNIQUE KEY idx_inbox_label (`inbox_id`, `label_id`),
    CONSTRAINT fk_inbox_label_value_id FOREIGN KEY (`label_value_id`) REFERENCES `label_value`(`id`) ON DELETE CASCADE,
    CONSTRAINT fk_inbox_label_inbox_id FOREIGN KEY (`inbox_id`) REFERENCES `inbox`(`id`) ON DELETE CASCADE,
    CONSTRAINT fk_inbox_label_label_id FOREIGN KEY (`label_id`) REFERENCES `label`(`id`) ON DELETE CASCADE,
    INDEX idx_label_value_id (`label_value_id`),
    INDEX idx_inbox_id (`inbox_id`)
);

CREATE TABLE `notification_type` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`id`),
    `version` INT NOT NULL,
    UNIQUE KEY idx_notification_type_name (`name`)
);

ALTER TABLE `notification` ADD COLUMN `notification_type_id` BIGINT NULL DEFAULT NULL,
    ADD CONSTRAINT `fk_notification_notification_type_id` FOREIGN KEY (`notification_type_id`) REFERENCES `notification_type`(`id`);
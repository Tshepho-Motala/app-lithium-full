ALTER TABLE `user` ADD COLUMN `status_id` BIGINT(20) NOT NULL DEFAULT 0, ALGORITHM=COPY, LOCK=SHARED;

CREATE TABLE `user_categories` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `user_id` bigint(20) NOT NULL,
    `user_category_id` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_user_category_id` (`user_category_id`),
    UNIQUE KEY `uidx_user_id_user_category_id` (`user_id`,`user_category_id`),
    CONSTRAINT  `fk_bl_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

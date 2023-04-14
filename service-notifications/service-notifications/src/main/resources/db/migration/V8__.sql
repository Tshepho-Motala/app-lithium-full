ALTER TABLE `notification` ADD COLUMN `system_notification` BIT(1) DEFAULT 0;

ALTER TABLE `inbox`
    ADD COLUMN `cta` BIT(1) DEFAULT 0,
    ADD INDEX `idx_user_domain_notification_read`  (`user_id`, `domain_id`, `notification_id`, `read`),
    ADD INDEX `idx_user_domain_read`  (`user_id`, `domain_id`, `read`);

CREATE TABLE `inbox_user` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `user_id` bigint(20) NOT NULL,
    `read_count` int(20) NOT NULL,
    `unread_count` int(20) NOT NULL,
    `cta_count` int(20) NOT NULL,
    `version` int(11) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `idx_inbox_user_id` UNIQUE KEY(`user_id`),
    CONSTRAINT `FKa76f3abbjyq5f60n30mprv9da3a5d` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
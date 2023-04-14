ALTER TABLE `inbox` DROP FOREIGN KEY `FK1djr1bcehv00lradf3hcumkng`;
ALTER TABLE `inbox` DROP FOREIGN KEY `FKa76f3abbjyq5f60n30mprv9da`;
ALTER TABLE `inbox`DROP INDEX `idx_notification_user`;
ALTER TABLE `inbox` ADD CONSTRAINT `FK1djr1bcehv00lradf3hcumkng` FOREIGN KEY (`notification_id`) REFERENCES `notification` (`id`);
ALTER TABLE `inbox` ADD CONSTRAINT `FKa76f3abbjyq5f60n30mprv9da` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);
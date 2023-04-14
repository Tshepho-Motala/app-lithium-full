ALTER TABLE `sub_category` ADD COLUMN `category_id` bigint(20);
ALTER TABLE `sub_category` ADD CONSTRAINT `FKlow3uiqba2jflba2jflb0v1ptur` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ;

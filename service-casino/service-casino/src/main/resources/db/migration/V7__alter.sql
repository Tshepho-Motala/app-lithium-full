ALTER TABLE `bonus_revision` DROP FOREIGN KEY `FKccuyj5kt47rrisiwqkeox79ep`;
ALTER TABLE `bonus_revision` CHANGE COLUMN `parent_id` `depends_on_bonus_id` BIGINT(20) NULL DEFAULT NULL ;
ALTER TABLE `bonus_revision` ADD CONSTRAINT `FKccuyj5kt47rrisiwqkeox79ep` FOREIGN KEY (`depends_on_bonus_id`) REFERENCES `bonus_revision` (`id`);

ALTER TABLE `bonus_revision` ADD COLUMN `bonus_id` bigint(20) DEFAULT NULL;
ALTER TABLE `bonus_revision` ADD CONSTRAINT `lkjasdljqoiwe12309asdkl` FOREIGN KEY (`bonus_id`) REFERENCES `bonus` (`id`);
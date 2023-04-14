UPDATE `game` SET `live_casino` = false WHERE `live_casino` is NULL;
ALTER TABLE `game` MODIFY COLUMN `live_casino` BIT(1) NOT NULL DEFAULT FALSE, ALGORITHM INPLACE, LOCK NONE;

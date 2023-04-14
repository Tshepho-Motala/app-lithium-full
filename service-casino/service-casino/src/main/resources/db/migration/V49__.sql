ALTER TABLE `bonus_revision` ADD COLUMN `deleted` BIT(1) DEFAULT 0, ALGORITHM=INPLACE, LOCK=NONE;

UPDATE bonus_revision SET deleted = 0 where deleted is null;


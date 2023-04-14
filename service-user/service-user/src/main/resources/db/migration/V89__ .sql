UPDATE `user`
SET `test_account` = 0
WHERE `test_account` is null;

ALTER TABLE `user` MODIFY COLUMN `test_account` BIT(1) NOT NULL DEFAULT 0,
    ALGORITHM=COPY, LOCK=SHARED;
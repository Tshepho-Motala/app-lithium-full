ALTER TABLE `user` ADD COLUMN `test_account` BIT(1) DEFAULT NULL;

CREATE INDEX idx_u_test_account ON `user`(test_account) ALGORITHM INPLACE LOCK NONE;

UPDATE `user` u
    INNER JOIN `status` s ON u.status_id = s.id
SET `test_account` = 1
WHERE `s`.`name` = 'DISABLED TEST ACCOUNT' OR `s`.`name` = 'ACTIVE TEST ACCOUNT';

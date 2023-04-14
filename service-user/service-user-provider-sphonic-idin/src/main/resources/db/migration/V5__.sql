ALTER TABLE `user`
    ADD COLUMN `cellphone_validated` BIT(1) NOT NULL DEFAULT 0,
    ADD COLUMN `email_validated` BIT(1) NOT NULL DEFAULT 0,
    ADD COLUMN `cellphone_number` varchar(255) DEFAULT NULL,
    ADD COLUMN `email` varchar(255) DEFAULT NULL, ALGORITHM INPLACE, LOCK NONE;

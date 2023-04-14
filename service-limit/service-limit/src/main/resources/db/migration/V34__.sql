ALTER TABLE `domain_restriction_set`
    ADD COLUMN `error_message` VARCHAR(255) NULL DEFAULT NULL,
    ALGORITHM = INPLACE, LOCK = NONE;
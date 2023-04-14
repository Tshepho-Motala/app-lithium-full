ALTER TABLE `domain_restriction_set`
    ADD COLUMN `dwh_visible` BIT(1) NOT NULL DEFAULT 0,
    ALGORITHM = INPLACE, LOCK = NONE;
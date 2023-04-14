ALTER TABLE `auto_restriction_rule_set`
    ADD COLUMN `root_only` BIT(1) NOT NULL DEFAULT 0,
    ADD COLUMN `all_ecosystem` BIT(1) NOT NULL DEFAULT 0,
    ALGORITHM INPLACE, LOCK NONE;
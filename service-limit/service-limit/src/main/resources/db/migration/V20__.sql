ALTER TABLE `domain_restriction_set` ADD COLUMN `system_restriction` BIT(1) NOT NULL DEFAULT 0;

CREATE INDEX `idx_system_restriction` ON `domain_restriction_set` (`system_restriction`) ALGORITHM INPLACE LOCK NONE;
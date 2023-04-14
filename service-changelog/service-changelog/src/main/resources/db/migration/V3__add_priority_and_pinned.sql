ALTER TABLE `change_log` ADD COLUMN `priority` INT  DEFAULT NULL;
ALTER TABLE `change_log` ADD COLUMN `pinned` BIT(1) DEFAULT NULL;

CREATE INDEX idx_changelog_priority ON `change_log`(priority) ALGORITHM INPLACE LOCK NONE;
CREATE INDEX idx_changelog_pinned ON `change_log`(pinned) ALGORITHM INPLACE LOCK NONE;

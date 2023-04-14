ALTER TABLE `change_set`
    ADD COLUMN `checksum` VARCHAR(255) DEFAULT NULL,
    ADD COLUMN `last_updated` DATETIME DEFAULT NULL;

CREATE UNIQUE INDEX `idx_changeset` ON `change_set` (`name`, `language_id`, `change_reference`) ALGORITHM INPLACE LOCK NONE;
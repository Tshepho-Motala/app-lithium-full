ALTER TABLE `promotion`
    ADD INDEX `idx_enabled_deleted` (`deleted`, `enabled`);
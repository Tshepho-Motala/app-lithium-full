UPDATE limit_system_access SET casino = 0 WHERE casino IS null;

ALTER TABLE limit_system_access MODIFY COLUMN casino BIT(1) NOT NULL DEFAULT 1;

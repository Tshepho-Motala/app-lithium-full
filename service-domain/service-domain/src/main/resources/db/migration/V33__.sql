-- This is required for adding soft delete functionality to templates
ALTER TABLE lithium_domain.`template` ADD COLUMN `deleted` bit(1) NOT NULL DEFAULT 0 AFTER `id`;

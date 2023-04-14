ALTER TABLE `machine_settlement_processing_boundary`
	DROP COLUMN `last_location_entity_id_processed`,
	DROP COLUMN `last_relationship_entity_id_processed`,
	ADD COLUMN `last_location_dist_config_rev_id_processed` BIGINT(20) DEFAULT NULL,
	ADD COLUMN `last_relationship_dist_config_rev_id_processed` BIGINT(20) DEFAULT NULL;

ALTER TABLE `machine_settlement`
	ADD COLUMN `rerun` BIT(1) DEFAULT 0,
	ADD COLUMN `last_failed_reason` LONGTEXT DEFAULT NULL,
	ADD COLUMN `last_failed_date` DATETIME DEFAULT NULL,
	ADD COLUMN `batch_name` VARCHAR(255) NOT NULL,
	ADD UNIQUE INDEX `idx_batch_name` (`batch_name`);
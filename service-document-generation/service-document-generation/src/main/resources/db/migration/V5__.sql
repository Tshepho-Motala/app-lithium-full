CREATE INDEX `idx_csv_doc_generation_created_time` ON `document_generation` (`created_date`) ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_csv_doc_generation_status` ON `document_generation` (`status`) ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_csv_doc_generation_provider` ON `document_generation` (`provider`) ALGORITHM INPLACE LOCK NONE;
DROP TABLE IF EXISTS `email`;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `domain`;
ALTER TABLE `document_generation` DROP COLUMN `page`;
ALTER TABLE `document_generation` DROP COLUMN `reference`;

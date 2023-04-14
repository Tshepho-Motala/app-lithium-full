ALTER TABLE `report_run_results`
ADD COLUMN `affiliate_guid_id` BIGINT(20) NULL,
ADD COLUMN `banner_guid_id` BIGINT(20) NULL,
ADD COLUMN `campaign_guid_id` BIGINT(20) NULL;
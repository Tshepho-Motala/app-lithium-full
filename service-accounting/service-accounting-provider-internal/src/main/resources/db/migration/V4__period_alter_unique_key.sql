ALTER TABLE `period` DROP INDEX `idx_pd_dates`;
ALTER TABLE `period` ADD CONSTRAINT UNIQUE `idx_pd_dates` (`date_start`,`date_end`,`domain_id`);
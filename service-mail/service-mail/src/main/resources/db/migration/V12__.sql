ALTER TABLE `email`
DROP INDEX `idx_email_created_date_priority`,
DROP INDEX `idx_email_sent_date_priority`,
DROP INDEX `idx_domain`;

CREATE INDEX `idx_f_pro_sd_ec_p_cd` ON `email` (`failed`, `processing`, `sent_date`, `error_count`, `priority`, `created_date`) ALGORITHM INPLACE LOCK NONE;

CREATE INDEX `idx_domain_deleted_priority` ON `domain_provider` (`domain_id`, `deleted`, `priority`) ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_domain_deleted_enabled_priority` ON `domain_provider` (`domain_id`, `deleted`, `enabled`, `priority`) ALGORITHM INPLACE LOCK NONE;
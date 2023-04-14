CREATE INDEX `idx_created_date` ON `email`(`created_date`) ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_sent_date` ON `email`(`sent_date`) ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_domain` ON `email`(`domain_id`) ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_failed` ON `email`(`failed`) ALGORITHM INPLACE LOCK NONE;
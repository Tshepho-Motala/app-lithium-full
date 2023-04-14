CREATE INDEX `idx_date` ON `signup_event` (`date`) ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_ip_address` ON `signup_event` (`ip_address`) ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_successful` ON `signup_event` (`successful`) ALGORITHM INPLACE LOCK NONE;
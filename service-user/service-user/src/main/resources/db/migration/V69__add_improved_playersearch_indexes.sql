CREATE INDEX `idx_u_verification_status` ON `user` (`verification_status`) ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_u_status_id` ON `user` (`status_id`) ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_u_dob_day` ON `user` (`dob_day`) ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_u_dob_month` ON `user` (`dob_month`) ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_u_dob_year` ON `user` (`dob_year`) ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_loginevent_provider_auth_client` ON `login_event` (`provider_auth_client`) ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_loginevent_date` ON `login_event` (`date`) ALGORITHM INPLACE LOCK NONE;

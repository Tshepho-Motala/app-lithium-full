CREATE INDEX `idx_loginevent_successful_domain_logout_last_activity`
ON `login_event`(`successful`, `domain_id`, `logout`, `last_activity`) ALGORITHM INPLACE LOCK NONE;

CREATE INDEX `idx_loginevent_successful_logout_last_activity`
ON `login_event`(`successful`, `logout`, `last_activity`) ALGORITHM INPLACE LOCK NONE;

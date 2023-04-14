ALTER TABLE `period`
  ADD UNIQUE KEY `idx_pd_all` (`year`,`month`,`week`,`day`,`domain_id`),
  ADD UNIQUE KEY `idx_pd_dates` (`date_start`,`date_end`,`domain_id`),
  ADD KEY `idx_pd_datestart` (`date_start`),
  ADD KEY `idx_pd_dateend` (`date_end`),
  ADD KEY `idx_pd_granularity` (`granularity_id`),
  ADD KEY `FKk1oj7pptmme05t9qyaay1d178` (`domain_id`);

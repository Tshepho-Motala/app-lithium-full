RENAME TABLE `stat_entry_label_value` to `stat_label_value`;

ALTER TABLE `stat_label_value` ADD UNIQUE KEY `idx_stat_label_value` (`stat_id`, `label_value_id`);

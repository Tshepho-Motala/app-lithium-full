CREATE INDEX `idx_label_value_and_label` ON `label_value` (`label_id`, `value` (256)) ALGORITHM INPLACE LOCK NONE;


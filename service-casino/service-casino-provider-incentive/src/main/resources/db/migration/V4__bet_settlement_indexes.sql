CREATE INDEX `idx_tran_timestamp` ON `bet` (`transaction_timestamp`) ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_lithium_accounting_id` ON `bet` (`lithium_accounting_id`) ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_max_potential_win` ON `bet` (`max_potential_win`) ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_total_odds` ON `bet` (`total_odds`) ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_total_stake` ON `bet` (`total_stake`) ALGORITHM INPLACE LOCK NONE;

CREATE INDEX `idx_tran_timestamp` ON `settlement` (`transaction_timestamp`) ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_lithium_accounting_id` ON `settlement` (`lithium_accounting_id`) ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_returns` ON `settlement` (`returns`) ALGORITHM INPLACE LOCK NONE;

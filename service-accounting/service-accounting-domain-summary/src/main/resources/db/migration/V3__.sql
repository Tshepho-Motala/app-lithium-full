ALTER TABLE `summary_domain`
    ADD COLUMN `opening_balance_cents` bigint(20) NOT NULL DEFAULT 0 AFTER `id`;
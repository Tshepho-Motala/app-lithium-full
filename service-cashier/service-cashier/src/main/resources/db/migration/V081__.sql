ALTER TABLE `transaction`
    ADD COLUMN `manual_cashier_adjustment_id` bigint(20) DEFAULT NULL, ALGORITHM=INPLACE, LOCK=NONE;


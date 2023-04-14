ALTER TABLE `transaction_type_label`
    ADD COLUMN `summarize_total` BIT(1) DEFAULT NULL,
    ADD COLUMN `synchronous` BIT(1) DEFAULT NULL;

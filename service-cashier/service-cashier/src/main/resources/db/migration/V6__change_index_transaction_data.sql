ALTER TABLE `transaction_data` 
DROP INDEX `idx_tran_field` ,
ADD UNIQUE INDEX `idx_tran_field_stage_output` (`transaction_id` ASC, `stage` ASC, `field` ASC, `output` ASC);

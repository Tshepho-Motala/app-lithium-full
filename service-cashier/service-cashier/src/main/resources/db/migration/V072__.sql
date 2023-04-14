ALTER TABLE `transaction` ADD COLUMN `status_id` BIGINT(20) DEFAULT NULL;
CREATE INDEX `idx_tr_status_id` ON `transaction` (`status_id`) ALGORITHM INPLACE LOCK NONE;

UPDATE transaction AS tr
    INNER JOIN transaction_workflow_history twh ON tr.current_id = twh.id
    SET tr.status_id = twh.status_id
WHERE tr.status_id IS NULL;
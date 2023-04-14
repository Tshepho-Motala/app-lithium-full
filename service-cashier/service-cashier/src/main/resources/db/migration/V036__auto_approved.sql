ALTER TABLE `transaction` ADD COLUMN `auto_approved` BIT(1) NOT NULL DEFAULT 0;

CREATE INDEX `idx_auto_approved` ON `transaction` (`auto_approved`) ALGORITHM INPLACE LOCK NONE;

UPDATE `transaction`
SET `auto_approved` = true
WHERE `id` IN (
    SELECT DISTINCT `transaction_id`
    FROM `transaction_workflow_history`
    WHERE `status_id` = (
        SELECT `id`
        FROM `transaction_status`
        WHERE `code` = 'AUTO_APPROVED'
    )
)
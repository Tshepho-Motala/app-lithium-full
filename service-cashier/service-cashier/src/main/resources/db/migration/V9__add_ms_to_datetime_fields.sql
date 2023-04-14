ALTER TABLE lithium_cashier.`transaction` MODIFY COLUMN created_on datetime(3) NOT NULL;
ALTER TABLE lithium_cashier.transaction_processing_attempt MODIFY COLUMN `timestamp` datetime(3) NOT NULL;
ALTER TABLE lithium_cashier.transaction_workflow_history MODIFY COLUMN `timestamp` datetime(3) NOT NULL ;

ALTER TABLE `transaction_type_label` 
ADD COLUMN `optional` BIT(1) NOT NULL DEFAULT 0 AFTER `transaction_type_id`,
ALGORITHM INPLACE,
LOCK NONE;

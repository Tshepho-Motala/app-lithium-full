ALTER TABLE `transaction`
ADD COLUMN `acc_ref_to_withdrawal_pending` bigint(20) DEFAULT NULL,
ADD COLUMN `acc_ref_from_withdrawal_pending` bigint(20) DEFAULT NULL;
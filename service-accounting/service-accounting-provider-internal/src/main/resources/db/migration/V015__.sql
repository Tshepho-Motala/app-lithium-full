ALTER TABLE `transaction_type_account` ADD COLUMN divider_to_cents INT (11) NOT NULL DEFAULT 1;
ALTER TABLE `account_type` ADD COLUMN divider_to_cents INT (11) NOT NULL DEFAULT 1;

UPDATE `lithium_accounting_internal`.`account_type` SET `divider_to_cents`='10000' WHERE `code` = 'PLAYER_JACKPOT_ACCRUALS';
UPDATE `lithium_accounting_internal`.`account_type` SET `divider_to_cents`='10000' WHERE `code` = 'JACKPOT_ACCRUALS';

UPDATE `lithium_accounting_internal`.`transaction_type_account` SET `divider_to_cents`='10000' WHERE `account_type_code` = 'PLAYER_JACKPOT_ACCRUALS';
UPDATE `lithium_accounting_internal`.`transaction_type_account` SET `divider_to_cents`='10000' WHERE `account_type_code` = 'JACKPOT_ACCRUALS';

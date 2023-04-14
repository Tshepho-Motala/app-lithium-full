UPDATE `lithium_accounting_internal`.`account_type` SET `divider_to_cents`='1000000' WHERE `code` = 'PLAYER_JACKPOT_ACCRUALS';
UPDATE `lithium_accounting_internal`.`account_type` SET `divider_to_cents`='1000000' WHERE `code` = 'JACKPOT_ACCRUALS';

UPDATE `lithium_accounting_internal`.`transaction_type_account` SET `divider_to_cents`='1000000' WHERE `account_type_code` = 'PLAYER_JACKPOT_ACCRUALS';
UPDATE `lithium_accounting_internal`.`transaction_type_account` SET `divider_to_cents`='1000000' WHERE `account_type_code` = 'JACKPOT_ACCRUALS';

UPDATE `inpay_reason`
SET `code` = 'Rejected - Invalid creditor account', `message` = 'invalid_creditor_account'
WHERE id = 1;
UPDATE `inpay_reason`
SET `code` = 'Returned - Returned payment as per beneficiary request', `message` = 'beneficiary_request_returned_payment_as_per_beneficiary_request'
WHERE id = 2;
UPDATE `inpay_reason`
SET `code` = 'Payment will be executed on YYYY-MM-DD', `message` = 'waiting_future_execution'
WHERE id = 3;
INSERT INTO `inpay_reason` (`id`, `version`, `kind`, `category`, `code`, `message`)
VALUES (4, 0, 'payment_request_rejected', 'schema_validation_error','birthdate:[can''t be blank]', 'invalid_ultimate_debtor');
# Remove redundant payment method status -> LEGACY

## Discover status_id of LEGACY in `processor_user_card`
SET @legacyStatusVarId = (
    SELECT lithium_cashier.payment_method_status.id
    FROM lithium_cashier.payment_method_status
    WHERE lithium_cashier.payment_method_status.name = 'LEGACY');

## Discover status_id of HISTORIC in `processor_user_card`
SET @historicStatusVarId = (
    SELECT lithium_cashier.payment_method_status.id
    FROM lithium_cashier.payment_method_status
    WHERE lithium_cashier.payment_method_status.name = 'HISTORIC');

## Change status_id from LEGACY to HISTORIC in all occurrences of `processor_user_card` (payment method)
UPDATE lithium_cashier.processor_user_card
SET processor_user_card.status_id = @historicStatusVarId
WHERE lithium_cashier.processor_user_card.status_id = @legacyStatusVarId;

## Remove "LEGACY" from `payment_method_status` table
DELETE FROM lithium_cashier.payment_method_status
WHERE lithium_cashier.payment_method_status.id = @legacyStatusVarId;

# Add new processor_account_type (payment method type) -> "HISTORIC"
INSERT IGNORE INTO lithium_cashier.processor_account_type (name, version) VALUES ('HISTORIC', 0);

# Update historic transactions with new payment method type -> HISTORIC

## Discover type_id of HISTORIC in `processor_user_card`
SET @historicTypeVarId = (
    SELECT lithium_cashier.processor_account_type.id
    FROM lithium_cashier.processor_account_type
    WHERE lithium_cashier.processor_account_type.name = 'HISTORIC');

## Populate <null> type_ids as HISTORIC in all occurrences
## where status_id = HISTORIC in `processor_user_card` (payment method)
UPDATE lithium_cashier.processor_user_card
SET processor_user_card.type_id = @historicTypeVarId
WHERE lithium_cashier.processor_user_card.status_id = @historicStatusVarId
  AND lithium_cashier.processor_user_card.type_id IS NULL;
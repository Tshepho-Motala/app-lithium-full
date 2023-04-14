UPDATE `translation_value_v2`
SET `value` = REPLACE(`value`, 'altMessageCount', 'Intervention Comps Block')
WHERE value LIKE 'altMessageCount %';
UPDATE domain_method dm
INNER JOIN method m ON dm.method_id = m.id
SET dm.image_id = NULL
WHERE m.code = 'checkout-cc';
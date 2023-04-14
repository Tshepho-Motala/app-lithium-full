UPDATE change_log cl
    INNER JOIN sub_category sc on cl.sub_category = sc.id
SET cl.sub_category = null
WHERE sc.name = 'More Sub-Categories Coming Real Soon!';
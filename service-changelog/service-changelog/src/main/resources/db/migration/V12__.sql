UPDATE change_log cl
    INNER JOIN sub_category sc on cl.sub_category = sc.id
SET cl.sub_category = null
WHERE sc.name = 'UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.SUB_CATEGORY.COMING_SOON';

DELETE FROM `sub_category` WHERE (`name` = 'UI_NETWORK_ADMIN.CHANGELOGS.GLOBAL.FIELDS.SUB_CATEGORY.COMING_SOON');
DELETE FROM `sub_category` WHERE (`name` = 'More Sub-Categories Coming Real Soon!');
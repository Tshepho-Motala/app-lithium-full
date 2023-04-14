UPDATE `notification`
SET `notification_type_id` = (SELECT id FROM `notification_type` WHERE `name`='default' LIMIT 1)
WHERE ISNULL(`notification_type_id`);
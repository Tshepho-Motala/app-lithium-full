-- This will depend on if SVC sms is started up first.
INSERT IGNORE INTO `category` (`description`, `name`) VALUES
('These are all the roles relevant to managing the sms queue.', 'SMS Queue Operations');

UPDATE `role`
SET `category_id` = (SELECT `id` FROM `category` WHERE `name` = 'SMS Queue Operations')
WHERE `role`.`role` = 'SMS_QUEUE_VIEW';

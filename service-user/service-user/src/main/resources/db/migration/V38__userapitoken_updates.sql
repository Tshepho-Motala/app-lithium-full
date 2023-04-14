ALTER TABLE `user_api_token` ADD COLUMN `user_id` bigint(20);

UPDATE `user_api_token` uat
INNER JOIN `domain` d ON d.name = SUBSTR(uat.guid, 1, LOCATE('/', uat.guid) - 1)
INNER JOIN `user` u ON u.domain_id = d.id AND u.username = SUBSTR(uat.guid, LOCATE('/', uat.guid) + 1, LENGTH(uat.guid))
SET `user_id` = u.id;

ALTER TABLE `user_api_token` MODIFY `user_id` bigint(20) NOT NULL;

ALTER TABLE `user` ADD COLUMN `user_api_token_id` bigint(20);

UPDATE `user` u
INNER JOIN `user_api_token` uat ON uat.user_id = u.id
SET `user_api_token_id` = uat.id;
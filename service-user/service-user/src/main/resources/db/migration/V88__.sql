ALTER TABLE `user` DROP COLUMN `country_code_of_birth`, ALGORITHM = COPY, LOCK = SHARED;
ALTER TABLE `incomplete_user` DROP COLUMN `country_code_of_birth`, ALGORITHM = COPY, LOCK = SHARED;
ALTER TABLE `user` ADD COLUMN `place_of_birth` VARCHAR(255), ALGORITHM = COPY, LOCK = SHARED;

#We checked on all environments and there is only one group that makes use of this role
DELETE FROM `grd` WHERE `role_id` = (SELECT id FROM `role`
                                               WHERE (`role` = 'PLAYER_COUNTRY_OF_BIRTH_EDIT'));

DELETE FROM `role` WHERE (`role` = 'PLAYER_COUNTRY_OF_BIRTH_EDIT');

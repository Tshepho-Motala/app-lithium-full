ALTER TABLE `login_event`
ADD COLUMN `logout` DATETIME(3) NULL DEFAULT NULL AFTER `provider_auth_client`,
ADD COLUMN `duration` BIGINT(20) NULL DEFAULT NULL AFTER `logout`;
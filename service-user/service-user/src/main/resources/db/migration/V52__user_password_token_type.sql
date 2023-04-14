ALTER TABLE `user_password_token`
ADD COLUMN `type` VARCHAR(10) DEFAULT NULL;

-- Removing unique constraint
SET FOREIGN_KEY_CHECKS = 0;
ALTER TABLE `user_password_token`
DROP INDEX `UK_nm566khtoltheyq9vxr6j2ynt`,
ADD CONSTRAINT `fk_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);
SET FOREIGN_KEY_CHECKS = 1;

CREATE UNIQUE INDEX `idx_u_type` ON `user_password_token` (`user_id`, `type`) ALGORITHM INPLACE LOCK NONE;

CREATE UNIQUE INDEX `idx_u_token` ON `user_password_token` (`user_id`, `token`) ALGORITHM INPLACE LOCK NONE;
ALTER TABLE `email_validation_token`
DROP FOREIGN KEY `FKpm63lw71nj0cigl24mog0bayw`,
DROP INDEX `UK_9286wnnpruoc6ynhnjmn8i5dx`,
  ADD COLUMN `email`varchar(255) NOT NULL;
ALTER TABLE `email_validation_token` ADD FOREIGN KEY `FKpm63lw71nj0cigl24mog0bayw` (`user_id`) REFERENCES `user` (`id`);
CREATE INDEX `idx_t_user_email_token` ON `email_validation_token` (`user_id`, `email`, `token`) ALGORITHM INPLACE LOCK NONE;

update email_validation_token as `token`, (select u.id, u.email from user u) as `src`
SET `token`.email = `src`.email
where `token`.email = '' and `token`.user_id = `src`.id and `src`.email is not null;

DELETE token FROM email_validation_token token JOIN user src ON token.user_id = src.id
WHERE src.email is null

ALTER TABLE `processor_user_card`  DROP INDEX `reference_idx`;
ALTER TABLE `processor_user_card` ADD CONSTRAINT `user_reference_idx` UNIQUE (`user_id`, `reference`);


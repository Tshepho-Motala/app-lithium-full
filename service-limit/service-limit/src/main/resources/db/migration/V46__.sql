ALTER TABLE `user` ADD COLUMN `loss_limits_visibility` INT(1) DEFAULT 0;
ALTER TABLE `user` ADD INDEX `idx_loss_limits_visibility` (`loss_limits_visibility` ASC);

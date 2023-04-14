CREATE INDEX `idx_u_username` ON `user` (`username`) ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_u_first_name` ON `user` (`first_name`) ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_u_last_name` ON `user` (`last_name`) ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_u_email` ON `user` (`email`) ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_u_cellphone_number` ON `user` (`cellphone_number`) ALGORITHM INPLACE LOCK NONE;
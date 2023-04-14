CREATE INDEX `idx_loginevent_user_id_ip_address_date` ON `login_event` (`user_id`,`date`,`ip_address`) ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_u_username_first_name_last_name` ON `user` (`username`,`first_name`,`last_name`) ALGORITHM INPLACE LOCK NONE;

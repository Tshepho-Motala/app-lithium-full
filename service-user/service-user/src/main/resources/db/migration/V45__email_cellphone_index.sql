CREATE INDEX `idx_u_d_email` ON `user` (`domain_id`, `email`) ALGORITHM INPLACE LOCK NONE;
CREATE INDEX `idx_u_d_cellphone` ON `user` (`domain_id`, `cellphone_number`) ALGORITHM INPLACE LOCK NONE;
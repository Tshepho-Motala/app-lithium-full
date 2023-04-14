ALTER TABLE `user`
  CHANGE COLUMN `email_opt_out` `email_opt_out` BIT(1) NOT NULL DEFAULT b'0' ,
  CHANGE COLUMN `sms_opt_out` `sms_opt_out` BIT(1) NOT NULL DEFAULT b'0' ,
  CHANGE COLUMN `call_opt_out` `call_opt_out` BIT(1) NOT NULL DEFAULT b'0' ,
  CHANGE COLUMN `push_opt_out` `push_opt_out` BIT(1) NOT NULL DEFAULT b'0' ,
  CHANGE COLUMN `leaderboard_opt_out` `leaderboard_opt_out` BIT(1) NOT NULL DEFAULT b'0' ,
  CHANGE COLUMN `post_opt_out` `post_opt_out` BIT(1) NOT NULL DEFAULT b'0' ,
  CHANGE COLUMN `promotions_opt_out` `promotions_opt_out` BIT(1) NOT NULL DEFAULT b'0';

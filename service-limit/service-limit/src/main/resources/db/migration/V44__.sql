ALTER TABLE `auto_restriction_rule_set` ADD COLUMN `skip_test_user` BIT(1) DEFAULT 0, ALGORITHM INPLACE, LOCK NONE;
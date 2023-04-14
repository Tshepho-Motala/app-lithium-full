ALTER TABLE user_restriction_set ADD COLUMN created_on DATETIME DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE user_restriction_set ADD COLUMN active_from DATETIME DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE user_restriction_set ADD COLUMN active_to DATETIME;

ALTER TABLE auto_restriction_rule ADD COLUMN delay BIGINT(20) DEFAULT NULL;
ALTER TABLE auto_restriction_rule ADD COLUMN event INT DEFAULT NULL;
ALTER TABLE auto_restriction_rule MODIFY `value` longtext;


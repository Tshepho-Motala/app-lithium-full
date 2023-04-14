SET FOREIGN_KEY_CHECKS=0;
ALTER TABLE `change_log_field_change`
    ADD COLUMN `edited_by` bigint (20) DEFAULT NULL,
    ADD COLUMN `date_updated` datetime DEFAULT NULL,
    ADD INDEX (`edited_by`),
    ADD FOREIGN KEY (edited_by) REFERENCES `user`(id),
    ALGORITHM INPLACE , LOCK NONE;
SET FOREIGN_KEY_CHECKS=1;
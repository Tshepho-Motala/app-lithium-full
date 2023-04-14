ALTER TABLE `label_value`
    DROP FOREIGN KEY `FKre71r2qpe0al31ks5ys0mf3fj`;

ALTER TABLE `label_value`
    DROP INDEX `idx_label_value` ;

ALTER TABLE `label_value`
    CHANGE COLUMN `value` `value` VARCHAR(4096) NULL DEFAULT NULL ,
    ALGORITHM=COPY, LOCK=SHARED;

ALTER TABLE `label_value`
    ADD CONSTRAINT `FKre71r2qpe0al31ks5ys0mf3fj`
        FOREIGN KEY (`label_id`)
            REFERENCES `lithium_user`.`label` (`id`);

ALTER TABLE `label_value`
    ADD COLUMN `sha1` VARCHAR(40) NOT NULL, ALGORITHM=COPY, LOCK=SHARED;

UPDATE `label_value`
    INNER JOIN (SELECT SHA1(`value`) as `sha1_value`, `id` FROM `label_value`) `lv2`
SET `label_value`.`sha1` = `lv2`.`sha1_value`
WHERE `label_value`.`id` = `lv2`.`id`;

ALTER TABLE `label_value`
    ADD UNIQUE INDEX `idx_label_sha1` (`label_id` ASC, `sha1`(40) ASC);



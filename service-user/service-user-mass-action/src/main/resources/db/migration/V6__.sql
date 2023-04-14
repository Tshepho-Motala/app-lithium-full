ALTER TABLE `mass_action_meta`
    ADD COLUMN `access_rule` VARCHAR(255) DEFAULT NULL,
    ALGORITHM = COPY,
    LOCK = SHARED;

ALTER TABLE `file_upload_data`
    ADD COLUMN `rule_set_result_success` BIT(1) DEFAULT NULL,
    ADD COLUMN `rule_set_result_message` VARCHAR(255) DEFAULT NULL,

    ALGORITHM = COPY,
    LOCK = SHARED;
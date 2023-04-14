ALTER TABLE `mass_action_meta`
    ADD COLUMN `biometrics_status` VARCHAR(255) DEFAULT NULL,
    ADD COLUMN `biometrics_status_comment` VARCHAR(255) DEFAULT NULL,
    ALGORITHM = COPY, LOCK = SHARED;



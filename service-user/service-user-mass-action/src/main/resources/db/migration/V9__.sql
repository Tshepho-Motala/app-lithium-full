ALTER TABLE `mass_action_meta`
    ADD COLUMN `age_verified` BIT(1) DEFAULT NULL,
    ADD COLUMN `address_verified` BIT(1) DEFAULT NULL,
    ALGORITHM = COPY, LOCK = SHARED;



CREATE TABLE `category` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `name` varchar(200) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_category_name` (`name`)
);

ALTER TABLE `rule`
    ADD COLUMN `category_id` BIGINT(20) DEFAULT NULL,
    ADD CONSTRAINT `fk_rule_category_id` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`);

ALTER TABLE `promo_provider`
    DROP COLUMN `category`,
    ADD COLUMN `category_id`BIGINT(20) DEFAULT NULL,
    ADD CONSTRAINT `idx_promo_provider_url_and_category_id` UNIQUE(`url`, `category_id`),
    ADD CONSTRAINT `fk_promo_provider_category_id` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`);

ALTER TABLE `promotion_revision`
    ADD COLUMN `requires_all_challenge_groups` BIT(1) DEFAULT 0;

ALTER TABLE `challenge_group`
    ADD COLUMN `requires_all_challenges` BIT(1) DEFAULT 1;

ALTER TABLE `challenge`
    ADD COLUMN `requires_all_rules` BIT(1) DEFAULT 1;

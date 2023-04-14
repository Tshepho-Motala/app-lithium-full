SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE `promo_revision_exclusive_players`;

CREATE TABLE `promo_revision_exclusive_players` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `promotion_revision_id` bigint(20) NOT NULL,
    `player_id` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_ev1tgbjoobw1n4gvx4w821byx` (`player_id`, `promotion_revision_id`),
    CONSTRAINT `FKctcfslfroi3gsbsl70hr3uk9e` FOREIGN KEY (`player_id`) REFERENCES `user` (`id`),
    CONSTRAINT `FKcxbf0ukwrmduolu0q2tfcqlwp` FOREIGN KEY (`promotion_revision_id`) REFERENCES `promotion_revision` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE `reward`;

CREATE TABLE `reward` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `version` int(11) NOT NULL,
    `challenge_id` bigint(20) DEFAULT NULL,
    `promotion_revision_id` bigint(20) DEFAULT NULL,
    `reward_id` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FK8m01x5eev9h271yjp4q58sh2h` (`challenge_id`),
    KEY `FK12id7q7b5psomklcj9p1kv0jo` (`promotion_revision_id`),
    CONSTRAINT `FK12id7q7b5psomklcj9p1kv0jo` FOREIGN KEY (`promotion_revision_id`) REFERENCES `promotion_revision` (`id`),
    CONSTRAINT `FK8m01x5eev9h271yjp4q58sh2h` FOREIGN KEY (`challenge_id`) REFERENCES `challenge` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;

ALTER TABLE promotion
    ADD COLUMN `enabled` BIT(1) DEFAULT 1;

ALTER TABLE promotion_stat
    ADD COLUMN `activity` VARCHAR(100),
    ADD COLUMN `category` VARCHAR(100);

UPDATE promotion_stat SET `activity`= `action`, `category`=`type`;

ALTER TABLE promotion_stat
    DROP COLUMN `type`,
    DROP COLUMN  `action`;
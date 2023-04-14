-- lithium_access.`domain` definition

CREATE TABLE `domain` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `version` int(11) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_domain_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

-- lithium_translate.translation_key_v2 definition

CREATE TABLE `translation_key_v2` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `code` varchar(255) NOT NULL,
    `description` varchar(255) DEFAULT NULL,
    `version` int(11) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_trans_key_v2` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

-- lithium_translate.translation_value_v2 definition

CREATE TABLE `translation_value_v2` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `version` int(11) NOT NULL,
    `value` longtext NOT NULL,
    `description` varchar(255) DEFAULT NULL,
    `key_id` bigint(20) DEFAULT NULL,
    `language_id` bigint(20) DEFAULT NULL,
    `domain_id` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_trans_value_v2` (`key_id`,`language_id`,`domain_id`),
    KEY `FK2lihgiogyoiuhwerouwerhj2e` (`key_id`),
    KEY `FKaohjwefouhwerohiwerpiqwue` (`language_id`),
    KEY `FKolnfopiqwefohwerjaslfkiwe` (`domain_id`),
    CONSTRAINT `FK2lihgiogyoiuhwerouwerhj2e` FOREIGN KEY (`key_id`) REFERENCES `translation_key_v2` (`id`),
    CONSTRAINT `FKaohjwefouhwerohiwerpiqwue` FOREIGN KEY (`language_id`) REFERENCES `language` (`id`),
    CONSTRAINT `FKolnfopiqwefohwerjaslfkiwe` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

ALTER TABLE `translation_value`
ADD COLUMN `migrated` BIT(1) NOT NULL DEFAULT 0;

-- lithium_translate.change_set definition

CREATE TABLE `change_set` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `version` int(11) NOT NULL,
    `apply_date` datetime NOT NULL,
    `name` varchar(255) DEFAULT NULL,
    `change_reference` varchar(255) NOT NULL,
    `language_id` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_change_set` (`name`,`change_reference`,`language_id`),
    KEY `FKjhsdfouhweoruhqewroihwere` (`language_id`),
    CONSTRAINT `FKjhsdfouhweoruhqewroihwere` FOREIGN KEY (`language_id`) REFERENCES `language` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;
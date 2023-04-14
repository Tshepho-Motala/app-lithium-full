-- lithium_mail.provider_type definition

CREATE TABLE `provider_type`
(
    `id`      bigint(20) NOT NULL AUTO_INCREMENT,
    `name`    varchar(255) NOT NULL,
    `version` int(11) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UNprovidertype20210709` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- Add provider type to provider
ALTER TABLE `provider`
    ADD COLUMN `provider_type_id` bigint(20) NULL AFTER `name`,
    ADD CONSTRAINT `FKprovidertypeid20210709` FOREIGN KEY (`provider_type_id`) REFERENCES `provider_type`(id);
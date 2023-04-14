CREATE TABLE `module` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `version` int(11) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `pool` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `version` int(11) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `shard` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `last_heartbeat` datetime NOT NULL,
    `shutdown` bit(1) NOT NULL,
    `uuid` varchar(255) NOT NULL,
    `version` int(11) NOT NULL,
    `module_id` bigint(20) DEFAULT NULL,
    `pool_id` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_shard_uuid` (`uuid`),
    UNIQUE KEY `idx_shard` (`module_id`,`pool_id`,`uuid`),
    KEY `idx_shard_shutdown_last_heartbeat` (`shutdown`,`last_heartbeat`),
    KEY `FKotrc000exr0j7yq78qmk7vk2s` (`pool_id`),
    CONSTRAINT `FKotrc000exr0j7yq78qmk7vk2s` FOREIGN KEY (`pool_id`) REFERENCES `pool` (`id`),
    CONSTRAINT `FKtms2va82lxg8tmn61omb6qhwk` FOREIGN KEY (`module_id`) REFERENCES `module` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `module` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `version` int(11) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `instance` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `instance_id` varchar(255) NOT NULL,
    `last_heartbeat` datetime NOT NULL,
    `leader` bit(1) DEFAULT NULL,
    `registered` datetime NOT NULL,
    `version` int(11) NOT NULL,
    `module_id` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_module_instance` (`module_id`,`instance_id`),
    UNIQUE KEY `idx_module_leader` (`module_id`,`leader`),
    KEY `idx_last_heartbeat` (`last_heartbeat`),
    CONSTRAINT `FK8xox3dqtsydlp0bhvewe2tunj` FOREIGN KEY (`module_id`) REFERENCES `module` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
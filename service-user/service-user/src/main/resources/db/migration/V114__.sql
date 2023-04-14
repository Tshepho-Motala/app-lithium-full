ALTER TABLE `user` ADD COLUMN `user_favourites_id` bigint(20) DEFAULT NULL, ALGORITHM INPLACE, LOCK NONE;

CREATE TABLE `user_favourites`(
                                `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                `events` longtext DEFAULT NULL,
                                `competitions` longtext DEFAULT NULL,
                                `last_updated` datetime NOT NULL,
                                PRIMARY KEY(`id`)
) ENGINE = InnoDB
DEFAULT CHARSET = utf8;

ALTER TABLE `user` ADD FOREIGN KEY `FKsmakwfd7sbsfcnny4gnzva6f7p` (`user_favourites_id`) REFERENCES `user_favourites` (`id`);

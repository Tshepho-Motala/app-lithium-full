CREATE TABLE `domain` (
                          `id` bigint NOT NULL AUTO_INCREMENT,
                          `name` varchar(255),
                          `version` integer NOT NULL,
                          PRIMARY KEY (id),
                          UNIQUE KEY `idx_domain_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `dob_day` integer,
                        `dob_month` integer,
                        `dob_year` integer,
                        `guid` varchar(255),
                        `notifications` bit NOT NULL,
                        `test_account` bit NOT NULL,
                        `username` varchar(255),
                        `version` integer NOT NULL,
                        `domain_id` bigint NOT NULL,
                        `account_creation_date` datetime(6),
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `idx_user_guid` (`guid`),
                        constraint `FKk1hsftp46a7obygffmevl2g3s` foreign key (`domain_id`) references `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `threshold` (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `active` bit NOT NULL,
                             `age_max` integer,
                             `age_min` integer,
                             `granularity` varchar(255),
                             `version` integer NOT NULL,
                             `current_id` bigint,
                             `domain_id` bigint,
                             `type_id` bigint,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `idx_threshold_all` (
                                 `domain_id`, `age_min`, `age_max`, `type_id`,
                                 `granularity`
                                 ),
                             constraint `FKse48297da7ye4augg62f51its` foreign key (`domain_id`) references `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `threshold_revision` (
                                      `id` bigint NOT NULL AUTO_INCREMENT,
                                      `amount` decimal(19, 2),
                                      `created_date` datetime(6),
                                      `percentage` decimal(19, 2),
                                      `version` integer NOT NULL,
                                      `created_by_id` bigint,
                                      `threshold_id` bigint NOT NULL,
                                      PRIMARY KEY (`id`),
                                      constraint `FKrfetja8s4d7uh2tsre2bmmg` foreign key (`created_by_id`) references user (`id`),
                                      constraint `FKptcgiitem6t9s98rp7lysyn5x` foreign key (`threshold_id`) references `threshold` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE
    `threshold`
    add
        constraint `FK82fn86eieh77skel8h3cso6me` foreign key (`current_id`) references `threshold_revision` (`id`);




CREATE TABLE `player_threshold_history` (
                                            `id` bigint NOT NULL AUTO_INCREMENT,
                                            `amount` decimal(19, 2),
                                            `daily_loss_limit` decimal(19, 2),
                                            `daily_loss_limit_used` decimal(19, 2),
                                            `deposit_amount` decimal(19, 2),
                                            `monthly_loss_limit` decimal(19, 2),
                                            `monthly_loss_limit_used` decimal(19, 2),
                                            `net_lifetime_deposit_amount` decimal(19, 2),
                                            `threshold_hit_date` datetime(6),
                                            `version` integer NOT NULL,
                                            `weekly_loss_limit` decimal(19, 2),
                                            `weekly_loss_limit_used` decimal(19, 2),
                                            `withdrawal_amount` decimal(19, 2),
                                            `threshold_revision_id` bigint,
                                            `user_id` bigint,
                                            PRIMARY KEY (`id`),
                                            constraint `FKl9lg4yx1q2ydf5x03rh38hec8` foreign key (`threshold_revision_id`) references `threshold_revision` (`id`),
                                            constraint `FKakegehv8c7upjkwrdwc6xni6e` foreign key (`user_id`) references `user` (`id`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `type` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `name` varchar(255),
                        `version` integer NOT NULL,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `idx_type_name`  (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `user_job`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT,
    `domain_id`     bigint(20) NOT NULL,
    `user_id`       bigint(20) NOT NULL,
    `page_size`     int          DEFAULT 10,
    `status`        int(11) DEFAULT 0,
    `phone_length`          int NOT NULL,
    `created_date`  datetime NOT NULL,
    `completed_date` datetime NULL DEFAULT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_user_job_domain_id` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`),
    CONSTRAINT `fk_user_job_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
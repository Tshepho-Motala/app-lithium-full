CREATE TABLE `document_status`
(
  `id`   bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY    `idx_document_status_id` (`id`),
  KEY    `idx_document_status_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `documents`
(
  `id`        bigint(20) NOT NULL,
  `user_id`   bigint(20) NOT NULL,
  `status_id` bigint(20) NOT NULL,
  `sensitive_doc` tinyint(1) NOT NULL DEFAULT '0',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY         `idx_user_id` (`user_id`),
  KEY         `idx_status_id` (`status_id`),
  CONSTRAINT `FK_document_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_document_status` FOREIGN KEY (`status_id`) REFERENCES `document_status` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

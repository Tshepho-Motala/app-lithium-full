CREATE TABLE `gameplay_operation_event_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `event_id` varchar(255) NOT NULL UNIQUE,
  `gameplay_id` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  `gameplay_operation_event_type` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

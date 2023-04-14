CREATE TABLE `summary_reconciliation` (
  `id` bigint(20) NOT NULL,
  `last_date_processed` date DEFAULT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

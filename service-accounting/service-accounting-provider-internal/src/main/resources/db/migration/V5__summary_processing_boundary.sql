CREATE TABLE `summary_processing_boundary` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `last_transaction_id_processed` bigint(20) NOT NULL,
  `last_transaction_label_value_id_processed` bigint(20) NOT NULL,
  `summary_type` int(11) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
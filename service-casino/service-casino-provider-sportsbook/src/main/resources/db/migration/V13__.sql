CREATE TABLE `open_bets_op_migration_audit` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_date` datetime(6) DEFAULT NULL,
    `version` int(11) NOT NULL,
    `bet_id` bigint(20) DEFAULT NULL,
    `reservation_id` bigint(20) NOT NULL,
    `reservation_commit_id` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FKlfrs9exr71kfk7cgb7nkdi6w9` (`bet_id`),
    KEY `FKnuv9silkcma6jky76resycvxg` (`reservation_id`),
    KEY `FKbos77bubfbsv1ox2vqsaqqnrd` (`reservation_commit_id`),
    CONSTRAINT `FKbos77bubfbsv1ox2vqsaqqnrd` FOREIGN KEY (`reservation_commit_id`) REFERENCES `reservation_commit` (`id`),
    CONSTRAINT `FKlfrs9exr71kfk7cgb7nkdi6w9` FOREIGN KEY (`bet_id`) REFERENCES `bet` (`id`),
    CONSTRAINT `FKnuv9silkcma6jky76resycvxg` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE reservation_commit
    MODIFY COLUMN accounting_transaction_id BIGINT(20) NULL;

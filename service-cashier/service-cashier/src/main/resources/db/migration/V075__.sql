CREATE TABLE `auto_withdrawal_rule_settings` (
                                                 `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                                 `version` int(11) NOT NULL,
                                                 `rule_id` bigint(20) NOT NULL,
                                                 `key` varchar(32) NOT NULL,
                                                 `value` varchar(512) NOT NULL,
                                                 PRIMARY KEY (`id`),
                                                 UNIQUE KEY `idx_settings_rule_name` (`rule_id`,`key`),
                                                 CONSTRAINT `FK_settings_rule_id` FOREIGN KEY (`rule_id`) REFERENCES `auto_withdrawal_rule` (`id`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `auto_withdrawal_rule_settings`(`version`, `rule_id`,`key`,`value`)
SELECT 0, rule.id, 'LIST_OF_ACCOUNT_CODES', 'SPORTS_BET_SPORTSBOOK,CASINO_BET_INCENTIVE,CASINO_BET_SLOTAPI,CASINO_BET_ROXOR,CASINO_BET_IFORIUM'
FROM `auto_withdrawal_rule` AS rule
WHERE rule.field = 20;

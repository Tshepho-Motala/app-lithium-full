ALTER TABLE `domain_method` ADD COLUMN `access_rule_on_tran_init` varchar(255) DEFAULT NULL;
ALTER TABLE `domain_method_processor` ADD COLUMN `access_rule_on_tran_init` varchar(255) DEFAULT NULL;

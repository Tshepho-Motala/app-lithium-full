LOCK TABLES `access_control_list` WRITE;
ALTER TABLE `access_control_list` ADD COLUMN `ip_reset_time` INT(11) DEFAULT NULL;
UNLOCK TABLES;

LOCK TABLES `access_control_list` WRITE;
ALTER TABLE `access_control_list` ADD COLUMN `message` varchar(255) DEFAULT NULL;
UNLOCK TABLES;




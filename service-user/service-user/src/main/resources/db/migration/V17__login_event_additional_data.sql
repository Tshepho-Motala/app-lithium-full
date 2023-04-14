ALTER TABLE `login_event`
ADD COLUMN `country` varchar(255),
ADD COLUMN `state` varchar(255),
ADD COLUMN `city` varchar(255),
ADD COLUMN `os` varchar(255),
ADD COLUMN `browser` varchar(255);
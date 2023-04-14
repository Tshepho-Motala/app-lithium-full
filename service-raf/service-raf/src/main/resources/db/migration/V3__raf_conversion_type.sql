ALTER TABLE `configuration`
ADD COLUMN `conversion_type` int(11) NOT NULL DEFAULT 0,
ADD COLUMN `conversion_xp_level` int(11) DEFAULT NULL;
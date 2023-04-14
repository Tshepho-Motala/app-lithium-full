ALTER TABLE bonus_rules_freespins 
ADD COLUMN `provider_guid` varchar(255) DEFAULT NULL;

ALTER TABLE bonus_revision
ADD COLUMN `play_through_required_type` int(11) DEFAULT NULL;
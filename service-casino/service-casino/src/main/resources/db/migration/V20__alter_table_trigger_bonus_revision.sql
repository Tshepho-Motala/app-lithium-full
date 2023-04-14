ALTER TABLE bonus_revision
ADD COLUMN `bonus_trigger_type` int(11) DEFAULT NULL;

ALTER TABLE bonus_revision
ADD COLUMN `trigger_amount` int(11) DEFAULT NULL;

ALTER TABLE bonus_revision
ADD COLUMN `trigger_granularity` int(11) DEFAULT NULL;

ALTER TABLE bonus_revision
ADD COLUMN `bonus_type` int(11) DEFAULT NULL;

UPDATE bonus_revision SET `bonus_type`=1;
UPDATE bonus_revision SET `bonus_type`=0 WHERE `bonus_code`='';
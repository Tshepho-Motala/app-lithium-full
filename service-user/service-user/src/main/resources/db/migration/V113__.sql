--  Updating day periods (granularity 3) to have week=-1
UPDATE `period`
SET `week` = '-1'
WHERE `granularity_id` = 3;

--  Updating week periods (granularity 4) to have month=-1 and day=-1
UPDATE `period`
SET `month` = '-1', `day` = '-1'
WHERE `granularity_id` = 4;

--  Updating month periods (granularity 2) to have day=-1 and week=-1
UPDATE `period`
SET `day` = '-1', `week` = '-1'
WHERE `granularity_id` = 2;

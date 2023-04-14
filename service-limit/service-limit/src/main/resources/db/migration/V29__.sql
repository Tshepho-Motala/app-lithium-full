ALTER TABLE player_time_slot_limit ADD COLUMN domain_id BIGINT(10);
ALTER TABLE player_time_slot_limit DROP COLUMN domain_name;

ALTER TABLE player_time_slot_limit_history ADD COLUMN domain_id BIGINT(10);
ALTER TABLE player_time_slot_limit_history DROP COLUMN domain_name;
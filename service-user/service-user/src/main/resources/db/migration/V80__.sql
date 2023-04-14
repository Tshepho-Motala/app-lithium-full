/*if roles duplicated - remove wrong role 'PLAYER_BALACE_ADJUST' */;

DELETE a
FROM grd a
JOIN (
    SELECT group_id
    FROM grd a
    WHERE role_id IN (SELECT id FROM role WHERE role IN ('PLAYER_BALACE_ADJUST','PLAYER_BALANCE_ADJUST'))
    GROUP BY group_id HAVING COUNT(*) > 1
    ) a1 ON a1.group_id = a.group_id
JOIN (
    SELECT * FROM grd a
    WHERE role_id IN (SELECT id FROM role WHERE role = 'PLAYER_BALACE_ADJUST')
    ) a2 ON a2.id = a.id

/*updated wrong role 'PLAYER_BALACE_ADJUST' to correct one */;

UPDATE grd
SET role_id = (SELECT id FROM role WHERE role = 'PLAYER_BALANCE_ADJUST')
WHERE role_id = (SELECT id FROM role WHERE role = 'PLAYER_BALACE_ADJUST') ;

/*delete wrong role 'PLAYER_BALACE_ADJUST'*/;
DELETE FROM role WHERE role = 'PLAYER_BALACE_ADJUST';


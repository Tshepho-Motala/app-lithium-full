DROP PROCEDURE IF EXISTS build_id_list;

DELIMITER $$

CREATE PROCEDURE build_id_list(id_list TEXT)
BEGIN

DECLARE v_finished INTEGER DEFAULT 0;
DECLARE clfc_id BIGINT DEFAULT -1;
DECLARE timer VARCHAR(1000) DEFAULT "";

DECLARE clfc_cursor CURSOR FOR
	select clfc.id from change_log_field_change clfc
	left outer join change_log cl
	on clfc.change_log_id = cl.id
	left outer join change_log_entity cle
	on (cle.id = cl.change_log_entity_id and cl.change_log_entity_id = 14)
	left outer join change_log_type clt
	on (clt.id = cl.change_log_type_id and cl.change_log_type_id = 4)
	where (cl.change_date >= (DATE(NOW() - INTERVAL 11 DAY)))
	and cl.author_user_id = 36
	and CHAR_LENGTH(clfc.to_value) > 50000;
	
DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_finished = 1;

OPEN clfc_cursor; 

SET timer = CONCAT(CURRENT_TIMESTAMP()," ; ", timer);
get_ids: LOOP
	FETCH clfc_cursor INTO clfc_id;
	
	IF v_finished = 1 THEN LEAVE get_ids; END IF;
	
	SET id_list = CONCAT(clfc_id, ";", id_list);

	UPDATE change_log_field_change clfc
	SET clfc.from_value = '', clfc.to_value = 'not available'
	WHERE clfc.id = clfc_id;
	
	DO SLEEP(0.1);
	
END LOOP get_ids;

CLOSE clfc_cursor;

SET timer = CONCAT(CURRENT_TIMESTAMP()," ; ", timer);

SELECT id_list, timer;

END$$
 
DELIMITER ;

SET @id_list = "";
CALL build_id_list(@id_list);
SELECT @id_list;
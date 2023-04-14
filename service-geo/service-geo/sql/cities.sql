SELECT 
 count(*)
-- ct.code, ct.name, c.name, l1.name, l2.name 
FROM city ct 
	LEFT OUTER JOIN admin_level2 l2 
	ON ct.level2_id = l2.id
	LEFT OUTER JOIN admin_level1 l1 
	ON ct.level1_id = l1.id 
	LEFT OUTER JOIN country c 
	ON ct.country_id = c.id
-- WHERE C.code = "ZA" 

-- LIMIT 10000;

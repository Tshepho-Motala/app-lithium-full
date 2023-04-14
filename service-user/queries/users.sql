select 
	lower(u.username), s.name as status, first_name, last_name, email, cellphone_number, a.address_line1, a.address_line2, a.address_line3, a.admin_level1 as state, a.country
from user u
	left outer join status s ON u.status_id = s.id
	left outer join address a ON u.residential_address_id = a.id
    left outer join domain d on u.domain_id = d.id
where d.name = 'luckybetz'
order by username;
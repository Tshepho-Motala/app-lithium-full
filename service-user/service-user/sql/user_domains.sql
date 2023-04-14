select d.name, grd.descending, grd.self_applied

from user u 
	left outer join user_groups ug 
		left outer join group_table g
			left outer join grd
				left outer join domain d
				on grd.domain_id = d.id
			on grd.group_id = g.id
		on g.id = ug.group_id
	on ug.user_id = u.id

where u.username = 'admin'
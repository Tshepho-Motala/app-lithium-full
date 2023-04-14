select 
	 substring(g.provider_guid from 25) as provider, lv.value as platform, g.visible, count(*) count
from 
	game g 
		left outer join game_label_value glv
			left outer join label_value lv 
				left outer join label l 
                on l.id = lv.label_id
            on lv.id = glv.label_value_id
        on glv.game_id = g.id
where l.name = 'os' and domain_name = 'default' 
and (lv.value = 'Desktop' or lv.value = 'Mobile' or lv.value is null)
group by substring(g.provider_guid from 25), lv.value, g.visible

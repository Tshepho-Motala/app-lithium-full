SELECT g.id, g.guid, g.provider_game_id, g.name, substring(provider_guid, 25) as provider, lv.value as platform, (g.visible && g.enabled) as enabled 
FROM game g 
LEFT OUTER JOIN game_label_value glv 
	LEFT OUTER JOIN label_value lv 
		LEFT OUTER JOIN label l
        ON lv.label_id = l.id
    ON glv.label_value_id = lv.id
ON glv.game_id = g.id
WHERE l.name="os" 
-- and lv.value in ('Mobile', 'Desktop', null)
-- and provider_game_id = 30025

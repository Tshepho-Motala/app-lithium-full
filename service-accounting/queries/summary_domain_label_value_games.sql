SELECT 
	sum(s.debit_cents) / 100 as bet, 
    sum(s.credit_cents) / 100 as win, 
    sum(case (tt.code) when 'CASINO_BET' then s.tran_count end) as bet_count, 
    sum(case (tt.code) when 'CASINO_WIN' then s.tran_count end) as win_count, 
    sum(s.credit_cents) / sum(s.debit_cents) * 100 as RTP, 
    ac.code, lv.value as game_guid,
    g.name as game, g.platform as platform, 
    g.provider as provider
    -- , MIN(p.date_start), MAX(p.date_end)

FROM 
	summary_domain_label_value s
    LEFT OUTER JOIN account_code ac ON ac.id = s.account_code_id
    LEFT OUTER JOIN label_value lv 
		LEFT OUTER JOIN games g 
        ON g.guid = REPLACE(REPLACE(lv.value, '/', '_'), 'luckybetz/', '')
		LEFT OUTER JOIN label l
        ON lv.label_id = l.id
    ON lv.id = s.label_value_id
    LEFT OUTER JOIN period p 
		LEFT OUTER JOIN domain d 
        ON p.domain_id = d.id
    ON p.id = s.period_id
    LEFT OUTER JOIN transaction_type tt
    ON tt.id = s.transaction_type_id
    
    
WHERE 
	l.name = 'game_guid'
    and (ac.code = 'PLAYER_BALANCE_CASINO_BONUS' or ac.code = 'PLAYER_BALANCE')
    and p.granularity = 2
    and p.date_start > '2017/08/20'
    and d.name = 'luckybetz'
    and (tt.code = 'CASINO_BET' or tt.code = 'CASINO_WIN')

GROUP BY ac.code, lv.value, g.name, g.platform, g.provider

ORDER BY MIN(p.date_start) DESC

LIMIT 1000000
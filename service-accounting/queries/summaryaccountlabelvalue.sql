select l.name, lv.value, ac.code, act.code, salv.*

from
	summary_account_label_value salv 
	left outer join account a 
		left outer join account_code ac
        on a.account_code_id = ac.id
        left outer join account_type act
        on a.account_type_id = act.id
		left outer join user u 
        on a.owner_id = u.id
    on salv.account_id = a.id
    left outer join label_value lv
		left outer join label l
        on lv.label_id = l.id
    on salv.label_value_id = lv.id
    left outer join period p
    on salv.period_id = p.id
    left outer join transaction_type tt 
    on salv.transaction_type_id = tt.id

where 1 = 1
	and ac.code = 'PLAYER_BALANCE_CASINO_BONUS'
	and u.guid = 'luckybetz/joelucky'
    and l.name = 'player_bonus_history_id'
    and lv.value = '5'
	and p.granularity = 5
    and tt.code = 'CASINO_WIN'
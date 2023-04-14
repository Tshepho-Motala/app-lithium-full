select * 
from transaction t
	left outer join transaction_entry te
		left outer join account a
			left outer join account_code ac
            on a.account_code_id = ac.id
        on te.account_id = a.id
    on te.transaction_id = t.id
	left outer join transaction_type tt
	on t.transaction_type_id = tt.id
    left outer join transaction_label_value tlv
		left outer join label_value lv
			left outer join label l
            on lv.label_id = l.id
        on tlv.label_value_id = lv.id
	on tlv.transaction_id = t.id
where 
	t.id = 64 and
	ac.code = 'PLAYER_BALANCE'
select
    te.date,
    t.id as 'tran_id',
    ac.code as 'account',
    tt.code as 'transaction',
    te.amount_cents * -1 as 'amount',
    u.guid as 'user',
    (select lv.value from transaction_label_value tlv
				left outer join label_value lv
					left outer join label l
                    on lv.label_id = l.id
                on tlv.label_value_id = lv.id
            where tlv.transaction_id = t.id and l.name = 'game_guid') as game_guid,
    (select lv.value from transaction_label_value tlv
				left outer join label_value lv
					left outer join label l
                    on lv.label_id = l.id
                on tlv.label_value_id = lv.id
            where tlv.transaction_id = t.id and l.name = 'transaction_id') as external_tran_id

from
    transaction_entry te
        left outer join account a 
			left outer join account_code ac
            on a.account_code_id = ac.id
            left outer join account_type act
            on a.account_type_id = act.id
            left outer join user u
            on a.owner_id = u.id
        on te.account_id = a.id
        left outer join transaction t
			left outer join transaction_type tt
            on t.transaction_type_id = tt.id
        on te.transaction_id = t.id
where 1 = 1
-- 	and u.guid = 'luckybetz/ematic210'
-- 	and te.date > '2016-12-09 00:00:00'
    and act.code = 'PLAYER_BALANCE'
    
group by
    te.date,
    t.id,
    ac.code,
    tt.code,
    te.amount_cents,
    u.guid
    
order by te.date
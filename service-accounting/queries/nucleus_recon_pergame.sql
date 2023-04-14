select sum(sd.credit_cents / 100) credit, sum(sd.debit_cents / 100) debit
from 
	summary_domain_label_value sd
		left outer join period p 
			left outer join domain d on p.domain_id = d.id
        on sd.period_id = p.id
        left outer join account_code ac
        on sd.account_code_id = ac.id
        left outer join label_value lv 
			left outer join label l
            on lv.label_id = l.id
		on sd.label_value_id = lv.id

where 
	d.name = 'luckybetz'
	and p.granularity = 2
    and date_start = '2018-01-01 00:00:00'
    and ac.code in ('CASINO_BET_NUCLEUS', 'CASINO_WIN_NUCLEUS')
    and l.name = 'game_guid'
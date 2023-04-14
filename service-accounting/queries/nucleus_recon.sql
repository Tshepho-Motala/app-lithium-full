select sum(sd.credit_cents) credit, sum(sd.debit_cents) debit, (sum(sd.credit_cents) - sum(sd.debit_cents)) net
from 
	summary_domain sd
		left outer join period p 
			left outer join domain d on p.domain_id = d.id
        on sd.period_id = p.id
        left outer join account_code ac
        on sd.account_code_id = ac.id

where 
	d.name = 'luckybetz'
	and p.granularity = 2
    and date_start = '2018-01-01 00:00:00'
    and ac.code in ('CASINO_BET_NUCLEUS', 'CASINO_WIN_NUCLEUS')
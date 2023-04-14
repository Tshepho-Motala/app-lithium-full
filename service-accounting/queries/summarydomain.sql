select 
	p.day, p.month, p.year, p.date_start, p.date_end, p.week, p.granularity, 
	ac.code, sd.opening_balance_cents, sd.closing_balance_cents, 
	sd.debit_cents, sd.credit_cents,
	sd.closing_balance_cents - sd.opening_balance_cents - sd.debit_cents + sd.credit_cents nett

from period p 
		left outer join summary_domain sd 
				left outer join account_code ac
				on sd.account_code_id = ac.id
		on sd.period_id = p.id
		left outer join domain d
		on p.domain_id = d.id

where 1 = 1
/*	and ac.code = 'PLAYERBALANCE' */
	and p.granularity = 1
	and d.name = 'player'

order by ac.code, p.granularity, date_start
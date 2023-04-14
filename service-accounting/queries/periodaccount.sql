select 
	p.day, p.month, p.year, p.date_start, p.date_end, p.week, p.granularity, 
	ac.code, o.guid, pa.opening_balance_cents, pa.closing_balance_cents, 
	pa.debit_cents, pa.credit_cents,
	pa.closing_balance_cents - pa.opening_balance_cents - pa.debit_cents + pa.credit_cents nett

from period p 
		left outer join summary_account pa 
			left outer join account a 
				left outer join account_code ac
				on a.account_code_id = ac.id
				left outer join user o
				on a.owner_id = o.id
			on a.id = pa.account_id
		on pa.period_id = p.id

where ac.code = 'PLAYERBALANCE' 
	and p.granularity=1
/*	and o.guid = 'player/player1'  */

order by o.guid, ac.code, p.granularity, date_start
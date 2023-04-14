select 
/*	p.day, p.month, p.year, p.week, */
	p.granularity, 
/*	sum(pa.opening_balance_cents) opening, sum(pa.closing_balance_cents) closing, */
	sum(pa.closing_balance_cents) - sum(pa.opening_balance_cents) as balance,
	sum(pa.debit_cents) debits, sum(pa.credit_cents) credits

from period p 
		left outer join summary_account pa 
			left outer join account a 
				left outer join account_code ac
				on a.account_code_id = ac.id
				left outer join user o
				on a.owner_id = o.id
			on a.id = pa.account_id
		on pa.period_id = p.id

group by 
	/* p.day, p.month, p.year, p.week, */
	p.granularity

order by 
	p.granularity
/*	, p.year, p.month, p.day */
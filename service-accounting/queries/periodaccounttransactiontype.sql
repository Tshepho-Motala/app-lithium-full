select 
	p.day, p.month, p.year, p.date_start, p.date_end, p.week, p.granularity, 
	ac.code as account_code, c.code as currency, o.guid, tt.code as tran_code,
	patt.debit_cents, patt.credit_cents

from period p 
		left outer join summary_account_transaction_type patt 
			left outer join transaction_type tt
			on patt.transaction_type_id = tt.id
			left outer join account a 
				left outer join account_code ac
				on a.account_code_id = ac.id
				left outer join user o
				on a.owner_id = o.id
				left outer join currency c
				on a.currency_id = c.id
			on a.id = patt.account_id
		on patt.period_id = p.id

where 1 = 1
	and ac.code = 'PLAYERBALANCE'  
	and tt.code = 'PROCESSOR_DEPOSIT' 
	and p.granularity=1
-- 	and o.guid = 'player/player1'

order by o.guid, p.granularity, date_start, ac.code
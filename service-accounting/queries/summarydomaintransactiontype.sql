set @accountCode = 'PLAYER_BALANCE_CASINO_BONUS';
set @transactionTypeCode = 'TRANSFER_FROM_CASINO_BONUS';
set @granularity = 2;
set @domainName = 'luckybetz';

	select 
		p.id,
		p.day, p.month, p.year, p.date_start, p.date_end, p.week, p.granularity, 
		ac.code as account_code, c.code as currency, tt.code as tran_code,
		s.debit_cents, s.credit_cents, s.tran_count

	from period p 
			left outer join domain d 
            on d.id = p.domain_id
			left outer join summary_domain_transaction_type s 
				left outer join transaction_type tt
				on s.transaction_type_id = tt.id
				left outer join account_code ac
				on s.account_code_id = ac.id
				left outer join currency c
				on s.currency_id = c.id
			on s.period_id = p.id

	where 1 = 1
 		and ac.code = @accountCode  
 		and tt.code = @transactionTypeCode 
		and p.granularity=@granularity
        and d.name = @domainName
        
	order by date_start desc
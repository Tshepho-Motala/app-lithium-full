set @accountCode = 'PLAYERBALANCE';
set @transactionTypeCode = 'CASINO_BET';
set @granularity = 1;
set @labelname = 'provider_guid';
set @labelvalue = 'betsoft';

drop table if exists temp_output;

create temporary table temp_output as (
	select 'per_domain  ' as src, 
		p.day, p.month, p.year, p.date_start, p.date_end, p.week, p.granularity, 
		ac.code as account_code, c.code as currency, tt.code as tran_code,
		s.debit_cents, s.credit_cents, s.tran_count

	from period p 
			left outer join summary_domain_label_value s 
				left outer join transaction_type tt
				on s.transaction_type_id = tt.id
				left outer join account_code ac
				on s.account_code_id = ac.id
				left outer join currency c
				on s.currency_id = c.id
				left outer join label_value lv
					left outer join label l 
					on lv.label_id = l.id
				on s.label_value_id = lv.id
			on s.period_id = p.id

	where 1 = 1
		and ac.code = @accountCode  
		and tt.code = @transactionTypeCode 
		and p.granularity=@granularity
		and l.name = @labelname
		and lv.value = @labelvalue
);

insert into temp_output
(
	select 'per_account' as src, 
		p.day, p.month, p.year, p.date_start, p.date_end, p.week, p.granularity, 
		ac.code as account_code, c.code as currency, tt.code as tran_code,
		sum(s.debit_cents) as debit_cents, sum(s.credit_cents) as credit_cents, sum(s.tran_count)

	from period p 
			left outer join summary_account_label_value s 
				left outer join transaction_type tt
				on s.transaction_type_id = tt.id
				left outer join account a 
					left outer join account_code ac
					on a.account_code_id = ac.id
					left outer join currency c
					on a.currency_id = c.id
				on a.id = s.account_id
				left outer join label_value lv
					left outer join label l 
					on lv.label_id = l.id
				on s.label_value_id = lv.id
			on s.period_id = p.id

	where 1 = 1
		and ac.code = @accountCode  
		and tt.code = @transactionTypeCode 
		and p.granularity=@granularity
		and l.name = @labelname
		and lv.value = @labelvalue

	group by 
		p.day, p.month, p.year, p.date_start, p.date_end, p.week, p.granularity, 
		ac.code, c.code, tt.code 
);

insert into temp_output
(
	select 'raw' as src, 
		p.day, p.month, p.year, p.date_start, p.date_end, p.week, p.granularity, 
		ac.code as account_code, c.code as currency, tt.code as tran_code,
		sum(if (te.amount_cents > 0, te.amount_cents, 0)) as debit_cents, 
		sum(if (te.amount_cents < 0, te.amount_cents * -1, 0)) as credit_cents, 
		sum(1)
		
	from period p 
			left outer join transaction_entry te
				left outer join transaction t
					left outer join transaction_type tt
					on t.transaction_type_id = tt.id
					left outer join transaction_label_value tlv
						left outer join label_value lv
							left outer join label l 
							on lv.label_id = l.id
						on tlv.label_value_id = lv.id
					on tlv.transaction_id = t.id
				on te.transaction_id = t.id
				left outer join account a
					left outer join account_code ac
					on a.account_code_id = ac.id
					left outer join currency c
					on a.currency_id = c.id
				on te.account_id = a.id
			on te.date >= p.date_start and te.date < p.date_end
	where 1 = 1
		and ac.code = @accountCode  
		and tt.code = @transactionTypeCode 
		and p.granularity=@granularity
		and l.name = @labelname
		and lv.value = @labelvalue
	group by
		p.day, p.month, p.year, p.date_start, p.date_end, p.week, p.granularity,
		ac.code, c.code, tt.code 
);

select * from temp_output order by granularity, date_start, account_code, tran_count
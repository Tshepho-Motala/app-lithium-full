select lower(substring(u.guid, 11)) username, 
	sum(case when (act.code = 'PLAYER_BALANCE' and tt.code = 'BALANCE_ADJUST') then (satt.credit_cents - satt.debit_cents) / 100 else 0 end) as balance_adjusts,
	sum(case when (ac.code = 'PLAYER_BALANCE' and tt.code = 'CASHIER_DEPOSIT') then (satt.credit_cents - satt.debit_cents) / 100 else 0 end) as deposit_amount,
	sum(case when (ac.code = 'PLAYER_BALANCE' and tt.code = 'CASHIER_DEPOSIT') then satt.tran_count else 0 end) as deposit_count,
	sum(case when (ac.code = 'PLAYER_BALANCE' and tt.code = 'CASHIER_PAYOUT') then (satt.debit_cents - satt.credit_cents) / 100 else 0 end) as payout_amount,
	sum(case when (ac.code = 'PLAYER_BALANCE' and tt.code = 'CASHIER_PAYOUT') then satt.tran_count else 0 end) as payout_count,
	sum(case when (ac.code = 'PLAYER_BALANCE' and tt.code = 'CASINO_BET') then (satt.debit_cents - satt.credit_cents) / 100 else 0 end) as casino_bets,
	sum(case when (ac.code = 'PLAYER_BALANCE' and tt.code = 'CASINO_BET_ROLLBACK') then (satt.credit_cents - satt.debit_cents) / 100 else 0 end) as casino_bets_rollback,
	sum(case when (ac.code = 'PLAYER_BALANCE' and tt.code = 'CASINO_WIN') then (satt.credit_cents - satt.debit_cents) / 100 else 0 end) as casino_wins,
	sum(case when (ac.code = 'PLAYER_BALANCE' and tt.code = 'CASINO_WIN_ROLLBACK') then (satt.debit_cents - satt.credit_cents) / 100 else 0 end) as casino_wins_rollback,
	sum(case when (ac.code = 'PLAYER_BALANCE' and tt.code = 'CASINO_NEGATIVE_BET') then (satt.credit_cents - satt.debit_cents) / 100 else 0 end) as casino_negative_bet,
	sum(case when (ac.code = 'PLAYER_BALANCE' and tt.code = 'CASINO_NEGATIVE_BET_ROLLBACK') then (satt.debit_cents - satt.credit_cents) / 100 else 0 end) as casino_negative_bet_rollback,
	sum(case when (ac.code = 'PLAYER_BALANCE' and (tt.code = 'CASINO_BET' or tt.code = 'CASINO_WIN')) then (satt.debit_cents - satt.credit_cents) / 100 else 0 end) as casino_betwin_net,
	sum(case when (ac.code = 'PLAYER_BALANCE' and tt.code = 'TRANSFER_TO_CASINO_BONUS') then (satt.debit_cents - satt.credit_cents) / 100 else 0 end) as balance_transferred_to_bonus,
	sum(case when (act.code = 'PLAYER_BALANCE' and tt.code = 'CASINO_BONUS_ACTIVATE') then (satt.credit_cents - satt.debit_cents) / 100 else 0 end) as bonus_free_money,
	sum(case when (act.code = 'PLAYER_BALANCE' and tt.code = 'CASINO_BONUS_CANCEL') then (satt.debit_cents - satt.credit_cents) / 100 else 0 end) as bonus_cancel,
	sum(case when (act.code = 'PLAYER_BALANCE' and tt.code = 'CASINO_BONUS_EXPIRED') then (satt.debit_cents - satt.credit_cents) / 100 else 0 end) as bonus_expired,
	sum(case when (act.code = 'PLAYER_BALANCE' and tt.code = 'CASINO_BONUS_MAXPAYOUT_EXCESS') then (satt.debit_cents - satt.credit_cents) / 100 else 0 end) as bonus_maxpayout_excess,
	sum(case when (ac.code = 'PLAYER_BALANCE_CASINO_BONUS' and tt.code = 'CASINO_BET') then (satt.debit_cents - satt.credit_cents) / 100 else 0 end) as casino_bonus_bet,
	sum(case when (ac.code = 'PLAYER_BALANCE_CASINO_BONUS' and tt.code = 'CASINO_WIN') then (satt.credit_cents - satt.debit_cents) / 100 else 0 end) as casino_bonus_win,
	sum(case when (ac.code = 'PLAYER_BALANCE_CASINO_BONUS' and (tt.code = 'CASINO_BET' or tt.code = 'CASINO_WIN')) then (satt.debit_cents - satt.credit_cents) / 100 else 0 end) as casino_bonus_betwin_net,
	sum(case when (ac.code = 'PLAYER_BALANCE' and tt.code = 'TRANSFER_FROM_CASINO_BONUS') then (satt.credit_cents - satt.debit_cents) / 100 else 0 end) as balance_transferred_from_bonus,
    max(case when (ac.code = 'PLAYER_BALANCE') then a.balance_cents * -1 / 100 else 0 end) as balance,
    max(case when (ac.code = 'PLAYER_BALANCE_CASINO_BONUS') then a.balance_cents * -1 / 100 else 0 end) as balance_casino_bonus

from account a

	left outer join domain d
	on a.domain_id = d.id

	left outer join user u 
	on a.owner_id = u.id

	left outer join summary_account_transaction_type satt
		left outer join transaction_type tt 
		on satt.transaction_type_id = tt.id
		left outer join period p
		on satt.period_id = p.id
	on satt.account_id = a.id

	left outer join account_code ac 
	on a.account_code_id = ac.id
    
    left outer join account_type act
    on a.account_type_id = act.id

where 1 = 1
	and p.granularity = 5
	and d.name = 'luckybetz'
    
group by u.guid
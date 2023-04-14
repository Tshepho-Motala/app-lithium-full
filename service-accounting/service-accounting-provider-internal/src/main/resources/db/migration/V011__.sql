-- select * from transaction_type_account tta
update transaction_type_account tta
    left outer join transaction_type tt
    on tta.transaction_type_id = tt.id
set tta.credit = TRUE , tta.debit = FALSE
where tt.code = 'NEGATIVE_BALANCE_ADJUST'
  and tta.account_type_code = 'PLAYER_BALANCE';

-- select * from transaction_type_account tta
update transaction_type_account tta
    left outer join transaction_type tt
    on tta.transaction_type_id = tt.id
set tta.credit = FALSE , tta.debit = TRUE
where tt.code = 'NEGATIVE_BALANCE_ADJUST'
  and tta.account_type_code = 'NEGATIVE_BALANCE_ADJUST';
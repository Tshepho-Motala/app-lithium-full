select bet.bet_transaction_id, bet.total_odds, bet.total_stake, bs.price, 
	bs.selection_id, st.name as selection, m.name as market, 
    en.name as event, e.start_timestamp as event_start
from bet
  left outer join bet_selection bs
    left outer join selection_type st
    on bs.selection_type_id = st.id
    left outer join market m
    on bs.market_id = m.id
    left outer join event e
	  left outer join event_name en
      on e.event_name_id = en.id
    on bs.event_id = e.id
  on bs.bet_id = bet.id
-- where bet_transaction_id = 'BETTRANSACTIONID21'

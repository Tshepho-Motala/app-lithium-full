select * 
from settlement s
  left outer join bet b
  on s.bet_id = s.id
  left outer join settlement_selection ss
	left outer join bet_selection bs
    on ss.bet_selection_id = bs.id
  on ss.settlement_id = s.id
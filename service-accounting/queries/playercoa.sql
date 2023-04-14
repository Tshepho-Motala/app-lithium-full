select ac.code, a.balance_cents
from account a
left outer join user o on a.owner_id = o.id
left outer join account_code ac on a.account_code_id = ac.id
where o.guid = 'luckybetz/shareta1'
select l.name, lv.value
from transaction t
left outer join transaction_label_value tlv 
	left outer join label_value lv 
		left outer join label l 
        on l.id = lv.label_id
    on tlv.label_value_id = lv.id
on t.id = tlv.transaction_id
where t.id = 223825;

select * 
from transaction t
left outer join user u
on t.author_id = u.id
where t.id = 223825;

select * 
from transaction_entry te 
	left outer join account a 
		left outer join account_code ac
        on a.account_code_id = ac.id
		left outer join user o
		on o.id = a.owner_id
	on a.id = te.account_id
where te.transaction_id = 223825;
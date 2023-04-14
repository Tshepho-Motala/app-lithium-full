select tk.key_code, tvd.value as default_value, tvr.value, l.locale2
from translation_key tk
	left outer join translation_value tv
		left outer join language l
        on tv.language_id = l.id
		left outer join translation_value_default tvd 
		on tv.default_value_id = tvd.id
        left outer join translation_value_revision tvr
        on tv.current_id = tvr.id
    on tv.key_id = tk.id
where key_code = 'SOMEKEY'
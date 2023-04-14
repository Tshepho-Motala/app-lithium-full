select 
	rrev.name,
	username.value as username,
    first_name.value as first_name,
    last_name.value as last_name,
    email.value as email,
    rrr.enabled,
    user_status.value as user_status,
	rrr.email_validated,
    
    residential_address_line1.value as residential_address_line1,
    residential_address_line2.value as residential_address_line2,
    residential_address_line3.value as residential_address_line3,
    residential_address_city.value as residential_address_city,
    residential_address_admin_level1.value as residential_address_admin_level1,
    residential_address_country.value as residential_address_country,
    residential_address_postal_code.value as residential_address_postal_code,

    postal_address_line1.value as postal_address_line1,
    postal_address_line2.value as postal_address_line2,
    postal_address_line3.value as postal_address_line3,
    postal_address_city.value as postal_address_city,
    postal_address_admin_level1.value as postal_address_admin_level1,
    postal_address_country.value as postal_address_country,
    postal_address_postal_code.value as postal_address_postal_code,
    
    rrr.current_balance_cents,
    rrr.current_balance_casino_bonus_cents,
    rrr.period_opening_balance_cents,
    rrr.period_closing_balance_cents,
    rrr.period_opening_balance_casino_bonus_cents,
    rrr.period_closing_balance_casino_bonus_cents,
    
    rrr.deposit_amount_cents,
    rrr.deposit_count,
    
    rrr.payout_amount_cents,
    rrr.payout_count,
    
    rrr.balance_adjust_amount_cents,
    rrr.balance_adjust_count,
    
    rrr.casino_bet_amount_cents,
    rrr.casino_bet_count,
    
    rrr.casino_win_amount_cents,
    rrr.casino_win_count,
    
    rrr.casino_net_amount_cents,
    
    rrr.casino_bonus_bet_amount_cents,
    rrr.casino_bonus_bet_count,
    
    rrr.casino_bonus_win_amount_cents,
    rrr.casino_bonus_win_count,
    
    rrr.casino_bonus_net_amount_cents,
    
    rrr.casino_bonus_activate_amount_cents,
    rrr.casino_bonus_transfer_to_bonus_amount_cents,
    rrr.casino_bonus_transfer_from_bonus_amount_cents,

    rrr.casino_bonus_cancel_amount_cents,
    rrr.casino_bonus_expire_amount_cents,

    rrr.casino_bonus_max_payout_excess_amount_cents
    
from report r

	left outer join report_revision rrev
    on r.current_id = rrev.id

	left outer join report_run rr 
		left outer join report_run_results rrr 
        
			left outer join string_value username
            on rrr.username_id = username.id
            
			left outer join string_value first_name
            on rrr.first_name_id = first_name.id

			left outer join string_value last_name
            on rrr.last_name_id = last_name.id

			left outer join string_value email
            on rrr.email_id = email.id

 			left outer join string_value user_status
			on rrr.status_id = user_status.id

			left outer join string_value residential_address_line1
            on rrr.residential_address_line1_id = residential_address_line1.id

			left outer join string_value residential_address_line2
            on rrr.residential_address_line2_id = residential_address_line2.id

			left outer join string_value residential_address_line3
            on rrr.residential_address_line3_id = residential_address_line3.id

			left outer join string_value residential_address_city
            on rrr.residential_address_city_id = residential_address_city.id

			left outer join string_value residential_address_admin_level1
            on rrr.residential_address_admin_level1_id = residential_address_admin_level1.id

			left outer join string_value residential_address_country
            on rrr.residential_address_country_id = residential_address_country.id

			left outer join string_value residential_address_postal_code
            on rrr.residential_address_postal_code_id = residential_address_postal_code.id


			left outer join string_value postal_address_line1
            on rrr.postal_address_line1_id = postal_address_line1.id

			left outer join string_value postal_address_line2
            on rrr.postal_address_line2_id = postal_address_line2.id

			left outer join string_value postal_address_line3
            on rrr.postal_address_line3_id = postal_address_line3.id

			left outer join string_value postal_address_city
            on rrr.postal_address_city_id = postal_address_city.id

			left outer join string_value postal_address_admin_level1
            on rrr.postal_address_admin_level1_id = postal_address_admin_level1.id

			left outer join string_value postal_address_country
            on rrr.postal_address_country_id = postal_address_country.id

			left outer join string_value postal_address_postal_code
            on rrr.postal_address_postal_code_id = postal_address_postal_code.id

            
        on rrr.report_run_id = rr.id
    on r.last_completed_id = rr.id
    
where rrev.name = 'players_alltime'
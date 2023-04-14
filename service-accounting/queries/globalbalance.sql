drop temporary table if exists temp_output;

create temporary table temp_output (
    total varchar(255),
    type_code varchar(255),
    balance_cents bigint
);

insert into temp_output (
    select
        'TOTAL                          ' as name,
        '                               ' as type_code, sum(balance_cents)
    from account
);

insert into temp_output (
    select ac.code as name,
           t.code as type_code,
           sum(balance_cents)
    from account a
             left outer join account_code ac
                             on a.account_code_id = ac.id
             left outer join account_type t
                             on a.account_type_id = t.id
    group by ac.code, t.code
);

select * from temp_output;
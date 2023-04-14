drop view if exists bet_with_game;

create or replace view bet_round_with_game as
    select br.id as 'id', br.id as 'bet_round_id', sum(b.amount) as 'bet_amount', g.id as 'game_id'
    from lithium_casino.bet b
             left join lithium_casino.bet_round br
                       on b.bet_round_id = br.id
             left join lithium_casino.game cg
                       on br.game_id = cg.id
             left join lithium_games.game g
                       on g.guid = substring_index(cg.guid, '/', -1)
                           and g.domain_id = (select id
                                              from lithium_games.domain
                                              where name = substring_index(cg.guid, '/', 1))
    group by br.id, g.id;
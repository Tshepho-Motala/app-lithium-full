-- I don't like it either...

create or replace view bet_with_game as
    select b.id as 'id', b.id as 'bet_id', g.id as 'game_id'
    from lithium_casino.bet b
             left join lithium_casino.bet_round br
                       on b.bet_round_id = br.id
             left join lithium_casino.game cg
                       on br.game_id = cg.id
             left join lithium_games.game g
                       on g.guid = substring_index(cg.guid, '/', -1)
                           and g.domain_id = (select id
                                              from lithium_games.domain
                                              where name = substring_index(cg.guid, '/', 1));
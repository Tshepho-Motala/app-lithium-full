create table `mfg_details`
(
    id               bigint auto_increment
        primary key,
    mfg_earned_picks varchar(255) null,
    mfg_status       varchar(255) null
);

create table `player_details`
(
    id                  bigint auto_increment
        primary key,
    current_day         varchar(255) null,
    dfg_picks_remaining varchar(255) null,
    mfg_details_id      bigint       not null,
    constraint FK69bxob7xr7ln7pm1i5dcu3oxa
        foreign key (mfg_details_id) references `mfg_details` (`id`)
);

create table `player_details_days_played`
(
    id          bigint       not null,
    days_played varchar(255) null,
    constraint FK3i76wasevr1vsa4g5ylv4kpjr
        foreign key (id) references `player_details` (`id`)
);

create table `summary`
(
    id                bigint auto_increment
        primary key,
    player_details_id bigint null,
    constraint FKg9r00sdyuy7cmnxf2xe38on7
        foreign key (player_details_id) references `player_details` (`id`)
);

create table `games_availability`
(
    id         bigint auto_increment
        primary key,
    game_key   varchar(255) null,
    player_id  bigint       null,
    status     varchar(255) null,
    version    int          not null,
    game_id    bigint       null,
    summary_id bigint       null,
    user_id    bigint       null,
    creation_date date       null,
    constraint FK448euh40e2m32hewpx2aiv9s6
        foreign key (game_id) references `game` (`id`),
    constraint FKkdg3bob0vk7a9f8hgd25dtq2s
        foreign key (summary_id) references `summary` (`id`),
    constraint FKray8orf4qbijglh0bhcua6dut
        foreign key (user_id) references `user` (`id`)
);

create table `prize`
(
    id         bigint auto_increment
        primary key,
    amount     varchar(255) null,
    prize_type varchar(255) null
);

create table `wins`
(
    id                bigint auto_increment
        primary key,
    screen_name       varchar(255) null,
    timestamp         varchar(255) null,
    prize_id          bigint       null,
    player_details_id bigint       null,
    constraint FKax7034pxmy279kq3y2r64d3x9
        foreign key (prize_id) references `prize` (`id`),
    constraint FKm1ldq7pwxlbmrls7rlehjjrhd
        foreign key (player_details_id) references `player_details` (`id`)
);
CREATE INDEX `idx_creation_date` ON `games_availability` (`creation_date`) ALGORITHM INPLACE LOCK NONE;

CREATE INDEX `idx_created_date` ON `game_play` (`created_date`) ALGORITHM INPLACE LOCK NONE;





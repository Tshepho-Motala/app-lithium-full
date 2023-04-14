ALTER TABLE `game`
    ADD COLUMN `exclude_recently_played` BIT(1) default 0, ALGORITHM INPLACE, LOCK NONE;

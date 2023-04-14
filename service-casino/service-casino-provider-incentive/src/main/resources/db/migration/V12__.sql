ALTER TABLE `pick_any_entry_pick`
    MODIFY `away_score` bigint(20) NULL,
    MODIFY `home_score` bigint(20) NULL,
    ALGORITHM INPLACE,
    LOCK NONE;
ALTER TABLE `pick_any_settlement_pick`
    MODIFY `away_score_result` bigint(20) NULL,
    MODIFY `home_score_result` bigint(20) NULL,
    ALGORITHM INPLACE,
    LOCK NONE;

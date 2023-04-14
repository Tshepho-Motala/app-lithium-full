CREATE UNIQUE INDEX `idx_gg_deleted_enabled_gameid_function_livecasino`
    ON `game_graphic` (`deleted`, `enabled`, `game_id`, `graphic_function_id`, `live_casino`) ALGORITHM INPLACE LOCK NONE;
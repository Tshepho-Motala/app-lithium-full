ALTER TABLE `supplier_game_meta_results`
    ADD COLUMN `supplier_game_meta_results_id` BIGINT(20),
    ADD CONSTRAINT fk_supplier_game_meta_results FOREIGN KEY (`supplier_game_meta_results_id`) REFERENCES `supplier_game_meta_results` (`id`);
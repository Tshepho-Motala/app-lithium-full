package lithium.service.casino.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.casino.data.entities.GameCategory;

public interface GameCategoryRepository extends PagingAndSortingRepository<GameCategory, Long> {
	GameCategory findByGameCategoriesContainingIgnoreCase(String category);
	GameCategory findByCasinoCategory(String category);
}
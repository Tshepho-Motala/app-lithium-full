package lithium.service.user.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.user.data.entities.Category;

public interface CategoryRepository extends PagingAndSortingRepository<Category, Long> {
	Category findByName(String name);

  default Category findOne(Long id) {
    return findById(id).orElse(null);
  }
}

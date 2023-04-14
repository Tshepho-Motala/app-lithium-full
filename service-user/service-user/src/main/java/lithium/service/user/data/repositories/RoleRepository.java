package lithium.service.user.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import lithium.service.user.data.entities.Role;

@RepositoryRestResource(collectionResourceRel = "roles", path = "roleRepo")
public interface RoleRepository extends PagingAndSortingRepository<Role, Long> {
	Role findByRole(String role);

  default Role findOne(Long id) {
    return findById(id).orElse(null);
  }
}

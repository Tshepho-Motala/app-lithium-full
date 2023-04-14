package lithium.service.domain.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.domain.data.entities.Role;

public interface RoleRepository extends PagingAndSortingRepository<Role, Long> {
	Role findByRole(String role);
}

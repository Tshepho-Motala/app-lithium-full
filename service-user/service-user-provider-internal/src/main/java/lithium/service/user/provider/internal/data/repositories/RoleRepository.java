package lithium.service.user.provider.internal.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.user.provider.internal.data.entities.Role;

public interface RoleRepository extends PagingAndSortingRepository<Role, Long> {
	Role findByRole(String role);
}

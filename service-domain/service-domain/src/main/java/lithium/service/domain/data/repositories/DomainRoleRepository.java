package lithium.service.domain.data.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.domain.data.entities.Domain;
import lithium.service.domain.data.entities.DomainRole;
import lithium.service.domain.data.entities.Role;

public interface DomainRoleRepository extends PagingAndSortingRepository<DomainRole, Long> {
	DomainRole findByDomainAndRole(Domain domain, Role role);
	List<DomainRole> findByDomainId(Long domainId);
	List<DomainRole> findByDomainNameAndDeletedFalseOrderByRole(String domainName);
}
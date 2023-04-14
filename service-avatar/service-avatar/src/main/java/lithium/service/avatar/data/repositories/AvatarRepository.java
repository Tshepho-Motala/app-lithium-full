package lithium.service.avatar.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.avatar.data.entities.Avatar;

public interface AvatarRepository extends PagingAndSortingRepository<Avatar, Long>, JpaSpecificationExecutor<Avatar> {
	List<Avatar> findByDomainName(String domainName);
	List<Avatar> findByDomainNameAndEnabledTrue(String domainName);
	Avatar findByDomainNameAndIsDefaultTrue(String domainName);

	default Avatar findOne(Long id) {
		return findById(id).orElse(null);
	}

}

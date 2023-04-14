package lithium.service.cashier.data.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.cashier.data.entities.Profile;

public interface ProfileRepository extends PagingAndSortingRepository<Profile, Long> {
	List<Profile> findByDomainName(String domainName);
	Profile findByDomainNameAndCode(String domainName, String code);
	Profile findByDomainNameAndName(String domainName, String name);

	default Profile findOne(Long id) {
		return findById(id).orElse(null);
	}
}

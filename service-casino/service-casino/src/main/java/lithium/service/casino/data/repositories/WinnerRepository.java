package lithium.service.casino.data.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.casino.data.entities.Winner;

public interface WinnerRepository extends PagingAndSortingRepository<Winner, Long> {
	
	List<Winner> findTop50ByDomainNameOrderByCreatedDateDesc(String domainName);
	
	List<Winner> findByCreatedDateBefore(Date cutoffDate);
	
	List<Winner> findTop50ByDomainNameOrderByIdDesc(String domainName);
	
	List<Winner> findByIdLessThanAndDomainName(Long id, String domainName);
	
	Winner findFirst1ByDomainNameAndUserName(String domainName, String userName);
	
	Winner findTop1ByDomainNameOrderByCreatedDateDesc(String domainName);
}
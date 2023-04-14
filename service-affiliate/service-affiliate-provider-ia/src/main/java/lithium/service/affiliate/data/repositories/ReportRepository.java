package lithium.service.affiliate.data.repositories;

import java.util.List;

import lithium.service.affiliate.data.entities.Report;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ReportRepository extends PagingAndSortingRepository<Report, Long>, JpaSpecificationExecutor<Report> {
	
	List<Report> findByCurrentNameAndDomainName(String name, String domainName);

	List<Report> findByEnabled(Boolean enabled);

	default Report findOne(Long id) {
		return findById(id).orElse(null);
	}


}

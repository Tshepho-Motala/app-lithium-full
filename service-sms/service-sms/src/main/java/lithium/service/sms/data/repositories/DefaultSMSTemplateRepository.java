package lithium.service.sms.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.sms.data.entities.DefaultSMSTemplate;

public interface DefaultSMSTemplateRepository extends PagingAndSortingRepository<DefaultSMSTemplate, Long>, JpaSpecificationExecutor<DefaultSMSTemplate> {
	DefaultSMSTemplate findByName(String name);
	default DefaultSMSTemplate findOne(Long id) {
		return findById(id).orElse(null);
	}
}
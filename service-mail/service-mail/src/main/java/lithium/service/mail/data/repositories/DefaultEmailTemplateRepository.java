package lithium.service.mail.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.mail.data.entities.DefaultEmailTemplate;

public interface DefaultEmailTemplateRepository extends PagingAndSortingRepository<DefaultEmailTemplate, Long>, JpaSpecificationExecutor<DefaultEmailTemplate> {
	DefaultEmailTemplate findByName(String name);

	default DefaultEmailTemplate findOne(Long id) {
		return findById(id).orElse(null);
	}
}
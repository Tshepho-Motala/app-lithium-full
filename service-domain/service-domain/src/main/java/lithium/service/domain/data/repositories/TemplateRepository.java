package lithium.service.domain.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.domain.data.entities.Template;

public interface TemplateRepository extends PagingAndSortingRepository<Template, Long>, JpaSpecificationExecutor<Template> {
	Template findByDomainNameAndNameAndLang(String domainName, String name, String lang);
	List<Template> findByDomainNameAndLang(String domainName, String lang);

  default Template findOne(Long id) {
    return findById(id).orElse(null);
  }
}

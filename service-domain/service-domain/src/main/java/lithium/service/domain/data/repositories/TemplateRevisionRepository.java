package lithium.service.domain.data.repositories;

import lithium.service.domain.data.entities.Template;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.domain.data.entities.TemplateRevision;

public interface TemplateRevisionRepository extends PagingAndSortingRepository<TemplateRevision, Long>, JpaSpecificationExecutor<TemplateRevision> {
  @Modifying
  @Query("DELETE FROM #{#entityName} t WHERE t.template = :template")
  void deleteByTemplate(@Param("template") Template template);

  default TemplateRevision findOne(Long id) {
    return findById(id).orElse(null);
  }
}

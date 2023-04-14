package lithium.service.user.data.repositories;

import java.util.List;
import lithium.service.user.data.entities.ClosureReason;
import lithium.service.user.data.projections.ClosureReasonProjection;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ClosureReasonRepository extends PagingAndSortingRepository<ClosureReason, Long>, JpaSpecificationExecutor<ClosureReason> {
    List<ClosureReasonProjection> findAllByDomainNameAndDeleted(String domainName, Boolean deleted);
    ClosureReasonProjection findByIdAndDomainName(Long id, String domainName);
    ClosureReasonProjection findByIdAndDomainNameAndDeletedFalse(Long id, String domainName);
  default ClosureReason findOne(Long id) {
    return findById(id).orElse(null);
  }
}

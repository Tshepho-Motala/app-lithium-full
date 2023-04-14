package lithium.service.cashier.data.repositories;

import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.ProcessorUserCard;
import lithium.service.cashier.data.entities.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ProcessorUserCardRepository extends PagingAndSortingRepository<ProcessorUserCard, Long> {
    List<ProcessorUserCard> findByUserAndDomainMethodProcessor(User user, DomainMethodProcessor domainMethodProcessor);
    List<ProcessorUserCard> findByUserIdAndDomainMethodProcessorIdIn(Long userId, List<Long> domainMethodProcessorIds);
    List<ProcessorUserCard> findByUser(User user);
    List<ProcessorUserCard> findByReference(String reference);
    ProcessorUserCard findByUserAndReference(User user, String reference);
    ProcessorUserCard findByUserAndFingerprint(User user, String fingerprint);
    List<ProcessorUserCard> findByReferenceAndTypeNameAndDomainMethodProcessorDomainMethodDomainName(String reference, String type, String domainName);
    List<ProcessorUserCard> findByUserGuidAndTypeName(String guid, String type);
    List<ProcessorUserCard> findByFingerprint(String fingerprint);
    Long countByUserIdAndStatusId(Long userId, Integer statusId);
    List<ProcessorUserCard> findByUserAndContraAccountTrueAndVerifiedTrue(User user);
    List<ProcessorUserCard> findByUserGuidAndContraAccountTrueAndVerifiedTrue(String userGuid);
    Long countByUserIdAndDomainMethodProcessorIdAndStatusName(Long userId, Long domainMethodProcessorId, String status);

    default ProcessorUserCard findOne(Long id) {
        return findById(id).orElse(null);
    }

}

package lithium.service.sms.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.sms.data.entities.SMSTemplateRevision;

public interface SMSTemplateRevisionRepository extends PagingAndSortingRepository<SMSTemplateRevision, Long> {
    default SMSTemplateRevision findOne(Long id) {
        return findById(id).orElse(null);
    }
}
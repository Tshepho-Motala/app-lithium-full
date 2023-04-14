package lithium.service.pushmsg.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.pushmsg.data.entities.PushMsgTemplateRevision;

public interface PushMsgTemplateRevisionRepository extends PagingAndSortingRepository<PushMsgTemplateRevision, Long> {
    default PushMsgTemplateRevision findOne(Long id) {
        return findById(id).orElse(null);
    }
}
package lithium.service.user.provider.sphonic.idin.storage.repositories;

import lithium.service.user.provider.sphonic.idin.storage.entities.IDINRequest;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDINRequestReposistory extends PagingAndSortingRepository<IDINRequest, Long> {
    IDINRequest findIDINRequestByIdinApplicantHash(String idinApplicantHash);
}

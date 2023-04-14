package lithium.service.kyc.provider.onfido.repositories;

import lithium.service.kyc.provider.onfido.entitites.UserApplicant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserApplicantRepository extends JpaRepository<UserApplicant, String> {
    Optional<UserApplicant> findByUserGuid(String userGuid);
    Optional<UserApplicant> findByApplicantId(String applicantId);
}
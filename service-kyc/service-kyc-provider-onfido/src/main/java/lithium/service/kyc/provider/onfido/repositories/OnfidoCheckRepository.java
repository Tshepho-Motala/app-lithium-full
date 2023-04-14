package lithium.service.kyc.provider.onfido.repositories;

import lithium.service.kyc.provider.onfido.entitites.OnfidoCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OnfidoCheckRepository extends JpaRepository<OnfidoCheck, String> {
    Optional<OnfidoCheck> findByCheckId(String checkId);
    Optional<OnfidoCheck> findFirstByApplicantIdOrderByIdDesc(String applicantId);
}
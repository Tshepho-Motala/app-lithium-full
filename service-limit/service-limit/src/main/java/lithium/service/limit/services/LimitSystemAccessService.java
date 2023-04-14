package lithium.service.limit.services;

import lithium.service.limit.data.entities.LimitSystemAccess;
import lithium.service.limit.data.entities.VerificationStatus;
import lithium.service.limit.data.repositories.LimitsSystemAccessRepository;
import lithium.service.limit.data.repositories.VerificationStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * The functionality in this class deals with restrictions based on verification status.
 * Perhaps "VerificationStatusRestriction" would be easier to read rather than "LimitSystemAccess".
 */
@Service
public class LimitSystemAccessService {
	@Autowired private LimitsSystemAccessRepository limitsSystemAccessRepository;
	@Autowired private VerificationStatusRepository verificationStatusRepository;

	public static final Long UNVERIFIED_LEVEL_ID = 1L;

	public Iterable<LimitSystemAccess> getListLimits(lithium.service.limit.data.entities.Domain domain) throws Exception {
		String domainName = domain.getName();
		Iterable<VerificationStatus> verificationStatuses = verificationStatusRepository.findAll();
		Iterable<LimitSystemAccess> limits = limitsSystemAccessRepository.findAllByDomainName(domainName);
		List<LimitSystemAccess> newlimits = new ArrayList<>();
		StreamSupport.stream(verificationStatuses.spliterator(), false).forEach(
				verificationStatus -> {
					long verificationStatusId = verificationStatus.getId();
					boolean limitNotExists = StreamSupport.stream(limits.spliterator(), false).noneMatch(limitSystemAccess ->
							limitSystemAccess.getVerificationStatus().getId() == verificationStatusId
					);
					if (limitNotExists) {
						LimitSystemAccess limitSystemAccess = LimitSystemAccess.builder()
								.domainName(domainName)
								.verificationStatus(verificationStatus)
								.login(true)
								.deposit(true)
								.withdraw(true)
								.betPlacement(true)
								.casino(true)
								.build();
						newlimits.add(limitSystemAccess);
						limitsSystemAccessRepository.save(limitSystemAccess);
					}
				}
		);

		if (newlimits.isEmpty()) {
			return limits;
		}

		return limitsSystemAccessRepository.findAllByDomainName(domainName);
	}
}

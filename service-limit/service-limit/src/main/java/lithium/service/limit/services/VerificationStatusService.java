package lithium.service.limit.services;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.objects.VerificationStatusDto;
import lithium.service.limit.data.entities.VerificationStatus;
import lithium.service.limit.data.repositories.VerificationStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class VerificationStatusService {
	@Autowired private VerificationStatusRepository verificationStatusRepository;
	@Autowired private CachingDomainClientService cachingDomainClientService;


	public VerificationStatus getVerificationStatus(Long verificationStatusId) throws Status500InternalServerErrorException {
		lithium.service.limit.data.entities.VerificationStatus verificationStatus = verificationStatusRepository.findOne(verificationStatusId);
		if (verificationStatus == null) {
			throw new Status500InternalServerErrorException("VerificationStatus not found [verificationStatusId=" + verificationStatusId + "]");
		}
		return verificationStatus;
	}

	public Integer getVerificationStatusLevel(Long verificationStatusId, String domainName) throws Status500InternalServerErrorException, Status550ServiceDomainClientException {
		VerificationStatus status = getVerificationStatus(verificationStatusId);
		return lithium.service.limit.client.objects.VerificationStatus.AGE_ONLY_VERIFIED.getId() == status.getId()
			? cachingDomainClientService.getAgeOnlyVerifiedStatusLevel(domainName).orElse(status.getLevel())
			: status.getLevel();
	}

	public List<VerificationStatusDto> getAllVerificationStatuses(){
		return StreamSupport.stream(verificationStatusRepository.findAll().spliterator(), false)
				.map(this::convert)
				.collect(Collectors.toList());
	}
	private VerificationStatusDto convert(VerificationStatus verificationStatus){
		return VerificationStatusDto.builder()
				.id(verificationStatus.getId())
				.code(verificationStatus.getCode())
				.level(verificationStatus.getLevel())
				.build();
	}

}

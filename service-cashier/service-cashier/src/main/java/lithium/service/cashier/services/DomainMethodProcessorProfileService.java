package lithium.service.cashier.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.DomainMethodProcessorProfile;
import lithium.service.cashier.data.entities.Fees;
import lithium.service.cashier.data.entities.Limits;
import lithium.service.cashier.data.entities.Profile;
import lithium.service.cashier.data.repositories.DomainMethodProcessorProfileRepository;
import lithium.service.client.datatable.DataTableRequest;

//@Slf4j
@Service
public class DomainMethodProcessorProfileService {
	@Autowired
	private DomainMethodProcessorProfileRepository domainMethodProcessorProfileRepository;
	@Autowired
	private FeesService feesService;
	@Autowired
	private LimitsService limitsService;
	@Autowired
	private ProfileService profileService;
	@Autowired
	private DomainMethodProcessorService domainMethodProcessorService;
	
	public DomainMethodProcessorProfile findByDomainMethodProcessorAndProfile(DomainMethodProcessor domainMethodProcessor, Profile profile) {
		return domainMethodProcessorProfileRepository.findByDomainMethodProcessorAndProfile(domainMethodProcessor, profile);
	}
	
	public DomainMethodProcessorProfile find(Long domainMethodProcessorProfileId) {
		return domainMethodProcessorProfileRepository.findOne(domainMethodProcessorProfileId);
	}
	
	public List<DomainMethodProcessorProfile> findByProfile(Profile profile) {
		return domainMethodProcessorProfileRepository.findByProfile(profile);
	}
	
	public List<DomainMethodProcessorProfile> findByDomainMethodProcessor(DomainMethodProcessor domainMethodProcessor) {
		return domainMethodProcessorProfileRepository.findByDomainMethodProcessor(domainMethodProcessor);
	}
	public Page<DomainMethodProcessorProfile> findByDomainMethodProcessor(DataTableRequest request, DomainMethodProcessor domainMethodProcessor) {
		return domainMethodProcessorProfileRepository.findByDomainMethodProcessor(domainMethodProcessor, request.getPageRequest());
	}
	
//	public List<DomainMethodProcessorProfile> findByDomainMethodProfile(DomainMethodProfile domainMethodProfile) {
//		return domainMethodProcessorProfileRepository.findByDomainMethodProfile(domainMethodProfile);
//	}
	
	public DomainMethodProcessorProfile createOrUpdate(DomainMethodProcessorProfile domainMethodProcessorProfile) throws Exception {
		Fees fees = null;
		Limits limits = null;
		if (domainMethodProcessorProfile.getFees() != null) {
			fees = feesService.create(domainMethodProcessorProfile.getFees());
		}
		if (domainMethodProcessorProfile.getLimits() != null) {
			limits = limitsService.create(domainMethodProcessorProfile.getLimits());
		}
		
		Profile profile = profileService.find(domainMethodProcessorProfile.getProfile().getId());
		if (profile == null) throw new Exception();
		DomainMethodProcessor domainMethodProcessor = domainMethodProcessorService.find(domainMethodProcessorProfile.getDomainMethodProcessor().getId());
		if (domainMethodProcessor == null) throw new Exception();
		
		domainMethodProcessorProfile.setFees(fees);
		domainMethodProcessorProfile.setLimits(limits);
		domainMethodProcessorProfile.setProfile(profile);
		domainMethodProcessorProfile.setDomainMethodProcessor(domainMethodProcessor);
		
		return domainMethodProcessorProfileRepository.save(domainMethodProcessorProfile);
	}
	
	
//	public DomainMethodProcessorProfile createOrUpdate(
//		Long domainMethodProcessorId,
//		Long profileId,
//		Long feeFlat,
//		BigDecimal feePercentage,
//		Long feeMinimum,
//		Long limitMinAmount,
//		Long limitMaxAmount,
//		Long limitMaxAmountDay,
//		Long limitMaxAmountWeek,
//		Long limitMaxAmountMonth,
//		Long limitMaxTransactionsDay,
//		Long limitMaxTransactionsWeek,
//		Long limitMaxTransactionsMonth,
//		Double weight,
//		Boolean enabled
//	) throws Exception {
//		log.trace("createOrUpdate");
//		DomainMethodProcessorProfile domainMethodProcessorProfile = findByDomainMethodProcessorIdAndProfileId(domainMethodProcessorId, profileId);
//		if (domainMethodProcessorProfile == null) {
//			//create
//			DomainMethodProcessor domainMethodProcessor = domainMethodProcessorService.find(domainMethodProcessorId);
//			if (domainMethodProcessor == null) throw new Exception("DomainMethodProcessor does not exist.");
//			Profile profile = profileService.find(profileId);
//			if (profile == null) throw new Exception("Profile does not exist.");
//			Fees fees = feesService.create(feeFlat, feePercentage, feeMinimum);
//			if (fees == null) throw new Exception("Could not create new fee structure.");
//			Limits limits = limitsService.create(limitMinAmount, limitMaxAmount, limitMaxAmountDay, limitMaxAmountWeek, limitMaxAmountMonth, limitMaxTransactionsDay, limitMaxTransactionsWeek, limitMaxTransactionsMonth);
//			if (limits == null) throw new Exception("Could not create new limits structure.");
//			
//			domainMethodProcessorProfile = domainMethodProcessorProfileRepository.save(
//				DomainMethodProcessorProfile.builder()
//				.domainMethodProcessor(domainMethodProcessor)
//				.profile(profile)
//				.fees(fees)
//				.limits(limits)
//				.weight(weight)
//				.enabled(enabled)
//				.build()
//			);
//		} else {
//			//update
//			Fees fees = feesService.update(
//				domainMethodProcessorProfile.getFees().getId(),
//				feeFlat, feePercentage, feeMinimum
//			);
//			Limits limits = limitsService.update(
//				domainMethodProcessorProfile.getLimits().getId(),
//				limitMinAmount,
//				limitMaxAmount,
//				limitMaxAmountDay,
//				limitMaxAmountWeek,
//				limitMaxAmountMonth,
//				limitMaxTransactionsDay,
//				limitMaxTransactionsWeek,
//				limitMaxTransactionsMonth
//			);
//			domainMethodProcessorProfile.setFees(fees);
//			domainMethodProcessorProfile.setLimits(limits);
//			domainMethodProcessorProfile = domainMethodProcessorProfileRepository.save(domainMethodProcessorProfile);
//		}
//		return domainMethodProcessorProfile;
//	}
	
	public DomainMethodProcessorProfile save(DomainMethodProcessorProfile domainMethodProcessorProfile) {
		return domainMethodProcessorProfileRepository.save(domainMethodProcessorProfile);
	}
	public DomainMethodProcessorProfile saveFees(DomainMethodProcessorProfile domainMethodProcessorProfile, Fees fees) {
		fees = feesService.create(fees);
		domainMethodProcessorProfile.setFees(fees);
		domainMethodProcessorProfile = save(domainMethodProcessorProfile);
		return domainMethodProcessorProfile;
	}
	public DomainMethodProcessorProfile saveLimits(DomainMethodProcessorProfile domainMethodProcessorProfile, Limits limits) {
		limits = limitsService.create(limits);
		domainMethodProcessorProfile.setLimits(limits);
		domainMethodProcessorProfile = save(domainMethodProcessorProfile);
		return domainMethodProcessorProfile;
	}
	
	public DomainMethodProcessorProfile deleteFees(DomainMethodProcessorProfile domainMethodProcessorProfile) {
		feesService.delete(domainMethodProcessorProfile.getFees());
		domainMethodProcessorProfile.setFees(null);
		return save(domainMethodProcessorProfile);
	}
	public DomainMethodProcessorProfile deleteLimits(DomainMethodProcessorProfile domainMethodProcessorProfile) {
		limitsService.delete(domainMethodProcessorProfile.getLimits());
		domainMethodProcessorProfile.setLimits(null);
		return save(domainMethodProcessorProfile);
	}
}
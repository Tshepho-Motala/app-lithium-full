package lithium.service.cashier.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.DomainMethodProcessorUser;
import lithium.service.cashier.data.entities.Fees;
import lithium.service.cashier.data.entities.Limits;
import lithium.service.cashier.data.entities.User;
import lithium.service.cashier.data.repositories.DomainMethodProcessorUserRepository;
import lithium.service.client.datatable.DataTableRequest;

//@Slf4j
@Service
public class DomainMethodProcessorUserService {
	@Autowired
	private DomainMethodProcessorUserRepository domainMethodProcessorUserRepository;
	@Autowired
	private FeesService feesService;
	@Autowired
	private LimitsService limitsService;
	@Autowired
	private UserService userService;
	@Autowired
	private DomainMethodProcessorService domainMethodProcessorService;
	
	public List<DomainMethodProcessorUser> findByUserGuid(String userGuid) {
		return domainMethodProcessorUserRepository.findByUserGuid(userGuid);
	}
	public List<DomainMethodProcessorUser> findByDomainMethodProcessor(DomainMethodProcessor domainMethodProcessor) {
		return domainMethodProcessorUserRepository.findByDomainMethodProcessor(domainMethodProcessor);
	}
	public DomainMethodProcessorUser findByDomainMethodProcessorAndUser(DomainMethodProcessor domainMethodProcessor, User user) {
		return domainMethodProcessorUserRepository.findByDomainMethodProcessorAndUser(domainMethodProcessor, user);
	}
	public Page<DomainMethodProcessorUser> findByDomainMethodProcessor(DataTableRequest request, DomainMethodProcessor domainMethodProcessor) {
		return domainMethodProcessorUserRepository.findByDomainMethodProcessor(domainMethodProcessor, request.getPageRequest());
	}
	
	public DomainMethodProcessorUser find(Long domainMethodProcessorUserId) {
		return domainMethodProcessorUserRepository.findOne(domainMethodProcessorUserId);
	}
	
//	public DomainMethodProcessorUser findByDomainMethodProcessorIdAndUserGuid(Long domainMethodProcessorId, String userGuid) {
//		return domainMethodProcessorUserRepository.findByDomainMethodProcessorIdAndUserGuid(domainMethodProcessorId, userGuid);
//	}
	
	public DomainMethodProcessorUser createOrUpdate(DomainMethodProcessorUser domainMethodProcessorUser) throws Exception {
		Fees fees = null;
		Limits limits = null;
		if (domainMethodProcessorUser.getFees() != null) fees = feesService.create(domainMethodProcessorUser.getFees());
//		if (fees == null) throw new Exception("Could not create new fee structure.");
		if (domainMethodProcessorUser.getLimits() != null) limits = limitsService.create(domainMethodProcessorUser.getLimits());
//		if (limits == null) throw new Exception("Could not create new limits structure.");
		
		User user = userService.findOrCreate(domainMethodProcessorUser.getUser().getGuid());
		DomainMethodProcessor domainMethodProcessor = domainMethodProcessorService.find(domainMethodProcessorUser.getDomainMethodProcessor().getId());
		
		domainMethodProcessorUser.setFees(fees);
		domainMethodProcessorUser.setLimits(limits);
		domainMethodProcessorUser.setUser(user);
		domainMethodProcessorUser.setDomainMethodProcessor(domainMethodProcessor);
		
		return domainMethodProcessorUserRepository.save(domainMethodProcessorUser);
	}
	
//	public DomainMethodProcessorUser createOrUpdate(
//		Long domainMethodProcessorId,
//		String userGuid,
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
//		DomainMethodProcessorUser domainMethodProcessorUser = findByDomainMethodProcessorIdAndUserGuid(domainMethodProcessorId, userGuid);
//		if (domainMethodProcessorUser == null) {
//			//create
//			DomainMethodProcessor domainMethodProcessor = domainMethodProcessorService.find(domainMethodProcessorId);
//			if (domainMethodProcessor == null) throw new Exception("DomainMethodProcessor does not exist.");
//			
//			Fees fees = feesService.create(feeFlat, feePercentage, feeMinimum);
//			if (fees == null) throw new Exception("Could not create new fee structure.");
//			Limits limits = limitsService.create(limitMinAmount, limitMaxAmount, limitMaxAmountDay, limitMaxAmountWeek, limitMaxAmountMonth, limitMaxTransactionsDay, limitMaxTransactionsWeek, limitMaxTransactionsMonth);
//			if (limits == null) throw new Exception("Could not create new limits structure.");
//			
//			User user = userService.findOrCreate(userGuid);
//			
//			domainMethodProcessorUser = domainMethodProcessorUserRepository.save(
//				DomainMethodProcessorUser.builder()
//				.domainMethodProcessor(domainMethodProcessor)
//				.user(user)
//				.fees(fees)
//				.limits(limits)
//				.weight(weight)
//				.enabled(enabled)
//				.build()
//			);
//		} else {
//			//update
//			Fees fees = feesService.update(
//				domainMethodProcessorUser.getFees().getId(),
//				feeFlat, feePercentage, feeMinimum
//			);
//			Limits limits = limitsService.update(
//				domainMethodProcessorUser.getLimits().getId(),
//				limitMinAmount,
//				limitMaxAmount,
//				limitMaxAmountDay,
//				limitMaxAmountWeek,
//				limitMaxAmountMonth,
//				limitMaxTransactionsDay,
//				limitMaxTransactionsWeek,
//				limitMaxTransactionsMonth
//			);
//			domainMethodProcessorUser.setFees(fees);
//			domainMethodProcessorUser.setLimits(limits);
//			domainMethodProcessorUser = domainMethodProcessorUserRepository.save(domainMethodProcessorUser);
//		}
//		return domainMethodProcessorUser;
//	}
	
	public DomainMethodProcessorUser save(DomainMethodProcessorUser domainMethodProcessorUser) {
		return domainMethodProcessorUserRepository.save(domainMethodProcessorUser);
	}
	public DomainMethodProcessorUser saveFees(DomainMethodProcessorUser domainMethodProcessorUser, Fees fees) {
		fees = feesService.create(fees);
		domainMethodProcessorUser.setFees(fees);
		domainMethodProcessorUser = save(domainMethodProcessorUser);
		return domainMethodProcessorUser;
	}
	public DomainMethodProcessorUser saveLimits(DomainMethodProcessorUser domainMethodProcessorUser, Limits limits) {
		limits = limitsService.create(limits);
		domainMethodProcessorUser.setLimits(limits);
		domainMethodProcessorUser = save(domainMethodProcessorUser);
		return domainMethodProcessorUser;
	}
	
	public DomainMethodProcessorUser deleteFees(DomainMethodProcessorUser domainMethodProcessorUser) {
		feesService.delete(domainMethodProcessorUser.getFees());
		domainMethodProcessorUser.setFees(null);
		return save(domainMethodProcessorUser);
	}
	public DomainMethodProcessorUser deleteLimits(DomainMethodProcessorUser domainMethodProcessorUser) {
		limitsService.delete(domainMethodProcessorUser.getLimits());
		domainMethodProcessorUser.setLimits(null);
		return save(domainMethodProcessorUser);
	}
}
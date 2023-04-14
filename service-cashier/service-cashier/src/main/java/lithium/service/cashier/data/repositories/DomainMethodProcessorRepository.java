package lithium.service.cashier.data.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.cashier.data.entities.DomainMethodProcessor;

public interface DomainMethodProcessorRepository extends PagingAndSortingRepository<DomainMethodProcessor, Long> {
	DomainMethodProcessor findByDomainMethodIdAndProcessorId(Long domainMethodId, Long processorId);
	List<DomainMethodProcessor> findByDomainMethodDomainNameAndDomainMethodMethodCode(String domainName, String methodCode);
	List<DomainMethodProcessor> findByDomainMethodId(Long domainMethodId);
	List<DomainMethodProcessor> findByDomainMethodIdAndDeletedFalseOrderByWeightDesc(Long domainMethodId);
	List<DomainMethodProcessor> findByDomainMethodIdAndDeletedFalse(Long domainMethodId);
	List<DomainMethodProcessor> findByDomainMethodIdAndEnabledTrueAndDeletedFalse(Long domainMethodId);
	List<DomainMethodProcessor> findByDomainMethodDomainNameAndDomainMethodDepositAndDomainMethodDeletedFalseAndDomainMethodEnabledTrueAndDeletedFalseAndEnabledTrue(String domainName, boolean deposit);
	DomainMethodProcessor findByDomainMethodDomainNameAndDomainMethodDepositAndDomainMethodMethodCodeAndDescriptionAndEnabledAndDomainMethodEnabledAndProcessorEnabled(
			String domainName, Boolean deposit, String methodCode, String description, Boolean enabled, Boolean enabledDomainMethod, Boolean enabledProcessor);
	List<DomainMethodProcessor> findByDomainMethodDomainNameAndDomainMethodDepositAndDomainMethodMethodCodeAndEnabledAndDomainMethodEnabledAndProcessorEnabled(
			String domainName, Boolean deposit, String methodCode,Boolean enabled, Boolean enabledDomainMethod, Boolean enabledProcessor);

	default DomainMethodProcessor findOne(Long id) {
		return findById(id).orElse(null);
	}

	DomainMethodProcessor findFirstByEnabledTrueAndDomainMethodDomainNameAndDomainMethodMethodCodeAndProcessorCode(
			String domainName, String methodCode, String processorCode);

}

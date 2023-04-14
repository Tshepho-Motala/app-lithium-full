package lithium.service.cashier.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.cashier.data.entities.Domain;
import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.data.entities.Method;

public interface DomainMethodRepository extends PagingAndSortingRepository<DomainMethod, Long>, JpaSpecificationExecutor<DomainMethod> {
	DomainMethod findByMethodCodeAndDomainName(String code, String domainName);
	List<DomainMethod> findByDomainNameAndDeletedFalseAndEnabledTrueOrderByPriorityDesc(String domainName);
	DomainMethod findByNameAndDomainNameAndMethodIdAndDeposit(String name, String domainName, Long methodId, Boolean deposit);
	List<DomainMethod> findByDomainOrderByPriority(Domain domain);
	List<DomainMethod> findByDomainAndDepositTrueAndDeletedFalseOrderByPriority(Domain domain);
	List<DomainMethod> findByDomainAndDepositFalseAndDeletedFalseOrderByPriority(Domain domain);
	List<DomainMethod> findByDomainAndDepositAndDeletedFalseOrderByPriority(Domain domain, Boolean deposit);
	List<DomainMethod> findByDomainAndDepositOrderByPriority(Domain domain, Boolean deposit);
	DomainMethod findByDomainAndMethodAndDeletedFalseAndName(Domain domain, Method method, String name);
	DomainMethod findByDomainAndMethodAndDeletedFalseAndNameAndDeposit(Domain domain, Method method, String name, Boolean deposit);
	List<DomainMethod> findByDomainNameAndMethodCodeAndDepositAndEnabledTrueAndDeletedFalse(String domainName, String methodCode, boolean deposit);
	List<DomainMethod> findByDomainNameAndDepositFalseAndDeletedFalse(String domainName);
	List<DomainMethod> findByDomainNameAndDepositFalseAndEnabledTrueAndDeletedFalse(String domainName);
	List<DomainMethod> findByDomainAndDeletedFalse(Domain domain);
	List<DomainMethod> findByDomain(Domain domain);

	default DomainMethod findOne(Long id) {
		return findById(id).orElse(null);
	}

}

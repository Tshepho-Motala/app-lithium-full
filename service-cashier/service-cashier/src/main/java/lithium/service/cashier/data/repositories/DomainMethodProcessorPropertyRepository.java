package lithium.service.cashier.data.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.cashier.data.entities.DomainMethodProcessorProperty;

public interface DomainMethodProcessorPropertyRepository extends PagingAndSortingRepository<DomainMethodProcessorProperty, Long> {
	List<DomainMethodProcessorProperty> findByDomainMethodProcessorIdOrderByProcessorPropertyName(Long domainMethodProcessorId);
	List<DomainMethodProcessorProperty> findByDomainMethodProcessorIdAndProcessorPropertyTypeOrderByProcessorPropertyName(Long domainMethodProcessorId, String processorPropertyType);
	DomainMethodProcessorProperty findByDomainMethodProcessorIdAndProcessorPropertyId(Long domainMethodProcessorId, Long processorPropertyId);

	default DomainMethodProcessorProperty findOne(Long id) {
		return findById(id).orElse(null);
	}

}

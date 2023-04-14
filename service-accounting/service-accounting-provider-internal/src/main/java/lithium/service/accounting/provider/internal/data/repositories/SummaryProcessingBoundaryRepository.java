package lithium.service.accounting.provider.internal.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.accounting.provider.internal.data.entities.SummaryProcessingBoundary;


public interface SummaryProcessingBoundaryRepository extends PagingAndSortingRepository<SummaryProcessingBoundary, Long> {
	SummaryProcessingBoundary findFirstBySummaryType(int summaryType);
}
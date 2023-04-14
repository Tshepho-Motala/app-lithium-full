package lithium.service.settlement.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.settlement.data.entities.SettlementPDF;

public interface SettlementPDFRepository extends PagingAndSortingRepository<SettlementPDF, Long>, JpaSpecificationExecutor<SettlementPDF> {
}

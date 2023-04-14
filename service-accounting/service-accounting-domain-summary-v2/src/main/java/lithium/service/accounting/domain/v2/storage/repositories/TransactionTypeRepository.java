package lithium.service.accounting.domain.v2.storage.repositories;

import lithium.service.accounting.domain.v2.storage.entities.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionTypeRepository extends JpaRepository<TransactionType, Long> {
	TransactionType findByCode(String code);
}

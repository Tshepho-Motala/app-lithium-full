package lithium.service.accounting.provider.internal.data.repositories;

import lithium.service.accounting.provider.internal.data.entities.TransactionLabelValue;
import lithium.service.accounting.provider.internal.data.projection.entities.TransactionLabelValueProjection;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionLabelValueRepository extends PagingAndSortingRepository<TransactionLabelValue, Long>, JpaSpecificationExecutor<TransactionLabelValue> {

	@Query(value=
			"select tlv.transactionId as transactionId, lv.value as labelValue, lb.name as labelName from TransactionLabelValue tlv "
					+"inner join tlv.labelValue lv "
					+"inner join lv.label lb "
					+"where tlv.transactionId = :transactionId "
	)
	List<TransactionLabelValueProjection> findByTransactionIdProjection(@Param("transactionId") Long transactionId);

	List<TransactionLabelValue> findByTransactionId(Long transactionId);

	TransactionLabelValue findFirstByIdGreaterThanOrderByTransactionId(long id);
	@Query("SELECT MIN(o.transactionId) FROM #{#entityName} o WHERE o.id > :id")
	Long findMinTransactionIdGreaterThanId(@Param("id") Long id);

	// I'm not adding a unique index on transaction_id + label_value_id because this table is massive.
	// As of 23rd July, on ls-prod,
	// select id from transaction_label_value order by id desc limit 1;
	// 508804567
	// It's possible we may already have dupes, hence the list...
	List<TransactionLabelValue> findByTransactionIdAndLabelValueId(Long transactionId, Long labelValueId);
	Long deleteByTransactionIdIn(List<Long> transactionIds);

	default TransactionLabelValue findOne(Long id) {
		return findById(id).orElse(null);
	}
}

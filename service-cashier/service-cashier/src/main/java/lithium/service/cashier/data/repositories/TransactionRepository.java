package lithium.service.cashier.data.repositories;

import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
    List<Transaction> findByDomainMethodAndUserAndStatusActive(DomainMethod domainMethod, User user, boolean active);
	List<Transaction> findByUserAndStatusCodeIn(User user, List<String> statusIncludeList);
	Transaction findByProcessorReference(String processorReference);
	Transaction findByProcessorReferenceAndDomainMethod(String processorReference, DomainMethod domainMethod);
	Transaction findByAdditionalReference(String additionalReference);
	List<Transaction> findByTtlNotAndStatusCodeNotIn(Long ttl, List<String> statusExcludeList);

    List<Transaction> findByTtlNotAndStatusCodeIn(Long ttl, List<String> statusIncludeList);

    List<Transaction> findByRetryProcessingIsTrue();

    List<Transaction> findByBonusCodeIsNullAndAccountInfoIsNull();

    Transaction findTop1ByUserGuidAndTransactionTypeAndStatusCodeOrderByCurrentIdDesc(String userGuid, TransactionType transactionType, String currentStatusCode);

    Page<Transaction> findByTransactionTypeAndStatusCode(TransactionType transactionType, String currentStatusCode, Pageable pageable);

    List<Transaction> findByTransactionTypeAndStatusCode(TransactionType transactionType, String currentStatusCode);

    List<Transaction> findByTransactionTypeAndStatusCodeAndCurrentTimestampBefore(TransactionType transactionType, String currentStatusCode, Timestamp timestamp);

    Transaction findFirstByUserGuidAndTransactionTypeAndStatusCode(String userGuid, TransactionType transactionType, String currentStatusCode);

    Page<Transaction> findTop1ByStatusCodeAndPaymentMethodNullOrderByUserId(String currentStatusCode, Pageable pageable);
    Page<Transaction> findByStatusCodeAndPaymentMethodNullOrderByUserId(String currentStatusCode, Pageable pageable);
    List<Transaction> findByStatusCodeAndPaymentMethodNull(String currentStatusCode);

    List<Transaction> findByUserGuidAndTransactionTypeAndStatusCode (String userGuid, TransactionType transactionType, String currentStatusCode);
    List<Transaction> findByUserGuidAndTransactionTypeAndStatusCodeIn (String userGuid, TransactionType transactionType, List<String> statusCodes);
    List<Transaction> findByUserGuidAndTransactionType(String userGuid, TransactionType transactionType);

    Long countByUserGuidAndDomainMethodAndTransactionTypeAndStatusCode(String userGuid, DomainMethod domainMethod, TransactionType transactionType, String currentStatusCode);

    Page<Transaction> findByUserGuidOrderByIdDesc(String guid, Pageable pageable);
    List<Transaction> findByCurrentProcessTimeNotNullAndCurrentProcessTimeBefore(Date currentTime);
    List<Transaction> findByStatusActiveAndCurrentProcessTimeNotNullAndCurrentProcessTimeBefore(boolean active, Date currentTime);
    List<Transaction> findByUserGuidAndTransactionTypeAndStatusCodeAndPaymentMethodId(String userGuid, TransactionType transactionType, String currentStatusCode, Long paymentMethodId);

    default Transaction findOne(Long id) {
        return findById(id).orElse(null);
    }

    Long countByAmountCentsIsNullAndStatusActiveFalse();

    List<Transaction> findByAmountCentsIsNullAndStatusActiveFalse(Pageable page);

    Long countByUserGuidAndTransactionType(String userGuid, TransactionType transactionType);

    Long countByUserGuidAndTransactionTypeAndStatusCode(String userGuid, TransactionType transactionType, String statusCode);
    Optional<Transaction> findFirstByUserGuidAndPaymentMethodIdOrderByCreatedOn(String userGuid, Long paymentMethodId);
    Page<Transaction> findByUserGuidAndTransactionTypeAndStatusCodeInOrderByIdDesc (String userGuid, TransactionType transactionType, List<String> statusCodes, Pageable pageRequest);
}

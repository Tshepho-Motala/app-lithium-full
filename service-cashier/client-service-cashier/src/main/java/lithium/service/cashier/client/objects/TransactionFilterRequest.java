package lithium.service.cashier.client.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.function.Predicate.not;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionFilterRequest {

    private static final int ANY_DATE_MARKER = -1;
    private String dmp;
    private String dm;
    private String guid;
    private String domain;
    private String transactionType;
    private String status;
    private List<String> statuses = new ArrayList<>();
    private Long cresd; //createdStart,
    private Long creed; //createdEnd,
    private Long updsd; //updatedStart,
    private Long upded; //updatedEnd,
    private Long registrationStart;
    private Long registrationEnd;
    private String processorReference;
    private String additionalReference;
    private String paymentType;
    private String declineReason;
    private String lastFourDigits;
    private String id; // transactionId,
    private String transactionRuntimeQuery;
    private Boolean testAccount;
    private List<String> includedTransactionTagsNames = new ArrayList<>();
    private List<String> excludedTransactionTagsNames = new ArrayList<>();
    private String searchValue;
    private String depositCount;
    private String daysSinceFirstDeposit;
    private String transactionAmount;
    private String activePaymentMethodCount;
    private List<Long> userStatusIds;
    private List<Long> userTagIds;

    public DateTime getCreatedStart() {
        return from(this.cresd);
    }

    public DateTime getCreatedEnd() {
        return from(this.creed);
    }

    public DateTime getUpdatedStart() {
        return from(this.updsd);
    }

    public DateTime getUpdatedEnd() {
        return from(this.upded);
    }

    public DateTime getRegistrationStart() {
        return from(this.registrationStart);
    }

    public DateTime getRegistrationEnd() {
        return from(this.registrationEnd);
    }

    public List<String> getAllStatuses() {
        if (this.statuses==null) {
            this.statuses = new ArrayList<>();
        }
        if (!statuses.isEmpty()) {
            return statuses;
        }
        return Optional.ofNullable(status)
                .filter(not(String::isEmpty))
                .stream()
                .toList();
    }

    private static DateTime from(@Nullable Long timestamp) {
        return Optional.ofNullable(timestamp)
                .filter(isAnyDate())
                .map(DateTime::new)
                .orElse(null);
    }
    public List<String> getIncludedTransactionTagsNames () {
        return Objects.requireNonNullElseGet(this.includedTransactionTagsNames, ArrayList::new);
    }
    public List<String> getExcludedTransactionTagsNames () {
        return Objects.requireNonNullElseGet(this.excludedTransactionTagsNames, ArrayList::new);
    }
    private static Predicate<Long> isAnyDate() {
        return timestamp -> timestamp != ANY_DATE_MARKER;
    }
}

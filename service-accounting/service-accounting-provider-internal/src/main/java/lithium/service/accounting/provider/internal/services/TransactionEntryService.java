package lithium.service.accounting.provider.internal.services;

import lithium.cashier.CashierTransactionLabels;
import lithium.casino.CasinoTransactionLabels;
import lithium.exceptions.Status425DateParseException;
import lithium.service.accounting.objects.LabelValue;
import lithium.service.accounting.objects.TransactionEntryBO;
import lithium.service.accounting.objects.TransactionEntryBODetails;
import lithium.service.accounting.provider.internal.data.entities.TransactionEntry;
import lithium.service.accounting.provider.internal.data.entities.TransactionEntry_;
import lithium.service.accounting.provider.internal.data.repositories.TransactionEntryRepository;
import lithium.service.accounting.provider.internal.data.repositories.specifications.TransactionEntrySpecifications;
import lithium.service.client.util.LabelManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import static lithium.service.accounting.provider.internal.services.SummaryAccountTransactionTypeService.EXCLUDED_TRANSACTION_TYPES;

@Slf4j
@Service
public class TransactionEntryService {
    public static final String PLAYER_BALANCE_TYPE_CODE_NAME = "PLAYER_BALANCE";
    @Autowired
    private TransactionEntryRepository repository;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ModelMapper mapper;

    public Page<TransactionEntryBO> find(
            List<String> domains,
            String createdOnRangeStart,
            String createdOnRangeEnd,
            String userGuid,
            String transactionId,
            String searchValue,
            String provider,
            String providerTransId,
            List<String> transactionTypeCode,
            String additionalTransId,
            Pageable pageable,
            String accountCode,
            String roundId) throws Status425DateParseException {

        Specification<TransactionEntry> spec  = getTransactionEntrySpecificationsForAccountHistory(domains, createdOnRangeStart, createdOnRangeEnd, userGuid, transactionId, searchValue, provider, providerTransId, transactionTypeCode, additionalTransId, accountCode, roundId);

        Page<TransactionEntryBO> pageCO = repository.findAll(spec, pageable).map(this::toTranEntryCO);

        enrichPageData(pageCO);

        return pageCO;
    }

    private Specification<TransactionEntry> getTransactionEntrySpecificationsForAccountHistory(
            List<String> domains,
            String createdOnRangeStart,
            String createdOnRangeEnd,
            String userGuid,
            String transactionId,
            String searchValue,
            String provider,
            String providerTransId,
            List<String> transactionTypeCode,
            String additionalTransId,
            String accountCode,
            String roundId) throws Status425DateParseException {
        Specification<TransactionEntry> spec = null;
        Date dCreatedOnRangeStart = null;
        Date dCreatedOnRangeEnd = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dCreatedOnRangeStart = (!isNullOrEmpty(createdOnRangeStart)) ? sdf.parse(createdOnRangeStart) : new DateTime().minusDays(7).toDate();
            dCreatedOnRangeEnd = (!isNullOrEmpty(createdOnRangeEnd)) ? sdf.parse(createdOnRangeEnd) : new Date();
        } catch (ParseException e) {
            throw new Status425DateParseException("Failed to parse date for format yyyy-MM-dd | " + e.getMessage());
        }
        String[] accountCodeArr = (accountCode != null && !accountCode.trim().isEmpty()) ? accountCode.split(",") : null;

        spec = addToSpec(dCreatedOnRangeStart, false, spec, TransactionEntrySpecifications::dateRangeStart);
        spec = addToSpec(dCreatedOnRangeEnd, true, spec, TransactionEntrySpecifications::dateRangeEnd);
        spec = addToSpec(domains, spec, TransactionEntrySpecifications::domains);
        spec = addToSpec(userGuid, spec, TransactionEntrySpecifications::user);
        spec = addToSpec(transactionId, spec, TransactionEntrySpecifications::transactionIdStartsWith);
        spec = addToSpec(searchValue, spec, TransactionEntrySpecifications::any);
        spec = addToSpec(provider, CasinoTransactionLabels.PROVIDER_GUID_LABEL, spec, Boolean.TRUE);
        spec = addToSpec(providerTransId, CasinoTransactionLabels.TRAN_ID_LABEL, spec, Boolean.FALSE);
        spec = addToSpec(transactionTypeCode, spec, TransactionEntrySpecifications::transactionTypeCode);
        spec = addToSpec(additionalTransId, LabelManager.ADDITIONAL_REFERENCE_ID, spec, Boolean.FALSE);
        spec = addToSpec(accountCodeArr, spec, TransactionEntrySpecifications::accountCode);
        spec = addToSpec(roundId, LabelManager.ROUND_ID, spec, Boolean.FALSE);
        return spec;
    }

    public Page<TransactionEntryBO> findBalanceMovementTransactions(
			List<String> domains,
			String createdOnRangeStart,
			String createdOnRangeEnd,
			String userGuid,
			String transactionId,
			String searchValue,
			String provider,
			String providerTransId,
			List<String> transactionTypeCode,
			String additionalTransId,
            String roundId,
			Pageable pageable
	) throws Status425DateParseException {

        Specification<TransactionEntry> spec  = getTransactionEntrySpecificationsForAccountHistory(domains, createdOnRangeStart, createdOnRangeEnd, userGuid, transactionId, searchValue, provider, providerTransId, transactionTypeCode, additionalTransId, PLAYER_BALANCE_TYPE_CODE_NAME, roundId);

        spec = addToSpecZeroAmountsFiltering(spec);

        spec = excludeTransactionTypes(spec);

        Page<TransactionEntryBO> pageCO = repository.findAll(spec, pageable).map(this::toTranEntryCO);

        enrichPageData(pageCO);

        return pageCO;
	}

    private Specification<TransactionEntry> addToSpecZeroAmountsFiltering(Specification<TransactionEntry> spec) {
        Specification<TransactionEntry> specification;

        specification = TransactionEntrySpecifications.notEqualForFieldName(TransactionEntry_.amountCents, 0L);

        if (spec == null) {
            spec = Specification.where(specification);
        } else {
            spec = spec.and(Specification.where(specification));
        }
        return spec;
    }

    private Specification<TransactionEntry> excludeTransactionTypes(Specification<TransactionEntry> spec) {
        Specification<TransactionEntry> specification = TransactionEntrySpecifications.excludeTransactionTypeCode(EXCLUDED_TRANSACTION_TYPES);

        if (spec == null) {
            spec = Specification.where(specification);
        } else {
            spec = spec.and(Specification.where(specification));
        }
        return spec;
    }

    private TransactionEntryBO toTranEntryCO(TransactionEntry entry) {
        TransactionEntryBO co = new TransactionEntryBO();
        mapper.map(entry, co);
        Integer divisor = entry.getAccount().getAccountType().getDividerToCents();
        co.setAmountCents((co.getAmountCents() / divisor) * -1);
        co.setPostEntryAccountBalanceCents(co.getPostEntryAccountBalanceCents() / divisor * -1);
        return co;
    }

    private void enrichPageData(Page<TransactionEntryBO> page) {
        Long lastTranId = null;
        List<LabelValue> labelValues = new ArrayList<>();
        for (TransactionEntryBO entry : page.getContent()) {
            boolean hasError = false;
            if (lastTranId == null || lastTranId.compareTo(entry.getTransaction().getId()) != 0) {
                lastTranId = entry.getTransaction().getId();
                try {
                    labelValues = transactionService.findLabelsForTransaction(entry.getTransaction().getId());
                } catch (Exception e) {
                    hasError = true;
                    log.warn("Problem getting labelValues for tran " + entry.getTransaction().getId() + " | " + e.getMessage());
                }
            }
            if (!hasError) {
                setTranEntryDetails(entry, labelValues);
            }
        }
    }

    private void setTranEntryDetails(TransactionEntryBO entry, List<LabelValue> labelValues) {
        TransactionEntryBODetails details = TransactionEntryBODetails.builder().build();
        for (LabelValue lv : labelValues) {
            switch (lv.getLabel().getName()) {
                case CasinoTransactionLabels.BONUS_REVISION_ID:
                    Long bonusRevisionId = Long.parseLong(lv.getValue());
                    details.setBonusRevisionId(bonusRevisionId);
                    // TODO: Set bonus name and bonus code. Info in svc-casino.
                    break;
                case CasinoTransactionLabels.TRAN_ID_LABEL:
                    details.setExternalTranId(lv.getValue());
                    break;
                case CasinoTransactionLabels.GAME_GUID_LABEL:
                    details.setGameGuid(lv.getValue());
                    break;
                case CasinoTransactionLabels.PLAYER_BONUS_HISTORY_ID:
                    details.setPlayerBonusHistoryId(Long.parseLong(lv.getValue()));
                    break;
                case CasinoTransactionLabels.PLAYER_REWARD_TYPE_HISTORY_ID:
                    details.setPlayerRewardTypeHistoryId(Long.parseLong(lv.getValue()));
                    break;
                case CashierTransactionLabels.PROCESSING_METHOD_LABEL:
                    details.setProcessingMethod(lv.getValue());
                    break;
                case CasinoTransactionLabels.PROVIDER_GUID_LABEL:
                    details.setProviderGuid(lv.getValue());
                    break;
                case CasinoTransactionLabels.ACCOUNTING_CLIENT_LABEL:
                    details.setAccountingClientTranId(lv.getValue());
                    break;
                case CasinoTransactionLabels.ACCOUNTING_CLIENT_RESPONSE_LABEL:
                    details.setAccountingClientExternalId(lv.getValue());
                    break;
                case LabelManager.ADDITIONAL_REFERENCE_ID:
                    details.setAdditionalTranId(lv.getValue());
                    break;
                case LabelManager.ROUND_ID:
                    details.setRoundId(lv.getValue());
                    break;
                case LabelManager.EXTERNAL_TIMESTAMP:
                    details.setExternalTimestamp(Long.valueOf(lv.getValue()));
                    break;
                default:
                    ;
            }
        }
        entry.setDetails(details);
    }

    private Specification<TransactionEntry> addToSpec(final String aString, Specification<TransactionEntry> spec, Function<String, Specification<TransactionEntry>> predicateMethod) {
        if (aString != null && !aString.isEmpty()) {
            Specification<TransactionEntry> localSpec = Specification.where(predicateMethod.apply(aString));
            spec = (spec == null) ? localSpec : spec.and(localSpec);
            return spec;
        }
        return spec;
    }

    private Specification<TransactionEntry> addToSpec(final String[] sArray, Specification<TransactionEntry> spec,
                                                       Function<String[], Specification<TransactionEntry>> predicateMethod) {
        if (sArray != null && sArray.length > 0) {
            Specification<TransactionEntry> localSpec = Specification.where(predicateMethod.apply(sArray));
            spec = (spec == null) ? localSpec : spec.and(localSpec);
            return spec;
        }
        return spec;
    }

    private Specification<TransactionEntry> addToSpec(final List<String> aStringList, Specification<TransactionEntry> spec, Function<List<String>, Specification<TransactionEntry>> predicateMethod) {
        if (aStringList != null && !aStringList.isEmpty()) {
            Specification<TransactionEntry> localSpec = Specification.where(predicateMethod.apply(aStringList));
            spec = (spec == null) ? localSpec : spec.and(localSpec);
            return spec;
        }
        return spec;
    }

    private Specification<TransactionEntry> addToSpec(final Date aDate, boolean addDay, Specification<TransactionEntry> spec, Function<Date, Specification<TransactionEntry>> predicateMethod) {
        if (aDate != null) {
            DateTime someDate = new DateTime(aDate);
            if (addDay) {
                someDate = someDate.plusDays(1).withTimeAtStartOfDay();
            } else {
                someDate = someDate.withTimeAtStartOfDay();
            }
            Specification<TransactionEntry> localSpec = Specification.where(predicateMethod.apply(someDate.toDate()));
            spec = (spec == null) ? localSpec : spec.and(localSpec);
            return spec;
        }
        return spec;
    }

    private boolean isNullOrEmpty(final String aString) {
        if (aString == null || aString.isEmpty()) return true;
        return false;
    }

    private Specification<TransactionEntry> addToSpec(final String labelValue, String labelName, Specification<TransactionEntry> spec, Boolean equal) {
        if (StringUtils.isNotBlank(labelValue) && StringUtils.isNotBlank(labelName)) {
            Specification<TransactionEntry> specification;
            if (equal) {
                specification = TransactionEntrySpecifications.equalWithLabelValue(labelName, labelValue);
            } else {
                specification = TransactionEntrySpecifications.likeWithLabelValue(labelName, labelValue);
            }

            if (spec == null) {
                spec = Specification.where(specification);
            } else {
                spec = spec.and(Specification.where(specification));
            }
        }
        return spec;
    }
}

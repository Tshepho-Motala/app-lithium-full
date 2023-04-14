package lithium.csv.cashier.transactions.provider.services;

import lithium.service.cashier.client.objects.TransactionFilterRequest;
import lithium.service.document.generation.client.objects.CommonCommandParams;
import lithium.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static java.util.Optional.ofNullable;

public class CashierTransactionRequestParams extends CommonCommandParams {
    public static final String PAYMENT_TYPE = "paymentType";
    public static final String DOMAIN = "domain";
    public static final String DM = "dm";
    public static final String DMP = "dmp";
    public static final String GUID = "guid";
    public static final String SEARCH = "search";
    public static final String TRANSACTION_TYPE = "transactionType";
    public static final String STATUSES = "statuses";
    public static final String CRESD = "cresd";
    public static final String CREED = "creed";
    public static final String UPDSD = "updsd";
    public static final String UPDED = "upded";
    public static final String PROCESSOR_REFERENCE = "processorReference";
    public static final String ADDITIONAL_REFERENCE = "additionalReference";
    public static final String DECLINE_REASON = "declineReason";
    public static final String LAST_FOUR_DIGITS = "lastFourDigits";

    public static final String ID = "id";
    public static final String STATUS = "status";
    public static final String TRANSACTION_RUNTIME_QUERY = "transactionRuntimeQuery";
    public static final String TEST_ACCOUNT = "testAccount";
    public static final String REGISTRATION_START = "registrationStart";
    public static final String REGISTRATION_END = "registrationEnd";
    public static final String INCLUDED_TRANSACTION_TAGS_NAMES = "includedTransactionTagsNames";
    public static final String EXCLUDED_TRANSACTION_TAGS_NAMES = "excludedTransactionTagsNames";
    public static final String DEPOSIT_COUNT = "depositCount";
    public static final String DAYS_SINCE_FIRST_DEPOSIT = "daysSinceFirstDeposit";
    public static final String TRANSACTION_AMOUNT = "transactionAmount";
    public static final String ACTIVE_PAYMENT_METOD_COUNT = "activePaymentMethodCount";
    public static final String USER_STATUS_IDS = "userStatuses";
    public static final String USER_TAG_IDS = "userTags";



    public CashierTransactionRequestParams(Map<String, String> paramsMap) {
        super(paramsMap);
    }

    public TransactionFilterRequest buildTransactionFilter() {

        TransactionFilterRequest filter = new TransactionFilterRequest();

        Map<String, String> params = this.getParamsMap();

        filter.setDomain(params.get(DOMAIN));
        if (StringUtil.isEmpty(params.get(DOMAIN))) {
            throw new NoSuchElementException("Required parameter 'domain' can`t be null");
        }

        filter.setDm(params.get(DM));
        filter.setDmp(params.get(DMP));
        filter.setGuid(params.get(GUID));

        filter.setSearchValue(params.get(SEARCH));

        filter.setTransactionType(params.get(TRANSACTION_TYPE));
        filter.setStatuses(ofNullable(params.get(STATUSES)).map(s -> Arrays.asList(s.split(","))).orElse(new ArrayList<>()));
        filter.setCresd(ofNullable(params.get(CRESD)).map(Long::valueOf).orElse(null));
        filter.setCreed(ofNullable(params.get(CREED)).map(Long::valueOf).orElse(null));
        filter.setUpdsd(ofNullable(params.get(UPDSD)).map(Long::valueOf).orElse(null));
        filter.setUpded(ofNullable(params.get(UPDED)).map(Long::valueOf).orElse(null));
        filter.setProcessorReference(params.get(PROCESSOR_REFERENCE));
        filter.setAdditionalReference(params.get(ADDITIONAL_REFERENCE));
        filter.setPaymentType(params.get(PAYMENT_TYPE));
        filter.setDeclineReason(params.get(DECLINE_REASON));
        filter.setLastFourDigits(params.get(LAST_FOUR_DIGITS));
        filter.setId(params.get(ID));
        filter.setStatus(params.get(STATUS));
        filter.setTransactionRuntimeQuery(params.get(TRANSACTION_RUNTIME_QUERY));
        filter.setTestAccount(ofNullable(params.get(TEST_ACCOUNT)).map(Boolean::valueOf).orElse(null));
        filter.setRegistrationStart(ofNullable(params.get(REGISTRATION_START)).map(Long::valueOf).orElse(null));
        filter.setRegistrationEnd(ofNullable(params.get(REGISTRATION_END)).map(Long::valueOf).orElse(null));

        filter.setIncludedTransactionTagsNames(parseStringList(params, INCLUDED_TRANSACTION_TAGS_NAMES));
        filter.setExcludedTransactionTagsNames(parseStringList(params, EXCLUDED_TRANSACTION_TAGS_NAMES));

        filter.setDepositCount(params.get(DEPOSIT_COUNT));
        filter.setDaysSinceFirstDeposit(params.get(DAYS_SINCE_FIRST_DEPOSIT));
        filter.setTransactionAmount(params.get(TRANSACTION_AMOUNT));
        filter.setActivePaymentMethodCount(params.get(ACTIVE_PAYMENT_METOD_COUNT));

        filter.setUserStatusIds(parseLongList(params, USER_STATUS_IDS));
        filter.setUserTagIds(parseLongList(params, USER_TAG_IDS));

        return filter;
    }

    private List<String> parseStringList(Map<String, String> params, String paramName) {
        return ofNullable(params.get(paramName))
                .map(s -> Arrays.asList(s.split(",")))
                .orElse(Collections.emptyList());
    }

    private List<Long> parseLongList(Map<String, String> params, String paramName) {
        return parseStringList(params, paramName).stream().map(Long::valueOf).toList();
    }
}

